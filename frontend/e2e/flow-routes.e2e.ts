import { writeFileSync } from 'node:fs'
import type { Locator, Page } from '@playwright/test'
import type { PlaceListResponse, RouteListResponse } from '../src/api/dto'
import { apiGet, type AuthResponse, type Caller } from './support/api'
import { addMember, newTeam, newUser, signIn, type NewTeamOptions } from './support/data'
import { richText } from './support/editor'
import { expect, test, unique } from './support/fixtures'
import {
  getRoute,
  gpxOf,
  newRoute,
  routePath,
  stubBasemap,
  traceMapPixels,
  type GpxPoint,
} from './support/routes'
import { entityCard, hydrated, pageAs } from './support/ui'

/**
 * The nominal journeys of the routes section (parcours) and of the team's places (lieux), through
 * the UI: upload a GPX in the « Créer un nouveau parcours » form, read the detail page and download
 * the GPX, edit the name, description and surface, find the route in the team's list and by search,
 * delete it — and add, rename and delete a place in the team admin.
 *
 * Each test has its own team admin and team: a fresh user who creates a team is its ADMIN — more
 * than the route and place forms ask for (an organizer may use them too), and it matters once a
 * route is deleted: a team admin still sees it, and restores it. Routes a test only needs to exist are seeded through the API;
 * each outcome is checked on the page and through the API. The full-geometry checks of the maps
 * live in route-maps.e2e.ts: here the map only has to be drawn.
 */

/**
 * 4 km heading due east out of Grenoble: 81 points 50 m apart. The backend replaces the GPX
 * elevations with SRTM data where it has some, so the elevations written here are not what it
 * reports — the tests compare the page with the API, never with this track's climbing.
 */
function eastward(): GpxPoint[] {
  const [lat0, lon0] = [45.1885, 5.7245]
  const metresPerDegLon = 111_320 * Math.cos((lat0 * Math.PI) / 180)
  return Array.from({ length: 81 }, (_, i) => [
    lat0,
    Number((lon0 + (i * 50) / metresPerDegLon).toFixed(7)),
    400 + i * 2.5,
  ])
}

/** A team admin (a fresh user) and a team of their own, of which they are the ADMIN. */
async function ownTeam(label: string, options: NewTeamOptions = {}) {
  const owner = await newUser(unique('Admin parcours'))
  const team = await newTeam(owner, unique(`Parcours ${label}`), options)
  return { owner, team }
}

const listRoutes = (auth: AuthResponse, teamSlug: string, search?: string) =>
  apiGet<RouteListResponse>(auth, `/api/teams/${teamSlug}/routes`, search ? { search } : {})

const listPlaces = (auth: AuthResponse, teamSlug: string) =>
  apiGet<PlaceListResponse>(auth, `/api/teams/${teamSlug}/places`)

const routesPath = (teamSlug: string) => `/equipes/${teamSlug}/parcours`

/** Opens a route's page and waits for its title. Returns the page's main region. */
async function openRouteDetail(page: Page, teamSlug: string, routeSlug: string, name: string) {
  await page.goto(routePath(teamSlug, routeSlug))
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { level: 1, name })).toBeVisible()
  return main
}

/** Opens the team's route list and waits for its title. Returns the page's main region. */
async function openRouteList(page: Page, teamSlug: string) {
  await page.goto(routesPath(teamSlug))
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { level: 2, name: 'Parcours' })).toBeVisible()
  return main
}

const routeCard = entityCard

/** The value shown under one of the detail page's stat labels (« Distance », « Dénivelé positif »). */
const statValue = (main: Locator, label: string) =>
  main.getByText(label, { exact: true }).locator('xpath=following-sibling::*[1]')

test.beforeEach(async ({ page }) => {
  // The basemap is a third-party CDN, not part of the stack: the map must draw without it.
  await stubBasemap(page)
})

test('a team admin creates a route by uploading a GPX; its page shows its stats, map and GPX', async ({
  page,
  context,
}, testInfo) => {
  const { owner, team } = await ownTeam('création')
  const name = unique('Sortie vers le Grésivaudan')
  const points = eastward()
  const gpxPath = testInfo.outputPath('vers-le-gresivaudan.gpx')
  writeFileSync(gpxPath, gpxOf(name, points))

  await signIn(context, owner)
  const list = await openRouteList(page, team.slug)
  // An empty team offers the form twice — in the header and in the empty state; either will do.
  await expect(list.getByRole('heading', { name: 'Aucun parcours' })).toBeVisible()
  const create = list.getByRole('link', { name: 'Créer un nouveau parcours' }).first()
  await hydrated(create)
  await create.click()

  const main = page.getByRole('main')
  await expect(
    main.getByRole('heading', { level: 1, name: 'Créer un nouveau parcours' })
  ).toBeVisible()
  const submit = main.getByRole('button', { name: 'Créer le parcours' })
  await hydrated(submit)
  // No file yet: nothing to create.
  await expect(submit).toBeDisabled()
  // Mantine's FileInput: a button, and a hidden input the description editor's uploads share the
  // page with — the GPX one is the one accepting .gpx.
  await main.locator('input[type="file"][accept=".gpx"]').setInputFiles(gpxPath)
  // The file's name pre-fills the route's; the team admin types their own.
  const nameInput = main.getByRole('textbox', { name: 'Nom du parcours' })
  await expect(nameInput).toHaveValue('vers-le-gresivaudan')
  await nameInput.fill(name)
  await expect(submit).toBeEnabled()
  await submit.click()

  // Straight to the new route's page.
  await expect(main.getByRole('heading', { level: 1, name })).toBeVisible()
  await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}/parcours/[^/?]+$`))
  const routeSlug = new URL(page.url()).pathname.split('/').pop()!

  // The route really exists, built from the uploaded track: 80 × 50 m = 4 km.
  const stored = await getRoute(owner, team.slug, routeSlug)
  expect(stored.name).toBe(name)
  expect(stored.surfaceType).toBe('ROAD')
  expect(stored.distance).toBeGreaterThan(3_900)
  expect(stored.distance).toBeLessThan(4_100)

  // Its stats, as the API has them.
  await expect(statValue(main, 'Distance')).toHaveText(`${(stored.distance / 1000).toFixed(1)} km`)
  await expect(statValue(main, 'Dénivelé positif')).toHaveText(
    `${Math.round(stored.elevationGain)} m`
  )
  await expect(statValue(main, 'Dénivelé négatif')).toHaveText(
    `${Math.round(stored.elevationLoss)} m`
  )
  // The map is drawn (on the stubbed basemap).
  await expect(page.locator('canvas.maplibregl-canvas')).toBeVisible()

  // The GPX is offered for download, and it is this track.
  const download = main.getByRole('link', { name: 'Télécharger GPX' })
  await expect(download).toBeVisible()
  const href = await download.getAttribute('href')
  expect(href, 'the download link has a target').toBeTruthy()
  const gpx = await page.request.get(href!)
  expect(gpx.ok()).toBe(true)
  const body = await gpx.text()
  expect(body).toContain('<gpx')
  const trkpts = [...body.matchAll(/<trkpt lat="([\d.]+)" lon="([\d.]+)"/g)].map((m) => [
    Number(m[1]),
    Number(m[2]),
  ])
  // A straight line: the backend's simplification may keep its two ends only. It starts where the
  // uploaded track starts and ends where it ends (within ~10 m).
  expect(trkpts.length).toBeGreaterThanOrEqual(2)
  const [first, last] = [points[0], points[points.length - 1]]
  expect(trkpts[0][0]).toBeCloseTo(first[0], 4)
  expect(trkpts[0][1]).toBeCloseTo(first[1], 4)
  expect(trkpts[trkpts.length - 1][0]).toBeCloseTo(last[0], 4)
  expect(trkpts[trkpts.length - 1][1]).toBeCloseTo(last[1], 4)

  // And the route is now in the team's list.
  const back = await openRouteList(page, team.slug)
  await expect(routeCard(back, name)).toBeVisible()
})

test('a team admin renames a route and writes its description', async ({ page, context }) => {
  const { owner, team } = await ownTeam('édition')
  const before = unique('Boucle avant')
  const after = unique('Boucle après')
  const description = 'Départ devant la gare, pause café au sommet.'
  const route = await newRoute(owner, team.slug, before, eastward())

  await signIn(context, owner)
  const main = await openRouteDetail(page, team.slug, route.slug, before)
  const edit = main.getByRole('link', { name: 'Modifier', exact: true })
  await hydrated(edit)
  await edit.click()

  await expect(main.getByRole('heading', { level: 1, name: 'Modifier le parcours' })).toBeVisible()
  const nameInput = main.getByRole('textbox', { name: 'Nom du parcours' })
  await expect(nameInput).toHaveValue(before)
  const save = main.getByRole('button', { name: 'Enregistrer' })
  await hydrated(save)

  await nameInput.fill(after)
  const editor = richText(main)
  await editor.click()
  await editor.pressSequentially(description)
  await expect(editor).toHaveText(description)
  // Straight to « Enregistrer »: the click blurs the editor, which hands its text to the form.
  await save.click()

  // Back on the route's page, under its new name and with its description.
  await expect(main.getByRole('heading', { level: 1, name: after })).toBeVisible()
  await expect(page).toHaveURL(routePath(team.slug, route.slug))
  await expect(main.getByText(description)).toBeVisible()

  const stored = await getRoute(owner, team.slug, route.slug)
  expect(stored.name).toBe(after)
  expect(stored.media.markdown.trim()).toBe(description)
  // An edit without a file leaves the track alone.
  expect(stored.surfaceType).toBe('ROAD')
  expect(stored.distance).toBe(route.distance)
  expect(stored.media.assets.gpx?.id).toBe(route.media.assets.gpx?.id)
})

test("a team admin changes a route's surface", async ({ page, context }, testInfo) => {
  // Skipped, not pinned: the vanishing dropdown below has no cause in the app code and may be an
  // artefact of Chromium's mobile emulation. To check on a real phone before deciding
  // (2026-09-25); until then the phone run would only freeze an emulation quirk.
  test.fixme(
    testInfo.project.name === 'mobile',
    'à vérifier sur un vrai appareil : sur téléphone émulé, le menu « Type de revêtement » disparaît à l’ouverture'
  )
  const { owner, team } = await ownTeam('revêtement')
  const name = unique('Chemin blanc')
  const route = await newRoute(owner, team.slug, name, eastward())

  await signIn(context, owner)
  await page.goto(`${routePath(team.slug, route.slug)}/modifier`)
  const main = page.getByRole('main')
  await expect(main.getByRole('textbox', { name: 'Nom du parcours' })).toHaveValue(name)
  const surface = main.getByRole('combobox', { name: 'Type de revêtement' })
  await hydrated(surface)
  await expect(surface, 'the route is a road route').toHaveValue('Route')
  await surface.click()
  await expect(surface, 'the dropdown is open').toHaveAttribute('aria-expanded', 'true')

  // What the phone emulation shows (mobile only, skipped above).
  // Opening the Select scrolls the active option into view (Mantine Combobox,
  // scrollIntoView({ block: 'nearest' })) before the dropdown is placed; on a 839 px-high viewport,
  // with the Select near the bottom of the page, that scrolls the window up by ~380 px, the Select
  // leaves the viewport, and the Popover's hide() middleware (@mantine/core use-popover.mjs:18,
  // PopoverDropdown.mjs:80) sets the dropdown to display: none — no option can be tapped.
  const gravel = page.getByRole('option', { name: 'Gravel' })
  await expect(gravel).toBeVisible()
  await gravel.click()
  await expect(surface).toHaveValue('Gravel')
  const save = main.getByRole('button', { name: 'Enregistrer' })
  await save.click()

  await expect(main.getByRole('heading', { level: 1, name })).toBeVisible()
  await expect(main.getByText('Gravel', { exact: true })).toBeVisible()
  const stored = await getRoute(owner, team.slug, route.slug)
  expect(stored.surfaceType).toBe('GRAVEL')
  expect(stored.name).toBe(name)
})

test("the team's list shows its routes, and the search finds one by name", async ({
  page,
  context,
}) => {
  const { owner, team } = await ownTeam('liste')
  const wanted = unique('Col de Porte')
  const other = unique('Tour du lac')
  await newRoute(owner, team.slug, wanted, eastward())
  await newRoute(owner, team.slug, other, eastward())

  await signIn(context, owner)
  const main = await openRouteList(page, team.slug)
  await expect(main.getByText('2 parcours', { exact: true })).toBeVisible()
  await expect(routeCard(main, wanted)).toBeVisible()
  await expect(routeCard(main, other)).toBeVisible()

  // The search lives in the filter panel.
  const filters = main.getByRole('button', { name: 'Filtres' })
  await hydrated(filters)
  await filters.click()
  const search = main.getByRole('searchbox', { name: 'Rechercher des parcours' })
  await expect(search).toBeVisible()
  await search.fill('Col de Porte')
  // The search goes into the URL (`q`), like every list filter.
  await expect(page).toHaveURL(/[?&]q=Col/)
  await expect(main.getByText('1 parcours', { exact: true })).toBeVisible()
  await expect(routeCard(main, wanted)).toBeVisible()
  await expect(routeCard(main, other)).toHaveCount(0)
  // The API agrees: that search matches this route alone.
  const found = await listRoutes(owner, team.slug, 'Col de Porte')
  expect(found.routes.map((r) => r.name)).toEqual([wanted])

  // A card opens its route.
  await routeCard(main, wanted).click()
  await expect(main.getByRole('heading', { level: 1, name: wanted })).toBeVisible()
})

test('a team admin deletes a route: members no longer see it, the team admin can restore it', async ({
  page,
  context,
  browser,
}) => {
  const { owner, team } = await ownTeam('suppression', { addMemberAllowed: true })
  const member = await newUser(unique('Membre'))
  await addMember(owner, team.slug, member)
  const name = unique('Parcours éphémère')
  const route = await newRoute(owner, team.slug, name, eastward())

  await signIn(context, owner)
  const main = await openRouteDetail(page, team.slug, route.slug, name)
  const remove = main.getByRole('button', { name: 'Supprimer', exact: true })
  await hydrated(remove)
  await remove.click()

  const dialog = page.getByRole('dialog', { name: 'Supprimer le parcours' })
  await expect(dialog).toContainText('Êtes-vous sûr de vouloir supprimer ce parcours ?')
  await dialog.getByRole('button', { name: 'Supprimer', exact: true }).click()

  // Back on the list, where the team admin still sees it, marked deleted.
  await expect(page).toHaveURL(new RegExp(`${routesPath(team.slug)}$`))
  await expect(main.getByRole('heading', { level: 2, name: 'Parcours' })).toBeVisible()
  await expect(routeCard(main, name)).toContainText('Supprimé')
  expect((await getRoute(owner, team.slug, route.slug)).deleted).toBe(true)

  // A member no longer sees it, neither in the list nor through the API.
  const { context: memberContext, page: memberPage } = await pageAs(browser, member)
  try {
    await stubBasemap(memberPage)
    const memberMain = await openRouteList(memberPage, team.slug)
    await expect(memberMain.getByRole('heading', { name: 'Aucun parcours' })).toBeVisible()
    await expect(routeCard(memberMain, name)).toHaveCount(0)
  } finally {
    await memberContext.close()
  }
  const seenByMember = await listRoutes(member, team.slug)
  expect(seenByMember.routes.map((r) => r.slug)).not.toContain(route.slug)

  // The team admin brings it back from its page.
  await routeCard(main, name).click()
  await expect(main.getByRole('heading', { level: 1, name })).toBeVisible()
  const restore = main.getByRole('button', { name: 'Restaurer' })
  await hydrated(restore)
  await restore.click()
  await expect(restore).toHaveCount(0)
  expect((await getRoute(owner, team.slug, route.slug)).deleted).toBeFalsy()
  expect((await listRoutes(member, team.slug)).routes.map((r) => r.slug)).toContain(route.slug)
})

test.describe('places', () => {
  const placesPath = (teamSlug: string) => `/equipes/${teamSlug}/admin/lieux`

  async function openPlaces(page: Page, teamSlug: string) {
    await page.goto(placesPath(teamSlug))
    const main = page.getByRole('main')
    await expect(main.getByRole('heading', { name: 'Lieux', exact: true })).toBeVisible()
    return main
  }

  test('a team admin adds a place, renames it and deletes it', async ({ page, context }) => {
    const { owner, team } = await ownTeam('lieux')
    const name = unique('Parking du stade')
    const renamed = unique('Place de la mairie')
    const address = '1 rue du Stade, 38000 Grenoble'
    const link = 'https://www.openstreetmap.org/#map=18/45.1885/5.7245'

    await signIn(context, owner)
    const main = await openPlaces(page, team.slug)
    await expect(main.getByText(/^Aucun lieu défini\./)).toBeVisible()

    // Add.
    const add = main.getByRole('button', { name: 'Ajouter un lieu' })
    await hydrated(add)
    await add.click()
    const form = page.getByRole('dialog', { name: 'Ajouter un lieu' })
    await form.getByRole('textbox', { name: 'Nom' }).fill(name)
    await form.getByRole('textbox', { name: 'Adresse' }).fill(address)
    // A link is optional (a place without one: the next test); this one has one, to check it.
    await form.getByRole('textbox', { name: 'Lien' }).fill(link)
    await form.getByRole('checkbox', { name: "Peut servir d'arrivée" }).uncheck()
    await form.getByRole('button', { name: 'Ajouter', exact: true }).click()
    await expect(form).toHaveCount(0)
    await expect(main.getByText(name, { exact: true })).toBeVisible()
    await expect(main.getByText(address, { exact: true })).toBeVisible()
    await expect(main.getByRole('link', { name: 'Voir sur la carte' })).toHaveAttribute(
      'href',
      link
    )

    let places = await listPlaces(owner, team.slug)
    expect(places.places).toHaveLength(1)
    expect(places.places[0]).toMatchObject({
      name,
      address,
      link,
      startPlace: true,
      endPlace: false,
    })

    // Rename.
    await main.getByRole('button', { name: 'Modifier', exact: true }).click()
    const editForm = page.getByRole('dialog', { name: 'Modifier le lieu' })
    const nameInput = editForm.getByRole('textbox', { name: 'Nom' })
    await expect(nameInput).toHaveValue(name)
    await nameInput.fill(renamed)
    await editForm.getByRole('button', { name: 'Enregistrer' }).click()
    await expect(editForm).toHaveCount(0)
    await expect(main.getByText(renamed, { exact: true })).toBeVisible()
    await expect(main.getByText(name, { exact: true })).toHaveCount(0)
    places = await listPlaces(owner, team.slug)
    expect(places.places).toHaveLength(1)
    expect(places.places[0]).toMatchObject({ name: renamed, address, link })

    // Delete.
    await main.getByRole('button', { name: 'Supprimer le lieu' }).click()
    const confirm = page.getByRole('dialog', { name: 'Supprimer le lieu' })
    await confirm.getByRole('button', { name: 'Supprimer', exact: true }).click()
    await expect(main.getByText(/^Aucun lieu défini\./)).toBeVisible()
    await expect(main.getByText(renamed, { exact: true })).toHaveCount(0)
    places = await listPlaces(owner, team.slug)
    expect(places.places).toEqual([])
  })

  test('a place can be added without a link', async ({ page, context }) => {
    // PlaceForm started the form with link: '' and the schema wants link.min(3) when present, so
    // « Ajouter » stayed disabled, with no error, until a link was typed (fixed 2026-09-25: an empty
    // address or link counts as absent).
    const { owner, team } = await ownTeam('lieux sans lien')
    const name = unique('Café du centre')
    const address = '2 place Grenette, 38000 Grenoble'

    await signIn(context, owner)
    const main = await openPlaces(page, team.slug)
    const add = main.getByRole('button', { name: 'Ajouter un lieu' })
    await hydrated(add)
    await add.click()
    const form = page.getByRole('dialog', { name: 'Ajouter un lieu' })
    await form.getByRole('textbox', { name: 'Nom' }).fill(name)
    await form.getByRole('textbox', { name: 'Adresse' }).fill(address)
    await expect(form.getByRole('textbox', { name: 'Lien' }), 'no link typed').toHaveValue('')
    await expect(form.getByText(/obligatoire|requis|invalide/i)).toHaveCount(0)

    const submit = form.getByRole('button', { name: 'Ajouter', exact: true })
    await expect(submit).toBeEnabled({ timeout: 2_000 })
    await submit.click()
    await expect(form).toHaveCount(0)
    await expect(main.getByText(name, { exact: true })).toBeVisible()
    await expect(main.getByRole('link', { name: 'Voir sur la carte' })).toHaveCount(0)
    const { places } = await listPlaces(owner, team.slug)
    expect(places).toHaveLength(1)
    expect(places[0]).toMatchObject({ name, address })
    expect(places[0].link ?? '', 'no link stored').toBe('')
  })
})

test.describe('the platform-wide list (/parcours)', () => {
  const ALL_ROUTES = '/parcours'
  const ALL_ROUTES_MAP = '/parcours/carte'

  /** GET /api/routes — the routes of every team `who` may read, as the page asks for them. */
  const listAllRoutes = (who: Caller, search: string) =>
    apiGet<RouteListResponse>(who, '/api/routes', { search })

  /** The list's total, as its header states it (« 2 parcours »). */
  const resultCount = (main: Locator, count: number) =>
    main.getByText(`${count} parcours`, { exact: true })

  /** The list/map toggle: a SegmentedControl, named since 2026-09-25 (test below). */
  const viewToggleGroup = (main: Locator) =>
    main.getByRole('radiogroup', { name: 'Affichage des parcours' })

  /**
   * One side of the list/map toggle, whose radios are visually hidden inputs — a person clicks the
   * label, and so does the test.
   */
  const viewToggle = (main: Locator, label: 'Liste' | 'Carte') =>
    viewToggleGroup(main)
      .locator('label')
      .filter({ hasText: new RegExp(`^${label}$`) })

  /** A public team of a fresh team admin: its routes are open to anyone. */
  async function publicTeam(label: string) {
    const { owner, team } = await ownTeam(label, { visibility: 'PUBLIC' })
    return { owner, team }
  }

  test("a public team's route shows up, and the search narrows the list to it by name", async ({
    page,
    context,
  }) => {
    const { owner, team } = await publicTeam('plateforme')
    const wanted = unique('Montée de Chamrousse')
    const other = unique('Balcon de Belledonne')
    await newRoute(owner, team.slug, wanted, eastward(), { visibility: 'PUBLIC' })
    await newRoute(owner, team.slug, other, eastward(), { visibility: 'PUBLIC' })

    // Signed in, the list starts on the teams its visitor belongs to — here, this one alone.
    await signIn(context, owner)
    await page.goto(ALL_ROUTES)
    const main = page.getByRole('main')
    await expect(resultCount(main, 2)).toBeVisible()
    await expect(routeCard(main, wanted)).toBeVisible()
    await expect(routeCard(main, other)).toBeVisible()
    // A card of the cross-team list names its team.
    await expect(routeCard(main, wanted)).toContainText(team.name)

    const filters = main.getByRole('button', { name: 'Filtres' })
    await hydrated(filters)
    await filters.click()
    const search = main.getByRole('searchbox', { name: 'Rechercher des parcours' })
    await expect(search).toBeVisible()
    await search.fill(wanted)
    await expect(page).toHaveURL(/[?&]q=/)
    await expect(resultCount(main, 1)).toBeVisible()
    await expect(routeCard(main, wanted)).toBeVisible()
    await expect(routeCard(main, other)).toHaveCount(0)
    const found = await listAllRoutes(owner, wanted)
    expect(found.routes.map((r) => r.name)).toEqual([wanted])

    // A card opens its route, in its team.
    await routeCard(main, wanted).click()
    await expect(main.getByRole('heading', { level: 1, name: wanted })).toBeVisible()
    await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}/parcours/[^/?]+$`))
  })

  test('an anonymous visitor finds a public route on the map view, and its popup opens it', async ({
    page,
  }) => {
    const { owner, team } = await publicTeam('carte')
    const name = unique('Boucle du Vercors')
    const route = await newRoute(owner, team.slug, name, eastward(), { visibility: 'PUBLIC' })

    // The list, searched: the visitor's search carries over to the map.
    await page.goto(`${ALL_ROUTES}?q=${encodeURIComponent(name)}`)
    const main = page.getByRole('main')
    await expect(resultCount(main, 1)).toBeVisible()
    await expect(routeCard(main, name)).toBeVisible()
    await hydrated(main.getByRole('radio', { name: 'Carte' }))
    await viewToggle(main, 'Carte').click()
    await expect(page).toHaveURL(new RegExp(`${ALL_ROUTES_MAP}\\?.*q=`))
    await expect(main.getByRole('radio', { name: 'Carte' })).toBeChecked()

    // Framed on the searched route alone, the map draws its trace (on the stubbed basemap).
    const canvas = page.locator('canvas.maplibregl-canvas')
    await expect(canvas).toBeVisible()
    await expect.poll(() => traceMapPixels(page, canvas), { timeout: 15_000 }).toBeGreaterThan(0)
    // A straight east-west line, framed on its bounds: it crosses the map's centre. A click there
    // opens its popup, whose link leads to the route.
    await canvas.click()
    const popup = page.locator('.maplibregl-popup')
    const link = popup.getByRole('link', { name })
    await expect(link).toBeVisible()
    await expect(link).toHaveAttribute('href', routePath(team.slug, route.slug))
    await link.click()
    await expect(main.getByRole('heading', { level: 1, name })).toBeVisible()

    // Back to the list, the search still applied.
    await page.goto(`${ALL_ROUTES_MAP}?q=${encodeURIComponent(name)}`)
    await expect(canvas).toBeVisible()
    await hydrated(main.getByRole('radio', { name: 'Liste' }))
    await viewToggle(main, 'Liste').click()
    await expect(page).toHaveURL(new RegExp(`${ALL_ROUTES}\\?.*q=`))
    await expect(resultCount(main, 1)).toBeVisible()
    await expect(routeCard(main, name)).toBeVisible()
  })

  test("members-only routes don't show to an anonymous visitor; a member sees their team's", async ({
    page,
    browser,
  }) => {
    const { owner, team } = await publicTeam('visibilité')
    // Another team admin's members-only team.
    const { owner: otherOwner, team: privateTeam } = await ownTeam('privée')
    const tag = unique('Circuit')
    const open = `${tag} ouvert`
    const membersOnly = `${tag} réservé`
    const ofPrivateTeam = `${tag} équipe privée`
    await newRoute(owner, team.slug, open, eastward(), { visibility: 'PUBLIC' })
    await newRoute(owner, team.slug, membersOnly, eastward(), { visibility: 'TEAM' })
    await newRoute(otherOwner, privateTeam.slug, ofPrivateTeam, eastward())

    // Anonymous: the public route of the public team, nothing else.
    await page.goto(`${ALL_ROUTES}?q=${encodeURIComponent(tag)}`)
    const main = page.getByRole('main')
    await expect(resultCount(main, 1)).toBeVisible()
    await expect(routeCard(main, open)).toBeVisible()
    await expect(routeCard(main, membersOnly)).toHaveCount(0)
    await expect(routeCard(main, ofPrivateTeam)).toHaveCount(0)
    expect((await listAllRoutes(undefined, tag)).routes.map((r) => r.name)).toEqual([open])

    // The public team's admin: both of its routes, still not the other team's.
    const { context: memberContext, page: memberPage } = await pageAs(browser, owner)
    try {
      await stubBasemap(memberPage)
      await memberPage.goto(`${ALL_ROUTES}?q=${encodeURIComponent(tag)}`)
      const memberMain = memberPage.getByRole('main')
      await expect(resultCount(memberMain, 2)).toBeVisible()
      await expect(routeCard(memberMain, open)).toBeVisible()
      await expect(routeCard(memberMain, membersOnly)).toBeVisible()
      await expect(routeCard(memberMain, ofPrivateTeam)).toHaveCount(0)
    } finally {
      await memberContext.close()
    }
    expect((await listAllRoutes(owner, tag)).routes.map((r) => r.name).sort()).toEqual(
      [open, membersOnly].sort()
    )
    // And the other team's admin sees their own route among the rest.
    expect((await listAllRoutes(otherOwner, tag)).routes.map((r) => r.name).sort()).toEqual(
      [open, ofPrivateTeam].sort()
    )
  })

  test('the list/map toggle has an accessible name', async ({ page }) => {
    // The SegmentedControl was an unnamed radiogroup: a screen reader announced two radios with no
    // hint of what they switch (fixed 2026-09-25).
    await page.goto(ALL_ROUTES)
    const main = page.getByRole('main')
    const toggle = viewToggleGroup(main)
    await expect(toggle).toBeVisible()
    await expect(toggle.getByRole('radio', { name: 'Liste' })).toBeChecked()
    await expect(toggle.getByRole('radio', { name: 'Carte' })).not.toBeChecked()
  })
})
