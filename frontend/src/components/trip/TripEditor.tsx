import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { XMarkIcon, PlusIcon, ChevronUpIcon, ChevronDownIcon } from '@heroicons/react/24/outline'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ApiClientError } from '../../lib/apiClient'
import { RoutePickerModal } from '../route/RoutePickerModal'
import { CreateRouteModal } from '../route/CreateRouteModal'
import { RoutePreview } from '../route/RoutePreview'
import { RoutePreviewCompact } from '../route/RoutePreviewCompact'
import { MediaEditor } from '../common/MediaEditor'
import { PlaceAutocomplete } from '../common/PlaceAutocomplete'
import { fromDateTimeLocalValue, toDateTimeLocalValue } from '../../utils/dateFormat'
import type { MediaDto, RouteDto, TeamDetailDto, PlaceDetailDto } from '../../api/api'
import { Visibility, Status } from '../../hooks/useTrip'
import { defaultMedia } from '@/lib/apiUtils'

export interface EditableStage {
  id?: string
  name: string
  dateTime: string // datetime-local value
  routeSlug?: string
  startPlace?: PlaceDetailDto
  endPlace?: PlaceDetailDto
  media: MediaDto
  isNew?: boolean
  isDeleted?: boolean
}

export interface TripFormData {
  name: string
  media: MediaDto
  dateTime: string // ISO string
  visibility: Visibility
  status: Status
  publishAt?: string // ISO string
  routeSlug?: string
  stages: EditableStage[]
}

interface TripEditorProps {
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
    stages: EditableStage[]
  }

  // Submission
  onSubmit: (data: TripFormData) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean
  error?: Error | ApiClientError | null

  // UI customization
  submitButtonText?: string
  cancelButtonText?: string
}

export function TripEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error,
  submitButtonText,
  cancelButtonText,
}: TripEditorProps) {
  const { t } = useTranslation('trips')
  const { t: tCommon } = useTranslation('common')

  // Form state - initialized from props
  const [name, setName] = useState(initialValues.name)
  const [media, setMedia] = useState<MediaDto>(initialValues.media)
  const [dateTime, setDateTime] = useState(initialValues.dateTime)
  const [visibility, setVisibility] = useState<Visibility>(initialValues.visibility)
  const [status, setStatus] = useState<Status>(initialValues.status)
  const [publishAt, setPublishAt] = useState(initialValues.publishAt || '')
  const [tripRouteSlug, setTripRouteSlug] = useState<string | null>(initialValues.routeSlug || null)
  const [stages, setStages] = useState<EditableStage[]>(initialValues.stages)

  // Modal state
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<
    'trip' | { type: 'stage'; index: number } | null
  >(null)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const filteredStages = stages.filter((s) => !s.isDeleted && s.name.trim())

    onSubmit({
      name,
      media,
      dateTime: fromDateTimeLocalValue(dateTime).toISOString(),
      status,
      visibility,
      publishAt: publishAt ? fromDateTimeLocalValue(publishAt).toISOString() : undefined,
      routeSlug: tripRouteSlug || undefined,
      stages: filteredStages.map((s) => ({
        ...s,
        dateTime: fromDateTimeLocalValue(s.dateTime).toISOString(),
      })),
    })
  }

  const handleAddStage = () => {
    // Default to trip date/time for new stage
    const newStageDate = new Date(fromDateTimeLocalValue(dateTime))
    newStageDate.setDate(newStageDate.getDate() + stages.filter((s) => !s.isDeleted).length)

    setStages([
      ...stages,
      {
        name: '',
        dateTime: toDateTimeLocalValue(newStageDate),
        routeSlug: undefined,
        startPlace: undefined,
        endPlace: undefined,
        media: defaultMedia(),
        isNew: true,
      },
    ])
  }

  const handleRemoveStage = (index: number) => {
    const stage = stages[index]
    if (stage.isNew) {
      setStages(stages.filter((_, i) => i !== index))
    } else {
      setStages(stages.map((s, i) => (i === index ? { ...s, isDeleted: true } : s)))
    }
  }

  const handleRestoreStage = (index: number) => {
    setStages(stages.map((s, i) => (i === index ? { ...s, isDeleted: false } : s)))
  }

  const handleUpdateStage = (index: number, updates: Partial<EditableStage>) => {
    setStages(stages.map((s, i) => (i === index ? { ...s, ...updates } : s)))
  }

  const handleMoveStage = (index: number, direction: 'up' | 'down') => {
    const newIndex = direction === 'up' ? index - 1 : index + 1
    if (newIndex < 0 || newIndex >= stages.length) return

    const newStages = [...stages]
    const temp = newStages[index]
    newStages[index] = newStages[newIndex]
    newStages[newIndex] = temp
    setStages(newStages)
  }

  const getFieldError = (field: string) => {
    if (error instanceof ApiClientError) {
      return error.error.errors?.find((e) => e.field === field)?.message
    }
    return undefined
  }

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

      {/* Start Date */}
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
            <label className="flex items-center">
              <input
                type="radio"
                name="visibility"
                value="PUBLIC"
                checked={visibility === 'PUBLIC'}
                onChange={() => setVisibility('PUBLIC')}
                className="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
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

      {/* Scheduled Publication */}
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
                setPickerTarget('trip')
                setShowRoutePickerModal(true)
              }}
              className="text-sm text-indigo-600 hover:text-indigo-700"
            >
              {tripRouteSlug ? t('create.form.route.change') : t('create.form.route.select')}
            </button>
            {tripRouteSlug && (
              <button
                type="button"
                onClick={() => setTripRouteSlug(null)}
                className="text-sm text-red-600 hover:text-red-700"
              >
                {t('create.form.route.clear')}
              </button>
            )}
          </div>
        </div>

        {tripRouteSlug ? (
          <RoutePreview routeSlug={tripRouteSlug} teamSlug={teamSlug} />
        ) : (
          <p className="text-sm text-gray-500 italic">{t('create.form.route.none')}</p>
        )}
        <p className="mt-1 text-sm text-gray-500">{t('create.form.route.hint')}</p>
      </div>

      {/* Stages */}
      <div>
        <div className="flex items-center justify-between mb-2">
          <label className="block text-sm font-medium text-gray-700">
            {t('create.form.stages.label')}
          </label>
          <button
            type="button"
            onClick={handleAddStage}
            className="inline-flex items-center text-sm text-indigo-600 hover:text-indigo-700"
          >
            <PlusIcon className="w-4 h-4 mr-1" />
            {t('create.form.stages.add')}
          </button>
        </div>
        <div className="space-y-4">
          {stages.map((stage, index) => (
            <div
              key={stage.id || `new-${index}`}
              className={`border rounded-lg p-4 ${
                stage.isDeleted
                  ? 'border-red-200 bg-red-50'
                  : stage.isNew
                    ? 'border-green-200 bg-green-50'
                    : 'border-gray-200'
              }`}
            >
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2">
                  <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-indigo-100 text-indigo-700 text-xs font-semibold">
                    {index + 1}
                  </span>
                  <span className="text-sm font-medium text-gray-700">
                    {stage.isNew
                      ? t('create.form.stages.new')
                      : stage.name || `${t('create.form.stages.label')} ${index + 1}`}
                    {stage.isDeleted && (
                      <span className="ml-2 text-red-600">
                        {t('create.form.stages.willBeDeleted')}
                      </span>
                    )}
                  </span>
                </div>
                <div className="flex items-center gap-1">
                  {!stage.isDeleted && (
                    <>
                      <button
                        type="button"
                        onClick={() => handleMoveStage(index, 'up')}
                        disabled={index === 0}
                        className="p-1 text-gray-400 hover:text-gray-600 disabled:opacity-30"
                      >
                        <ChevronUpIcon className="w-4 h-4" />
                      </button>
                      <button
                        type="button"
                        onClick={() => handleMoveStage(index, 'down')}
                        disabled={index === stages.length - 1}
                        className="p-1 text-gray-400 hover:text-gray-600 disabled:opacity-30"
                      >
                        <ChevronDownIcon className="w-4 h-4" />
                      </button>
                    </>
                  )}
                  {stage.isDeleted ? (
                    <button
                      type="button"
                      onClick={() => handleRestoreStage(index)}
                      className="text-sm text-indigo-600 hover:text-indigo-700"
                    >
                      {t('create.form.stages.restore')}
                    </button>
                  ) : (
                    <button
                      type="button"
                      onClick={() => handleRemoveStage(index)}
                      className="text-sm text-red-600 hover:text-red-700"
                    >
                      {t('create.form.stages.remove')}
                    </button>
                  )}
                </div>
              </div>
              {!stage.isDeleted && (
                <>
                  {/* Stage Name and DateTime */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3">
                    <div>
                      <input
                        type="text"
                        value={stage.name}
                        onChange={(e) => handleUpdateStage(index, { name: e.target.value })}
                        placeholder={t('create.form.stages.name.placeholder')}
                        required
                        className="block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-indigo-500 focus:border-indigo-500"
                      />
                    </div>
                    <div>
                      <input
                        type="datetime-local"
                        value={stage.dateTime}
                        onChange={(e) => handleUpdateStage(index, { dateTime: e.target.value })}
                        required
                        className="block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-indigo-500 focus:border-indigo-500"
                      />
                    </div>
                  </div>

                  {/* Stage Places */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3">
                    <div>
                      <label className="block text-xs font-medium text-gray-600 mb-1">
                        {t('create.form.stages.startPlace.label')}
                      </label>
                      {stage.startPlace ? (
                        <div className="flex items-center justify-between px-3 py-2 border border-gray-300 rounded-md bg-gray-50">
                          <span className="text-sm truncate">{stage.startPlace.name}</span>
                          <button
                            type="button"
                            onClick={() => handleUpdateStage(index, { startPlace: undefined })}
                            className="ml-2 p-1 text-gray-400 hover:text-red-500"
                          >
                            <XMarkIcon className="h-4 w-4" />
                          </button>
                        </div>
                      ) : (
                        <PlaceAutocomplete
                          teamSlug={teamSlug}
                          onSelect={(place) => handleUpdateStage(index, { startPlace: place })}
                          filterStart={true}
                          placeholder={t('create.form.stages.startPlace.placeholder')}
                        />
                      )}
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-gray-600 mb-1">
                        {t('create.form.stages.endPlace.label')}
                      </label>
                      {stage.endPlace ? (
                        <div className="flex items-center justify-between px-3 py-2 border border-gray-300 rounded-md bg-gray-50">
                          <span className="text-sm truncate">{stage.endPlace.name}</span>
                          <button
                            type="button"
                            onClick={() => handleUpdateStage(index, { endPlace: undefined })}
                            className="ml-2 p-1 text-gray-400 hover:text-red-500"
                          >
                            <XMarkIcon className="h-4 w-4" />
                          </button>
                        </div>
                      ) : (
                        <PlaceAutocomplete
                          teamSlug={teamSlug}
                          onSelect={(place) => handleUpdateStage(index, { endPlace: place })}
                          filterEnd={true}
                          placeholder={t('create.form.stages.endPlace.placeholder')}
                        />
                      )}
                    </div>
                  </div>

                  {/* Stage Description */}
                  <div className="mb-3">
                    <label className="block text-xs font-medium text-gray-600 mb-1">
                      {t('create.form.stages.description.label')}
                    </label>
                    <MediaEditor
                      value={stage.media}
                      onChange={(mediaOrFn) => {
                        const newMedia =
                          typeof mediaOrFn === 'function' ? mediaOrFn(stage.media) : mediaOrFn
                        handleUpdateStage(index, { media: newMedia })
                      }}
                      placeholder={t('create.form.stages.description.placeholder')}
                      minHeight="80px"
                      maxHeight="150px"
                      disabled={isPending}
                      ariaLabel={t('create.form.stages.description.label')}
                      teamSlug={teamSlug}
                    />
                  </div>

                  {/* Stage Route */}
                  <div className="pt-3 border-t border-gray-200">
                    <div className="flex items-center justify-between mb-1">
                      <label className="text-xs font-medium text-gray-600">
                        {t('create.form.stages.route.label')}
                      </label>
                      <div className="flex gap-2">
                        <button
                          type="button"
                          onClick={() => {
                            setPickerTarget({ type: 'stage', index })
                            setShowRoutePickerModal(true)
                          }}
                          className="text-xs text-indigo-600 hover:text-indigo-700"
                        >
                          {stage.routeSlug
                            ? t('create.form.route.change')
                            : t('create.form.route.select')}
                        </button>
                        {stage.routeSlug && (
                          <button
                            type="button"
                            onClick={() => handleUpdateStage(index, { routeSlug: undefined })}
                            className="text-xs text-red-600 hover:text-red-700"
                          >
                            {t('create.form.route.clear')}
                          </button>
                        )}
                      </div>
                    </div>
                    {stage.routeSlug ? (
                      <RoutePreviewCompact routeSlug={stage.routeSlug} teamSlug={teamSlug} />
                    ) : (
                      <p className="text-xs text-gray-400 italic">
                        {t('create.form.stages.route.none')}
                      </p>
                    )}
                  </div>
                </>
              )}
            </div>
          ))}
          {stages.length === 0 && (
            <p className="text-sm text-gray-500 italic">{t('create.form.stages.empty')}</p>
          )}
        </div>
        <p className="mt-2 text-sm text-gray-500">{t('create.form.stages.hint')}</p>
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
          disabled={isPending || !name.trim() || !dateTime}
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
          if (pickerTarget === 'trip') {
            setTripRouteSlug(route ? route.slug : null)
          } else if (pickerTarget && typeof pickerTarget === 'object') {
            handleUpdateStage(pickerTarget.index, { routeSlug: route ? route.slug : undefined })
          }
          setShowRoutePickerModal(false)
          setPickerTarget(null)
        }}
        teamSlug={teamSlug}
        selectedRouteSlug={
          pickerTarget === 'trip'
            ? tripRouteSlug
            : pickerTarget && typeof pickerTarget === 'object'
              ? stages[pickerTarget.index]?.routeSlug
              : null
        }
        title={
          pickerTarget === 'trip'
            ? t('create.form.route.selectForTrip')
            : t('create.form.route.selectForStage')
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
          if (pickerTarget === 'trip') {
            setTripRouteSlug(route.slug)
          } else if (pickerTarget && typeof pickerTarget === 'object') {
            handleUpdateStage(pickerTarget.index, { routeSlug: route.slug })
          }
          setShowCreateRouteModal(false)
          setPickerTarget(null)
        }}
        team={team}
      />
    </form>
  )
}
