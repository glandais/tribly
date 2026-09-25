import type { Locator, Page } from '@playwright/test'
import { signIn } from './support/data'
import { expect, test } from './support/fixtures'
import { configuredAuth, contractWebRoutes, fillPath } from './support/contract'
import { buildDataset, type Dataset, type RenderRole } from './support/routes-render'
import { pageHydrated, watchHydration } from './support/ui'

/**
 * Every web route of contracts/routes.yaml renders, for every role that may see it.
 *
 * One test per (route, role). Each one opens the route in a fresh context as that role and checks
 * that the document answers 200, the app lands where it should — the page itself, or the redirect
 * its guard is meant to do — that nothing threw (`pageerror`) or failed to hydrate (`[hydration]`),
 * that no error screen is shown, and that something only that screen has is visible.
 *
 * The guard redirects are derived from each route's `auth` in routes.config.ts: an `authenticated`
 * route sends an anonymous visitor to the login page, an `unauthenticated` one sends a signed-in
 * visitor home. Who may see a screen beyond that (a member, an organizer, an admin of the team, the
 * platform admin) is the table below.
 *
 * All the params come from one dataset per worker: a public team of its own with a member, an
 * organizer and an admin, and one entity of every kind a route names.
 */

type Main = Locator

interface Expectation {
  /** Where the app ends up, when not on the requested path. */
  lands?: (data: Dataset) => string
  /** What only that screen shows. `main` is the page's <main>, or the page for a bare layout. */
  sees: (main: Main, data: Dataset, page: Page) => Promise<void>
}

interface Screen extends Expectation {
  /** Who may see the screen. The guard's own redirects are added from routes.config.ts. */
  roles: readonly RenderRole[]
  /** Rendered outside the app shell (`layout: 'bare'`): no <main>. */
  bare?: boolean
  /** Per-role outcomes that differ from the screen itself — the page's own redirects. */
  otherwise?: Partial<Record<RenderRole, Expectation & { why: string }>>
}

const EVERYONE = ['anonymous', 'member', 'organizer', 'teamAdmin', 'platformAdmin'] as const
const SIGNED_IN = ['member', 'organizer', 'teamAdmin', 'platformAdmin'] as const
// The platform admin is not a member of the dataset's team, but the backend lets it read and
// manage every team (TeamAdminPage sends it to the ride templates like a team admin).
const MEMBERS = SIGNED_IN
const ORGANIZERS = ['organizer', 'teamAdmin', 'platformAdmin'] as const
const ADMINS = ['teamAdmin', 'platformAdmin'] as const

const heading = (name: string) => async (main: Main) => {
  await expect(main.getByRole('heading', { name, exact: true })).toBeVisible()
}

/** The current entry of a NavButtons row (`aria-current="page"`), by the row's landmark name. */
const currentTab = (page: Page, landmark: string, name: string) =>
  expect(
    page.getByRole('navigation', { name: landmark }).getByRole('link', { name, exact: true })
  ).toHaveAttribute('aria-current', 'page')

/** A fullscreen map page: its back link to the detail page, its title, and the map itself. */
const fullscreenMap =
  (title: (d: Dataset) => string, back: (d: Dataset) => string) =>
  async (page: Main, data: Dataset) => {
    await expect(page.getByRole('link', { name: 'Retour' })).toHaveAttribute('href', back(data))
    await expect(page.getByText(title(data), { exact: true })).toBeVisible()
    await expect(page.locator('canvas.maplibregl-canvas')).toBeVisible()
  }

const teamPath = (d: Dataset) => `/equipes/${d.team.slug}`

/** The same page-level redirect for each of `roles`. */
const redirects = (
  roles: readonly RenderRole[],
  outcome: Expectation & { why: string }
): Screen['otherwise'] => Object.fromEntries(roles.map((role) => [role, outcome]))

const screens: Record<string, Screen> = {
  home: { roles: EVERYONE, sees: heading('Dernières publications') },
  login: {
    roles: ['anonymous'],
    sees: (main) =>
      expect(main.getByRole('button', { name: 'Se connecter', exact: true })).toBeVisible(),
  },
  // The e-mail links land here with a token; without one, each page says the link is unusable.
  // Following a real link is the journeys' job (auth, invitations).
  verifyEmail: { roles: EVERYONE, sees: heading('Lien invalide') },
  invitation: { roles: EVERYONE, sees: heading('Invitation indisponible') },
  forgotPassword: { roles: ['anonymous'], sees: heading('Mot de passe oublié') },
  resetPassword: { roles: EVERYONE, sees: heading('Lien invalide') },
  stravaCallback: { roles: EVERYONE, sees: heading('Échec de la connexion Strava') },
  completeAccount: {
    roles: [],
    sees: async () => {},
    // Every account of the dataset already has a real address — the page only serves accounts
    // born without one (CompleteAccountPage's <Navigate> home).
    otherwise: redirects(SIGNED_IN, {
      why: 'an account with an e-mail has nothing to complete',
      lands: () => '/',
      sees: heading('Dernières publications'),
    }),
  },
  deviceVerifyGarmin: { roles: SIGNED_IN, sees: heading('Entrez le code') },
  deviceVerifyKaroo: { roles: SIGNED_IN, sees: heading('Entrez le code') },
  apps: { roles: EVERYONE, sees: heading('Applications') },
  // The legal pages repeat their title in their own markdown: two level-1 headings.
  privacy: {
    roles: EVERYONE,
    sees: (main) =>
      expect(
        main.getByRole('heading', { level: 1, name: 'Politique de confidentialité' })
      ).toHaveCount(2),
  },
  terms: {
    roles: EVERYONE,
    sees: (main) =>
      expect(main.getByRole('heading', { level: 1, name: "Conditions d'utilisation" })).toHaveCount(
        2
      ),
  },
  support: { roles: EVERYONE, sees: heading('Aide et contact') },
  profile: { roles: SIGNED_IN, sees: heading('Paramètres du profil') },
  notifications: { roles: SIGNED_IN, sees: heading('Notifications') },
  calendar: { roles: SIGNED_IN, sees: heading('Calendrier') },
  allRoutes: {
    roles: EVERYONE,
    sees: (main) => expect(main.getByRole('radio', { name: 'Liste' })).toBeChecked(),
  },
  allRoutesMap: {
    roles: EVERYONE,
    sees: async (main) => {
      await expect(main.getByRole('radio', { name: 'Carte' })).toBeChecked()
      await expect(main.locator('canvas.maplibregl-canvas')).toBeVisible()
    },
  },
  gpxTools: { roles: SIGNED_IN, sees: heading('Outils GPX') },
  gpxToolsList: { roles: SIGNED_IN, sees: heading('Lister mes fichiers') },
  gpxToolsNew: {
    roles: [],
    sees: async () => {},
    // The e2e domain keeps the planner closed (GET /api/config: enableGpxPlanner=false), and the
    // page sends the visitor back to the hub rather than render an editor that cannot submit.
    otherwise: redirects(SIGNED_IN, {
      why: 'the planner is closed on this domain',
      lands: () => '/outils-gpx',
      sees: heading('Outils GPX'),
    }),
  },
  gpxToolsView: { roles: EVERYONE, sees: (main, d) => heading(d.preview.name)(main) },
  gpxToolsMap: {
    roles: EVERYONE,
    bare: true,
    sees: fullscreenMap(
      (d) => d.preview.name,
      (d) => `/outils-gpx/${d.preview.id}`
    ),
  },
  gpxToolsEdit: {
    // The preview belongs to the member.
    roles: ['member'],
    sees: heading('Modifier le fichier GPX'),
    otherwise: redirects(ORGANIZERS, {
      why: 'editing a preview is owner-only: the page sends others to the read view',
      lands: (d) => `/outils-gpx/${d.preview.id}`,
      sees: (main, d) => heading(d.preview.name)(main),
    }),
  },
  teams: { roles: EVERYONE, sees: heading('Équipes') },
  teamsNew: { roles: SIGNED_IN, sees: heading('Créer une équipe') },
  team: {
    roles: EVERYONE,
    sees: async (main, d) => {
      await expect(main.getByRole('heading', { level: 1, name: d.team.name })).toBeVisible()
      await expect(main.getByRole('link', { name: d.ride.name }).first()).toBeVisible()
    },
  },
  teamAbout: { roles: EVERYONE, sees: heading("À propos de l'équipe") },
  teamCalendar: {
    roles: MEMBERS,
    sees: async (main, d) => {
      await expect(main.getByRole('heading', { level: 1, name: d.team.name })).toBeVisible()
      await heading('Calendrier')(main)
    },
  },
  teamPage: {
    roles: EVERYONE,
    sees: async (main, d) => {
      await heading(d.page.title)(main)
      await expect(main.getByText('Une page pour vérifier les écrans.')).toBeVisible()
    },
  },
  teamAdmin: {
    roles: [],
    sees: async () => {},
    // The admin area has no page of its own: TeamAdminPage opens its first tab.
    otherwise: redirects(ORGANIZERS, {
      why: 'the admin area opens on its ride templates tab',
      lands: (d) => `${teamPath(d)}/admin/modeles-sortie`,
      sees: (main, d, page) => rideTemplates(main, d, page),
    }),
  },
  teamAdminPlaces: {
    roles: ORGANIZERS,
    sees: async (main, d, page) => {
      await currentTab(page, "Navigation de la gestion de l'équipe", 'Lieux')
      await expect(main.getByText(d.place.name)).toBeVisible()
    },
  },
  teamAdminPages: {
    roles: ADMINS,
    sees: async (main, d, page) => {
      await currentTab(page, "Navigation de la gestion de l'équipe", 'Pages')
      await expect(main.getByText(d.page.title)).toBeVisible()
    },
  },
  teamAdminPageNew: { roles: ADMINS, sees: heading('Créer une page') },
  teamAdminPageEdit: {
    roles: ADMINS,
    sees: (main, d) => heading(`Modifier ${d.page.title}`)(main),
  },
  teamAdminMembers: {
    roles: ADMINS,
    sees: async (main, d, page) => {
      await currentTab(page, "Navigation de la gestion de l'équipe", 'Membres')
      await expect(main.getByText(d.sessions.member.user.displayName).first()).toBeVisible()
    },
  },
  teamAdminReports: {
    roles: ORGANIZERS,
    sees: async (main, _d, page) => {
      await currentTab(page, "Navigation de la gestion de l'équipe", 'Signalements')
      await heading('Aucun signalement en attente')(main)
    },
  },
  teamSettings: { roles: ADMINS, sees: heading("Paramètres de l'équipe") },
  rideNew: { roles: ORGANIZERS, sees: heading('Créer une sortie') },
  ride: { roles: EVERYONE, sees: (main, d) => heading(d.ride.name)(main) },
  rideEdit: { roles: ORGANIZERS, sees: heading('Modifier la sortie') },
  rideTemplates: { roles: ORGANIZERS, sees: (main, d, page) => rideTemplates(main, d, page) },
  rideTemplateNew: { roles: ORGANIZERS, sees: heading('Créer un modèle') },
  rideTemplateEdit: { roles: ORGANIZERS, sees: heading('Modifier le modèle') },
  tripNew: { roles: ORGANIZERS, sees: heading('Créer un voyage') },
  trip: { roles: EVERYONE, sees: (main, d) => heading(d.trip.name)(main) },
  tripEdit: { roles: ORGANIZERS, sees: heading('Modifier le voyage') },
  stage: {
    roles: EVERYONE,
    sees: async (main, d) => {
      await expect(main.getByRole('heading', { name: d.trip.stages[0].name }).first()).toBeVisible()
      await heading(d.route.name)(main)
    },
  },
  stageMap: {
    roles: EVERYONE,
    bare: true,
    sees: fullscreenMap(
      (d) => d.trip.stages[0].name,
      (d) => `${teamPath(d)}/voyages/${d.trip.slug}/etapes/${d.trip.stages[0].slug}`
    ),
  },
  postNew: { roles: ORGANIZERS, sees: heading('Nouvelle publication') },
  post: {
    roles: EVERYONE,
    sees: async (main, d) => {
      await heading(d.post.name)(main)
      await expect(main.getByText('Un article pour vérifier les écrans.')).toBeVisible()
    },
  },
  postEdit: { roles: ORGANIZERS, sees: heading('Modifier la publication') },
  routes: {
    roles: EVERYONE,
    sees: async (main, d) => {
      await expect(main.getByRole('radio', { name: 'Liste' })).toBeChecked()
      await expect(main.getByRole('link', { name: d.route.name }).first()).toBeVisible()
    },
  },
  routesMap: {
    roles: EVERYONE,
    sees: async (main) => {
      await expect(main.getByRole('radio', { name: 'Carte' })).toBeChecked()
      await expect(main.locator('canvas.maplibregl-canvas')).toBeVisible()
    },
  },
  routeNew: { roles: ORGANIZERS, sees: heading('Créer un nouveau parcours') },
  route: { roles: EVERYONE, sees: (main, d) => heading(d.route.name)(main) },
  routeMap: {
    roles: EVERYONE,
    bare: true,
    sees: fullscreenMap(
      (d) => d.route.name,
      (d) => `${teamPath(d)}/parcours/${d.route.slug}`
    ),
  },
  routeEdit: { roles: ORGANIZERS, sees: heading('Modifier le parcours') },
  ads: {
    roles: MEMBERS,
    sees: async (main, d) => {
      await heading('Annonces')(main)
      await expect(main.getByRole('link', { name: d.ad.name }).first()).toBeVisible()
    },
  },
  adNew: { roles: MEMBERS, sees: heading('Nouvelle annonce') },
  ad: { roles: MEMBERS, sees: (main, d) => heading(d.ad.name)(main) },
  // The ad belongs to the member; the team's admins may edit any ad.
  adEdit: { roles: ['member', ...ADMINS], sees: heading("Modifier l'annonce") },
  admin: { roles: ['platformAdmin'], sees: adminTab('Tableau de bord') },
  adminDomains: { roles: ['platformAdmin'], sees: adminTab('Domaines') },
  adminTeams: { roles: ['platformAdmin'], sees: adminTab('Équipes') },
  adminUsers: { roles: ['platformAdmin'], sees: adminTab('Utilisateurs') },
  adminBetaSignups: { roles: ['platformAdmin'], sees: adminTab('Inscriptions bêta') },
  adminReports: { roles: ['platformAdmin'], sees: adminTab('Signalements') },
}

async function rideTemplates(main: Main, d: Dataset, page: Page) {
  await currentTab(page, "Navigation de la gestion de l'équipe", 'Modèles de sortie')
  await expect(main.getByText(d.template.name)).toBeVisible()
}

function adminTab(name: string) {
  return async (main: Main, _d: Dataset, page: Page) => {
    await heading('Administration plateforme')(main)
    await currentTab(page, "Navigation de l'administration", name)
  }
}

const auth = configuredAuth()
const contract = contractWebRoutes()

let dataset: Promise<Dataset> | undefined
function data(): Promise<Dataset> {
  if (!dataset) {
    const building = buildDataset(`${process.pid}-${Date.now().toString(36)}`)
    dataset = building
    // A failed build is not cached: the next test tries again.
    building.catch(() => {
      if (dataset === building) dataset = undefined
    })
  }
  return dataset
}

test.beforeAll(async () => {
  test.setTimeout(120_000)
  await data()
})

test('the table covers every web route of the contract, and nothing else', () => {
  expect(Object.keys(screens).sort()).toEqual(contract.map((route) => route.id).sort())
  for (const route of contract)
    expect(auth.get(route.id), `routes.config.ts registers ${route.id}`).toBeDefined()
})

for (const route of contract) {
  const screen = screens[route.id]
  if (!screen) continue
  const requirement = auth.get(route.id)

  for (const role of EVERYONE) {
    let outcome: (Expectation & { why?: string }) | undefined
    if (requirement === 'authenticated' && role === 'anonymous')
      outcome = {
        why: 'authenticated route',
        lands: () => '/connexion',
        sees: (main) =>
          expect(main.getByRole('button', { name: 'Se connecter', exact: true })).toBeVisible(),
      }
    else if (requirement === 'unauthenticated' && role !== 'anonymous')
      outcome = {
        why: 'unauthenticated route',
        lands: () => '/',
        sees: heading('Dernières publications'),
      }
    else if (screen.otherwise?.[role]) outcome = screen.otherwise[role]
    else if (screen.roles.includes(role)) outcome = screen
    if (!outcome) continue
    const expected = outcome

    const title = expected.why
      ? `${route.id} ${route.path} as ${role}: ${expected.why}, redirects`
      : `${route.id} ${route.path} as ${role}: renders`

    test(title, async ({ page }) => {
      const d = await data()
      const path = fillPath(route, d.params)
      const where = `${route.id} as ${role} (${path})`

      const { pageErrors, hydrationErrors } = await watchHydration(page)
      if (role !== 'anonymous') await signIn(page.context(), d.sessions[role])

      const response = await page.goto(path)
      expect(response?.status(), `${where}: document status`).toBe(200)
      await pageHydrated(page)

      const lands = expected.lands?.(d) ?? path
      await expect
        .poll(() => new URL(page.url()).pathname, { message: `${where}: lands on ${lands}` })
        .toBe(lands)

      const main = screen.bare && !expected.lands ? page.locator('body') : page.getByRole('main')
      await expected.sees(main, d, page)

      // The page is up: now the absences mean something.
      for (const failure of [
        page.getByText('Une erreur est survenue'),
        page.getByText('Chargement impossible'),
        page.getByRole('heading', { name: 'Introuvable' }),
        page.getByRole('heading', { name: '404', exact: true }),
      ])
        await expect(failure, `${where}: no error screen`).toHaveCount(0)
      expect(pageErrors, `${where}: uncaught errors`).toEqual([])

      // The edit and create forms (a DateTimePicker hydrated in the SSR server's zone, a default
      // date computed in the process's zone), the platform dashboard's counts and the paginated
      // lists on a phone used to fail hydration (fixed 2026-09-25). The browser runs in
      // Europe/Paris and the SSR server in UTC, so a zone-dependent render shows up here.
      expect(hydrationErrors, `${where}: hydration errors`).toEqual([])
    })
  }
}
