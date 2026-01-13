import { useState, useCallback } from 'react'
import type { ListRoutesParams, ListAllRoutesParams } from '@/api/dto'
import {
  DEFAULT_ROUTE_SORT_BY,
  DEFAULT_ROUTE_SORT_DIR,
} from '../components/route/RouteFilterPanel'

type RouteFilters = ListRoutesParams | ListAllRoutesParams

interface UseRouteFiltersOptions {
  pageSize?: number
}

export function useRouteFilters<T extends RouteFilters>({ pageSize = 12 }: UseRouteFiltersOptions = {}) {
  const [filters, setFilters] = useState<T>({
    page: 0,
    size: pageSize,
    sortBy: DEFAULT_ROUTE_SORT_BY,
    sortDir: DEFAULT_ROUTE_SORT_DIR,
  } as T)
  const [filtersOpen, setFiltersOpen] = useState(false)

  const handleFiltersChange = useCallback(
    (newFilters: T) => {
      setFilters({ ...newFilters, size: pageSize })
    },
    [pageSize]
  )

  const handlePageChange = useCallback((page: number) => {
    setFilters((prev) => ({ ...prev, page }))
  }, [])

  const hasFiltersOrSearch =
    filters.search ||
    filters.minDistance !== undefined ||
    filters.maxDistance !== undefined ||
    filters.minElevationGain !== undefined ||
    filters.maxElevationGain !== undefined ||
    filters.hilliness !== undefined ||
    filters.surfaceType !== undefined ||
    filters.windDirection !== undefined

  return {
    filters,
    setFilters,
    filtersOpen,
    setFiltersOpen,
    handleFiltersChange,
    handlePageChange,
    hasFiltersOrSearch,
    pageSize,
  }
}
