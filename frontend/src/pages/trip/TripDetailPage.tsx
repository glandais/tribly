import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import {
  CalendarIcon,
  UsersIcon,
  PencilIcon,
  RectangleStackIcon,
  ChevronDownIcon,
} from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import {
  useTrip,
  useUpdateTrip,
  useDeleteTrip,
  useJoinTrip,
  useLeaveTrip,
} from '../../hooks/useTrip'
import { Status } from '../../hooks/useTrip'
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

const statusColors: Record<Status, string> = {
  [Status.Draft]: 'bg-gray-100 text-gray-800',
  [Status.Published]: 'bg-green-100 text-green-800',
  [Status.Cancelled]: 'bg-red-100 text-red-800',
}

export function TripDetailPage() {
  const { t } = useTranslation('trips')
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, tripSlug } = useParams<{ teamSlug: string; tripSlug: string }>()
  const { isAuthenticated, user } = useAuth()
  const [highlightedStageId, setHighlightedStageId] = useState<string | null>(null)
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [showUncancelConfirm, setShowUncancelConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: trip, isLoading: isLoadingTrip, error } = useTrip(teamSlug, tripSlug)

  const updateMutation = useUpdateTrip(teamSlug, tripSlug!)
  const deleteMutation = useDeleteTrip(teamSlug)
  const joinMutation = useJoinTrip(teamSlug, tripSlug!)
  const leaveMutation = useLeaveTrip(teamSlug, tripSlug!)

  if (isLoadingTeam || isLoadingTrip) {
    return <LoadingPage message={t('loading')} />
  }

  if (team && !team.enableTrips) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  if (error || !trip) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('detail.notFound.title')}</h1>
          <p className="text-gray-600 mb-6">{t('detail.notFound.message')}</p>
          <Link
            to={paths.team(teamSlug!)}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('detail.notFound.backToTrips')}
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
  const canJoinTrip = isMember && trip.status === Status.Published && !hasJoined

  const formattedDate = formatDateTime(trip.dateTime)

  const handlePublish = () => {
    updateMutation.mutate({ ...trip, status: Status.Published })
  }

  const handleUnpublish = () => {
    updateMutation.mutate({ ...trip, status: Status.Draft })
    setShowUnpublishConfirm(false)
  }

  const handleCancel = () => {
    updateMutation.mutate({ ...trip, status: Status.Cancelled })
    setShowCancelConfirm(false)
  }

  const handleUncancel = () => {
    updateMutation.mutate({ ...trip, status: Status.Draft })
    setShowUncancelConfirm(false)
  }

  const handleDelete = () => {
    deleteMutation.mutate(tripSlug!)
    setShowDeleteConfirm(false)
  }

  const handleJoin = () => {
    joinMutation.mutate()
  }

  const handleLeave = () => {
    leaveMutation.mutate()
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
              {t(`status.${trip.status}`)}
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
                {t('detail.actions.join')}
              </button>
            )}
            {hasJoined && (
              <button
                onClick={handleLeave}
                disabled={leaveMutation.isPending}
                className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
              >
                {leaveMutation.isPending ? <LoadingSpinner size="sm" className="mr-2" /> : null}
                {t('detail.actions.leave')}
              </button>
            )}

            {canEdit && (
              <ButtonGroup>
                <Button asChild variant="outline">
                  <Link to={paths.tripEdit(teamSlug!, tripSlug!)}>
                    <PencilIcon className="w-4 h-4" />
                    {t('detail.actions.edit')}
                  </Link>
                </Button>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline" className="!pl-2">
                      <ChevronDownIcon className="w-4 h-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    {trip.status === Status.Draft && (
                      <DropdownMenuItem
                        onClick={handlePublish}
                        disabled={updateMutation.isPending}
                        className="text-green-700"
                      >
                        {updateMutation.isPending && <LoadingSpinner size="sm" />}
                        {t('detail.actions.publish')}
                      </DropdownMenuItem>
                    )}
                    {trip.status === Status.Published && (
                      <>
                        <DropdownMenuItem
                          onClick={() => setShowUnpublishConfirm(true)}
                          className="text-yellow-700"
                        >
                          {t('detail.actions.unpublish')}
                        </DropdownMenuItem>
                        <DropdownMenuItem
                          onClick={() => setShowCancelConfirm(true)}
                          className="text-yellow-700"
                        >
                          {t('detail.actions.cancel')}
                        </DropdownMenuItem>
                      </>
                    )}
                    {trip.status === Status.Cancelled && (
                      <DropdownMenuItem
                        onClick={() => setShowUncancelConfirm(true)}
                        className="text-green-700"
                      >
                        {t('detail.actions.uncancel')}
                      </DropdownMenuItem>
                    )}
                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      onClick={() => setShowDeleteConfirm(true)}
                      variant="destructive"
                    >
                      {t('detail.actions.delete')}
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
        {trip.status === Status.Draft && trip.publishAt && (
          <div className="mt-2 text-sm text-amber-600 flex items-center">
            <CalendarIcon className="w-4 h-4 mr-1" />
            {t('detail.scheduledPublish', {
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
            {t('card.participantCount', { count: trip.participantCount })}
          </span>
          <span className="flex items-center">
            <RectangleStackIcon className="w-4 h-4 mr-1" />
            {t('card.stageCount', { count: trip.stageCount })}
          </span>
        </div>
      </div>

      {/* Map and Stages */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6 mb-6">
        {/* Stages list on left (takes 1 column on xl screens) */}
        <div className="xl:col-span-1 order-2 xl:order-1">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">{t('detail.stages.title')}</h2>
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
            <p className="text-gray-500">{t('detail.stages.empty')}</p>
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
            {t('detail.participants.title')}
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
            {t('detail.nonMember.message')}{' '}
            <Link to={paths.team(teamSlug!)} className="font-medium underline">
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
