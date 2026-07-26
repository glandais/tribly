import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { Container, Stack, Title, Text } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetRide,
  useUpdateRide,
  useChangeRideSlug,
  getGetRideQueryKey,
} from '../../api/endpoints/rides/rides'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideEditor } from '../../components/ride/RideEditor'
import { paths } from '@/config/paths'
import { RideRequest } from '@/api/dto'

export function EditRidePage() {
  const { t } = useTranslation()
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: ride, isLoading: isLoadingRide } = useGetRide(teamSlug!, rideSlug!, {
    query: { enabled: !!teamSlug && !!rideSlug },
  })

  const updateMutation = useUpdateRide()
  const changeSlugMutation = useChangeRideSlug()

  useCanonicalPath(team && ride ? paths.rideEdit(team.slug, ride.slug) : undefined)

  if (isLoadingTeam || isLoadingRide) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !ride) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  if (!team.enableRides || !team.enableRoutes) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={paths.ride(teamSlug!, rideSlug!)} replace />
  }

  const handleSubmit = (data: RideRequest) => {
    const filteredGroups = data.groups.filter((g) => g.name.trim())

    updateMutation.mutate(
      {
        teamSlug: teamSlug!,
        rideSlug: rideSlug!,
        data: {
          ...data,
          groups: filteredGroups,
        },
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: i18next.t('rides.notifications.updated'), color: 'green' })
          navigate(paths.ride(teamSlug!, rideSlug!))
        },
      }
    )
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { teamSlug: teamSlug!, rideSlug: rideSlug!, data: { slug: newSlug } },
      {
        onSuccess: (updatedRide) => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          navigate(paths.rideEdit(teamSlug!, updatedRide.slug), { replace: true })
        },
      }
    )
  }

  // Prepare initial values from fetched ride data.
  // `RideGroupDto` carries `leader` (an object); `GroupRequest` wants `leaderId`. Without this
  // mapping the field is simply absent on submit, and the server applies
  // `setLeader(resolveLeader(null))` unconditionally — so merely editing a ride used to wipe
  // every group's designated leader.
  const initialValues = {
    ...ride,
    startPlaceId: ride.startPlace?.id,
    endPlaceId: ride.endPlace?.id,
    groups: ride.groups?.map((group) => ({ ...group, leaderId: group.leader?.id })),
  } as RideRequest

  const initialLeaders = Object.fromEntries(
    (ride.groups ?? [])
      .map((group) => group.leader)
      .filter((leader): leader is NonNullable<typeof leader> => !!leader)
      .map((leader) => [leader.id, leader])
  )

  return (
    <Container size="sm" py="xl">
      <Stack gap="xs">
        <Title order={1}>{t('rides.edit.title')}</Title>
        <Text c="dimmed">{t('rides.edit.subtitle', { teamName: team.name })}</Text>
      </Stack>

      <RideEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        initialLeaders={initialLeaders}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.ride(teamSlug!, rideSlug!))}
        isPending={updateMutation.isPending}
        submitButtonText={t('actions.save')}
        currentSlug={rideSlug!}
        onSlugChange={handleSlugChange}
        canEditSlug={canEdit}
      />
    </Container>
  )
}
