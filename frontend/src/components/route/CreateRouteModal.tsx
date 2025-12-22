import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { XMarkIcon } from '@heroicons/react/24/outline'
import { useCreateRoute } from '../../hooks/useRoute'
import { Visibility } from '../../api/api'
import type { RouteDto } from '../../api/api'
import { LoadingSpinner } from '../common/LoadingSpinner'

interface CreateRouteModalProps {
  isOpen: boolean
  onClose: () => void
  onRouteCreated: (route: RouteDto) => void
  teamSlug: string
  teamVisibility?: Visibility
}

export function CreateRouteModal({
  isOpen,
  onClose,
  onRouteCreated,
  teamSlug,
  teamVisibility,
}: CreateRouteModalProps) {
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')
  const { t: tErrors } = useTranslation('errors')

  const createRoute = useCreateRoute(teamSlug)

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [surfaceType, setSurfaceType] = useState<'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'>('ROAD')
  const [visibility, setVisibility] = useState<Visibility>(Visibility.Team)
  const [gpxFile, setGpxFile] = useState<File | null>(null)
  const [error, setError] = useState<string | null>(null)

  const handleFileChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0]
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
    [t]
  )

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!gpxFile) {
      setError(t('create.validation.fileRequired'))
      return
    }

    try {
      const route = await createRoute.mutateAsync({
        name,
        description: description || undefined,
        surfaceType,
        visibility,
        gpxFile,
      })

      // Reset form
      setName('')
      setDescription('')
      setSurfaceType('ROAD')
      setVisibility(Visibility.Team)
      setGpxFile(null)
      setError(null)

      // Call success callback
      onRouteCreated(route)
    } catch (err) {
      setError(err instanceof Error ? err.message : tErrors('api.unknown'))
    }
  }

  const handleClose = () => {
    // Reset form on close
    setName('')
    setDescription('')
    setSurfaceType('ROAD')
    setVisibility(Visibility.Team)
    setGpxFile(null)
    setError(null)
    onClose()
  }

  if (!isOpen) return null

  // For private teams, routes must always be team-only
  const visibilityDisabled = teamVisibility === Visibility.Team

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
      <div className="relative w-full max-w-2xl max-h-[90vh] bg-white rounded-lg shadow-xl flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-gray-900">{t('createModal.title')}</h2>
          <button onClick={handleClose} className="text-gray-400 hover:text-gray-600" type="button">
            <XMarkIcon className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6">
          {error && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-red-700">{error}</p>
            </div>
          )}

          <div className="space-y-6">
            {/* GPX File Upload */}
            <div>
              <label htmlFor="gpxFile" className="block text-sm font-medium text-gray-700">
                {t('create.form.gpxFile')} *
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
                  required
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
              <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                {t('create.form.description')}
              </label>
              <textarea
                id="description"
                name="description"
                rows={4}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-xs focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
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
            <div>
              <label htmlFor="visibility" className="block text-sm font-medium text-gray-700">
                {t('create.form.visibility')}
              </label>
              <select
                id="visibility"
                name="visibility"
                value={visibility}
                onChange={(e) => setVisibility(e.target.value as Visibility)}
                disabled={visibilityDisabled}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-xs focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <option value={Visibility.Team}>{tCommon('visibility.team')}</option>
                <option value={Visibility.Public}>{tCommon('visibility.public')}</option>
              </select>
              {visibilityDisabled && (
                <p className="mt-2 text-sm text-gray-500">
                  {t('create.form.visibilityDisabledHint')}
                </p>
              )}
            </div>
          </div>
        </form>

        {/* Footer */}
        <div className="flex items-center justify-end gap-3 p-6 border-t border-gray-200">
          <button
            type="button"
            onClick={handleClose}
            disabled={createRoute.isPending}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {tCommon('buttons.cancel')}
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={createRoute.isPending || !gpxFile}
            className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            {createRoute.isPending && <LoadingSpinner size="sm" />}
            {createRoute.isPending ? tCommon('status.creating') : t('createModal.create')}
          </button>
        </div>
      </div>
    </div>
  )
}
