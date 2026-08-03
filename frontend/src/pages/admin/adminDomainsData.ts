import type { QueryClient } from '@tanstack/react-query'
import { prefetchListDomainsQuery } from '@/api/endpoints/admin-domains/admin-domains'
import { adminDomainFiltersSchema } from '@/hooks/filters/adminFilters'
import { useAuthStore } from '@/store/authStore'

/**
 * Server-side counterpart of `AdminDomainsPage`, which reads its list through
 * `useUrlFilters({ schema: adminDomainFiltersSchema, alias: adminDomainFiltersAlias })`. No
 * `use<Screen>Data` hook here — the page already gets its filter state straight from
 * `useUrlFilters`, and there is nothing else for a companion hook to add.
 *
 * Known limitation, deliberately preserved (see SSR-data-loading.md): this primes
 * `adminDomainFiltersSchema.parse({})`, the **default** list (page 0, `ADMIN_PAGE_SIZE`), not the
 * URL's filters — same gap the other admin lists (`admin-teams`, `admin-users`) already carry. A
 * filtered admin URL server-renders the default page and refetches after hydration; it is not a
 * page/prefetch divergence, both sides agree on the default.
 *
 * Gated on `isAuthenticated` only — see `adminDashboardData.ts` for why (no platform-admin flag
 * in the store to check server-side; a signed-in non-admin's prefetch resolves to a 403).
 */
export async function prefetchAdminDomains(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  const filters = adminDomainFiltersSchema.parse({})
  await prefetchListDomainsQuery(queryClient, filters)
}
