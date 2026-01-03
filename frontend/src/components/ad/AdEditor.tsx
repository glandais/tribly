import { useEffect } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import type { MediaDto, TeamDetailDto } from '../../api/api'
import { AdType, RentalPeriod, Status, Visibility } from '../../api/api'
import { ApiClientError } from '../../lib/apiClient'
import { fromDateTimeLocalValue } from '../../utils/dateFormat'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

export interface AdFormData {
  name: string
  media: MediaDto
  dateTime: string // ISO string
  visibility: Visibility
  status: Status
  adType: AdType
  price?: number
  rentalPeriod?: RentalPeriod
  latitude?: number
  longitude?: number
  locationDescription?: string
}

const adSchema = z
  .object({
    name: z.string().min(3).max(200),
    media: z.custom<MediaDto>(),
    dateTime: z.string(),
    visibility: z.nativeEnum(Visibility),
    status: z.nativeEnum(Status),
    adType: z.nativeEnum(AdType),
    price: z.number().min(0).optional(),
    rentalPeriod: z.nativeEnum(RentalPeriod).optional(),
    latitude: z.number().min(-90).max(90).optional(),
    longitude: z.number().min(-180).max(180).optional(),
    locationDescription: z.string().max(255).optional(),
  })
  .refine(
    (data) => {
      if (data.adType === AdType.Rental) {
        return data.rentalPeriod !== undefined
      }
      return true
    },
    {
      message: 'Rental period is required for rental ads',
      path: ['rentalPeriod'],
    }
  )

type AdFormValues = z.infer<typeof adSchema>

interface AdEditorProps {
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
    adType: AdType
    price?: number | null
    rentalPeriod?: RentalPeriod | null
    latitude?: number | null
    longitude?: number | null
    locationDescription?: string
  }

  // Submission
  onSubmit: (data: AdFormData) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean
  error?: Error | ApiClientError | null

  // UI customization
  submitButtonText?: string
  cancelButtonText?: string
}

export function AdEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error,
  submitButtonText,
  cancelButtonText,
}: AdEditorProps) {
  const { t } = useTranslation('ads')
  const { t: tCommon } = useTranslation('common')

  const form = useForm<AdFormValues>({
    resolver: zodResolver(adSchema),
    defaultValues: {
      name: initialValues.name,
      media: initialValues.media ?? defaultMedia(),
      dateTime: initialValues.dateTime,
      visibility: initialValues.visibility,
      status: initialValues.status,
      adType: initialValues.adType,
      price: initialValues.price ?? undefined,
      rentalPeriod: initialValues.rentalPeriod ?? undefined,
      latitude: initialValues.latitude ?? undefined,
      longitude: initialValues.longitude ?? undefined,
      locationDescription: initialValues.locationDescription || '',
    },
  })

  const adType = useWatch({ control: form.control, name: 'adType' })
  const name = useWatch({ control: form.control, name: 'name' })

  // Sync form values when initial props change (for edit mode)
  useEffect(() => {
    form.setValue('name', initialValues.name)
    if (initialValues.media?.markdown || initialValues.media?.assets?.images?.length) {
      form.setValue('media', initialValues.media)
    }
    form.setValue('dateTime', initialValues.dateTime)
    form.setValue('visibility', initialValues.visibility)
    form.setValue('status', initialValues.status)
    form.setValue('adType', initialValues.adType)
    form.setValue('price', initialValues.price ?? undefined)
    form.setValue('rentalPeriod', initialValues.rentalPeriod ?? undefined)
    form.setValue('latitude', initialValues.latitude ?? undefined)
    form.setValue('longitude', initialValues.longitude ?? undefined)
    form.setValue('locationDescription', initialValues.locationDescription || '')
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    initialValues.name,
    initialValues.media?.markdown,
    initialValues.media?.assets?.images?.length,
    initialValues.dateTime,
    initialValues.visibility,
    initialValues.status,
    initialValues.adType,
    initialValues.price,
    initialValues.rentalPeriod,
    initialValues.latitude,
    initialValues.longitude,
    initialValues.locationDescription,
  ])

  // Set server-side errors on form fields
  useEffect(() => {
    if (error instanceof ApiClientError && error.error.errors) {
      error.error.errors.forEach((err) => {
        if (err.field && err.field in adSchema.shape) {
          form.setError(err.field as keyof AdFormValues, { message: err.message })
        }
      })
    }
  }, [error, form])

  const handleSubmit = (values: AdFormValues) => {
    onSubmit({
      name: values.name,
      media: values.media,
      dateTime: fromDateTimeLocalValue(values.dateTime).toISOString(),
      status: values.status,
      visibility: values.visibility,
      adType: values.adType,
      price: values.price ?? undefined,
      rentalPeriod:
        values.adType === AdType.Rental ? (values.rentalPeriod ?? undefined) : undefined,
      latitude: values.latitude ?? undefined,
      longitude: values.longitude ?? undefined,
      locationDescription: values.locationDescription || undefined,
    })
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
        {error && !(error instanceof ApiClientError && error.error.errors) && (
          <div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg">
            <p className="text-destructive">
              {error instanceof ApiClientError ? error.error.message : t('edit.error')}
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
                {tCommon('form.title')} <span className="text-destructive">*</span>
              </FormLabel>
              <FormControl>
                <Input placeholder={t('create.namePlaceholder')} {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Ad Type */}
        <FormField
          control={form.control}
          name="adType"
          render={({ field }) => (
            <FormItem>
              <FormLabel>
                {t('create.adTypeLabel')} <span className="text-destructive">*</span>
              </FormLabel>
              <FormControl>
                <RadioGroup
                  value={field.value}
                  onValueChange={field.onChange}
                  className="flex gap-4"
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AdType.Sale} id="adType-sale" />
                    <Label htmlFor="adType-sale">{t('adType.SALE')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AdType.Rental} id="adType-rental" />
                    <Label htmlFor="adType-rental">{t('adType.RENTAL')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AdType.Wanted} id="adType-wanted" />
                    <Label htmlFor="adType-wanted">{t('adType.WANTED')}</Label>
                  </div>
                </RadioGroup>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Price */}
        <FormField
          control={form.control}
          name="price"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{t('create.priceLabel')}</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder={t('create.pricePlaceholder')}
                  {...field}
                  value={field.value ?? ''}
                  onChange={(e) =>
                    field.onChange(e.target.value ? Number(e.target.value) : undefined)
                  }
                />
              </FormControl>
              <FormDescription>{t('create.priceHint')}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Rental Period - only shown for RENTAL type */}
        {adType === AdType.Rental && (
          <FormField
            control={form.control}
            name="rentalPeriod"
            render={({ field }) => (
              <FormItem>
                <FormLabel>
                  {t('create.rentalPeriodLabel')} <span className="text-destructive">*</span>
                </FormLabel>
                <Select value={field.value ?? ''} onValueChange={field.onChange}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder={t('create.rentalPeriodLabel')} />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value={RentalPeriod.Day}>{t('rentalPeriod.DAY')}</SelectItem>
                    <SelectItem value={RentalPeriod.Week}>{t('rentalPeriod.WEEK')}</SelectItem>
                    <SelectItem value={RentalPeriod.Month}>{t('rentalPeriod.MONTH')}</SelectItem>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

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
                  placeholder={t('create.descriptionPlaceholder')}
                  minHeight="200px"
                  maxHeight="400px"
                  disabled={isPending}
                  ariaLabel={tCommon('form.description')}
                  teamSlug={teamSlug}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Location Description */}
        <FormField
          control={form.control}
          name="locationDescription"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{t('create.locationDescriptionLabel')}</FormLabel>
              <FormControl>
                <Input placeholder={t('create.locationDescriptionPlaceholder')} {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Coordinates (optional) */}
        <div className="grid grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="latitude"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{tCommon('form.latitude')}</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    step="any"
                    placeholder="48.8566"
                    {...field}
                    value={field.value ?? ''}
                    onChange={(e) =>
                      field.onChange(e.target.value ? Number(e.target.value) : undefined)
                    }
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="longitude"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{tCommon('form.longitude')}</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    step="any"
                    placeholder="2.3522"
                    {...field}
                    value={field.value ?? ''}
                    onChange={(e) =>
                      field.onChange(e.target.value ? Number(e.target.value) : undefined)
                    }
                  />
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
                </RadioGroup>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Date and Time */}
        <FormField
          control={form.control}
          name="dateTime"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{t('create.dateTimeLabel')}</FormLabel>
              <FormControl>
                <Input type="datetime-local" {...field} />
              </FormControl>
              <FormDescription>{t('create.dateTimeHint')}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            {cancelButtonText || tCommon('actions.cancelAction')}
          </Button>
          <Button type="submit" disabled={isPending || !name.trim()}>
            {isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {tCommon('loading')}
              </>
            ) : (
              submitButtonText || tCommon('actions.save')
            )}
          </Button>
        </div>
      </form>
    </Form>
  )
}
