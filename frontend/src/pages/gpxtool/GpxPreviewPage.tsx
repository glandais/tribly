import { useTranslation } from 'react-i18next'
import { Navigate, useParams } from 'react-router-dom'
import { GpxPreviewView } from '@/components/gpxtool/GpxPreviewView'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { paths } from '@/config/paths'
import { useGpxPreviewData } from '@/pages/gpxtool/gpxPreviewData'

export function GpxPreviewPage() {
  const { previewId } = useParams<{ previewId: string }>()
  const { t } = useTranslation()

  const { data: preview, isLoading } = useGpxPreviewData(previewId)

  if (isLoading) {
    return <LoadingPage message={t('gpxTools.title')} />
  }

  if (!preview) {
    return <Navigate to={paths.gpxTools()} replace />
  }

  return <GpxPreviewView preview={preview} />
}
