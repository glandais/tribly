import { existsSync, readFileSync } from 'node:fs'
import type { AddMemberRequest, TeamDetailDto } from '../src/api/dto'
import {
  ApiError,
  apiContext,
  expectOk,
  loginWithOtp,
  loginWithPassword,
  refresh,
  register,
  writeFileAtomic,
  writeStorageState,
  type AuthResponse,
} from './support/api'
import { teamRequest } from './support/data'
import { seedPath, stack, storageStatePath, type Role } from './support/stack'
import type { Seed } from './support/fixtures'

/**
 * Brings the e2e stack to a known state before the suite: one session per role, saved as a
 * Playwright storageState, and the shared data every test may rely on.
 *
 * Idempotent, so a run against a stack that already went through it only refreshes the saved
 * sessions — the admin's OTP is rate-limited (3 per 5 minutes), which rules out logging in again on
 * every run. After `scripts/e2e.sh reset` the sessions no longer refresh and everything is redone.
 * Tests create their own data on top of this; nothing here is meant to be modified by a test.
 */

const RIDER = {
  email: 'rider@e2e.test',
  displayName: 'Rider E2E',
  password: 'e2e-rider-password',
}

// `slug` is what the backend derives from `name` — how an earlier, interrupted run's team is found.
const TEAM = { name: 'Peloton E2E', slug: 'peloton-e2e' }

export default async function globalSetup() {
  await assertStackUp()

  const admin = await session('admin', () => loginWithOtp(stack.adminEmail))
  const rider = await session('rider', async () => {
    try {
      return await loginWithPassword(RIDER.email, RIDER.password)
    } catch (e) {
      if (!(e instanceof ApiError) || e.status !== 400) throw e
      return register(RIDER)
    }
  })

  const team = await ensureTeam(admin)
  await ensureMember(admin, rider, team.slug)

  const seed: Seed = {
    admin: { email: stack.adminEmail, displayName: admin.user.displayName },
    rider: { email: RIDER.email, displayName: RIDER.displayName, password: RIDER.password },
    team,
  }
  writeFileAtomic(seedPath, JSON.stringify(seed, null, 2))
}

async function assertStackUp() {
  try {
    const response = await fetch(`${stack.baseURL}/api/config`)
    if (response.ok) return
    throw new Error(`answered ${response.status}`)
  } catch (e) {
    throw new Error(
      `e2e stack not reachable at ${stack.baseURL} (${(e as Error).message}) — start it with ` +
        '`scripts/e2e.sh up` from the repository root',
      { cause: e }
    )
  }
}

/** The saved session of `role` if it still refreshes, else a new login saved in its place. */
async function session(role: Role, login: () => Promise<AuthResponse>): Promise<AuthResponse> {
  const path = storageStatePath(role)
  if (existsSync(path)) {
    const saved = JSON.parse(readFileSync(path, 'utf8')) as { cookies: { value: string }[] }
    const refreshed = saved.cookies[0] && (await refresh(saved.cookies[0].value))
    if (refreshed) return refreshed
  }
  const auth = await login()
  writeStorageState(path, auth.refreshToken)
  return auth
}

async function ensureTeam(admin: AuthResponse): Promise<Seed['team']> {
  const api = await apiContext(admin.accessToken)
  try {
    // A team is always born TEAM-visible; only a platform admin may then open it to the public.
    // The PUT runs on every setup, so a team whose creation was interrupted is still made public.
    const request = teamRequest(TEAM.name)
    const existing = await api.get(`/api/teams/${TEAM.slug}`)
    const slug = existing.ok()
      ? TEAM.slug
      : (await expectOk<TeamDetailDto>(await api.post('/api/teams', { data: request }))).slug
    const team = await expectOk<TeamDetailDto>(
      await api.put(`/api/teams/${slug}`, { data: { ...request, visibility: 'PUBLIC' } })
    )
    return { slug: team.slug, name: team.name }
  } finally {
    await api.dispose()
  }
}

/**
 * Added by the platform admin rather than through `join`: joining needs the team to be marked
 * joinable, an admin-only setting a test would then depend on for no reason.
 */
async function ensureMember(admin: AuthResponse, member: AuthResponse, teamSlug: string) {
  const api = await apiContext(admin.accessToken)
  try {
    const request: AddMemberRequest = { userId: member.user.id, role: 'MEMBER' }
    await expectOk(await api.post(`/api/teams/${teamSlug}/members`, { data: request }))
  } catch (e) {
    if (!(e instanceof ApiError) || e.code !== 'ALREADY_REGISTERED') throw e
  } finally {
    await api.dispose()
  }
}
