import type { Page } from '@playwright/test'
import { newUser, signIn } from './support/data'
import {
  countCompletions,
  DEVICE_PAGE,
  deviceMe,
  deviceOwner,
  jwtClaims,
  NEVER_ISSUED_CODE,
  pollError,
  pollToken,
  refreshDeviceToken,
  startDeviceFlow,
  verifyUserCode,
} from './support/device'
import { expect, test } from './support/fixtures'
import { escapeRegExp, hydrated } from './support/ui'

/**
 * Pairing a Garmin or Karoo with an account — the only way the apps sign in. The device (played from
 * Node through support/device.ts) asks for a code and polls `/token`; the rider opens
 * `/garmin?code=…` or `/karoo?code=…` (the QR code the device shows), signs in if needed, and the
 * page authorizes the code by itself. Every test pairs a fresh account: `/token` opens a session on
 * it and records a login.
 */

const heading = (page: Page, name: string) =>
  page.getByRole('main').getByRole('heading', { name, exact: true })

/** The URL of the verification page, path and query, for `toHaveURL`. */
const verificationUrl = (path: string, userCode: string) =>
  new RegExp(`${escapeRegExp(`${path}?code=${userCode}`)}$`)

test('an anonymous rider scanning the Garmin code signs in, comes back to the same code, and the watch gets its tokens', async ({
  page,
  context,
}) => {
  const rider = await newUser('device garmin rider')
  const flow = await startDeviceFlow('garmin')
  expect(flow.userCode).toMatch(/^[A-HJ-NP-Z2-9]{6}$/)
  expect(flow.verificationUriComplete).toBe(`${flow.verificationUri}?code=${flow.userCode}`)

  // The device polls before anyone authorized it; the code is known, not authorized, and matched
  // whatever its case.
  expect(await pollError(flow.deviceCode)).toBe('AUTHORIZATION_PENDING')
  expect(await verifyUserCode(flow.userCode.toLowerCase())).toEqual({
    userCode: flow.userCode,
    authorized: false,
  })

  // Signed out, the page is protected: the login form, and the code must survive the detour.
  await page.goto(`/garmin?code=${flow.userCode}`)
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { name: /^Bienvenue sur / })).toBeVisible()
  await expect(heading(page, DEVICE_PAGE.success)).toHaveCount(0)
  expect(await pollError(flow.deviceCode), 'nothing authorized before the sign-in').toBe(
    'AUTHORIZATION_PENDING'
  )

  const completions = countCompletions(page)
  const submit = main.getByRole('button', { name: 'Se connecter', exact: true })
  await hydrated(submit)
  await main.getByRole('textbox', { name: 'Email' }).fill(rider.user.email)
  await main.getByRole('textbox', { name: 'Mot de passe' }).fill(rider.password)
  await submit.click()

  // Back on the Garmin page with the code intact, authorized without another click.
  await expect(page).toHaveURL(verificationUrl('/garmin', flow.userCode))
  await expect(heading(page, DEVICE_PAGE.success)).toBeVisible()
  await expect(
    main.getByText('Vous pouvez maintenant retourner sur votre appareil.', { exact: false })
  ).toBeVisible()
  expect(completions()).toBe(1)
  expect(await context.cookies()).toContainEqual(expect.objectContaining({ name: 'refresh_token' }))

  // The device's next poll gets the tokens, signed for the rider and for this client.
  const tokens = await pollToken(flow.deviceCode)
  expect(tokens).toMatchObject({ tokenType: 'Bearer', refreshToken: expect.any(String) })
  expect(tokens.expiresIn).toBeGreaterThan(0)
  expect(jwtClaims(tokens.accessToken)).toMatchObject({
    email: rider.user.email,
    userId: rider.user.id,
    client: 'garmin',
  })
  expect(await deviceMe(tokens.accessToken)).toEqual({ connectedGpsServices: [] })
  expect(await deviceOwner(tokens.accessToken)).toMatchObject({
    id: rider.user.id,
    email: rider.user.email,
  })

  // A device code is single-use, and its user code is gone with it.
  expect(await pollError(flow.deviceCode)).toBe('TOKEN_INVALID')
  expect(await verifyUserCode(flow.userCode)).toBeNull()

  // The refresh token keeps the device signed in as the same rider.
  const refreshed = await refreshDeviceToken(tokens.refreshToken!)
  expect(refreshed.refreshToken ?? null).toBeNull()
  expect(await deviceOwner(refreshed.accessToken)).toMatchObject({ id: rider.user.id })
})

test('a signed-in rider opening the Karoo link authorizes it with a single call, and a reload does not authorize it again', async ({
  page,
  context,
}) => {
  const rider = await newUser('device karoo rider')
  await signIn(context, rider)
  const flow = await startDeviceFlow('karoo')
  // What the Karoo's QR code holds: the page on this domain.
  const { pathname, search } = new URL(flow.verificationUriComplete)
  expect(pathname).toBe('/karoo')

  const completions = countCompletions(page)
  await page.goto(`${pathname}${search}`)
  await expect(heading(page, DEVICE_PAGE.success)).toBeVisible()
  await expect(page).toHaveURL(verificationUrl('/karoo', flow.userCode))
  expect(completions()).toBe(1)
  expect(await verifyUserCode(flow.userCode)).toEqual({
    userCode: flow.userCode,
    authorized: true,
  })

  // Opening the link again, before the device polled: already authorized, nothing re-sent.
  await page.reload()
  await expect(heading(page, DEVICE_PAGE.success)).toBeVisible()
  expect(completions()).toBe(1)

  const tokens = await pollToken(flow.deviceCode)
  expect(jwtClaims(tokens.accessToken)).toMatchObject({ userId: rider.user.id, client: 'karoo' })
  expect(await deviceOwner(tokens.accessToken)).toMatchObject({ id: rider.user.id })
})

test('an unknown code shows the error, « Réessayer » opens the manual entry, and a code typed in lowercase is authorized', async ({
  page,
  context,
}) => {
  const rider = await newUser('device manual rider')
  await signIn(context, rider)
  const flow = await startDeviceFlow('garmin')

  const completions = countCompletions(page)
  await page.goto(`/garmin?code=${NEVER_ISSUED_CODE}`)
  const main = page.getByRole('main')
  await expect(heading(page, DEVICE_PAGE.error)).toBeVisible()
  await expect(main.getByText('Ce code est invalide ou a expiré.', { exact: false })).toBeVisible()
  expect(completions(), 'an unknown code is never sent for authorization').toBe(0)

  const retry = main.getByRole('button', { name: 'Réessayer' })
  await hydrated(retry)
  await retry.click()
  await expect(heading(page, DEVICE_PAGE.manualEntry)).toBeVisible()
  await expect(page).toHaveURL(/\/garmin$/)

  // The six boxes of the PIN input: typing moves from one to the next.
  const boxes = main.getByRole('textbox')
  await expect(boxes).toHaveCount(6)
  await hydrated(boxes.first())
  await boxes.first().click()
  await page.keyboard.type(flow.userCode.toLowerCase())

  // Converted to uppercase: the URL carries the code as issued, and the pairing goes through.
  await expect(page).toHaveURL(verificationUrl('/garmin', flow.userCode))
  await expect(heading(page, DEVICE_PAGE.success)).toBeVisible()
  expect(completions()).toBe(1)

  const tokens = await pollToken(flow.deviceCode)
  expect(await deviceOwner(tokens.accessToken)).toMatchObject({ id: rider.user.id })
})
