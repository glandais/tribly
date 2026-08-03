import type { QueryClient } from '@tanstack/react-query'
import {
  useAdminListTeams,
  prefetchAdminListTeamsQuery,
} from '@/api/endpoints/admin-teams/admin-teams'
import { prefetchListDomainsQuery } from '@/api/endpoints/admin-domains/admin-domains'
import { adminTeamFiltersSchema } from '@/hooks/filters/adminFilters'
import { useAuthStore } from '@/store/authStore'

/**
 * The one description of what `AdminTeamsPage` reads: the domain dropdown's options and the team
 * list itself. `useAdminTeamsData` stays hook-shaped for the page's `useUrlFilters`-driven
 * `filters`/`setFilters`, `prefetchAdminTeams` is its server-side counterpart.
 *
 * `ADMIN_DOMAIN_FILTER_PARAMS` — `{ page: 0, size: 100 }` — is the domain dropdown's own key,
 * shared verbatim with `adminUsersData.ts` (same dropdown, same screen family) so both prefetch
 * the identical `/admin/domains` entry instead of two near-duplicates. It is deliberately *not*
 * `adminDomainFiltersSchema.parse({})` (`size: 20`, the domains **list** screen's own key,
 * companion `adminDomainsData.ts`) — different screen, different page size, different cache entry.
 *
 * Per the documented limitation, the team list itself is prefetched at the schema's *default*
 * filters (`adminTeamFiltersSchema.parse({})`, i.e. no `domainId`, `page: 0`), not the URL's —
 * `AdminTeamsPage` never went through `readUrlFilters` here either (it reads its own filters via
 * `useUrlFilters`, unlike the public lists' `prefetchPageWindow`/`readUrlFilters` pair), so a
 * filtered or paginated link still server-renders the default list and refetches after hydration.
 */
export const ADMIN_DOMAIN_FILTER_PARAMS = { page: 0, size: 100 } as const

export function useAdminTeamsData(filters: ReturnType<typeof adminTeamFiltersSchema.parse>) {
  return useAdminListTeams(filters)
}

/**
 * Gated on `useAuthStore.getState().isAuthenticated`, the same signal `profileData.ts` uses — not
 * on `selectIsPlatformAdmin`. `AdminLayout` is what actually gate-keeps the screen (redirects a
 * non-admin to `paths.home()` client-side); the backend resource is `@RolesAllowed`-protected on
 * top of that, so an authenticated-but-not-admin visitor's prefetch attempt is rejected server-side
 * exactly as a direct call to it would be — this mirrors that, not bypasses it.
 */
export async function prefetchAdminTeams(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  await Promise.all([
    prefetchListDomainsQuery(queryClient, ADMIN_DOMAIN_FILTER_PARAMS),
    prefetchAdminListTeamsQuery(queryClient, adminTeamFiltersSchema.parse({})),
  ])
}
