import { useState, useMemo } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import {
  CalendarIcon,
  UsersIcon,
  PencilIcon,
  MapPinIcon,
  ChevronDownIcon,
} from '@heroicons/react/24/outline'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetRide,
  useUpdateRide,
  useDeleteRide,
  useJoinGroup,
  useLeaveGroup,
  getGetRideQueryKey,
  getListRidesQueryKey,
} from '../../api/endpoints/rides/rides'
import { Status } from '@/api/dto'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { RideGroupCard } from '../../components/ride/RideGroupCard'
import { RoutesMapView, MapRouteItem } from '../../components/common/RoutesMapView'
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
import { paths } from '@/config/paths'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const statusColors: Record<Status, string> = {
  [Status.DRAFT]: 'bg-gray-100 text-gray-800',
  [Status.PUBLISHED]: 'bg-green-100 text-green-800',
  [Status.CANCELLED]: 'bg-red-100 text-red-800',
}

export function RideDetailPage() {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>()
  const { isAuthenticated, user } = useAuth()
  const [joiningGroupId, setJoiningGroupId] = useState<string | null>(null)
  const [highlightedGroupId, setHighlightedGroupId] = useState<string | null>(null)
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [showUncancelConfirm, setShowUncancelConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: ride,
    isLoading: isLoadingRide,
    error,
  } = useGetRide(teamSlug!, rideSlug!, {
    query: { enabled: !!teamSlug && !!rideSlug },
  })

  const queryClient = useQueryClient()
  const navigate = useNavigate()
  const updateMutation = useUpdateRide()
  const deleteMutation = useDeleteRide()
  const joinMutation = useJoinGroup()
  const leaveMutation = useLeaveGroup()

  // Combine ride's main route with group routes for map display
  // Must be before early returns to maintain hook order
  const mapItems = useMemo(() => {
    if (!ride) return []
    const items: MapRouteItem[] = []

    // Add ride's main route if it exists
    if (ride.routeSlug) {
      items.push({
        id: 'ride-main-route',
        name: t('rides.detail.mainRoute'),
        routeSlug: ride.routeSlug,
      })
    }

    // Add all groups
    if (ride.groups) {
      items.push(...ride.groups)
    }

    return items
  }, [ride, t])

  useCanonicalPath(team && ride ? paths.ride(team.slug, ride.slug) : undefined)

  if (isLoadingTeam || isLoadingRide) {
    return <LoadingPage message={t('loading')} />
  }

  if (error || !ride || !team) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">
            {t('rides.detail.notFound.title')}
          </h1>
          <p className="text-gray-600 mb-6">{t('rides.detail.notFound.message')}</p>
          <Link
            to={paths.team(teamSlug!)}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('rides.detail.notFound.backToRides')}
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
  const canJoinRide = isMember && ride.status === Status.PUBLISHED && !hasJoinedAnyGroup

  const formattedDate = formatDateTime(ride.dateTime)

  const handlePublish = () => {
    updateMutation.mutate(
      { slug: teamSlug!, rideSlug: rideSlug!, data: { ...ride, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListRidesQueryKey(teamSlug!) })
          toast.success(t('rides.notifications.published'))
        },
      }
    )
  }

  const handleUnpublish = () => {
    updateMutation.mutate(
      { slug: teamSlug!, rideSlug: rideSlug!, data: { ...ride, status: Status.DRAFT } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListRidesQueryKey(teamSlug!) })
          toast.success(t('rides.notifications.unpublished'))
        },
      }
    )
    setShowUnpublishConfirm(false)
  }

  const handleCancel = () => {
    updateMutation.mutate(
      { slug: teamSlug!, rideSlug: rideSlug!, data: { ...ride, status: Status.CANCELLED } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListRidesQueryKey(teamSlug!) })
          toast.success(t('rides.notifications.cancelled'))
        },
      }
    )
    setShowCancelConfirm(false)
  }

  const handleUncancel = () => {
    updateMutation.mutate(
      { slug: teamSlug!, rideSlug: rideSlug!, data: { ...ride, status: Status.DRAFT } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListRidesQueryKey(teamSlug!) })
          toast.success(t('rides.notifications.uncancelled'))
        },
      }
    )
    setShowUncancelConfirm(false)
  }

  const handleDelete = () => {
    deleteMutation.mutate(
      { slug: teamSlug!, rideSlug: rideSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListRidesQueryKey(teamSlug!) })
          toast.success(t('rides.notifications.deleted'))
          navigate(paths.team(teamSlug!))
        },
      }
    )
    setShowDeleteConfirm(false)
  }

  const handleJoinGroup = (groupId: string) => {
    setJoiningGroupId(groupId)
    joinMutation.mutate(
      { slug: teamSlug!, rideSlug: rideSlug!, groupId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          toast.success(t('rides.notifications.joined'))
        },
        onSettled: () => setJoiningGroupId(null),
      }
    )
  }

  const handleLeaveGroup = (groupId: string) => {
    setJoiningGroupId(groupId)
    leaveMutation.mutate(
      { slug: teamSlug!, rideSlug: rideSlug!, groupId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          toast.success(t('rides.notifications.left'))
        },
        onSettled: () => setJoiningGroupId(null),
      }
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3 min-w-0">
            <EntityLogo logo={ride.media.assets.logo} alt={ride.name} size="lg" />
            <h1 className="text-2xl font-bold text-gray-900 truncate">{ride.name}</h1>
            <span
              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium shrink-0 ${statusColors[ride.status]}`}
            >
              {t(`status.${ride.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </span>
          </div>

          {canEdit && (
            <ButtonGroup>
              <Button asChild variant="outline">
                <Link to={paths.rideEdit(teamSlug!, rideSlug!)}>
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
                  {ride.status === Status.DRAFT && (
                    <DropdownMenuItem
                      onClick={handlePublish}
                      disabled={updateMutation.isPending}
                      className="text-green-700"
                    >
                      {updateMutation.isPending && <LoadingSpinner size="sm" />}
                      {t('actions.publish')}
                    </DropdownMenuItem>
                  )}
                  {ride.status === Status.PUBLISHED && (
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
                        {t('rides.detail.actions.cancel')}
                      </DropdownMenuItem>
                    </>
                  )}
                  {ride.status === Status.CANCELLED && (
                    <DropdownMenuItem
                      onClick={() => setShowUncancelConfirm(true)}
                      className="text-green-700"
                    >
                      {t('rides.detail.actions.uncancel')}
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

        <div className="mt-4">
          <MediaDisplay media={ride.media} className="text-gray-600" />
        </div>
        {ride.status === Status.DRAFT && ride.publishAt && (
          <div className="mt-2 text-sm text-amber-600 flex items-center">
            <CalendarIcon className="w-4 h-4 mr-1" />
            {t('rides.detail.scheduledPublish', {
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
            <UsersIcon className="w-4 h-4 mr-1" />
            {t('participantCount', { count: ride.participantCount })}
          </span>
        </div>
        {/* Start and End Places */}
        {(ride.startPlace || ride.endPlace) && (
          <div className="mt-3 flex flex-wrap items-center gap-4 text-sm text-gray-600">
            {ride.startPlace && (
              <span className="flex items-center">
                <MapPinIcon className="w-4 h-4 mr-1 text-green-600" />
                <span className="font-medium text-green-700">{t('startPlace')}:</span>
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
                <span className="font-medium text-red-700">{t('endPlace')}:</span>
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

      {/* Map and Groups */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6 mb-6">
        {/* Groups list on left (takes 1 column on xl screens) */}
        <div className="xl:col-span-1 order-2 xl:order-1">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            {t('rides.detail.groups.title')}
          </h2>
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
                    isHighlighted={highlightedGroupId === group.id}
                    isLoading={
                      joiningGroupId === group.id &&
                      (joinMutation.isPending || leaveMutation.isPending)
                    }
                  />
                )
              })}
            </div>
          ) : (
            <p className="text-gray-500">{t('rides.detail.groups.empty')}</p>
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
            {t('rides.detail.nonMember.message')}{' '}
            <Link to={paths.team(teamSlug!)} className="font-medium underline">
              {t('rides.detail.nonMember.viewTeam')}
            </Link>
          </p>
        </div>
      )}

      {!isAuthenticated && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <p className="text-blue-800">
            {t('rides.detail.notAuthenticated.message')}{' '}
            <Link to="/login" className="font-medium underline">
              {t('rides.detail.notAuthenticated.signIn')}
            </Link>
          </p>
        </div>
      )}

      {/* Comments Section - only visible to team members */}
      {isMember && (
        <div className="mt-6">
          <CommentSection
            teamSlug={teamSlug!}
            entityType="rides"
            entitySlug={rideSlug!}
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
        message={t('rides.detail.confirmations.unpublish')}
        confirmText={t('actions.unpublish')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showCancelConfirm}
        onClose={() => setShowCancelConfirm(false)}
        onConfirm={handleCancel}
        title={t('rides.detail.actions.cancel')}
        message={t('rides.detail.confirmations.cancel')}
        confirmText={t('rides.detail.actions.cancel')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showUncancelConfirm}
        onClose={() => setShowUncancelConfirm(false)}
        onConfirm={handleUncancel}
        title={t('rides.detail.actions.uncancel')}
        message={t('rides.detail.confirmations.uncancel')}
        confirmText={t('rides.detail.actions.uncancel')}
        variant="info"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('actions.delete')}
        message={t('rides.detail.confirmations.delete')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
