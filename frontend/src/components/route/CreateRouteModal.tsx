import { useTranslation } from 'react-i18next'
import { XMarkIcon } from '@heroicons/react/24/outline'
import type { RouteDto, TeamDetailDto } from '../../api/api'
import { RouteEditor } from './RouteEditor'
import type { RouteFormData } from './RouteEditor'
import { useCreateRoute, SurfaceType } from '../../hooks/useRoute'
import { Visibility } from '../../api/api'
import { defaultMedia } from '@/lib/apiUtils'

interface CreateRouteModalProps {
  isOpen: boolean
  onClose: () => void
  onRouteCreated: (route: RouteDto) => void
  team: TeamDetailDto
}

export function CreateRouteModal({ isOpen, onClose, onRouteCreated, team }: CreateRouteModalProps) {
  const { t } = useTranslation('routes')
  const createRoute = useCreateRoute(team.slug)

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: defaultMedia(),
    surfaceType: SurfaceType.Road,
    visibility: team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public,
  }

  const handleSubmit = async (data: RouteFormData, gpxFile?: File) => {
    // Either gpxFile or points must be provided
    if (!gpxFile && (!data.points || data.points.length < 2)) return

    const route = await createRoute.mutateAsync({
      route: {
        name: data.name,
        media: data.media,
        surfaceType: data.surfaceType,
        visibility: data.visibility,
        points: data.points,
      },
      gpxFile,
    })

    onRouteCreated(route)
    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
      <div className="relative w-full max-w-2xl max-h-[90vh] bg-white rounded-lg shadow-xl flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-gray-900">{t('createModal.title')}</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600" type="button">
            <XMarkIcon className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <div className="flex-1 overflow-y-auto p-6">
          <RouteEditor
            team={team}
            teamSlug={team.slug}
            initialValues={initialValues}
            isCreateMode={true}
            onSubmit={handleSubmit}
            onCancel={onClose}
            isPending={createRoute.isPending}
            error={createRoute.error}
            submitButtonText={t('createModal.create')}
            showCancelButton={false}
          />
        </div>
      </div>
    </div>
  )
}
