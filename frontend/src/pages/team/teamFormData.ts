import type { QueryClient } from '@tanstack/react-query'
import { useListTeams, prefetchListTeamsQuery } from '@/api/endpoints/teams/teams'
import { useAuthStore } from '@/store/authStore'

/**
 * The one description of what `CreateTeamPage` reads, consumed two ways: `CreateTeamPage` calls
 * {@link useTeamFormData} for the query result, the `teams-new` route in `routes.config.ts` calls
 * {@link prefetchTeamForm} for the same data server-side. Its own module so `routes.config.ts`,
 * which imports it eagerly, doesn't pull the page (or `TeamForm`) out of its lazy chunk.
 *
 * `TEAM_EXISTENCE_PROBE_PARAMS` is the "do you already have a team" probe the page redirects on
 * when `config.singleTeam` is set — it wants a single row, not a count, so `size: 1` is enough.
 * Do NOT confuse this with `resolveMembershipDefault`'s `{ minRole: MEMBER, page: 0, size: 1 }`
 * probe in `prefetchHelpers`: same shape, different key, because that one carries `minRole` and
 * this one doesn't.
 */
export const TEAM_EXISTENCE_PROBE_PARAMS = { page: 0, size: 1 }

/** Authenticated only — an anonymous visitor can't have a team, and the page never calls this
 * hook signed out either (the route itself gates on `auth: 'authenticated'`). */
export function useTeamFormData() {
  return useListTeams(TEAM_EXISTENCE_PROBE_PARAMS)
}

/**
 * Server-side counterpart of {@link useTeamFormData}: gated the same way `profileData.ts` gates
 * its participation counts, since there is no `enabled` hook option available on the server.
 */
export async function prefetchTeamForm(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  await prefetchListTeamsQuery(queryClient, TEAM_EXISTENCE_PROBE_PARAMS)
}
