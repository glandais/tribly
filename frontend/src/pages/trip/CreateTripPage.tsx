import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { useTeam } from '../../hooks/useTeam'
import { useCreateTrip, Visibility, Status } from '../../hooks/useTrip'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TripEditor } from '../../components/trip/TripEditor'
import type { TripFormData } from '../../components/trip/TripEditor'
import { toDateTimeLocalValue } from '../../utils/dateFormat'
import { defaultMedia } from '@/lib/apiUtils'

export function CreateTripPage() {
  const { t } = useTranslation('trips')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  const createMutation = useCreateTrip(teamSlug)

  const { t: tCommon } = useTranslation('common')

  if (isLoadingTeam) {
    return <LoadingPage message={tCommon('loading')} />
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
    return toDateTimeLocalValue(nextSaturday)
  }

  const tripStartDate = getNextSaturday()

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: defaultMedia(),
    dateTime: tripStartDate,
    visibility: team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public,
    status: Status.Draft,
    publishAt: undefined,
    routeSlug: undefined,
    stages: [
      {
        name: t('create.form.stages.defaultName', { number: 1 }),
        dateTime: tripStartDate,
        routeSlug: undefined,
        startPlace: undefined,
        endPlace: undefined,
        media: defaultMedia(),
        isNew: true,
      },
    ],
  }

  const handleSubmit = (data: TripFormData) => {
    const filteredStages = data.stages.filter((s) => s.name.trim())
    createMutation.mutate(
      {
        name: data.name,
        media: data.media,
        dateTime: data.dateTime,
        status: data.status,
        visibility: data.visibility,
        publishAt: data.publishAt,
        routeSlug: data.routeSlug,
        stages: filteredStages.map((s) => ({
          name: s.name,
          dateTime: s.dateTime,
          routeSlug: s.routeSlug,
          startPlaceId: s.startPlace?.id,
          endPlaceId: s.endPlace?.id,
          media: s.media,
        })),
      },
      {
        onSuccess: () => {
          navigate(paths.team(teamSlug!))
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-1 text-gray-600">{t('create.subtitle', { teamName: team.name })}</p>
      </div>

      <TripEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.team(teamSlug!))}
        isPending={createMutation.isPending}
        error={createMutation.error}
        submitButtonText={t('create.button')}
      />
    </div>
  )
}
