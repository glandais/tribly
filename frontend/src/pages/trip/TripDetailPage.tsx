import { useState, lazy, Suspense } from 'react'
import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { paths } from '../../config/paths'
import {
  IconCalendar,
  IconUsers,
  IconPencil,
  IconStack2,
  IconChevronDown,
} from '@tabler/icons-react'
import {
  Box,
  Button,
  Menu,
  Container,
  Paper,
  Group,
  Stack,
  Title,
  Text,
  Badge,
  Alert,
  Anchor,
  Skeleton,
  Loader,
} from '@mantine/core'
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
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TripStageCard } from '../../components/trip/TripStageCard'
import { TripLayout } from '../../components/trip/TripLayout'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'

// Lazy load the map component (pulls in map-vendor ~1MB and chart-vendor ~150KB)
const RoutesMapView = lazy(() =>
  import('../../components/route/RoutesMapView').then((m) => ({ default: m.RoutesMapView }))
)
import { useFormattedDate } from '../../utils/dateFormat'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const statusColors: Record<Status, 'gray' | 'green' | 'red'> = {
  [Status.DRAFT]: 'gray',
  [Status.PUBLISHED]: 'green',
  [Status.CANCELLED]: 'red',
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
      <Container size="xl" py="xl">
        <Paper withBorder p="xl" ta="center">
          <Title order={2} mb="xs">
            {t('trips.detail.notFound.title')}
          </Title>
          <Text c="dimmed" mb="lg">
            {t('trips.detail.notFound.message')}
          </Text>
          <Button component={Link} to={paths.team(teamSlug!)}>
            {t('trips.detail.notFound.backToTrips')}
          </Button>
        </Paper>
      </Container>
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
          notifications.show({ message: t('trips.notifications.published'), color: 'green' })
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
          notifications.show({ message: t('trips.notifications.unpublished'), color: 'green' })
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
          notifications.show({ message: t('trips.notifications.cancelled'), color: 'green' })
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
          notifications.show({ message: t('trips.notifications.uncancelled'), color: 'green' })
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
          notifications.show({ message: t('trips.notifications.deleted'), color: 'green' })
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
          notifications.show({ message: t('trips.notifications.joined'), color: 'green' })
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
          notifications.show({ message: t('trips.notifications.left'), color: 'green' })
        },
      }
    )
  }

  return (
    <Container size="xl" py="xl">
      {/* Header */}
      <Paper withBorder p="lg" mb="lg">
        <Group justify="space-between" align="flex-start" wrap="wrap">
          <Group style={{ minWidth: 0 }}>
            <EntityLogo logo={trip.media.assets.logo} alt={trip.name} size="lg" />
            <Title order={2} lineClamp={1}>
              {trip.name}
            </Title>
            <Badge color={statusColors[trip.status]}>
              {t(`status.${trip.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </Badge>
          </Group>

          <Group gap="sm" wrap="wrap">
            {/* Join/Leave button for members */}
            {canJoinTrip && (
              <Button onClick={handleJoin} loading={joinMutation.isPending}>
                {t('trips.detail.actions.join')}
              </Button>
            )}
            {hasJoined && (
              <Button variant="outline" onClick={handleLeave} loading={leaveMutation.isPending}>
                {t('trips.detail.actions.leave')}
              </Button>
            )}

            {canEdit && (
              <Button.Group>
                <Button
                  component={Link}
                  to={paths.tripEdit(teamSlug!, tripSlug!)}
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
                    {trip.status === Status.DRAFT && (
                      <Menu.Item
                        onClick={handlePublish}
                        disabled={updateMutation.isPending}
                        color="success"
                        leftSection={updateMutation.isPending ? <Loader size="sm" /> : undefined}
                      >
                        {t('actions.publish')}
                      </Menu.Item>
                    )}
                    {trip.status === Status.PUBLISHED && (
                      <>
                        <Menu.Item onClick={() => setShowUnpublishConfirm(true)} color="warning">
                          {t('actions.unpublish')}
                        </Menu.Item>
                        <Menu.Item onClick={() => setShowCancelConfirm(true)} color="warning">
                          {t('trips.detail.actions.cancel')}
                        </Menu.Item>
                      </>
                    )}
                    {trip.status === Status.CANCELLED && (
                      <Menu.Item onClick={() => setShowUncancelConfirm(true)} color="green">
                        {t('trips.detail.actions.uncancel')}
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
        </Group>

        <Box mt="md">
          <MediaDisplay media={trip.media} />
        </Box>
        {trip.status === Status.DRAFT && trip.publishAt && (
          <Group mt="sm" gap="xs">
            <IconCalendar size={16} color="var(--mantine-color-yellow-text)" />
            <Text size="sm" c="var(--mantine-color-yellow-text)">
              {t('trips.detail.scheduledPublish', {
                date: formatDateTime(trip.publishAt),
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
              {t('participantCount', { count: trip.participantCount })}
            </Text>
          </Group>
          <Group gap="xs">
            <IconStack2 size={16} />
            <Text size="sm" c="dimmed">
              {t('trips.card.stageCount', { count: trip.stageCount })}
            </Text>
          </Group>
        </Group>
      </Paper>

      {/* Stage tabs + Overview content */}
      <TripLayout trip={trip} teamSlug={teamSlug!} currentTab="overview">
        <Stack>
          {/* Map */}
          {trip.stages && trip.stages.length > 0 && (
            <Suspense fallback={<Skeleton height={500} radius="md" />}>
              <RoutesMapView
                items={trip.stages}
                teamSlug={teamSlug!}
                highlightedItemId={highlightedStageId}
                onItemHover={setHighlightedStageId}
              />
            </Suspense>
          )}

          {/* Stages list */}
          <Box>
            <Title order={3} mb="md">
              {t('trips.detail.stages.title')}
            </Title>
            {trip.stages && trip.stages.length > 0 ? (
              <Stack gap="sm">
                {trip.stages.map((stage, index) => (
                  <TripStageCard
                    key={stage.id}
                    stage={stage}
                    index={index}
                    teamSlug={teamSlug!}
                    tripSlug={trip.slug}
                    onHover={setHighlightedStageId}
                    isHighlighted={highlightedStageId === stage.id}
                  />
                ))}
              </Stack>
            ) : (
              <Text c="dimmed">{t('trips.detail.stages.empty')}</Text>
            )}
          </Box>

          {/* Participants section */}
          {trip.participants && trip.participants.length > 0 && (
            <Paper withBorder p="lg">
              <Title order={3} mb="md">
                {t('trips.detail.participants.title')}
              </Title>
              <Group gap="xs">
                {trip.participants.map((participant) => (
                  <Badge key={participant.id} variant="light" color="gray" size="lg">
                    {participant.displayName}
                  </Badge>
                ))}
              </Group>
            </Paper>
          )}

          {/* Info for non-members */}
          {!isMember && isAuthenticated && (
            <Alert color="yellow" variant="light">
              <Text>
                {t('trips.detail.nonMember.message')}{' '}
                <Anchor component={Link} to={paths.team(teamSlug!)} fw={500}>
                  {t('trips.detail.nonMember.viewTeam')}
                </Anchor>
              </Text>
            </Alert>
          )}

          {!isAuthenticated && (
            <Alert color="blue" variant="light">
              <Text>
                {t('trips.detail.notAuthenticated.message')}{' '}
                <Anchor component={Link} to="/login" fw={500}>
                  {t('trips.detail.notAuthenticated.signIn')}
                </Anchor>
              </Text>
            </Alert>
          )}

          {/* Comments Section - only visible to team members */}
          {isMember && (
            <Box>
              <CommentSection
                teamSlug={teamSlug!}
                entityType="trips"
                entitySlug={tripSlug!}
                isOrganizer={canEdit}
              />
            </Box>
          )}
        </Stack>
      </TripLayout>

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
    </Container>
  )
}
