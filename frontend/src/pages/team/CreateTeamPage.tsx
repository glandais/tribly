import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useCreateTeam } from '../../hooks/useTeam'
import { LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ApiClientError } from '../../lib/apiClient'
import { Visibility } from '../../api/api'
import { MarkdownEditor } from '../../components/common/MarkdownEditor'

export function CreateTeamPage() {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [visibility, setVisibility] = useState<Visibility>(Visibility.Public)

  const createMutation = useCreateTeam()

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    createMutation.mutate({
      name,
      description: description || undefined,
      visibility,
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
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
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
          <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
            {t('create.form.description.label')}
          </label>
          <MarkdownEditor
            initialValue={description}
            onChange={setDescription}
            placeholder={t('create.form.description.placeholder')}
            minHeight="150px"
            maxHeight="300px"
            disabled={createMutation.isPending}
            ariaLabel={t('create.form.description.label')}
          />
          <p className="mt-1 text-sm text-gray-500">
            {t('create.form.description.charCount', { count: description.length, max: 2000 })}
          </p>
        </div>

        <div>
          <label htmlFor="visibility" className="block text-sm font-medium text-gray-700">
            {t('create.form.visibility.label')}
          </label>
          <select
            id="visibility"
            value={visibility}
            onChange={(e) => setVisibility(e.target.value as Visibility)}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value={Visibility.Team}>{tCommon('visibility.team')}</option>
            <option value={Visibility.Public}>{tCommon('visibility.public')}</option>
          </select>
          <p className="mt-1 text-sm text-gray-500">{t('create.form.visibility.hint')}</p>
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
