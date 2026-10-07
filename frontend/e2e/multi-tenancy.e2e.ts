import type {
  ConfigDto,
  PostDto,
  PostRequest,
  PublicationListResponse,
  RideDto,
  TeamDetailDto,
} from '../src/api/dto'
import type { AuthResponse } from './support/api'
import { markdownMedia, newTeam, newUser, roleSession } from './support/data'
import {
  OTHER_HOST,
  OTHER_NAME,
  PINNED_HOST,
  PINNED_NAME,
  hostDocument,
  hostGet,
  hostPost,
  hostStatus,
  newTeamOn,
  originOf,
  otherAdmin,
  pinnedAlias,
  refreshOn,
  registerOn,
  signInOn,
} from './support/domains'
import { expect, test, unique } from './support/fixtures'
import { newPost, postPath } from './support/posts'
import { newRide, ridePath, rideRequest } from './support/rides'
import { authState, dehydratedQuery, sessionCookie, ssrOutlet } from './support/ssr'
import { stack } from './support/stack'
import { entityCard } from './support/ui'
import { wallTimeOf } from './support/dates'

/**
 * Multi-tenancy (docs/plans/archive/2026-09-27-e2e-coverage-audit.md, P1): one stack, several sites. The backend resolves the
 * tenant from the request's host (`DomainResolver`: `X-Forwarded-Host`, then `Host`) and every
 * query filters on it; the SSR server renders each document inside its own `AsyncLocalStorage`
 * store (`entry-server.tsx`), so the per-request config — the site's name, its WebAuthn RP ID, the
 * team it is pinned to — never crosses from one request to another.
 *
 * Two sites of the same stack: `localhost` (the suite's) and `autre.localhost`, a second domain the
 * platform admin creates on first use (support/domains.ts). Setup: on each, a PUBLIC team holding a
 * public ride and a public post, all named after one tag so a single search finds both teams; and
 * one address signed up on both sites — two accounts, one per domain.
 */

const LOCAL_HOST = new URL(stack.baseURL).hostname
const LOCAL_NAME = 'Pédalons E2E'

interface Site {
  host: string
  team: TeamDetailDto
  ride: RideDto
  post: PostDto
  /** The account of the shared address on this site. */
  twin: AuthResponse
}

let tag: string
let local: Site
let other: Site

test.beforeAll(async () => {
  tag = unique('Tenant')
  const [admin, otherAdminAuth] = await Promise.all([roleSession('admin'), otherAdmin()])

  const localTwin = await newUser(unique('Jumeau local'))
  const otherTwin = await registerOn(OTHER_HOST, {
    email: localTwin.user.email,
    displayName: unique('Jumeau autre'),
    password: 'e2e-password',
  })

  const localTeam = await newTeam(admin, `${tag} local`, { visibility: 'PUBLIC' })
  const [localRide, localPost] = await Promise.all([
    newRide(admin, localTeam.slug, `${tag} sortie locale`, { visibility: 'PUBLIC' }),
    newPost(admin, localTeam.slug, `${tag} article local`, { visibility: 'PUBLIC' }),
  ])
  local = { host: LOCAL_HOST, team: localTeam, ride: localRide, post: localPost, twin: localTwin }

  const otherOwner = await registerOn(OTHER_HOST, {
    email: `${localTwin.user.email.split('@')[0]}-owner@e2e.test`,
    displayName: 'Autre bureau',
    password: 'e2e-password',
  })
  const otherTeam = await newTeamOn(OTHER_HOST, otherOwner, `${tag} autre`, {
    visibility: 'PUBLIC',
    admin: otherAdminAuth,
  })
  const [otherRide, otherPost] = await Promise.all([
    hostPost<RideDto>(
      OTHER_HOST,
      otherOwner,
      `/api/teams/${otherTeam.slug}/rides`,
      rideRequest(`${tag} sortie autre`, { visibility: 'PUBLIC' })
    ),
    hostPost<PostDto>(OTHER_HOST, otherOwner, `/api/teams/${otherTeam.slug}/posts`, {
      name: `${tag} article autre`,
      media: markdownMedia(),
      dateTime: wallTimeOf(Date.now()),
      visibility: 'PUBLIC',
      status: 'PUBLISHED',
    } satisfies PostRequest),
  ])
  other = { host: OTHER_HOST, team: otherTeam, ride: otherRide, post: otherPost, twin: otherTwin }
})

const pairs = () =>
  [
    { from: local, to: other },
    { from: other, to: local },
  ] as const

test.describe('two domains, one stack', () => {
  test('each host is a site of its own: its name, its teams, none of the other', async ({
    page,
  }) => {
    for (const { from, to } of pairs()) {
      await page.goto(`${originOf(from.host)}/equipes?q=${encodeURIComponent(tag)}`)
      const name = from.host === LOCAL_HOST ? LOCAL_NAME : OTHER_NAME
      await expect(page.getByRole('banner').getByRole('link', { name, exact: true })).toBeVisible()
      const main = page.getByRole('main')
      await expect(entityCard(main, from.team.name)).toBeVisible()
      await expect(entityCard(main, to.team.name)).toHaveCount(0)
      await expect(main.getByText(to.team.name)).toHaveCount(0)
    }
  })

  test('a team, a ride and a post of one host are unknown on the other — API and server HTML', async () => {
    for (const { from, to } of pairs()) {
      const paths = [
        `/api/teams/${from.team.slug}`,
        `/api/teams/${from.team.slug}/rides/${from.ride.slug}`,
        `/api/teams/${from.team.slug}/posts/${from.post.slug}`,
      ]
      for (const path of paths) {
        // Readable where it lives (the control), unknown next door.
        expect(await hostStatus(from.host, undefined, path), `${path} on ${from.host}`).toBe(200)
        expect(await hostStatus(to.host, undefined, path), `${path} on ${to.host}`).toBe(404)
      }
      // Nor does the other host's feed list them, when their own host's does.
      const feed = (host: string) =>
        hostGet<PublicationListResponse>(
          host,
          undefined,
          `/api/publications?search=${encodeURIComponent(tag)}`
        ).then((page) => page.publications.map((item) => item.name))
      expect(await feed(from.host)).toEqual(
        expect.arrayContaining([from.ride.name, from.post.name])
      )
      const next = await feed(to.host)
      expect(next).not.toContain(from.ride.name)
      expect(next).not.toContain(from.post.name)

      // Rendered on the wrong host, the pages carry nothing of them: not in the markup, not in the
      // dehydrated cache, not in the link-preview tags — anywhere in the document. Answered 404
      // (026b3824): the team is unknown on that host, and the server says so to a crawler too.
      for (const [path, name] of [
        [ridePath(from.team.slug, from.ride.slug), from.ride.name],
        [postPath(from.team.slug, from.post.slug), from.post.name],
      ]) {
        const document = await hostDocument(to.host, path)
        expect(document.status, `${path} on ${to.host}`).toBe(404)
        expect(document.html).not.toContain(name)
        expect(document.html).not.toContain(from.team.name)
      }
    }
  })

  test('one address, two accounts: each host signs in its own', async ({ page, context }) => {
    expect(local.twin.user.id).not.toBe(other.twin.user.id)
    // Both sessions in one browser: cookies are host-only, so each host reads its own.
    await signInOn(context, LOCAL_HOST, local.twin)
    await signInOn(context, OTHER_HOST, other.twin)
    for (const site of [local, other]) {
      await page.goto(`${originOf(site.host)}/profil`)
      const main = page.getByRole('main')
      await expect(
        main.getByText(site.twin.user.displayName, { exact: true }).first()
      ).toBeVisible()
      const twin = site === local ? other.twin : local.twin
      await expect(main.getByText(twin.user.displayName, { exact: true })).toHaveCount(0)
    }
  })

  test('a session cookie of one host opens no session on the other', async ({ page, context }) => {
    // The refresh token is bound to its domain: the other host refuses it.
    expect(await refreshOn(OTHER_HOST, local.twin.refreshToken)).toBeNull()
    expect(await refreshOn(LOCAL_HOST, other.twin.refreshToken)).toBeNull()
    // Carried to the wrong host (a cookie set for it by hand), the SSR renders anonymously.
    await signInOn(context, OTHER_HOST, local.twin)
    await page.goto(`${originOf(OTHER_HOST)}/`)
    await expect(page.getByRole('banner').getByRole('link', { name: OTHER_NAME })).toBeVisible()
    await expect(page.getByRole('link', { name: 'Se connecter' }).first()).toBeVisible()
  })

  test('an access token of one host is refused on the other', async () => {
    // Regression (6a791794, AccessTokenDomainTest): a bearer whose `domainId` is not the resolved
    // domain's gives no user — before, the backend looked the token's e-mail up in the host's
    // domain and signed in as the account holding the same address there. A token whose address
    // has no account next door is refused too — the control.
    const lonely = await newUser(unique('Sans jumeau'))
    expect(await hostStatus(OTHER_HOST, lonely, '/api/users/me')).toBe(403)

    for (const { from, to } of pairs()) {
      const status = await hostStatus(to.host, from.twin, '/api/users/me')
      expect(status, `${from.host}'s token on ${to.host}`).not.toBe(200)
    }
  })
})

test.describe('concurrent server renders', () => {
  test('thirty interleaved documents on three hosts each carry their own site and session', async () => {
    // The third host: the alias of localhost pinned to a team (see pinned-host.e2e.ts).
    const alias = await pinnedAlias()
    const sites = [
      { host: LOCAL_HOST, name: LOCAL_NAME, rpId: LOCAL_HOST, pinned: undefined, twin: local.twin },
      { host: OTHER_HOST, name: OTHER_NAME, rpId: OTHER_HOST, pinned: undefined, twin: other.twin },
      // Same domain as localhost: same users, so the localhost account is signed in there too.
      {
        host: PINNED_HOST,
        name: PINNED_NAME,
        rpId: PINNED_HOST,
        pinned: alias.pinnedTeamSlug,
        twin: local.twin,
      },
    ]
    const names = sites.map((site) => site.name)

    // 30 requests fired together, host after host, signed in every other time: 3 and 2 being
    // coprime, each (host, signed in) pair comes up five times, next to requests of the others.
    const requests = Array.from({ length: 30 }, (_, i) => ({
      site: sites[i % 3],
      signedIn: i % 2 === 0,
    }))
    const documents = await Promise.all(
      requests.map(({ site, signedIn }) =>
        hostDocument(site.host, '/', {
          cookie: signedIn ? sessionCookie(site.twin) : undefined,
        })
      )
    )

    documents.forEach((document, i) => {
      const { site, signedIn } = requests[i]
      const label = `#${i} ${site.host} ${signedIn ? 'signed in' : 'anonymous'}`
      expect(document.status, label).toBe(200)

      // The config the client will hydrate with: this host's, not a neighbour's.
      const config = dehydratedQuery(document.html, '/api/config')?.state.data as ConfigDto
      expect(config?.appName, label).toBe(site.name)
      expect(config.webAuthnRpId, label).toBe(site.rpId)
      expect(config.pinnedTeamSlug ?? undefined, label).toBe(site.pinned)
      expect(config.singleTeam, label).toBe(!!site.pinned)

      // The markup: the header and footer name this site, never another.
      const markup = ssrOutlet(document.html)
      expect(markup, label).toContain(`>${site.name}<`)
      for (const name of names.filter((name) => name !== site.name))
        expect(markup, `${label} names ${name}`).not.toContain(name)

      // The session the page was rendered for: this host's account, or nobody.
      expect(authState(document.html)?.user?.id ?? null, label).toBe(
        signedIn ? site.twin.user.id : null
      )
    })
  })
})
