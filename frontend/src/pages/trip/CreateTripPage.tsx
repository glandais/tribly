import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useCreateTrip, getListTripsQueryKey } from '../../api/endpoints/trips/trips'
import { Visibility, Status, TripRequest } from '@/api/dto'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TripEditor } from '../../components/trip/TripEditor'
import { defaultMedia } from '@/lib/apiUtils'

export function CreateTripPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  const createMutation = useCreateTrip()

  useCanonicalPath(team ? paths.tripNew(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  if (!team.enableTrips) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  // Calculate next Saturday at 8am (trips often start on weekends)
  const getNextSaturday = () => {
    const today = new Date()
    const daysUntilSaturday = (6 - today.getDay() + 7) % 7 || 7
    const nextSaturday = new Date(today)
    nextSaturday.setDate(today.getDate() + daysUntilSaturday)
    nextSaturday.setHours(8, 0, 0, 0)
    return nextSaturday.toISOString()
  }

  const tripStartDate = getNextSaturday()

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: defaultMedia(),
    dateTime: tripStartDate,
    visibility: team.visibility === Visibility.TEAM ? Visibility.TEAM : Visibility.PUBLIC,
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
        slug: teamSlug!,
        data,
      },
      {
        onSuccess: (trip) => {
          queryClient.invalidateQueries({ queryKey: getListTripsQueryKey(teamSlug!) })
          toast.success(i18next.t('trips.notifications.created'))
          navigate(paths.trip(teamSlug!, trip.slug))
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('trips.create.title')}</h1>
        <p className="mt-1 text-gray-600">{t('trips.create.subtitle', { teamName: team.name })}</p>
      </div>

      <TripEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.team(teamSlug!))}
        isPending={createMutation.isPending}
        submitButtonText={t('trips.create.button')}
      />
    </div>
  )
}
