import { useState } from 'react'
import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { DocumentDuplicateIcon } from '@heroicons/react/24/outline'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useCreateRide, getListRidesQueryKey } from '../../api/endpoints/rides/rides'
import { Visibility, Status } from '@/api/dto'
import type { RideRequest, RideTemplateDto } from '@/api/dto'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideEditor } from '../../components/ride/RideEditor'
import { RideTemplatePickerModal } from '../../components/ridetemplate/RideTemplatePickerModal'
import { defaultMedia } from '@/lib/apiUtils'
import { paths } from '@/config/paths'

export function CreateRidePage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  const createMutation = useCreateRide()

  const [showTemplateModal, setShowTemplateModal] = useState(false)
  const [editorKey, setEditorKey] = useState(0)
  const [templateValues, setTemplateValues] = useState<RideTemplateDto | null>(null)

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
    return nextSunday.toISOString()
  }

  // Prepare initial values - use template values if available
  const initialValues = templateValues
    ? {
        ...templateValues,
        media: {
          markdown: templateValues.markdown,
          assets: defaultMedia().assets,
        },
        dateTime: getNextSunday(),
        publishAt: undefined,
        routeSlug: undefined,
      }
    : {
        name: '',
        media: defaultMedia(),
        dateTime: getNextSunday(),
        visibility: team.visibility === Visibility.TEAM ? Visibility.TEAM : Visibility.PUBLIC,
        status: Status.DRAFT,
        publishAt: undefined,
        routeSlug: undefined,
        groups: [
          {
            name: t('rides.create.form.groups.defaultName', { number: 1 }),
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
      ...template,
      groups: template.groups.map((g) => ({
        ...g,
        routeSlug: undefined,
        isNew: true,
      })),
    })
    setEditorKey((prev) => prev + 1)
    setShowTemplateModal(false)
  }

  const handleSubmit = (data: RideRequest) => {
    const filteredGroups = data.groups.filter((g) => g.name.trim())
    createMutation.mutate(
      {
        slug: teamSlug!,
        data: {
          ...data,
          groups: filteredGroups,
        },
      },
      {
        onSuccess: (ride) => {
          queryClient.invalidateQueries({ queryKey: getListRidesQueryKey(teamSlug!) })
          toast.success(i18next.t('rides.notifications.created'))
          navigate(paths.ride(teamSlug!, ride.slug))
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold text-gray-900">{t('rides.create.title')}</h1>
          <button
            type="button"
            onClick={() => setShowTemplateModal(true)}
            className="inline-flex items-center px-3 py-1.5 text-sm font-medium text-indigo-600 bg-indigo-50 rounded-lg hover:bg-indigo-100"
          >
            <DocumentDuplicateIcon className="w-4 h-4 mr-1.5" />
            {t('rides.create.loadTemplate')}
          </button>
        </div>
        <p className="mt-1 text-gray-600">{t('rides.create.subtitle', { teamName: team.name })}</p>
      </div>

      <RideEditor
        key={editorKey}
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.team(teamSlug!))}
        isPending={createMutation.isPending}
        submitButtonText={t('rides.create.button')}
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
