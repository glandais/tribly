import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { MapIcon } from '@heroicons/react/24/outline'
import { useListAllRoutes } from '@/api/endpoints/routes/routes'
import type { ListAllRoutesParams } from '@/api/dto'
import { RouteCard, RouteCardSkeleton } from '../../components/route/RouteCard'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { HomeLayout } from '../../components/home/HomeLayout'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'

export function AllRoutesPage() {
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')
  const pageSize = 20

  // Filter state - all in one object
  const [filters, setFilters] = useState<ListAllRoutesParams>({
    page: 0,
    size: pageSize,
  })
  const [filtersOpen, setFiltersOpen] = useState(false)

  const {
    data: routesData,
    isLoading,
    isError,
  } = useListAllRoutes(filters, {
    query: { placeholderData: keepPreviousData },
  })

  const handleFiltersChange = (newFilters: ListAllRoutesParams) => {
    setFilters({ ...newFilters, size: pageSize })
  }

  const handlePageChange = (page: number) => {
    setFilters((prev) => ({ ...prev, page }))
  }

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: routesData?.total ?? 0,
  })

  const hasFiltersOrSearch =
    filters.search ||
    filters.minDistance !== undefined ||
    filters.maxDistance !== undefined ||
    filters.minElevationGain !== undefined ||
    filters.maxElevationGain !== undefined ||
    filters.hilliness !== undefined ||
    filters.surfaceType !== undefined ||
    filters.windDirection !== undefined

  return (
    <HomeLayout currentTab="routes">
      {/* Routes Section */}
      <div className="mt-6 mb-6">
        <h2 className="text-2xl font-bold text-gray-900">{t('list.title')}</h2>
      </div>

      {/* Filter Panel */}
      <RouteFilterPanel
        filters={filters}
        onFiltersChange={handleFiltersChange}
        isOpen={filtersOpen}
        onOpenChange={setFiltersOpen}
      />

      {/* Loading State */}
      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <RouteCardSkeleton count={6} />
        </div>
      ) : isError ? (
        /* Error State */
        <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
          <MapIcon className="mx-auto h-12 w-12 text-red-400" />
          <h3 className="mt-4 text-lg font-medium text-gray-900">{tCommon('error.loading')}</h3>
        </div>
      ) : routesData?.routes && routesData.routes.length > 0 ? (
        /* Routes Grid */
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {routesData.routes.map((route) => {
              return <RouteCard key={route.id} route={route} showTeam={true} />
            })}
          </div>

          <Pagination
            currentPage={filters.page ?? 0}
            totalPages={totalPages}
            onPageChange={handlePageChange}
            className="mt-8"
          />
        </>
      ) : (
        /* Empty State */
        <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
          <MapIcon className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-4 text-lg font-medium text-gray-900">
            {hasFiltersOrSearch ? t('list.noResults') : t('list.empty.title')}
          </h3>
          {!hasFiltersOrSearch && (
            <p className="mt-2 text-gray-500">{t('list.empty.description')}</p>
          )}
        </div>
      )}
    </HomeLayout>
  )
}
