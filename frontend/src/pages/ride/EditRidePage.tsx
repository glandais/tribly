import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetRide,
  useUpdateRide,
  useChangeRideSlug,
  getListRidesQueryKey,
  getGetRideQueryKey,
} from '../../api/endpoints/rides/rides'
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

  if (isLoadingTeam || isLoadingRide) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !ride) {
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
        slug: teamSlug!,
        rideSlug: rideSlug!,
        data: {
          ...data,
          groups: filteredGroups,
        },
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListRidesQueryKey(teamSlug!) })
          toast.success(i18next.t('rides.notifications.updated'))
          navigate(paths.ride(teamSlug!, rideSlug!))
        },
      }
    )
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { slug: teamSlug!, rideSlug: rideSlug!, data: { slug: newSlug } },
      {
        onSuccess: (updatedRide) => {
          queryClient.invalidateQueries({ queryKey: getListRidesQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          navigate(paths.rideEdit(teamSlug!, updatedRide.slug), { replace: true })
        },
      }
    )
  }

  // Prepare initial values from fetched ride data
  const initialValues = {
    ...ride,
    startPlaceId: ride.startPlace?.id,
    endPlaceId: ride.endPlace?.id,
  } as RideRequest

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('rides.edit.title')}</h1>
        <p className="mt-1 text-gray-600">{t('rides.edit.subtitle', { teamName: team.name })}</p>
      </div>

      <RideEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.ride(teamSlug!, rideSlug!))}
        isPending={updateMutation.isPending}
        submitButtonText={t('actions.save')}
        currentSlug={rideSlug!}
        onSlugChange={handleSlugChange}
        canEditSlug={canEdit}
      />
    </div>
  )
}
