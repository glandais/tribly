import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useTeam } from '../../hooks/useTeam'
import { useRides } from '../../hooks/useRide'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideCard, RideCardSkeleton } from '../../components/ride/RideCard'
import { TeamLayout } from '../../components/team/TeamLayout'

export function RideListPage() {
  const { t } = useTranslation('rides')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: ridesData, isLoading: isLoadingRides } = useRides(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('list.notFound.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  return (
    <TeamLayout team={team} currentTab="rides">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{t('list.title')}</h2>
          </div>
          {canCreate && (
            <Link
              to={`/teams/${teamSlug}/rides/new`}
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
              {t('list.createRide')}
            </Link>
          )}
        </div>

        {/* Rides List */}
        {isLoadingRides ? (
          <div className="space-y-4">
            {[...Array(3)].map((_, i) => (
              <RideCardSkeleton key={i} />
            ))}
          </div>
        ) : ridesData?.rides && ridesData.rides.length > 0 ? (
          <div className="space-y-4">
            {ridesData.rides.map((ride) => (
              <RideCard key={ride.id} ride={ride} teamSlug={teamSlug!} />
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
                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
            <h3 className="mt-4 text-lg font-medium text-gray-900">{t('list.empty.title')}</h3>
            <p className="mt-2 text-gray-500">
              {canCreate ? t('list.empty.admin') : t('list.empty.member')}
            </p>
            {canCreate && (
              <Link
                to={`/teams/${teamSlug}/rides/new`}
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
