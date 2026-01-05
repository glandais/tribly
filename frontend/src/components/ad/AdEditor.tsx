import { useEffect } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import { AdRequest, AdType, RentalPeriod, TeamDetailDto } from '@/api/dto'
import { createAdBody } from '@/api/zod/ads/ads.zod'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
import { SlugEditor } from '../common/SlugEditor'
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

  // Slug editing (only for edit mode)
  currentSlug?: string
  onSlugChange?: (newSlug: string) => Promise<void>
  canEditSlug?: boolean
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
  currentSlug,
  onSlugChange,
  canEditSlug = false,
}: AdEditorProps) {
  const { t } = useTranslation()

  const form = useForm<AdRequest>({
    resolver: zodResolver(adSchema),
    mode: 'onChange',
    defaultValues: initialValues,
  })

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
                {t('form.title')} <span className="text-destructive">*</span>
              </FormLabel>
              <FormControl>
                <Input placeholder={t('ads.create.namePlaceholder')} {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Slug Editor (only in edit mode) */}
        {currentSlug && onSlugChange && (
          <SlugEditor
            currentSlug={currentSlug}
            baseUrl={`/teams/${teamSlug}/ads/`}
            onSlugChange={onSlugChange}
            disabled={!canEditSlug}
          />
        )}

        {/* Ad Type */}
        <FormField
          control={form.control}
          name="adType"
          render={({ field }) => (
            <FormItem>
              <FormLabel>
                {t('ads.create.adTypeLabel')} <span className="text-destructive">*</span>
              </FormLabel>
              <FormControl>
                <RadioGroup
                  value={field.value}
                  onValueChange={field.onChange}
                  className="flex gap-4"
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AdType.SALE} id="adType-sale" />
                    <Label htmlFor="adType-sale">{t('ads.adType.SALE')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AdType.RENTAL} id="adType-rental" />
                    <Label htmlFor="adType-rental">{t('ads.adType.RENTAL')}</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value={AdType.WANTED} id="adType-wanted" />
                    <Label htmlFor="adType-wanted">{t('ads.adType.WANTED')}</Label>
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
              <FormLabel>{t('ads.create.priceLabel')}</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder={t('ads.create.pricePlaceholder')}
                  {...field}
                  value={field.value ?? ''}
                  onChange={(e) =>
                    field.onChange(e.target.value ? Number(e.target.value) : undefined)
                  }
                />
              </FormControl>
              <FormDescription>{t('ads.create.priceHint')}</FormDescription>
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
                  {t('ads.create.rentalPeriodLabel')} <span className="text-destructive">*</span>
                </FormLabel>
                <Select {...field} onValueChange={field.onChange}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder={t('ads.create.rentalPeriodLabel')} />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value={RentalPeriod.DAY}>{t('ads.rentalPeriod.DAY')}</SelectItem>
                    <SelectItem value={RentalPeriod.WEEK}>{t('ads.rentalPeriod.WEEK')}</SelectItem>
                    <SelectItem value={RentalPeriod.MONTH}>
                      {t('ads.rentalPeriod.MONTH')}
                    </SelectItem>
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
              <FormLabel>{t('form.description')}</FormLabel>
              <FormControl>
                <MediaEditor
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={t('ads.create.descriptionPlaceholder')}
                  minHeight="200px"
                  maxHeight="400px"
                  disabled={isPending}
                  ariaLabel={t('form.description')}
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
              <FormLabel>{t('ads.create.locationDescriptionLabel')}</FormLabel>
              <FormControl>
                <Input placeholder={t('ads.create.locationDescriptionPlaceholder')} {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

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
                </RadioGroup>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Actions */}
        <div className="pt-4 flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            {cancelButtonText || t('actions.cancelAction')}
          </Button>
          <Button type="submit" disabled={isPending || !form.formState.isValid}>
            {isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {t('loading')}
              </>
            ) : (
              submitButtonText || t('actions.save')
            )}
          </Button>
        </div>
      </form>
    </Form>
  )
}
