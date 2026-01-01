import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useCreateRideTemplate } from '../../hooks/useRideTemplate'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideTemplateEditor } from '../../components/ridetemplate/RideTemplateEditor'
import type { RideTemplateFormData } from '../../components/ridetemplate/RideTemplateEditor'
import { Visibility, Status } from '../../api/api'

export function CreateRideTemplatePage() {
  const { t } = useTranslation('rideTemplates')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  const createMutation = useCreateRideTemplate(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('create.loading')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={`/teams/${teamSlug}/ride-templates`} replace />
  }

  const initialValues = {
    name: '',
    markdown: undefined,
    visibility: team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public,
    status: Status.Published,
    groups: [
      {
        name: t('form.groups.defaultName'),
        averageSpeed: undefined,
        maxParticipants: undefined,
        isNew: true,
      },
    ],
  }

  const handleSubmit = (data: RideTemplateFormData) => {
    const filteredGroups = data.groups.filter((g) => g.name.trim())
    createMutation.mutate({
      name: data.name,
      markdown: data.markdown,
      visibility: data.visibility,
      status: data.status,
      groups: filteredGroups.map((g) => ({
        id: g.id,
        name: g.name,
        averageSpeed: g.averageSpeed,
        maxParticipants: g.maxParticipants,
      })),
    })
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/ride-templates`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('create.backToTemplates')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-1 text-gray-600">{t('create.subtitle', { teamName: team.name })}</p>
      </div>

      <RideTemplateEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/teams/${teamSlug}/ride-templates`)}
        isPending={createMutation.isPending}
        error={createMutation.error}
        submitButtonText={t('create.button')}
      />
    </div>
  )
}
