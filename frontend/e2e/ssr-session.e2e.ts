import type { Page } from '@playwright/test'
import { request } from '@playwright/test'
import type { AuthResponse } from './support/api'
import { addMember, newTeam, newUser, roleSession, signIn } from './support/data'
import { expect, test, unique } from './support/fixtures'
import { stack } from './support/stack'
import { joinGroup, newRide } from './support/rides'
import { pageHydrated, watchHydration } from './support/ui'

/**
 * docs/NEXT.md §1.2 — authenticated SSR (frontend/SSR.md, "Session-aware SSR"): for a document
 * request carrying the refresh_token cookie, « Ma prochaine sortie », the « Inscrit » badge on the
 * feed cards and « Mes participations » are in the server HTML; without the cookie they are not;
 * and hydration adopts that markup instead of throwing it away.
 *
 * Setup: a PUBLIC team (a platform admin is needed to make it public), a public ride two days
 * ahead, and a fresh user — member of the team — registered in its group. The feed is narrowed
 * with `?q=<ride name>` so the parallel suite's other publications never push it off page 0.
 */

const NEXT_RIDE = 'Ma prochaine sortie'
const REGISTERED = 'Inscrit'
const PARTICIPATIONS = 'Mes participations'
const UPCOMING = 'Mes sorties à venir'
const BREADCRUMB = "Fil d'Ariane"

interface Setup {
  rider: AuthResponse
  rideName: string
  feedPath: string
}

let setup: Setup

test.beforeAll(async () => {
  const admin = await roleSession('admin')
  const team = await newTeam(admin, unique('SSR session'), { visibility: 'PUBLIC' })
  const rider = await newUser('ssr-rider')
  await addMember(admin, team.slug, rider)
  const rideName = unique('Sortie SSR')
  const ride = await newRide(admin, team.slug, rideName, { visibility: 'PUBLIC' })
  await joinGroup(rider, team.slug, ride, ride.groups[0].name)
  setup = { rider, rideName, feedPath: `/?q=${encodeURIComponent(rideName)}` }
})

/** The feed card of the ride: a link whose content carries the ride name (NextRideCard is not a link). */
const feedCard = (page: Page, rideName: string) =>
  page.getByRole('link').filter({ hasText: rideName })

test.describe('server HTML, JavaScript disabled', () => {
  test.use({ javaScriptEnabled: false })

  test('signed in: home carries « Ma prochaine sortie » and the « Inscrit » feed badge', async ({
    page,
    context,
  }) => {
    await signIn(context, setup.rider)
    const response = await page.goto(setup.feedPath)
    expect(response?.status()).toBe(200)

    const nextRide = page.getByRole('heading', { name: NEXT_RIDE })
    await expect(nextRide).toBeVisible()
    // The card under the heading is this ride, not just any heading text.
    await expect(nextRide.locator('xpath=..')).toContainText(setup.rideName)

    const card = feedCard(page, setup.rideName)
    await expect(card).toHaveCount(1)
    await expect(card).toContainText(REGISTERED)
  })

  test('anonymous: the same feed lists the ride, without « Inscrit » nor « Ma prochaine sortie »', async ({
    page,
  }) => {
    const response = await page.goto(setup.feedPath)
    expect(response?.status()).toBe(200)

    // The page did render its feed server-side: the ride's card is there (public ride)...
    const card = feedCard(page, setup.rideName)
    await expect(card).toHaveCount(1)
    // ...but carries nothing that belongs to a session.
    await expect(card).not.toContainText(REGISTERED)
    await expect(page.getByRole('heading', { name: NEXT_RIDE })).toHaveCount(0)
  })

  test('signed in: the profile carries « Mes participations » with the upcoming count', async ({
    page,
    context,
  }) => {
    await signIn(context, setup.rider)
    const response = await page.goto('/profil')
    expect(response?.status()).toBe(200)

    await expect(page.getByRole('heading', { name: PARTICIPATIONS })).toBeVisible()
    // The count is the prefetched `size: 1` query's total: exactly the one registration.
    await expect(page.getByRole('button', { name: new RegExp(UPCOMING) })).toHaveText(
      new RegExp(`${UPCOMING}\\s*1$`)
    )
  })

  test('anonymous: the profile has no « Mes participations »', async ({ page }) => {
    const response = await page.goto('/profil')
    expect(response?.status()).toBe(200)
    // The server did render the page for a visitor (the guarded route stays on its loading
    // branch): its breadcrumb, and a way to sign in. Attached rather than visible — the mobile
    // layout hides both the breadcrumb and the header link.
    await expect(
      page.locator(`main nav[aria-label="${BREADCRUMB}"]`),
      'the anonymous profile page rendered its breadcrumb'
    ).toContainText('Profil')
    await expect(
      page.locator('a[href="/login"]').first(),
      'the anonymous page offers a login'
    ).toBeAttached()
    await expect(page.getByText(PARTICIPATIONS)).toHaveCount(0)
    await expect(page.getByText(UPCOMING)).toHaveCount(0)
  })
})

test.describe('raw document response', () => {
  /** The server-rendered markup only: what `<!--ssr-outlet-->` became, scripts and state excluded. */
  async function ssrOutlet(path: string, cookie?: string) {
    const api = await request.newContext({
      baseURL: stack.baseURL,
      extraHTTPHeaders: { 'Accept-Language': 'fr-FR', ...(cookie ? { Cookie: cookie } : {}) },
    })
    try {
      const response = await api.get(path)
      expect(response.status()).toBe(200)
      const html = await response.text()
      // From #root to the first script — StaticRouterProvider's hydration data, then the
      // dehydrated query state and __AUTH_STATE__, none of which is markup.
      const start = html.indexOf('<div id="root">')
      expect(start, 'no #root in the document').toBeGreaterThanOrEqual(0)
      const end = html.indexOf('<script', start)
      return { html: html.slice(start, end < 0 ? undefined : end), headers: response.headers() }
    } finally {
      await api.dispose()
    }
  }

  test('curl -H "Cookie: refresh_token=…": the blocks are in the markup, and not without it', async () => {
    const cookie = `refresh_token=${setup.rider.refreshToken}`

    const signedHome = await ssrOutlet(setup.feedPath, cookie)
    expect(signedHome.html).toContain(NEXT_RIDE)
    expect(signedHome.html).toContain(REGISTERED)
    expect(signedHome.html).toContain(setup.rideName)
    // Per-visitor HTML: SSR.md makes these a security requirement.
    expect(signedHome.headers['cache-control']).toContain('no-store')
    expect(signedHome.headers['vary']?.toLowerCase()).toContain('cookie')

    const anonymousHome = await ssrOutlet(setup.feedPath)
    expect(anonymousHome.html).toContain(setup.rideName)
    expect(anonymousHome.html).not.toContain(NEXT_RIDE)
    expect(anonymousHome.html).not.toContain(REGISTERED)

    const signedProfile = await ssrOutlet('/profil', cookie)
    expect(signedProfile.html).toContain(PARTICIPATIONS)
    const anonymousProfile = await ssrOutlet('/profil')
    // What the anonymous page does render — its breadcrumb and the login link — so that a broken
    // page cannot pass the absence check below.
    expect(anonymousProfile.html, 'the anonymous profile rendered its breadcrumb').toContain(
      `aria-label="${BREADCRUMB.replace("'", '&#x27;')}"`
    )
    expect(anonymousProfile.html, 'the anonymous profile offers a login').toContain('href="/login"')
    expect(anonymousProfile.html).not.toContain(PARTICIPATIONS)
  })
})

test.describe('hydration, JavaScript enabled', () => {
  test('home: the server blocks survive hydration untouched', async ({ page, context }) => {
    await signIn(context, setup.rider)
    const watch = await watchHydration(page, [NEXT_RIDE, REGISTERED])

    await page.goto(setup.feedPath)
    await pageHydrated(page)

    await expect(page.getByRole('heading', { name: NEXT_RIDE })).toBeVisible()
    await expect(feedCard(page, setup.rideName)).toContainText(REGISTERED)
    expect(watch.hydrationErrors).toEqual([])
    expect(await watch.removed()).toEqual([])
  })

  test('profile: « Mes participations » survives hydration untouched', async ({
    page,
    context,
  }) => {
    await signIn(context, setup.rider)
    const watch = await watchHydration(page, [PARTICIPATIONS])

    await page.goto('/profil')
    await pageHydrated(page)

    await expect(page.getByRole('heading', { name: PARTICIPATIONS })).toBeVisible()
    await expect(page.getByRole('button', { name: new RegExp(UPCOMING) })).toHaveText(
      new RegExp(`${UPCOMING}\\s*1$`)
    )
    expect(watch.hydrationErrors).toEqual([])
    expect(await watch.removed()).toEqual([])
  })
})
