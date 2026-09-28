import type { Locator, Page } from '@playwright/test'
import type { AuthResponse } from './support/api'
import { signIn } from './support/data'
import { expect, test } from './support/fixtures'
import { stubBasemap } from './support/routes'
import {
  adTarget,
  entityTargets,
  freshSlug,
  isSlugPatch,
  newEntity,
  readAt,
  renameThroughApi,
  rideTarget,
  routeTarget,
  slugEditor,
  slugScene,
  teamFeed,
  teamTarget,
  type SlugTarget,
} from './support/slug-change'
import { hydrated, pageAs, toasts } from './support/ui'

/**
 * Changing a slug — the URL of a team, a ride, a route or an ad — from its edit form's
 * `SlugEditor`, and what becomes of the links already shared: the backend keeps a redirect from
 * every old slug (`TeamSlugRedirect`, `TeamEntitySlugRedirect`), so the old URL still leads to the
 * entity. An old team slug is redirected by the server (301, so a crawler without JavaScript moves
 * too); an old entity slug is rendered, and the page replaces it with the new one
 * (`useCanonicalPath`). A link in a chat, an ICS feed or a mobile deeplink must never end on a 404.
 *
 * One scenario per kind of entity, the same for all four: a malformed slug is refused on the page
 * without a request, a taken one by the API (409), and a new one replaces the URL; then a plain
 * member follows the old links. Every test builds its own owner (never a platform admin), team and
 * member.
 */

/**
 * Where `target` lives once its slug is `slug`: a team's slug is its own team slug, an entity's
 * team keeps its slug.
 */
function at(target: SlugTarget, teamSlug: string, slug: string) {
  const team = target.kind === 'team' ? slug : teamSlug
  return {
    team,
    edit: target.editPath(team, slug),
    detail: target.detailPath(team, slug),
  }
}

/**
 * The scene of one kind: its owner and member, the entity renamed (its slug and name), and a slug
 * already taken where it lives — another team's for a team (the seeded one), a sibling's for an
 * entity.
 */
async function sceneFor(target: SlugTarget, seedTeamSlug: string) {
  const { owner, member, team } = await slugScene(target.kind)
  if (target.kind === 'team')
    return { owner, member, teamSlug: team.slug, entity: team, taken: [seedTeamSlug] }
  const [entity, sibling] = await Promise.all([
    newEntity(target.kind, owner, team.slug, `Slug ${target.kind}`),
    newEntity(target.kind, owner, team.slug, `Slug ${target.kind} voisin`),
  ])
  // A route may not take the literal segments of its own API (`/routes/bulk`…): the backend
  // refuses them with the same 409 as a taken slug (SlugService.RESERVED_SLUGS).
  const taken = target === routeTarget ? [sibling.slug, 'bulk'] : [sibling.slug]
  return { owner, member, teamSlug: team.slug, entity, taken }
}

/** Opens the edit form of `target` at `slug`, signed in as `owner`. */
async function openEditForm(page: Page, owner: AuthResponse, target: SlugTarget, edit: string) {
  await signIn(page.context(), owner)
  // The route form draws its track on a map: keep the third-party basemap out of the test.
  if (target === routeTarget) await stubBasemap(page)
  await page.goto(edit)
  await expect(
    page.getByRole('main').getByRole('heading', { name: target.editTitle })
  ).toBeVisible()
}

/**
 * A member follows an old link and ends on the entity (never a 404), under the canonical address.
 * `oldTeamSlug`: the link carries a former team slug, which the server itself redirects (301) —
 * an old entity slug under the current team slug is rendered as is and swapped on the client.
 */
async function followOldLink(page: Page, from: string, to: string, oldTeamSlug: boolean) {
  const response = await page.goto(from)
  expect(response?.status(), `${from} is served, not a 404`).toBe(200)
  const redirectedFrom = response?.request().redirectedFrom()
  if (oldTeamSlug) {
    expect(redirectedFrom?.url(), `${from} is redirected by the server`).toContain(from)
  } else {
    expect(redirectedFrom, `${from} is rendered, not redirected`).toBeNull()
  }
  // Swapped on the client once the page hydrated and read the entity: slow on a loaded stack.
  await expect(page).toHaveURL(to, { timeout: 15_000 })
}

// Each scenario seeds a team and an entity (a GPX upload for a route), renames it twice and opens
// several pages as two users: well past the default 30 s once the whole suite shares the stack.
test.describe.configure({ timeout: 60_000 })

for (const target of [teamTarget, ...entityTargets]) {
  test.describe(`changing ${target.kind === 'ad' ? 'an' : 'a'} ${target.kind}'s slug`, () => {
    test('a malformed slug is refused on the page, a taken one by the API, and nothing changes', async ({
      page,
      seed,
    }) => {
      const { owner, teamSlug, entity, taken } = await sceneFor(target, seed.team.slug)
      const current = at(target, teamSlug, entity.slug)
      const patches: string[] = []
      page.on('request', (request) => {
        if (isSlugPatch(request.method(), request.url())) patches.push(request.url())
      })
      await openEditForm(page, owner, target, current.edit)

      const slug = slugEditor(page)
      await expect(slug.shown(entity.slug)).toBeVisible()
      await hydrated(slug.pencil)
      await slug.pencil.click()
      await expect(slug.input).toHaveValue(entity.slug)

      // The field only keeps what a slug is made of: lowercased, the rest dropped as it is typed.
      await slug.input.fill('Mon URL à moi!')
      await expect(slug.input).toHaveValue('monurlmoi')

      // Well-formed characters, malformed slug: refused before any request.
      await slug.input.fill('-debut')
      await slug.save.click()
      await expect(
        slug.open.getByText('Seules les lettres minuscules, chiffres et tirets sont autorisés', {
          exact: true,
        })
      ).toBeVisible()
      await slug.input.fill('')
      await slug.save.click()
      await expect(
        slug.open.getByText("L'URL ne peut pas être vide", { exact: true })
      ).toBeVisible()
      expect(patches, 'a malformed slug never reaches the API').toEqual([])

      // A taken slug is only known to the API: one PATCH, a 409, said inline and in a toast.
      for (const slugTaken of taken) {
        await slug.input.fill(slugTaken)
        const refused = page.waitForResponse((response) =>
          isSlugPatch(response.request().method(), response.url())
        )
        await slug.save.click()
        expect((await refused).status(), `${slugTaken} is taken`).toBe(409)
        await expect(
          slug.open.getByText('Cette URL est déjà utilisée', { exact: true })
        ).toBeVisible()
        await expect(
          toasts(page).filter({ hasText: 'Cette URL personnalisée est déjà utilisée' }).first()
        ).toBeVisible()
      }
      expect(patches).toHaveLength(taken.length)

      // Nothing moved: same address, and « Annuler » shows the slug it had.
      await expect(page).toHaveURL(current.edit)
      await slug.cancel.click()
      await expect(slug.shown(entity.slug)).toBeVisible()
      const stored = await readAt(owner, target, current.team, entity.slug)
      expect(stored.slug).toBe(entity.slug)
    })

    test('a new slug replaces the URL, the form still saves, and every old link leads to the entity', async ({
      page,
      browser,
      seed,
    }) => {
      const { owner, member, teamSlug, entity } = await sceneFor(target, seed.team.slug)
      const original = at(target, teamSlug, entity.slug)
      const renamed = freshSlug(`${target.kind} renomme`)
      const next = at(target, teamSlug, renamed)
      await openEditForm(page, owner, target, original.edit)

      const slug = slugEditor(page)
      await hydrated(slug.pencil)
      await slug.pencil.click()
      await slug.input.fill(renamed)
      await slug.save.click()

      await expect(toasts(page).filter({ hasText: 'URL modifiée avec succès' })).toBeVisible()
      await expect(page).toHaveURL(next.edit)
      await expect(slug.shown(renamed)).toBeVisible()
      // The API serves it under the new slug, and still under the old one.
      const stored = await readAt(owner, target, next.team, renamed)
      expect(stored.id).toBe(entity.id)
      expect((await readAt(owner, target, original.team, entity.slug)).slug).toBe(renamed)

      // The form, reloaded under the new slug, saves to it: no request left on the old one.
      // The form's own submit — the team settings hold a second « Enregistrer », the webhook's.
      const save = page
        .getByRole('main')
        .locator('form')
        .getByRole('button', { name: 'Enregistrer', exact: true })
      await hydrated(save)
      await save.click()
      await expect(toasts(page).filter({ hasText: target.savedToast })).toBeVisible()
      await expect(page).toHaveURL(next.detail)
      // The address changes before the detail page's lazy chunk has rendered: seconds on a loaded
      // stack, where the settings form still fills the main landmark.
      await expect(target.detailHeading(page, entity.name)).toBeVisible({ timeout: 15_000 })

      // Renamed once more (through the API): every slug it ever had still leads to it.
      const latestSlug = freshSlug(`${target.kind} encore`)
      await renameThroughApi(owner, target, next.team, renamed, latestSlug)
      const latest = at(target, teamSlug, latestSlug)

      const { context, page: memberPage } = await pageAs(browser, member)
      try {
        for (const old of [original, next]) {
          await followOldLink(memberPage, old.detail, latest.detail, target === teamTarget)
          await expect(target.detailHeading(memberPage, entity.name)).toBeVisible()
        }
      } finally {
        await context.close()
      }
    })
  })
}

test.describe("the old team slug's deep links", () => {
  test('a member following a link or a calendar feed under the old team slug gets the same page under the new one', async ({
    browser,
  }) => {
    const { owner, member, team } = await slugScene('liens')
    const [ride, route, ad] = await Promise.all([
      newEntity('ride', owner, team.slug, 'Slug lien sortie'),
      newEntity('route', owner, team.slug, 'Slug lien parcours'),
      newEntity('ad', owner, team.slug, 'Slug lien annonce'),
    ])
    const teamSlug = freshSlug('equipe renommee')
    await renameThroughApi(owner, teamTarget, team.slug, team.slug, teamSlug)
    // The ride is renamed too: a link carrying both old slugs.
    const rideSlug = freshSlug('sortie renommee')
    await renameThroughApi(owner, rideTarget, teamSlug, ride.slug, rideSlug)

    const main = (page: Page) => page.getByRole('main')
    const links: { from: string; to: string; landmark: (page: Page) => Locator }[] = [
      {
        from: rideTarget.detailPath(team.slug, ride.slug),
        to: rideTarget.detailPath(teamSlug, rideSlug),
        landmark: (page) => rideTarget.detailHeading(page, ride.name),
      },
      {
        from: routeTarget.detailPath(team.slug, route.slug),
        to: routeTarget.detailPath(teamSlug, route.slug),
        landmark: (page) => routeTarget.detailHeading(page, route.name),
      },
      {
        from: adTarget.detailPath(team.slug, ad.slug),
        to: adTarget.detailPath(teamSlug, ad.slug),
        landmark: (page) => adTarget.detailHeading(page, ad.name),
      },
      {
        from: `/equipes/${team.slug}/a-propos`,
        to: `/equipes/${teamSlug}/a-propos`,
        landmark: (page) =>
          main(page).getByRole('heading', { level: 2, name: "À propos de l'équipe" }),
      },
      {
        from: `/equipes/${team.slug}/calendrier`,
        to: `/equipes/${teamSlug}/calendrier`,
        landmark: (page) => main(page).getByRole('heading', { name: 'Calendrier', exact: true }),
      },
      {
        from: `/equipes/${team.slug}/parcours`,
        to: `/equipes/${teamSlug}/parcours`,
        landmark: (page) => main(page).getByRole('heading', { level: 2, name: 'Parcours' }),
      },
      {
        from: `/equipes/${team.slug}/annonces`,
        to: `/equipes/${teamSlug}/annonces`,
        landmark: (page) => main(page).getByRole('heading', { level: 2, name: 'Annonces' }),
      },
    ]

    const { context, page } = await pageAs(browser, member)
    try {
      await stubBasemap(page)
      for (const { from, to, landmark } of links) {
        await followOldLink(page, from, to, true)
        // The page the link was about, rendered — not an empty shell nor a « introuvable ».
        await expect(landmark(page)).toBeVisible()
      }
    } finally {
      await context.close()
    }

    // A calendar app subscribed before the rename keeps polling the old feed URL: it still gets
    // the team's rides.
    const feed = await teamFeed(member, team.slug)
    expect(feed.status, 'the feed under the old team slug is served').toBe(200)
    expect(feed.body).toContain(`SUMMARY:${ride.name}`)
  })
})
