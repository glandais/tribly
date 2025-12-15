import { Link, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useTeam } from '../../hooks/useTeam';
import { useRides } from '../../hooks/useRide';
import { useAuth } from '../../hooks/useAuth';
import { LoadingPage } from '../../components/common/LoadingSpinner';
import { RideCard, RideCardSkeleton } from '../../components/ride/RideCard';

export function RideListPage() {
  const { t } = useTranslation('rides');
  const { teamSlug } = useParams<{ teamSlug: string }>();
  const { isAuthenticated } = useAuth();
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug);
  const { data: ridesData, isLoading: isLoadingRides } = useRides(teamSlug);

  if (isLoadingTeam) {
    return <LoadingPage message={t('list.notFound.title')} />;
  }

  if (!team) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('list.notFound.title')}</h1>
          <Link
            to="/teams"
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('list.notFound.backToTeams')}
          </Link>
        </div>
      </div>
    );
  }

  const isMember = !!team.userRole;
  const canCreate = team.userRole === 'ADMIN' || team.userRole === 'ORGANIZER';

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          {team.name}
        </Link>
        <div className="mt-4 flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{t('list.title')}</h1>
            <p className="mt-1 text-gray-600">{t('list.subtitle', { teamName: team.name })}</p>
          </div>
          {canCreate && (
            <Link
              to={`/teams/${teamSlug}/rides/new`}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              {t('list.createRide')}
            </Link>
          )}
        </div>
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
        <div className="text-center py-12 bg-white rounded-lg shadow border border-gray-200">
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
            {canCreate
              ? t('list.empty.admin')
              : isMember
                ? t('list.empty.member')
                : isAuthenticated
                  ? t('detail.nonMember.message')
                  : t('detail.notAuthenticated.message')}
          </p>
          {canCreate && (
            <Link
              to={`/teams/${teamSlug}/rides/new`}
              className="mt-4 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              {t('list.empty.createAction')}
            </Link>
          )}
        </div>
      )}
    </div>
  );
}
