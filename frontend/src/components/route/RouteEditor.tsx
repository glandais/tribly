import { useState, useCallback, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { Visibility, SurfaceType } from '../../api/api'
import type { MediaDto, TeamDetailDto } from '../../api/api'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MarkdownEditor } from '../common/MarkdownEditor'

export interface RouteFormData {
  name: string
  media: {
    markdown?: string
  }
  surfaceType: SurfaceType
  visibility: Visibility
}

interface RouteEditorProps {
  // Context
  team: TeamDetailDto
  teamSlug: string

  // Initial values (REQUIRED - each page prepares these)
  initialValues: {
    name: string
    media: MediaDto
    surfaceType: SurfaceType
    visibility: Visibility
  }

  // Submission
  onSubmit: (data: RouteFormData, gpxFile?: File) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean
  error?: Error | null

  // UI customization
  requireGpxFile?: boolean // true for create, false for edit
  submitButtonText?: string
  cancelButtonText?: string
  showCancelButton?: boolean
}

export function RouteEditor({
  team,
  teamSlug: _teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error: _error,
  requireGpxFile = false,
  submitButtonText,
  cancelButtonText,
  showCancelButton = true,
}: RouteEditorProps) {
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')

  const [name, setName] = useState('')
  const [media, setMedia] = useState({ markdown: '' } as MediaDto)
  const [surfaceType, setSurfaceType] = useState<SurfaceType>(SurfaceType.Road)
  const [visibility, setVisibility] = useState<Visibility>(
    team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public
  )
  const [gpxFile, setGpxFile] = useState<File | null>(null)
  const [error, setError] = useState<string | null>(null)

  // Initialize form state from initialValues prop
  useEffect(() => {
    setName(initialValues.name)
    setMedia(initialValues.media)
    setSurfaceType(initialValues.surfaceType)
    setVisibility(initialValues.visibility)
  }, [initialValues])

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

    // Validate GPX file if required
    if (requireGpxFile && !gpxFile) {
      setError(t('create.validation.fileRequired'))
      return
    }

    // Clear local errors
    setError(null)

    // Call parent's onSubmit with data and optional file
    onSubmit(
      {
        name,
        media,
        surfaceType,
        visibility,
      },
      gpxFile || undefined
    )
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">{error}</p>
        </div>
      )}

      {/* GPX File Upload */}
      <div>
        <label htmlFor="gpxFile" className="block text-sm font-medium text-gray-700">
          {t('create.form.gpxFile')} {requireGpxFile && '*'}
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
            required={requireGpxFile}
          />
        </div>
        <p className="mt-2 text-sm text-gray-500">{t('create.form.gpxFileHint')}</p>
      </div>

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
          {t('create.form.description')}
        </label>
        <MarkdownEditor
          initialValue={media}
          onChange={setMedia}
          placeholder={t('create.form.description')}
          minHeight="150px"
          maxHeight="300px"
          disabled={isPending}
          ariaLabel={t('create.form.description')}
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
            {cancelButtonText || tCommon('buttons.cancel')}
          </button>
        )}
        <button
          type="submit"
          disabled={isPending || (requireGpxFile && !gpxFile)}
          className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          {isPending && <LoadingSpinner size="sm" />}
          {isPending ? tCommon('status.creating') : submitButtonText || t('create.submit')}
        </button>
      </div>
    </form>
  )
}
