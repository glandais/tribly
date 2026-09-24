import { readFileSync } from 'node:fs'
import type { BrowserContext } from '@playwright/test'
import type { AddMemberRequest, TeamDetailDto, TeamRequest, TeamRole } from '../../src/api/dto'
import { apiContext, expectOk, refresh, register, type AuthResponse } from './api'
import { stack, storageStatePath, type Role } from './stack'

/**
 * What most tests need before they open a page: a session for a seeded role, a fresh user, a team
 * of their own. Everything goes through the REST API, typed with the generated DTOs.
 */

/** A fresh access token for a role global-setup signed in (admin, rider). */
export async function roleSession(role: Role): Promise<AuthResponse> {
  const saved = JSON.parse(readFileSync(storageStatePath(role), 'utf8')) as {
    cookies: { value: string }[]
  }
  const auth = await refresh(saved.cookies[0].value)
  if (!auth) throw new Error(`the saved ${role} session no longer refreshes — rerun global-setup`)
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

let userCounter = 0

/** A verified account nobody else uses, signed up through the real register + e-mail link flow. */
export async function newUser(label: string): Promise<AuthResponse & { password: string }> {
  userCounter += 1
  const tag = `${label}-${process.pid}-${userCounter}-${Date.now().toString(36)}`
    .toLowerCase()
    .replace(/[^a-z0-9-]/g, '-')
  const password = 'e2e-password'
  const auth = await register({ email: `${tag}@e2e.test`, displayName: label, password })
  return { ...auth, password }
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
 * A team owned by `owner`. A team is always born TEAM-visible: any other visibility is applied
 * afterwards, which only a platform admin may do (the seeded admin is one).
 */
export async function newTeam(
  owner: AuthResponse,
  name: string,
  overrides: Partial<TeamRequest> = {}
): Promise<TeamDetailDto> {
  const api = await apiContext(owner.accessToken)
  try {
    const request = teamRequest(name, overrides)
    const created = await expectOk<TeamDetailDto>(
      await api.post('/api/teams', { data: { ...request, visibility: 'TEAM' } })
    )
    if (request.visibility === 'TEAM') return created
    return await expectOk<TeamDetailDto>(
      await api.put(`/api/teams/${created.slug}`, { data: request })
    )
  } finally {
    await api.dispose()
  }
}

/** Adds `member` to the team, as `by` — the platform admin can, whatever the team's settings. */
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
