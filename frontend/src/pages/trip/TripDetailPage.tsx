import { useState } from 'react'
import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { paths } from '../../config/paths'
import {
  CalendarIcon,
  UsersIcon,
  PencilIcon,
  RectangleStackIcon,
  ChevronDownIcon,
} from '@heroicons/react/24/outline'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetTrip,
  useUpdateTrip,
  useDeleteTrip,
  useJoinTrip,
  useLeaveTrip,
  getGetTripQueryKey,
} from '../../api/endpoints/trips/trips'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { Status } from '@/api/dto'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { TripStageCard } from '../../components/trip/TripStageCard'
import { RoutesMapView } from '../../components/common/RoutesMapView'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { useFormattedDate } from '../../utils/dateFormat'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const statusColors: Record<Status, string> = {
  [Status.DRAFT]: 'bg-gray-100 text-gray-800',
  [Status.PUBLISHED]: 'bg-green-100 text-green-800',
  [Status.CANCELLED]: 'bg-red-100 text-red-800',
}

export function TripDetailPage() {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, tripSlug } = useParams<{ teamSlug: string; tripSlug: string }>()
  const { isAuthenticated, user } = useAuth()
  const [highlightedStageId, setHighlightedStageId] = useState<string | null>(null)
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [showUncancelConfirm, setShowUncancelConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const queryClient = useQueryClient()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: trip,
    isLoading: isLoadingTrip,
    error,
  } = useGetTrip(teamSlug!, tripSlug!, {
    query: { enabled: !!teamSlug && !!tripSlug },
  })

  const updateMutation = useUpdateTrip()
  const deleteMutation = useDeleteTrip()
  const joinMutation = useJoinTrip()
  const leaveMutation = useLeaveTrip()

  useCanonicalPath(team && trip ? paths.trip(team.slug, trip.slug) : undefined)

  if (isLoadingTeam || isLoadingTrip) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  if (team && !team.enableTrips) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  if (error || !trip) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">
            {t('trips.detail.notFound.title')}
          </h1>
          <p className="text-gray-600 mb-6">{t('trips.detail.notFound.message')}</p>
          <Link
            to={paths.team(teamSlug!)}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('trips.detail.notFound.backToTrips')}
          </Link>
        </div>
      </div>
    )
  }

  const isMember = !!team?.role
  const isAdmin = team?.role === 'ADMIN'
  const isOrganizer = team?.role === 'ORGANIZER'
  const canEdit = isAdmin || isOrganizer
  const hasJoined =
    user && trip.participants ? trip.participants.some((p) => p.id === user.id) : false
  const canJoinTrip = isMember && trip.status === Status.PUBLISHED && !hasJoined

  const formattedDate = formatDateTime(trip.dateTime)

  const handlePublish = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, tripSlug: tripSlug!, data: { ...trip, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          toast.success(t('trips.notifications.published'))
        },
      }
    )
  }

  const handleUnpublish = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, tripSlug: tripSlug!, data: { ...trip, status: Status.DRAFT } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          toast.success(t('trips.notifications.unpublished'))
        },
      }
    )
    setShowUnpublishConfirm(false)
  }

  const handleCancel = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, tripSlug: tripSlug!, data: { ...trip, status: Status.CANCELLED } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          toast.success(t('trips.notifications.cancelled'))
        },
      }
    )
    setShowCancelConfirm(false)
  }

  const handleUncancel = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, tripSlug: tripSlug!, data: { ...trip, status: Status.DRAFT } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          toast.success(t('trips.notifications.uncancelled'))
        },
      }
    )
    setShowUncancelConfirm(false)
  }

  const handleDelete = () => {
    deleteMutation.mutate(
      { teamSlug: teamSlug!, tripSlug: tripSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          toast.success(t('trips.notifications.deleted'))
          navigate(paths.team(teamSlug!))
        },
      }
    )
    setShowDeleteConfirm(false)
  }

  const handleJoin = () => {
    joinMutation.mutate(
      { teamSlug: teamSlug!, tripSlug: tripSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          toast.success(t('trips.notifications.joined'))
        },
      }
    )
  }

  const handleLeave = () => {
    leaveMutation.mutate(
      { teamSlug: teamSlug!, tripSlug: tripSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          toast.success(t('trips.notifications.left'))
        },
      }
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3 min-w-0">
            <EntityLogo logo={trip.media.assets.logo} alt={trip.name} size="lg" />
            <h1 className="text-2xl font-bold text-gray-900 truncate">{trip.name}</h1>
            <span
              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium shrink-0 ${statusColors[trip.status]}`}
            >
              {t(`status.${trip.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </span>
          </div>

          <div className="flex flex-wrap items-center gap-2 shrink-0">
            {/* Join/Leave button for members */}
            {canJoinTrip && (
              <button
                onClick={handleJoin}
                disabled={joinMutation.isPending}
                className="inline-flex items-center px-3 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
              >
                {joinMutation.isPending ? <LoadingSpinner size="sm" className="mr-2" /> : null}
                {t('trips.detail.actions.join')}
              </button>
            )}
            {hasJoined && (
              <button
                onClick={handleLeave}
                disabled={leaveMutation.isPending}
                className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
              >
                {leaveMutation.isPending ? <LoadingSpinner size="sm" className="mr-2" /> : null}
                {t('trips.detail.actions.leave')}
              </button>
            )}

            {canEdit && (
              <ButtonGroup>
                <Button asChild variant="outline">
                  <Link to={paths.tripEdit(teamSlug!, tripSlug!)}>
                    <PencilIcon className="w-4 h-4" />
                    {t('actions.edit')}
                  </Link>
                </Button>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline" className="!pl-2">
                      <ChevronDownIcon className="w-4 h-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    {trip.status === Status.DRAFT && (
                      <DropdownMenuItem
                        onClick={handlePublish}
                        disabled={updateMutation.isPending}
                        className="text-green-700"
                      >
                        {updateMutation.isPending && <LoadingSpinner size="sm" />}
                        {t('actions.publish')}
                      </DropdownMenuItem>
                    )}
                    {trip.status === Status.PUBLISHED && (
                      <>
                        <DropdownMenuItem
                          onClick={() => setShowUnpublishConfirm(true)}
                          className="text-yellow-700"
                        >
                          {t('actions.unpublish')}
                        </DropdownMenuItem>
                        <DropdownMenuItem
                          onClick={() => setShowCancelConfirm(true)}
                          className="text-yellow-700"
                        >
                          {t('trips.detail.actions.cancel')}
                        </DropdownMenuItem>
                      </>
                    )}
                    {trip.status === Status.CANCELLED && (
                      <DropdownMenuItem
                        onClick={() => setShowUncancelConfirm(true)}
                        className="text-green-700"
                      >
                        {t('trips.detail.actions.uncancel')}
                      </DropdownMenuItem>
                    )}
                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      onClick={() => setShowDeleteConfirm(true)}
                      variant="destructive"
                    >
                      {t('actions.delete')}
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </ButtonGroup>
            )}
          </div>
        </div>

        <div className="mt-4">
          <MediaDisplay media={trip.media} className="text-gray-600" />
        </div>
        {trip.status === Status.DRAFT && trip.publishAt && (
          <div className="mt-2 text-sm text-amber-600 flex items-center">
            <CalendarIcon className="w-4 h-4 mr-1" />
            {t('trips.detail.scheduledPublish', {
              date: formatDateTime(trip.publishAt),
            })}
          </div>
        )}
        <div className="mt-4 flex flex-wrap items-center gap-4 text-sm text-gray-500">
          <span className="flex items-center">
            <CalendarIcon className="w-4 h-4 mr-1" />
            {formattedDate}
          </span>
          <span className="flex items-center">
            <UsersIcon className="w-4 h-4 mr-1" />
            {t('participantCount', { count: trip.participantCount })}
          </span>
          <span className="flex items-center">
            <RectangleStackIcon className="w-4 h-4 mr-1" />
            {t('trips.card.stageCount', { count: trip.stageCount })}
          </span>
        </div>
      </div>

      {/* Map and Stages */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6 mb-6">
        {/* Stages list on left (takes 1 column on xl screens) */}
        <div className="xl:col-span-1 order-2 xl:order-1">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            {t('trips.detail.stages.title')}
          </h2>
          {trip.stages && trip.stages.length > 0 ? (
            <div className="space-y-3">
              {trip.stages.map((stage, index) => (
                <TripStageCard
                  key={stage.id}
                  stage={stage}
                  index={index}
                  teamSlug={teamSlug!}
                  onHover={setHighlightedStageId}
                  isHighlighted={highlightedStageId === stage.id}
                />
              ))}
            </div>
          ) : (
            <p className="text-gray-500">{t('trips.detail.stages.empty')}</p>
          )}
        </div>

        {/* Map on right (takes 2 columns on xl screens) */}
        <div className="xl:col-span-2 order-1 xl:order-2">
          {trip.stages && trip.stages.length > 0 && (
            <RoutesMapView
              items={trip.stages}
              teamSlug={teamSlug!}
              highlightedItemId={highlightedStageId}
              onItemHover={setHighlightedStageId}
            />
          )}
        </div>
      </div>

      {/* Participants section */}
      {trip.participants && trip.participants.length > 0 && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            {t('trips.detail.participants.title')}
          </h2>
          <div className="flex flex-wrap gap-2">
            {trip.participants.map((participant) => (
              <span
                key={participant.id}
                className="inline-flex items-center px-3 py-1 rounded-full text-sm bg-gray-100 text-gray-800"
              >
                {participant.displayName}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Info for non-members */}
      {!isMember && isAuthenticated && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <p className="text-yellow-800">
            {t('trips.detail.nonMember.message')}{' '}
            <Link to={paths.team(teamSlug!)} className="font-medium underline">
              {t('trips.detail.nonMember.viewTeam')}
            </Link>
          </p>
        </div>
      )}

      {!isAuthenticated && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <p className="text-blue-800">
            {t('trips.detail.notAuthenticated.message')}{' '}
            <Link to="/login" className="font-medium underline">
              {t('trips.detail.notAuthenticated.signIn')}
            </Link>
          </p>
        </div>
      )}

      {/* Comments Section - only visible to team members */}
      {isMember && (
        <div className="mt-6">
          <CommentSection
            teamSlug={teamSlug!}
            entityType="trips"
            entitySlug={tripSlug!}
            isOrganizer={canEdit}
          />
        </div>
      )}

      {/* Confirmation Dialogs */}
      <ConfirmDialog
        isOpen={showUnpublishConfirm}
        onClose={() => setShowUnpublishConfirm(false)}
        onConfirm={handleUnpublish}
        title={t('actions.unpublish')}
        message={t('trips.detail.confirmations.unpublish')}
        confirmText={t('actions.unpublish')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showCancelConfirm}
        onClose={() => setShowCancelConfirm(false)}
        onConfirm={handleCancel}
        title={t('trips.detail.actions.cancel')}
        message={t('trips.detail.confirmations.cancel')}
        confirmText={t('trips.detail.actions.cancel')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showUncancelConfirm}
        onClose={() => setShowUncancelConfirm(false)}
        onConfirm={handleUncancel}
        title={t('trips.detail.actions.uncancel')}
        message={t('trips.detail.confirmations.uncancel')}
        confirmText={t('trips.detail.actions.uncancel')}
        variant="info"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('actions.delete')}
        message={t('trips.detail.confirmations.delete')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
