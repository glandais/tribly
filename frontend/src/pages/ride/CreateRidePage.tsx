import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useTeam } from '../../hooks/useTeam'
import { useCreateRide, CreateGroupRequest, Visibility } from '../../hooks/useRide'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ApiClientError } from '../../api/client'

export function CreateRidePage() {
  const { t } = useTranslation('rides')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  // Calculate next Sunday
  const getNextSunday = () => {
    const today = new Date()
    const daysUntilSunday = (7 - today.getDay()) % 7 || 7
    const nextSunday = new Date(today)
    nextSunday.setDate(today.getDate() + daysUntilSunday)
    return nextSunday.toISOString().split('T')[0]
  }

  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [date, setDate] = useState(getNextSunday())
  const [startTime, setStartTime] = useState('08:00')
  const [visibility, setVisibility] = useState<Visibility>('TEAM')
  const [publishAt, setPublishAt] = useState('')
  const [groups, setGroups] = useState<CreateGroupRequest[]>([
    {
      name: t('create.form.groups.defaultName'),
      averageSpeed: undefined,
      maxParticipants: undefined,
    },
  ])

  const createMutation = useCreateRide(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.userRole === 'ADMIN' || team.userRole === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={`/teams/${teamSlug}/rides`} replace />
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const filteredGroups = groups.filter((g) => g.name.trim())
    createMutation.mutate({
      title,
      description: description || undefined,
      date,
      startTime: startTime || undefined,
      visibility,
      publishAt: publishAt ? new Date(publishAt).toISOString() : undefined,
      groups: filteredGroups.length > 0 ? filteredGroups : undefined,
    })
  }

  const handleAddGroup = () => {
    setGroups([...groups, { name: '', averageSpeed: undefined, maxParticipants: undefined }])
  }

  const handleRemoveGroup = (index: number) => {
    if (groups.length > 1) {
      setGroups(groups.filter((_, i) => i !== index))
    }
  }

  const handleUpdateGroup = (index: number, updates: Partial<CreateGroupRequest>) => {
    setGroups(groups.map((g, i) => (i === index ? { ...g, ...updates } : g)))
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
          to={`/teams/${teamSlug}/rides`}
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
          {t('create.backToRides')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-1 text-gray-600">{t('create.subtitle', { teamName: team.name })}</p>
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
            {t('create.form.title.label')} <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            minLength={3}
            maxLength={200}
            className={`mt-1 block w-full px-4 py-2 border rounded-lg focus:ring-indigo-500 focus:border-indigo-500 ${
              getFieldError('title') ? 'border-red-300' : 'border-gray-300'
            }`}
            placeholder={t('create.form.title.placeholder')}
          />
          {getFieldError('title') && (
            <p className="mt-1 text-sm text-red-600">{getFieldError('title')}</p>
          )}
        </div>

        {/* Description */}
        <div>
          <label htmlFor="description" className="block text-sm font-medium text-gray-700">
            {t('create.form.description.label')}
          </label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            maxLength={5000}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder={t('create.form.description.placeholder')}
          />
        </div>

        {/* Date and Time */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label htmlFor="date" className="block text-sm font-medium text-gray-700">
              {t('create.form.date.label')} <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              id="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
              min={new Date().toISOString().split('T')[0]}
              className={`mt-1 block w-full px-4 py-2 border rounded-lg focus:ring-indigo-500 focus:border-indigo-500 ${
                getFieldError('date') ? 'border-red-300' : 'border-gray-300'
              }`}
            />
            {getFieldError('date') && (
              <p className="mt-1 text-sm text-red-600">{getFieldError('date')}</p>
            )}
          </div>
          <div>
            <label htmlFor="startTime" className="block text-sm font-medium text-gray-700">
              {t('create.form.startTime.label')}
            </label>
            <input
              type="time"
              id="startTime"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
        </div>

        {/* Visibility */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t('create.form.visibility.label')}
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
              <span className="ml-2 text-sm text-gray-700">{t('create.form.visibility.team')}</span>
            </label>
            <label
              className={`flex items-center ${!team.isPublic ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              <input
                type="radio"
                name="visibility"
                value="PUBLIC"
                checked={visibility === 'PUBLIC'}
                onChange={() => setVisibility('PUBLIC')}
                disabled={!team.isPublic}
                className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500 disabled:cursor-not-allowed"
              />
              <span className="ml-2 text-sm text-gray-700">
                {t('create.form.visibility.public')}
              </span>
            </label>
          </div>
          {!team.isPublic && (
            <p className="mt-2 text-sm text-gray-500">
              {t('create.form.visibility.privateTeamHint')}
            </p>
          )}
        </div>

        {/* Scheduled Publication */}
        <div>
          <label htmlFor="publishAt" className="block text-sm font-medium text-gray-700">
            {t('create.form.publishAt.label')}
          </label>
          <input
            type="datetime-local"
            id="publishAt"
            value={publishAt}
            onChange={(e) => setPublishAt(e.target.value)}
            min={new Date().toISOString().slice(0, 16)}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
          />
          <p className="mt-1 text-sm text-gray-500">{t('create.form.publishAt.hint')}</p>
        </div>

        {/* Groups */}
        <div>
          <div className="flex items-center justify-between mb-2">
            <label className="block text-sm font-medium text-gray-700">
              {t('create.form.groups.label')}
            </label>
            <button
              type="button"
              onClick={handleAddGroup}
              className="text-sm text-indigo-600 hover:text-indigo-700"
            >
              {t('create.form.groups.add')}
            </button>
          </div>
          <div className="space-y-3">
            {groups.map((group, index) => (
              <div key={index} className="border border-gray-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-3">
                  <span className="text-sm font-medium text-gray-700">
                    {t('create.form.groups.new')} {index + 1}
                  </span>
                  {groups.length > 1 && (
                    <button
                      type="button"
                      onClick={() => handleRemoveGroup(index)}
                      className="text-sm text-red-600 hover:text-red-700"
                    >
                      {t('create.form.groups.remove')}
                    </button>
                  )}
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div>
                    <input
                      type="text"
                      value={group.name}
                      onChange={(e) => handleUpdateGroup(index, { name: e.target.value })}
                      placeholder={t('create.form.groups.name.placeholder')}
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
                      placeholder={t('create.form.groups.speed.placeholder')}
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
                      placeholder={t('create.form.groups.maxParticipants.placeholder')}
                      min={1}
                      className="block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-indigo-500 focus:border-indigo-500"
                    />
                  </div>
                </div>
              </div>
            ))}
          </div>
          <p className="mt-2 text-sm text-gray-500">{t('create.form.groups.hint')}</p>
        </div>

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Link
            to={`/teams/${teamSlug}/rides`}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            {tCommon('buttons.cancel')}
          </Link>
          <button
            type="submit"
            disabled={createMutation.isPending || !title.trim() || !date}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
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
