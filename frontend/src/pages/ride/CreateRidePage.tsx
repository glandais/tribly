import { useState } from 'react'
import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { DocumentDuplicateIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useCreateRide, Visibility, Status } from '../../hooks/useRide'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideEditor } from '../../components/ride/RideEditor'
import type { RideFormData, EditableGroup } from '../../components/ride/RideEditor'
import { RideTemplatePickerModal } from '../../components/ridetemplate/RideTemplatePickerModal'
import type { RideTemplateDto } from '../../api/api'
import { toDateTimeLocalValue } from '../../utils/dateFormat'
import { defaultMedia } from '@/lib/apiUtils'
import { paths } from '@/config/paths'

export function CreateRidePage() {
  const { t } = useTranslation('rides')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  const createMutation = useCreateRide(teamSlug)

  const [showTemplateModal, setShowTemplateModal] = useState(false)
  const [editorKey, setEditorKey] = useState(0)
  const [templateValues, setTemplateValues] = useState<{
    name: string
    markdown?: string
    visibility: Visibility
    status: Status
    groups: EditableGroup[]
  } | null>(null)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={paths.team(teamSlug!)} replace />
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

  // Prepare initial values - use template values if available
  const initialValues = templateValues
    ? {
        name: templateValues.name,
        media: {
          markdown: templateValues.markdown,
          assets: defaultMedia().assets,
        },
        dateTime: getNextSunday(),
        visibility: templateValues.visibility,
        status: templateValues.status,
        publishAt: undefined,
        routeSlug: undefined,
        groups: templateValues.groups,
      }
    : {
        name: '',
        media: defaultMedia(),
        dateTime: getNextSunday(),
        visibility: team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public,
        status: Status.Draft,
        publishAt: undefined,
        routeSlug: undefined,
        groups: [
          {
            name: t('create.form.groups.defaultName'),
            time: undefined,
            averageSpeed: undefined,
            maxParticipants: undefined,
            routeSlug: undefined,
            isNew: true,
          },
        ],
      }

  const handleTemplateSelect = (template: RideTemplateDto) => {
    setTemplateValues({
      name: template.name,
      markdown: template.markdown || undefined,
      visibility: template.visibility as Visibility,
      status: template.status as Status,
      groups: template.groups.map((g) => ({
        name: g.name,
        time: g.time,
        averageSpeed: g.averageSpeed ?? undefined,
        maxParticipants: g.maxParticipants ?? undefined,
        routeSlug: undefined,
        isNew: true,
      })),
    })
    setEditorKey((prev) => prev + 1)
    setShowTemplateModal(false)
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
          navigate(paths.team(teamSlug!))
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold text-gray-900">{t('create.title')}</h1>
          <button
            type="button"
            onClick={() => setShowTemplateModal(true)}
            className="inline-flex items-center px-3 py-1.5 text-sm font-medium text-indigo-600 bg-indigo-50 rounded-lg hover:bg-indigo-100"
          >
            <DocumentDuplicateIcon className="w-4 h-4 mr-1.5" />
            {t('create.loadTemplate')}
          </button>
        </div>
        <p className="mt-1 text-gray-600">{t('create.subtitle', { teamName: team.name })}</p>
      </div>

      <RideEditor
        key={editorKey}
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.team(teamSlug!))}
        isPending={createMutation.isPending}
        error={createMutation.error}
        submitButtonText={t('create.button')}
      />

      <RideTemplatePickerModal
        isOpen={showTemplateModal}
        onClose={() => setShowTemplateModal(false)}
        onSelect={handleTemplateSelect}
        teamSlug={teamSlug!}
      />
    </div>
  )
}
