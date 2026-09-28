import { randomBytes } from 'node:crypto'
import type { Page } from '@playwright/test'
import { apiGetOrNull, apiPut, ApiError, type AuthResponse } from './support/api'
import {
  addMember,
  markdownMedia,
  newTeam,
  newTeamPage,
  newUser,
  roleSession,
  signIn,
  teamRequest,
} from './support/data'
import { expect, test, unique } from './support/fixtures'
import { joinGroup, newRide, ridePath } from './support/rides'
import { newPost } from './support/posts'
import { newRoute, newTrip, routePath, stagePath, tripPath, windingTrack } from './support/routes'
import {
  authState,
  ogTags,
  rawDocument,
  reactQueryState,
  sessionCookie,
  ssrOutlet,
  type RawDocument,
} from './support/ssr'
import { pageAs, pageHydrated, watchHydration } from './support/ui'

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
  /** The server-rendered markup of `path`, and the response headers; the document must be a 200. */
  async function outletOf(path: string, cookie?: string) {
    const document = await rawDocument(path, { cookie })
    expect(document.status).toBe(200)
    return { html: ssrOutlet(document.html), headers: document.headers }
  }

  test('curl -H "Cookie: refresh_token=…": the blocks are in the markup, and not without it', async () => {
    const cookie = sessionCookie(setup.rider)

    const signedHome = await outletOf(setup.feedPath, cookie)
    expect(signedHome.html).toContain(NEXT_RIDE)
    expect(signedHome.html).toContain(REGISTERED)
    expect(signedHome.html).toContain(setup.rideName)
    // Per-visitor HTML: SSR.md makes these a security requirement.
    expect(signedHome.headers['cache-control']).toContain('no-store')
    expect(signedHome.headers['vary']?.toLowerCase()).toContain('cookie')

    const anonymousHome = await outletOf(setup.feedPath)
    expect(anonymousHome.html).toContain(setup.rideName)
    expect(anonymousHome.html).not.toContain(NEXT_RIDE)
    expect(anonymousHome.html).not.toContain(REGISTERED)

    const signedProfile = await outletOf('/profil', cookie)
    expect(signedProfile.html).toContain(PARTICIPATIONS)
    const anonymousProfile = await outletOf('/profil')
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

/**
 * E2E_COVERAGE_AUDIT.md, P0 #9 — a members-only (TEAM) team leaks nothing into the server document.
 * An unfurl crawler or a search engine reads the raw HTML with no session, and a signed-in
 * non-member gets the same HTML rendered for them: neither may find the team's name, its
 * description, nor the name or text of anything it holds — in the markup, in the dehydrated
 * `__REACT_QUERY_STATE__`, or in the `og:*` / `twitter:*` / `<title>` link preview. A member gets all
 * of it, which is the positive control that the needles are the right ones.
 *
 * The team goes private the way a real one does: born PUBLIC, its ride, post, route, trip and page
 * published PUBLIC, then switched to TEAM by the platform admin. Nothing cascades to the entities —
 * each still says PUBLIC (checked below) — so what hides them is the team-level clause alone:
 * `te.team.visibility <> 'TEAM'` in `TeamEntityRepository.getPublicEntity` and `TeamAccessChecker`'s
 * READ. Content created TEAM inside a TEAM team would be hidden twice over and prove less.
 *
 * Each name is `unique(...)`, with spaces and capitals, so it never matches the slug the URL (and
 * `og:url`) legitimately carries; each description is a random token found nowhere else.
 */
test.describe('private team: nothing in the server document', () => {
  interface PrivatePage {
    label: string
    path: string
    /** Rendered by the page, and in its preview title, for a member. */
    name: string
    /** In the preview description, for a member — pages whose entity has a text of its own. */
    description?: string
  }

  interface PrivateTeam {
    slug: string
    member: AuthResponse
    outsider: AuthResponse
    pages: PrivatePage[]
    /** Cross-team listings narrowed to one private item: they must not list it. */
    listings: { label: string; path: string; name: string }[]
    /** Every name and text of the team, by label. */
    secrets: Record<string, string>
  }

  /** A text found nowhere else: a random token, no markdown in it for stripMarkdown to eat. */
  const secretText = () => `Confidentiel${randomBytes(8).toString('hex')}`

  /** Rendered by the layout of every page, whoever the visitor: the render did not crash. */
  const FOOTER = 'Tous droits réservés'

  let priv: PrivateTeam

  test.beforeAll(async () => {
    const admin = await roleSession('admin')
    const [owner, member, outsider] = await Promise.all([
      newUser('private-owner'),
      newUser('private-member'),
      newUser('private-outsider'),
    ])

    const teamName = unique('Club privé')
    const teamAbout = secretText()
    const team = await newTeam(owner, teamName, {
      visibility: 'PUBLIC',
      media: markdownMedia(teamAbout),
    })
    await addMember(admin, team.slug, member)

    const names = {
      ride: unique('Sortie privée'),
      post: unique('Article privé'),
      route: unique('Parcours privé'),
      trip: unique('Voyage privé'),
      stage: unique('Étape privée'),
      page: unique('Page privée'),
    }
    const texts = {
      ride: secretText(),
      post: secretText(),
      route: secretText(),
      trip: secretText(),
      page: secretText(),
    }
    const [ride, post, route, trip, teamPage] = await Promise.all([
      newRide(owner, team.slug, names.ride, {
        visibility: 'PUBLIC',
        media: markdownMedia(texts.ride),
      }),
      newPost(owner, team.slug, names.post, {
        visibility: 'PUBLIC',
        media: markdownMedia(texts.post),
      }),
      newRoute(owner, team.slug, names.route, windingTrack(), {
        visibility: 'PUBLIC',
        media: markdownMedia(texts.route),
      }),
      newTrip(owner, team.slug, names.trip, [{ name: names.stage }], {
        visibility: 'PUBLIC',
        media: markdownMedia(texts.trip),
      }),
      newTeamPage(owner, team.slug, names.page, texts.page, { visibility: 'PUBLIC' }),
    ])

    // The team goes private; its content keeps saying PUBLIC.
    await apiPut(
      admin,
      `/api/teams/${team.slug}`,
      teamRequest(teamName, { visibility: 'TEAM', media: markdownMedia(teamAbout) })
    )
    const refused = await apiGetOrNull(undefined, `/api/teams/${team.slug}`).catch(
      (error: unknown) => error
    )
    expect(refused instanceof ApiError && refused.status, 'the API refuses the team').toBe(403)
    const stillPublic = await apiGetOrNull<{ visibility: string }>(
      member,
      `/api/teams/${team.slug}/rides/${ride.slug}`
    )
    expect(stillPublic?.visibility, 'the ride itself still says PUBLIC').toBe('PUBLIC')

    // Searched by the unique suffix alone: the page echoes its search (input value, query key),
    // and the echo of a secret typed into the URL would be no leak.
    const q = (name: string) => encodeURIComponent(name.split(' ').at(-1)!)
    priv = {
      slug: team.slug,
      member,
      outsider,
      pages: [
        { label: 'team', path: `/equipes/${team.slug}`, name: teamName, description: teamAbout },
        {
          label: 'team about',
          path: `/equipes/${team.slug}/a-propos`,
          name: teamName,
          description: teamAbout,
        },
        {
          label: 'ride',
          path: ridePath(team.slug, ride.slug),
          name: names.ride,
          description: texts.ride,
        },
        {
          label: 'post',
          path: `/equipes/${team.slug}/articles/${post.slug}`,
          name: names.post,
          description: texts.post,
        },
        {
          label: 'route',
          path: routePath(team.slug, route.slug),
          name: names.route,
          description: texts.route,
        },
        {
          label: 'trip',
          path: tripPath(team.slug, trip.slug),
          name: names.trip,
          description: texts.trip,
        },
        {
          label: 'stage',
          path: stagePath(team.slug, trip.slug, trip.stages[0].slug),
          name: names.stage,
        },
        {
          label: 'team page',
          path: `/equipes/${team.slug}/pages/${teamPage.slug}`,
          name: names.page,
          description: texts.page,
        },
      ],
      listings: [
        { label: 'home feed', path: `/?q=${q(names.ride)}`, name: names.ride },
        { label: 'team list', path: `/equipes?q=${q(teamName)}&role=all`, name: teamName },
        { label: 'route list', path: `/parcours?q=${q(names.route)}&role=all`, name: names.route },
      ],
      secrets: {
        'team name': teamName,
        'team description': teamAbout,
        ...Object.fromEntries(Object.entries(names).map(([k, v]) => [`${k} name`, v])),
        ...Object.fromEntries(Object.entries(texts).map(([k, v]) => [`${k} text`, v])),
      },
    }
  })

  /** Every secret of the team, looked for in each layer of the document on its own. */
  function expectNothingPrivate(document: RawDocument, where: string) {
    const layers: Record<string, string> = {
      markup: ssrOutlet(document.html),
      __REACT_QUERY_STATE__: JSON.stringify(reactQueryState(document.html)),
      'link preview': JSON.stringify(ogTags(document.html)),
      // Anything else: __AUTH_STATE__, the router's hydration data, an attribute.
      document: document.html,
    }
    for (const [layer, text] of Object.entries(layers))
      for (const [label, secret] of Object.entries(priv.secrets))
        expect.soft(text.includes(secret), `${where}: the ${label} in the ${layer}`).toBe(false)
  }

  /** The dehydrated queries of the team's own API that carry data (a refused one carries none). */
  const teamQueriesWithData = (html: string) =>
    reactQueryState(html)
      .queries.filter(
        (query) =>
          String(query.queryKey[0]).startsWith(`/api/teams/${priv.slug}`) &&
          query.state.data !== undefined
      )
      .map((query) => JSON.stringify(query.queryKey))

  const refusedVisitors = [
    { visitor: 'anonymous', session: (): AuthResponse | undefined => undefined },
    { visitor: 'signed-in non-member', session: (): AuthResponse | undefined => priv.outsider },
  ]

  for (const { visitor, session } of refusedVisitors) {
    test(`${visitor}: no page of the team carries its content`, async () => {
      const auth = session()
      for (const page of priv.pages) {
        const document = await rawDocument(page.path, {
          cookie: auth ? sessionCookie(auth) : undefined,
        })
        const where = `${page.label} (${page.path})`
        // A rendered page, for this visitor — not a crash, nor an anonymous render of the outsider.
        // Answered 404, as a team that never existed: a crawler must not index a private URL.
        expect(document.status, `${where}: status`).toBe(404)
        expect(authState(document.html)?.user?.id ?? null, `${where}: rendered as`).toBe(
          auth?.user.id ?? null
        )
        expect(ssrOutlet(document.html), `${where}: the layout rendered`).toContain(FOOTER)

        expectNothingPrivate(document, where)
        expect
          .soft(teamQueriesWithData(document.html), `${where}: team data handed over`)
          .toEqual([])
        // The preview falls back to the site defaults: nothing entity-specific to unfurl.
        const preview = ogTags(document.html)
        expect.soft(preview['og:type'], `${where}: og:type`).toEqual(['website'])
      }
    })

    test(`${visitor}: the cross-team listings do not list the team's content`, async () => {
      const auth = session()
      for (const listing of priv.listings) {
        const document = await rawDocument(listing.path, {
          cookie: auth ? sessionCookie(auth) : undefined,
        })
        const where = `${listing.label} (${listing.path})`
        expect(document.status, `${where}: status`).toBe(200)
        expect(authState(document.html)?.user?.id ?? null, `${where}: rendered as`).toBe(
          auth?.user.id ?? null
        )
        expect(ssrOutlet(document.html), `${where}: the layout rendered`).toContain(FOOTER)
        expectNothingPrivate(document, where)
      }
    })
  }

  test('member: the same documents carry the team content (positive control)', async () => {
    const cookie = sessionCookie(priv.member)
    for (const page of priv.pages) {
      const document = await rawDocument(page.path, { cookie })
      const where = `${page.label} (${page.path})`
      expect(document.status, `${where}: status`).toBe(200)
      expect(authState(document.html)?.user?.id, `${where}: rendered as`).toBe(priv.member.user.id)

      // The same structural probe the refused visitors pass: here it does find the team's data.
      expect
        .soft(teamQueriesWithData(document.html).length, `${where}: team data handed over`)
        .toBeGreaterThan(0)
      const markup = ssrOutlet(document.html)
      const state = JSON.stringify(reactQueryState(document.html))
      const preview = ogTags(document.html)
      expect.soft(markup.includes(page.name), `${where}: the name in the markup`).toBe(true)
      expect.soft(state.includes(page.name), `${where}: the name in the query state`).toBe(true)
      expect.soft(preview['og:title']?.[0], `${where}: og:title`).toContain(page.name)
      expect.soft(preview['title']?.[0], `${where}: <title>`).toContain(page.name)
      if (page.description) {
        expect
          .soft(state.includes(page.description), `${where}: the text in the query state`)
          .toBe(true)
        expect
          .soft(preview['og:description']?.[0], `${where}: og:description`)
          .toContain(page.description)
      }
    }
    for (const listing of priv.listings) {
      const document = await rawDocument(listing.path, { cookie })
      const where = `${listing.label} (${listing.path})`
      expect(document.status, `${where}: status`).toBe(200)
      expect
        .soft(ssrOutlet(document.html).includes(listing.name), `${where}: listed in the markup`)
        .toBe(true)
    }
  })

  test('signed-in non-member, JavaScript on: nor the page nor any API response shows it', async ({
    browser,
  }) => {
    const { context, page } = await pageAs(browser, priv.outsider)
    try {
      const bodies: Promise<{ url: string; status: number; body: string }>[] = []
      page.on('response', (response) => {
        if (!new URL(response.url()).pathname.startsWith('/api/')) return
        bodies.push(
          response
            .text()
            .catch(() => '')
            .then((body) => ({ url: response.url(), status: response.status(), body }))
        )
      })
      for (const privatePage of priv.pages) {
        const where = `${privatePage.label} (${privatePage.path})`
        bodies.length = 0
        await page.goto(privatePage.path)
        await pageHydrated(page)

        const responses = await Promise.all(bodies)
        // The page did ask the team's API, and was refused — the absence below is an answer.
        const teamReads = responses.filter((r) =>
          new URL(r.url).pathname.startsWith(`/api/teams/${priv.slug}`)
        )
        expect(teamReads.length, `${where}: the page read the team's API`).toBeGreaterThan(0)
        for (const read of teamReads)
          expect.soft(read.status, `${where}: ${read.url}`).toBeGreaterThanOrEqual(400)

        const text = await page.locator('body').innerText()
        for (const [label, secret] of Object.entries(priv.secrets)) {
          expect.soft(text.includes(secret), `${where}: the ${label} on screen`).toBe(false)
          for (const response of responses)
            expect
              .soft(response.body.includes(secret), `${where}: the ${label} in ${response.url}`)
              .toBe(false)
        }
      }
    } finally {
      await context.close()
    }
  })
})
