import { useState, useCallback } from 'react'
import { useParams, Link, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useCreateRoute } from '../../hooks/useRoute'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { Visibility } from '../../api/api'

export function CreateRoutePage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')
  const { t: tErrors } = useTranslation('errors')

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const createRoute = useCreateRoute(teamSlug!)

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
      await createRoute.mutateAsync({
        name,
        description: description || undefined,
        surfaceType,
        visibility,
        gpxFile,
      })
    } catch (err) {
      setError(err instanceof Error ? err.message : tErrors('api.unknown'))
    }
  }

  if (isLoadingTeam) {
    return <LoadingPage message={t('create.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  // For private teams, routes must always be team-only
  const visibilityDisabled = team.visibility === Visibility.Team

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/routes`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('create.backToList')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-2 text-gray-600">{t('create.subtitle')}</p>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">{error}</p>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
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
            <p className="mt-2 text-sm text-gray-500">{t('create.form.visibilityDisabledHint')}</p>
          )}
        </div>

        {/* Submit Buttons */}
        <div className="flex justify-end gap-3">
          <Link
            to={`/teams/${teamSlug}/routes`}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            {tCommon('buttons.cancel')}
          </Link>
          <button
            type="submit"
            disabled={createRoute.isPending || !gpxFile}
            className="px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {createRoute.isPending ? tCommon('status.creating') : t('create.submit')}
          </button>
        </div>
      </form>
    </div>
  )
}
