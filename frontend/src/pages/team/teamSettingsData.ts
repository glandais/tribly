import { useGetTeam } from '@/api/endpoints/teams/teams'

/**
 * `TeamSettingsPage` reads only the team — the form, the slug change and the delete confirmation
 * all work off `team` from this one query, mutations included (mutations don't need a companion,
 * they aren't part of SSR). That query is already covered server-side by `teamScopedPrefetch()`
 * on the `team-settings` route in `routes.config.ts`, which prefetches the same
 * `getGetTeamQueryKey` entry. There is no `prefetchTeamSettings` here: adding one would prefetch
 * the team a second time under the same key. This module exists only so the page's fetch and the
 * route's prefetch stay discoverable in one place, per the project's "always a companion"
 * convention.
 */
export function useTeamSettingsData(teamSlug: string | undefined) {
  return useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
}
