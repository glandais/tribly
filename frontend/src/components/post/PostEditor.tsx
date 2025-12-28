import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import type { MediaDto, TeamDetailDto } from '../../api/api'
import { ApiClientError } from '../../lib/apiClient'
import { fromDateTimeLocalValue, toDateTimeLocalValue } from '../../utils/dateFormat'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
import { Visibility, Status } from '../../hooks/usePost'
import { defaultMedia } from '@/lib/apiUtils'

export interface PostFormData {
  name: string
  media: MediaDto
  dateTime: string // ISO string
  visibility: Visibility
  status: Status
  publishAt?: string // ISO string
}

interface PostEditorProps {
  // Context
  team: TeamDetailDto
  teamSlug: string

  // Initial values (REQUIRED - each page prepares these)
  initialValues: {
    name: string
    media: MediaDto
    dateTime: string // datetime-local value
    visibility: Visibility
    status: Status
    publishAt?: string // datetime-local value
  }

  // Submission
  onSubmit: (data: PostFormData) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean
  error?: Error | ApiClientError | null

  // UI customization
  submitButtonText?: string
  cancelButtonText?: string
}

export function PostEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error,
  submitButtonText,
  cancelButtonText,
}: PostEditorProps) {
  const { t } = useTranslation('posts')
  const { t: tCommon } = useTranslation('common')

  const [name, setName] = useState('')
  const [media, setMedia] = useState<MediaDto>(defaultMedia())
  const [dateTime, setDateTime] = useState('')
  const [visibility, setVisibility] = useState<Visibility>(Visibility.Team)
  const [status, setStatus] = useState<Status>(Status.Draft)
  const [publishAt, setPublishAt] = useState('')

  // Initialize form state from initialValues prop (happens once on mount)
  useEffect(() => {
    setName(initialValues.name)
    setMedia(initialValues.media)
    setDateTime(initialValues.dateTime)
    setVisibility(initialValues.visibility)
    setStatus(initialValues.status)
    setPublishAt(initialValues.publishAt || '')
  }, [initialValues])

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()

    onSubmit({
      name,
      media,
      dateTime: fromDateTimeLocalValue(dateTime).toISOString(),
      status,
      visibility,
      publishAt: publishAt ? fromDateTimeLocalValue(publishAt).toISOString() : undefined,
    })
  }

  const getFieldError = (field: string) => {
    if (error instanceof ApiClientError) {
      return error.error.errors?.find((e) => e.field === field)?.message
    }
    return undefined
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {error && !(error instanceof ApiClientError && error.error.errors) && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">
            {error instanceof ApiClientError ? error.error.message : t('edit.error')}
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
        <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
          {t('create.descriptionLabel')}
        </label>
        <MediaEditor
          initialValue={media}
          onChange={setMedia}
          placeholder={t('create.descriptionPlaceholder')}
          minHeight="200px"
          maxHeight="400px"
          disabled={isPending}
          ariaLabel={t('create.descriptionLabel')}
          teamSlug={teamSlug}
        />
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

      {/* Date and Time */}
      {status === Status.Published && (
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
      )}

      {/* Scheduled Publication */}
      {status === Status.Draft && (
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
      )}

      {/* Actions */}
      <div className="pt-4 flex items-center justify-end gap-3">
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
        >
          {cancelButtonText || tCommon('buttons.cancel')}
        </button>
        <button
          type="submit"
          disabled={isPending || !name.trim()}
          className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isPending ? (
            <>
              <LoadingSpinner size="sm" color="white" className="mr-2" />
              {tCommon('buttons.loading')}
            </>
          ) : (
            submitButtonText || t('edit.submit')
          )}
        </button>
      </div>
    </form>
  )
}
