import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useCreateTeam, useUpdateTeam } from '../../hooks/useTeam'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ApiClientError } from '../../lib/apiClient'
import { Visibility, TeamDetailDto, MediaDto } from '../../api/api'
import { MarkdownEditor } from '../common/MarkdownEditor'
import { defaultMedia } from '@/lib/apiUtils'

interface TeamFormProps {
  // Data
  teamSlug?: string
  initialName?: string
  initialMedia?: MediaDto
  initialVisibility?: Visibility

  // Behavior
  onSuccess: (team: TeamDetailDto) => void

  // UI Customization
  submitButtonText: string
  submitLoadingText: string
  cancelLink: string
  disableSubmitWhenEmpty?: boolean
  namespace: 'create' | 'settings'
}

export function TeamForm({
  teamSlug,
  initialName = '',
  initialMedia = defaultMedia(),
  initialVisibility = Visibility.Public,
  onSuccess,
  submitButtonText,
  submitLoadingText,
  cancelLink,
  disableSubmitWhenEmpty = false,
  namespace,
}: TeamFormProps) {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')

  const [name, setName] = useState(initialName)
  const [media, setMedia] = useState({ markdown: '' } as MediaDto)
  const [visibility, setVisibility] = useState<Visibility>(initialVisibility)

  // Conditional mutation based on context
  const createMutation = useCreateTeam()
  const updateMutation = useUpdateTeam(teamSlug || '')
  const mutation = teamSlug ? updateMutation : createMutation

  // Sync state from props when they change (for edit context)
  useEffect(() => {
    if (initialName) {
      // eslint-disable-next-line react-hooks/set-state-in-effect -- Form initialization from server data
      setName(initialName)
    }
    setMedia(initialMedia)
    if (initialVisibility) {
      setVisibility(initialVisibility)
    }
  }, [initialName, initialMedia, initialVisibility])

  // Call onSuccess when mutation succeeds
  useEffect(() => {
    if (mutation.isSuccess && mutation.data) {
      onSuccess(mutation.data)
    }
  }, [mutation.isSuccess, mutation.data, onSuccess])

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    mutation.mutate({
      name,
      media,
      visibility,
    })
  }

  const getFieldError = (field: string) => {
    if (mutation.error instanceof ApiClientError) {
      return mutation.error.error.errors?.find((e) => e.field === field)?.message
    }
    return undefined
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {mutation.error &&
        !(mutation.error instanceof ApiClientError && mutation.error.error.errors) && (
          <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-red-700">
              {mutation.error instanceof ApiClientError
                ? mutation.error.error.message
                : t(`${namespace}.error`)}
            </p>
          </div>
        )}

      <div>
        <label htmlFor="name" className="block text-sm font-medium text-gray-700">
          {t(`${namespace}.form.name.label`)}
          {namespace === 'create' && <span className="text-red-500"> *</span>}
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
          placeholder={namespace === 'create' ? t('create.form.name.placeholder') : undefined}
        />
        {getFieldError('name') && (
          <p className="mt-1 text-sm text-red-600">{getFieldError('name')}</p>
        )}
      </div>

      <div>
        <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
          {t(`${namespace}.form.description.label`)}
        </label>
        <MarkdownEditor
          initialValue={media}
          onChange={setMedia}
          placeholder={
            namespace === 'create'
              ? t('create.form.description.placeholder')
              : t('settings.form.description.label')
          }
          minHeight="150px"
          maxHeight="300px"
          disabled={mutation.isPending}
          ariaLabel={t(`${namespace}.form.description.label`)}
        />
        <p className="mt-1 text-sm text-gray-500">
          {t(`${namespace}.form.description.charCount`, {
            count: media.markdown?.length || 0,
            max: 2000,
          })}
        </p>
      </div>

      <div>
        <label htmlFor="visibility" className="block text-sm font-medium text-gray-700">
          {t(`${namespace}.form.visibility.label`)}
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
        <p className="mt-1 text-sm text-gray-500">{t(`${namespace}.form.visibility.hint`)}</p>
      </div>

      <div className="pt-4 flex items-center justify-end gap-3">
        <Link
          to={cancelLink}
          className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          {tCommon('buttons.cancel')}
        </Link>
        <button
          type="submit"
          disabled={mutation.isPending || (disableSubmitWhenEmpty && !name.trim())}
          className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {mutation.isPending ? (
            <>
              <LoadingSpinner size="sm" color="white" className="mr-2" />
              {submitLoadingText}
            </>
          ) : (
            submitButtonText
          )}
        </button>
      </div>
    </form>
  )
}
