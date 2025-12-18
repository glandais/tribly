import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useRoute, useUpdateRoute } from '../../hooks/useRoute'

export function EditRoutePage() {
  const { teamSlug, routeId } = useParams<{ teamSlug: string; routeId: string }>()
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')
  const { t: tErrors } = useTranslation('errors')
  const navigate = useNavigate()

  const { data: route, isLoading } = useRoute(teamSlug, routeId)
  const updateRoute = useUpdateRoute(teamSlug!, routeId!)

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [difficulty, setDifficulty] = useState<'EASY' | 'MODERATE' | 'HARD' | 'EXPERT'>('MODERATE')
  const [surfaceType, setSurfaceType] = useState<'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'>('ROAD')
  const [isPublic, setIsPublic] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Load route data into form
  useEffect(() => {
    if (route) {
      // eslint-disable-next-line react-hooks/set-state-in-effect -- Form initialization from server data
      setName(route.name)
      setDescription(route.description || '')
      setDifficulty(route.difficulty || 'MODERATE')
      setSurfaceType(route.surfaceType || 'ROAD')
      setIsPublic(route.isPublic)
    }
  }, [route])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      await updateRoute.mutateAsync({
        name,
        description,
        difficulty,
        surfaceType,
        isPublic,
      })
      navigate(`/teams/${teamSlug}/routes/${routeId}`)
    } catch (err) {
      setError(err instanceof Error ? err.message : tErrors('api.unknown'))
    }
  }

  if (isLoading) {
    return (
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded-sm w-1/4 mb-4" />
          <div className="h-4 bg-gray-200 rounded-sm w-1/2 mb-8" />
          <div className="space-y-6">
            {[...Array(5)].map((_, i) => (
              <div key={i}>
                <div className="h-4 bg-gray-200 rounded-sm w-1/4 mb-2" />
                <div className="h-10 bg-gray-200 rounded-sm" />
              </div>
            ))}
          </div>
        </div>
      </div>
    )
  }

  if (!route) {
    return (
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center">
          <p className="text-gray-500">{tErrors('api.notFound')}</p>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/routes/${routeId}`}
          className="text-indigo-600 hover:text-indigo-900 text-sm font-medium"
        >
          ← {t('edit.backToDetail')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-2 text-gray-600">{t('edit.subtitle')}</p>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">{error}</p>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Name */}
        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700">
            {t('edit.form.name')} *
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
            {t('edit.form.description')}
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

        {/* Difficulty */}
        <div>
          <label htmlFor="difficulty" className="block text-sm font-medium text-gray-700">
            {t('edit.form.difficulty')}
          </label>
          <select
            id="difficulty"
            name="difficulty"
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value as typeof difficulty)}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-xs focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          >
            <option value="EASY">{t('difficulty.EASY')}</option>
            <option value="MODERATE">{t('difficulty.MODERATE')}</option>
            <option value="HARD">{t('difficulty.HARD')}</option>
            <option value="EXPERT">{t('difficulty.EXPERT')}</option>
          </select>
        </div>

        {/* Surface Type */}
        <div>
          <label htmlFor="surfaceType" className="block text-sm font-medium text-gray-700">
            {t('edit.form.surfaceType')}
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

        {/* Is Public */}
        <div className="flex items-center">
          <input
            id="isPublic"
            name="isPublic"
            type="checkbox"
            checked={isPublic}
            onChange={(e) => setIsPublic(e.target.checked)}
            className="h-4 w-4 rounded-sm border-gray-300 text-indigo-600 focus:ring-indigo-500"
          />
          <label htmlFor="isPublic" className="ml-2 block text-sm text-gray-900">
            {t('edit.form.isPublic')}
          </label>
        </div>

        {/* Submit Buttons */}
        <div className="flex justify-end gap-3">
          <Link
            to={`/teams/${teamSlug}/routes/${routeId}`}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            {tCommon('buttons.cancel')}
          </Link>
          <button
            type="submit"
            disabled={updateRoute.isPending}
            className="px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {updateRoute.isPending ? tCommon('status.saving') : tCommon('buttons.save')}
          </button>
        </div>
      </form>
    </div>
  )
}
