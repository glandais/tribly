import { useState, useEffect } from 'react'
import { useForm, useFieldArray, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import { PlusIcon } from '@heroicons/react/24/outline'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ReorderControls } from '../common/ReorderControls'
import { RoutePickerModal } from '../route/RoutePickerModal'
import { CreateRouteModal } from '../route/CreateRouteModal'
import { RoutePreview } from '../route/RoutePreview'
import { RoutePreviewCompact } from '../route/RoutePreviewCompact'
import { MediaEditor } from '../common/MediaEditor'
import { PlaceAutocomplete } from '../common/PlaceAutocomplete'
import type { RouteDto, TeamDetailDto, TripRequest } from '@/api/dto'
import { Status } from '@/api/dto'
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
import { createTripBody } from '@/api/zod/trips/trips.zod'
import { InputDateTime } from '../ui/input-datetime'

const tripSchema = createTripBody.refine(
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

interface TripEditorProps {
  // Context
  team: TeamDetailDto
  teamSlug: string

  // Initial values (REQUIRED - each page prepares these)
  initialValues: TripRequest

  // Submission
  onSubmit: (data: TripRequest) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean

  // UI customization
  submitButtonText?: string
  cancelButtonText?: string
}

type Target =
  | {
      type: 'stage'
      index: number
    }
  | {
      type: 'trip'
    }

export function TripEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  submitButtonText,
  cancelButtonText,
}: TripEditorProps) {
  const { t } = useTranslation('trips')
  const { t: tCommon } = useTranslation('common')

  // Modal state
  const [showRoutePickerModal, setShowRoutePickerModal] = useState(false)
  const [showCreateRouteModal, setShowCreateRouteModal] = useState(false)
  const [pickerTarget, setPickerTarget] = useState<Target | null>(null)

  const form = useForm<TripRequest>({
    resolver: zodResolver(tripSchema),
    mode: 'onChange',
    defaultValues: initialValues,
  })

  const {
    fields: stageFieldArray,
    append,
    remove,
    move,
  } = useFieldArray({
    control: form.control,
    name: 'stages',
  })

  const status = useWatch({ control: form.control, name: 'status' })
  const dateTime = useWatch({ control: form.control, name: 'dateTime' })
  const stages = useWatch({ control: form.control, name: 'stages' })
  const routeSlug = useWatch({ control: form.control, name: 'routeSlug' })

  useEffect(() => {
    form.trigger('publishAt')
  }, [status, form])

  const handleAddStage = () => {
    // Default to trip date/time for new stage
    const newStageDate = new Date(dateTime || new Date().toISOString())
    newStageDate.setDate(newStageDate.getDate() + stages.length)

    append({
      name: `Jour ${stages.length + 1}`,
      dateTime: newStageDate.toISOString(),
      media: defaultMedia(),
    })
  }

  const handleRemoveStage = (index: number) => {
    remove(index)
  }

  const handleMoveStage = (index: number, direction: 'up' | 'down') => {
    const newIndex = direction === 'up' ? index - 1 : index + 1
    if (newIndex >= 0 && newIndex < stageFieldArray.length) {
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
                  <InputDateTime {...field} />
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
        {status === Status.DRAFT && (
          <FormField
            control={form.control}
            name="publishAt"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('create.form.publishAt.label')}</FormLabel>
                <FormControl>
                  <InputDateTime {...field} />
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
                      setPickerTarget({ type: 'trip' })
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
                      onClick={() => form.setValue('routeSlug', undefined)}
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
            {stageFieldArray.map((field, index) => {
              const stage = stages?.[index]
              if (!stage) return null

              return (
                <div key={field.id} className={`border rounded-lg p-4 border-border`}>
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-2">
                      <ReorderControls
                        index={index}
                        total={stageFieldArray.length}
                        onMove={(dir) => handleMoveStage(index, dir)}
                      />
                      <span className="text-sm font-medium">
                        {stage.name || t('create.form.stages.defaultName', { number: index + 1 })}
                      </span>
                    </div>
                    <div className="flex items-center gap-1">
                      <Button
                        type="button"
                        variant="link"
                        size="sm"
                        className="h-auto p-0 text-destructive"
                        onClick={() => handleRemoveStage(index)}
                      >
                        {t('create.form.stages.remove')}
                      </Button>
                    </div>
                  </div>
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
                            <InputDateTime {...field} />
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
                      <PlaceAutocomplete
                        teamSlug={teamSlug}
                        value={stage.startPlaceId}
                        onChange={(placeId) =>
                          form.setValue(`stages.${index}.startPlaceId`, placeId)
                        }
                        filterStart={true}
                        placeholder={t('create.form.stages.startPlace.placeholder')}
                      />
                    </div>
                    <div className="space-y-1">
                      <span className="text-xs font-medium text-muted-foreground">
                        {t('create.form.stages.endPlace.label')}
                      </span>
                      <PlaceAutocomplete
                        teamSlug={teamSlug}
                        value={stage.endPlaceId}
                        onChange={(placeId) => form.setValue(`stages.${index}.endPlaceId`, placeId)}
                        filterEnd={true}
                        placeholder={t('create.form.stages.endPlace.placeholder')}
                      />
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
                            onClick={() => form.setValue(`stages.${index}.routeSlug`, undefined)}
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
                </div>
              )
            })}
            {stageFieldArray.length === 0 && (
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
          <Button type="submit" disabled={isPending || !form.formState.isValid}>
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
            if (pickerTarget) {
              if (pickerTarget.type === 'trip') {
                form.setValue('routeSlug', route ? route.slug : undefined)
              } else if (pickerTarget.type === 'stage') {
                form.setValue(
                  `stages.${pickerTarget.index}.routeSlug`,
                  route ? route.slug : undefined
                )
              }
            }
            setShowRoutePickerModal(false)
            setPickerTarget(null)
          }}
          teamSlug={teamSlug}
          selectedRouteSlug={
            pickerTarget && pickerTarget.type === 'trip'
              ? routeSlug
              : pickerTarget && pickerTarget.type === 'stage'
                ? stages?.[pickerTarget.index]?.routeSlug
                : null
          }
          title={
            pickerTarget && pickerTarget.type === 'trip'
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
            if (pickerTarget) {
              if (pickerTarget.type === 'trip') {
                form.setValue('routeSlug', route.slug)
              } else if (pickerTarget.type === 'stage') {
                form.setValue(`stages.${pickerTarget.index}.routeSlug`, route.slug)
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
