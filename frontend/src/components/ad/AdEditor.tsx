import { useEffect } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import { AdRequest, AdType, RentalPeriod, TeamDetailDto } from '@/api/dto'
import { createAdBody } from '@/api/zod/ads/ads.zod'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
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

// Use generated schema with custom media type for form handling
const adSchema = createAdBody.refine(
  (data) => {
    if (data.adType === AdType.RENTAL) {
      return data.rentalPeriod !== undefined
    }
    return true
  },
  {
    message: 'Rental period is required for rental ads',
    path: ['rentalPeriod'],
  }
)

interface AdEditorProps {
  // Context
  team: TeamDetailDto
  teamSlug: string

  // Initial values (REQUIRED - each page prepares these)
  initialValues: AdRequest

  // Submission
  onSubmit: (data: AdRequest) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean

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
  submitButtonText,
  cancelButtonText,
}: AdEditorProps) {
  const { t } = useTranslation('ads')
  const { t: tCommon } = useTranslation('common')

  const form = useForm<AdRequest>({
    resolver: zodResolver(adSchema),
    mode: 'onChange',
  })

  // Sync form values when initial props change (for edit mode)
  useEffect(() => {
    form.reset(initialValues)
  }, [initialValues, form])

  const adType = useWatch({ control: form.control, name: 'adType' })

  // Handle adType changes: clear rentalPeriod if not RENTAL, re-validate
  useEffect(() => {
    form.trigger('rentalPeriod')
  }, [adType, form])

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
                    <RadioGroupItem value={AdType.SALE} id="adType-sale" />
                    <Label htmlFor="adType-sale">{t('adType.SALE')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AdType.RENTAL} id="adType-rental" />
                    <Label htmlFor="adType-rental">{t('adType.RENTAL')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AdType.WANTED} id="adType-wanted" />
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
        {adType === AdType.RENTAL && (
          <FormField
            control={form.control}
            name="rentalPeriod"
            render={({ field }) => (
              <FormItem>
                <FormLabel>
                  {t('create.rentalPeriodLabel')} <span className="text-destructive">*</span>
                </FormLabel>
                <Select {...field} onValueChange={field.onChange}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder={t('create.rentalPeriodLabel')} />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value={RentalPeriod.DAY}>{t('rentalPeriod.DAY')}</SelectItem>
                    <SelectItem value={RentalPeriod.WEEK}>{t('rentalPeriod.WEEK')}</SelectItem>
                    <SelectItem value={RentalPeriod.MONTH}>{t('rentalPeriod.MONTH')}</SelectItem>
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

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            {cancelButtonText || tCommon('actions.cancelAction')}
          </Button>
          <Button type="submit" disabled={isPending || !form.formState.isValid}>
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
