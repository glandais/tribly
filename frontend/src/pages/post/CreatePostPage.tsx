import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useCreatePost, Visibility, Status } from '../../hooks/usePost'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ApiClientError } from '../../lib/apiClient'
import { toDateTimeLocalValue, fromDateTimeLocalValue } from '../../utils/dateFormat'

export function CreatePostPage() {
  const { t } = useTranslation('posts')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [dateTime, setDateTime] = useState(toDateTimeLocalValue(new Date()))
  const [visibility, setVisibility] = useState<Visibility>(Visibility.Team)
  const [status, setStatus] = useState<Status>(Status.Draft)
  const [publishAt, setPublishAt] = useState('')

  const createMutation = useCreatePost(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={`/teams/${teamSlug}/posts`} replace />
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    createMutation.mutate({
      name,
      description: description || undefined,
      dateTime: fromDateTimeLocalValue(dateTime).toISOString(),
      status,
      visibility,
      publishAt: publishAt ? fromDateTimeLocalValue(publishAt).toISOString() : undefined,
    })
  }

  const getFieldError = (field: string) => {
    if (createMutation.error instanceof ApiClientError) {
      return createMutation.error.error.errors?.find((e) => e.field === field)?.message
    }
    return undefined
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/posts`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('breadcrumb.posts')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {createMutation.error &&
          !(
            createMutation.error instanceof ApiClientError && createMutation.error.error.errors
          ) && (
            <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-red-700">
                {createMutation.error instanceof ApiClientError
                  ? createMutation.error.error.message
                  : t('create.error')}
              </p>
            </div>
          )}

        {/* Title */}
        <div>
          <label htmlFor="title" className="block text-sm font-medium text-gray-700">
            {t('create.nameLabel')} <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            id="title"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            minLength={3}
            maxLength={200}
            className={`mt-1 block w-full px-4 py-2 border rounded-lg focus:ring-indigo-500 focus:border-indigo-500 ${
              getFieldError('name') ? 'border-red-300' : 'border-gray-300'
            }`}
            placeholder={t('create.namePlaceholder')}
          />
          {getFieldError('name') && (
            <p className="mt-1 text-sm text-red-600">{getFieldError('name')}</p>
          )}
        </div>

        {/* Description */}
        <div>
          <label htmlFor="description" className="block text-sm font-medium text-gray-700">
            {t('create.descriptionLabel')}
          </label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={5}
            maxLength={5000}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder={t('create.descriptionPlaceholder')}
          />
        </div>

        {/* Date and Time */}
        <div>
          <label htmlFor="dateTime" className="block text-sm font-medium text-gray-700">
            {t('create.dateTimeLabel')} <span className="text-red-500">*</span>
          </label>
          <input
            type="datetime-local"
            id="dateTime"
            value={dateTime}
            onChange={(e) => setDateTime(e.target.value)}
            required
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
          />
          <p className="mt-1 text-sm text-gray-500">{t('create.dateTimeHint')}</p>
        </div>

        {/* Visibility */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t('create.visibilityLabel')}
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
              <span className="ml-2 text-sm text-gray-700">{t('visibility.TEAM')}</span>
            </label>
            <label
              className={`flex items-center ${team.visibility === 'TEAM' ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              <input
                type="radio"
                name="visibility"
                value="PUBLIC"
                checked={visibility === 'PUBLIC'}
                onChange={() => setVisibility('PUBLIC')}
                disabled={team.visibility === 'TEAM'}
                className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500 disabled:cursor-not-allowed"
              />
              <span className="ml-2 text-sm text-gray-700">{t('visibility.PUBLIC')}</span>
            </label>
          </div>
          {team.visibility === 'TEAM' && (
            <p className="mt-2 text-sm text-gray-500">{tCommon('visibility.privateTeamHint')}</p>
          )}
        </div>

        {/* Status */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t('create.statusLabel')}
          </label>
          <div className="space-y-2">
            <label className="flex items-center">
              <input
                type="radio"
                name="status"
                value="DRAFT"
                checked={status === 'DRAFT'}
                onChange={() => setStatus('DRAFT')}
                className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
              />
              <span className="ml-2 text-sm text-gray-700">{t('status.DRAFT')}</span>
            </label>
            <label className="flex items-center">
              <input
                type="radio"
                name="status"
                value="PUBLISHED"
                checked={status === 'PUBLISHED'}
                onChange={() => setStatus('PUBLISHED')}
                className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
              />
              <span className="ml-2 text-sm text-gray-700">{t('status.PUBLISHED')}</span>
            </label>
          </div>
        </div>

        {/* Scheduled Publication */}
        <div>
          <label htmlFor="publishAt" className="block text-sm font-medium text-gray-700">
            {t('create.publishAtLabel')}
          </label>
          <input
            type="datetime-local"
            id="publishAt"
            value={publishAt}
            onChange={(e) => setPublishAt(e.target.value)}
            min={toDateTimeLocalValue(new Date())}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
          />
          <p className="mt-1 text-sm text-gray-500">{t('create.publishAtHint')}</p>
        </div>

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Link
            to={`/teams/${teamSlug}/posts`}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            {t('create.cancel')}
          </Link>
          <button
            type="submit"
            disabled={createMutation.isPending || !name.trim()}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {createMutation.isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {tCommon('buttons.loading')}
              </>
            ) : (
              t('create.submit')
            )}
          </button>
        </div>
      </form>
    </div>
  )
}
