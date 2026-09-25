import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { Container, Stack, Title, Text } from '@mantine/core'
import { useCreateTrip } from '../../api/endpoints/trips/trips'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { Status, TripRequest } from '@/api/dto'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TripEditor } from '../../components/trip/TripEditor'
import { defaultMedia } from '@/lib/apiUtils'
import { useCreateTripFormData } from './tripFormData'
import { nextWeekdayAt, useEffectiveTimezone } from '@/utils/dateFormat'

export function CreateTripPage() {
  const { t } = useTranslation()
  // Default dates are wall times in the effective timezone, which reads UTC on the hydration render
  // and the visitor's real zone right after (unless they set one). The editor seeds its form once,
  // so it is keyed on the zone: without a preference it remounts, untouched, with the default
  // recomputed in the visitor's own zone.
  const { timezone } = useEffectiveTimezone()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { team: teamQuery } = useCreateTripFormData(teamSlug)
  const { data: team, isLoading: isLoadingTeam } = teamQuery

  const createMutation = useCreateTrip()

  useCanonicalPath(team ? paths.tripNew(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  if (!team.enableTrips || !team.enableRoutes) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  // Next Saturday at 8am (trips often start on weekends)
  const tripStartDate = nextWeekdayAt(6, 8, timezone)

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: defaultMedia(),
    dateTime: tripStartDate,
    visibility: team.visibility,
    status: Status.DRAFT,
    publishAt: undefined,
    routeSlug: undefined,
    stages: [
      {
        name: t('trips.create.form.stages.defaultName', { number: 1 }),
        dateTime: tripStartDate,
        routeSlug: undefined,
        startPlace: undefined,
        endPlace: undefined,
        media: defaultMedia(),
        isNew: true,
      },
    ],
  }

  const handleSubmit = (data: TripRequest) => {
    createMutation.mutate(
      {
        teamSlug: teamSlug!,
        data,
      },
      {
        onSuccess: (trip) => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: i18next.t('trips.notifications.created'), color: 'green' })
          navigate(paths.trip(teamSlug!, trip.slug))
        },
      }
    )
  }

  return (
    <Container size="sm" py="xl">
      <Stack gap="xs">
        <Title order={1}>{t('trips.create.title')}</Title>
        <Text c="dimmed">{t('trips.create.subtitle', { teamName: team.name })}</Text>
      </Stack>

      <TripEditor
        key={timezone}
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.team(teamSlug!))}
        isPending={createMutation.isPending}
        submitButtonText={t('trips.create.button')}
      />
    </Container>
  )
}
