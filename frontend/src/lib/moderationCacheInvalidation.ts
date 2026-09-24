import type { QueryClient } from '@tanstack/react-query'

/**
 * The list endpoints a report, a block or a moderation decision can change.
 *
 * The server filters them per reader: what you reported and what the people you blocked wrote
 * disappear from *your* lists, and a moderator's decision hides or restores content for everyone.
 * Detail endpoints are deliberately absent — a reported publication stays reachable by its link,
 * and invalidating the detail a page is still showing would refetch it for nothing.
 *
 * Matched on the generated key's URL (its first element) rather than listed per hook, because the
 * same content shows up in many lists (a team's publications, the home feed, the calendar, a
 * comment thread) and a missed one would keep showing what the reader just asked not to see.
 */
const MODERATED_LIST_KEYS: readonly RegExp[] = [
  // Home feed and its counters.
  /^\/api\/publications(\/count)?$/,
  // Routes across teams, and their map bounds.
  /^\/api\/routes(\/count|\/bounds)?$/,
  /^\/api\/calendar\/events$/,
  /^\/api\/users\/me\/participations$/,
  // A team's publications, ads, routes and calendar.
  /^\/api\/teams\/[^/]+\/(publications|classifieds|routes|calendar\/events)(\/count|\/bounds)?$/,
  // Every comment thread (top-level pages and expanded replies share this prefix).
  /^\/api\/teams\/[^/]+\/(rides|posts|trips|routes)\/[^/]+\/comments$/,
]

function isModeratedList(queryKey: readonly unknown[]): boolean {
  const url = queryKey[0]
  return typeof url === 'string' && MODERATED_LIST_KEYS.some((pattern) => pattern.test(url))
}

/** Refetches every list a report, a block or a moderation decision may have changed. */
export function invalidateModeratedContent(queryClient: QueryClient): Promise<void> {
  return queryClient.invalidateQueries({ predicate: (query) => isModeratedList(query.queryKey) })
}
