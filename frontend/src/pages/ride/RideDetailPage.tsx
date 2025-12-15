import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useTeam } from '../../hooks/useTeam';
import { useRide, useUpdateRide, useDeleteRide, useJoinRide, useLeaveRide, RideStatus } from '../../hooks/useRide';
import { useAuth } from '../../hooks/useAuth';
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner';
import { RideGroupCard } from '../../components/ride/RideGroupCard';

const statusColors: Record<RideStatus, string> = {
  DRAFT: 'bg-gray-100 text-gray-800',
  PUBLISHED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
  COMPLETED: 'bg-blue-100 text-blue-800',
};

export function RideDetailPage() {
  const { t, i18n } = useTranslation('rides');
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>();
  const { isAuthenticated } = useAuth();
  const [joiningGroupId, setJoiningGroupId] = useState<string | null>(null);

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug);
  const { data: ride, isLoading: isLoadingRide, error } = useRide(teamSlug, rideSlug);

  const updateMutation = useUpdateRide(teamSlug, rideSlug!);
  const deleteMutation = useDeleteRide(teamSlug);
  const joinMutation = useJoinRide(teamSlug, rideSlug!);
  const leaveMutation = useLeaveRide(teamSlug, rideSlug!);

  if (isLoadingTeam || isLoadingRide) {
    return <LoadingPage message={t('loading')} />;
  }

  if (error || !ride) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('detail.notFound.title')}</h1>
          <p className="text-gray-600 mb-6">{t('detail.notFound.message')}</p>
          <Link
            to={`/teams/${teamSlug}/rides`}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('detail.notFound.backToRides')}
          </Link>
        </div>
      </div>
    );
  }

  const isMember = !!team?.userRole;
  const isAdmin = team?.userRole === 'ADMIN';
  const isOrganizer = team?.userRole === 'ORGANIZER';
  const canEdit = isAdmin || isOrganizer;
  const canJoinRide = isMember && ride.status === 'PUBLISHED';

  const rideDate = new Date(ride.date);
  const formattedDate = rideDate.toLocaleDateString(i18n.language, {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  const handlePublish = () => {
    updateMutation.mutate({ status: 'PUBLISHED' });
  };

  const handleUnpublish = () => {
    if (confirm(t('detail.confirmations.unpublish'))) {
      updateMutation.mutate({ status: 'DRAFT' });
    }
  };

  const handleCancel = () => {
    if (confirm(t('detail.confirmations.cancel'))) {
      updateMutation.mutate({ status: 'CANCELLED' });
    }
  };

  const handleDelete = () => {
    if (confirm(t('detail.confirmations.delete'))) {
      deleteMutation.mutate(rideSlug!);
    }
  };

  const handleJoinGroup = (groupId: string) => {
    setJoiningGroupId(groupId);
    joinMutation.mutate(
      { groupId },
      {
        onSettled: () => setJoiningGroupId(null),
      }
    );
  };

  const handleLeaveGroup = (groupId: string) => {
    setJoiningGroupId(groupId);
    leaveMutation.mutate(groupId, {
      onSettled: () => setJoiningGroupId(null),
    });
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Breadcrumb */}
      <div className="mb-6">
        <nav className="flex items-center space-x-2 text-sm text-gray-500">
          <Link to={`/teams/${teamSlug}`} className="hover:text-gray-700">
            {team?.name}
          </Link>
          <span>/</span>
          <Link to={`/teams/${teamSlug}/rides`} className="hover:text-gray-700">
            {t('breadcrumb.rides')}
          </Link>
          <span>/</span>
          <span className="text-gray-900">{ride.title}</span>
        </nav>
      </div>

      {/* Header */}
      <div className="bg-white rounded-lg shadow border border-gray-200 p-6 mb-6">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold text-gray-900">{ride.title}</h1>
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[ride.status]}`}>
                {t(`status.${ride.status}`)}
              </span>
            </div>
            {ride.description && (
              <p className="mt-2 text-gray-600">{ride.description}</p>
            )}
            {ride.status === 'DRAFT' && ride.publishAt && (
              <div className="mt-2 text-sm text-amber-600 flex items-center">
                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                {t('detail.scheduledPublish', {
                  date: new Date(ride.publishAt).toLocaleString(i18n.language, {
                    dateStyle: 'medium',
                    timeStyle: 'short',
                  }),
                })}
              </div>
            )}
            <div className="mt-4 flex flex-wrap items-center gap-4 text-sm text-gray-500">
              <span className="flex items-center">
                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                {formattedDate}
              </span>
              {ride.startTime && (
                <span className="flex items-center">
                  <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  {ride.startTime.substring(0, 5)}
                </span>
              )}
              <span className="flex items-center">
                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                {t('card.participantCount', { count: ride.participantCount })}
              </span>
            </div>
          </div>

          {canEdit && (
            <div className="flex items-center gap-2">
              <Link
                to={`/teams/${teamSlug}/rides/${rideSlug}/edit`}
                className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
              >
                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
                {t('detail.actions.edit')}
              </Link>
              {ride.status === 'DRAFT' && (
                <button
                  onClick={handlePublish}
                  disabled={updateMutation.isPending}
                  className="inline-flex items-center px-3 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-green-600 hover:bg-green-700 disabled:opacity-50"
                >
                  {updateMutation.isPending ? <LoadingSpinner size="sm" className="mr-2" /> : null}
                  {t('detail.actions.publish')}
                </button>
              )}
              {ride.status === 'PUBLISHED' && (
                <>
                  <button
                    onClick={handleUnpublish}
                    disabled={updateMutation.isPending}
                    className="inline-flex items-center px-3 py-2 border border-yellow-300 rounded-md text-sm font-medium text-yellow-700 bg-white hover:bg-yellow-50 disabled:opacity-50"
                  >
                    {t('detail.actions.unpublish')}
                  </button>
                  <button
                    onClick={handleCancel}
                    disabled={updateMutation.isPending}
                    className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
                  >
                    {t('detail.actions.cancel')}
                  </button>
                </>
              )}
              <button
                onClick={handleDelete}
                disabled={deleteMutation.isPending}
                className="inline-flex items-center px-3 py-2 border border-red-300 rounded-md text-sm font-medium text-red-700 bg-white hover:bg-red-50 disabled:opacity-50"
              >
                {t('detail.actions.delete')}
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Groups */}
      <div className="mb-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">{t('detail.groups.title')}</h2>
        {ride.groups && ride.groups.length > 0 ? (
          <div className="space-y-3">
            {ride.groups.map((group) => (
              <RideGroupCard
                key={group.id}
                group={group}
                canJoin={canJoinRide}
                onJoin={() => handleJoinGroup(group.id)}
                onLeave={() => handleLeaveGroup(group.id)}
                isLoading={joiningGroupId === group.id && (joinMutation.isPending || leaveMutation.isPending)}
              />
            ))}
          </div>
        ) : (
          <p className="text-gray-500">{t('detail.groups.empty')}</p>
        )}
      </div>

      {/* Info for non-members */}
      {!isMember && isAuthenticated && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <p className="text-yellow-800">
            {t('detail.nonMember.message')}{' '}
            <Link to={`/teams/${teamSlug}`} className="font-medium underline">
              {t('detail.nonMember.viewTeam')}
            </Link>
          </p>
        </div>
      )}

      {!isAuthenticated && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <p className="text-blue-800">
            {t('detail.notAuthenticated.message')}{' '}
            <Link to="/login" className="font-medium underline">
              {t('detail.notAuthenticated.signIn')}
            </Link>
          </p>
        </div>
      )}
    </div>
  );
}
