import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useRoutes } from '../../hooks/useRoute'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { RouteCard, RouteCardSkeleton } from '../../components/route/RouteCard'

export function RouteListPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation('routes')

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: routesData, isLoading: isLoadingRoutes } = useRoutes(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('list.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
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
              to={`/teams/${teamSlug}/routes/new`}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 4v16m8-8H4"
                />
              </svg>
              {t('list.createRoute')}
            </Link>
          )}
        </div>

        {/* Routes List */}
        {isLoadingRoutes ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <RouteCardSkeleton count={6} />
          </div>
        ) : routesData && routesData.routes.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {routesData.routes.map((route) => (
              <RouteCard key={route.id} route={route} teamSlug={teamSlug!} />
            ))}
          </div>
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
            <svg
              className="mx-auto h-12 w-12 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={1.5}
                d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
              />
            </svg>
            <h3 className="mt-4 text-lg font-medium text-gray-900">{t('list.empty.title')}</h3>
            <p className="mt-2 text-gray-500">{t('list.empty.description')}</p>
            {canCreateRoute && (
              <Link
                to={`/teams/${teamSlug}/routes/new`}
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
