import type { Locator, Page } from '@playwright/test'
import { ApiError, apiPost } from './support/api'
import { calendarEvent, openCalendar, teamEvents } from './support/calendar'
import { addMember, newTeam, newUser, roleSession, signIn } from './support/data'
import {
  frenchDateTime,
  openPicker,
  parisDaysAhead,
  parisWallClock,
  pickDateTime,
  twoDaysThisMonth,
} from './support/dates'
import { letEditorSettle, richText } from './support/editor'
import { expect, test, unique } from './support/fixtures'
import {
  fetchTrip,
  newRoute,
  newTrip,
  stagePath,
  stubBasemap,
  traceMapPixels,
  tripNewPath,
  tripPath,
  windingTrack,
} from './support/routes'
import { entityCard, hydrated, openActionsMenu, pageAs, startsWith, toasts } from './support/ui'

/**
 * Trips, the nominal journey through the web UI: a team admin creates a trip and its two stages
 * with the form (dates, a route on one of them), reads the trip page (stages in order, map), a
 * member registers and leaves, the team admin edits a stage, deletes another, then deletes the
 * trip. The team calendar lists the stages — never the trip itself, by design (docs/NEXT.md §1.1).
 *
 * Every test owns its team: a fresh user creates it — and so is its ADMIN, not a mere teamAdmin: a
 * deleted trip stays readable to them — and the platform admin adds the member (a team's own admins
 * may not, see data.ts addMember).
 */

async function tripTeam(label: string) {
  const teamAdmin = await newUser(unique('Admin voyages'))
  const team = await newTeam(teamAdmin, unique(`Voyages ${label}`))
  return { teamAdmin, team }
}

/** The stage cards of the trip page, in page order (each is a link to its stage). */
const stageCards = (main: Locator) => main.getByRole('link', { name: /^\d+ / })

/** A team of its own, with a member the platform admin added. */
async function tripTeamWithMember(label: string) {
  const { teamAdmin, team } = await tripTeam(label)
  const member = await newUser(unique('Membre'))
  await addMember(await roleSession('admin'), team.slug, member)
  return { teamAdmin, team, member }
}

/** The team feed, loaded — its publication count shown — before any presence or absence check. */
async function openFeed(page: Page, teamSlug: string) {
  await page.goto(`/equipes/${teamSlug}`)
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { name: "Fil d'actualités", level: 2 })).toBeVisible()
  await expect(main.getByText(/^\d+ publications?$/)).toBeVisible()
  return main
}

/** Opens the trip page and waits for its title and its stage list. */
async function openTrip(page: Page, teamSlug: string, tripSlug: string, name: string) {
  await page.goto(tripPath(teamSlug, tripSlug))
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { level: 2, name })).toBeVisible()
  await expect(main.getByRole('heading', { level: 3, name: 'Étapes' })).toBeVisible()
  return main
}

test('a team admin creates a trip and its two stages with the form; the calendar shows the stages', async ({
  page,
}) => {
  const { teamAdmin, team } = await tripTeam('création')
  // The route exists beforehand: picking it is the form's job, drawing it is not.
  const route = await newRoute(teamAdmin, team.slug, unique('Boucle beauceronne'), windingTrack())
  const tripName = unique('Voyage en Beauce')
  const first = { name: unique('Chartres – Bonneval'), when: twoDaysThisMonth()[0] }
  const second = { name: unique('Bonneval – Châteaudun'), when: twoDaysThisMonth()[1] }

  await stubBasemap(page)
  await signIn(page.context(), teamAdmin)
  await page.goto(tripNewPath(team.slug))
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { level: 1, name: 'Créer un voyage' })).toBeVisible()

  // Details tab.
  const title = main.getByRole('textbox', { name: 'Titre du voyage' })
  await hydrated(title)
  await title.fill(tripName)
  await main.getByRole('radio', { name: 'Publié' }).check()

  // Stage 1 — the form starts with one: its name, its date, and the route.
  await main.getByRole('tab', { name: /Étape 1$/ }).click()
  let panel = main.getByRole('tabpanel')
  await panel.getByRole('textbox', { name: "Nom de l'étape" }).fill(first.name)
  await pickDateTime(page, panel.getByRole('button', { name: 'Date/Heure' }), first.when)
  await panel.getByRole('button', { name: 'Sélectionner un parcours' }).click()
  const routePicker = page.getByRole('dialog', { name: "Sélectionner le parcours de l'étape" })
  await routePicker.getByRole('button', { name: startsWith(route.name) }).click()
  await expect(routePicker).toBeHidden()
  await expect(panel.getByText(route.name)).toBeVisible()
  await expect(panel.getByText('Aucun parcours sélectionné')).toHaveCount(0)

  // Stage 2 — added with its tab, no route.
  await main.getByRole('tab', { name: 'Ajouter une étape' }).click()
  await expect(main.getByRole('tab', { name: /^2 Étape 2$/, selected: true })).toBeVisible()
  panel = main.getByRole('tabpanel')
  await panel.getByRole('textbox', { name: "Nom de l'étape" }).fill(second.name)
  await pickDateTime(page, panel.getByRole('button', { name: 'Date/Heure' }), second.when)
  await expect(panel.getByText('Aucun parcours sélectionné')).toBeVisible()

  await main.getByRole('button', { name: 'Créer le voyage' }).click()
  await expect(toasts(page).filter({ hasText: 'Voyage créé avec succès' })).toBeVisible()
  await expect(page).toHaveURL(/\/voyages\/(?!nouveau)[^/]+$/)
  const tripSlug = new URL(page.url()).pathname.split('/').pop()!

  // The trip page: title, status, both stages in the order they were entered, and the map.
  await expect(main.getByRole('heading', { level: 2, name: tripName })).toBeVisible()
  await expect(main.getByText('Publié', { exact: true })).toBeVisible()
  await expect(main.getByText('2 étapes', { exact: true })).toBeVisible()
  const cards = stageCards(main)
  await expect(cards).toHaveCount(2)
  await expect(cards.nth(0)).toHaveAccessibleName(startsWith(`1 ${first.name}`))
  await expect(cards.nth(0)).toContainText(frenchDateTime(first.when))
  await expect(cards.nth(0).getByRole('button', { name: 'Voir le parcours' })).toBeVisible()
  await expect(cards.nth(1)).toHaveAccessibleName(startsWith(`2 ${second.name}`))
  await expect(cards.nth(1)).toContainText(frenchDateTime(second.when))
  await expect(cards.nth(1).getByRole('button', { name: 'Voir le parcours' })).toHaveCount(0)

  const canvas = page.locator('canvas.maplibregl-canvas')
  await expect(canvas, 'one map on the trip page').toHaveCount(1)
  await expect(canvas).toBeVisible()
  // The first stage's route is the only colour on the stubbed basemap.
  await expect
    .poll(() => traceMapPixels(page, canvas), {
      message: 'the map draws the stage route',
      timeout: 20_000,
    })
    .toBeGreaterThan(500)

  // What was saved is what was typed.
  const trip = await fetchTrip(teamAdmin, team.slug, tripSlug)
  expect(trip).not.toBeNull()
  expect(trip!.name).toBe(tripName)
  expect(trip!.status).toBe('PUBLISHED')
  expect(trip!.stages.map((s) => s.name)).toEqual([first.name, second.name])
  expect(parisWallClock(trip!.stages[0].dateTime)).toEqual(first.when)
  expect(parisWallClock(trip!.stages[1].dateTime)).toEqual(second.when)
  expect(trip!.stages[0].route?.slug).toBe(route.slug)
  expect(trip!.stages[1].route).toBeUndefined()

  // The calendar: one event per stage, and none for the trip.
  const month = parisWallClock(new Date())
  const events = await teamEvents(
    teamAdmin,
    team.slug,
    new Date(Date.UTC(month.year, month.month - 2, 1)),
    new Date(Date.UTC(month.year, month.month + 1, 1))
  )
  expect(events.map((e) => [e.type, e.title, e.tripSlug])).toEqual(
    expect.arrayContaining([
      ['TRIP_STAGE', first.name, tripSlug],
      ['TRIP_STAGE', second.name, tripSlug],
    ])
  )
  expect(events.filter((e) => e.title === tripName)).toEqual([])

  for (const stage of [second, first]) {
    await openCalendar(page, team.slug, stage.when)
    await expect(calendarEvent(page, stage.name)).toBeVisible()
    await expect(calendarEvent(page, tripName)).toHaveCount(0)
  }
  // A stage event leads to its stage.
  await calendarEvent(page, first.name).click()
  await expect(page).toHaveURL(new RegExp(`/voyages/${tripSlug}/etapes/${trip!.stages[0].slug}$`))
  // Its name heads both the page and the stage's own block.
  await expect(main.getByRole('heading', { level: 2, name: first.name }).first()).toBeVisible()
})

test.describe('registration', () => {
  test('a member registers to a trip from its page, then leaves it', async ({ page }) => {
    const { teamAdmin, team } = await tripTeam('inscription')
    const member = await newUser(unique('Voyageuse'))
    await addMember(await roleSession('admin'), team.slug, member)
    const trip = await newTrip(teamAdmin, team.slug, unique('Voyage à rejoindre'), [
      { name: unique('Étape unique') },
    ])

    await signIn(page.context(), member)
    let main = await openTrip(page, team.slug, trip.slug, trip.name)
    await expect(main.getByText('0 participant', { exact: true })).toBeVisible()
    await expect(main.getByRole('heading', { name: 'Participants' })).toHaveCount(0)
    // A member is no organizer: no way to edit.
    await expect(main.getByRole('link', { name: 'Modifier' })).toHaveCount(0)

    const join = main.getByRole('button', { name: 'Rejoindre le voyage' })
    await hydrated(join)
    await join.click()
    await expect(toasts(page).filter({ hasText: 'Inscription au voyage confirmée' })).toBeVisible()
    await expect(main.getByRole('button', { name: 'Quitter le voyage' })).toBeVisible()
    await expect(join).toHaveCount(0)
    await expect(main.getByText('1 participant', { exact: true }).first()).toBeVisible()
    await expect(main.getByRole('heading', { level: 3, name: 'Participants' })).toBeVisible()

    let saved = await fetchTrip(member, team.slug, trip.slug)
    expect(saved!.participants?.map((p) => p.id)).toEqual([member.user.id])

    // The server's state, not only the page's.
    main = await openTrip(page, team.slug, trip.slug, trip.name)
    const leave = main.getByRole('button', { name: 'Quitter le voyage' })
    await hydrated(leave)
    await leave.click()
    await expect(
      toasts(page).filter({ hasText: 'Désinscription du voyage confirmée' })
    ).toBeVisible()
    await expect(main.getByRole('button', { name: 'Rejoindre le voyage' })).toBeVisible()
    await expect(main.getByText('0 participant', { exact: true })).toBeVisible()
    await expect(main.getByRole('heading', { name: 'Participants' })).toHaveCount(0)

    saved = await fetchTrip(member, team.slug, trip.slug)
    expect(saved!.participants ?? []).toEqual([])
  })
})

test('a team admin edits a stage, deletes the other one, then deletes the trip', async ({
  page,
  browser,
}) => {
  const { teamAdmin, team } = await tripTeam('édition')
  const member = await newUser(unique('Membre'))
  await addMember(await roleSession('admin'), team.slug, member)
  const kept = unique('Étape gardée')
  const dropped = unique('Étape abandonnée')
  const trip = await newTrip(teamAdmin, team.slug, unique('Voyage à remanier'), [
    { name: kept },
    { name: dropped },
  ])
  const [keptStage, droppedStage] = trip.stages
  const renamed = unique('Étape renommée')
  const rescheduled = parisDaysAhead(12, 18, 45)

  await signIn(page.context(), teamAdmin)
  // For letEditorSettle(), before deleting a stage (see below).
  await page.clock.install()
  let main = await openTrip(page, team.slug, trip.slug, trip.name)
  await expect(stageCards(main)).toHaveCount(2)
  const edit = main.getByRole('link', { name: 'Modifier' })
  await hydrated(edit)
  await edit.click()
  await expect(main.getByRole('heading', { level: 1, name: 'Modifier le voyage' })).toBeVisible()

  // Edit the first stage: its name and its date.
  const firstTab = main.getByRole('tab', { name: startsWith(`1 ${kept}`) })
  await hydrated(firstTab)
  await firstTab.click()
  let panel = main.getByRole('tabpanel')
  const stageName = panel.getByRole('textbox', { name: "Nom de l'étape" })
  await expect(stageName).toHaveValue(kept)
  await stageName.fill(renamed)
  await pickDateTime(page, panel.getByRole('button', { name: 'Date/Heure' }), rescheduled)

  // Delete the second one.
  await main.getByRole('tab', { name: startsWith(`2 ${dropped}`) }).click()
  panel = main.getByRole('tabpanel')
  await expect(panel.getByRole('textbox', { name: "Nom de l'étape" })).toHaveValue(dropped)
  // Opening the tab queues an update in the stage's editor, and deleting the stage before it is
  // handed over crashes the form — pinned in « app defects » below. A person reads the stage
  // before deleting it; this is that pause.
  await letEditorSettle(page)
  await panel.getByRole('button', { name: 'Supprimer' }).click()
  await expect(main.getByRole('tab', { name: startsWith(`2 `) })).toHaveCount(0)
  await expect(main.getByRole('tab', { name: startsWith(`1 ${renamed}`) })).toBeVisible()

  await main.getByRole('button', { name: 'Enregistrer' }).click()
  await expect(toasts(page).filter({ hasText: 'Voyage mis à jour avec succès' })).toBeVisible()
  await expect(page).toHaveURL(new RegExp(`${tripPath(team.slug, trip.slug)}$`))
  await expect(main.getByRole('heading', { level: 2, name: trip.name })).toBeVisible()
  await expect(main.getByText('1 étape', { exact: true })).toBeVisible()
  const cards = stageCards(main)
  await expect(cards).toHaveCount(1)
  await expect(cards.first()).toHaveAccessibleName(startsWith(`1 ${renamed}`))
  await expect(cards.first()).toContainText(frenchDateTime(rescheduled))
  await expect(main.getByText(dropped)).toHaveCount(0)

  // The same stage was edited in place, the other one is gone.
  const saved = await fetchTrip(teamAdmin, team.slug, trip.slug)
  expect(saved!.stages.map((s) => [s.id, s.name])).toEqual([[keptStage.id, renamed]])
  expect(parisWallClock(saved!.stages[0].dateTime)).toEqual(rescheduled)
  await page.goto(stagePath(team.slug, trip.slug, droppedStage.slug))
  await expect(main.getByRole('heading', { name: 'Étape introuvable' })).toBeVisible()

  // Delete the trip, from its page's actions menu.
  main = await openTrip(page, team.slug, trip.slug, trip.name)
  const menu = await openActionsMenu(page)
  await menu.getByRole('menuitem', { name: 'Supprimer' }).click()
  const confirm = page.getByRole('dialog', { name: 'Supprimer' })
  await expect(confirm).toContainText('Êtes-vous sûr de vouloir supprimer ce voyage ?')
  await confirm.getByRole('button', { name: 'Supprimer' }).click()
  await expect(toasts(page).filter({ hasText: 'Voyage supprimé avec succès' })).toBeVisible()
  await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}$`))

  // Soft-deleted: gone for the members, still readable (and restorable) by the team's admins.
  expect(await fetchTrip(member, team.slug, trip.slug)).toBeNull()
  expect((await fetchTrip(teamAdmin, team.slug, trip.slug))?.deleted).toBe(true)
  const { context: memberContext, page: memberPage } = await pageAs(browser, member)
  try {
    await memberPage.goto(tripPath(team.slug, trip.slug))
    // The page retries the 404 three times before saying so (see the defect test below).
    await expect(
      memberPage.getByRole('main').getByRole('heading', { name: 'Voyage introuvable' })
    ).toBeVisible({ timeout: 15_000 })
  } finally {
    await memberContext.close()
  }
})

test.describe('publication states', () => {
  test('a trip created as a draft is hidden from members until it is published from its page', async ({
    page,
    browser,
  }) => {
    const { teamAdmin, team, member } = await tripTeamWithMember('brouillon')
    const tripName = unique('Voyage en brouillon')

    await signIn(page.context(), teamAdmin)
    await page.goto(tripNewPath(team.slug))
    const main = page.getByRole('main')
    await expect(main.getByRole('heading', { level: 1, name: 'Créer un voyage' })).toBeVisible()
    const title = main.getByRole('textbox', { name: 'Titre du voyage' })
    await hydrated(title)
    await title.fill(tripName)
    // A draft is the form's default.
    await expect(main.getByRole('radio', { name: 'Brouillon' })).toBeChecked()
    await main.getByRole('tab', { name: /Étape 1$/ }).click()
    await main
      .getByRole('tabpanel')
      .getByRole('textbox', { name: "Nom de l'étape" })
      .fill(unique('Étape'))
    await main.getByRole('button', { name: 'Créer le voyage' }).click()
    await expect(toasts(page).filter({ hasText: 'Voyage créé avec succès' })).toBeVisible()
    await expect(page).toHaveURL(/\/voyages\/(?!nouveau)[^/]+$/)
    const tripSlug = new URL(page.url()).pathname.split('/').pop()!
    await expect(main.getByRole('heading', { level: 2, name: tripName })).toBeVisible()
    await expect(main.getByText('Brouillon', { exact: true })).toBeVisible()
    expect((await fetchTrip(teamAdmin, team.slug, tripSlug))?.status).toBe('DRAFT')

    const reader = await pageAs(browser, member)
    try {
      // --- A draft: members see nothing of it.
      expect(await fetchTrip(member, team.slug, tripSlug), 'a draft is 404 to a member').toBeNull()
      let memberMain = await openFeed(reader.page, team.slug)
      await expect(memberMain.getByText('0 publication', { exact: true })).toBeVisible()
      await expect(entityCard(memberMain, tripName)).toHaveCount(0)

      // --- Published from the trip page's menu.
      const menu = await openActionsMenu(page)
      await menu.getByRole('menuitem', { name: 'Publier' }).click()
      await expect(toasts(page).filter({ hasText: 'Voyage publié avec succès' })).toBeVisible()
      await expect(main.getByText('Publié', { exact: true })).toBeVisible()
      await expect(main.getByText('Brouillon', { exact: true })).toHaveCount(0)
      expect((await fetchTrip(teamAdmin, team.slug, tripSlug))?.status).toBe('PUBLISHED')

      // --- The member finds it in the feed, and may register.
      memberMain = await openFeed(reader.page, team.slug)
      const card = entityCard(memberMain, tripName)
      await expect(card).toBeVisible()
      await hydrated(card)
      await card.click()
      await expect(reader.page).toHaveURL(new RegExp(`${tripPath(team.slug, tripSlug)}$`))
      await expect(memberMain.getByRole('heading', { level: 2, name: tripName })).toBeVisible()
      await expect(memberMain.getByRole('button', { name: 'Rejoindre le voyage' })).toBeVisible()
    } finally {
      await reader.context.close()
    }
  })

  test('a team admin unpublishes a trip: it goes back to draft, out of the members’ sight', async ({
    page,
    browser,
  }) => {
    const { teamAdmin, team, member } = await tripTeamWithMember('dépublication')
    const trip = await newTrip(teamAdmin, team.slug, unique('Voyage à dépublier'), [
      { name: unique('Étape') },
    ])

    const reader = await pageAs(browser, member)
    try {
      // Precondition: published, the member sees it.
      let memberMain = await openFeed(reader.page, team.slug)
      await expect(entityCard(memberMain, trip.name)).toBeVisible()

      await signIn(page.context(), teamAdmin)
      const main = await openTrip(page, team.slug, trip.slug, trip.name)
      await expect(main.getByText('Publié', { exact: true })).toBeVisible()
      const menu = await openActionsMenu(page)
      await menu.getByRole('menuitem', { name: 'Dépublier' }).click()
      const confirm = page.getByRole('dialog', { name: 'Dépublier' })
      await expect(confirm).toContainText(
        'Êtes-vous sûr de vouloir dépublier ce voyage ? Il reviendra au statut brouillon.'
      )
      await confirm.getByRole('button', { name: 'Dépublier' }).click()
      await expect(confirm).toBeHidden()
      await expect(toasts(page).filter({ hasText: 'Voyage dépublié avec succès' })).toBeVisible()
      await expect(main.getByText('Brouillon', { exact: true })).toBeVisible()
      // Back to draft, it can be published again.
      const again = await openActionsMenu(page)
      await expect(again.getByRole('menuitem', { name: 'Publier' })).toBeVisible()
      await page.keyboard.press('Escape')
      expect((await fetchTrip(teamAdmin, team.slug, trip.slug))?.status).toBe('DRAFT')

      // Gone for the member.
      expect(await fetchTrip(member, team.slug, trip.slug), 'a draft is 404 to a member').toBeNull()
      memberMain = await openFeed(reader.page, team.slug)
      await expect(memberMain.getByText('0 publication', { exact: true })).toBeVisible()
      await expect(entityCard(memberMain, trip.name)).toHaveCount(0)
    } finally {
      await reader.context.close()
    }
  })

  test('a team admin cancels a trip: it says « Annulé » and takes no more registrations', async ({
    page,
    browser,
  }) => {
    const { teamAdmin, team, member } = await tripTeamWithMember('annulation')
    const trip = await newTrip(teamAdmin, team.slug, unique('Voyage annulé'), [
      { name: unique('Étape') },
    ])

    await signIn(page.context(), teamAdmin)
    const main = await openTrip(page, team.slug, trip.slug, trip.name)
    await expect(main.getByText('Publié', { exact: true })).toBeVisible()
    const menu = await openActionsMenu(page)
    await menu.getByRole('menuitem', { name: 'Annuler le voyage' }).click()
    const confirm = page.getByRole('dialog', { name: 'Annuler le voyage' })
    await expect(confirm).toContainText('Êtes-vous sûr de vouloir annuler ce voyage ?')
    await confirm.getByRole('button', { name: 'Annuler le voyage' }).click()
    await expect(confirm).toBeHidden()
    await expect(toasts(page).filter({ hasText: 'Voyage annulé avec succès' })).toBeVisible()
    await expect(main.getByText('Annulé', { exact: true })).toBeVisible()
    expect((await fetchTrip(teamAdmin, team.slug, trip.slug))?.status).toBe('CANCELLED')

    // The member still finds it, marked cancelled, and cannot register any more.
    const reader = await pageAs(browser, member)
    try {
      const feed = await openFeed(reader.page, team.slug)
      const card = entityCard(feed, trip.name)
      await expect(card).toBeVisible()
      await expect(card.getByText('Annulé', { exact: true })).toBeVisible()

      const memberMain = await openTrip(reader.page, team.slug, trip.slug, trip.name)
      await expect(memberMain.getByText('Annulé', { exact: true })).toBeVisible()
      await expect(memberMain.getByText('0 participant', { exact: true })).toBeVisible()
      await expect(memberMain.getByRole('button', { name: 'Rejoindre le voyage' })).toHaveCount(0)
      // Nor through the API.
      const refused = await apiPost(
        member,
        `/api/teams/${team.slug}/trips/${trip.slug}/join`
      ).catch((error: unknown) => error)
      expect(refused).toBeInstanceOf(ApiError)
      expect((refused as ApiError).status).toBe(403)
      expect((await fetchTrip(member, team.slug, trip.slug))?.participants ?? []).toEqual([])
    } finally {
      await reader.context.close()
    }
  })
})

test.describe('regressions', () => {
  test('the stage date picker speaks French', async ({ page }) => {
    // There was no DatesProvider anywhere: every @mantine/dates picker spoke English — « September
    // 2026 », « Mo Tu We » — on the French site (fixed 2026-09-25: AppProviders passes the page's
    // language to DatesProvider).
    const { teamAdmin, team } = await tripTeam('sélecteur')
    await signIn(page.context(), teamAdmin)
    await page.goto(tripNewPath(team.slug))
    const main = page.getByRole('main')
    const title = main.getByRole('textbox', { name: 'Titre du voyage' })
    await hydrated(title)
    await main.getByRole('tab', { name: /Étape 1$/ }).click()
    const field = main.getByRole('tabpanel').getByRole('button', { name: 'Date/Heure' })
    const [, day, month, year] = /^(\d{2})\/(\d{2})\/(\d{4})/.exec((await field.textContent())!)!
    const dropdown = await openPicker(page, field)
    // Preconditions: the dropdown is open on the value's month, and the page itself is French.
    await expect(dropdown.getByRole('table')).toBeVisible()
    await expect(main.getByText('Date/Heure', { exact: true })).toBeVisible()
    const frenchMonth = new Intl.DateTimeFormat('fr-FR', { month: 'long', timeZone: 'UTC' }).format(
      new Date(Date.UTC(Number(year), Number(month) - 1, 15))
    )
    await expect(
      dropdown.getByRole('button', { name: `${Number(day)} ${frenchMonth} ${year}`, exact: true })
    ).toBeVisible({ timeout: 2_000 })
  })

  test("publishing a draft trip from its page keeps its stages' routes", async ({ page }) => {
    // TripDetailPage published by sending the TripDto back as the request ({ ...trip, status }): a
    // stage's `route` is not the request's `routeSlug`, so the update cleared every stage's route
    // (and start/end places) — unpublish and cancel too (fixed 2026-09-25).
    const { teamAdmin, team } = await tripTeam('publication')
    const route = await newRoute(teamAdmin, team.slug, unique('Boucle'), windingTrack(100))
    const trip = await newTrip(
      teamAdmin,
      team.slug,
      unique('Voyage à publier'),
      [{ name: unique('Étape tracée'), routeSlug: route.slug }],
      { status: 'DRAFT' }
    )
    await signIn(page.context(), teamAdmin)
    const main = await openTrip(page, team.slug, trip.slug, trip.name)
    // Preconditions: a draft whose stage has its route.
    await expect(main.getByText('Brouillon', { exact: true })).toBeVisible()
    await expect(
      stageCards(main).first().getByRole('button', { name: 'Voir le parcours' })
    ).toBeVisible()

    const menu = await openActionsMenu(page)
    await menu.getByRole('menuitem', { name: 'Publier' }).click()
    await expect(toasts(page).filter({ hasText: 'Voyage publié avec succès' })).toBeVisible()
    await expect(main.getByText('Publié', { exact: true })).toBeVisible()
    const published = await fetchTrip(teamAdmin, team.slug, trip.slug)
    expect(published?.status).toBe('PUBLISHED')
    expect(published?.stages[0].route?.slug, 'the stage keeps its route').toBe(route.slug)
  })

  test('a trip that is not there is not requested again and again', async ({ page }) => {
    // A 404 is an answer, not a failure: the query client used to retry it three times with a
    // 1 s / 2 s / 4 s backoff — ~8 s of skeleton and four reads before « introuvable » on every
    // detail page (fixed 2026-09-25).
    const { teamAdmin, team } = await tripTeam('absent')
    await signIn(page.context(), teamAdmin)
    const endpoint = `/api/teams/${team.slug}/trips/voyage-absent`
    const reads: string[] = []
    page.on('request', (request) => {
      if (new URL(request.url()).pathname === endpoint) reads.push(request.method())
    })
    await page.goto(tripPath(team.slug, 'voyage-absent'))
    // Precondition: the page does end on its not-found state.
    await expect(
      page.getByRole('main').getByRole('heading', { name: 'Voyage introuvable' })
    ).toBeVisible({
      timeout: 15_000,
    })
    // One read is enough.
    expect(reads.length, 'browser reads of the missing trip').toBeLessThanOrEqual(1)
  })

  test('deleting a stage right after opening it does not crash the form', async ({ page }) => {
    // Opening a stage's tab queues an update in that stage's editor. When MarkdownEditor flushed
    // its 150 ms debounce on unmount, deleting the stage within those 150 ms wrote
    // stages.<index>.media on an index the form no longer had: a TypeError, and the ErrorBoundary
    // in place of the form. The unmount cancels again; the blur still flushes (fixed 2026-09-25).
    const { teamAdmin, team } = await tripTeam('suppression étape')
    const kept = unique('Étape gardée')
    const dropped = unique('Étape abandonnée')
    const trip = await newTrip(teamAdmin, team.slug, unique('Voyage à élaguer'), [
      { name: kept },
      { name: dropped },
    ])
    await signIn(page.context(), teamAdmin)
    await page.clock.install()
    await page.goto(`${tripPath(team.slug, trip.slug)}/modifier`)
    const main = page.getByRole('main')
    await expect(main.getByRole('heading', { level: 1, name: 'Modifier le voyage' })).toBeVisible()
    const tab = main.getByRole('tab', { name: startsWith(`2 ${dropped}`) })
    await hydrated(tab)
    // The clock stands still from here: the delete lands inside the debounce whatever the
    // machine's speed.
    await page.clock.pauseAt(Date.now() + 60_000)
    await tab.click()
    const panel = main.getByRole('tabpanel')
    await expect(panel.getByRole('textbox', { name: "Nom de l'étape" })).toHaveValue(dropped)
    // Precondition: the stage's editor is mounted, and the stage can be deleted.
    await expect(richText(panel)).toBeVisible()
    await panel.getByRole('button', { name: 'Supprimer' }).click()
    await page.clock.resume()

    await expect(main.getByRole('tab', { name: startsWith(`1 ${kept}`) })).toBeVisible({
      timeout: 2_000,
    })
    await expect(main.getByRole('tab', { name: startsWith('2 ') })).toHaveCount(0)
    await expect(main.getByRole('heading', { name: 'Une erreur est survenue' })).toHaveCount(0)
  })
})
