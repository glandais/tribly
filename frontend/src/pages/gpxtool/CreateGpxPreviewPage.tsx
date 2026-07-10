import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { Stack, Text, Title } from '@mantine/core'
import { notifications } from '@mantine/notifications'
import type { GeoPoint } from '@/api/dto'
import {
  getListMyPreviewsQueryKey,
  useCreatePreviewFromPoints,
} from '@/api/endpoints/gpx-previews/gpx-previews'
import { GpxPreviewEditor } from '@/components/gpxtool/GpxPreviewEditor'
import { paths } from '@/config/paths'

/**
 * Draws a GPX preview from scratch: an empty planner map, a name, and nothing else. On submit the
 * planner points (already routed on the frontend) are sent to the backend, which runs the same
 * elevation/climb pipeline as an uploaded file and returns a shareable preview.
 */
export function CreateGpxPreviewPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const createMutation = useCreatePreviewFromPoints()

  const handleSubmit = async (name: string, points?: GeoPoint[]) => {
    if (!points || points.length < 2) return

    const preview = await createMutation.mutateAsync(
      { data: { name, points } },
      {
        onSuccess: () => {
          // Refresh "my files": the key prefix matches every paginated variant.
          queryClient.invalidateQueries({ queryKey: getListMyPreviewsQueryKey() })
          notifications.show({ message: t('gpxTools.createFromScratch.created'), color: 'green' })
        },
      }
    )
    navigate(paths.gpxToolsView(preview.id))
  }

  return (
    <Stack>
      <Stack gap="xs">
        <Title order={1}>{t('gpxTools.createFromScratch.title')}</Title>
        <Text c="dimmed">{t('gpxTools.createFromScratch.subtitle')}</Text>
      </Stack>

      <GpxPreviewEditor
        create
        initialName=""
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.gpxTools())}
        isPending={createMutation.isPending}
      />
    </Stack>
  )
}
