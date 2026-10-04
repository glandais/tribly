import type { Page } from '@playwright/test'
import { ApiError, apiDelete } from './support/api'
import { newUser, signIn } from './support/data'
import {
  countCompletions,
  countDenials,
  DEVICE_PAGE,
  deviceMe,
  deviceOwner,
  ensureHammerheadOffered,
  jwtClaims,
  NEVER_ISSUED_CODE,
  pairDevice,
  pairedDevices,
  pollError,
  pollToken,
  refreshDeviceToken,
  refuseAtHammerhead,
  startDeviceFlow,
  verifyUserCode,
} from './support/device'
import { expect, test } from './support/fixtures'
import { escapeRegExp, hydrated } from './support/ui'

/**
 * Pairing a Garmin or Karoo with an account — the only way the apps sign in. The device (played from
 * Node through support/device.ts) asks for a code and polls `/token`; the rider opens
 * `/garmin?code=…` or `/karoo?code=…` (the QR code the device shows), signs in if needed, reads
 * the code and presses « Autoriser » — never less: opening a link that carries a code must not be
 * enough to pair a stranger's device (docs/LEDGER_*.md SEC-2, audit H3). Every test pairs a fresh
 * account: `/token` opens a session on it and records a login.
 *
 * A Karoo then needs Hammerhead, which carries the routes to it: once authorized, the Karoo page
 * goes straight on to that step, and the OAuth brings the browser back to it (docs/LEDGER_*.md
 * API-63). `localhost` offers Hammerhead for that ({@link ensureHammerheadOffered}); a Garmin never
 * sees the step.
 */

test.beforeAll(ensureHammerheadOffered)

const heading = (page: Page, name: string) =>
  page.getByRole('main').getByRole('heading', { name, exact: true })

/** Presses « Autoriser » on the confirmation card, once hydrated. */
async function authorize(page: Page) {
  const button = page.getByRole('main').getByRole('button', { name: 'Autoriser', exact: true })
  await hydrated(button)
  await button.click()
}

/** The URL of the verification page, path and query, for `toHaveURL`. */
const verificationUrl = (path: string, userCode: string) =>
  new RegExp(`${escapeRegExp(`${path}?code=${userCode}`)}$`)

test('an anonymous rider scanning the Garmin code signs in, comes back to the same code, and the device gets its tokens', async ({
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
  expect(await verifyUserCode(flow.userCode.toLowerCase())).toMatchObject({
    userCode: flow.userCode,
    authorized: false,
    clientId: 'garmin',
    requestedAt: expect.any(String),
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
  await main.getByRole('textbox', { name: 'Adresse e-mail' }).fill(rider.user.email)
  await main.getByRole('textbox', { name: 'Mot de passe' }).fill(rider.password)
  await submit.click()

  // Back on the Garmin page with the code intact — and asked, not authorized: signing in is not a
  // consent to pairing.
  await expect(page).toHaveURL(verificationUrl('/garmin', flow.userCode))
  await expect(heading(page, DEVICE_PAGE.confirm)).toBeVisible()
  await expect(main.getByText('un appareil Garmin', { exact: false })).toBeVisible()
  await expect(main.getByText(flow.userCode, { exact: true })).toBeVisible()
  expect(completions()).toBe(0)
  expect(await pollError(flow.deviceCode)).toBe('AUTHORIZATION_PENDING')

  await authorize(page)
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

  // The refresh token keeps the device signed in as the same rider, and rotates
  // (docs/LEDGER_*.md SEC-11): the answer carries the one to keep.
  const refreshed = await refreshDeviceToken(tokens.refreshToken!)
  expect(refreshed.refreshToken).toBeTruthy()
  expect(refreshed.refreshToken).not.toBe(tokens.refreshToken)
  expect(await deviceOwner(refreshed.accessToken)).toMatchObject({ id: rider.user.id })
})

test('a signed-in rider opening the Karoo link is asked first, authorizes it with one click, goes on to Hammerhead, and a reload does not authorize it again', async ({
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
  // The link alone authorizes nothing: the card names the device, the account and the code.
  await expect(heading(page, DEVICE_PAGE.confirm)).toBeVisible()
  await expect(
    page.getByRole('main').getByText(`un Karoo à accéder au compte ${rider.user.displayName}`, {
      exact: false,
    })
  ).toBeVisible()
  expect(completions()).toBe(0)
  expect(await pollError(flow.deviceCode)).toBe('AUTHORIZATION_PENDING')

  await authorize(page)
  // Straight on to Hammerhead: the account has none, and without it the Karoo gets no routes.
  await expect(heading(page, DEVICE_PAGE.hammerhead)).toBeVisible()
  await expect(
    page.getByRole('main').getByText('Karoo autorisé sur votre compte.', { exact: true })
  ).toBeVisible()
  await expect(
    page.getByRole('main').getByRole('button', { name: 'Associer Hammerhead', exact: true })
  ).toBeVisible()
  await expect(heading(page, DEVICE_PAGE.success)).toHaveCount(0)
  await expect(page).toHaveURL(verificationUrl('/karoo', flow.userCode))
  expect(completions()).toBe(1)
  expect(await verifyUserCode(flow.userCode)).toMatchObject({
    userCode: flow.userCode,
    authorized: true,
  })

  // Opening the link again, before the device polled: already authorized, nothing re-sent, and
  // still at the Hammerhead step.
  await page.reload()
  await expect(heading(page, DEVICE_PAGE.hammerhead)).toBeVisible()
  expect(completions()).toBe(1)

  // The Karoo gets its tokens all the same, and reads that Hammerhead is missing: it stays on its
  // own Hammerhead screen, following /api/device/me.
  const tokens = await pollToken(flow.deviceCode)
  expect(jwtClaims(tokens.accessToken)).toMatchObject({ userId: rider.user.id, client: 'karoo' })
  expect(await deviceOwner(tokens.accessToken)).toMatchObject({ id: rider.user.id })
  expect(await deviceMe(tokens.accessToken)).toEqual({ connectedGpsServices: [] })
})

test('« Associer Hammerhead » leaves for Hammerhead with the Karoo page as the way back, and a refusal there lands back on that step with the reason', async ({
  page,
  context,
}) => {
  const rider = await newUser('device hammerhead rider')
  await signIn(context, rider)
  const flow = await startDeviceFlow('karoo')
  const visits = await refuseAtHammerhead(page)

  await page.goto(`/karoo?code=${flow.userCode}`)
  await authorize(page)
  const main = page.getByRole('main')
  const connect = main.getByRole('button', { name: 'Associer Hammerhead', exact: true })
  await hydrated(connect)
  await connect.click()

  // Back from Hammerhead on the Karoo page — not the profile — with the refusal said, and the step
  // offered again.
  await expect(page).toHaveURL(/\/karoo\?gps_error=access_denied$/)
  await expect(heading(page, DEVICE_PAGE.hammerhead)).toBeVisible()
  await expect(
    main.getByText("Vous avez refusé l'accès chez Hammerhead.", { exact: false })
  ).toBeVisible()
  await expect(connect).toBeVisible()

  // One trip to Hammerhead, its callback on this site, and a state the callback consumed.
  expect(visits).toHaveLength(1)
  const callback = new URL(visits[0].searchParams.get('redirect_uri')!)
  expect(callback.pathname).toBe('/api/gps/callback/hammerhead')
  expect(visits[0].searchParams.get('state')).toBeTruthy()
})

test("the Karoo's fallback QR opens the Hammerhead step alone, and a Garmin never sees it", async ({
  page,
  context,
}) => {
  const rider = await newUser('device fallback rider')
  await signIn(context, rider)

  // What the Karoo's second QR holds: the step, without a code to authorize.
  await page.goto('/karoo/hammerhead')
  await expect(heading(page, DEVICE_PAGE.hammerhead)).toBeVisible()
  await expect(heading(page, DEVICE_PAGE.confirm)).toHaveCount(0)

  // A Garmin pairs and stops there: Hammerhead is the Karoo's.
  const flow = await startDeviceFlow('garmin')
  await page.goto(`/garmin?code=${flow.userCode}`)
  await authorize(page)
  await expect(heading(page, DEVICE_PAGE.success)).toBeVisible()
  await expect(heading(page, DEVICE_PAGE.hammerhead)).toHaveCount(0)
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

  // Converted to uppercase: the URL carries the code as issued, and the pairing goes through once
  // confirmed.
  await expect(page).toHaveURL(verificationUrl('/garmin', flow.userCode))
  await expect(heading(page, DEVICE_PAGE.confirm)).toBeVisible()
  await authorize(page)
  await expect(heading(page, DEVICE_PAGE.success)).toBeVisible()
  expect(completions()).toBe(1)

  const tokens = await pollToken(flow.deviceCode)
  expect(await deviceOwner(tokens.accessToken)).toMatchObject({ id: rider.user.id })
})

test('a rider who did not ask for the code refuses it: the device is told the code expired', async ({
  page,
  context,
}) => {
  const rider = await newUser('device denying rider')
  await signIn(context, rider)
  const flow = await startDeviceFlow('karoo')

  const completions = countCompletions(page)
  const denials = countDenials(page)
  await page.goto(`/karoo?code=${flow.userCode}`)
  await expect(heading(page, DEVICE_PAGE.confirm)).toBeVisible()

  const deny = page.getByRole('main').getByRole('button', { name: 'Refuser', exact: true })
  await hydrated(deny)
  await deny.click()
  await expect(heading(page, DEVICE_PAGE.denied)).toBeVisible()
  expect(denials()).toBe(1)
  expect(completions()).toBe(0)

  // Dead for everyone: the page cannot find it any more, and the device hears the answer Karoo and
  // Garmin already handle by starting over.
  expect(await verifyUserCode(flow.userCode)).toBeNull()
  expect(await pollError(flow.deviceCode)).toBe('TOKEN_EXPIRED')
})

/**
 * The verification page a device's QR code opens is the one of its own app (bug #7): a Garmin
 * device used to hand out `/karoo`, the Hammerhead page, which means nothing to a Garmin user
 * (DeviceAuthService.verificationPath — `/garmin` for the Garmin client, `/karoo` otherwise).
 */
test('each device is sent to the verification page of its own app', async () => {
  for (const [clientId, page] of [
    ['garmin', '/garmin'],
    ['karoo', '/karoo'],
  ] as const) {
    const flow = await startDeviceFlow(clientId)
    const uri = new URL(flow.verificationUri)
    expect(uri.pathname, `${clientId}: verificationUri`).toBe(page)
    expect(uri.search, `${clientId}: verificationUri carries no code`).toBe('')
    expect(flow.verificationUriComplete, `${clientId}: verificationUriComplete`).toBe(
      `${flow.verificationUri}?code=${flow.userCode}`
    )
  }
})

/**
 * « Appareils et services » lists each paired device and unpairs one at a time (docs/LEDGER_*.md API-64): until
 * then only « Déconnecter tous les appareils » could, closing the browser and the app with it.
 * Unpairing revokes the session that device's pairing opened — its refresh fails — and nothing
 * else: the other device and this browser stay signed in.
 */
test('the profile lists each paired device, and unpairing one ends its session only', async ({
  page,
  context,
}) => {
  const rider = await newUser('device list rider')
  await signIn(context, rider)
  const karoo = await pairDevice(rider, 'karoo')
  const garmin = await pairDevice(rider, 'garmin')

  // The two devices, newest first — not the browser's session nor the rider's own sign-in.
  const listed = await pairedDevices(rider)
  expect(listed.map((d) => d.type)).toEqual(['GARMIN', 'KAROO'])
  expect(listed.every((d) => d.pairedAt)).toBe(true)

  await page.goto('/profil/appareils')
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { name: 'Appareils appairés', exact: true })).toBeVisible()
  await expect(main.getByText('Karoo', { exact: true })).toBeVisible()
  await expect(main.getByText('Garmin', { exact: true })).toBeVisible()

  const unpair = main.getByRole('button', { name: 'Délier Garmin', exact: true })
  await hydrated(unpair)
  await unpair.click()
  const dialog = page.getByRole('dialog')
  await expect(dialog.getByText("Délier l'appareil", { exact: true })).toBeVisible()
  await dialog.getByRole('button', { name: 'Délier', exact: true }).click()

  await expect(main.getByText('Garmin', { exact: true })).toHaveCount(0)
  await expect(main.getByText('Karoo', { exact: true })).toBeVisible()
  expect((await pairedDevices(rider)).map((d) => d.type)).toEqual(['KAROO'])

  // The Garmin can no longer renew its access; the Karoo still does, and this page stays signed in.
  await expect(refreshDeviceToken(garmin.refreshToken!)).rejects.toMatchObject({
    code: 'TOKEN_INVALID',
  })
  expect((await refreshDeviceToken(karoo.refreshToken!)).accessToken).toBeTruthy()
  await page.reload()
  await expect(main.getByText('Karoo', { exact: true })).toBeVisible()
})

test("a device of someone else cannot be unpaired, nor a session that is not a device's", async () => {
  const rider = await newUser('device owner rider')
  const stranger = await newUser('device stranger rider')
  await pairDevice(rider, 'karoo')
  const [karoo] = await pairedDevices(rider)

  // Someone else's pairing answers 404, like an unknown one: nothing tells it exists.
  const strangerUnpair = apiDelete(stranger, `/api/users/me/devices/${karoo.id}`)
  await expect(strangerUnpair).rejects.toBeInstanceOf(ApiError)
  await expect(strangerUnpair).rejects.toMatchObject({ status: 404 })
  expect(await pairedDevices(rider)).toHaveLength(1)
  expect(await pairedDevices(stranger)).toEqual([])
})
