import type { Locator, Page } from '@playwright/test'
import type {
  AdminTeamDto,
  AdminUserDto,
  AssignPlatformRoleRequest,
  TeamDetailDto,
} from '../../src/api/dto'
import { apiGet, apiPost, apiPut } from './api'
import { roleSession } from './data'

/**
 * The platform administration journey (flow-platform-admin.e2e.ts): its screens' rows, and the
 * platform admin's reads and writes through the API. Every write goes to a team or an account the
 * test created — the seeded admin account is shared by every test running in parallel.
 */

export const ADMIN_TEAMS_PATH = '/plateforme/equipes'
export const ADMIN_USERS_PATH = '/plateforme/utilisateurs'

/**
 * The row of the platform team list holding `team`, found by its slug (unique, and printed in a
 * cell of its own). The list is the newest first, so a team the test just created is on page 1.
 */
export const adminTeamRow = (page: Page, team: Pick<TeamDetailDto, 'slug'>): Locator =>
  page
    .getByRole('main')
    .getByRole('row')
    .filter({ has: page.getByRole('cell', { name: team.slug, exact: true }) })

/** The row of the platform user list holding the account `email`. */
export const adminUserRow = (page: Page, email: string): Locator =>
  page
    .getByRole('main')
    .getByRole('row')
    .filter({ has: page.getByRole('cell', { name: email, exact: true }) })

/** The user list searched on `email` (`?q=`), so the account is on its first page. */
export const adminUsersSearch = (email: string) =>
  `${ADMIN_USERS_PATH}?q=${encodeURIComponent(email)}`

/** GET /api/admin/teams/{id} as the platform admin — archived teams included. */
export const adminTeam = async (team: Pick<TeamDetailDto, 'id'>) =>
  apiGet<AdminTeamDto>(await roleSession('admin'), `/api/admin/teams/${team.id}`)

/** GET /api/admin/users/{id} as the platform admin. */
export const adminUser = async (userId: string) =>
  apiGet<AdminUserDto>(await roleSession('admin'), `/api/admin/users/${userId}`)

/** POST …/toggle-deleted as the platform admin: archives a live team, restores an archived one. */
export const toggleTeamArchived = async (team: Pick<TeamDetailDto, 'id'>) =>
  apiPost<AdminTeamDto>(await roleSession('admin'), `/api/admin/teams/${team.id}/toggle-deleted`)

/** PUT …/platform-role as the platform admin — never on the seeded admin itself. */
export const setPlatformRole = async (userId: string, role: AssignPlatformRoleRequest['role']) =>
  apiPut<AdminUserDto>(await roleSession('admin'), `/api/admin/users/${userId}/platform-role`, {
    role,
  } satisfies AssignPlatformRoleRequest)
