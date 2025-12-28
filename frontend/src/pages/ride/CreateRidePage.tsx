import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useCreateRide, Visibility, Status } from '../../hooks/useRide'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideEditor } from '../../components/ride/RideEditor'
import type { RideFormData } from '../../components/ride/RideEditor'
import { toDateTimeLocalValue } from '../../utils/dateFormat'

export function CreateRidePage() {
  const { t } = useTranslation('rides')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  const createMutation = useCreateRide(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={`/teams/${teamSlug}/rides`} replace />
  }

  // Calculate next Sunday at 8am
  const getNextSunday = () => {
    const today = new Date()
    const daysUntilSunday = (7 - today.getDay()) % 7 || 7
    const nextSunday = new Date(today)
    nextSunday.setDate(today.getDate() + daysUntilSunday)
    nextSunday.setHours(8, 0, 0, 0)
    return toDateTimeLocalValue(nextSunday)
  }

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: { markdown: '', assets: [] },
    dateTime: getNextSunday(),
    visibility: team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public,
    status: Status.Draft,
    publishAt: undefined,
    routeSlug: undefined,
    groups: [
      {
        name: t('create.form.groups.defaultName'),
        averageSpeed: undefined,
        maxParticipants: undefined,
        routeSlug: undefined,
        isNew: true,
      },
    ],
  }

  const handleSubmit = (data: RideFormData) => {
    const filteredGroups = data.groups.filter((g) => g.name.trim())
    createMutation.mutate(
      {
        name: data.name,
        media: data.media,
        dateTime: data.dateTime,
        status: data.status,
        visibility: data.visibility,
        publishAt: data.publishAt,
        routeSlug: data.routeSlug,
        groups: filteredGroups,
      },
      {
        onSuccess: () => {
          navigate(`/teams/${teamSlug}/rides`)
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/rides`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('create.backToRides')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-1 text-gray-600">{t('create.subtitle', { teamName: team.name })}</p>
      </div>

      <RideEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/teams/${teamSlug}/rides`)}
        isPending={createMutation.isPending}
        error={createMutation.error}
        submitButtonText={t('create.button')}
      />
    </div>
  )
}
