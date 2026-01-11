import { useState, useMemo, lazy, Suspense } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import {
  IconCalendar,
  IconUsers,
  IconPencil,
  IconMapPin,
  IconChevronDown,
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
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetRide,
  useUpdateRide,
  useDeleteRide,
  useJoinGroup,
  useLeaveGroup,
  getGetRideQueryKey,
} from '../../api/endpoints/rides/rides'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { Status } from '@/api/dto'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { RideGroupCard } from '../../components/ride/RideGroupCard'
import type { MapRouteItem } from '../../components/common/RoutesMapView'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'

// Lazy load the map component (pulls in map-vendor ~1MB and chart-vendor ~150KB)
const RoutesMapView = lazy(() =>
  import('../../components/common/RoutesMapView').then((m) => ({ default: m.RoutesMapView }))
)
import { useFormattedDate } from '../../utils/dateFormat'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { paths } from '@/config/paths'
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
      <Container size="xl" py="xl">
        <Stack align="center" py="xl">
          <Title order={2}>{t('rides.detail.notFound.title')}</Title>
          <Text c="dimmed">{t('rides.detail.notFound.message')}</Text>
          <Button component={Link} to={paths.team(teamSlug!)}>
            {t('rides.detail.notFound.backToRides')}
          </Button>
        </Stack>
      </Container>
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

  const handleJoinGroup = (groupId: string) => {
    setJoiningGroupId(groupId)
    joinMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug!, groupId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          notifications.show({ message: t('rides.notifications.joined'), color: 'green' })
        },
        onSettled: () => setJoiningGroupId(null),
      }
    )
  }

  const handleLeaveGroup = (groupId: string) => {
    setJoiningGroupId(groupId)
    leaveMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug!, groupId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          notifications.show({ message: t('rides.notifications.left'), color: 'green' })
        },
        onSettled: () => setJoiningGroupId(null),
      }
    )
  }

  return (
    <Container size="xl" py="xl">
      {/* Header */}
      <Paper shadow="xs" p="lg" mb="lg" withBorder>
        <Group justify="space-between" wrap="wrap" gap="md">
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
                      {updateMutation.isPending && <LoadingSpinner size="sm" />}
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
        <Group mt="md" gap="lg">
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
          <Group mt="sm" gap="lg" wrap="wrap">
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
      <SimpleGrid cols={{ base: 1, xl: 3 }} spacing="lg" mb="lg">
        {/* Groups list on left (takes 1 column on xl screens) */}
        <Box style={{ order: 2 }} data-order-xl="1">
          <Title order={4} mb="md">
            {t('rides.detail.groups.title')}
          </Title>
          {ride.groups && ride.groups.length > 0 ? (
            <Stack gap="sm">
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
            </Stack>
          ) : (
            <Text c="dimmed">{t('rides.detail.groups.empty')}</Text>
          )}
        </Box>

        {/* Map on right (takes 2 columns on xl screens) */}
        <Box style={{ order: 1, gridColumn: 'span 2' }} data-order-xl="2" visibleFrom="xl">
          {mapItems.length > 0 && (
            <Suspense fallback={<Skeleton height={500} radius="md" />}>
              <RoutesMapView
                items={mapItems}
                teamSlug={teamSlug!}
                highlightedItemId={highlightedGroupId}
                onItemHover={setHighlightedGroupId}
              />
            </Suspense>
          )}
        </Box>
        <Box style={{ order: 1 }} hiddenFrom="xl">
          {mapItems.length > 0 && (
            <Suspense fallback={<Skeleton height={400} radius="md" />}>
              <RoutesMapView
                items={mapItems}
                teamSlug={teamSlug!}
                highlightedItemId={highlightedGroupId}
                onItemHover={setHighlightedGroupId}
              />
            </Suspense>
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
