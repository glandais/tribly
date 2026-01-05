import { useState, useEffect } from 'react'
import { useForm, useFieldArray, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import { XMarkIcon } from '@heroicons/react/24/outline'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ReorderControls } from '../common/ReorderControls'
import { RoutePickerModal } from '../route/RoutePickerModal'
import { CreateRouteModal } from '../route/CreateRouteModal'
import { RoutePreview } from '../route/RoutePreview'
import { RoutePreviewCompact } from '../route/RoutePreviewCompact'
import { MediaEditor } from '../common/MediaEditor'
import { PlaceAutocomplete } from '../common/PlaceAutocomplete'
import type { RouteDto, TeamDetailDto, RideRequest } from '@/api/dto'
import { Status } from '@/api/dto'
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
import { createRideBody } from '@/api/zod/rides/rides.zod'
import { InputDateTime } from '../ui/input-datetime'

const rideSchema = createRideBody.refine(
  (data) => {
    if (data.status === Status.DRAFT && data.publishAt) {
      return new Date(data.publishAt) > new Date()
    }
    return true
  },
  {
    message: 'Publish date must be in the future for draft posts',
    path: ['publishAt'],
  }
)

type Target =
  | {
      type: 'group'
      index: number
    }
  | {
      type: 'ride'
    }

interface RideEditorProps {
  // Context
  team: TeamDetailDto
  teamSlug: string

  // Initial values (REQUIRED - each page prepares these)
  initialValues: RideRequest

  // Submission
  onSubmit: (data: RideRequest) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean

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
  submitButtonText,
  cancelButtonText,
}: RideEditorProps) {
  const { t } = useTranslation()

  // Modal state
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<Target | null>(null)

  const form = useForm<RideRequest>({
    resolver: zodResolver(rideSchema),
    mode: 'onChange',
    defaultValues: initialValues,
  })

  const {
    fields: groupFieldArray,
    append,
    remove,
    move,
  } = useFieldArray({
    control: form.control,
    name: 'groups',
  })

  const status = useWatch({ control: form.control, name: 'status' })
  const groups = useWatch({ control: form.control, name: 'groups' })
  const routeSlug = useWatch({ control: form.control, name: 'routeSlug' })

  useEffect(() => {
    form.trigger('publishAt')
  }, [status, form])

  const handleAddGroup = () => {
    append({
      name: `Groupe ${groups.length + 1}`,
    })
  }

  const handleRemoveGroup = (index: number) => {
    remove(index)
  }

  const handleMoveGroup = (index: number, direction: 'up' | 'down') => {
    const newIndex = direction === 'up' ? index - 1 : index + 1
    if (newIndex >= 0 && newIndex < groupFieldArray.length) {
      move(index, newIndex)
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        {/* Title */}
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>
                {t('rides.create.form.title.label')} <span className="text-destructive">*</span>
              </FormLabel>
              <FormControl>
                <Input placeholder={t('rides.create.form.title.placeholder')} {...field} />
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
              <FormLabel>{t('form.description')}</FormLabel>
              <FormControl>
                <MediaEditor
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={t('rides.create.form.description.placeholder')}
                  minHeight="150px"
                  maxHeight="300px"
                  disabled={isPending}
                  ariaLabel={t('form.description')}
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
                  {t('startPlace')} <span className="text-destructive">*</span>
                </FormLabel>
                <FormControl>
                  <InputDateTime {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Start and End Places */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label>{t('rides.create.form.startPlace.label')}</Label>
            <PlaceAutocomplete
              teamSlug={teamSlug}
              value={form.getValues('startPlaceId')}
              onChange={(placeId) => form.setValue(`startPlaceId`, placeId)}
              filterStart={true}
              placeholder={t('rides.create.form.startPlace.placeholder')}
            />
          </div>
          <div className="space-y-2">
            <Label>{t('rides.create.form.endPlace.label')}</Label>
            <PlaceAutocomplete
              teamSlug={teamSlug}
              value={form.getValues('endPlaceId')}
              onChange={(placeId) => form.setValue(`endPlaceId`, placeId)}
              filterEnd={true}
              placeholder={t('rides.create.form.endPlace.placeholder')}
            />
          </div>
        </div>

        {/* Visibility */}
        {team.visibility !== 'TEAM' && (
          <FormField
            control={form.control}
            name="visibility"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('visibility.label')}</FormLabel>
                <FormControl>
                  <RadioGroup
                    value={field.value}
                    onValueChange={field.onChange}
                    className="space-y-2"
                  >
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="TEAM" id="visibility-team" />
                      <Label htmlFor="visibility-team">{t('visibility.team')}</Label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="PUBLIC" id="visibility-public" />
                      <Label htmlFor="visibility-public">{t('visibility.public')}</Label>
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
              <FormLabel>{t('form.status')}</FormLabel>
              <FormControl>
                <RadioGroup
                  value={field.value}
                  onValueChange={field.onChange}
                  className="space-y-2"
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="DRAFT" id="status-draft" />
                    <Label htmlFor="status-draft">{t('status.DRAFT')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="PUBLISHED" id="status-published" />
                    <Label htmlFor="status-published">{t('status.PUBLISHED')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="CANCELLED" id="status-cancelled" />
                    <Label htmlFor="status-cancelled">{t('status.CANCELLED')}</Label>
                  </div>
                </RadioGroup>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Scheduled Publication (only shown when status === DRAFT) */}
        {status === Status.DRAFT && (
          <FormField
            control={form.control}
            name="publishAt"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('rides.create.form.publishAt.label')}</FormLabel>
                <FormControl>
                  <InputDateTime {...field} />
                </FormControl>
                <FormDescription>{t('form.publishAtHint')}</FormDescription>
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
                <FormLabel>{t('rides.create.form.route.label')}</FormLabel>
                <div className="flex gap-2">
                  <Button
                    type="button"
                    variant="link"
                    size="sm"
                    className="h-auto p-0"
                    onClick={() => {
                      setPickerTarget({ type: 'ride' })
                      setShowRoutePickerModal(true)
                    }}
                  >
                    {routeSlug ? t('actions.edit') : t('rides.create.form.route.select')}
                  </Button>
                  {routeSlug && (
                    <Button
                      type="button"
                      variant="link"
                      size="sm"
                      className="h-auto p-0 text-destructive"
                      onClick={() => form.setValue('routeSlug', undefined)}
                    >
                      {t('rides.create.form.route.clear')}
                    </Button>
                  )}
                </div>
              </div>
              {routeSlug ? (
                <RoutePreview routeSlug={routeSlug} teamSlug={teamSlug} />
              ) : (
                <p className="text-sm text-muted-foreground italic">{t('noRouteSelected')}</p>
              )}
              <FormDescription>{t('rides.create.form.route.hint')}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Groups */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <Label>{t('rides.create.form.groups.label')}</Label>
            <Button
              type="button"
              variant="link"
              size="sm"
              className="h-auto p-0"
              onClick={handleAddGroup}
            >
              {t('groups.add')}
            </Button>
          </div>
          <div className="space-y-3">
            {groupFieldArray.map((field, index) => {
              const group = groups?.[index]
              if (!group) return null

              return (
                <div key={field.id} className={`border rounded-lg p-4 border-border`}>
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-2">
                      <ReorderControls
                        index={index}
                        total={groupFieldArray.length}
                        onMove={(dir) => handleMoveGroup(index, dir)}
                      />
                      <span className="text-sm font-medium">
                        {group.name ||
                          t('rides.create.form.groups.defaultName', { number: index + 1 })}
                      </span>
                    </div>
                    <Button
                      type="button"
                      variant="link"
                      size="sm"
                      className="h-auto p-0 text-destructive"
                      onClick={() => handleRemoveGroup(index)}
                    >
                      {t('rides.create.form.groups.remove')}
                    </Button>
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-4 gap-3">
                    <FormField
                      control={form.control}
                      name={`groups.${index}.name`}
                      render={({ field }) => (
                        <FormItem>
                          <FormControl>
                            <Input
                              placeholder={t('rides.create.form.groups.name.placeholder')}
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
                              placeholder={t('rides.create.form.groups.speed.placeholder')}
                              min={0}
                              {...field}
                              value={field.value ?? ''}
                              onChange={(e) =>
                                field.onChange(e.target.value ? Number(e.target.value) : undefined)
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
                              placeholder={t(
                                'rides.create.form.groups.maxParticipants.placeholder'
                              )}
                              min={1}
                              {...field}
                              value={field.value ?? ''}
                              onChange={(e) =>
                                field.onChange(e.target.value ? Number(e.target.value) : undefined)
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
                        {t('rides.create.form.groups.route.label')}
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
                            ? t('actions.edit')
                            : t('rides.create.form.route.select')}
                        </Button>
                        {group.routeSlug && (
                          <Button
                            type="button"
                            variant="link"
                            size="sm"
                            className="h-auto p-0 text-xs text-destructive"
                            onClick={() => form.setValue(`groups.${index}.routeSlug`, undefined)}
                          >
                            {t('rides.create.form.route.clear')}
                          </Button>
                        )}
                      </div>
                    </div>
                    {group.routeSlug ? (
                      <RoutePreviewCompact routeSlug={group.routeSlug} teamSlug={teamSlug} />
                    ) : (
                      <p className="text-xs text-muted-foreground italic">{t('noRouteSelected')}</p>
                    )}
                  </div>
                </div>
              )
            })}
            {groupFieldArray.length === 0 && (
              <p className="text-sm text-muted-foreground italic">
                {t('rides.create.form.groups.empty')}
              </p>
            )}
          </div>
          <p className="text-sm text-muted-foreground">{t('rides.create.form.groups.hint')}</p>
        </div>

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            {cancelButtonText || t('actions.cancelAction')}
          </Button>
          <Button type="submit" disabled={isPending || !form.formState.isValid}>
            {isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {t('status.saving')}
              </>
            ) : (
              submitButtonText || t('actions.save')
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
            if (pickerTarget) {
              if (pickerTarget.type === 'ride') {
                form.setValue('routeSlug', route ? route.slug : undefined)
              } else if (pickerTarget.type === 'group') {
                form.setValue(
                  `groups.${pickerTarget.index}.routeSlug`,
                  route ? route.slug : undefined
                )
              }
            }
            setShowRoutePickerModal(false)
            setPickerTarget(null)
          }}
          teamSlug={teamSlug}
          selectedRouteSlug={
            pickerTarget && pickerTarget.type === 'ride'
              ? routeSlug
              : pickerTarget && pickerTarget.type === 'group'
                ? groups?.[pickerTarget.index]?.routeSlug
                : null
          }
          title={
            pickerTarget && pickerTarget.type === 'ride'
              ? t('rides.create.form.route.selectForRide')
              : t('rides.create.form.route.selectForGroup')
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
            if (pickerTarget) {
              if (pickerTarget.type === 'ride') {
                form.setValue('routeSlug', route.slug)
              } else if (pickerTarget.type === 'group') {
                form.setValue(`groups.${pickerTarget.index}.routeSlug`, route.slug)
              }
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
