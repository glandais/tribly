import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetTrip,
  useUpdateTrip,
  useChangeTripSlug,
  getGetTripQueryKey,
  getListTripsQueryKey,
} from '../../api/endpoints/trips/trips'
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
        slug: teamSlug!,
        tripSlug: tripSlug!,
        data,
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          queryClient.invalidateQueries({ queryKey: getListTripsQueryKey(teamSlug!) })
          toast.success(i18next.t('trips.notifications.updated'))
          navigate(paths.trip(teamSlug!, tripSlug!))
        },
      }
    )
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { slug: teamSlug!, tripSlug: tripSlug!, data: { slug: newSlug } },
      {
        onSuccess: (updatedTrip) => {
          queryClient.invalidateQueries({ queryKey: getListTripsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          navigate(paths.tripEdit(teamSlug!, updatedTrip.slug), { replace: true })
        },
      }
    )
  }

  // Prepare initial values from fetched trip data
  const initialValues = { ...trip }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('trips.edit.title')}</h1>
        <p className="mt-1 text-gray-600">{t('trips.edit.subtitle', { teamName: team.name })}</p>
      </div>

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
    </div>
  )
}
