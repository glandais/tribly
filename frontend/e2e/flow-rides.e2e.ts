import type { Locator, Page } from '@playwright/test'
import type { PlaceDetailDto, PlaceRequest, RideDto, RideRequest } from '../src/api/dto'
import { apiPost, apiPut } from './support/api'
import { calendarEvent, openCalendar } from './support/calendar'
import {
  frenchDateTime,
  monthName,
  openPicker,
  parisDaysAhead,
  parisInstant,
  parisWallClock,
  pickDateTime,
  pickerText,
  type WallClock,
} from './support/dates'
import { addMember, newTeam, newUser, roleSession, signIn } from './support/data'
import { richText } from './support/editor'
import { expect, test, unique } from './support/fixtures'
import {
  findRide,
  groupCard,
  newRide,
  readComments,
  readRide,
  readTemplates,
  ridePath,
} from './support/rides'
import { newRoute, windingTrack } from './support/routes'
import { actionsMenu, entityCard, hydrated, openActionsMenu, pageAs, toasts } from './support/ui'

/**
 * The ride journey through the UI only, as a team's organizer and one of its members: the ride
 * editor (create, publish, edit), the team's feed and calendar, registration and a comment from the
 * ride page, then cancellation and deletion — and ride templates, created in their own editor and
 * loaded into a new ride.
 *
 * What rides.e2e.ts already pins on the ride page (comment paging, the leader badge, a full group,
 * GROUP_FULL) is not repeated here. Every test builds its own team, owned by the platform admin (only
 * a platform admin may add members directly), with its own organizer and member.
 */

async function ridingTeam(label: string) {
  const owner = await roleSession('admin')
  const team = await newTeam(owner, unique(`Flow sorties ${label}`))
  const organizer = await newUser(unique('Organisatrice'))
  const member = await newUser(unique('Membre'))
  await addMember(owner, team.slug, organizer, 'ORGANIZER')
  await addMember(owner, team.slug, member)
  return { team, organizer, member }
}

const main = (page: Page) => page.getByRole('main')

/** The team feed, loaded: its heading is there before any presence or absence check. */
async function openFeed(page: Page, teamSlug: string) {
  await page.goto(`/equipes/${teamSlug}`)
  await expect(
    main(page).getByRole('heading', { name: "Fil d'actualités", level: 2 })
  ).toBeVisible()
  await expect(main(page).getByText(/^\d+ publications?$/)).toBeVisible()
}

const feedCard = (page: Page, name: string) => entityCard(main(page), name)

/**
 * Sets an empty DateTimePicker (`field`: its button) to `when` — dates.ts pickDateTime pages from
 * the value the field shows, and an unset « Publication programmée » shows none: its dropdown opens
 * on today's month.
 */
async function pickIntoEmptyPicker(page: Page, field: Locator, when: WallClock) {
  await expect(field, 'the picker starts empty').toHaveText('')
  const today = parisWallClock(new Date())
  const months = when.year * 12 + when.month - (today.year * 12 + today.month)
  const dropdown = await openPicker(page, field)
  for (let i = 0; i < months; i++) await dropdown.locator('button[data-direction="next"]').click()
  const names = ['fr-FR', 'en-US'].map((locale) => monthName(when.month, locale)).join('|')
  await dropdown
    .getByRole('button', { name: new RegExp(`^${when.day} (${names}) ${when.year}$`, 'i') })
    .click()
  const spinbuttons = dropdown.getByRole('spinbutton')
  await expect(spinbuttons).toHaveCount(2)
  await spinbuttons.nth(0).fill(String(when.hour).padStart(2, '0'))
  await spinbuttons.nth(1).fill(String(when.minute).padStart(2, '0'))
  await spinbuttons.nth(1).press('Enter')
  await expect(dropdown).toBeHidden()
  await expect(field).toHaveText(pickerText(when))
}

/** The request that saves `ride` back as it is, but for `changes` — what the ride editor sends. */
const rideAsRequest = (ride: RideDto, changes: Partial<RideRequest>): RideRequest => ({
  name: ride.name,
  media: ride.media,
  dateTime: ride.dateTime,
  status: ride.status,
  visibility: ride.visibility,
  routeSlug: ride.routeSlug,
  publishAt: ride.publishAt,
  groups: ride.groups.map((g) => ({
    id: g.id,
    name: g.name,
    maxParticipants: g.maxParticipants,
    leaderId: g.leader?.id,
  })),
  ...changes,
})

test.describe('ride journey', () => {
  test('an organizer creates, publishes and edits a ride; a member joins and comments; the organizer deletes it', async ({
    page,
    browser,
  }) => {
    test.slow()
    const { team, organizer, member } = await ridingTeam('parcours')
    const routeName = unique('Boucle de la Beauce')
    const route = await newRoute(organizer, team.slug, routeName, windingTrack(200))
    const rideName = unique('Sortie du jeudi')
    const description = 'Café à mi-parcours, retour avant midi.'
    const fast = unique('Groupe rapide')
    const easy = unique('Groupe tranquille')
    const day = parisDaysAhead(3, 18, 30)

    // --- Create, from the team feed's « Créer une sortie ».
    await signIn(page.context(), organizer)
    await openFeed(page, team.slug)
    const create = main(page).getByRole('link', { name: 'Créer une sortie' })
    await hydrated(create)
    await create.click()
    await expect(
      main(page).getByRole('heading', { name: 'Créer une sortie', level: 1 })
    ).toBeVisible()

    const title = main(page).getByRole('textbox', { name: 'Titre de la sortie' })
    await hydrated(title)
    await title.fill(rideName)
    await richText(main(page)).fill(description)
    await pickDateTime(page, main(page).getByRole('button', { name: 'Départ' }), day)

    // The ride's route, among the team's routes (the first « Sélectionner un parcours » is the
    // ride's; the others belong to the groups).
    await main(page).getByRole('button', { name: 'Sélectionner un parcours' }).first().click()
    const picker = page.getByRole('dialog', { name: 'Sélectionner un parcours pour la sortie' })
    await picker.getByRole('button').filter({ hasText: routeName }).click()
    await expect(picker).toBeHidden()
    await expect(main(page).getByText('Aucun parcours sélectionné')).toHaveCount(1) // the group's

    // Two groups, each with a capacity.
    const groupNames = main(page).getByRole('textbox', { name: 'Nom du groupe' })
    const capacities = main(page).getByRole('textbox', { name: 'Taille max' })
    await groupNames.first().fill(fast)
    await capacities.first().fill('8')
    await main(page).getByRole('button', { name: 'Ajouter un groupe' }).click()
    await expect(groupNames).toHaveCount(2)
    await groupNames.nth(1).fill(easy)
    await capacities.nth(1).fill('12')

    // Created as a draft (the editor's default), published from the ride page afterwards.
    await expect(main(page).getByRole('radio', { name: 'Brouillon' })).toBeChecked()
    await main(page).getByRole('button', { name: 'Créer la sortie' }).click()
    await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}/sorties/(?!nouvelle)[^/]+$`))
    const rideSlug = new URL(page.url()).pathname.split('/').pop()!
    await expect(main(page).getByRole('heading', { name: rideName, level: 2 })).toBeVisible()
    await expect(main(page).getByText('Brouillon', { exact: true })).toBeVisible()

    let saved = await readRide(organizer, team.slug, rideSlug)
    expect(saved.name).toBe(rideName)
    expect(saved.media.markdown.trim()).toBe(description)
    expect(parisWallClock(saved.dateTime)).toEqual(day)
    expect(saved.routeSlug).toBe(route.slug)
    expect(saved.status).toBe('DRAFT')
    expect(saved.groups.map((g) => [g.name, g.maxParticipants])).toEqual([
      [fast, 8],
      [easy, 12],
    ])

    // A draft is not in the member's feed yet.
    const { context: memberContext, page: memberPage } = await pageAs(browser, member)
    try {
      await openFeed(memberPage, team.slug)
      await expect(main(memberPage).getByText('0 publication', { exact: true })).toBeVisible()
      await expect(feedCard(memberPage, rideName)).toHaveCount(0)
      expect(await findRide(member, team.slug, rideSlug), 'a draft is 404 to a member').toBeNull()

      // --- Publish.
      const menu = await openActionsMenu(page)
      await menu.getByRole('menuitem', { name: 'Publier' }).click()
      await expect(main(page).getByText('Publié', { exact: true })).toBeVisible()
      expect((await readRide(organizer, team.slug, rideSlug)).status).toBe('PUBLISHED')

      // --- In the team's feed and its calendar, for the member.
      await openFeed(memberPage, team.slug)
      await expect(feedCard(memberPage, rideName)).toBeVisible()
      await openCalendar(memberPage, team.slug, day)
      await expect(calendarEvent(memberPage, rideName)).toBeVisible()

      // --- Edit: the easy group becomes smaller and changes its name.
      const easier = unique('Groupe découverte')
      const edit = main(page).getByRole('link', { name: 'Modifier' })
      await hydrated(edit)
      await edit.click()
      await expect(
        main(page).getByRole('heading', { name: 'Modifier la sortie', level: 1 })
      ).toBeVisible()
      const editNames = main(page).getByRole('textbox', { name: 'Nom du groupe' })
      await expect(editNames.nth(1)).toHaveValue(easy)
      await hydrated(editNames.nth(1))
      await editNames.nth(1).fill(easier)
      await main(page).getByRole('textbox', { name: 'Taille max' }).nth(1).fill('4')
      await main(page).getByRole('button', { name: 'Enregistrer' }).click()
      await expect(page).toHaveURL(new RegExp(`/sorties/${rideSlug}$`))
      await expect(groupCard(page, easier)).toBeVisible()
      await expect(groupCard(page, easier).getByText('0/4 participants')).toBeVisible()
      await expect(main(page).getByText(easy, { exact: true })).toHaveCount(0)

      saved = await readRide(organizer, team.slug, rideSlug)
      expect(saved.groups.map((g) => [g.name, g.maxParticipants])).toEqual([
        [fast, 8],
        [easier, 4],
      ])
      // Editing kept what it did not touch.
      expect(saved.status).toBe('PUBLISHED')
      expect(saved.routeSlug).toBe(route.slug)
      expect(parisWallClock(saved.dateTime)).toEqual(day)

      // --- The member opens it from the calendar, joins a group and comments.
      await calendarEvent(memberPage, rideName).click()
      await expect(memberPage).toHaveURL(new RegExp(`/sorties/${rideSlug}$`))
      await expect(
        main(memberPage).getByRole('heading', { name: rideName, level: 2 })
      ).toBeVisible()
      const card = groupCard(memberPage, easier)
      await expect(card.getByText('0/4 participants')).toBeVisible()
      const join = card.getByRole('button', { name: 'Rejoindre' })
      await hydrated(join)
      await join.click()
      await expect(card.getByText('Inscrit', { exact: true })).toBeVisible()
      await expect(card.getByText('1/4 participants')).toBeVisible()

      const comment = `On se retrouve au parking ${unique('msg')}`
      const commentBox = main(memberPage).getByRole('textbox', { name: /Écrivez un commentaire/ })
      await commentBox.fill(comment)
      await main(memberPage).getByRole('button', { name: 'Envoyer le commentaire' }).click()
      await expect(main(memberPage).getByText(comment, { exact: true })).toBeVisible()
      await expect(
        main(memberPage).getByRole('heading', { name: 'Commentaires (1)' })
      ).toBeVisible()

      const asMember = await readRide(member, team.slug, rideSlug)
      expect(asMember.registered).toBe(true)
      expect(asMember.registeredGroupId).toBe(saved.groups[1].id)
      const comments = await readComments(organizer, team.slug, rideSlug)
      expect(comments.items.map((c) => c.content)).toEqual([comment])

      // The organizer sees both on their page.
      await page.reload()
      await expect(groupCard(page, easier).getByText('1/4 participants')).toBeVisible()
      await expect(main(page).getByText(comment, { exact: true })).toBeVisible()

      // --- Delete: back on the feed, and gone from every list.
      const actions = await openActionsMenu(page)
      await actions.getByRole('menuitem', { name: 'Supprimer' }).click()
      const confirm = page.getByRole('dialog', { name: 'Supprimer' })
      await expect(
        confirm.getByText('Êtes-vous sûr de vouloir supprimer cette sortie ?')
      ).toBeVisible()
      await confirm.getByRole('button', { name: 'Supprimer' }).click()
      await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}$`))
      await expect(
        main(page).getByRole('heading', { name: "Fil d'actualités", level: 2 })
      ).toBeVisible()
      // An organizer is not a team admin: deleted rides are not listed back to them.
      await expect(main(page).getByText('0 publication', { exact: true })).toBeVisible()
      await expect(feedCard(page, rideName)).toHaveCount(0)

      expect(await findRide(member, team.slug, rideSlug), 'the API answers 404').toBeNull()
      await openFeed(memberPage, team.slug)
      await expect(main(memberPage).getByText('0 publication', { exact: true })).toBeVisible()
      await expect(feedCard(memberPage, rideName)).toHaveCount(0)
      // The calendar's events come with the page, prefetched by the server: another ride of the
      // same day, shown, is what says they are in (see support/calendar.ts).
      const anchor = await newRide(organizer, team.slug, unique('Sortie repère'), {
        dateTime: parisInstant({ ...day, hour: 10, minute: 0 }),
      })
      await openCalendar(memberPage, team.slug, day)
      await expect(calendarEvent(memberPage, anchor.name)).toBeVisible()
      await expect(calendarEvent(memberPage, rideName)).toHaveCount(0)
      // Its own page says it is gone — see 'a deleted ride's page says so at once' for how long
      // that takes.
      await memberPage.goto(`/equipes/${team.slug}/sorties/${rideSlug}`)
      await expect(main(memberPage).getByText('Sortie non trouvée')).toBeVisible({
        timeout: 15_000,
      })
    } finally {
      await memberContext.close()
    }
  })

  test('an organizer cancels a published ride: it says « Annulé » and takes no more registrations', async ({
    page,
    browser,
  }) => {
    const { team, organizer, member } = await ridingTeam('annulation')
    const rideName = unique('Sortie annulée')
    const group = unique('Groupe du matin')

    await signIn(page.context(), organizer)
    await page.goto(`/equipes/${team.slug}/sorties/nouvelle`)
    await expect(
      main(page).getByRole('heading', { name: 'Créer une sortie', level: 1 })
    ).toBeVisible()
    const title = main(page).getByRole('textbox', { name: 'Titre de la sortie' })
    await hydrated(title)
    await title.fill(rideName)
    await main(page).getByRole('textbox', { name: 'Nom du groupe' }).fill(group)
    // Published straight from the editor this time.
    await main(page).getByRole('radio', { name: 'Publié' }).check()
    await main(page).getByRole('button', { name: 'Créer la sortie' }).click()
    await expect(main(page).getByRole('heading', { name: rideName, level: 2 })).toBeVisible()
    await expect(main(page).getByText('Publié', { exact: true })).toBeVisible()
    const rideSlug = new URL(page.url()).pathname.split('/').pop()!

    const menu = await openActionsMenu(page)
    await menu.getByRole('menuitem', { name: 'Annuler la sortie' }).click()
    const confirm = page.getByRole('dialog', { name: 'Annuler la sortie' })
    await expect(confirm.getByText('Êtes-vous sûr de vouloir annuler cette sortie ?')).toBeVisible()
    await confirm.getByRole('button', { name: 'Annuler la sortie' }).click()
    await expect(confirm).toBeHidden()
    await expect(main(page).getByText('Annulé', { exact: true })).toBeVisible()
    expect((await readRide(organizer, team.slug, rideSlug)).status).toBe('CANCELLED')

    // The member still finds it, marked cancelled, and cannot register any more.
    const { context, page: memberPage } = await pageAs(browser, member)
    try {
      await openFeed(memberPage, team.slug)
      const card = feedCard(memberPage, rideName)
      await expect(card).toBeVisible()
      await expect(card.getByText('Annulé', { exact: true })).toBeVisible()
      await card.click()
      await expect(
        main(memberPage).getByRole('heading', { name: rideName, level: 2 })
      ).toBeVisible()
      await expect(groupCard(memberPage, group)).toBeVisible()
      await expect(main(memberPage).getByText('Annulé', { exact: true })).toBeVisible()
      await expect(
        groupCard(memberPage, group).getByRole('button', { name: 'Rejoindre' })
      ).toHaveCount(0)
    } finally {
      await context.close()
    }
  })
})

test.describe('publication states', () => {
  test('a ride with a scheduled publication: a draft that says when, hidden from members until the scheduler publishes it', async ({
    page,
    browser,
  }) => {
    // The scheduler (PublicationPublishScheduler) runs once a minute: the last step waits for it.
    test.setTimeout(180_000)
    const { team, organizer, member } = await ridingTeam('programmée')
    const rideName = unique('Sortie programmée')
    const day = parisDaysAhead(4, 9, 0)
    const publishOn = parisDaysAhead(1, 7, 15)

    await signIn(page.context(), organizer)
    await page.goto(`/equipes/${team.slug}/sorties/nouvelle`)
    await expect(
      main(page).getByRole('heading', { name: 'Créer une sortie', level: 1 })
    ).toBeVisible()
    const title = main(page).getByRole('textbox', { name: 'Titre de la sortie' })
    await hydrated(title)
    await title.fill(rideName)
    await pickDateTime(page, main(page).getByRole('button', { name: 'Départ' }), day)
    await main(page).getByRole('textbox', { name: 'Nom du groupe' }).fill(unique('Groupe'))
    // A draft (the editor's default) is what offers a scheduled publication.
    await expect(main(page).getByRole('radio', { name: 'Brouillon' })).toBeChecked()
    await pickIntoEmptyPicker(
      page,
      main(page).getByRole('button', { name: 'Publication programmée' }),
      publishOn
    )
    await main(page).getByRole('button', { name: 'Créer la sortie' }).click()
    await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}/sorties/(?!nouvelle)[^/]+$`))
    const rideSlug = new URL(page.url()).pathname.split('/').pop()!

    // --- Before the date: the page says it is a draft, and when it will be published.
    await expect(main(page).getByRole('heading', { name: rideName, level: 2 })).toBeVisible()
    await expect(main(page).getByText('Brouillon', { exact: true })).toBeVisible()
    await expect(
      main(page).getByText(`Publication programmée pour le ${frenchDateTime(publishOn)}`, {
        exact: true,
      })
    ).toBeVisible()
    const saved = await readRide(organizer, team.slug, rideSlug)
    expect(saved.status).toBe('DRAFT')
    expect(new Date(saved.publishAt!).toISOString()).toBe(parisInstant(publishOn))
    expect(parisWallClock(saved.dateTime)).toEqual(day)

    const { context: memberContext, page: memberPage } = await pageAs(browser, member)
    try {
      // Members see nothing of it yet.
      expect(await findRide(member, team.slug, rideSlug), 'a draft is 404 to a member').toBeNull()
      await openFeed(memberPage, team.slug)
      await expect(main(memberPage).getByText('0 publication', { exact: true })).toBeVisible()
      await expect(feedCard(memberPage, rideName)).toHaveCount(0)

      // --- The date comes. The stack's clock cannot be moved, but the backend takes a scheduled
      // date already past as it is, and its scheduler then publishes the ride on its next run —
      // exactly what happens when the date is reached.
      await apiPut(
        organizer,
        `/api/teams/${team.slug}/rides/${rideSlug}`,
        rideAsRequest(saved, { publishAt: new Date(Date.now() - 60_000).toISOString() })
      )
      await expect
        .poll(async () => (await readRide(organizer, team.slug, rideSlug)).status, {
          message: 'the scheduler publishes the ride',
          timeout: 100_000,
          intervals: [2_000],
        })
        .toBe('PUBLISHED')
      const published = await readRide(organizer, team.slug, rideSlug)
      expect(published.publishAt, 'the schedule is spent').toBeUndefined()
      expect(parisWallClock(published.dateTime), 'a ride keeps its own date').toEqual(day)

      await page.reload()
      await expect(main(page).getByRole('heading', { name: rideName, level: 2 })).toBeVisible()
      await expect(main(page).getByText('Publié', { exact: true })).toBeVisible()
      await expect(main(page).getByText(/^Publication programmée/)).toHaveCount(0)

      await openFeed(memberPage, team.slug)
      await expect(feedCard(memberPage, rideName)).toBeVisible()
      expect((await readRide(member, team.slug, rideSlug)).status).toBe('PUBLISHED')
    } finally {
      await memberContext.close()
    }
  })
})

test.describe('regressions', () => {
  test("the actions menus' chevrons have a name", async ({ page }) => {
    // The chevron opening a detail page's other actions was an icon-only Button with no
    // aria-label — on the ride, trip, post and ad pages — and so was the feed's create menu
    // (fixed 2026-09-25: « Plus d'actions », « Créer autre chose »). The ride page stands for the
    // others here; support/ui.ts actionsMenu finds the chevron by that name, so flow-trips,
    // flow-posts and flow-ads check it on theirs.
    const { team, organizer } = await ridingTeam('menu')
    const ride = await newRide(organizer, team.slug, unique('Sortie au menu'))
    await signIn(page.context(), organizer)
    await page.goto(`/equipes/${team.slug}/sorties/${ride.slug}`)
    await expect(main(page).getByRole('heading', { name: ride.name, level: 2 })).toBeVisible()
    await expect(actionsMenu(page)).toHaveAccessibleName("Plus d'actions")
    const menu = await openActionsMenu(page)
    await expect(menu.getByRole('menuitem', { name: 'Annuler la sortie' })).toBeVisible()
    await page.keyboard.press('Escape')

    // The feed's create button, whose chevron offers the other kinds of publication.
    await openFeed(page, team.slug)
    await expect(main(page).getByRole('link', { name: 'Créer une sortie' })).toBeVisible()
    const createOther = main(page).getByRole('button', { name: 'Créer autre chose', exact: true })
    await hydrated(createOther)
    await createOther.click()
    await expect(
      page.getByRole('menu').getByRole('menuitem', { name: 'Nouvelle publication' })
    ).toBeVisible()
  })

  test("publishing a draft ride from its page keeps its groups' leaders and its start place", async ({
    page,
  }) => {
    // RideDetailPage published by sending the RideDto back as the request ({ ...ride, status }): a
    // group's `leader` is not the request's `leaderId`, nor `startPlace` its `startPlaceId`, so
    // the update cleared them (fixed 2026-09-25).
    const { team, organizer } = await ridingTeam('meneur')
    const led = unique('Groupe mené')
    const place = await apiPost<PlaceDetailDto>(
      await roleSession('admin'),
      `/api/teams/${team.slug}/places`,
      {
        name: unique('Parvis de la cathédrale'),
        address: '1 place des Halles, Chartres',
        startPlace: true,
        endPlace: false,
        geometry: { type: 'Point', coordinates: [1.4875, 48.4469] },
      } satisfies PlaceRequest
    )
    const ride = await newRide(organizer, team.slug, unique('Sortie menée'), {
      status: 'DRAFT',
      startPlaceId: place.id,
      groups: [{ name: led, leaderId: organizer.user.id }],
    })
    expect(ride.startPlace?.id, 'precondition: the ride starts from the place').toBe(place.id)
    await signIn(page.context(), organizer)
    await page.goto(ridePath(team.slug, ride.slug))
    await expect(main(page).getByRole('heading', { name: ride.name, level: 2 })).toBeVisible()
    // Preconditions: a draft whose group shows its leader.
    await expect(main(page).getByText('Brouillon', { exact: true })).toBeVisible()
    await expect(
      groupCard(page, led).getByText(organizer.user.displayName, { exact: true })
    ).toBeVisible()

    const menu = await openActionsMenu(page)
    await menu.getByRole('menuitem', { name: 'Publier' }).click()
    await expect(toasts(page).filter({ hasText: 'Sortie publiée avec succès' })).toBeVisible()
    await expect(main(page).getByText('Publié', { exact: true })).toBeVisible()
    const published = await readRide(organizer, team.slug, ride.slug)
    expect(published.status).toBe('PUBLISHED')
    expect(published.groups[0].leader?.id, 'the group keeps its leader').toBe(organizer.user.id)
    expect(published.startPlace?.id, 'the ride keeps its start place').toBe(place.id)
    await expect(
      groupCard(page, led).getByText(organizer.user.displayName, { exact: true })
    ).toBeVisible()
  })
})

test.describe('ride templates', () => {
  test('an organizer creates a template, then a ride from it', async ({ page }) => {
    const { team, organizer } = await ridingTeam('modèles')
    const templateName = unique('Modèle du dimanche')
    const description = 'Départ du local, arrêt boulangerie.'
    const first = unique('Groupe A')
    const second = unique('Groupe B')

    await signIn(page.context(), organizer)
    await page.goto(`/equipes/${team.slug}/admin/modeles-sortie`)
    await expect(
      main(page).getByRole('heading', { name: 'Modèles de sortie', level: 2 })
    ).toBeVisible()
    await expect(main(page).getByText('Aucun modèle', { exact: true })).toBeVisible()
    const create = main(page).getByRole('link', { name: 'Créer un modèle' }).first()
    await hydrated(create)
    await create.click()
    await expect(
      main(page).getByRole('heading', { name: 'Créer un modèle', level: 1 })
    ).toBeVisible()

    const name = main(page).getByRole('textbox', { name: 'Nom du modèle' })
    await hydrated(name)
    await name.fill(templateName)
    await richText(main(page)).fill(description)
    const groupNames = main(page).getByRole('textbox', { name: 'Nom du groupe' })
    const capacities = main(page).getByRole('textbox', { name: 'Taille max' })
    await groupNames.first().fill(first)
    await capacities.first().fill('6')
    await main(page).getByRole('button', { name: 'Ajouter un groupe' }).click()
    await expect(groupNames).toHaveCount(2)
    await groupNames.nth(1).fill(second)
    await capacities.nth(1).fill('10')
    await main(page).getByRole('button', { name: 'Créer le modèle' }).click()

    // Back on the list, which now holds it.
    await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}/admin/modeles-sortie$`))
    await expect(main(page).getByText(templateName, { exact: true })).toBeVisible()
    const templates = await readTemplates(organizer, team.slug)
    expect(templates.templates).toHaveLength(1)
    const [template] = templates.templates
    expect(template.name).toBe(templateName)
    expect(template.markdown.trim()).toBe(description)
    expect(template.groups.map((g) => [g.name, g.maxParticipants])).toEqual([
      [first, 6],
      [second, 10],
    ])

    // --- A ride from it: the template fills the editor, the organizer only names and dates it.
    await page.goto(`/equipes/${team.slug}/sorties/nouvelle`)
    await expect(
      main(page).getByRole('heading', { name: 'Créer une sortie', level: 1 })
    ).toBeVisible()
    const load = main(page).getByRole('button', { name: 'Charger un modèle' })
    await hydrated(load)
    await load.click()
    const picker = page.getByRole('dialog', { name: 'Choisir un modèle' })
    await picker.getByRole('button').filter({ hasText: templateName }).click()
    await expect(picker).toBeHidden()

    const title = main(page).getByRole('textbox', { name: 'Titre de la sortie' })
    await expect(title).toHaveValue(templateName)
    await expect(main(page).getByRole('textbox', { name: 'Nom du groupe' })).toHaveCount(2)
    await expect(main(page).getByRole('textbox', { name: 'Nom du groupe' }).nth(0)).toHaveValue(
      first
    )
    await expect(main(page).getByRole('textbox', { name: 'Nom du groupe' }).nth(1)).toHaveValue(
      second
    )
    await expect(main(page).getByRole('textbox', { name: 'Taille max' }).nth(1)).toHaveValue('10')
    await expect(main(page).getByText(description)).toBeVisible()

    const rideName = unique('Sortie depuis modèle')
    await title.fill(rideName)
    const day = parisDaysAhead(5, 9, 15)
    await pickDateTime(page, main(page).getByRole('button', { name: 'Départ' }), day)
    await main(page).getByRole('button', { name: 'Créer la sortie' }).click()
    await expect(main(page).getByRole('heading', { name: rideName, level: 2 })).toBeVisible()
    await expect(groupCard(page, first).getByText('0/6 participants')).toBeVisible()
    await expect(groupCard(page, second).getByText('0/10 participants')).toBeVisible()

    const rideSlug = new URL(page.url()).pathname.split('/').pop()!
    const ride = await readRide(organizer, team.slug, rideSlug)
    expect(ride.name).toBe(rideName)
    expect(ride.media.markdown.trim()).toBe(description)
    expect(parisWallClock(ride.dateTime)).toEqual(day)
    // The template's status (PUBLISHED by default) carries over.
    expect(ride.status).toBe(template.status)
    expect(ride.groups.map((g) => [g.name, g.maxParticipants])).toEqual([
      [first, 6],
      [second, 10],
    ])
    // The template is still there, untouched, for the next ride.
    expect((await readTemplates(organizer, team.slug)).templates.map((t) => t.name)).toEqual([
      templateName,
    ])
  })
})
