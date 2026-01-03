import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useTeam } from '../../hooks/useTeam'
import { useRide, useUpdateRide } from '../../hooks/useRide'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideEditor } from '../../components/ride/RideEditor'
import type { RideFormData } from '../../components/ride/RideEditor'
import { toDateTimeLocalValue } from '../../utils/dateFormat'
import { paths } from '@/config/paths'

export function EditRidePage() {
  const { t } = useTranslation('rides')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: ride, isLoading: isLoadingRide } = useRide(teamSlug, rideSlug)

  const updateMutation = useUpdateRide(teamSlug, rideSlug!)

  if (isLoadingTeam || isLoadingRide) {
    return <LoadingPage message={tCommon('loading')} />
  }

  if (!team || !ride) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={paths.ride(teamSlug!, rideSlug!)} replace />
  }

  const handleSubmit = async (data: RideFormData) => {
    const filteredGroups = data.groups.filter((g) => g.name.trim())

    await updateMutation.mutateAsync({
      name: data.name,
      media: data.media,
      dateTime: data.dateTime,
      status: data.status,
      visibility: data.visibility,
      publishAt: data.publishAt,
      routeSlug: data.routeSlug,
      groups: filteredGroups,
    })

    navigate(paths.ride(teamSlug!, rideSlug!))
  }

  // Prepare initial values from fetched ride data
  const initialValues = {
    name: ride.name,
    media: ride.media,
    dateTime: toDateTimeLocalValue(ride.dateTime),
    visibility: ride.visibility,
    status: ride.status,
    publishAt: ride.publishAt ? toDateTimeLocalValue(ride.publishAt) : undefined,
    routeSlug: ride.routeSlug,
    groups:
      ride.groups?.map((g) => ({
        id: g.id,
        name: g.name,
        time: g.time,
        averageSpeed: g.averageSpeed,
        maxParticipants: g.maxParticipants,
        routeSlug: g.routeSlug,
      })) || [],
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-1 text-gray-600">{t('edit.subtitle', { teamName: team.name })}</p>
      </div>

      <RideEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.ride(teamSlug!, rideSlug!))}
        isPending={updateMutation.isPending}
        error={updateMutation.error}
        submitButtonText={tCommon('actions.save')}
      />
    </div>
  )
}
