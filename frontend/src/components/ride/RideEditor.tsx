import { useState, useEffect } from 'react'
import { useForm, useFieldArray, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import { XMarkIcon } from '@heroicons/react/24/outline'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ReorderControls } from '../common/ReorderControls'
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

const groupSchema = z.object({
  id: z.string().optional(),
  name: z.string(),
  time: z.string().optional(),
  averageSpeed: z.number().optional(),
  maxParticipants: z.number().optional(),
  routeSlug: z.string().optional(),
  isNew: z.boolean().optional(),
  isDeleted: z.boolean().optional(),
})

const rideSchema = z.object({
  name: z.string().min(3).max(200),
  media: z.custom<MediaDto>(),
  dateTime: z.string().min(1),
  visibility: z.nativeEnum(Visibility),
  status: z.nativeEnum(Status),
  publishAt: z.string().optional(),
  routeSlug: z.string().nullable().optional(),
  groups: z.array(groupSchema),
})

type RideFormValues = z.infer<typeof rideSchema>

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

  // Places are handled separately (PlaceAutocomplete doesn't fit FormField pattern)
  const [startPlace, setStartPlace] = useState<PlaceDetailDto | null>(
    initialValues.startPlace || null
  )
  const [endPlace, setEndPlace] = useState<PlaceDetailDto | null>(initialValues.endPlace || null)

  // Modal state
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<
    'ride' | { type: 'group'; index: number } | null
  >(null)

  const form = useForm<RideFormValues>({
    resolver: zodResolver(rideSchema),
    defaultValues: {
      name: initialValues.name,
      media: initialValues.media,
      dateTime: initialValues.dateTime,
      visibility: initialValues.visibility,
      status: initialValues.status,
      publishAt: initialValues.publishAt || '',
      routeSlug: initialValues.routeSlug || null,
      groups: initialValues.groups,
    },
  })

  const { fields, append, remove, update, move } = useFieldArray({
    control: form.control,
    name: 'groups',
  })

  const status = useWatch({ control: form.control, name: 'status' })
  const name = useWatch({ control: form.control, name: 'name' })
  const dateTime = useWatch({ control: form.control, name: 'dateTime' })
  const groups = useWatch({ control: form.control, name: 'groups' })
  const routeSlug = useWatch({ control: form.control, name: 'routeSlug' })

  // Set server-side errors on form fields
  useEffect(() => {
    if (error instanceof ApiClientError && error.error.errors) {
      error.error.errors.forEach((err) => {
        if (err.field && err.field in rideSchema.shape) {
          form.setError(err.field as keyof RideFormValues, { message: err.message })
        }
      })
    }
  }, [error, form])

  const handleFormSubmit = (values: RideFormValues) => {
    const filteredGroups = values.groups.filter((g) => !g.isDeleted && g.name.trim())

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
      startPlaceId: startPlace?.id,
      endPlaceId: endPlace?.id,
      groups: filteredGroups,
    })
  }

  const handleAddGroup = () => {
    append({
      name: '',
      time: undefined,
      averageSpeed: undefined,
      maxParticipants: undefined,
      routeSlug: undefined,
      isNew: true,
    })
  }

  const handleRemoveGroup = (index: number) => {
    const group = fields[index]
    if (group.isNew) {
      remove(index)
    } else {
      update(index, { ...groups[index], isDeleted: true })
    }
  }

  const handleRestoreGroup = (index: number) => {
    update(index, { ...groups[index], isDeleted: false })
  }

  const handleMoveGroup = (index: number, direction: 'up' | 'down') => {
    const newIndex = direction === 'up' ? index - 1 : index + 1
    if (newIndex >= 0 && newIndex < fields.length) {
      move(index, newIndex)
    }
  }

  const activeGroups = groups?.filter((g) => !g.isDeleted) || []

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

        {/* Date and Time */}
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

        {/* Start and End Places */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label>{t('create.form.startPlace.label')}</Label>
            {startPlace ? (
              <div className="flex items-center justify-between px-4 py-2 border rounded-lg bg-muted">
                <div className="min-w-0">
                  <p className="text-sm font-medium truncate">{startPlace.name}</p>
                  {startPlace.address && (
                    <p className="text-xs text-muted-foreground truncate">{startPlace.address}</p>
                  )}
                </div>
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="size-8"
                  onClick={() => setStartPlace(null)}
                >
                  <XMarkIcon className="size-4" />
                </Button>
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
          <div className="space-y-2">
            <Label>{t('create.form.endPlace.label')}</Label>
            {endPlace ? (
              <div className="flex items-center justify-between px-4 py-2 border rounded-lg bg-muted">
                <div className="min-w-0">
                  <p className="text-sm font-medium truncate">{endPlace.name}</p>
                  {endPlace.address && (
                    <p className="text-xs text-muted-foreground truncate">{endPlace.address}</p>
                  )}
                </div>
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="size-8"
                  onClick={() => setEndPlace(null)}
                >
                  <XMarkIcon className="size-4" />
                </Button>
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

        {/* Scheduled Publication (only shown when status === DRAFT) */}
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
                      setPickerTarget('ride')
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

        {/* Groups */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <Label>{t('create.form.groups.label')}</Label>
            <Button
              type="button"
              variant="link"
              size="sm"
              className="h-auto p-0"
              onClick={handleAddGroup}
            >
              {tCommon('groups.add')}
            </Button>
          </div>
          <div className="space-y-3">
            {fields.map((field, index) => {
              const group = groups?.[index]
              if (!group) return null

              return (
                <div
                  key={field.id}
                  className={`border rounded-lg p-4 ${
                    group.isDeleted
                      ? 'border-destructive/30 bg-destructive/5'
                      : group.isNew
                        ? 'border-green-200 bg-green-50'
                        : 'border-border'
                  }`}
                >
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-2">
                      <ReorderControls
                        index={index}
                        total={fields.length}
                        onMove={(dir) => handleMoveGroup(index, dir)}
                        disabled={group.isDeleted}
                      />
                      <span className="text-sm font-medium">
                        {group.isNew
                          ? t('create.form.groups.new')
                          : group.name ||
                            t('create.form.groups.defaultName', { number: index + 1 })}
                        {group.isDeleted && (
                          <span className="ml-2 text-destructive">{tCommon('willBeDeleted')}</span>
                        )}
                      </span>
                    </div>
                    {group.isDeleted ? (
                      <Button
                        type="button"
                        variant="link"
                        size="sm"
                        className="h-auto p-0"
                        onClick={() => handleRestoreGroup(index)}
                      >
                        {t('create.form.groups.restore')}
                      </Button>
                    ) : (
                      <Button
                        type="button"
                        variant="link"
                        size="sm"
                        className="h-auto p-0 text-destructive"
                        onClick={() => handleRemoveGroup(index)}
                      >
                        {t('create.form.groups.remove')}
                      </Button>
                    )}
                  </div>
                  {!group.isDeleted && (
                    <>
                      <div className="grid grid-cols-1 sm:grid-cols-4 gap-3">
                        <FormField
                          control={form.control}
                          name={`groups.${index}.name`}
                          render={({ field }) => (
                            <FormItem>
                              <FormControl>
                                <Input
                                  placeholder={t('create.form.groups.name.placeholder')}
                                  {...field}
                                />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                        <div className="relative">
                          <FormField
                            control={form.control}
                            name={`groups.${index}.time`}
                            render={({ field }) => (
                              <FormItem>
                                <FormControl>
                                  <Input
                                    type="time"
                                    {...field}
                                    value={field.value || ''}
                                    onChange={(e) => field.onChange(e.target.value || undefined)}
                                  />
                                </FormControl>
                              </FormItem>
                            )}
                          />
                          {group.time && (
                            <Button
                              type="button"
                              variant="ghost"
                              size="icon"
                              className="absolute right-8 top-1/2 -translate-y-1/2 size-6"
                              onClick={() => form.setValue(`groups.${index}.time`, undefined)}
                            >
                              <XMarkIcon className="size-4" />
                            </Button>
                          )}
                        </div>
                        <FormField
                          control={form.control}
                          name={`groups.${index}.averageSpeed`}
                          render={({ field }) => (
                            <FormItem>
                              <FormControl>
                                <Input
                                  type="number"
                                  placeholder={t('create.form.groups.speed.placeholder')}
                                  min={0}
                                  {...field}
                                  value={field.value ?? ''}
                                  onChange={(e) =>
                                    field.onChange(
                                      e.target.value ? Number(e.target.value) : undefined
                                    )
                                  }
                                />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                        <FormField
                          control={form.control}
                          name={`groups.${index}.maxParticipants`}
                          render={({ field }) => (
                            <FormItem>
                              <FormControl>
                                <Input
                                  type="number"
                                  placeholder={t('create.form.groups.maxParticipants.placeholder')}
                                  min={1}
                                  {...field}
                                  value={field.value ?? ''}
                                  onChange={(e) =>
                                    field.onChange(
                                      e.target.value ? Number(e.target.value) : undefined
                                    )
                                  }
                                />
                              </FormControl>
                            </FormItem>
                          )}
                        />
                      </div>

                      {/* Route Selection for Group */}
                      <div className="mt-3 pt-3 border-t">
                        <div className="flex items-center justify-between mb-1">
                          <span className="text-xs font-medium text-muted-foreground">
                            {t('create.form.groups.route.label')}
                          </span>
                          <div className="flex gap-2">
                            <Button
                              type="button"
                              variant="link"
                              size="sm"
                              className="h-auto p-0 text-xs"
                              onClick={() => {
                                setPickerTarget({ type: 'group', index })
                                setShowRoutePickerModal(true)
                              }}
                            >
                              {group.routeSlug
                                ? tCommon('actions.edit')
                                : t('create.form.route.select')}
                            </Button>
                            {group.routeSlug && (
                              <Button
                                type="button"
                                variant="link"
                                size="sm"
                                className="h-auto p-0 text-xs text-destructive"
                                onClick={() =>
                                  form.setValue(`groups.${index}.routeSlug`, undefined)
                                }
                              >
                                {t('create.form.route.clear')}
                              </Button>
                            )}
                          </div>
                        </div>
                        {group.routeSlug ? (
                          <RoutePreviewCompact routeSlug={group.routeSlug} teamSlug={teamSlug} />
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
                {t('create.form.groups.empty')}
              </p>
            )}
          </div>
          <p className="text-sm text-muted-foreground">{t('create.form.groups.hint')}</p>
        </div>

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            {cancelButtonText || tCommon('actions.cancelAction')}
          </Button>
          <Button
            type="submit"
            disabled={
              isPending ||
              !name?.trim() ||
              !dateTime ||
              activeGroups.filter((g) => g.name.trim()).length === 0
            }
          >
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
            if (pickerTarget === 'ride') {
              form.setValue('routeSlug', route ? route.slug : null)
            } else if (pickerTarget && typeof pickerTarget === 'object') {
              form.setValue(
                `groups.${pickerTarget.index}.routeSlug`,
                route ? route.slug : undefined
              )
            }
            setShowRoutePickerModal(false)
            setPickerTarget(null)
          }}
          teamSlug={teamSlug}
          selectedRouteSlug={
            pickerTarget === 'ride'
              ? routeSlug
              : pickerTarget && typeof pickerTarget === 'object'
                ? groups?.[pickerTarget.index]?.routeSlug
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
              form.setValue('routeSlug', route.slug)
            } else if (pickerTarget && typeof pickerTarget === 'object') {
              form.setValue(`groups.${pickerTarget.index}.routeSlug`, route.slug)
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
