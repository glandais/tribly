import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetTemplate,
  useUpdateTemplate,
  getGetTemplateQueryKey,
  getListTemplatesQueryKey,
} from '@/api/endpoints/ride-templates/ride-templates'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideTemplateEditor } from '../../components/ridetemplate/RideTemplateEditor'
import { RideTemplateRequest } from '@/api/dto'

export function EditRideTemplatePage() {
  const { t } = useTranslation('rideTemplates')
  const { teamSlug, templateSlug } = useParams<{ teamSlug: string; templateSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: template, isLoading: isLoadingTemplate } = useGetTemplate(
    teamSlug!,
    templateSlug!,
    {
      query: { enabled: !!teamSlug && !!templateSlug },
    }
  )

  const updateMutation = useUpdateTemplate()
  const { t: tCommon } = useTranslation('common')
  if (isLoadingTeam || isLoadingTemplate) {
    return <LoadingPage message={tCommon('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  if (!template) {
    return <Navigate to={paths.rideTemplates(teamSlug!)} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={paths.rideTemplates(teamSlug!)} replace />
  }

  const initialValues = { ...template }

  const handleSubmit = (data: RideTemplateRequest) => {
    updateMutation.mutate(
      {
        slug: teamSlug!,
        templateSlug: templateSlug!,
        data,
      },
      {
        onSuccess: () => {
          navigate(paths.rideTemplates(teamSlug!))
          queryClient.invalidateQueries({
            queryKey: getGetTemplateQueryKey(teamSlug!, templateSlug!),
          })
          queryClient.invalidateQueries({ queryKey: getListTemplatesQueryKey(teamSlug!) })
          toast.success(i18next.t('rideTemplates:notifications.updated'))
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-1 text-gray-600">{t('edit.subtitle')}</p>
      </div>

      <RideTemplateEditor
        key={template.id}
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.rideTemplates(teamSlug!))}
        isPending={updateMutation.isPending}
        submitButtonText={tCommon('actions.save')}
      />
    </div>
  )
}
