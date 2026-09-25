import type { Page } from '@playwright/test'
import { ApiError, loginWithPassword } from './support/api'
import {
  addMember,
  freshAddress,
  getTeam,
  newTeam,
  newUser,
  roleSession,
  signIn,
} from './support/data'
import { expect, test, unique } from './support/fixtures'
import {
  calendarTokenOf,
  fetchFeed,
  headerControls,
  icsDateTime,
  mailLinkTo,
  meFromSession,
  parseIcs,
  passkeysOf,
  sessionCookie,
  sessionIsAlive,
  signOutFromHeader,
  virtualAuthenticator,
} from './support/flow-account'
import { rosterOf } from './support/flow-team'
import { mailbox, waitForNewMail } from './support/mailhog'
import { findRide, joinGroup, newRide, openRide } from './support/rides'
import { hydrated, pageAs } from './support/ui'

/**
 * The account journeys, through the UI only: sign-up with the form and the mail's link, sign-out,
 * password sign-in, forgotten password, the profile and its immediate preferences, passkeys, the
 * personal calendar feed, and deleting the account.
 *
 * Every test signs up its own account: nothing here touches the seeded ones.
 */

const WELCOME = /^Bienvenue sur /

async function openLogin(page: Page) {
  await page.goto('/connexion')
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { name: WELCOME })).toBeVisible()
  return main
}

/** Fills and submits the login form (the page is already on /connexion). */
async function signInWithPassword(page: Page, email: string, password: string) {
  const main = page.getByRole('main')
  const submit = main.getByRole('button', { name: 'Se connecter', exact: true })
  await hydrated(submit)
  await main.getByRole('textbox', { name: 'Email' }).fill(email)
  await main.getByRole('textbox', { name: 'Mot de passe' }).fill(password)
  await submit.click()
}

/** The profile page, signed in: its heading and the account's address. */
async function openProfile(page: Page, email: string) {
  await page.goto('/profil')
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { name: 'Paramètres du profil' })).toBeVisible()
  await expect(main.getByText(email, { exact: true }).first()).toBeVisible()
  return main
}

/** A protected page sends an anonymous visitor to the login form. */
async function expectSignedOut(page: Page) {
  await page.goto('/profil')
  await expect(page.getByRole('main').getByRole('heading', { name: WELCOME })).toBeVisible()
  await expect(
    page.getByRole('main').getByRole('heading', { name: 'Paramètres du profil' })
  ).toHaveCount(0)
}

test('sign up with the form, verify through the mail, sign out from the header, sign in with the password', async ({
  page,
  context,
  isMobile,
}) => {
  const email = freshAddress('account signup')
  const displayName = unique('Inscrite')
  const password = 'e2e-signup-password'

  const main = await openLogin(page)
  const toRegister = main.getByRole('button', { name: 'Créer un compte' })
  await hydrated(toRegister)
  await toRegister.click()
  await expect(main.getByRole('heading', { name: 'Créer un compte' })).toBeVisible()

  await main.getByRole('textbox', { name: 'Email' }).fill(email)
  await main.getByRole('textbox', { name: "Nom d'affichage" }).fill(displayName)
  await main.getByRole('textbox', { name: 'Mot de passe', exact: true }).fill(password)
  await main.getByRole('textbox', { name: 'Confirmer le mot de passe' }).fill(password)
  await main.getByRole('checkbox').check()
  const seen = await mailbox(email)
  await main.getByRole('button', { name: 'Créer un compte' }).click()

  // Back on the login form, the address kept, told to open the mail.
  await expect(
    page.getByText("Cliquez sur le lien dans l'email pour activer votre compte.")
  ).toBeVisible()
  await expect(main.getByRole('heading', { name: WELCOME })).toBeVisible()
  await expect(main.getByRole('textbox', { name: 'Email' })).toHaveValue(email)
  // Not usable before the address is verified.
  await expect(loginWithPassword(email, password)).rejects.toBeInstanceOf(ApiError)

  // The verification link signs the new account in; the passkey offer can wait.
  await page.goto(mailLinkTo(await waitForNewMail(email, seen), '/verify-email'))
  await expect(main.getByRole('heading', { name: 'Sécurisez votre compte' })).toBeVisible()
  const later = main.getByRole('button', { name: 'Plus tard' })
  await later.click()
  await expect(page).toHaveURL(/\/$/)

  const cookie = await sessionCookie(context)
  expect(cookie, 'the verification signed the browser in').toBeTruthy()
  const me = await meFromSession(cookie!)
  expect(me).toMatchObject({ email, displayName, emailVerified: true })
  const profile = await openProfile(page, email)
  await expect(profile.getByText(displayName, { exact: true }).first()).toBeVisible()

  await signOutFromHeader(page, isMobile, displayName)
  await expect(page.getByRole('main').getByRole('heading', { name: WELCOME })).toBeVisible()
  expect(await sessionIsAlive(cookie!), 'the backend revoked the session').toBe(false)
  expect(await sessionCookie(context)).toBeUndefined()
  await expectSignedOut(page)

  // Signing in again with the password chosen at sign-up, from the login form the protected page
  // sent us to: it returns there.
  await signInWithPassword(page, email, password)
  await expect(page).toHaveURL(/\/profil$/)
  await expect(
    page.getByRole('main').getByRole('heading', { name: 'Paramètres du profil' })
  ).toBeVisible()
  expect((await meFromSession((await sessionCookie(context))!)).email).toBe(email)
})

test('a wrong password is refused and signs nobody in', async ({ page, context }) => {
  const user = await newUser(unique('Mauvais mot de passe'))
  await openLogin(page)
  await signInWithPassword(page, user.user.email, 'not-the-password')
  await expect(page.getByText('Email ou mot de passe incorrect')).toBeVisible()
  await expect(page).toHaveURL(/\/connexion$/)
  expect(await sessionCookie(context)).toBeUndefined()
})

test('forgotten password: the mail, a new password, then sign in with it', async ({
  page,
  context,
}) => {
  const user = await newUser(unique('Oublieuse'))
  const email = user.user.email
  const newPassword = 'e2e-new-password'

  const login = await openLogin(page)
  const forgot = login.getByRole('link', { name: 'Mot de passe oublié ?' })
  await hydrated(forgot)
  await forgot.click()
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { name: 'Mot de passe oublié' })).toBeVisible()
  await main.getByRole('textbox', { name: 'Email' }).fill(email)
  const seen = await mailbox(email)
  await main.getByRole('button', { name: 'Envoyer le lien' }).click()
  await expect(main.getByRole('heading', { name: 'Email envoyé' })).toBeVisible()

  await page.goto(mailLinkTo(await waitForNewMail(email, seen), '/reset-password'))
  await expect(main.getByRole('heading', { name: 'Réinitialiser le mot de passe' })).toBeVisible()
  const submit = main.getByRole('button', { name: 'Réinitialiser le mot de passe' })
  await hydrated(submit)
  await main.getByRole('textbox', { name: 'Nouveau mot de passe' }).fill(newPassword)
  await main.getByRole('textbox', { name: 'Confirmer le mot de passe' }).fill(newPassword)
  await submit.click()

  // The reset signs the browser in.
  await expect(page).toHaveURL(/\/$/)
  const profile = await openProfile(page, email)
  // The old password is gone, the new one works.
  await expect(loginWithPassword(email, user.password)).rejects.toBeInstanceOf(ApiError)
  expect((await loginWithPassword(email, newPassword)).user.email).toBe(email)

  // Sign out from the profile page's own button, then back in with the new password.
  const signOut = profile.getByRole('button', { name: 'Se déconnecter' })
  await hydrated(signOut)
  await signOut.click()
  await expect(page.getByRole('main').getByRole('heading', { name: WELCOME })).toBeVisible()
  expect(await sessionCookie(context)).toBeUndefined()
  await signInWithPassword(page, email, newPassword)
  await expect(page).toHaveURL(/\/$/)
  await openProfile(page, email)
})

test('the display name is edited from the profile', async ({ page, context, isMobile }) => {
  const user = await newUser(unique('Ancien nom'))
  const renamed = unique('Nouveau nom')
  await signIn(context, user)
  const main = await openProfile(page, user.user.email)

  const edit = main.getByRole('button', { name: 'Modifier le profil' })
  await hydrated(edit)
  await edit.click()
  await main.getByRole('textbox', { name: "Nom d'affichage" }).fill(renamed)
  const saved = page.waitForResponse(
    (r) => r.request().method() === 'PUT' && r.url().endsWith('/api/users/me')
  )
  await main.getByRole('button', { name: 'Enregistrer' }).click()
  expect((await saved).ok()).toBe(true)

  await expect(main.getByRole('textbox', { name: "Nom d'affichage" })).toHaveCount(0)
  await expect(main.getByText(renamed, { exact: true }).first()).toBeVisible()
  if (!isMobile)
    await expect(page.getByRole('banner').getByRole('button', { name: renamed })).toBeVisible()
  expect((await meFromSession(user.refreshToken)).displayName).toBe(renamed)

  await page.reload()
  await expect(main.getByText(renamed, { exact: true }).first()).toBeVisible()
  await expect(main.getByText(user.user.displayName, { exact: true })).toHaveCount(0)
})

test('units, theme, language and contact preferences apply at once and persist', async ({
  page,
  context,
  isMobile,
}) => {
  const user = await newUser(unique('Préférences'))
  await signIn(context, user)
  const main = await openProfile(page, user.user.email)
  const html = page.locator('html')
  await expect(html).toHaveAttribute('data-mantine-color-scheme', 'light')

  const before = await meFromSession(user.refreshToken)
  expect(before.unitSystem ?? 'METRIC').toBe('METRIC')
  expect(before.contactableByMembers).toBe(true)

  // Units: the segmented control saves the profile at once.
  const imperial = main.getByRole('radio', { name: 'Impérial (mi, ft)' })
  await hydrated(imperial)
  const unitsSaved = page.waitForResponse(
    (r) => r.request().method() === 'PUT' && r.url().endsWith('/api/users/me')
  )
  await main.getByText('Impérial (mi, ft)', { exact: true }).click()
  expect((await unitsSaved).ok()).toBe(true)
  await expect(imperial).toBeChecked()

  // Contactable: a partial PATCH of the preferences.
  const contactable = main.getByRole('switch', {
    name: 'Recevoir les messages des membres au sujet de mes annonces',
  })
  await expect(contactable).toBeChecked()
  const contactSaved = page.waitForResponse(
    (r) => r.request().method() === 'PATCH' && r.url().endsWith('/api/users/me/preferences')
  )
  await contactable.click()
  expect((await contactSaved).ok()).toBe(true)
  await expect(contactable).not.toBeChecked()
  await expect(contactable).toBeEnabled()

  // Theme and language, from the header.
  const controls = await headerControls(page, isMobile)
  const themeSaved = page.waitForResponse(
    (r) => r.request().method() === 'PATCH' && r.url().endsWith('/api/users/me/preferences')
  )
  await controls.getByRole('button', { name: 'Changer le thème' }).click()
  expect((await themeSaved).ok()).toBe(true)
  await expect(html).toHaveAttribute('data-mantine-color-scheme', 'dark')

  const languageSaved = page.waitForResponse(
    (r) => r.request().method() === 'PATCH' && r.url().endsWith('/api/users/me/preferences')
  )
  await controls.getByRole('combobox', { name: 'Langue' }).selectOption('en')
  expect((await languageSaved).ok()).toBe(true)
  await expect(main.getByRole('heading', { name: 'Profile Settings' })).toBeVisible()

  expect(await meFromSession(user.refreshToken)).toMatchObject({
    unitSystem: 'IMPERIAL',
    theme: 'DARK',
    language: 'en',
    contactableByMembers: false,
  })

  // A reload renders every one of them from the account.
  await page.reload()
  await expect(main.getByRole('heading', { name: 'Profile Settings' })).toBeVisible()
  await expect(html).toHaveAttribute('data-mantine-color-scheme', 'dark')
  await expect(main.getByRole('radio', { name: 'Imperial (mi, ft)' })).toBeChecked()
  await expect(
    main.getByRole('switch', { name: 'Receive messages from members about my ads' })
  ).not.toBeChecked()
})

test('a passkey registered from the profile signs in from the login page', async ({
  page,
  context,
  isMobile,
}) => {
  const user = await newUser(unique('Passkey'))
  const authenticator = await virtualAuthenticator(page)
  await signIn(context, user)
  const main = await openProfile(page, user.user.email)
  const device = unique('Clé virtuelle')

  await expect(main.getByText('Aucune passkey enregistrée.', { exact: false })).toBeVisible()
  const add = main.getByRole('button', { name: 'Ajouter', exact: true })
  await hydrated(add)
  await add.click()
  const dialog = page.getByRole('dialog', { name: 'Ajouter une passkey' })
  await dialog.getByRole('textbox', { name: "Nom de l'appareil (optionnel)" }).fill(device)
  const registered = page.waitForResponse(
    (r) => r.request().method() === 'POST' && r.url().includes('/api/auth/passkeys/register')
  )
  await dialog.getByRole('button', { name: 'Enregistrer' }).click()
  expect((await registered).ok()).toBe(true)
  await expect(dialog).toBeHidden()
  await expect(main.getByText(device, { exact: true })).toBeVisible()

  expect(await authenticator.credentials()).toHaveLength(1)
  const [passkey] = await passkeysOf(user.accessToken)
  expect(passkey).toMatchObject({ deviceName: device })
  expect(passkey.lastUsedAt).toBeFalsy()

  await signOutFromHeader(page, isMobile, user.user.displayName)
  const login = page.getByRole('main')
  await expect(login.getByRole('heading', { name: WELCOME })).toBeVisible()
  expect(await sessionCookie(context)).toBeUndefined()

  // No address typed: the authenticator offers the account's resident credential.
  const quick = login.getByRole('button', { name: 'Connexion rapide' })
  await hydrated(quick)
  await quick.click()
  await expect(page).toHaveURL(/\/$/)
  await openProfile(page, user.user.email)
  const cookie = await sessionCookie(context)
  expect((await meFromSession(cookie!)).id).toBe(user.user.id)
  const [used] = await passkeysOf(user.accessToken)
  expect(used.lastUsedAt).toBeTruthy()
})

const FEED_URL = /\/api\/calendar\/ics\?token=[0-9a-f]+$/

/** « Régénérer le lien », confirmed, on /calendrier; returns once the backend answered. */
async function regenerateFeed(page: Page) {
  const regenerate = page.getByRole('main').getByRole('button', { name: 'Régénérer le lien' })
  await hydrated(regenerate)
  await regenerate.click()
  const dialog = page.getByRole('dialog', { name: 'Régénérer le lien du calendrier' })
  await expect(
    dialog.getByText("L'ancien lien ne fonctionnera plus.", { exact: false })
  ).toBeVisible()
  const regenerated = page.waitForResponse(
    (r) => r.request().method() === 'POST' && r.url().endsWith('/api/calendar/token/regenerate')
  )
  await dialog.getByRole('button', { name: 'Confirmer' }).click()
  expect((await regenerated).ok()).toBe(true)
  await expect(dialog).toBeHidden()
}

test.describe('the personal calendar feed', () => {
  test('the feed URL is shown and copied, serves the upcoming ride as iCalendar, and a new link retires the old one', async ({
    context,
    page,
  }) => {
    const user = await newUser(unique('Abonnée ICS'))
    const team = await newTeam(user, unique('Équipe ICS'))
    const ride = await newRide(user, team.slug, unique('Sortie ICS'))
    await joinGroup(user, team.slug, ride, 'Groupe A')
    // A team the user is not in: its ride has no business in their feed.
    const stranger = await newUser(unique('Étrangère ICS'))
    const otherTeam = await newTeam(stranger, unique('Autre équipe ICS'))
    const otherRide = await newRide(stranger, otherTeam.slug, unique('Sortie étrangère'))
    await context.grantPermissions(['clipboard-read', 'clipboard-write'])
    await signIn(context, user)

    // The feed settings live under the personal calendar, /calendrier.
    await page.goto('/calendrier')
    const main = page.getByRole('main')
    await expect(main.getByRole('heading', { name: 'Calendrier', level: 2 })).toBeVisible()
    const field = main.getByRole('textbox', { name: 'URL du flux global' })
    await expect(field).toHaveValue(FEED_URL)
    const url = await field.inputValue()
    expect(url, 'the URL shown is the account’s').toBe((await calendarTokenOf(user)).globalFeedUrl)
    await expect(main.getByRole('link', { name: "S'abonner" })).toHaveAttribute(
      'href',
      url.replace(/^https?:\/\//, 'webcal://')
    )

    const copy = main.getByRole('button', { name: 'Copier le lien' })
    await hydrated(copy)
    await copy.click()
    await expect.poll(() => page.evaluate(() => navigator.clipboard.readText())).toBe(url)

    // A calendar application fetches it with nothing but the URL.
    const feed = await fetchFeed(url)
    expect(feed.status).toBe(200)
    expect(feed.contentType).toMatch(/^text\/calendar\b/)
    const calendar = parseIcs(feed.body)
    const event = calendar.events.find((e) => e.SUMMARY === ride.name)
    expect(event, `the feed lists « ${ride.name} »`).toBeDefined()
    expect(event).toMatchObject({
      DTSTART: icsDateTime(ride.dateTime),
      DESCRIPTION: team.name,
      CATEGORIES: 'RIDE',
    })
    expect(calendar.events.map((e) => e.SUMMARY)).not.toContain(otherRide.name)

    // The event's link opens the ride.
    await openRide(page, team.slug, ride)
    await page.goto(event!.URL)
    await expect(page.getByRole('heading', { level: 2, name: ride.name })).toBeVisible()

    // A new link: the old one stops working at once.
    await page.goto('/calendrier')
    await expect(field).toHaveValue(url)
    await regenerateFeed(page)
    expect((await fetchFeed(url)).status, 'the old URL is refused').toBe(403)

    // The page shows the new link (once reloaded: see the defect below), and it serves the feed.
    await page.reload()
    await expect(field).not.toHaveValue(url)
    await expect(field).toHaveValue(FEED_URL)
    const renewed = await field.inputValue()
    expect(renewed).toBe((await calendarTokenOf(user)).globalFeedUrl)
    const again = await fetchFeed(renewed)
    expect(again.status).toBe(200)
    expect(parseIcs(again.body).events.map((e) => e.SUMMARY)).toContain(ride.name)
  })

  test('the new link replaces the old one on screen as soon as it is regenerated', async ({
    context,
    page,
  }) => {
    test.fail(
      true,
      'IcsFeedSettings.tsx:48-58: useRegenerateToken() neither sets nor invalidates the token query, so the field and the copy button keep the dead URL until a reload'
    )
    const user = await newUser(unique('Régénère ICS'))
    await signIn(context, user)
    await page.goto('/calendrier')
    const field = page.getByRole('main').getByRole('textbox', { name: 'URL du flux global' })
    await expect(field).toHaveValue(FEED_URL)
    const url = await field.inputValue()
    await regenerateFeed(page)
    const renewed = (await calendarTokenOf(user)).globalFeedUrl
    expect(renewed, 'precondition: the backend issued a new link').not.toBe(url)
    expect((await fetchFeed(url)).status, 'precondition: the old one is dead').toBe(403)

    // The defect.
    await expect(field).toHaveValue(renewed, { timeout: 2_000 })
  })
})

test.describe('deleting the account', () => {
  test('confirmed from the profile’s danger zone: signed out for good, sign-in refused, and the team it ran stays with its members', async ({
    browser,
    context,
    page,
  }) => {
    const user = await newUser(unique('Partante'))
    const member = await newUser(unique('Restante'))
    const team = await newTeam(user, unique('Équipe orpheline'))
    await addMember(await roleSession('admin'), team.slug, member)
    const ride = await newRide(user, team.slug, unique('Sortie de la partante'))
    await signIn(context, user)
    const main = await openProfile(page, user.user.email)
    const cookie = await sessionCookie(context)
    expect(cookie, 'precondition: signed in').toBeTruthy()

    await expect(main.getByText('Zone de danger', { exact: true })).toBeVisible()
    const remove = main.getByRole('button', { name: 'Supprimer le compte' })
    await hydrated(remove)

    // Cancelling keeps everything.
    await remove.click()
    const dialog = page.getByRole('dialog', { name: 'Zone de danger' })
    await expect(dialog.getByText('Êtes-vous sûr ? Cette action est irréversible.')).toBeVisible()
    await dialog.getByRole('button', { name: 'Annuler' }).click()
    await expect(dialog).toBeHidden()
    expect(await sessionIsAlive(cookie!), 'cancelled: the session is untouched').toBe(true)

    await remove.click()
    const deleted = page.waitForResponse(
      (r) => r.request().method() === 'DELETE' && r.url().endsWith('/api/users/me')
    )
    await dialog.getByRole('button', { name: 'Oui, supprimer mon compte' }).click()
    expect((await deleted).status()).toBe(204)

    // Signed out, on the login form.
    await expect(page).toHaveURL(/\/connexion$/)
    await expect(page.getByRole('main').getByRole('heading', { name: WELCOME })).toBeVisible()
    expect(await sessionIsAlive(cookie!), 'the session no longer refreshes').toBe(false)
    await expectSignedOut(page)

    // The address and password open nothing any more — neither through the form nor the API.
    await signInWithPassword(page, user.user.email, user.password)
    await expect(page.getByText('Email ou mot de passe incorrect')).toBeVisible()
    await expect(page).toHaveURL(/\/connexion/)
    const refused = await loginWithPassword(user.user.email, user.password).catch(
      (error: unknown) => error
    )
    expect(refused).toBeInstanceOf(ApiError)
    expect((refused as ApiError).code).toBe('INVALID_CREDENTIALS')

    // AccountErasureService drops the memberships and leaves the team's content to the team: the
    // team stays, with its other members and the ride it published.
    const kept = await getTeam(member, team.slug)
    expect(kept).toMatchObject({ name: team.name, role: 'MEMBER', memberCount: 1 })
    const roster = await rosterOf(await roleSession('admin'), team.slug)
    expect(roster.members.map((m) => m.user.id)).toEqual([member.user.id])
    expect(await findRide(member, team.slug, ride.slug), 'the ride stays').not.toBeNull()

    // And the member still reads it.
    const { context: memberContext, page: memberPage } = await pageAs(browser, member)
    try {
      await openRide(memberPage, team.slug, ride)
    } finally {
      await memberContext.close()
    }
  })
})

test.describe('mobile header', () => {
  test.skip(({ isMobile }) => !isMobile, 'the burger only exists on the mobile layout')

  test('the menu button has an accessible name', async ({ page }) => {
    // Layout.tsx renders <Burger> without aria-label, while nav.openMenu / nav.closeMenu exist in
    // every locale and are used nowhere.
    test.fail()
    await openLogin(page)
    const banner = page.getByRole('banner')
    await expect(banner.getByRole('link', { name: /Pédalons/ })).toBeVisible()
    await expect(banner.getByRole('button')).toHaveCount(1)
    // The defect.
    await expect(banner.getByRole('button', { name: 'Ouvrir le menu' })).toBeVisible({
      timeout: 2_000,
    })
  })
})
