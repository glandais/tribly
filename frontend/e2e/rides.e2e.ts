import { addMember, newTeam, newUser, roleSession, signIn } from './support/data'
import { expect, test, unique } from './support/fixtures'
import { groupCard, joinGroup, newRide, openRide, postComments } from './support/rides'
import { hydrated } from './support/ui'

/**
 * The ride detail page (`RideDetailPage` + `RideGroupCard` + `CommentSection`), against the real
 * backend: the first page of comments, the group leader invariant, and registration to a group.
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
    // Defect: RideDetailPage computes canJoinRide with `!ride.full`, and RideGroupCard renders its
    // « Complet » badge only inside `(canJoin || isJoined)` — so when every group of the ride is
    // full (ride.full), the full group shows neither a join button nor « Complet ».
    test.fail()
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
    // The defect.
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
