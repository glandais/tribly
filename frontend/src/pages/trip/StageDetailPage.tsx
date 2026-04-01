import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  Container,
  Paper,
  Group,
  Stack,
  Title,
  Text,
  Badge,
  Button,
  Box,
  Anchor,
} from '@mantine/core'
import { IconCalendar, IconMapPin, IconPencil } from '@tabler/icons-react'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetTrip } from '../../api/endpoints/trips/trips'
import { useGetRoute } from '../../api/endpoints/routes/routes'
import { RouteDetailView } from '../../components/route/RouteDetailView'
import { paths } from '../../config/paths'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TripLayout } from '../../components/trip/TripLayout'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { Status } from '@/api/dto'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const statusColors: Record<Status, 'gray' | 'green' | 'red'> = {
  [Status.DRAFT]: 'gray',
  [Status.PUBLISHED]: 'green',
  [Status.CANCELLED]: 'red',
}

export function StageDetailPage() {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, tripSlug, stageSlug } = useParams<{
    teamSlug: string
    tripSlug: string
    stageSlug: string
  }>()
  useAuth() // For authentication context

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

  // Find stage and its route slug (derived before hooks to maintain consistent hook order)
  const stage = trip?.stages?.find((s) => s.slug === stageSlug)
  const routeSlug = stage?.routeSlug

  // Fetch route if stage has one
  const { data: route, isLoading: isLoadingRoute } = useGetRoute(teamSlug!, routeSlug ?? '', {
    query: { enabled: !!teamSlug && !!routeSlug },
  })

  useCanonicalPath(
    team && trip && stageSlug ? paths.stage(team.slug, trip.slug, stageSlug) : undefined
  )

  if (isLoadingTeam || isLoadingTrip || (routeSlug && isLoadingRoute)) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  if (team && (!team.enableTrips || !team.enableRoutes)) {
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

  if (!stage) {
    return (
      <Container size="xl" py="xl">
        <Paper withBorder p="xl" ta="center">
          <Title order={2} mb="xs">
            {t('trips.stage.notFound.title')}
          </Title>
          <Text c="dimmed" mb="lg">
            {t('trips.stage.notFound.message')}
          </Text>
          <Button component={Link} to={paths.trip(teamSlug!, tripSlug!)}>
            {t('trips.stage.notFound.backToTrip')}
          </Button>
        </Paper>
      </Container>
    )
  }

  const stageIndex = trip.stages?.findIndex((s) => s.slug === stageSlug) ?? 0
  const isAdmin = team?.role === 'ADMIN'
  const isOrganizer = team?.role === 'ORGANIZER'
  const canEdit = isAdmin || isOrganizer

  return (
    <Container size="xl" py="xl">
      {/* Trip Header */}
      <Paper withBorder p="lg" mb="lg">
        <Group justify="space-between" align="flex-start" wrap="wrap">
          <Group style={{ minWidth: 0 }}>
            <EntityLogo
              logo={stage.media.assets.logo ?? trip.media.assets.logo}
              alt={stage.name || trip.name}
              size="lg"
            />
            <Box>
              <Anchor
                component={Link}
                to={paths.trip(teamSlug!, tripSlug!)}
                c="dimmed"
                size="sm"
                underline="hover"
              >
                {trip.name}
              </Anchor>
              <Title order={2} lineClamp={1}>
                {stage.name}
              </Title>
            </Box>
            <Badge color={statusColors[trip.status]}>
              {t(`status.${trip.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
            </Badge>
          </Group>

          {canEdit && (
            <Button
              component={Link}
              to={paths.tripEdit(teamSlug!, tripSlug!)}
              variant="outline"
              leftSection={<IconPencil size={16} />}
            >
              {t('actions.edit')}
            </Button>
          )}
        </Group>
      </Paper>

      {/* Stage tabs + Stage content */}
      <TripLayout trip={trip} teamSlug={teamSlug!} currentTab={stageSlug!}>
        <Stack>
          {/* Stage header */}
          <Paper withBorder p="lg">
            <Group mb="md">
              <Badge size="xl" variant="light" color="primary" circle>
                {stageIndex + 1}
              </Badge>
              <Box>
                <Title order={2}>{stage.name}</Title>
                <Group gap="xs" mt="xs">
                  <IconCalendar size={16} />
                  <Text size="sm" c="dimmed">
                    {formatDateTime(stage.dateTime)}
                  </Text>
                </Group>
              </Box>
            </Group>

            {/* Description */}
            {stage.media.markdown && (
              <Box mb="md">
                <MediaDisplay media={stage.media} />
              </Box>
            )}

            {/* Places */}
            {(stage.startPlace || stage.endPlace) && (
              <Stack gap="sm">
                {stage.startPlace && (
                  <Group gap="sm">
                    <IconMapPin size={20} color="var(--mantine-color-green-text)" />
                    <Box>
                      <Text size="xs" c="dimmed">
                        {t('startPlace')}
                      </Text>
                      <Text fw={500}>{stage.startPlace.name}</Text>
                    </Box>
                  </Group>
                )}
                {stage.endPlace && (
                  <Group gap="sm">
                    <IconMapPin size={20} color="var(--mantine-color-red-text)" />
                    <Box>
                      <Text size="xs" c="dimmed">
                        {t('endPlace')}
                      </Text>
                      <Text fw={500}>{stage.endPlace.name}</Text>
                    </Box>
                  </Group>
                )}
              </Stack>
            )}
          </Paper>

          {/* Route details */}
          {route && (
            <Box>
              <Group justify="space-between" mb="md">
                <Title order={3}>{route.name}</Title>
                <Button
                  component={Link}
                  to={paths.route(teamSlug!, route.slug)}
                  variant="subtle"
                  size="sm"
                >
                  {t('trips.stage.viewRouteDetails')}
                </Button>
              </Group>
              <RouteDetailView route={route} teamSlug={teamSlug} showInfo={false} />
            </Box>
          )}
        </Stack>
      </TripLayout>
    </Container>
  )
}
