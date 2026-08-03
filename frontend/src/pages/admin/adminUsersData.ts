import type { QueryClient } from '@tanstack/react-query'
import { useListUsers, prefetchListUsersQuery } from '@/api/endpoints/admin-users/admin-users'
import { prefetchListDomainsQuery } from '@/api/endpoints/admin-domains/admin-domains'
import { adminUserFiltersSchema } from '@/hooks/filters/adminFilters'
import { useAuthStore } from '@/store/authStore'
import { ADMIN_DOMAIN_FILTER_PARAMS } from './adminTeamsData'

/**
 * The one description of what `AdminUsersPage` reads: the same domain dropdown as
 * `AdminTeamsPage` (hence `ADMIN_DOMAIN_FILTER_PARAMS` imported from `adminTeamsData.ts` rather
 * than duplicated here) plus the user list. `useAdminUsersData` stays hook-shaped for the page's
 * `useUrlFilters`-driven `filters`/`setFilters`; `prefetchAdminUsers` is its server-side
 * counterpart.
 *
 * Per the documented limitation, the user list is prefetched at the schema's *default* filters
 * (`adminUserFiltersSchema.parse({})`: no `search`/`domainId`, `adminOnly: false`, `page: 0`), not
 * the URL's — a filtered, searched or paginated link still server-renders the default list and
 * refetches after hydration. The page also spreads `adminOnly: filters.adminOnly || undefined`
 * onto its own query, which only ever *drops* the key at its `false` default — `parse({})` already
 * yields that same default, so no extra projection is needed here to match.
 */
export function useAdminUsersData(filters: ReturnType<typeof adminUserFiltersSchema.parse>) {
  return useListUsers({ ...filters, adminOnly: filters.adminOnly || undefined })
}

/**
 * Gated on `useAuthStore.getState().isAuthenticated`, same as `adminTeamsData.ts`'s
 * `prefetchAdminTeams` — see its docblock for why `selectIsPlatformAdmin` isn't the gate here.
 */
export async function prefetchAdminUsers(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  const filters = adminUserFiltersSchema.parse({})
  await Promise.all([
    prefetchListDomainsQuery(queryClient, ADMIN_DOMAIN_FILTER_PARAMS),
    prefetchListUsersQuery(queryClient, { ...filters, adminOnly: filters.adminOnly || undefined }),
  ])
}
