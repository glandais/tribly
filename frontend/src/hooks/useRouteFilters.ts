import { useState } from 'react'
import { useUrlFilters } from './useUrlFilters'
import { routeFiltersSchema, routeFiltersAlias } from './filters/routeFilters'

export function useRouteFilters() {
  const [filtersOpen, setFiltersOpen] = useState(false)
  const { filters, setFilters, replaceFilters } = useUrlFilters({
    schema: routeFiltersSchema,
    alias: routeFiltersAlias,
  })

  const hasFiltersOrSearch: boolean =
    (filters.search ? true : false) ||
    filters.minDistance !== undefined ||
    filters.maxDistance !== undefined ||
    filters.minElevationGain !== undefined ||
    filters.maxElevationGain !== undefined ||
    filters.hilliness !== undefined ||
    filters.surfaceType !== undefined ||
    filters.windDirection !== undefined

  return {
    filters,
    // RouteFilterPanel's "clear" hands back an object with the cleared keys
    // omitted, so this must replace rather than merge.
    handleFiltersChange: replaceFilters,
    handlePageChange: (page: number) => setFilters({ page }),
    filtersOpen,
    setFiltersOpen,
    hasFiltersOrSearch,
    pageSize: filters.size,
  }
}
