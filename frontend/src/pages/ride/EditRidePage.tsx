import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useRide, useUpdateRide } from '../../hooks/useRide'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideEditor } from '../../components/ride/RideEditor'
import type { RideFormData } from '../../components/ride/RideEditor'
import { toDateTimeLocalValue } from '../../utils/dateFormat'

export function EditRidePage() {
  const { t } = useTranslation('rides')
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: ride, isLoading: isLoadingRide } = useRide(teamSlug, rideSlug)

  const updateMutation = useUpdateRide(teamSlug, rideSlug!)

  if (isLoadingTeam || isLoadingRide) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !ride) {
    return <Navigate to={`/teams/${teamSlug}/rides`} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={`/teams/${teamSlug}/rides/${rideSlug}`} replace />
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

    navigate(`/teams/${teamSlug}/rides/${rideSlug}`)
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
        averageSpeed: g.averageSpeed,
        maxParticipants: g.maxParticipants,
        routeSlug: g.routeSlug,
      })) || [],
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/rides/${rideSlug}`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('edit.backToRide')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-1 text-gray-600">{t('edit.subtitle', { teamName: team.name })}</p>
      </div>

      <RideEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/teams/${teamSlug}/rides/${rideSlug}`)}
        isPending={updateMutation.isPending}
        error={updateMutation.error}
        submitButtonText={t('edit.button')}
      />
    </div>
  )
}
