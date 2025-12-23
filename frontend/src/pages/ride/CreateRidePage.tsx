import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useCreateRide, GroupRequest, Visibility } from '../../hooks/useRide'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ApiClientError } from '../../lib/apiClient'
import { RoutePickerModal } from '../../components/route/RoutePickerModal'
import { CreateRouteModal } from '../../components/route/CreateRouteModal'
import { RoutePreview } from '../../components/route/RoutePreview'
import type { RouteDto } from '../../api/api'
import { toDateTimeLocalValue, fromDateTimeLocalValue } from '../../utils/dateFormat'

export function CreateRidePage() {
  const { t } = useTranslation('rides')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  // Calculate next Sunday at 8am
  const getNextSunday = () => {
    const today = new Date()
    const daysUntilSunday = (7 - today.getDay()) % 7 || 7
    const nextSunday = new Date(today)
    nextSunday.setDate(today.getDate() + daysUntilSunday)
    nextSunday.setHours(8, 0, 0, 0)
    return toDateTimeLocalValue(nextSunday)
  }

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [dateTime, setDateTime] = useState(getNextSunday())
  const [visibility, setVisibility] = useState<Visibility>(Visibility.Team)
  const [publishAt, setPublishAt] = useState('')
  const [rideRouteSlug, setRideRouteSlug] = useState<string | null>(null)
  const [groups, setGroups] = useState<GroupRequest[]>([
    {
      name: t('create.form.groups.defaultName'),
      averageSpeed: undefined,
      maxParticipants: undefined,
    },
  ])
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)

  const createMutation = useCreateRide(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={`/teams/${teamSlug}/rides`} replace />
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const filteredGroups = groups.filter((g) => g.name.trim())
    createMutation.mutate({
      name,
      description: description || undefined,
      dateTime: fromDateTimeLocalValue(dateTime).toISOString(),
      status: 'DRAFT',
      visibility,
      publishAt: publishAt ? fromDateTimeLocalValue(publishAt).toISOString() : undefined,
      routeSlug: rideRouteSlug || undefined,
      groups: filteredGroups,
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

  const handleUpdateGroup = (index: number, updates: Partial<GroupRequest>) => {
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
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
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
            value={name}
            onChange={(e) => setName(e.target.value)}
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
              type="datetime-local"
              id="date"
              value={dateTime}
              onChange={(e) => setDateTime(e.target.value)}
              required
              min={toDateTimeLocalValue(new Date())}
              className={`mt-1 block w-full px-4 py-2 border rounded-lg focus:ring-indigo-500 focus:border-indigo-500 ${
                getFieldError('date') ? 'border-red-300' : 'border-gray-300'
              }`}
            />
            {getFieldError('date') && (
              <p className="mt-1 text-sm text-red-600">{getFieldError('date')}</p>
            )}
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
              <span className="ml-2 text-sm text-gray-700">
                {t('create.form.visibility.public')}
              </span>
            </label>
          </div>
          {team.visibility === 'TEAM' && (
            <p className="mt-2 text-sm text-gray-500">
              {t('create.form.visibility.privateTeamHint')}
            </p>
          )}
        </div>

        {/* Route Selection */}
        <div>
          <div className="flex items-center justify-between mb-2">
            <label className="block text-sm font-medium text-gray-700">
              {t('create.form.route.label')}
            </label>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => setShowRoutePickerModal(true)}
                className="text-sm text-indigo-600 hover:text-indigo-700"
              >
                {rideRouteSlug ? t('create.form.route.change') : t('create.form.route.select')}
              </button>
              {rideRouteSlug && (
                <button
                  type="button"
                  onClick={() => setRideRouteSlug(null)}
                  className="text-sm text-red-600 hover:text-red-700"
                >
                  {t('create.form.route.clear')}
                </button>
              )}
            </div>
          </div>

          {rideRouteSlug ? (
            <RoutePreview routeSlug={rideRouteSlug} teamSlug={teamSlug!} />
          ) : (
            <p className="text-sm text-gray-500 italic">{t('create.form.route.none')}</p>
          )}
          <p className="mt-1 text-sm text-gray-500">{t('create.form.route.hint')}</p>
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
            min={toDateTimeLocalValue(new Date())}
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
            disabled={createMutation.isPending || !name.trim() || !dateTime}
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

      {/* Route Picker Modal */}
      <RoutePickerModal
        isOpen={showRoutePickerModal}
        onClose={() => setShowRoutePickerModal(false)}
        onSelect={(route: RouteDto | null) => {
          setRideRouteSlug(route ? route.slug : null)
          setShowRoutePickerModal(false)
        }}
        teamSlug={teamSlug!}
        selectedRouteSlug={rideRouteSlug}
        title={t('create.form.route.selectForRide')}
        onCreateNew={() => {
          setShowRoutePickerModal(false)
          setShowCreateRouteModal(true)
        }}
      />

      {/* Create Route Modal */}
      <CreateRouteModal
        isOpen={showCreateRouteModal}
        onClose={() => setShowCreateRouteModal(false)}
        onRouteCreated={(route: RouteDto) => {
          setRideRouteSlug(route.slug)
          setShowCreateRouteModal(false)
        }}
        teamSlug={teamSlug!}
        teamVisibility={team.visibility}
      />
    </div>
  )
}
