import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { PlusIcon, MapIcon } from '@heroicons/react/24/outline'
import { useRoutes, type RouteFilters } from '../../hooks/useRoute'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { RouteCard, RouteCardSkeleton } from '../../components/route/RouteCard'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'

export function RouteListPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation('routes')
  const pageSize = 20

  // Filter state - all in one object
  const [filters, setFilters] = useState<RouteFilters>({
    page: 0,
    size: pageSize,
  })
  const [filtersOpen, setFiltersOpen] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: routesData, isLoading: isLoadingRoutes } = useRoutes(teamSlug, filters)

  const handleFiltersChange = (newFilters: RouteFilters) => {
    setFilters({ ...newFilters, size: pageSize })
  }

  const handleSearchChange = (search: string) => {
    setFilters((prev) => ({
      ...prev,
      search: search || undefined,
      page: 0,
    }))
  }

  const handlePageChange = (page: number) => {
    setFilters((prev) => ({ ...prev, page }))
  }

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: routesData?.total ?? 0,
  })

  if (isLoadingTeam) {
    return <LoadingPage message={t('list.title')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreateRoute = team.role === 'ADMIN' || team.role === 'ORGANIZER'
  const hasFiltersOrSearch =
    filters.search ||
    filters.minDistance !== undefined ||
    filters.maxDistance !== undefined ||
    filters.minElevationGain !== undefined ||
    filters.maxElevationGain !== undefined ||
    filters.hilliness !== undefined ||
    (filters.surfaceTypes?.length ?? 0) > 0 ||
    filters.windDirection !== undefined

  return (
    <TeamLayout team={team} currentTab="routes">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{t('list.title')}</h2>
          </div>
          {canCreateRoute && (
            <Link
              to={paths.routeNew(teamSlug!)}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <PlusIcon className="w-4 h-4 mr-2" />
              {t('list.createRoute')}
            </Link>
          )}
        </div>

        {/* Search Input */}
        <SearchInput
          id="routes-search"
          value={filters.search ?? ''}
          onChange={handleSearchChange}
          placeholder={t('list.search.placeholder')}
          label={t('list.search.label')}
          className="mb-4"
        />

        {/* Filter Panel */}
        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
        />

        {/* Routes List */}
        {isLoadingRoutes ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <RouteCardSkeleton count={6} />
          </div>
        ) : routesData && routesData.routes.length > 0 ? (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {routesData.routes.map((route) => (
                <RouteCard key={route.id} route={route} showTeam={false} />
              ))}
            </div>

            <Pagination
              currentPage={filters.page ?? 0}
              totalPages={totalPages}
              onPageChange={handlePageChange}
              className="mt-8"
            />
          </>
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
            <MapIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {hasFiltersOrSearch ? t('list.noResults') : t('list.empty.title')}
            </h3>
            {!hasFiltersOrSearch && (
              <p className="mt-2 text-gray-500">{t('list.empty.description')}</p>
            )}
            {canCreateRoute && !hasFiltersOrSearch && (
              <Link
                to={paths.routeNew(teamSlug!)}
                className="mt-4 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
              >
                {t('list.empty.createAction')}
              </Link>
            )}
          </div>
        )}
      </div>
    </TeamLayout>
  )
}
