import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useCreateTemplate,
  getListTemplatesQueryKey,
} from '@/api/endpoints/ride-templates/ride-templates'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideTemplateEditor } from '../../components/ridetemplate/RideTemplateEditor'
import { Visibility, Status, RideTemplateRequest } from '@/api/dto'
import { paths } from '@/config/paths'

export function CreateRideTemplatePage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  const createMutation = useCreateTemplate()
  const { t } = useTranslation()
  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={paths.rideTemplates(teamSlug!)} replace />
  }

  const initialValues = {
    name: '',
    markdown: '',
    visibility: team.visibility === Visibility.TEAM ? Visibility.TEAM : Visibility.PUBLIC,
    status: Status.PUBLISHED,
    groups: [
      {
        name: t('rideTemplates.form.groups.defaultName', { number: 1 }),
      },
    ],
  }

  const handleSubmit = (data: RideTemplateRequest) => {
    createMutation.mutate(
      {
        slug: teamSlug!,
        data,
      },
      {
        onSuccess: () => {
          navigate(paths.rideTemplates(teamSlug!))
          queryClient.invalidateQueries({ queryKey: getListTemplatesQueryKey(teamSlug!) })
          toast.success(i18next.t('rideTemplates.notifications.created'))
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('rideTemplates.create.title')}</h1>
        <p className="mt-1 text-gray-600">
          {t('rideTemplates.create.subtitle', { teamName: team.name })}
        </p>
      </div>

      <RideTemplateEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.rideTemplates(teamSlug!))}
        isPending={createMutation.isPending}
        submitButtonText={t('rideTemplates.create.button')}
      />
    </div>
  )
}
