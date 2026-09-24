import type { QueryClient } from '@tanstack/react-query'
import {
  useListTeamReports,
  prefetchListTeamReportsQuery,
} from '@/api/endpoints/moderation/moderation'
import { reportFiltersSchema, reportFiltersAlias } from '@/hooks/filters/reportFilters'
import { useUrlFilters, readUrlFilters } from '@/hooks/useUrlFilters'

/**
 * `team-admin-reports`: the team's moderation queue, `OPEN` or `RESOLVED` from the query string.
 * The page's `useUrlFilters` and the route's prefetch read the status through the same options,
 * so both land on the same `listTeamReports(teamSlug, { status })` key.
 */
export const teamReportFilterOptions = {
  schema: reportFiltersSchema,
  alias: reportFiltersAlias,
} as const

export function useTeamReportsData(teamSlug: string | undefined, enabled: boolean) {
  const { filters, setFilters } = useUrlFilters(teamReportFilterOptions)
  const reports = useListTeamReports(teamSlug!, filters, {
    query: { enabled: enabled && !!teamSlug },
  })
  return { filters, setFilters, reports }
}

export async function prefetchTeamReports(
  queryClient: QueryClient,
  teamSlug: string,
  url: URL
): Promise<void> {
  const filters = readUrlFilters(url.searchParams, teamReportFilterOptions)
  await prefetchListTeamReportsQuery(queryClient, teamSlug, filters)
}
