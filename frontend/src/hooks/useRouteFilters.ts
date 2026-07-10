import { useState } from 'react'
import type { z } from 'zod'
import { useUrlFilters, type UseUrlFiltersOptions } from './useUrlFilters'
import type { RouteFilters } from './filters/routeFilters'

/**
 * Shared by the team route list and the cross-team one, whose schema also carries a membership
 * filter — hence the schema is a parameter. Any schema passed here extends the route shape.
 */
export function useRouteFilters<S extends z.ZodObject<z.ZodRawShape>>(
  options: UseUrlFiltersOptions<S>
) {
  const [filtersOpen, setFiltersOpen] = useState(false)
  const { filters, setFilters, replaceFilters } = useUrlFilters(options)
  // The generic schema hides the route shape from the type system, but every caller passes one.
  const routeFilters = filters as RouteFilters
  const patchRouteFilters = setFilters as unknown as (patch: Partial<RouteFilters>) => void

  const hasFiltersOrSearch: boolean =
    (routeFilters.search ? true : false) ||
    routeFilters.minDistance !== undefined ||
    routeFilters.maxDistance !== undefined ||
    routeFilters.minElevationGain !== undefined ||
    routeFilters.maxElevationGain !== undefined ||
    routeFilters.hilliness !== undefined ||
    routeFilters.surfaceType !== undefined ||
    routeFilters.windDirection !== undefined

  return {
    filters: filters as z.infer<S> & RouteFilters,
    setFilters,
    // RouteFilterPanel hands back the whole filter set with the cleared keys reset, so this must
    // replace rather than merge.
    handleFiltersChange: replaceFilters,
    handlePageChange: (page: number) => patchRouteFilters({ page }),
    filtersOpen,
    setFiltersOpen,
    hasFiltersOrSearch,
    pageSize: routeFilters.size,
  }
}
