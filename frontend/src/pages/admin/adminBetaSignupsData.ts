import type { QueryClient } from '@tanstack/react-query'
import { prefetchListBetaSignupsQuery } from '@/api/endpoints/admin-beta-signups/admin-beta-signups'
import { adminBetaSignupFiltersSchema } from '@/hooks/filters/adminFilters'
import { useAuthStore } from '@/store/authStore'

/**
 * Server-side counterpart of `AdminBetaSignupsPage`, which reads its list through
 * `useUrlFilters({ schema: adminBetaSignupFiltersSchema, alias: adminBetaSignupFiltersAlias })`.
 * No `use<Screen>Data` hook here for the same reason as `adminDomainsData.ts` — the page's own
 * `useUrlFilters` call already gives it everything it reads.
 *
 * Known limitation, deliberately preserved (see SSR-data-loading.md): this primes
 * `adminBetaSignupFiltersSchema.parse({})`, the **default** list (page 0, `ADMIN_PAGE_SIZE`), not
 * the URL's filters — the same gap the other admin lists carry, not a page/prefetch divergence.
 *
 * Gated on `isAuthenticated` only — see `adminDashboardData.ts` for why.
 */
export async function prefetchAdminBetaSignups(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  const filters = adminBetaSignupFiltersSchema.parse({})
  await prefetchListBetaSignupsQuery(queryClient, filters)
}
