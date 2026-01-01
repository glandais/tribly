import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { XMarkIcon } from '@heroicons/react/24/outline'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ReorderControls } from '../common/ReorderControls'
import { moveItem } from '../../utils/arrayUtils'
import { ApiClientError } from '../../lib/apiClient'
import { RoutePickerModal } from '../route/RoutePickerModal'
import { CreateRouteModal } from '../route/CreateRouteModal'
import { RoutePreview } from '../route/RoutePreview'
import { RoutePreviewCompact } from '../route/RoutePreviewCompact'
import { MediaEditor } from '../common/MediaEditor'
import { PlaceAutocomplete } from '../common/PlaceAutocomplete'
import { fromDateTimeLocalValue } from '../../utils/dateFormat'
import type { MediaDto, RouteDto, TeamDetailDto, PlaceDetailDto } from '../../api/api'
import { Visibility, Status } from '../../hooks/useRide'

export interface EditableGroup {
  id?: string
  name: string
  time?: string
  averageSpeed?: number
  maxParticipants?: number
  routeSlug?: string
  isNew?: boolean
  isDeleted?: boolean
}

export interface RideFormData {
  name: string
  media: MediaDto
  dateTime: string // ISO string
  visibility: Visibility
  status: Status
  publishAt?: string // ISO string
  routeSlug?: string
  startPlaceId?: string
  endPlaceId?: string
  groups: EditableGroup[]
}

interface RideEditorProps {
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
    routeSlug?: string
    startPlace?: PlaceDetailDto
    endPlace?: PlaceDetailDto
    groups: EditableGroup[]
  }

  // Submission
  onSubmit: (data: RideFormData) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean
  error?: Error | ApiClientError | null

  // UI customization
  submitButtonText?: string
  cancelButtonText?: string
}

export function RideEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error,
  submitButtonText,
  cancelButtonText,
}: RideEditorProps) {
  const { t } = useTranslation('rides')
  const { t: tCommon } = useTranslation('common')

  // Form state - initialized from props (use key prop on parent to reset)
  const [name, setName] = useState(initialValues.name)
  const [media, setMedia] = useState<MediaDto>(initialValues.media)
  const [dateTime, setDateTime] = useState(initialValues.dateTime)
  const [visibility, setVisibility] = useState<Visibility>(initialValues.visibility)
  const [status, setStatus] = useState<Status>(initialValues.status)
  const [publishAt, setPublishAt] = useState(initialValues.publishAt || '')
  const [rideRouteSlug, setRideRouteSlug] = useState<string | null>(initialValues.routeSlug || null)
  const [startPlace, setStartPlace] = useState<PlaceDetailDto | null>(
    initialValues.startPlace || null
  )
  const [endPlace, setEndPlace] = useState<PlaceDetailDto | null>(initialValues.endPlace || null)
  const [groups, setGroups] = useState<EditableGroup[]>(initialValues.groups)

  // Modal state
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<
    'ride' | { type: 'group'; index: number } | null
  >(null)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const filteredGroups = groups.filter((g) => !g.isDeleted && g.name.trim())

    onSubmit({
      name,
      media,
      dateTime: fromDateTimeLocalValue(dateTime).toISOString(),
      status,
      visibility,
      publishAt: publishAt ? fromDateTimeLocalValue(publishAt).toISOString() : undefined,
      routeSlug: rideRouteSlug || undefined,
      startPlaceId: startPlace?.id,
      endPlaceId: endPlace?.id,
      groups: filteredGroups,
    })
  }

  const handleAddGroup = () => {
    setGroups([
      ...groups,
      {
        name: '',
        time: undefined,
        averageSpeed: undefined,
        maxParticipants: undefined,
        routeSlug: undefined,
        isNew: true,
      },
    ])
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

  const handleMoveGroup = (index: number, direction: 'up' | 'down') => {
    setGroups(moveItem(groups, index, direction))
  }

  const getFieldError = (field: string) => {
    if (error instanceof ApiClientError) {
      return error.error.errors?.find((e) => e.field === field)?.message
    }
    return undefined
  }

  const activeGroups = groups.filter((g) => !g.isDeleted)

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Form-level error */}
      {error && !(error instanceof ApiClientError && error.error.errors) && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">
            {error instanceof ApiClientError ? error.error.message : t('create.error')}
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
        <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
          {t('create.form.description.label')}
        </label>
        <MediaEditor
          value={media}
          onChange={setMedia}
          placeholder={t('create.form.description.placeholder')}
          minHeight="150px"
          maxHeight="300px"
          disabled={isPending}
          ariaLabel={t('create.form.description.label')}
          teamSlug={teamSlug}
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

      {/* Start and End Places */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {t('create.form.startPlace.label')}
          </label>
          {startPlace ? (
            <div className="flex items-center justify-between px-4 py-2 border border-gray-300 rounded-lg bg-gray-50">
              <div className="min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">{startPlace.name}</p>
                {startPlace.address && (
                  <p className="text-xs text-gray-500 truncate">{startPlace.address}</p>
                )}
              </div>
              <button
                type="button"
                onClick={() => setStartPlace(null)}
                className="ml-2 p-1 text-gray-400 hover:text-red-500"
              >
                <XMarkIcon className="h-4 w-4" />
              </button>
            </div>
          ) : (
            <PlaceAutocomplete
              teamSlug={teamSlug}
              onSelect={setStartPlace}
              filterStart={true}
              placeholder={t('create.form.startPlace.placeholder')}
            />
          )}
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {t('create.form.endPlace.label')}
          </label>
          {endPlace ? (
            <div className="flex items-center justify-between px-4 py-2 border border-gray-300 rounded-lg bg-gray-50">
              <div className="min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">{endPlace.name}</p>
                {endPlace.address && (
                  <p className="text-xs text-gray-500 truncate">{endPlace.address}</p>
                )}
              </div>
              <button
                type="button"
                onClick={() => setEndPlace(null)}
                className="ml-2 p-1 text-gray-400 hover:text-red-500"
              >
                <XMarkIcon className="h-4 w-4" />
              </button>
            </div>
          ) : (
            <PlaceAutocomplete
              teamSlug={teamSlug}
              onSelect={setEndPlace}
              filterEnd={true}
              placeholder={t('create.form.endPlace.placeholder')}
            />
          )}
        </div>
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

      {/* Scheduled Publication (only shown when status === DRAFT) */}
      {status === Status.Draft && (
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
      )}

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
          <RoutePreview routeSlug={rideRouteSlug} teamSlug={teamSlug} />
        ) : (
          <p className="text-sm text-gray-500 italic">{t('create.form.route.none')}</p>
        )}
        <p className="mt-1 text-sm text-gray-500">{t('create.form.route.hint')}</p>
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
                <div className="flex items-center gap-2">
                  <ReorderControls
                    index={index}
                    total={groups.length}
                    onMove={(dir) => handleMoveGroup(index, dir)}
                    disabled={group.isDeleted}
                  />
                  <span className="text-sm font-medium text-gray-700">
                    {group.isNew
                      ? t('create.form.groups.new')
                      : group.name || `${t('create.form.groups.label')} ${index + 1}`}
                    {group.isDeleted && (
                      <span className="ml-2 text-red-600">
                        {t('create.form.groups.willBeDeleted')}
                      </span>
                    )}
                  </span>
                </div>
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
                <>
                  <div className="grid grid-cols-1 sm:grid-cols-4 gap-3">
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
                    <div className="relative">
                      <input
                        type="time"
                        value={group.time || ''}
                        onChange={(e) =>
                          handleUpdateGroup(index, {
                            time: e.target.value || undefined,
                          })
                        }
                        className="block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-indigo-500 focus:border-indigo-500"
                      />
                      {group.time && (
                        <button
                          type="button"
                          onClick={() => handleUpdateGroup(index, { time: undefined })}
                          className="absolute right-8 top-1/2 -translate-y-1/2 p-1 text-gray-400 hover:text-gray-600"
                        >
                          <XMarkIcon className="w-4 h-4" />
                        </button>
                      )}
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

                  {/* Route Selection for Group */}
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
                      <RoutePreviewCompact routeSlug={group.routeSlug} teamSlug={teamSlug} />
                    ) : (
                      <p className="text-xs text-gray-400 italic">
                        {t('create.form.groups.route.none')}
                      </p>
                    )}
                  </div>
                </>
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
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
        >
          {cancelButtonText || tCommon('buttons.cancel')}
        </button>
        <button
          type="submit"
          disabled={
            isPending ||
            !name.trim() ||
            !dateTime ||
            activeGroups.filter((g) => g.name.trim()).length === 0
          }
          className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isPending ? (
            <>
              <LoadingSpinner size="sm" color="white" className="mr-2" />
              {tCommon('status.saving')}
            </>
          ) : (
            submitButtonText || tCommon('buttons.save')
          )}
        </button>
      </div>

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
        teamSlug={teamSlug}
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
        team={team}
      />
    </form>
  )
}
