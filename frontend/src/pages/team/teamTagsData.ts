import type { QueryClient } from '@tanstack/react-query'
import { z } from 'zod'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useListTeamTags, prefetchListTeamTagsQuery } from '@/api/endpoints/tags/tags'

/**
 * The one description of what `TeamTagsPage` reads, consumed two ways: the page calls
 * {@link useTeamTagsData}, the `team-admin-tags` route in `routes.config.ts` calls
 * {@link prefetchTeamTagsAdmin} for the same data server-side.
 *
 * The screen reads the whole vocabulary — every kind at once, `GET /tags` without `type` — rather
 * than one kind per tab: switching tabs then costs no request, each tab can show its count, and the
 * prefetch does not depend on the tab in the URL. At most 100 tags per kind (plan D16) keeps that
 * one list small. It is a different key from `TagFilter`'s and `TagPicker`'s (`{ type }`); a
 * mutation invalidates them all through their common prefix, `getListTeamTagsQueryKey(teamSlug)`.
 *
 * The team itself comes from the `teamScopedPrefetch` wrapper, as for the other admin tabs.
 */
export function useTeamTagsData(teamSlug: string | undefined) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const tags = useListTeamTags(teamSlug!, undefined, { query: { enabled: !!teamSlug } })
  return { team, tags }
}

/** Server-side counterpart of {@link useTeamTagsData}'s screen-specific query. */
export async function prefetchTeamTagsAdmin(queryClient: QueryClient, teamSlug: string) {
  await prefetchListTeamTagsQuery(queryClient, teamSlug)
}

/**
 * The open tab, in the query string (`?type=route`) so a link lands on it. Lower case like the
 * other enum-valued URL params; absent or unknown, the page opens its first tab. It only picks the
 * tab — the data is the same for all of them — so the prefetch never reads it.
 */
export const teamTagsTabOptions = {
  schema: z.object({
    type: z.enum(['ride', 'post', 'trip', 'route', 'ad']).optional().catch(undefined),
  }),
} as const
