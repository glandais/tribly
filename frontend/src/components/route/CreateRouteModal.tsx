import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
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
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const createRouteMutation = useCreateRoute()

  const initialValues = {
    name: '',
    media: defaultMedia(),
    surfaceType: SurfaceTypeEnum.ROAD as SurfaceType,
    visibility: (team.visibility === VisibilityEnum.TEAM
      ? VisibilityEnum.TEAM
      : VisibilityEnum.PUBLIC) as Visibility,
  }

  const handleSubmit = async (data: RouteRequest, gpxFile?: File) => {
    if (!gpxFile && (!data.points || data.points.length < 2)) return

    const route = await createRouteMutation.mutateAsync(
      {
        teamSlug: team.slug,
        data: {
          route: data,
          gpxFile,
        },
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(team.slug) })
          notifications.show({ message: i18next.t('routes.notifications.created'), color: 'green' })
        },
      }
    )

    onRouteCreated(route)
    onClose()
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={t('routes.create.title')} size="full">
      <RouteEditor
        team={team}
        teamSlug={team.slug}
        initialValues={initialValues}
        isCreateMode={true}
        onSubmit={handleSubmit}
        onCancel={onClose}
        isPending={createRouteMutation.isPending}
        error={createRouteMutation.error}
        submitButtonText={t('routes.create.submit')}
        showCancelButton={false}
      />
    </Modal>
  )
}
