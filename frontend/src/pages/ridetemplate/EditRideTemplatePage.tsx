import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useRideTemplate, useUpdateRideTemplate } from '../../hooks/useRideTemplate'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideTemplateEditor } from '../../components/ridetemplate/RideTemplateEditor'
import type { RideTemplateFormData } from '../../components/ridetemplate/RideTemplateEditor'

export function EditRideTemplatePage() {
  const { t } = useTranslation('rideTemplates')
  const { teamSlug, templateSlug } = useParams<{ teamSlug: string; templateSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: template, isLoading: isLoadingTemplate } = useRideTemplate(teamSlug, templateSlug)

  const updateMutation = useUpdateRideTemplate(teamSlug, templateSlug!)

  if (isLoadingTeam || isLoadingTemplate) {
    return <LoadingPage message={t('edit.loading')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  if (!template) {
    return <Navigate to={`/teams/${teamSlug}/ride-templates`} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={`/teams/${teamSlug}/ride-templates`} replace />
  }

  const initialValues = {
    name: template.name,
    markdown: template.markdown || undefined,
    visibility: template.visibility,
    status: template.status,
    groups: template.groups.map((g) => ({
      id: g.id,
      name: g.name,
      time: g.time,
      averageSpeed: g.averageSpeed,
      maxParticipants: g.maxParticipants,
      isNew: false,
      isDeleted: false,
    })),
  }

  const handleSubmit = (data: RideTemplateFormData) => {
    const filteredGroups = data.groups.filter((g) => !g.isDeleted && g.name.trim())
    updateMutation.mutate({
      name: data.name,
      markdown: data.markdown,
      visibility: data.visibility,
      status: data.status,
      groups: filteredGroups.map((g) => ({
        id: g.id,
        name: g.name,
        time: g.time,
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
          {t('edit.backToTemplates')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-1 text-gray-600">{t('edit.subtitle')}</p>
      </div>

      <RideTemplateEditor
        key={template.id}
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/teams/${teamSlug}/ride-templates`)}
        isPending={updateMutation.isPending}
        error={updateMutation.error}
        submitButtonText={t('edit.button')}
      />
    </div>
  )
}
