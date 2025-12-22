import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ClockIcon, CalendarIcon, UsersIcon, PencilIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import {
  useRide,
  useUpdateRide,
  useDeleteRide,
  useJoinRide,
  useLeaveRide,
} from '../../hooks/useRide'
import { RideStatus } from '../../hooks/useRide'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { RideGroupCard } from '../../components/ride/RideGroupCard'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'

const statusColors: Record<RideStatus, string> = {
  [RideStatus.Draft]: 'bg-gray-100 text-gray-800',
  [RideStatus.Published]: 'bg-green-100 text-green-800',
  [RideStatus.Cancelled]: 'bg-red-100 text-red-800',
}

export function RideDetailPage() {
  const { t, i18n } = useTranslation('rides')
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>()
  const { isAuthenticated, user } = useAuth()
  const [joiningGroupId, setJoiningGroupId] = useState<string | null>(null)
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [showUncancelConfirm, setShowUncancelConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: ride, isLoading: isLoadingRide, error } = useRide(teamSlug, rideSlug)

  const updateMutation = useUpdateRide(teamSlug, rideSlug!)
  const deleteMutation = useDeleteRide(teamSlug)
  const joinMutation = useJoinRide(teamSlug, rideSlug!)
  const leaveMutation = useLeaveRide(teamSlug, rideSlug!)

  if (isLoadingTeam || isLoadingRide) {
    return <LoadingPage message={t('loading')} />
  }

  if (error || !ride) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('detail.notFound.title')}</h1>
          <p className="text-gray-600 mb-6">{t('detail.notFound.message')}</p>
          <Link
            to={`/teams/${teamSlug}/rides`}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('detail.notFound.backToRides')}
          </Link>
        </div>
      </div>
    )
  }

  const isMember = !!team?.role
  const isAdmin = team?.role === 'ADMIN'
  const isOrganizer = team?.role === 'ORGANIZER'
  const canEdit = isAdmin || isOrganizer
  const hasJoinedAnyGroup =
    user && ride.groups
      ? ride.groups.some((group) => group.participants.some((p) => p.id === user.dbId))
      : false
  const canJoinRide = isMember && ride.status === RideStatus.Published && !hasJoinedAnyGroup

  const rideDate = new Date(ride.date)
  const formattedDate = rideDate.toLocaleDateString(i18n.language, {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })

  const handlePublish = () => {
    updateMutation.mutate({ ...ride, status: RideStatus.Published })
  }

  const handleUnpublish = () => {
    updateMutation.mutate({ ...ride, status: RideStatus.Draft })
    setShowUnpublishConfirm(false)
  }

  const handleCancel = () => {
    updateMutation.mutate({ ...ride, status: RideStatus.Cancelled })
    setShowCancelConfirm(false)
  }

  const handleUncancel = () => {
    updateMutation.mutate({ ...ride, status: RideStatus.Draft })
    setShowUncancelConfirm(false)
  }

  const handleDelete = () => {
    deleteMutation.mutate(rideSlug!)
    setShowDeleteConfirm(false)
  }

  const handleJoinGroup = (groupId: string) => {
    setJoiningGroupId(groupId)
    joinMutation.mutate(
      { groupId },
      {
        onSettled: () => setJoiningGroupId(null),
      }
    )
  }

  const handleLeaveGroup = (groupId: string) => {
    setJoiningGroupId(groupId)
    leaveMutation.mutate(groupId, {
      onSettled: () => setJoiningGroupId(null),
    })
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold text-gray-900">{ride.title}</h1>
              <span
                className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[ride.status]}`}
              >
                {t(`status.${ride.status}`)}
              </span>
            </div>
            {ride.description && <p className="mt-2 text-gray-600">{ride.description}</p>}
            {ride.status === RideStatus.Draft && ride.publishAt && (
              <div className="mt-2 text-sm text-amber-600 flex items-center">
                <ClockIcon className="w-4 h-4 mr-1" />
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
                <CalendarIcon className="w-4 h-4 mr-1" />
                {formattedDate}
              </span>
              {ride.startTime && (
                <span className="flex items-center">
                  <ClockIcon className="w-4 h-4 mr-1" />
                  {ride.startTime.substring(0, 5)}
                </span>
              )}
              <span className="flex items-center">
                <UsersIcon className="w-4 h-4 mr-1" />
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
                <PencilIcon className="w-4 h-4 mr-1" />
                {t('detail.actions.edit')}
              </Link>
              {ride.status === RideStatus.Draft && (
                <button
                  onClick={handlePublish}
                  disabled={updateMutation.isPending}
                  className="inline-flex items-center px-3 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-green-600 hover:bg-green-700 disabled:opacity-50"
                >
                  {updateMutation.isPending ? <LoadingSpinner size="sm" className="mr-2" /> : null}
                  {t('detail.actions.publish')}
                </button>
              )}
              {ride.status === RideStatus.Published && (
                <>
                  <button
                    onClick={() => setShowUnpublishConfirm(true)}
                    className="inline-flex items-center px-3 py-2 border border-yellow-300 rounded-md text-sm font-medium text-yellow-700 bg-white hover:bg-yellow-50"
                  >
                    {t('detail.actions.unpublish')}
                  </button>
                  <button
                    onClick={() => setShowCancelConfirm(true)}
                    className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                  >
                    {t('detail.actions.cancel')}
                  </button>
                </>
              )}
              {ride.status === RideStatus.Cancelled && (
                <button
                  onClick={() => setShowUncancelConfirm(true)}
                  className="inline-flex items-center px-3 py-2 border border-green-300 rounded-md text-sm font-medium text-green-700 bg-white hover:bg-green-50"
                >
                  {t('detail.actions.uncancel')}
                </button>
              )}
              <button
                onClick={() => setShowDeleteConfirm(true)}
                className="inline-flex items-center px-3 py-2 border border-red-300 rounded-md text-sm font-medium text-red-700 bg-white hover:bg-red-50"
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
            {ride.groups.map((group) => {
              const isJoined = user ? group.participants.some((p) => p.id === user.dbId) : false
              return (
                <RideGroupCard
                  key={group.id}
                  group={group}
                  isJoined={isJoined}
                  canJoin={canJoinRide}
                  onJoin={() => handleJoinGroup(group.id)}
                  onLeave={() => handleLeaveGroup(group.id)}
                  isLoading={
                    joiningGroupId === group.id &&
                    (joinMutation.isPending || leaveMutation.isPending)
                  }
                />
              )
            })}
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

      {/* Confirmation Dialogs */}
      <ConfirmDialog
        isOpen={showUnpublishConfirm}
        onClose={() => setShowUnpublishConfirm(false)}
        onConfirm={handleUnpublish}
        title={t('detail.actions.unpublish')}
        message={t('detail.confirmations.unpublish')}
        confirmText={t('detail.actions.unpublish')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showCancelConfirm}
        onClose={() => setShowCancelConfirm(false)}
        onConfirm={handleCancel}
        title={t('detail.actions.cancel')}
        message={t('detail.confirmations.cancel')}
        confirmText={t('detail.actions.cancel')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showUncancelConfirm}
        onClose={() => setShowUncancelConfirm(false)}
        onConfirm={handleUncancel}
        title={t('detail.actions.uncancel')}
        message={t('detail.confirmations.uncancel')}
        confirmText={t('detail.actions.uncancel')}
        variant="info"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('detail.actions.delete')}
        message={t('detail.confirmations.delete')}
        confirmText={t('detail.actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
