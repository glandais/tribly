import { useTranslation } from 'react-i18next'
import { Navigate, useParams } from 'react-router-dom'
import { useGetPreview } from '@/api/endpoints/gpx-previews/gpx-previews'
import { GpxPreviewView } from '@/components/gpxtool/GpxPreviewView'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { paths } from '@/config/paths'

export function GpxPreviewPage() {
  const { previewId } = useParams<{ previewId: string }>()
  const { t } = useTranslation()

  const { data: preview, isLoading } = useGetPreview(previewId!, {
    query: { enabled: !!previewId },
  })

  if (isLoading) {
    return <LoadingPage message={t('gpxTools.title')} />
  }

  if (!preview) {
    return <Navigate to={paths.gpxTools()} replace />
  }

  return <GpxPreviewView preview={preview} />
}
