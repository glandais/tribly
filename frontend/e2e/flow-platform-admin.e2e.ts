import type { Page } from '@playwright/test'
import type { AdminTeamDto, TeamDetailDto } from '../src/api/dto'
import { apiGetOrNull } from './support/api'
import { getTeam, newTeam, newUser, roleSession, signIn } from './support/data'
import { expect, test, unique } from './support/fixtures'
import { listedTeams } from './support/flow-team'
import {
  ADMIN_TEAMS_PATH,
  adminTeam,
  adminTeamRow,
  adminUser,
  adminUserRow,
  adminUsersSearch,
  setPlatformRole,
  toggleTeamArchived,
} from './support/platform-admin'
import { hydrated, pageAs } from './support/ui'

/**
 * The platform administration, through its screens: the team list (archive, restore), the user
 * list (the platform role), and the « Paramètres plateforme » block of a team's settings, whose
 * governance switches decide what the team's own admins may do.
 *
 * The seeded admin account is shared by every test running in parallel: its own rights are never
 * touched (the self-demotion check only reads its row), the teams archived are the test's own, and
 * the platform role goes to a fresh account.
 */

// The admin's session goes into the page's context, not `test.use(as('admin'))`: a storageState
// in `use` also reaches the Node-side request contexts of support/api.ts, whose refresh_token cookie
// the backend honours — an « anonymous » API read would then be the platform admin's.
test.beforeEach(async ({ context }) => signIn(context, await roleSession('admin')))

const main = (page: Page) => page.getByRole('main')

/** The team settings form, once hydrated. */
async function settingsForm(page: Page, slug: string) {
  await page.goto(`/equipes/${slug}/admin/parametres`)
  const nameInput = main(page).getByLabel("Nom de l'équipe")
  await hydrated(nameInput)
  return main(page)
    .locator('form')
    .filter({ has: page.getByLabel("Nom de l'équipe") })
}

const teamHeading = (page: Page, name: string) =>
  page.getByRole('heading', { level: 1, name, exact: true })

test.describe('archiving a team', () => {
  test('the platform admin archives a team from the list: it is gone for its members, and a restore brings it back', async ({
    page,
  }) => {
    const owner = await newUser('Platform archive owner')
    const team = await newTeam(owner, unique('Archivage'))

    await page.goto(ADMIN_TEAMS_PATH)
    const row = adminTeamRow(page, team)
    await expect(row.getByText('Active', { exact: true })).toBeVisible()
    await expect(row.getByText(team.name, { exact: true })).toBeVisible()
    // The row's only control: archive (a toggle icon — see the accessible-name test below).
    const toggle = row.getByRole('button')
    await hydrated(toggle)
    const archived = page.waitForResponse(
      (r) => r.url().endsWith(`/api/admin/teams/${team.id}/toggle-deleted`) && r.ok()
    )
    await toggle.click()
    expect(((await (await archived).json()) as AdminTeamDto).deleted).toBe(true)

    // Its owner no longer reaches it…
    expect(await apiGetOrNull<TeamDetailDto>(owner, `/api/teams/${team.slug}`)).toBeNull()
    // …while the platform list keeps it, marked « Archivée ».
    await page.reload()
    await expect(row.getByText('Archivée', { exact: true })).toBeVisible()

    // Restored (here through the API; from the list, see below), the team is its owner's again.
    await toggleTeamArchived(team)
    expect((await getTeam(owner, team.slug)).role).toBe('ADMIN')
    await page.reload()
    await expect(adminTeamRow(page, team).getByText('Active', { exact: true })).toBeVisible()
  })

  // Regression (ebf14f23): the toggle mutation invalidates the admin team list — before, the row
  // kept its « Active » badge until a reload.
  test('the status badge follows the archive at once, without a reload', async ({ page }) => {
    const owner = await newUser('Platform archive badge owner')
    const team = await newTeam(owner, unique('Archivage badge'))

    await page.goto(ADMIN_TEAMS_PATH)
    const row = adminTeamRow(page, team)
    await expect(row.getByText('Active', { exact: true })).toBeVisible()
    const toggle = row.getByRole('button')
    await hydrated(toggle)
    const archived = page.waitForResponse(
      (r) => r.url().endsWith(`/api/admin/teams/${team.id}/toggle-deleted`) && r.ok()
    )
    await toggle.click()
    expect(
      ((await (await archived).json()) as AdminTeamDto).deleted,
      'precondition: the team is archived'
    ).toBe(true)

    // No reload: the list is read again at once, and the row stays, marked « Archivée ».
    await expect(row.getByText('Archivée', { exact: true })).toBeVisible()
    await expect(row.getByText('Active', { exact: true })).toHaveCount(0)
  })

  // Regression (2270bc79): the admin list and GET /api/admin/teams/{id} include archived teams —
  // before, an archived team left the list and could only be restored through the API.
  test('an archived team stays in the list, marked « Archivée », with a way to restore it', async ({
    page,
  }) => {
    const owner = await newUser('Platform restore owner')
    const team = await newTeam(owner, unique('Restauration'))
    expect((await toggleTeamArchived(team)).deleted, 'precondition: the team is archived').toBe(
      true
    )
    // The admin read answers too, instead of a 404.
    expect((await adminTeam(team)).deleted).toBe(true)

    await page.goto(ADMIN_TEAMS_PATH)
    const row = adminTeamRow(page, team)
    await expect(row.getByText('Archivée', { exact: true })).toBeVisible()
    const restore = row.getByRole('button', { name: "Restaurer l'équipe" })
    await hydrated(restore)
    await restore.click()
    await expect(row.getByText('Active', { exact: true })).toBeVisible()
    expect((await getTeam(owner, team.slug)).role).toBe('ADMIN')
  })
})

test.describe('the platform role', () => {
  test('the platform admin grants the role to a user, who then reaches the platform area; and takes it back', async ({
    browser,
    page,
  }) => {
    const target = await newUser('Platform role target')
    await page.goto(adminUsersSearch(target.user.email))
    const row = adminUserRow(page, target.user.email)
    await expect(row.getByText(target.user.displayName, { exact: true })).toBeVisible()
    await expect(row.getByText('Admin plateforme', { exact: true })).toHaveCount(0)

    const toggle = row.getByRole('button')
    await hydrated(toggle)
    const granted = page.waitForResponse(
      (r) => r.url().endsWith(`/api/admin/users/${target.user.id}/platform-role`) && r.ok()
    )
    await toggle.click()
    await granted
    expect((await adminUser(target.user.id)).platformRole).toBe('PLATFORM_ADMIN')

    try {
      // The new admin's next session carries the role: the platform area opens.
      const { context, page: theirs } = await pageAs(browser, target)
      try {
        await theirs.goto('/plateforme')
        await expect(
          theirs.getByRole('heading', { name: 'Administration plateforme', exact: true })
        ).toBeVisible()
        await expect(theirs).toHaveURL(/\/plateforme$/)
      } finally {
        await context.close()
      }

      // Back on the list (reloaded: see the badge test below), the role is shown, and taken back.
      await page.reload()
      await expect(row.getByText('Admin plateforme', { exact: true })).toBeVisible()
      await hydrated(toggle)
      const revoked = page.waitForResponse(
        (r) => r.url().endsWith(`/api/admin/users/${target.user.id}/platform-role`) && r.ok()
      )
      await toggle.click()
      await revoked
      expect((await adminUser(target.user.id)).platformRole ?? null).toBeNull()
      await page.reload()
      await expect(row.getByText(target.user.displayName, { exact: true })).toBeVisible()
      await expect(row.getByText('Admin plateforme', { exact: true })).toHaveCount(0)
    } finally {
      // Never leave a stray platform admin behind a failed assertion.
      if ((await adminUser(target.user.id)).platformRole)
        await setPlatformRole(target.user.id, undefined)
    }
  })

  test("the platform admin's own row cannot take its own role away", async ({ page, seed }) => {
    await page.goto(adminUsersSearch(seed.admin.email))
    const row = adminUserRow(page, seed.admin.email)
    await expect(row.getByText('Admin plateforme', { exact: true })).toBeVisible()
    const toggle = row.getByRole('button')
    await hydrated(toggle)
    await expect(toggle).toBeDisabled()
  })

  // Regression (ebf14f23): the role mutation invalidates the user list.
  test('the platform role badge follows the grant at once, without a reload', async ({ page }) => {
    const target = await newUser('Platform role badge target')
    try {
      await page.goto(adminUsersSearch(target.user.email))
      const row = adminUserRow(page, target.user.email)
      await expect(row.getByText(target.user.displayName, { exact: true })).toBeVisible()
      const toggle = row.getByRole('button')
      await hydrated(toggle)
      const granted = page.waitForResponse(
        (r) => r.url().endsWith(`/api/admin/users/${target.user.id}/platform-role`) && r.ok()
      )
      await toggle.click()
      await granted
      expect(
        (await adminUser(target.user.id)).platformRole,
        'precondition: the role is granted'
      ).toBe('PLATFORM_ADMIN')

      await expect(row.getByText('Admin plateforme', { exact: true })).toBeVisible()
    } finally {
      await setPlatformRole(target.user.id, undefined)
    }
  })
})

// Regression (ebf14f23): each ActionIcon carries its Tooltip's label as aria-label.
test('the row actions of the platform lists have an accessible name', async ({ page, seed }) => {
  const owner = await newUser('Platform a11y owner')
  const team = await newTeam(owner, unique('Accessibilité'))

  await page.goto(ADMIN_TEAMS_PATH)
  const teamRow = adminTeamRow(page, team)
  await expect(teamRow.getByRole('button'), 'precondition: the row has its button').toHaveCount(1)
  // Soft: the user list's button is checked too, whatever this one gives.
  await expect.soft(teamRow.getByRole('button', { name: "Archiver l'équipe" })).toBeVisible({
    timeout: 2_000,
  })

  await page.goto(adminUsersSearch(seed.admin.email))
  const userRow = adminUserRow(page, seed.admin.email)
  await expect(userRow.getByRole('button')).toHaveCount(1)
  await expect(
    userRow.getByRole('button', {
      name: 'Impossible de retirer votre propre rôle admin plateforme',
    })
  ).toBeVisible({ timeout: 2_000 })
})

test.describe('team governance', () => {
  test("the platform admin lets a team's admins choose its visibility and add members, from the settings; the owner then makes it unlisted", async ({
    browser,
    page,
  }) => {
    test.setTimeout(90_000)
    const owner = await newUser('Platform governance owner')
    const team = await newTeam(owner, unique('Gouvernance'))
    expect(team).toMatchObject({ visibilityEditable: false, addMemberAllowed: false })

    const { context: ownerContext, page: ownerPage } = await pageAs(browser, owner)
    try {
      // Before: the owner reads the visibility, cannot change it, and has no way to add anyone.
      let ownerForm = await settingsForm(ownerPage, team.slug)
      await expect(ownerForm.getByRole('combobox', { name: "Visibilité de l'équipe" })).toHaveCount(
        0
      )
      await expect(ownerForm.getByText('Équipe uniquement', { exact: true })).toBeVisible()
      for (const name of [
        'Autoriser les admins à modifier la visibilité',
        'Autoriser les admins à ajouter des membres',
      ])
        await expect(ownerForm.getByRole('switch', { name })).toBeDisabled()
      await ownerPage.goto(`/equipes/${team.slug}/admin/membres`)
      await expect(
        main(ownerPage).getByRole('heading', { level: 2, name: 'Membres', exact: true })
      ).toBeVisible()
      await expect(main(ownerPage).getByText(owner.user.displayName)).toBeVisible()
      await expect(main(ownerPage).getByRole('button', { name: 'Inviter par e-mail' })).toHaveCount(
        0
      )

      // The platform admin turns both switches on in the team's settings.
      const form = await settingsForm(page, team.slug)
      await expect(
        form.getByRole('heading', { level: 4, name: 'Paramètres plateforme' })
      ).toBeVisible()
      const visibilityEditable = form.getByRole('switch', {
        name: 'Autoriser les admins à modifier la visibilité',
      })
      const addMemberAllowed = form.getByRole('switch', {
        name: 'Autoriser les admins à ajouter des membres',
      })
      await expect(visibilityEditable).toBeEnabled()
      await expect(visibilityEditable).not.toBeChecked()
      await expect(addMemberAllowed).not.toBeChecked()
      await visibilityEditable.check()
      await addMemberAllowed.check()
      await form.getByRole('button', { name: 'Enregistrer' }).click()
      await expect(page.getByText('Équipe mise à jour avec succès', { exact: true })).toBeVisible()
      await expect(teamHeading(page, team.name)).toBeVisible()
      expect(await adminTeam(team)).toMatchObject({
        visibilityEditable: true,
        addMemberAllowed: true,
        joinable: false,
        visibility: 'TEAM',
      })
      // The settings page reads them back.
      const reread = await settingsForm(page, team.slug)
      await expect(
        reread.getByRole('switch', { name: 'Autoriser les admins à modifier la visibilité' })
      ).toBeChecked()
      await expect(
        reread.getByRole('switch', { name: 'Autoriser les admins à ajouter des membres' })
      ).toBeChecked()

      // After: the owner may invite…
      await ownerPage.goto(`/equipes/${team.slug}/admin/membres`)
      await expect(
        main(ownerPage).getByRole('button', { name: 'Inviter par e-mail' })
      ).toBeVisible()

      // …and choose the visibility: unlisted.
      ownerForm = await settingsForm(ownerPage, team.slug)
      const visibility = ownerForm.getByRole('combobox', { name: "Visibilité de l'équipe" })
      await expect(visibility).toHaveValue('Équipe uniquement')
      await visibility.click()
      await ownerPage.getByRole('option', { name: 'Non répertorié', exact: true }).click()
      await expect(visibility).toHaveValue('Non répertorié')
      await ownerForm.getByRole('button', { name: 'Enregistrer' }).click()
      await expect(
        ownerPage.getByText('Équipe mise à jour avec succès', { exact: true })
      ).toBeVisible()
      await expect(teamHeading(ownerPage, team.name)).toBeVisible()
      expect((await getTeam(owner, team.slug)).visibility).toBe('PUBLIC_UNLISTED')
    } finally {
      await ownerContext.close()
    }

    await expectUnlisted(browser, team)
  })

  test('an unlisted team is missing from /equipes, but open to anyone holding its link', async ({
    browser,
  }) => {
    const owner = await newUser('Platform unlisted owner')
    const team = await newTeam(owner, unique('Non répertoriée'), { visibility: 'PUBLIC_UNLISTED' })
    await expectUnlisted(browser, team)
  })
})

/**
 * `team` is PUBLIC_UNLISTED: /equipes searched on its name shows nothing to an anonymous visitor nor
 * to a signed-in outsider (the API neither), while its URL opens it to both.
 */
async function expectUnlisted(browser: import('@playwright/test').Browser, team: TeamDetailDto) {
  const outsider = await newUser('Platform unlisted outsider')
  expect((await listedTeams(undefined, team.name)).teams).toEqual([])
  expect((await listedTeams(outsider, team.name)).teams).toEqual([])

  for (const who of [undefined, outsider]) {
    const { context, page } = await pageAs(browser, who)
    try {
      // role=all: a signed-in visitor's list defaults to their own teams.
      await page.goto(`/equipes?q=${encodeURIComponent(team.name)}${who ? '&role=all' : ''}`)
      await expect(page.getByRole('heading', { level: 2, name: 'Équipes' })).toBeVisible()
      await expect(main(page).getByText('Aucune équipe trouvée', { exact: true })).toBeVisible()
      await expect(main(page).getByRole('link').filter({ hasText: team.name })).toHaveCount(0)

      const response = await page.goto(`/equipes/${team.slug}`)
      expect(response?.status()).toBe(200)
      await expect(teamHeading(page, team.name)).toBeVisible()
    } finally {
      await context.close()
    }
  }
}
