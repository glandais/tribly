import { useCallback, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Alert, Box, Button, FileInput, Group, Stack, Text, TextInput } from '@mantine/core'
import type { GeoPoint } from '@/api/dto'
import { RoutePlanner } from '../planner/RoutePlanner'

type SourceMode = 'gpx' | 'planner'

interface GpxPreviewEditorProps {
  initialName: string
  /** Existing track ([lng, lat, ele, dist][]); enables the planner when the preview has one track. */
  initialTrack?: number[][]
  /** Create-from-scratch: planner on an empty map, no GPX upload, no source toggle. */
  create?: boolean
  onSubmit: (name: string, points?: GeoPoint[], gpxFile?: File) => void | Promise<void>
  onCancel: () => void
  isPending: boolean
}

/**
 * Editor for a GPX preview: rename it and, optionally, redraw its track with the planner or replace
 * it with a fresh GPX upload. A lighter cousin of {@link RouteEditor} — a preview has no team, media,
 * surface type or visibility, so only the name and the track are editable.
 *
 * <p>In {@code create} mode there is no existing preview: the planner starts on an empty map and is
 * the only source (no GPX upload, no toggle), so the caller gets planner points back.
 */
export function GpxPreviewEditor({
  initialName,
  initialTrack,
  create = false,
  onSubmit,
  onCancel,
  isPending,
}: GpxPreviewEditorProps) {
  const { t } = useTranslation()

  const [name, setName] = useState(initialName)
  const canUsePlanner = create || !!initialTrack
  const [sourceMode, setSourceMode] = useState<SourceMode>(canUsePlanner ? 'planner' : 'gpx')
  const [gpxFile, setGpxFile] = useState<File | null>(null)
  const [plannerPoints, setPlannerPoints] = useState<GeoPoint[]>([])
  const [error, setError] = useState<string | null>(null)

  const handleFileChange = useCallback(
    (file: File | null) => {
      if (file && !file.name.endsWith('.gpx')) {
        setError(t('routes.create.validation.invalidFileType'))
        setGpxFile(null)
        return
      }
      setError(null)
      setGpxFile(file)
    },
    [t]
  )

  const nameValid = name.trim().length >= 3

  const handleSubmit = async () => {
    if (!nameValid) return
    if (create && plannerPoints.length < 2) {
      setError(t('routes.create.validation.pointsRequired'))
      return
    }
    setError(null)
    const points = sourceMode === 'planner' && plannerPoints.length >= 2 ? plannerPoints : undefined
    const file = sourceMode === 'gpx' ? gpxFile || undefined : undefined
    await onSubmit(name.trim(), points, file)
  }

  const showPlanner = canUsePlanner && sourceMode === 'planner'

  return (
    <Stack>
      {error && <Alert color="red">{error}</Alert>}

      {/* Track source selector — only when editing a single-track preview; hidden when creating
          from scratch, where the planner is the only source. */}
      {canUsePlanner && !create && (
        <Stack gap="xs">
          <Text size="sm" fw={500}>
            {t('routes.create.form.sourceMode')}
          </Text>
          <Group gap="xs">
            <Button
              variant={sourceMode === 'gpx' ? 'filled' : 'default'}
              onClick={() => setSourceMode('gpx')}
            >
              {t('routes.create.form.sourceModeGpx')}
            </Button>
            <Button
              variant={sourceMode === 'planner' ? 'filled' : 'default'}
              onClick={() => setSourceMode('planner')}
            >
              {t('routes.create.form.sourceModePlanner')}
            </Button>
          </Group>
        </Stack>
      )}

      {/* GPX upload — replaces the track when a file is provided; leave empty to rename only. */}
      {(sourceMode === 'gpx' || !canUsePlanner) && (
        <FileInput
          label={t('routes.create.form.gpxFile')}
          description={t('gpxTools.edit.gpxFileHint')}
          accept=".gpx"
          value={gpxFile}
          onChange={handleFileChange}
        />
      )}

      {showPlanner && (
        <Box>
          <Text size="sm" fw={500} mb="xs" px="md">
            {t('routes.create.form.plannerLabel')}
          </Text>
          <Box
            h="70vh"
            style={{
              borderTop: '1px solid var(--mantine-color-default-border)',
              borderBottom: '1px solid var(--mantine-color-default-border)',
            }}
          >
            <RoutePlanner onPointsChange={setPlannerPoints} initialTrack={initialTrack} />
          </Box>
          {plannerPoints.length > 0 && (
            <Text size="sm" c="dimmed" mt="xs" px="md">
              {t('routes.create.form.pointCount', { count: plannerPoints.length })}
            </Text>
          )}
        </Box>
      )}

      <TextInput
        label={
          <>
            {t('routes.create.form.name')}{' '}
            <Text span c="red">
              *
            </Text>
          </>
        }
        placeholder={t('routes.create.form.name')}
        value={name}
        onChange={(event) => setName(event.currentTarget.value)}
        error={name.length > 0 && !nameValid ? t('gpxTools.edit.nameTooShort') : undefined}
      />

      <Group justify="flex-end" pt="md">
        <Button variant="default" onClick={onCancel} disabled={isPending}>
          {t('actions.cancelAction')}
        </Button>
        <Button onClick={handleSubmit} disabled={isPending || !nameValid} loading={isPending}>
          {t('actions.save')}
        </Button>
      </Group>
    </Stack>
  )
}
