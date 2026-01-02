import { useTranslation } from 'react-i18next'
import type { RouteDto, TeamDetailDto } from '../../api/api'
import { RouteEditor } from './RouteEditor'
import type { RouteFormData } from './RouteEditor'
import { useCreateRoute, SurfaceType } from '../../hooks/useRoute'
import { Visibility } from '../../api/api'
import { defaultMedia } from '@/lib/apiUtils'
import { Modal } from '../common/Modal'

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

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={t('createModal.title')} size="2xl">
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
    </Modal>
  )
}
