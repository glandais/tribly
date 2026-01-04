import { useState, useCallback, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { Visibility, SurfaceType, RouteRequest } from '@/api/dto'
import type { TeamDetailDto, GeoPoint } from '@/api/dto'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
import { EmbeddedRoutePlanner } from '../planner/EmbeddedRoutePlanner'
import { defaultMedia } from '@/lib/apiUtils'

export type RouteSourceMode = 'gpx' | 'planner'

interface RouteEditorProps {
  // Context
  team: TeamDetailDto
  teamSlug: string

  // Initial values (REQUIRED - each page prepares these)
  initialValues: RouteRequest

  // Submission
  onSubmit: (data: RouteRequest, gpxFile?: File) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean
  error?: Error | null

  // UI customization
  isCreateMode?: boolean // true for create (requires source), false for edit
  initialTrack?: number[][] // [lng, lat, ele, dist][] for edit mode with planner
  submitButtonText?: string
  cancelButtonText?: string
  showCancelButton?: boolean
}

export function RouteEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error: _error,
  isCreateMode = false,
  initialTrack,
  submitButtonText,
  cancelButtonText,
  showCancelButton = true,
}: RouteEditorProps) {
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')

  // Can use planner if creating or if editing with a single-track route
  const canUsePlanner = isCreateMode || !!initialTrack

  // Route source mode (GPX upload or Planner)
  // Default to planner if editing with initialTrack
  const [sourceMode, setSourceMode] = useState<RouteSourceMode>(
    !isCreateMode && initialTrack ? 'planner' : 'gpx'
  )

  const [name, setName] = useState('')
  const [media, setMedia] = useState(defaultMedia())
  const [surfaceType, setSurfaceType] = useState<SurfaceType>(SurfaceType.ROAD)
  const [visibility, setVisibility] = useState<Visibility>(
    team.visibility === Visibility.TEAM ? Visibility.TEAM : Visibility.PUBLIC
  )
  const [gpxFile, setGpxFile] = useState<File | null>(null)
  const [plannerPoints, setPlannerPoints] = useState<GeoPoint[]>([])
  const [error, setError] = useState<string | null>(null)

  // Initialize form state from initialValues prop
  useEffect(() => {
    setName(initialValues.name)
    // Only sync media if there's actual content, not on empty default
    if (initialValues.media.markdown || initialValues.media.assets?.images?.length) {
      setMedia(initialValues.media)
    }
    setSurfaceType(initialValues.surfaceType)
    setVisibility(initialValues.visibility)
    // eslint-disable-next-line react-hooks/exhaustive-deps -- Only sync on actual value changes
  }, [
    initialValues.name,
    initialValues.media.markdown,
    initialValues.media.assets?.images?.length,
    initialValues.surfaceType,
    initialValues.visibility,
  ])

  const handleFileChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0]
      if (!name && file?.name) {
        const defaultName = file.name.replace(/\.gpx$/i, '')
        setName(defaultName)
      }
      if (file) {
        // Validate file type
        if (!file.name.endsWith('.gpx')) {
          setError(t('create.validation.invalidFileType'))
          setGpxFile(null)
          return
        }
        // Validate file size (10MB max)
        if (file.size > 10 * 1024 * 1024) {
          setError(t('create.validation.fileTooLarge'))
          setGpxFile(null)
          return
        }
        setError(null)
        setGpxFile(file)
      }
    },
    [t, name]
  )

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    // Validate source in create mode
    if (isCreateMode) {
      if (sourceMode === 'gpx' && !gpxFile) {
        setError(t('create.validation.fileRequired'))
        return
      }
      if (sourceMode === 'planner' && plannerPoints.length < 2) {
        setError(t('create.validation.pointsRequired'))
        return
      }
    }

    // Clear local errors
    setError(null)

    // Call parent's onSubmit with data and optional file/points
    onSubmit(
      {
        name,
        media,
        surfaceType,
        visibility,
        points: sourceMode === 'planner' ? plannerPoints : undefined,
      },
      sourceMode === 'gpx' ? gpxFile || undefined : undefined
    )
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">{error}</p>
        </div>
      )}

      {/* Route Source - Mode selector (create mode or edit with single-track) */}
      {canUsePlanner && (
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t('create.form.sourceMode')} {isCreateMode && '*'}
          </label>
          <div className="flex gap-2">
            <button
              type="button"
              onClick={() => setSourceMode('gpx')}
              className={`flex-1 py-2 px-4 text-sm font-medium rounded-lg border transition-colors ${
                sourceMode === 'gpx'
                  ? 'bg-indigo-600 text-white border-indigo-600'
                  : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
              }`}
            >
              {t('create.form.sourceModeGpx')}
            </button>
            <button
              type="button"
              onClick={() => setSourceMode('planner')}
              className={`flex-1 py-2 px-4 text-sm font-medium rounded-lg border transition-colors ${
                sourceMode === 'planner'
                  ? 'bg-indigo-600 text-white border-indigo-600'
                  : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
              }`}
            >
              {t('create.form.sourceModePlanner')}
            </button>
          </div>
        </div>
      )}

      {/* GPX File Upload (GPX mode or edit mode without planner) */}
      {(sourceMode === 'gpx' || !canUsePlanner) && (
        <div>
          <label htmlFor="gpxFile" className="block text-sm font-medium text-gray-700">
            {t('create.form.gpxFile')} {isCreateMode && sourceMode === 'gpx' && '*'}
          </label>
          <div className="mt-1">
            <input
              id="gpxFile"
              name="gpxFile"
              type="file"
              accept=".gpx"
              onChange={handleFileChange}
              className="block w-full text-sm text-gray-500
                file:mr-4 file:py-2 file:px-4
                file:rounded-md file:border-0
                file:text-sm file:font-semibold
                file:bg-indigo-50 file:text-indigo-700
                hover:file:bg-indigo-100"
            />
          </div>
          <p className="mt-2 text-sm text-gray-500">{t('create.form.gpxFileHint')}</p>
        </div>
      )}

      {/* Route Planner - full viewport width */}
      {canUsePlanner && sourceMode === 'planner' && (
        <div className="relative left-1/2 right-1/2 -ml-[50vw] -mr-[50vw] w-screen">
          <label className="block text-sm font-medium text-gray-700 mb-2 px-4 sm:px-6 lg:px-8">
            {t('create.form.plannerLabel')} {isCreateMode && '*'}
          </label>
          <div className="h-[70vh] border-y border-gray-300 overflow-hidden">
            <EmbeddedRoutePlanner onPointsChange={setPlannerPoints} initialTrack={initialTrack} />
          </div>
          {plannerPoints.length > 0 && (
            <p className="mt-2 text-sm text-gray-500 px-4 sm:px-6 lg:px-8">
              {t('create.form.pointCount', { count: plannerPoints.length })}
            </p>
          )}
        </div>
      )}

      {/* Name */}
      <div>
        <label htmlFor="name" className="block text-sm font-medium text-gray-700">
          {t('create.form.name')} *
        </label>
        <input
          id="name"
          name="name"
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-xs focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          required
          maxLength={255}
        />
      </div>

      {/* Description */}
      <div>
        <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
          {tCommon('form.description')}
        </label>
        <MediaEditor
          value={media}
          onChange={setMedia}
          placeholder={tCommon('form.description')}
          minHeight="150px"
          maxHeight="300px"
          disabled={isPending}
          ariaLabel={tCommon('form.description')}
          teamSlug={teamSlug}
        />
      </div>

      {/* Surface Type */}
      <div>
        <label htmlFor="surfaceType" className="block text-sm font-medium text-gray-700">
          {t('create.form.surfaceType')}
        </label>
        <select
          id="surfaceType"
          name="surfaceType"
          value={surfaceType}
          onChange={(e) => setSurfaceType(e.target.value as typeof surfaceType)}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-xs focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
        >
          <option value="ROAD">{t('surfaceType.ROAD')}</option>
          <option value="GRAVEL">{t('surfaceType.GRAVEL')}</option>
          <option value="MTB">{t('surfaceType.MTB')}</option>
          <option value="MIXED">{t('surfaceType.MIXED')}</option>
        </select>
      </div>

      {/* Visibility */}
      {team.visibility !== 'TEAM' && (
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {tCommon('visibility.label')}
          </label>
          <div className="space-y-2">
            <label className="flex items-center">
              <input
                type="radio"
                name="visibility"
                value="TEAM"
                checked={visibility === 'TEAM'}
                onChange={() => setVisibility('TEAM')}
                className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
              />
              <span className="ml-2 text-sm text-gray-700">{tCommon('visibility.team')}</span>
            </label>
            <label className={`flex items-center`}>
              <input
                type="radio"
                name="visibility"
                value="PUBLIC"
                checked={visibility === 'PUBLIC'}
                onChange={() => setVisibility('PUBLIC')}
                className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500 disabled:cursor-not-allowed"
              />
              <span className="ml-2 text-sm text-gray-700">{tCommon('visibility.public')}</span>
            </label>
          </div>
        </div>
      )}

      {/* Submit Buttons */}
      <div className="flex justify-end gap-3">
        {showCancelButton && (
          <button
            type="button"
            onClick={onCancel}
            disabled={isPending}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {cancelButtonText || tCommon('actions.cancelAction')}
          </button>
        )}
        <button
          type="submit"
          disabled={
            isPending ||
            (isCreateMode && sourceMode === 'gpx' && !gpxFile) ||
            (isCreateMode && sourceMode === 'planner' && plannerPoints.length < 2)
          }
          className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          {isPending && <LoadingSpinner size="sm" />}
          {isPending ? tCommon('status.creating') : submitButtonText || t('create.submit')}
        </button>
      </div>
    </form>
  )
}
