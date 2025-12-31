import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useTrip, useUpdateTrip } from '../../hooks/useTrip'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TripEditor } from '../../components/trip/TripEditor'
import type { TripFormData } from '../../components/trip/TripEditor'
import { toDateTimeLocalValue } from '../../utils/dateFormat'

export function EditTripPage() {
  const { t } = useTranslation('trips')
  const { teamSlug, tripSlug } = useParams<{ teamSlug: string; tripSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: trip, isLoading: isLoadingTrip } = useTrip(teamSlug, tripSlug)

  const updateMutation = useUpdateTrip(teamSlug, tripSlug!)

  if (isLoadingTeam || isLoadingTrip) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !trip) {
    return <Navigate to={`/teams/${teamSlug}/trips`} replace />
  }

  if (!team.enableTrips) {
    return <Navigate to={`/teams/${teamSlug}`} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={`/teams/${teamSlug}/trips/${tripSlug}`} replace />
  }

  const handleSubmit = async (data: TripFormData) => {
    const filteredStages = data.stages.filter((s) => s.name.trim())

    await updateMutation.mutateAsync({
      name: data.name,
      media: data.media,
      dateTime: data.dateTime,
      status: data.status,
      visibility: data.visibility,
      publishAt: data.publishAt,
      routeSlug: data.routeSlug,
      stages: filteredStages.map((s) => ({
        id: s.id,
        name: s.name,
        dateTime: s.dateTime,
        routeSlug: s.routeSlug,
        startPlaceId: s.startPlace?.id,
        endPlaceId: s.endPlace?.id,
        media: s.media,
      })),
    })

    navigate(`/teams/${teamSlug}/trips/${tripSlug}`)
  }

  // Prepare initial values from fetched trip data
  const initialValues = {
    name: trip.name,
    media: trip.media,
    dateTime: toDateTimeLocalValue(trip.dateTime),
    visibility: trip.visibility,
    status: trip.status,
    publishAt: trip.publishAt ? toDateTimeLocalValue(trip.publishAt) : undefined,
    routeSlug: trip.routeSlug,
    stages:
      trip.stages?.map((s) => ({
        id: s.id,
        name: s.name,
        dateTime: toDateTimeLocalValue(s.dateTime),
        routeSlug: s.routeSlug,
        startPlace: s.startPlace,
        endPlace: s.endPlace,
        media: s.media,
      })) || [],
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/trips/${tripSlug}`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('edit.backToTrip')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-1 text-gray-600">{t('edit.subtitle', { teamName: team.name })}</p>
      </div>

      <TripEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/teams/${teamSlug}/trips/${tripSlug}`)}
        isPending={updateMutation.isPending}
        error={updateMutation.error}
        submitButtonText={t('edit.button')}
      />
    </div>
  )
}
