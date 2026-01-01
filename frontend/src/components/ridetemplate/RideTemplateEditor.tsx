import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MarkdownEditor } from '../common/MarkdownEditor'
import { ApiClientError } from '../../lib/apiClient'
import type { TeamDetailDto } from '../../api/api'
import { Visibility, Status } from '../../api/api'

export interface EditableTemplateGroup {
  id?: string
  name: string
  averageSpeed?: number
  maxParticipants?: number
  isNew?: boolean
  isDeleted?: boolean
}

export interface RideTemplateFormData {
  name: string
  markdown?: string
  visibility: Visibility
  status: Status
  groups: EditableTemplateGroup[]
}

interface RideTemplateEditorProps {
  team: TeamDetailDto
  teamSlug: string
  initialValues: {
    name: string
    markdown?: string
    visibility: Visibility
    status: Status
    groups: EditableTemplateGroup[]
  }
  onSubmit: (data: RideTemplateFormData) => void | Promise<void>
  onCancel: () => void
  isPending: boolean
  error?: Error | ApiClientError | null
  submitButtonText?: string
}

export function RideTemplateEditor({
  team,
  teamSlug: _teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error,
  submitButtonText,
}: RideTemplateEditorProps) {
  const { t } = useTranslation('rideTemplates')
  const { t: tCommon } = useTranslation('common')

  const [name, setName] = useState(initialValues.name)
  const [markdown, setMarkdown] = useState(initialValues.markdown || '')
  const [visibility, setVisibility] = useState<Visibility>(initialValues.visibility)
  const [status, setStatus] = useState<Status>(initialValues.status)
  const [groups, setGroups] = useState<EditableTemplateGroup[]>(initialValues.groups)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const filteredGroups = groups.filter((g) => !g.isDeleted && g.name.trim())

    onSubmit({
      name,
      markdown: markdown || undefined,
      visibility,
      status,
      groups: filteredGroups,
    })
  }

  const handleAddGroup = () => {
    setGroups([
      ...groups,
      {
        name: '',
        averageSpeed: undefined,
        maxParticipants: undefined,
        isNew: true,
      },
    ])
  }

  const handleRemoveGroup = (index: number) => {
    const group = groups[index]
    if (group.isNew) {
      setGroups(groups.filter((_, i) => i !== index))
    } else {
      setGroups(groups.map((g, i) => (i === index ? { ...g, isDeleted: true } : g)))
    }
  }

  const handleRestoreGroup = (index: number) => {
    setGroups(groups.map((g, i) => (i === index ? { ...g, isDeleted: false } : g)))
  }

  const handleUpdateGroup = (index: number, updates: Partial<EditableTemplateGroup>) => {
    setGroups(groups.map((g, i) => (i === index ? { ...g, ...updates } : g)))
  }

  const getFieldError = (field: string) => {
    if (error instanceof ApiClientError) {
      return error.error.errors?.find((e) => e.field === field)?.message
    }
    return undefined
  }

  const isPrivateTeam = team.visibility !== Visibility.Public

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Form-level error */}
      {error && !(error instanceof ApiClientError && error.error.errors) && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">
            {error instanceof ApiClientError ? error.error.message : t('form.error')}
          </p>
        </div>
      )}

      {/* Name */}
      <div>
        <label htmlFor="name" className="block text-sm font-medium text-gray-700">
          {t('form.name.label')} <span className="text-red-500">*</span>
        </label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          minLength={3}
          maxLength={200}
          className={`mt-1 block w-full px-4 py-2 border rounded-lg focus:ring-indigo-500 focus:border-indigo-500 ${
            getFieldError('name') ? 'border-red-300' : 'border-gray-300'
          }`}
          placeholder={t('form.name.placeholder')}
        />
        {getFieldError('name') && (
          <p className="mt-1 text-sm text-red-600">{getFieldError('name')}</p>
        )}
      </div>

      {/* Description (Markdown) */}
      <div>
        <label htmlFor="markdown" className="block text-sm font-medium text-gray-700">
          {t('form.description.label')}
        </label>
        <div className="mt-1">
          <MarkdownEditor
            value={markdown}
            onChange={setMarkdown}
            placeholder={t('form.description.placeholder')}
            minHeight="150px"
            maxHeight="300px"
            disabled={isPending}
            ariaLabel={t('form.description.placeholder')}
          />
        </div>
      </div>

      {/* Visibility */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          {t('form.visibility.label')} <span className="text-red-500">*</span>
        </label>
        <div className="space-y-2">
          <label className="flex items-center">
            <input
              type="radio"
              name="visibility"
              value="TEAM"
              checked={visibility === Visibility.Team}
              onChange={() => setVisibility(Visibility.Team)}
              className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
            />
            <span className="ml-2 text-sm text-gray-700">{t('form.visibility.team')}</span>
          </label>
          <label className="flex items-center">
            <input
              type="radio"
              name="visibility"
              value="PUBLIC"
              checked={visibility === Visibility.Public}
              onChange={() => setVisibility(Visibility.Public)}
              disabled={isPrivateTeam}
              className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500 disabled:opacity-50"
            />
            <span className={`ml-2 text-sm ${isPrivateTeam ? 'text-gray-400' : 'text-gray-700'}`}>
              {t('form.visibility.public')}
              {isPrivateTeam && (
                <span className="ml-1 text-xs">({t('form.visibility.publicDisabled')})</span>
              )}
            </span>
          </label>
        </div>
      </div>

      {/* Status */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          {t('form.status.label')} <span className="text-red-500">*</span>
        </label>
        <div className="space-y-2">
          <label className="flex items-center">
            <input
              type="radio"
              name="status"
              value="DRAFT"
              checked={status === Status.Draft}
              onChange={() => setStatus(Status.Draft)}
              className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
            />
            <span className="ml-2 text-sm text-gray-700">{t('form.status.draft')}</span>
          </label>
          <label className="flex items-center">
            <input
              type="radio"
              name="status"
              value="PUBLISHED"
              checked={status === Status.Published}
              onChange={() => setStatus(Status.Published)}
              className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
            />
            <span className="ml-2 text-sm text-gray-700">{t('form.status.published')}</span>
          </label>
        </div>
      </div>

      {/* Groups */}
      <div>
        <div className="flex items-center justify-between mb-2">
          <label className="block text-sm font-medium text-gray-700">
            {t('form.groups.label')}
          </label>
          <button
            type="button"
            onClick={handleAddGroup}
            className="text-sm text-indigo-600 hover:text-indigo-700"
          >
            {t('form.groups.add')}
          </button>
        </div>
        <p className="text-sm text-gray-500 mb-3">{t('form.groups.hint')}</p>
        <div className="space-y-3">
          {groups.map((group, index) => (
            <div
              key={group.id || `new-${index}`}
              className={`border rounded-lg p-4 ${
                group.isDeleted
                  ? 'border-red-200 bg-red-50'
                  : group.isNew
                    ? 'border-green-200 bg-green-50'
                    : 'border-gray-200'
              }`}
            >
              <div className="flex items-center justify-between mb-3">
                <span className="text-sm font-medium text-gray-700">
                  {group.isNew ? t('form.groups.new') : `${t('form.groups.label')} ${index + 1}`}
                  {group.isDeleted && (
                    <span className="ml-2 text-red-600">{t('form.groups.willBeDeleted')}</span>
                  )}
                </span>
                {group.isDeleted ? (
                  <button
                    type="button"
                    onClick={() => handleRestoreGroup(index)}
                    className="text-sm text-indigo-600 hover:text-indigo-700"
                  >
                    {t('form.groups.restore')}
                  </button>
                ) : (
                  <button
                    type="button"
                    onClick={() => handleRemoveGroup(index)}
                    className="text-sm text-red-600 hover:text-red-700"
                  >
                    {t('form.groups.remove')}
                  </button>
                )}
              </div>
              {!group.isDeleted && (
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div>
                    <input
                      type="text"
                      value={group.name}
                      onChange={(e) => handleUpdateGroup(index, { name: e.target.value })}
                      placeholder={t('form.groups.name.placeholder')}
                      required
                      className="block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-indigo-500 focus:border-indigo-500"
                    />
                  </div>
                  <div>
                    <input
                      type="number"
                      value={group.averageSpeed || ''}
                      onChange={(e) =>
                        handleUpdateGroup(index, {
                          averageSpeed: e.target.value ? Number(e.target.value) : undefined,
                        })
                      }
                      placeholder={t('form.groups.speed.placeholder')}
                      min={0}
                      className="block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-indigo-500 focus:border-indigo-500"
                    />
                  </div>
                  <div>
                    <input
                      type="number"
                      value={group.maxParticipants || ''}
                      onChange={(e) =>
                        handleUpdateGroup(index, {
                          maxParticipants: e.target.value ? Number(e.target.value) : undefined,
                        })
                      }
                      placeholder={t('form.groups.maxParticipants.placeholder')}
                      min={1}
                      className="block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-indigo-500 focus:border-indigo-500"
                    />
                  </div>
                </div>
              )}
            </div>
          ))}
          {groups.length === 0 && (
            <p className="text-sm text-gray-500 italic text-center py-4">
              {t('form.groups.empty')}
            </p>
          )}
        </div>
      </div>

      {/* Submit / Cancel */}
      <div className="flex justify-end gap-3 pt-4 border-t border-gray-200">
        <button
          type="button"
          onClick={onCancel}
          disabled={isPending}
          className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50"
        >
          {tCommon('buttons.cancel')}
        </button>
        <button
          type="submit"
          disabled={isPending || !name.trim()}
          className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          {isPending && <LoadingSpinner size="sm" />}
          {submitButtonText || tCommon('buttons.save')}
        </button>
      </div>
    </form>
  )
}
