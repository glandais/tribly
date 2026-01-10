import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { Container, Stack, Text, Title } from '@mantine/core'
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
  const { t } = useTranslation()

  useCanonicalPath(team && template ? paths.rideTemplateEdit(team.slug, template.slug) : undefined)

  if (isLoadingTeam || isLoadingTemplate) {
    return <LoadingPage message={t('loading')} />
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
        teamSlug: teamSlug!,
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
          notifications.show({ message: t('rideTemplates.notifications.updated'), color: 'green' })
        },
      }
    )
  }

  return (
    <Container size="sm" py="xl">
      <Stack gap="xs" mb="xl">
        <Title order={1}>{t('rideTemplates.edit.title')}</Title>
        <Text c="dimmed">{t('rideTemplates.edit.subtitle')}</Text>
      </Stack>

      <RideTemplateEditor
        key={template.id}
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.rideTemplates(teamSlug!))}
        isPending={updateMutation.isPending}
        submitButtonText={t('actions.save')}
      />
    </Container>
  )
}
