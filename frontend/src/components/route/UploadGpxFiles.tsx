import { useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Button, Text } from '@mantine/core'
import { IconUpload } from '@tabler/icons-react'
import { useQueryClient } from '@tanstack/react-query'
import { createRoute, getListRoutesQueryKey } from '@/api/endpoints/routes/routes'
import { Visibility, SurfaceType } from '@/api/dto'
import { defaultMedia } from '@/lib/apiUtils'

export interface UploadGpxFilesProps {
  teamSlug: string
  disabled?: boolean
}

export function UploadGpxFiles({ teamSlug, disabled }: UploadGpxFilesProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [uploading, setUploading] = useState(false)
  const [progress, setProgress] = useState<{ current: number; total: number } | null>(null)
  const [error, setError] = useState<string | null>(null)

  const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files
    if (!files || files.length === 0) return

    setUploading(true)
    setError(null)
    setProgress({ current: 0, total: files.length })

    let successCount = 0
    const fileArray = Array.from(files)

    for (let i = 0; i < fileArray.length; i++) {
      const file = fileArray[i]
      const name = file.name.replace(/\.gpx$/i, '')

      try {
        await createRoute(teamSlug, {
          route: {
            name,
            visibility: Visibility.TEAM,
            surfaceType: SurfaceType.ROAD,
            media: defaultMedia(),
          },
          gpxFile: file,
        })
        successCount++
        setProgress({ current: i + 1, total: files.length })
      } catch {
        setError(t('error.loading'))
      }
    }

    setUploading(false)
    setProgress(null)

    if (successCount > 0) {
      // Invalidate routes list to refresh
      queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(teamSlug) })
    }

    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  return (
    <>
      <input
        ref={fileInputRef}
        type="file"
        accept=".gpx"
        multiple
        onChange={handleFileSelect}
        disabled={disabled || uploading}
        hidden
      />
      <Button
        variant="default"
        onClick={() => fileInputRef.current?.click()}
        disabled={disabled || uploading}
        loading={uploading}
        leftSection={!uploading && <IconUpload size={16} />}
      >
        {uploading && progress
          ? t('routes.upload.progress', { current: progress.current, total: progress.total })
          : t('routes.upload.button')}
      </Button>
      {error && (
        <Text size="sm" c="red">
          {error}
        </Text>
      )}
    </>
  )
}
