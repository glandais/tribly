import { useState, useMemo, lazy, Suspense } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import dayjs from 'dayjs'
import { notifications } from '@mantine/notifications'
import {
  IconCalendar,
  IconUsers,
  IconPencil,
  IconMapPin,
  IconChevronDown,
  IconUsersGroup,
} from '@tabler/icons-react'
import {
  Container,
  Paper,
  Group,
  Stack,
  Title,
  Text,
  Button,
  Menu,
  Badge,
  SimpleGrid,
  Box,
  Alert,
  Anchor,
  Skeleton,
  Loader,
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetRide,
  useUpdateRide,
  useDeleteRide,
  useUndeleteRide,
  useJoinGroup,
  useLeaveGroup,
  getGetRideQueryKey,
} from '../../api/endpoints/rides/rides'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { useRoutesBulk } from '@/hooks/useRoutesBulk'
import { Status } from '@/api/dto'
import type { RideDto, RouteDetailDto } from '@/api/dto'
import { ApiClientError } from '@/lib/apiError'
import { useAuth } from '../../hooks/useAuth'
import { EmptyState } from '../../components/common/EmptyState'
import { QueryStateBoundary } from '../../components/common/QueryStateBoundary'
import { DetailPageSkeleton } from '../../components/common/DetailPageSkeleton'
import { RideGroupCard } from '../../components/ride/RideGroupCard'
import { TeamContextBanner } from '../../components/team/TeamContextBanner'
import type { MapRouteItem } from '../../components/route/RoutesMapView'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'

// Lazy load the map component (pulls in map-vendor ~1MB and chart-vendor ~150KB)
const RoutesMapView = lazy(() =>
  import('../../components/route/RoutesMapView').then((m) => ({ default: m.RoutesMapView }))
)
import { useFormattedDate } from '../../utils/dateFormat'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { paths } from '@/config/paths'
import { ErrorBoundary } from '../../components/common/ErrorBoundary'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const statusColors: Record<Status, string> = {
  [Status.DRAFT]: 'gray',
  [Status.PUBLISHED]: 'green',
  [Status.CANCELLED]: 'red',
}

export function RideDetailPage() {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>()
  const { isAuthenticated } = useAuth()
  const [joiningGroupId, setJoiningGroupId] = useState<string | null>(null)
  // Failure message for the group whose join/leave just failed. Kept in the card rather than
  // in a toast: a toast fires far from the button, then vanishes, leaving the card unchanged.
  const [groupError, setGroupError] = useState<{ groupId: string; message: string } | null>(null)
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
    refetch,
  } = useGetRide(teamSlug!, rideSlug!, {
    query: { enabled: !!teamSlug && !!rideSlug },
  })

  const queryClient = useQueryClient()
  const navigate = useNavigate()
  const updateMutation = useUpdateRide()
  const deleteMutation = useDeleteRide()
  const undeleteMutation = useUndeleteRide()
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

  // One bulk request for every group's (or the ride's) route, instead of each `RideGroupCard`
  // fetching its own on first hover — groups frequently share the ride's route. The key string
  // is memoized on the slugs' own values so the request stays stable across unrelated renders.
  const groupRouteSlugsKey = (ride?.groups ?? [])
    .map((g) => g.routeSlug || ride?.routeSlug)
    .filter((s): s is string => !!s)
    .sort()
    .join(',')
  const groupRouteSlugs = useMemo(
    () => (groupRouteSlugsKey ? Array.from(new Set(groupRouteSlugsKey.split(','))) : []),
    [groupRouteSlugsKey]
  )
  const { data: groupRoutesBulk } = useRoutesBulk(teamSlug!, {
    // Only the GPX/FIT asset links are used — request the minimum track geometry the
    // contract allows (2 points) rather than the full simplified track.
    slug: groupRouteSlugs,
    points: 2,
  })
  const groupRoutesBySlug = useMemo(() => {
    const map = new Map<string, RouteDetailDto>()
    for (const route of groupRoutesBulk?.routes ?? []) {
      map.set(route.slug, route)
    }
    return map
  }, [groupRoutesBulk])

  useCanonicalPath(team && ride ? paths.ride(team.slug, ride.slug) : undefined)

  if (isLoadingTeam || isLoadingRide || error || !ride || !team) {
    return (
      <Container size="xl" py="xl">
        <QueryStateBoundary
          isLoading={isLoadingTeam || isLoadingRide}
          isError={!!error}
          error={error}
          isNotFound={!ride || !team}
          onRetry={() => void refetch()}
          skeleton={<DetailPageSkeleton />}
          notFound={{
            title: t('rides.detail.notFound.title'),
            message: t('rides.detail.notFound.message'),
            backTo: paths.team(teamSlug!),
            backLabel: t('rides.detail.notFound.backToRides'),
          }}
        >
          {null}
        </QueryStateBoundary>
      </Container>
    )
  }

  const isMember = !!team?.role
  const isAdmin = team?.role === 'ADMIN'
  const isOrganizer = team?.role === 'ORGANIZER'
  const canEdit = isAdmin || isOrganizer
  // A ride that has already happened is not joinable — `canJoinRide` used to ignore the date
  // entirely, so last year's rides kept a live "Join" button.
  const isPast = dayjs(ride.dateTime).isBefore(dayjs())
  const canJoinRide =
    isMember && ride.status === Status.PUBLISHED && !ride.registered && !isPast && !ride.full

  const formattedDate = formatDateTime(ride.dateTime)

  const handlePublish = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug!, data: { ...ride, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('rides.notifications.published'), color: 'green' })
        },
      }
    )
  }

  const handleUnpublish = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug!, data: { ...ride, status: Status.DRAFT } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('rides.notifications.unpublished'), color: 'green' })
        },
      }
    )
    setShowUnpublishConfirm(false)
  }

  const handleCancel = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug!, data: { ...ride, status: Status.CANCELLED } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('rides.notifications.cancelled'), color: 'green' })
        },
      }
    )
    setShowCancelConfirm(false)
  }

  const handleUncancel = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug!, data: { ...ride, status: Status.DRAFT } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('rides.notifications.uncancelled'), color: 'green' })
        },
      }
    )
    setShowUncancelConfirm(false)
  }

  const handleDelete = () => {
    deleteMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('rides.notifications.deleted'), color: 'green' })
          navigate(paths.team(teamSlug!))
        },
      }
    )
    setShowDeleteConfirm(false)
  }

  const handleRestore = () => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('rides.notifications.restored'), color: 'green' })
        },
      }
    )
  }

  const rideQueryKey = getGetRideQueryKey(teamSlug!, rideSlug!)

  /**
   * Flips `registered` on the ride and on one group in the cache, and returns the previous
   * snapshot so the caller can roll back. `countParticipants` moves with it so the card does
   * not show "8/20" next to a freshly-added avatar.
   */
  const applyOptimisticMembership = (groupId: string, joining: boolean) => {
    const previous = queryClient.getQueryData<RideDto>(rideQueryKey)
    if (!previous) return previous

    queryClient.setQueryData<RideDto>(rideQueryKey, {
      ...previous,
      registered: joining,
      registeredGroupId: joining ? groupId : undefined,
      groups: previous.groups?.map((group) =>
        group.id === groupId
          ? {
              ...group,
              registered: joining,
              countParticipants: group.countParticipants + (joining ? 1 : -1),
            }
          : group
      ),
    })
    return previous
  }

  const describeGroupError = (error: unknown) => {
    const code = error instanceof ApiClientError ? error.error.code : undefined
    // Codes are open-ended, so a generic fallback is mandatory rather than optional.
    return code
      ? t(`errors.api.${code}`, { defaultValue: t('rides.detail.groups.actionFailed') })
      : t('rides.detail.groups.actionFailed')
  }

  const mutateMembership = (groupId: string, joining: boolean) => {
    setJoiningGroupId(groupId)
    setGroupError(null)
    const previous = applyOptimisticMembership(groupId, joining)
    const mutation = joining ? joinMutation : leaveMutation

    mutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug!, groupId },
      {
        onSuccess: () => {
          notifications.show({
            message: t(joining ? 'rides.notifications.joined' : 'rides.notifications.left'),
            color: 'green',
          })
        },
        onError: (error) => {
          if (previous) queryClient.setQueryData(rideQueryKey, previous)
          setGroupError({ groupId, message: describeGroupError(error) })
        },
        onSettled: () => {
          setJoiningGroupId(null)
          queryClient.invalidateQueries({ queryKey: rideQueryKey })
        },
      }
    )
  }

  const handleJoinGroup = (groupId: string) => mutateMembership(groupId, true)
  const handleLeaveGroup = (groupId: string) => mutateMembership(groupId, false)

  return (
    <Container size="xl" py="xl">
      {/* Way back to the team: this page is not mounted inside TeamLayout */}
      <TeamContextBanner team={team} />

      {/* Header */}
      <Paper shadow="xs" p="lg" mb="lg" withBorder>
        <Group justify="space-between" wrap="wrap">
          <Group gap="sm" style={{ minWidth: 0 }}>
            <EntityLogo logo={ride.media.assets.logo} alt={ride.name} size="lg" />
            <Title
              order={2}
              style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}
            >
              {ride.name}
            </Title>
            <Badge color={statusColors[ride.status]} variant="light">
              {t(`status.${ride.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </Badge>
            {isPast && (
              <Badge color="gray" variant="light">
                {t('rides.detail.finished')}
              </Badge>
            )}
          </Group>

          {canEdit && (
            <Button.Group>
              <Button
                component={Link}
                to={paths.rideEdit(teamSlug!, rideSlug!)}
                variant="outline"
                leftSection={<IconPencil size={16} />}
              >
                {t('actions.edit')}
              </Button>
              <Menu position="bottom-end">
                <Menu.Target>
                  <Button variant="outline" px="xs">
                    <IconChevronDown size={16} />
                  </Button>
                </Menu.Target>
                <Menu.Dropdown>
                  {ride.status === Status.DRAFT && (
                    <Menu.Item
                      onClick={handlePublish}
                      disabled={updateMutation.isPending}
                      color="success"
                    >
                      {updateMutation.isPending && <Loader size="sm" />}
                      {t('actions.publish')}
                    </Menu.Item>
                  )}
                  {ride.status === Status.PUBLISHED && (
                    <>
                      <Menu.Item onClick={() => setShowUnpublishConfirm(true)} color="warning">
                        {t('actions.unpublish')}
                      </Menu.Item>
                      <Menu.Item onClick={() => setShowCancelConfirm(true)} color="warning">
                        {t('rides.detail.actions.cancel')}
                      </Menu.Item>
                    </>
                  )}
                  {ride.status === Status.CANCELLED && (
                    <Menu.Item onClick={() => setShowUncancelConfirm(true)} color="green">
                      {t('rides.detail.actions.uncancel')}
                    </Menu.Item>
                  )}
                  {ride.deleted && (
                    <Menu.Item
                      onClick={handleRestore}
                      color="green"
                      disabled={undeleteMutation.isPending}
                    >
                      {t('actions.restore')}
                    </Menu.Item>
                  )}
                  <Menu.Divider />
                  <Menu.Item onClick={() => setShowDeleteConfirm(true)} color="danger">
                    {t('actions.delete')}
                  </Menu.Item>
                </Menu.Dropdown>
              </Menu>
            </Button.Group>
          )}
        </Group>

        <Box mt="md">
          <MediaDisplay media={ride.media} />
        </Box>
        {ride.status === Status.DRAFT && ride.publishAt && (
          <Group mt="xs" gap="xs">
            <IconCalendar size={16} color="var(--mantine-color-yellow-text)" />
            <Text size="sm" c="var(--mantine-color-yellow-text)">
              {t('rides.detail.scheduledPublish', {
                date: formatDateTime(ride.publishAt),
              })}
            </Text>
          </Group>
        )}
        <Group mt="md">
          <Group gap="xs">
            <IconCalendar size={16} />
            <Text size="sm" c="dimmed">
              {formattedDate}
            </Text>
          </Group>
          <Group gap="xs">
            <IconUsers size={16} />
            <Text size="sm" c="dimmed">
              {t('participantCount', { count: ride.participantCount })}
            </Text>
          </Group>
        </Group>
        {/* Start and End Places */}
        {(ride.startPlace || ride.endPlace) && (
          <Group mt="sm" wrap="wrap">
            {ride.startPlace && (
              <Group gap="xs">
                <IconMapPin size={16} color="var(--mantine-color-green-text)" />
                <Text size="sm" fw={500} c="var(--mantine-color-green-text)">
                  {t('startPlace')}:
                </Text>
                <Text size="sm">
                  {ride.startPlace.name}
                  {ride.startPlace.address && (
                    <Text span c="dimmed">
                      {' '}
                      ({ride.startPlace.address})
                    </Text>
                  )}
                </Text>
              </Group>
            )}
            {ride.endPlace && (
              <Group gap="xs">
                <IconMapPin size={16} color="var(--mantine-color-red-text)" />
                <Text size="sm" fw={500} c="var(--mantine-color-red-text)">
                  {t('endPlace')}:
                </Text>
                <Text size="sm">
                  {ride.endPlace.name}
                  {ride.endPlace.address && (
                    <Text span c="dimmed">
                      {' '}
                      ({ride.endPlace.address})
                    </Text>
                  )}
                </Text>
              </Group>
            )}
          </Group>
        )}
      </Paper>

      {/* Map and Groups */}
      <SimpleGrid
        cols={{ base: 1, md: 2, xl: 3 }}
        spacing={{ base: 'md', sm: 'lg' }}
        mb={{ base: 'md', sm: 'lg' }}
      >
        {/* Map - shown first on mobile, spans 2 cols on xl */}
        <Box className="detail-map" style={{ order: 1 }}>
          {mapItems.length > 0 && (
            <ErrorBoundary variant="inline">
              <Suspense fallback={<Skeleton height={500} radius="md" />}>
                <RoutesMapView
                  items={mapItems}
                  teamSlug={teamSlug!}
                  highlightedItemId={highlightedGroupId}
                  onItemHover={setHighlightedGroupId}
                />
              </Suspense>
            </ErrorBoundary>
          )}
        </Box>

        {/* Groups list */}
        <Box style={{ order: 2 }}>
          <Title order={4} mb="md">
            {t('rides.detail.groups.title')}
          </Title>
          {ride.groups && ride.groups.length > 0 ? (
            <Stack gap="sm">
              {ride.groups.map((group) => {
                return (
                  <RideGroupCard
                    key={group.id}
                    group={group}
                    teamSlug={teamSlug!}
                    rideRouteSlug={ride.routeSlug}
                    routesBySlug={groupRoutesBySlug}
                    canJoin={canJoinRide}
                    onJoin={() => handleJoinGroup(group.id)}
                    onLeave={() => handleLeaveGroup(group.id)}
                    onHover={setHighlightedGroupId}
                    isHighlighted={highlightedGroupId === group.id}
                    isLoading={
                      joiningGroupId === group.id &&
                      (joinMutation.isPending || leaveMutation.isPending)
                    }
                    error={groupError?.groupId === group.id ? groupError.message : undefined}
                  />
                )
              })}
            </Stack>
          ) : (
            <EmptyState
              icon={<IconUsersGroup size={48} />}
              title={t('rides.detail.groups.emptyTitle')}
              description={t('rides.detail.groups.empty')}
            />
          )}
        </Box>
      </SimpleGrid>

      {/* Info for non-members */}
      {!isMember && isAuthenticated && (
        <Alert color="yellow" variant="light" mb="lg">
          <Text>
            {t('rides.detail.nonMember.message')}{' '}
            <Anchor component={Link} to={paths.team(teamSlug!)} fw={500}>
              {t('rides.detail.nonMember.viewTeam')}
            </Anchor>
          </Text>
        </Alert>
      )}

      {!isAuthenticated && (
        <Alert color="blue" variant="light" mb="lg">
          <Text>
            {t('rides.detail.notAuthenticated.message')}{' '}
            <Anchor component={Link} to="/login" fw={500}>
              {t('rides.detail.notAuthenticated.signIn')}
            </Anchor>
          </Text>
        </Alert>
      )}

      {/* Comments Section - only visible to team members */}
      {isMember && (
        <Box mt="lg">
          <CommentSection
            teamSlug={teamSlug!}
            entityType="rides"
            entitySlug={rideSlug!}
            isOrganizer={canEdit}
          />
        </Box>
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
    </Container>
  )
}
