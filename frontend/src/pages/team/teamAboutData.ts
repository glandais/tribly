import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam, prefetchGetTeamQuery } from '@/api/endpoints/teams/teams'

/**
 * `TeamAboutPage` reads a single query — the team itself, keyed on `teamSlug` — and this module
 * exists so that fetch and its server-side prefetch stay declared in one place rather than as two
 * separate calls to `useGetTeam`/`prefetchGetTeamQuery` that could drift apart. This is a public
 * route (unlike the admin screens that wrap their prefetch in `teamScopedPrefetch`), so the
 * prefetch here is unconditional.
 */
export function useTeamAboutData(teamSlug?: string) {
  return useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
}

/** Server-side counterpart of {@link useTeamAboutData}. */
export async function prefetchTeamAbout(queryClient: QueryClient, teamSlug: string): Promise<void> {
  await prefetchGetTeamQuery(queryClient, teamSlug)
}
