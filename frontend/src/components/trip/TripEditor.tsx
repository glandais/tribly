import { useState, useEffect } from 'react'
import { useForm, useFieldArray, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import { XMarkIcon, PlusIcon } from '@heroicons/react/24/outline'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ReorderControls } from '../common/ReorderControls'
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
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'

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

const stageSchema = z.object({
  id: z.string().optional(),
  name: z.string(),
  dateTime: z.string(),
  routeSlug: z.string().optional(),
  startPlace: z.custom<PlaceDetailDto>().optional(),
  endPlace: z.custom<PlaceDetailDto>().optional(),
  media: z.custom<MediaDto>(),
  isNew: z.boolean().optional(),
  isDeleted: z.boolean().optional(),
})

const tripSchema = z.object({
  name: z.string().min(3).max(200),
  media: z.custom<MediaDto>(),
  dateTime: z.string().min(1),
  visibility: z.enum(Visibility),
  status: z.enum(Status),
  publishAt: z.string().optional(),
  routeSlug: z.string().optional(),
  stages: z.array(stageSchema),
})

type TripFormValues = z.infer<typeof tripSchema>

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

  // Modal state
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<
    'trip' | { type: 'stage'; index: number } | null
  >(null)

  const form = useForm<TripFormValues>({
    resolver: zodResolver(tripSchema),
    defaultValues: {
      name: initialValues.name,
      media: initialValues.media,
      dateTime: initialValues.dateTime,
      visibility: initialValues.visibility,
      status: initialValues.status,
      publishAt: initialValues.publishAt || '',
      routeSlug: initialValues.routeSlug || null,
      stages: initialValues.stages,
    },
  })

  const { fields, append, remove, update, move } = useFieldArray({
    control: form.control,
    name: 'stages',
  })

  const status = useWatch({ control: form.control, name: 'status' })
  const name = useWatch({ control: form.control, name: 'name' })
  const dateTime = useWatch({ control: form.control, name: 'dateTime' })
  const stages = useWatch({ control: form.control, name: 'stages' })
  const routeSlug = useWatch({ control: form.control, name: 'routeSlug' })

  // Set server-side errors on form fields
  useEffect(() => {
    if (error instanceof ApiClientError && error.error.errors) {
      error.error.errors.forEach((err) => {
        if (err.field && err.field in tripSchema.shape) {
          form.setError(err.field as keyof TripFormValues, { message: err.message })
        }
      })
    }
  }, [error, form])

  const handleFormSubmit = (values: TripFormValues) => {
    const filteredStages = values.stages.filter((s) => !s.isDeleted && s.name.trim())

    onSubmit({
      name: values.name,
      media: values.media,
      dateTime: fromDateTimeLocalValue(values.dateTime).toISOString(),
      status: values.status,
      visibility: values.visibility,
      publishAt: values.publishAt
        ? fromDateTimeLocalValue(values.publishAt).toISOString()
        : undefined,
      routeSlug: values.routeSlug || undefined,
      stages: filteredStages.map((s) => ({
        ...s,
        dateTime: fromDateTimeLocalValue(s.dateTime).toISOString(),
      })),
    })
  }

  const handleAddStage = () => {
    // Default to trip date/time for new stage
    const activeStages = stages?.filter((s) => !s.isDeleted) || []
    const newStageDate = new Date(fromDateTimeLocalValue(dateTime || new Date().toISOString()))
    newStageDate.setDate(newStageDate.getDate() + activeStages.length)

    append({
      name: '',
      dateTime: toDateTimeLocalValue(newStageDate),
      routeSlug: undefined,
      startPlace: undefined,
      endPlace: undefined,
      media: defaultMedia(),
      isNew: true,
    })
  }

  const handleRemoveStage = (index: number) => {
    const stage = fields[index]
    if (stage.isNew) {
      remove(index)
    } else {
      update(index, { ...stages[index], isDeleted: true })
    }
  }

  const handleRestoreStage = (index: number) => {
    update(index, { ...stages[index], isDeleted: false })
  }

  const handleMoveStage = (index: number, direction: 'up' | 'down') => {
    const newIndex = direction === 'up' ? index - 1 : index + 1
    if (newIndex >= 0 && newIndex < fields.length) {
      move(index, newIndex)
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleFormSubmit)} className="space-y-6">
        {/* Form-level error */}
        {error && !(error instanceof ApiClientError && error.error.errors) && (
          <div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg">
            <p className="text-destructive">
              {error instanceof ApiClientError ? error.error.message : t('create.error')}
            </p>
          </div>
        )}

        {/* Title */}
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>
                {t('create.form.title.label')} <span className="text-destructive">*</span>
              </FormLabel>
              <FormControl>
                <Input placeholder={t('create.form.title.placeholder')} {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Description */}
        <FormField
          control={form.control}
          name="media"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{tCommon('form.description')}</FormLabel>
              <FormControl>
                <MediaEditor
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={t('create.form.description.placeholder')}
                  minHeight="150px"
                  maxHeight="300px"
                  disabled={isPending}
                  ariaLabel={tCommon('form.description')}
                  teamSlug={teamSlug}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Start Date */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="dateTime"
            render={({ field }) => (
              <FormItem>
                <FormLabel>
                  {tCommon('startPlace')} <span className="text-destructive">*</span>
                </FormLabel>
                <FormControl>
                  <Input type="datetime-local" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Visibility */}
        {team.visibility !== 'TEAM' && (
          <FormField
            control={form.control}
            name="visibility"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{tCommon('visibility.label')}</FormLabel>
                <FormControl>
                  <RadioGroup
                    value={field.value}
                    onValueChange={field.onChange}
                    className="space-y-2"
                  >
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="TEAM" id="visibility-team" />
                      <Label htmlFor="visibility-team">{tCommon('visibility.team')}</Label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="PUBLIC" id="visibility-public" />
                      <Label htmlFor="visibility-public">{tCommon('visibility.public')}</Label>
                    </div>
                  </RadioGroup>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        {/* Status */}
        <FormField
          control={form.control}
          name="status"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{tCommon('form.status')}</FormLabel>
              <FormControl>
                <RadioGroup
                  value={field.value}
                  onValueChange={field.onChange}
                  className="space-y-2"
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="DRAFT" id="status-draft" />
                    <Label htmlFor="status-draft">{tCommon('status.DRAFT')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="PUBLISHED" id="status-published" />
                    <Label htmlFor="status-published">{tCommon('status.PUBLISHED')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="CANCELLED" id="status-cancelled" />
                    <Label htmlFor="status-cancelled">{tCommon('status.CANCELLED')}</Label>
                  </div>
                </RadioGroup>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Scheduled Publication */}
        {status === Status.Draft && (
          <FormField
            control={form.control}
            name="publishAt"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('create.form.publishAt.label')}</FormLabel>
                <FormControl>
                  <Input type="datetime-local" {...field} />
                </FormControl>
                <FormDescription>{tCommon('form.publishAtHint')}</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        {/* Route Selection */}
        <FormField
          control={form.control}
          name="routeSlug"
          render={() => (
            <FormItem>
              <div className="flex items-center justify-between">
                <FormLabel>{t('create.form.route.label')}</FormLabel>
                <div className="flex gap-2">
                  <Button
                    type="button"
                    variant="link"
                    size="sm"
                    className="h-auto p-0"
                    onClick={() => {
                      setPickerTarget('trip')
                      setShowRoutePickerModal(true)
                    }}
                  >
                    {routeSlug ? tCommon('actions.edit') : t('create.form.route.select')}
                  </Button>
                  {routeSlug && (
                    <Button
                      type="button"
                      variant="link"
                      size="sm"
                      className="h-auto p-0 text-destructive"
                      onClick={() => form.setValue('routeSlug', null)}
                    >
                      {t('create.form.route.clear')}
                    </Button>
                  )}
                </div>
              </div>
              {routeSlug ? (
                <RoutePreview routeSlug={routeSlug} teamSlug={teamSlug} />
              ) : (
                <p className="text-sm text-muted-foreground italic">{tCommon('noRouteSelected')}</p>
              )}
              <FormDescription>{t('create.form.route.hint')}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Stages */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <Label>{t('detail.stages.title')}</Label>
            <Button
              type="button"
              variant="link"
              size="sm"
              className="h-auto p-0"
              onClick={handleAddStage}
            >
              <PlusIcon className="size-4 mr-1" />
              {t('create.form.stages.add')}
            </Button>
          </div>
          <div className="space-y-4">
            {fields.map((field, index) => {
              const stage = stages?.[index]
              if (!stage) return null

              return (
                <div
                  key={field.id}
                  className={`border rounded-lg p-4 ${
                    stage.isDeleted
                      ? 'border-destructive/30 bg-destructive/5'
                      : stage.isNew
                        ? 'border-green-200 bg-green-50'
                        : 'border-border'
                  }`}
                >
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-2">
                      <ReorderControls
                        index={index}
                        total={fields.length}
                        onMove={(dir) => handleMoveStage(index, dir)}
                        disabled={stage.isDeleted}
                      />
                      <span className="text-sm font-medium">
                        {stage.isNew
                          ? t('create.form.stages.new')
                          : stage.name ||
                            t('create.form.stages.defaultName', { number: index + 1 })}
                        {stage.isDeleted && (
                          <span className="ml-2 text-destructive">{tCommon('willBeDeleted')}</span>
                        )}
                      </span>
                    </div>
                    <div className="flex items-center gap-1">
                      {stage.isDeleted ? (
                        <Button
                          type="button"
                          variant="link"
                          size="sm"
                          className="h-auto p-0"
                          onClick={() => handleRestoreStage(index)}
                        >
                          {t('create.form.stages.restore')}
                        </Button>
                      ) : (
                        <Button
                          type="button"
                          variant="link"
                          size="sm"
                          className="h-auto p-0 text-destructive"
                          onClick={() => handleRemoveStage(index)}
                        >
                          {t('create.form.stages.remove')}
                        </Button>
                      )}
                    </div>
                  </div>
                  {!stage.isDeleted && (
                    <>
                      {/* Stage Name and DateTime */}
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3">
                        <FormField
                          control={form.control}
                          name={`stages.${index}.name`}
                          render={({ field }) => (
                            <FormItem>
                              <FormControl>
                                <Input
                                  placeholder={t('create.form.stages.name.placeholder')}
                                  {...field}
                                />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                        <FormField
                          control={form.control}
                          name={`stages.${index}.dateTime`}
                          render={({ field }) => (
                            <FormItem>
                              <FormControl>
                                <Input type="datetime-local" {...field} />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                      </div>

                      {/* Stage Places */}
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3">
                        <div className="space-y-1">
                          <span className="text-xs font-medium text-muted-foreground">
                            {t('create.form.stages.startPlace.label')}
                          </span>
                          {stage.startPlace ? (
                            <div className="flex items-center justify-between px-3 py-2 border rounded-md bg-muted">
                              <span className="text-sm truncate">{stage.startPlace.name}</span>
                              <Button
                                type="button"
                                variant="ghost"
                                size="icon"
                                className="size-6"
                                onClick={() =>
                                  form.setValue(`stages.${index}.startPlace`, undefined)
                                }
                              >
                                <XMarkIcon className="size-4" />
                              </Button>
                            </div>
                          ) : (
                            <PlaceAutocomplete
                              teamSlug={teamSlug}
                              onSelect={(place) =>
                                form.setValue(`stages.${index}.startPlace`, place)
                              }
                              filterStart={true}
                              placeholder={t('create.form.stages.startPlace.placeholder')}
                            />
                          )}
                        </div>
                        <div className="space-y-1">
                          <span className="text-xs font-medium text-muted-foreground">
                            {t('create.form.stages.endPlace.label')}
                          </span>
                          {stage.endPlace ? (
                            <div className="flex items-center justify-between px-3 py-2 border rounded-md bg-muted">
                              <span className="text-sm truncate">{stage.endPlace.name}</span>
                              <Button
                                type="button"
                                variant="ghost"
                                size="icon"
                                className="size-6"
                                onClick={() => form.setValue(`stages.${index}.endPlace`, undefined)}
                              >
                                <XMarkIcon className="size-4" />
                              </Button>
                            </div>
                          ) : (
                            <PlaceAutocomplete
                              teamSlug={teamSlug}
                              onSelect={(place) => form.setValue(`stages.${index}.endPlace`, place)}
                              filterEnd={true}
                              placeholder={t('create.form.stages.endPlace.placeholder')}
                            />
                          )}
                        </div>
                      </div>

                      {/* Stage Description */}
                      <FormField
                        control={form.control}
                        name={`stages.${index}.media`}
                        render={({ field }) => (
                          <FormItem className="mb-3">
                            <span className="text-xs font-medium text-muted-foreground">
                              {tCommon('form.description')}
                            </span>
                            <FormControl>
                              <MediaEditor
                                value={field.value}
                                onChange={field.onChange}
                                placeholder={t('create.form.stages.description.placeholder')}
                                minHeight="80px"
                                maxHeight="150px"
                                disabled={isPending}
                                ariaLabel={tCommon('form.description')}
                                teamSlug={teamSlug}
                              />
                            </FormControl>
                          </FormItem>
                        )}
                      />

                      {/* Stage Route */}
                      <div className="pt-3 border-t">
                        <div className="flex items-center justify-between mb-1">
                          <span className="text-xs font-medium text-muted-foreground">
                            {t('create.form.stages.route.label')}
                          </span>
                          <div className="flex gap-2">
                            <Button
                              type="button"
                              variant="link"
                              size="sm"
                              className="h-auto p-0 text-xs"
                              onClick={() => {
                                setPickerTarget({ type: 'stage', index })
                                setShowRoutePickerModal(true)
                              }}
                            >
                              {stage.routeSlug
                                ? tCommon('actions.edit')
                                : t('create.form.route.select')}
                            </Button>
                            {stage.routeSlug && (
                              <Button
                                type="button"
                                variant="link"
                                size="sm"
                                className="h-auto p-0 text-xs text-destructive"
                                onClick={() =>
                                  form.setValue(`stages.${index}.routeSlug`, undefined)
                                }
                              >
                                {t('create.form.route.clear')}
                              </Button>
                            )}
                          </div>
                        </div>
                        {stage.routeSlug ? (
                          <RoutePreviewCompact routeSlug={stage.routeSlug} teamSlug={teamSlug} />
                        ) : (
                          <p className="text-xs text-muted-foreground italic">
                            {tCommon('noRouteSelected')}
                          </p>
                        )}
                      </div>
                    </>
                  )}
                </div>
              )
            })}
            {fields.length === 0 && (
              <p className="text-sm text-muted-foreground italic">
                {t('create.form.stages.empty')}
              </p>
            )}
          </div>
          <p className="text-sm text-muted-foreground">{t('create.form.stages.hint')}</p>
        </div>

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            {cancelButtonText || tCommon('actions.cancelAction')}
          </Button>
          <Button type="submit" disabled={isPending || !name?.trim() || !dateTime}>
            {isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {tCommon('status.saving')}
              </>
            ) : (
              submitButtonText || tCommon('actions.save')
            )}
          </Button>
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
              form.setValue('routeSlug', route ? route.slug : null)
            } else if (pickerTarget && typeof pickerTarget === 'object') {
              form.setValue(
                `stages.${pickerTarget.index}.routeSlug`,
                route ? route.slug : undefined
              )
            }
            setShowRoutePickerModal(false)
            setPickerTarget(null)
          }}
          teamSlug={teamSlug}
          selectedRouteSlug={
            pickerTarget === 'trip'
              ? routeSlug
              : pickerTarget && typeof pickerTarget === 'object'
                ? stages?.[pickerTarget.index]?.routeSlug
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
              form.setValue('routeSlug', route.slug)
            } else if (pickerTarget && typeof pickerTarget === 'object') {
              form.setValue(`stages.${pickerTarget.index}.routeSlug`, route.slug)
            }
            setShowCreateRouteModal(false)
            setPickerTarget(null)
          }}
          team={team}
        />
      </form>
    </Form>
  )
}
