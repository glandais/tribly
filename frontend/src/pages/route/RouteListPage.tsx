import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { PlusIcon, MapIcon } from '@heroicons/react/24/outline'
import { useRoutes } from '../../hooks/useRoute'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { RouteCard, RouteCardSkeleton } from '../../components/route/RouteCard'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'

export function RouteListPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation('routes')
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const pageSize = 20

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: routesData, isLoading: isLoadingRoutes } = useRoutes(
    teamSlug,
    page,
    pageSize,
    search || undefined
  )

  const resetPage = () => setPage(0)

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
          value={search}
          onChange={(value) => {
            setSearch(value)
            resetPage()
          }}
          placeholder={t('list.search.placeholder')}
          label={t('list.search.label')}
          className="mb-6"
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
                <RouteCard key={route.id} route={route} teamSlug={teamSlug!} />
              ))}
            </div>

            <Pagination
              currentPage={page}
              totalPages={totalPages}
              onPageChange={setPage}
              className="mt-8"
            />
          </>
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
            <MapIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {search ? t('list.noResults') : t('list.empty.title')}
            </h3>
            {!search && <p className="mt-2 text-gray-500">{t('list.empty.description')}</p>}
            {canCreateRoute && !search && (
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
