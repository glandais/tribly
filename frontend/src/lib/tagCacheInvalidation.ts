import type { QueryClient } from '@tanstack/react-query'
import { getListTeamTagsQueryKey } from '@/api/endpoints/tags/tags'

/**
 * Below `/api/teams/{slug}/`: the lists and details that render a tag (and the counts a `?tags=`
 * filter narrows). Comment threads, route geometry, `.ics` and the like carry none, and refetching
 * them would cost for nothing.
 */
const TEAM_TAGGED_KEYS: readonly RegExp[] = [
  /^(publications|classifieds|routes|ride-templates)(\/count)?$/,
  /^(rides|posts|trips|routes|classifieds|ride-templates)\/[^/]+(\/edit)?$/,
]

/** Lists across teams: no tag filter there (plan D7), but their rows show the tags. */
const CROSS_TEAM_TAGGED_KEYS: readonly string[] = ['/api/publications', '/api/routes']

function carriesTeamTags(queryKey: readonly unknown[], teamSlug: string): boolean {
  const url = queryKey[0]
  if (typeof url !== 'string') return false
  if (CROSS_TEAM_TAGGED_KEYS.includes(url)) return true
  const prefix = `/api/teams/${teamSlug}/`
  if (!url.startsWith(prefix)) return false
  const rest = url.slice(prefix.length)
  return TEAM_TAGGED_KEYS.some((pattern) => pattern.test(rest))
}

/**
 * Refetches the team's tag vocabulary and every cached content that may show one of its tags —
 * after a tag is renamed, recoloured or deleted (docs/LEDGER_*.md WEB-40). Without the contents, a
 * card would keep a deleted tag for the query's stale time, and an edit form opened from that cache
 * would send its id back, which the API refuses (`TAG_INVALID`).
 *
 * Matched on the generated key's URL (its first element), like `invalidateModeratedContent`.
 */
export function invalidateTeamTags(queryClient: QueryClient, teamSlug: string): Promise<void> {
  return queryClient.invalidateQueries({
    predicate: (query) =>
      query.queryKey[0] === getListTeamTagsQueryKey(teamSlug)[0] ||
      carriesTeamTags(query.queryKey, teamSlug),
  })
}
