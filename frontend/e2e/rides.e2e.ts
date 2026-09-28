import { ApiError } from './support/api'
import { addMember, newTeam, newUser, roleSession, signIn } from './support/data'
import { expect, test, unique } from './support/fixtures'
import {
  groupCard,
  groupId,
  joinGroup,
  newRide,
  openRide,
  postComments,
  readRide,
} from './support/rides'
import { hydrated } from './support/ui'

/**
 * The ride detail page (`RideDetailPage` + `RideGroupCard` + `CommentSection`), against the real
 * backend: the first page of comments, the group leader invariant, registration to a group, a
 * past ride, and moving from one group to another.
 *
 * Each test builds its own team (owned by the platform admin: only a platform admin may add a member
 * directly, an owner gets TEAM_ADD_MEMBER_NOT_ALLOWED), its own members and its own ride.
 */

/** A team with an organizer (who creates the rides) and a plain member (who looks). */
async function ridingTeam(label: string) {
  const owner = await roleSession('admin')
  const team = await newTeam(owner, unique(`Rides ${label}`))
  const organizer = await newUser(unique('Créatrice'))
  const member = await newUser(unique('Membre'))
  await addMember(owner, team.slug, organizer, 'ORGANIZER')
  await addMember(owner, team.slug, member)
  return { owner, team, organizer, member }
}

test.describe('comments', () => {
  test('a ride with more than 20 comments loads only 20 at first render', async ({ page }) => {
    const { team, organizer, member } = await ridingTeam('comments')
    const ride = await newRide(organizer, team.slug, unique('Sortie bavarde'), {
      groups: [{ name: unique('Groupe unique') }],
    })
    const contents = Array.from(
      { length: 25 },
      (_, i) => `Commentaire e2e n°${String(i + 1).padStart(2, '0')}`
    )
    await postComments(organizer, team.slug, ride.slug, contents)

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)

    // The heading counts every comment; only the first page of them is in the DOM.
    await expect(page.getByRole('heading', { name: 'Commentaires (25)' })).toBeVisible()
    const rendered = page.getByText(/^Commentaire e2e n°\d{2}$/)
    await expect(rendered).toHaveCount(20)
    // Newest first: the five oldest are the ones left out.
    await expect(page.getByText('Commentaire e2e n°25', { exact: true })).toBeVisible()
    await expect(page.getByText('Commentaire e2e n°06', { exact: true })).toBeVisible()
    await expect(page.getByText('Commentaire e2e n°05', { exact: true })).toHaveCount(0)

    // The rest is one click away, and then the button goes.
    const loadMore = page.getByRole('button', { name: 'Charger plus de commentaires' })
    await hydrated(loadMore)
    await loadMore.click()
    await expect(rendered).toHaveCount(25)
    await expect(page.getByText('Commentaire e2e n°01', { exact: true })).toBeVisible()
    await expect(loadMore).toHaveCount(0)
  })
})

test.describe('group leader', () => {
  test('no designated leader: no badge, and never the ride creator in its place', async ({
    page,
  }) => {
    const { team, organizer, member } = await ridingTeam('no-leader')
    const first = unique('Groupe A')
    const second = unique('Groupe B')
    const ride = await newRide(organizer, team.slug, unique('Sortie sans meneur'), {
      groups: [{ name: first }, { name: second }],
    })
    expect(ride.groups.map((g) => g.leader)).toEqual([undefined, undefined])

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)

    for (const name of [first, second]) {
      const card = groupCard(page, name)
      await expect(card).toBeVisible()
      await expect(card.getByText('Meneur', { exact: true })).toHaveCount(0)
      await expect(card.getByText(organizer.user.displayName)).toHaveCount(0)
    }
    await expect(page.getByText('Meneur', { exact: true })).toHaveCount(0)
  })

  test('a designated leader is shown on their group only', async ({ page }) => {
    const { owner, team, organizer, member } = await ridingTeam('leader')
    const leader = await newUser(unique('Meneuse'))
    await addMember(owner, team.slug, leader)
    const led = unique('Groupe mené')
    const free = unique('Groupe libre')
    const ride = await newRide(organizer, team.slug, unique('Sortie menée'), {
      groups: [{ name: led, leaderId: leader.user.id }, { name: free }],
    })

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)

    const ledCard = groupCard(page, led)
    await expect(ledCard.getByText('Meneur', { exact: true })).toBeVisible()
    await expect(ledCard.getByText(leader.user.displayName, { exact: true })).toBeVisible()
    await expect(ledCard.getByText(organizer.user.displayName)).toHaveCount(0)

    const freeCard = groupCard(page, free)
    await expect(freeCard).toBeVisible()
    await expect(freeCard.getByText('Meneur', { exact: true })).toHaveCount(0)
    await expect(freeCard.getByText(leader.user.displayName)).toHaveCount(0)
    await expect(freeCard.getByText(organizer.user.displayName)).toHaveCount(0)
  })
})

test.describe('registration', () => {
  test('register to a group, see it, unregister', async ({ page }) => {
    const { team, organizer, member } = await ridingTeam('register')
    const name = unique('Groupe loisir')
    const ride = await newRide(organizer, team.slug, unique('Sortie à rejoindre'), {
      groups: [{ name, maxParticipants: 5 }],
    })

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)

    const card = groupCard(page, name)
    await expect(card.getByText('0/5 participants')).toBeVisible()
    await expect(card.getByText('Inscrit', { exact: true })).toHaveCount(0)
    await hydrated(card.getByRole('button', { name: 'Rejoindre' }))
    await card.getByRole('button', { name: 'Rejoindre' }).click()

    await expect(card.getByText('Inscrit', { exact: true })).toBeVisible()
    await expect(card.getByRole('button', { name: 'Quitter' })).toBeEnabled()
    await expect(card.getByText('1/5 participants')).toBeVisible()

    // It is the server's state, not only the optimistic cache.
    await page.reload()
    await openRide(page, team.slug, ride)
    await expect(card.getByText('Inscrit', { exact: true })).toBeVisible()
    await hydrated(card.getByRole('button', { name: 'Quitter' }))
    await card.getByRole('button', { name: 'Quitter' }).click()

    await expect(card.getByRole('button', { name: 'Rejoindre' })).toBeEnabled()
    await expect(card.getByText('Inscrit', { exact: true })).toHaveCount(0)
    await expect(card.getByText('0/5 participants')).toBeVisible()

    await page.reload()
    await openRide(page, team.slug, ride)
    await expect(card.getByRole('button', { name: 'Rejoindre' })).toBeVisible()
    await expect(card.getByText('Inscrit', { exact: true })).toHaveCount(0)
  })

  test('a full group shows « Complet » and offers no way to join', async ({ page }) => {
    const { owner, team, organizer, member } = await ridingTeam('full')
    const other = await newUser(unique('Premier inscrit'))
    await addMember(owner, team.slug, other)
    const full = unique('Groupe plein')
    const open = unique('Groupe ouvert')
    const ride = await newRide(organizer, team.slug, unique('Sortie presque pleine'), {
      groups: [
        { name: full, maxParticipants: 1 },
        { name: open, maxParticipants: 10 },
      ],
    })
    await joinGroup(other, team.slug, ride, full)

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)

    const fullCard = groupCard(page, full)
    await expect(fullCard.getByText('Complet', { exact: true })).toBeVisible()
    await expect(fullCard.getByText('1/1 participants')).toBeVisible()
    await expect(fullCard.getByRole('button', { name: 'Rejoindre' })).toHaveCount(0)
    // The other group is still open — which also proves the page offers joining at all.
    await expect(groupCard(page, open).getByRole('button', { name: 'Rejoindre' })).toBeEnabled()
  })

  test('a ride whose only group is full still says « Complet »', async ({ page }) => {
    // RideGroupCard used to render « Complet » only inside `(canJoin || isJoined)`, and canJoin is
    // false once the whole ride is full — the capacity state vanished exactly then (fixed
    // 2026-09-25).
    const { owner, team, organizer, member } = await ridingTeam('all-full')
    const other = await newUser(unique('Premier inscrit'))
    await addMember(owner, team.slug, other)
    const full = unique('Groupe plein')
    const ride = await newRide(organizer, team.slug, unique('Sortie pleine'), {
      groups: [{ name: full, maxParticipants: 1 }],
    })
    await joinGroup(other, team.slug, ride, full)

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)

    // Preconditions: the group is on the page, and full.
    const card = groupCard(page, full)
    await expect(card, 'precondition: the group card is rendered').toBeVisible()
    await expect(
      card.getByText('1/1 participants'),
      'precondition: the only seat is taken'
    ).toBeVisible()
    await expect(
      card.getByRole('button', { name: 'Rejoindre' }),
      'precondition: a full group offers no join'
    ).toHaveCount(0)
    await expect(card.getByText('Complet', { exact: true })).toBeVisible()
  })

  test('a GROUP_FULL answer rolls back and is reported on that group', async ({ page }) => {
    const { owner, team, organizer, member } = await ridingTeam('race')
    const other = await newUser(unique('Plus rapide'))
    await addMember(owner, team.slug, other)
    const contested = unique('Groupe disputé')
    const spare = unique('Groupe de réserve')
    const ride = await newRide(organizer, team.slug, unique('Sortie disputée'), {
      groups: [
        { name: contested, maxParticipants: 1 },
        { name: spare, maxParticipants: 10 },
      ],
    })

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)
    const card = groupCard(page, contested)
    const join = card.getByRole('button', { name: 'Rejoindre' })
    await expect(join).toBeEnabled()
    await hydrated(join)

    // Someone else takes the last seat while the page is open: the join now answers GROUP_FULL.
    await joinGroup(other, team.slug, ride, contested)
    const answer = page.waitForResponse(
      (r) => r.url().includes(`/rides/${ride.slug}/groups/`) && r.request().method() === 'POST'
    )
    await join.click()
    const response = await answer
    expect(response.status()).toBe(409)
    expect(((await response.json()) as { code?: string }).code).toBe('GROUP_FULL')

    // The failure is written in the card of the group that refused, not in the other one...
    await expect(card.getByRole('alert').filter({ hasText: 'Ce groupe est complet' })).toBeVisible()
    await expect(groupCard(page, spare).getByText('Ce groupe est complet')).toHaveCount(0)
    // ...the optimistic registration is undone, and the refetched group says it is full.
    await expect(card.getByText('Inscrit', { exact: true })).toHaveCount(0)
    await expect(card.getByText('Complet', { exact: true })).toBeVisible()
    await expect(card.getByText('1/1 participants')).toBeVisible()
  })
})

test.describe('a past ride', () => {
  test('a ride of yesterday says « Terminée » and offers no « Rejoindre » in any group', async ({
    page,
  }) => {
    const { team, organizer, member } = await ridingTeam('past')
    const first = unique('Groupe A')
    const second = unique('Groupe B')
    const ride = await newRide(organizer, team.slug, unique('Sortie passée'), {
      dateTime: new Date(Date.now() - 24 * 3600 * 1000).toISOString(),
      groups: [
        { name: first, maxParticipants: 10 },
        { name: second, maxParticipants: 10 },
      ],
    })
    expect(ride.status, 'precondition: published, so only its date stops the join').toBe(
      'PUBLISHED'
    )

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)

    const header = page.getByRole('main').getByRole('heading', { level: 2, name: ride.name })
    await expect(header).toBeVisible()
    await expect(page.getByRole('main').getByText('Terminée', { exact: true })).toBeVisible()
    await expect(page.getByRole('main').getByText('Publié', { exact: true })).toBeVisible()
    for (const name of [first, second]) {
      const card = groupCard(page, name)
      await expect(card.getByText('0/10 participants')).toBeVisible()
      await expect(card.getByRole('button', { name: 'Rejoindre' })).toHaveCount(0)
      await expect(card.getByText('Complet', { exact: true })).toHaveCount(0)
    }
  })

  test('the API refuses a registration to a ride of yesterday', async () => {
    // Regression (80670273): RideService.joinGroup refuses a ride whose dateTime is past (409
    // RIDE_PAST) — before, only the clients withheld « Rejoindre », and the API registered.
    const { team, organizer, member } = await ridingTeam('past-api')
    const name = unique('Groupe A')
    const ride = await newRide(organizer, team.slug, unique('Sortie passée'), {
      dateTime: new Date(Date.now() - 24 * 3600 * 1000).toISOString(),
      groups: [{ name }],
    })
    const answer = await joinGroup(member, team.slug, ride, name).then(
      () => undefined,
      (error: unknown) => error
    )
    expect(answer, 'the join is refused').toBeInstanceOf(ApiError)
    expect((answer as ApiError).status).toBe(409)
    expect((answer as ApiError).code).toBe('RIDE_PAST')
    expect((await readRide(member, team.slug, ride.slug)).registered).toBe(false)
  })

  test('an upcoming ride is not « Terminée »', async ({ page }) => {
    // The counterpart of the test above: the badge follows the date, not the status.
    const { team, organizer, member } = await ridingTeam('upcoming')
    const name = unique('Groupe A')
    const ride = await newRide(organizer, team.slug, unique('Sortie à venir'), {
      groups: [{ name }],
    })
    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)
    await expect(groupCard(page, name).getByRole('button', { name: 'Rejoindre' })).toBeVisible()
    await expect(page.getByRole('main').getByText('Terminée', { exact: true })).toHaveCount(0)
  })
})

test.describe('changing group', () => {
  test('registered in one group: no « Rejoindre » on the others, the API refuses a second group, and leaving frees the member to join another', async ({
    page,
  }) => {
    const { team, organizer, member } = await ridingTeam('switch')
    const first = unique('Groupe A')
    const second = unique('Groupe B')
    const ride = await newRide(organizer, team.slug, unique('Sortie à deux groupes'), {
      groups: [
        { name: first, maxParticipants: 10 },
        { name: second, maxParticipants: 10 },
      ],
    })
    await joinGroup(member, team.slug, ride, first)

    await signIn(page.context(), member)
    await openRide(page, team.slug, ride)
    const firstCard = groupCard(page, first)
    const secondCard = groupCard(page, second)
    await expect(firstCard.getByText('Inscrit', { exact: true })).toBeVisible()
    await expect(firstCard.getByRole('button', { name: 'Quitter' })).toBeEnabled()
    await expect(firstCard.getByText('1/10 participants')).toBeVisible()
    await expect(secondCard.getByText('0/10 participants')).toBeVisible()
    await expect(secondCard.getByRole('button', { name: 'Rejoindre' })).toHaveCount(0)
    await expect(secondCard.getByText('Inscrit', { exact: true })).toHaveCount(0)

    // The page is not the only guard: the API refuses a second registration on the same ride.
    const refused = await joinGroup(member, team.slug, ride, second).then(
      () => undefined,
      (error: unknown) => error
    )
    expect(refused).toBeInstanceOf(ApiError)
    expect((refused as ApiError).status).toBe(409)
    expect((refused as ApiError).code).toBe('ALREADY_REGISTERED')

    // Leave A: B becomes joinable, and joining it moves the member there.
    const leave = firstCard.getByRole('button', { name: 'Quitter' })
    await hydrated(leave)
    await leave.click()
    await expect(firstCard.getByText('Inscrit', { exact: true })).toHaveCount(0)
    await expect(firstCard.getByText('0/10 participants')).toBeVisible()
    const join = secondCard.getByRole('button', { name: 'Rejoindre' })
    await expect(join).toBeEnabled()
    await join.click()
    await expect(secondCard.getByText('Inscrit', { exact: true })).toBeVisible()
    await expect(secondCard.getByText('1/10 participants')).toBeVisible()
    await expect(firstCard.getByRole('button', { name: 'Rejoindre' })).toHaveCount(0)

    const after = await readRide(member, team.slug, ride.slug)
    expect(after.registered).toBe(true)
    expect(after.registeredGroupId).toBe(groupId(ride, second))
    expect(after.groups.map((g) => [g.name, g.countParticipants])).toEqual([
      [first, 0],
      [second, 1],
    ])

    // The server's state, not only the optimistic cache.
    await page.reload()
    await openRide(page, team.slug, ride)
    await expect(secondCard.getByText('Inscrit', { exact: true })).toBeVisible()
    await expect(firstCard.getByRole('button', { name: 'Rejoindre' })).toHaveCount(0)
  })
})
