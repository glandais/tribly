import { useState, useEffect } from 'react'
import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useRide, useUpdateRide, Visibility, Status } from '../../hooks/useRide'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ApiClientError } from '../../lib/apiClient'
import { RoutePickerModal } from '../../components/route/RoutePickerModal'
import { CreateRouteModal } from '../../components/route/CreateRouteModal'
import { RoutePreview } from '../../components/route/RoutePreview'
import { RoutePreviewCompact } from '../../components/route/RoutePreviewCompact'
import type { RouteDto } from '../../api/api'
import { toDateTimeLocalValue, fromDateTimeLocalValue } from '../../utils/dateFormat'

interface EditableGroup {
  id?: string
  name: string
  description?: string
  averageSpeed?: number
  maxParticipants?: number
  routeSlug?: string
  isNew?: boolean
  isDeleted?: boolean
}

export function EditRidePage() {
  const { t } = useTranslation('rides')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug, rideSlug } = useParams<{ teamSlug: string; rideSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: ride, isLoading: isLoadingRide } = useRide(teamSlug, rideSlug)

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [dateTime, setDateTime] = useState('')
  const [visibility, setVisibility] = useState<Visibility>(Visibility.Team)
  const [status, setStatus] = useState<Status>(Status.Draft)
  const [publishAt, setPublishAt] = useState('')
  const [rideRouteSlug, setRideRouteSlug] = useState<string | null>(null)
  const [groups, setGroups] = useState<EditableGroup[]>([])
  const [initialized, setInitialized] = useState(false)
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<
    'ride' | { type: 'group'; index: number } | null
  >(null)

  const updateMutation = useUpdateRide(teamSlug, rideSlug!)

  // Initialize form state from fetched ride data
  useEffect(() => {
    if (ride && !initialized) {
      // eslint-disable-next-line react-hooks/set-state-in-effect -- Form initialization from server data
      setName(ride.name)
      setDescription(ride.description || '')
      setDateTime(toDateTimeLocalValue(ride.dateTime))
      setVisibility(ride.visibility)
      setStatus(ride.status)
      setPublishAt(ride.publishAt ? toDateTimeLocalValue(ride.publishAt) : '')
      setRideRouteSlug(ride.routeSlug || null)
      setGroups(
        ride.groups?.map((g) => ({
          id: g.id,
          name: g.name,
          description: g.description || undefined,
          averageSpeed: g.averageSpeed || undefined,
          maxParticipants: g.maxParticipants || undefined,
          routeSlug: g.routeSlug || undefined,
        })) || []
      )
      setInitialized(true)
    }
  }, [ride, initialized])

  if (isLoadingTeam || isLoadingRide) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !ride) {
    return <Navigate to={`/teams/${teamSlug}/rides`} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={`/teams/${teamSlug}/rides/${rideSlug}`} replace />
  }

  const isSaving = updateMutation.isPending

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const filteredGroups = groups.filter((g) => g.name.trim())

    // Update ride details
    await updateMutation.mutateAsync({
      name,
      status,
      description: description || undefined,
      dateTime: fromDateTimeLocalValue(dateTime).toISOString(),
      visibility,
      publishAt: publishAt ? fromDateTimeLocalValue(publishAt).toISOString() : undefined,
      routeSlug: rideRouteSlug || undefined,
      groups: filteredGroups,
    })

    navigate(`/teams/${teamSlug}/rides/${rideSlug}`)
  }

  const handleAddGroup = () => {
    setGroups([...groups, { name: '', isNew: true }])
  }

  const handleRemoveGroup = (index: number) => {
    const group = groups[index]
    if (group.isNew) {
      // Remove new group entirely
      setGroups(groups.filter((_, i) => i !== index))
    } else {
      // Mark existing group as deleted
      setGroups(groups.map((g, i) => (i === index ? { ...g, isDeleted: true } : g)))
    }
  }

  const handleRestoreGroup = (index: number) => {
    setGroups(groups.map((g, i) => (i === index ? { ...g, isDeleted: false } : g)))
  }

  const handleUpdateGroup = (index: number, updates: Partial<EditableGroup>) => {
    setGroups(groups.map((g, i) => (i === index ? { ...g, ...updates } : g)))
  }

  const getFieldError = (field: string) => {
    if (updateMutation.error instanceof ApiClientError) {
      return updateMutation.error.error.errors?.find((e) => e.field === field)?.message
    }
    return undefined
  }

  const activeGroups = groups.filter((g) => !g.isDeleted)

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/rides/${rideSlug}`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('edit.backToRide')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-1 text-gray-600">{t('edit.subtitle', { teamName: team.name })}</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {updateMutation.error &&
          !(
            updateMutation.error instanceof ApiClientError && updateMutation.error.error.errors
          ) && (
            <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-red-700">
                {updateMutation.error instanceof ApiClientError
                  ? updateMutation.error.error.message
                  : t('edit.error')}
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
                checked={visibility === Visibility.Team}
                onChange={() => setVisibility(Visibility.Team)}
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
                checked={visibility === Visibility.Public}
                onChange={() => setVisibility(Visibility.Public)}
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

        {/* Status */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t('create.form.status.label')}
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
              <span className="ml-2 text-sm text-gray-700">{t('status.DRAFT')}</span>
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
              <span className="ml-2 text-sm text-gray-700">{t('status.PUBLISHED')}</span>
            </label>
            <label className="flex items-center">
              <input
                type="radio"
                name="status"
                value="CANCELLED"
                checked={status === Status.Cancelled}
                onChange={() => setStatus(Status.Cancelled)}
                className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
              />
              <span className="ml-2 text-sm text-gray-700">{t('status.CANCELLED')}</span>
            </label>
          </div>
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
                onClick={() => {
                  setPickerTarget('ride')
                  setShowRoutePickerModal(true)
                }}
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
                    {group.isNew
                      ? t('create.form.groups.new')
                      : `${t('create.form.groups.label')} ${index + 1}`}
                    {group.isDeleted && (
                      <span className="ml-2 text-red-600">
                        {t('create.form.groups.willBeDeleted')}
                      </span>
                    )}
                  </span>
                  {group.isDeleted ? (
                    <button
                      type="button"
                      onClick={() => handleRestoreGroup(index)}
                      className="text-sm text-indigo-600 hover:text-indigo-700"
                    >
                      {t('create.form.groups.restore')}
                    </button>
                  ) : (
                    <button
                      type="button"
                      onClick={() => handleRemoveGroup(index)}
                      className="text-sm text-red-600 hover:text-red-700"
                    >
                      {t('create.form.groups.remove')}
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
                        placeholder={t('create.form.groups.name.placeholder')}
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
                )}

                {/* Route Selection for Group */}
                {!group.isDeleted && (
                  <div className="mt-3 pt-3 border-t border-gray-200">
                    <div className="flex items-center justify-between mb-1">
                      <label className="text-xs font-medium text-gray-600">
                        {t('create.form.groups.route.label')}
                      </label>
                      <div className="flex gap-2">
                        <button
                          type="button"
                          onClick={() => {
                            setPickerTarget({ type: 'group', index })
                            setShowRoutePickerModal(true)
                          }}
                          className="text-xs text-indigo-600 hover:text-indigo-700"
                        >
                          {group.routeSlug
                            ? t('create.form.route.change')
                            : t('create.form.route.select')}
                        </button>
                        {group.routeSlug && (
                          <button
                            type="button"
                            onClick={() => handleUpdateGroup(index, { routeSlug: undefined })}
                            className="text-xs text-red-600 hover:text-red-700"
                          >
                            {t('create.form.route.clear')}
                          </button>
                        )}
                      </div>
                    </div>
                    {group.routeSlug ? (
                      <RoutePreviewCompact routeSlug={group.routeSlug} teamSlug={teamSlug!} />
                    ) : (
                      <p className="text-xs text-gray-400 italic">
                        {t('create.form.groups.route.none')}
                      </p>
                    )}
                  </div>
                )}
              </div>
            ))}
            {groups.length === 0 && (
              <p className="text-sm text-gray-500 italic">{t('create.form.groups.empty')}</p>
            )}
          </div>
          <p className="mt-2 text-sm text-gray-500">{t('create.form.groups.hint')}</p>
        </div>

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Link
            to={`/teams/${teamSlug}/rides/${rideSlug}`}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            {tCommon('buttons.cancel')}
          </Link>
          <button
            type="submit"
            disabled={
              isSaving ||
              !name.trim() ||
              !dateTime ||
              activeGroups.filter((g) => g.name.trim()).length === 0
            }
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isSaving ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {t('edit.saving')}
              </>
            ) : (
              t('edit.button')
            )}
          </button>
        </div>
      </form>

      {/* Route Picker Modal */}
      <RoutePickerModal
        isOpen={showRoutePickerModal}
        onClose={() => {
          setShowRoutePickerModal(false)
          setPickerTarget(null)
        }}
        onSelect={(route: RouteDto | null) => {
          if (pickerTarget === 'ride') {
            setRideRouteSlug(route ? route.slug : null)
          } else if (pickerTarget && typeof pickerTarget === 'object') {
            handleUpdateGroup(pickerTarget.index, { routeSlug: route ? route.slug : undefined })
          }
          setShowRoutePickerModal(false)
          setPickerTarget(null)
        }}
        teamSlug={teamSlug!}
        selectedRouteSlug={
          pickerTarget === 'ride'
            ? rideRouteSlug
            : pickerTarget && typeof pickerTarget === 'object'
              ? groups[pickerTarget.index]?.routeSlug
              : null
        }
        title={
          pickerTarget === 'ride'
            ? t('create.form.route.selectForRide')
            : t('create.form.route.selectForGroup')
        }
        onCreateNew={() => {
          setShowRoutePickerModal(false)
          setShowCreateRouteModal(true)
        }}
      />

      {/* Create Route Modal */}
      <CreateRouteModal
        isOpen={showCreateRouteModal}
        onClose={() => {
          setShowCreateRouteModal(false)
          setPickerTarget(null)
        }}
        onRouteCreated={(route: RouteDto) => {
          if (pickerTarget === 'ride') {
            setRideRouteSlug(route.slug)
          } else if (pickerTarget && typeof pickerTarget === 'object') {
            handleUpdateGroup(pickerTarget.index, { routeSlug: route.slug })
          }
          setShowCreateRouteModal(false)
          setPickerTarget(null)
        }}
        teamSlug={teamSlug!}
        teamVisibility={team.visibility}
      />
    </div>
  )
}
