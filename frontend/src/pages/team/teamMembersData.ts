import { useCallback } from 'react'
import type { QueryClient } from '@tanstack/react-query'
import {
  useGetMembers,
  getMembers,
  getGetMembersQueryKey,
  prefetchGetMembersQuery,
} from '@/api/endpoints/team-members/team-members'
import { prefetchListInvitationsQuery } from '@/api/endpoints/team-invitations/team-invitations'
import { InvitationStatus } from '@/api/dto'
import { teamMemberFiltersSchema, teamMemberFiltersAlias } from '@/hooks/filters/teamMemberFilters'
import { useUrlFilters } from '@/hooks/useUrlFilters'
import { usePaginatedQuery } from '@/hooks/usePaginatedQuery'

/**
 * `team-members` admin screen: the member list the page owns, plus the pending-invitations query
 * `TeamInvitationList` (a child it always mounts) owns. Both sides share the filter schema/alias
 * pair and the invitation params so the prefetch and the hooks land on the same query keys.
 *
 * KNOWN GAP, left as-is on purpose: the prefetch calls `teamMemberFiltersSchema.parse({})` — the
 * *default* filters — rather than reading the URL's query string. A link into a filtered member
 * list (`?search=…` or `?role=ADMIN`) server-renders the unfiltered list and refetches after
 * hydration. A separate audit will decide whether to fix this; don't fix it here.
 */
export const teamMemberFilterOptions = {
  schema: teamMemberFiltersSchema,
  alias: teamMemberFiltersAlias,
} as const

/** Params for the pending-invitations query `TeamInvitationList` renders — part of its key. */
export const pendingInvitationsParams = { status: InvitationStatus.PENDING } as const

/**
 * The team's own member list: the filter state (the page needs the setters for its Select/
 * SearchInput controls), the raw `useGetMembers` result, and the neighbouring pages
 * `usePaginatedQuery` fetches ahead — same shape as `useRouteListData` in `routeListData.ts`.
 */
export function useTeamMembersData(teamSlug?: string) {
  const { filters, setFilters } = useUrlFilters(teamMemberFilterOptions)

  const members = useGetMembers(teamSlug!, filters, { query: { enabled: !!teamSlug } })

  // The neighbouring pages, on the same key `prefetchPage`/`usePaginatedQuery` primes client-side.
  const prefetchPage = useCallback(
    (page: number) => ({
      queryKey: getGetMembersQueryKey(teamSlug!, { ...filters, page }),
      queryFn: () => getMembers(teamSlug!, { ...filters, page }),
    }),
    [teamSlug, filters]
  )
  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
    totalItems: members.data?.total ?? 0,
    prefetchPage,
  })

  return { filters, setFilters, members, totalPages }
}

export async function prefetchTeamMembers(queryClient: QueryClient, teamSlug: string) {
  await Promise.all([
    prefetchGetMembersQuery(queryClient, teamSlug, teamMemberFiltersSchema.parse({})),
    // Prefetched here because `TeamInvitationList` — a child the page always mounts — owns this
    // query itself; the page's own hooks never touch it.
    prefetchListInvitationsQuery(queryClient, teamSlug, pendingInvitationsParams),
  ])
}
