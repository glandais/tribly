import type { QueryClient } from '@tanstack/react-query'
import {
  useListAdminReports,
  prefetchListAdminReportsQuery,
} from '@/api/endpoints/admin-reports/admin-reports'
import { reportFiltersSchema, reportFiltersAlias } from '@/hooks/filters/reportFilters'
import { useUrlFilters, readUrlFilters } from '@/hooks/useUrlFilters'
import { useAuthStore } from '@/store/authStore'

/**
 * `admin-reports`: the platform moderation queue, `OPEN` or `RESOLVED` from the query string. Same
 * filter options on both sides, so the page and the prefetch share the `listAdminReports` key.
 *
 * Gated on `isAuthenticated` only — see `adminDashboardData.ts` for why.
 */
export const adminReportFilterOptions = {
  schema: reportFiltersSchema,
  alias: reportFiltersAlias,
} as const

export function useAdminReportsData() {
  const { filters, setFilters } = useUrlFilters(adminReportFilterOptions)
  const reports = useListAdminReports(filters)
  return { filters, setFilters, reports }
}

export async function prefetchAdminReports(queryClient: QueryClient, url: URL): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  const filters = readUrlFilters(url.searchParams, adminReportFilterOptions)
  await prefetchListAdminReportsQuery(queryClient, filters)
}
