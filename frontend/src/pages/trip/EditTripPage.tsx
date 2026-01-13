import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { Container, Stack, Title, Text } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetTrip,
  useUpdateTrip,
  useChangeTripSlug,
  getGetTripQueryKey,
} from '../../api/endpoints/trips/trips'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TripEditor } from '../../components/trip/TripEditor'
import { TripRequest } from '@/api/dto'

export function EditTripPage() {
  const { t } = useTranslation()
  const { teamSlug, tripSlug } = useParams<{ teamSlug: string; tripSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: trip, isLoading: isLoadingTrip } = useGetTrip(teamSlug!, tripSlug!, {
    query: { enabled: !!teamSlug && !!tripSlug },
  })

  const updateMutation = useUpdateTrip()
  const changeSlugMutation = useChangeTripSlug()

  useCanonicalPath(team && trip ? paths.tripEdit(team.slug, trip.slug) : undefined)

  if (isLoadingTeam || isLoadingTrip) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !trip || !team.enableTrips) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={paths.trip(teamSlug!, tripSlug!)} replace />
  }

  const handleSubmit = (data: TripRequest) => {
    updateMutation.mutate(
      {
        teamSlug: teamSlug!,
        tripSlug: tripSlug!,
        data,
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: i18next.t('trips.notifications.updated'), color: 'green' })
          navigate(paths.trip(teamSlug!, tripSlug!))
        },
      }
    )
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { teamSlug: teamSlug!, tripSlug: tripSlug!, data: { slug: newSlug } },
      {
        onSuccess: (updatedTrip) => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          navigate(paths.tripEdit(teamSlug!, updatedTrip.slug), { replace: true })
        },
      }
    )
  }

  // Prepare initial values from fetched trip data
  const initialValues = { ...trip }

  return (
    <Container size="sm" py="xl">
      <Stack gap="xs">
        <Title order={1}>{t('trips.edit.title')}</Title>
        <Text c="dimmed">{t('trips.edit.subtitle', { teamName: team.name })}</Text>
      </Stack>

      <TripEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.trip(teamSlug!, tripSlug!))}
        isPending={updateMutation.isPending}
        submitButtonText={t('actions.save')}
        currentSlug={tripSlug!}
        onSlugChange={handleSlugChange}
        canEditSlug={canEdit}
      />
    </Container>
  )
}
