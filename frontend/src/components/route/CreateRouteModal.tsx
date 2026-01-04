import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import type { RouteDto, TeamDetailDto, Visibility, SurfaceType, RouteRequest } from '@/api/dto'
import { RouteEditor } from './RouteEditor'
import { useCreateRoute, getListRoutesQueryKey } from '@/api/endpoints/routes/routes'
import { Visibility as VisibilityEnum, SurfaceType as SurfaceTypeEnum } from '@/api/dto'
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
  const queryClient = useQueryClient()
  const createRouteMutation = useCreateRoute()

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: defaultMedia(),
    surfaceType: SurfaceTypeEnum.ROAD as SurfaceType,
    visibility: (team.visibility === VisibilityEnum.TEAM
      ? VisibilityEnum.TEAM
      : VisibilityEnum.PUBLIC) as Visibility,
  }

  const handleSubmit = async (data: RouteRequest, gpxFile?: File) => {
    // Either gpxFile or points must be provided
    if (!gpxFile && (!data.points || data.points.length < 2)) return

    const route = await createRouteMutation.mutateAsync(
      {
        slug: team.slug,
        data: {
          route: data,
          gpxFile,
        },
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(team.slug) })
          toast.success(i18next.t('routes:notifications.created'))
        },
      }
    )

    onRouteCreated(route)
    onClose()
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={t('create.title')} size="full">
      <RouteEditor
        team={team}
        teamSlug={team.slug}
        initialValues={initialValues}
        isCreateMode={true}
        onSubmit={handleSubmit}
        onCancel={onClose}
        isPending={createRouteMutation.isPending}
        error={createRouteMutation.error}
        submitButtonText={t('createModal.create')}
        showCancelButton={false}
      />
    </Modal>
  )
}
