import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useCreateTeam } from '../../hooks/useTeam'
import { LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ApiClientError } from '../../api/client'

export function CreateTeamPage() {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [isPublic, setIsPublic] = useState(true)
  const [maxMembers, setMaxMembers] = useState<number | ''>('')

  const createMutation = useCreateTeam()

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    createMutation.mutate({
      name,
      description: description || undefined,
      isPublic,
      maxMembers: maxMembers ? Number(maxMembers) : undefined,
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
          to="/teams"
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M15 19l-7-7 7-7"
            />
          </svg>
          {t('create.backToTeams')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-1 text-gray-600">{t('create.subtitle')}</p>
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

        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700">
            {t('create.form.name.label')} <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            minLength={2}
            maxLength={255}
            className={`mt-1 block w-full px-4 py-2 border rounded-lg focus:ring-indigo-500 focus:border-indigo-500 ${
              getFieldError('name') ? 'border-red-300' : 'border-gray-300'
            }`}
            placeholder={t('create.form.name.placeholder')}
          />
          {getFieldError('name') && (
            <p className="mt-1 text-sm text-red-600">{getFieldError('name')}</p>
          )}
        </div>

        <div>
          <label htmlFor="description" className="block text-sm font-medium text-gray-700">
            {t('create.form.description.label')}
          </label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={4}
            maxLength={2000}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder={t('create.form.description.placeholder')}
          />
          <p className="mt-1 text-sm text-gray-500">
            {t('create.form.description.charCount', { count: description.length, max: 2000 })}
          </p>
        </div>

        <div className="flex items-start">
          <div className="flex items-center h-5">
            <input
              type="checkbox"
              id="isPublic"
              checked={isPublic}
              onChange={(e) => setIsPublic(e.target.checked)}
              className="h-4 w-4 text-indigo-600 border-gray-300 rounded-sm focus:ring-indigo-500"
            />
          </div>
          <div className="ml-3">
            <label htmlFor="isPublic" className="text-sm font-medium text-gray-700">
              {t('create.form.isPublic.label')}
            </label>
            <p className="text-sm text-gray-500">
              {isPublic ? t('create.form.isPublic.public') : t('create.form.isPublic.private')}
            </p>
          </div>
        </div>

        <div>
          <label htmlFor="maxMembers" className="block text-sm font-medium text-gray-700">
            {t('create.form.maxMembers.label')}
          </label>
          <input
            type="number"
            id="maxMembers"
            value={maxMembers}
            onChange={(e) => setMaxMembers(e.target.value ? Number(e.target.value) : '')}
            min={1}
            className="mt-1 block w-full sm:w-32 px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder={t('create.form.maxMembers.placeholder')}
          />
          <p className="mt-1 text-sm text-gray-500">{t('create.form.maxMembers.hint')}</p>
        </div>

        <div className="pt-4 flex items-center justify-end gap-3">
          <Link
            to="/teams"
            className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            {tCommon('buttons.cancel')}
          </Link>
          <button
            type="submit"
            disabled={createMutation.isPending || !name.trim()}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {createMutation.isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {t('create.creating')}
              </>
            ) : (
              t('create.button')
            )}
          </button>
        </div>
      </form>
    </div>
  )
}
