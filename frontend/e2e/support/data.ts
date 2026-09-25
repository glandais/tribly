import { readFileSync } from 'node:fs'
import type { BrowserContext } from '@playwright/test'
import type {
  AddMemberRequest,
  AdminTeamAttributesRequest,
  AdminTeamDto,
  TeamDetailDto,
  TeamRequest,
  TeamRole,
} from '../../src/api/dto'
import { apiContext, expectOk, refresh, register, type AuthResponse } from './api'
import { stack, storageStatePath, type Role } from './stack'

/**
 * What most tests need before they open a page: a session for a seeded role, a fresh user, a team
 * of their own. Everything goes through the REST API, typed with the generated DTOs.
 */

/** Access tokens live 15 minutes: a cached one is refreshed once it is older than this. */
const SESSION_MAX_AGE_MS = 10 * 60_000

const sessions = new Map<Role, { auth: Promise<AuthResponse>; at: number }>()

/**
 * A fresh access token for a role global-setup signed in (admin, rider).
 *
 * Cached per worker process, and shared by concurrent callers: every test of every worker hitting
 * POST /api/auth/refresh on the same saved session is exactly what trips the backend defect that
 * `refresh` (support/api.ts) documents — concurrent refreshes of one session answer 500 to all
 * but one (AuthService.refreshToken → user.recordLogin() → optimistic lock on User @Version). The
 * cache keeps a worker to one refresh per 10 minutes, and `refresh` retries the collisions that
 * remain between workers.
 */
export function roleSession(role: Role): Promise<AuthResponse> {
  const cached = sessions.get(role)
  if (cached && Date.now() - cached.at < SESSION_MAX_AGE_MS) return cached.auth
  const auth = (async () => {
    const saved = JSON.parse(readFileSync(storageStatePath(role), 'utf8')) as {
      cookies: { value: string }[]
    }
    const refreshed = await refresh(saved.cookies[0].value)
    if (!refreshed)
      throw new Error(`the saved ${role} session no longer refreshes — rerun global-setup`)
    return refreshed
  })()
  sessions.set(role, { auth, at: Date.now() })
  // A failure is not cached: the next caller tries again.
  auth.catch(() => {
    if (sessions.get(role)?.auth === auth) sessions.delete(role)
  })
  return auth
}

/**
 * Signs a browser context in as `auth`: the refresh_token cookie the backend would have set. Call it
 * before the first navigation — the SSR server and the app read it on the first request.
 */
export async function signIn(context: BrowserContext, auth: AuthResponse) {
  const { hostname } = new URL(stack.baseURL)
  await context.addCookies([
    {
      name: 'refresh_token',
      value: auth.refreshToken,
      domain: hostname,
      path: '/',
      httpOnly: true,
      secure: true,
      sameSite: 'Lax',
    },
  ])
}

/** A per-process unique, lowercase, address-safe tag for `label`. */
let tagCounter = 0
function uniqueTag(label: string): string {
  tagCounter += 1
  return `${label}-${process.pid}-${tagCounter}-${Date.now().toString(36)}`
    .toLowerCase()
    .replace(/[^a-z0-9-]/g, '-')
}

/** A verified account nobody else uses, signed up through the real register + e-mail link flow. */
export async function newUser(label: string): Promise<AuthResponse & { password: string }> {
  const password = 'e2e-password'
  const auth = await register({
    email: `${uniqueTag(label)}@e2e.test`,
    displayName: label,
    password,
  })
  return { ...auth, password }
}

/** An address no account holds and no other test uses (an invitee without an account, say). */
export function freshAddress(label: string): string {
  return `${uniqueTag(label)}@e2e.test`
}

export function teamRequest(name: string, overrides: Partial<TeamRequest> = {}): TeamRequest {
  return {
    name,
    visibility: 'TEAM',
    media: { markdown: '', assets: { images: [], attachments: [] } },
    enableTrips: true,
    enableAds: true,
    enablePosts: true,
    enableRides: true,
    enableRoutes: true,
    enableMemberDirectory: true,
    ...overrides,
  }
}

/**
 * The settings POST /api/teams leaves a team with, whatever its request said (Team entity defaults,
 * TeamService.createTeam).
 */
const CREATED_WITH = {
  visibility: 'TEAM',
  enableTrips: true,
  enableAds: true,
  enablePosts: true,
  enableRides: true,
  enableRoutes: true,
  enableMemberDirectory: false,
} as const satisfies Partial<TeamRequest>

export interface NewTeamOptions extends Partial<TeamRequest> {
  /**
   * Lets the team's own admins add members and send invitations. Born false, and an admin-only
   * attribute: the seeded platform admin sets it.
   */
  addMemberAllowed?: boolean
}

/**
 * A team owned by `owner`, with the settings of `teamRequest(name, overrides)`.
 *
 * POST /api/teams honours only the name, media and geometry of its request: it always creates a
 * TEAM-visible team and silently IGNORES the enable* flags (TeamService.createTeam never sets them,
 * only updateTeam does) — the team gets the entity defaults, member directory off. So whenever the
 * request differs from those defaults, it is applied again with a PUT, as the owner. A visibility
 * other than TEAM is then accepted only from a platform admin (the seeded admin is one).
 */
export async function newTeam(
  owner: AuthResponse,
  name: string,
  { addMemberAllowed, ...overrides }: NewTeamOptions = {}
): Promise<TeamDetailDto> {
  const request = teamRequest(name, overrides)
  const api = await apiContext(owner.accessToken)
  let team: TeamDetailDto
  try {
    team = await expectOk<TeamDetailDto>(
      await api.post('/api/teams', { data: { ...request, visibility: 'TEAM' } })
    )
    const differs = (Object.keys(CREATED_WITH) as (keyof typeof CREATED_WITH)[]).some(
      (key) => request[key] !== CREATED_WITH[key]
    )
    if (differs)
      team = await expectOk<TeamDetailDto>(
        await api.put(`/api/teams/${team.slug}`, { data: request })
      )
  } finally {
    await api.dispose()
  }
  if (addMemberAllowed) await setTeamAttributes(team, { addMemberAllowed })
  return team
}

/** Changes the admin-only attributes of a team, as the platform admin. */
export async function setTeamAttributes(
  team: TeamDetailDto,
  changes: Partial<AdminTeamAttributesRequest>
) {
  const admin = await roleSession('admin')
  const api = await apiContext(admin.accessToken)
  try {
    const current = await expectOk<AdminTeamDto>(await api.get(`/api/admin/teams/${team.id}`))
    const attributes: AdminTeamAttributesRequest = {
      visibilityEditable: current.visibilityEditable,
      joinable: current.joinable,
      addMemberAllowed: current.addMemberAllowed,
      enableRoutePlanner: current.enableRoutePlanner,
      ...changes,
    }
    await expectOk(await api.patch(`/api/admin/teams/${team.id}/attributes`, { data: attributes }))
  } finally {
    await api.dispose()
  }
}

/**
 * Adds `member` to the team, as `by`.
 *
 * A team is born with addMemberAllowed=false, and then only the platform admin may add a member
 * directly — its own admins get TEAM_ADD_MEMBER_NOT_ALLOWED. So pass `roleSession('admin')` as
 * `by`, or create the team with `newTeam(…, { addMemberAllowed: true })`. The platform admin does
 * not become a member by adding one.
 */
export async function addMember(
  by: AuthResponse,
  teamSlug: string,
  member: AuthResponse,
  role: TeamRole = 'MEMBER'
) {
  const api = await apiContext(by.accessToken)
  try {
    const request: AddMemberRequest = { userId: member.user.id, role }
    await expectOk(await api.post(`/api/teams/${teamSlug}/members`, { data: request }))
  } finally {
    await api.dispose()
  }
}
