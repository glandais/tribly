import { useState, useMemo } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  ClockIcon,
  CalendarIcon,
  UsersIcon,
  PencilIcon,
  MapPinIcon,
} from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import {
  useRide,
  useUpdateRide,
  useDeleteRide,
  useJoinRide,
  useLeaveRide,
} from '../../hooks/useRide'
import { Status } from '../../hooks/useRide'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { RideGroupCard } from '../../components/ride/RideGroupCard'
import { RoutesMapView, MapRouteItem } from '../../components/common/RoutesMapView'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { useFormattedDate } from '../../utils/dateFormat'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'

const statusColors: Record<Status, string> = {
  [Status.Draft]: 'bg-gray-100 text-gray-800',
  [Status.Published]: 'bg-green-100 text-green-800',
  [Status.Cancelled]: 'bg-red-100 text-red-800',
}

export function RideDetailPage() {
  const { t } = useTranslation('rides')
  const { formatDate, formatDateTime, formatTime } = useFormattedDate()
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>()
  const { isAuthenticated, user } = useAuth()
  const [joiningGroupId, setJoiningGroupId] = useState<string | null>(null)
  const [highlightedGroupId, setHighlightedGroupId] = useState<string | null>(null)
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

  // Combine ride's main route with group routes for map display
  // Must be before early returns to maintain hook order
  const mapItems = useMemo(() => {
    if (!ride) return []
    const items: MapRouteItem[] = []

    // Add ride's main route if it exists
    if (ride.routeSlug) {
      items.push({
        id: 'ride-main-route',
        name: t('detail.mainRoute'),
        routeSlug: ride.routeSlug,
      })
    }

    // Add all groups
    if (ride.groups) {
      items.push(...ride.groups)
    }

    return items
  }, [ride, t])

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
      ? ride.groups.some((group) => group.participants.some((p) => p.id === user.id))
      : false
  const canJoinRide = isMember && ride.status === Status.Published && !hasJoinedAnyGroup

  const formattedDate = formatDate(ride.dateTime)

  const handlePublish = () => {
    updateMutation.mutate({ ...ride, status: Status.Published })
  }

  const handleUnpublish = () => {
    updateMutation.mutate({ ...ride, status: Status.Draft })
    setShowUnpublishConfirm(false)
  }

  const handleCancel = () => {
    updateMutation.mutate({ ...ride, status: Status.Cancelled })
    setShowCancelConfirm(false)
  }

  const handleUncancel = () => {
    updateMutation.mutate({ ...ride, status: Status.Draft })
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
              <EntityLogo logo={ride.media.assets.logo} alt={ride.name} size="lg" />
              <h1 className="text-2xl font-bold text-gray-900">{ride.name}</h1>
              <span
                className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[ride.status]}`}
              >
                {t(`status.${ride.status}`)}
              </span>
            </div>
            <div className="mt-2">
              <MediaDisplay media={ride.media} className="text-gray-600" />
            </div>
            {ride.status === Status.Draft && ride.publishAt && (
              <div className="mt-2 text-sm text-amber-600 flex items-center">
                <ClockIcon className="w-4 h-4 mr-1" />
                {t('detail.scheduledPublish', {
                  date: formatDateTime(ride.publishAt),
                })}
              </div>
            )}
            <div className="mt-4 flex flex-wrap items-center gap-4 text-sm text-gray-500">
              <span className="flex items-center">
                <CalendarIcon className="w-4 h-4 mr-1" />
                {formattedDate}
              </span>
              <span className="flex items-center">
                <ClockIcon className="w-4 h-4 mr-1" />
                {formatTime(ride.dateTime)}
              </span>
              <span className="flex items-center">
                <UsersIcon className="w-4 h-4 mr-1" />
                {t('card.participantCount', { count: ride.participantCount })}
              </span>
            </div>
            {/* Start and End Places */}
            {(ride.startPlace || ride.endPlace) && (
              <div className="mt-3 flex flex-wrap items-center gap-4 text-sm text-gray-600">
                {ride.startPlace && (
                  <span className="flex items-center">
                    <MapPinIcon className="w-4 h-4 mr-1 text-green-600" />
                    <span className="font-medium text-green-700">{t('detail.startPlace')}:</span>
                    <span className="ml-1">
                      {ride.startPlace.name}
                      {ride.startPlace.address && (
                        <span className="text-gray-500"> ({ride.startPlace.address})</span>
                      )}
                    </span>
                  </span>
                )}
                {ride.endPlace && (
                  <span className="flex items-center">
                    <MapPinIcon className="w-4 h-4 mr-1 text-red-600" />
                    <span className="font-medium text-red-700">{t('detail.endPlace')}:</span>
                    <span className="ml-1">
                      {ride.endPlace.name}
                      {ride.endPlace.address && (
                        <span className="text-gray-500"> ({ride.endPlace.address})</span>
                      )}
                    </span>
                  </span>
                )}
              </div>
            )}
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
              {ride.status === Status.Draft && (
                <button
                  onClick={handlePublish}
                  disabled={updateMutation.isPending}
                  className="inline-flex items-center px-3 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-green-600 hover:bg-green-700 disabled:opacity-50"
                >
                  {updateMutation.isPending ? <LoadingSpinner size="sm" className="mr-2" /> : null}
                  {t('detail.actions.publish')}
                </button>
              )}
              {ride.status === Status.Published && (
                <>
                  <button
                    onClick={() => setShowUnpublishConfirm(true)}
                    className="inline-flex items-center px-3 py-2 border border-yellow-300 rounded-md text-sm font-medium text-yellow-700 bg-white hover:bg-yellow-50"
                  >
                    {t('detail.actions.unpublish')}
                  </button>
                  <button
                    onClick={() => setShowCancelConfirm(true)}
                    className="inline-flex items-center px-3 py-2 border border-yellow-300 rounded-md text-sm font-medium text-yellow-700 bg-white hover:bg-yellow-50"
                  >
                    {t('detail.actions.cancel')}
                  </button>
                </>
              )}
              {ride.status === Status.Cancelled && (
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

      {/* Map and Groups */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6 mb-6">
        {/* Groups list on left (takes 1 column on xl screens) */}
        <div className="xl:col-span-1 order-2 xl:order-1">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">{t('detail.groups.title')}</h2>
          {ride.groups && ride.groups.length > 0 ? (
            <div className="space-y-3">
              {ride.groups.map((group) => {
                const isJoined = user ? group.participants.some((p) => p.id === user.id) : false
                return (
                  <RideGroupCard
                    key={group.id}
                    group={group}
                    teamSlug={teamSlug!}
                    rideRouteSlug={ride.routeSlug}
                    isJoined={isJoined}
                    canJoin={canJoinRide}
                    onJoin={() => handleJoinGroup(group.id)}
                    onLeave={() => handleLeaveGroup(group.id)}
                    onHover={setHighlightedGroupId}
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

        {/* Map on right (takes 2 columns on xl screens) */}
        <div className="xl:col-span-2 order-1 xl:order-2">
          {mapItems.length > 0 && (
            <RoutesMapView
              items={mapItems}
              teamSlug={teamSlug!}
              highlightedItemId={highlightedGroupId}
              onItemHover={setHighlightedGroupId}
            />
          )}
        </div>
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
