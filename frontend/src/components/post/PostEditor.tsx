import { useEffect } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import type { MediaDto, TeamDetailDto } from '../../api/api'
import { ApiClientError } from '../../lib/apiClient'
import { fromDateTimeLocalValue, toDateTimeLocalValue } from '../../utils/dateFormat'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
import { Visibility, Status } from '../../hooks/usePost'
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

export interface PostFormData {
  name: string
  media: MediaDto
  dateTime: string // ISO string
  visibility: Visibility
  status: Status
  publishAt?: string // ISO string
}

const postSchema = z.object({
  name: z.string().min(3).max(200),
  media: z.custom<MediaDto>(),
  dateTime: z.string(),
  visibility: z.nativeEnum(Visibility),
  status: z.nativeEnum(Status),
  publishAt: z.string().optional(),
})

type PostFormValues = z.infer<typeof postSchema>

interface PostEditorProps {
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
  }

  // Submission
  onSubmit: (data: PostFormData) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean
  error?: Error | ApiClientError | null

  // UI customization
  submitButtonText?: string
  cancelButtonText?: string
}

export function PostEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error,
  submitButtonText,
  cancelButtonText,
}: PostEditorProps) {
  const { t } = useTranslation('posts')
  const { t: tCommon } = useTranslation('common')

  const form = useForm<PostFormValues>({
    resolver: zodResolver(postSchema),
    defaultValues: {
      name: initialValues.name,
      media: initialValues.media ?? defaultMedia(),
      dateTime: initialValues.dateTime,
      visibility: initialValues.visibility,
      status: initialValues.status,
      publishAt: initialValues.publishAt || '',
    },
  })

  const status = useWatch({ control: form.control, name: 'status' })
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
    form.setValue('publishAt', initialValues.publishAt || '')
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    initialValues.name,
    initialValues.media?.markdown,
    initialValues.media?.assets?.images?.length,
    initialValues.dateTime,
    initialValues.visibility,
    initialValues.status,
    initialValues.publishAt,
  ])

  // Set server-side errors on form fields
  useEffect(() => {
    if (error instanceof ApiClientError && error.error.errors) {
      error.error.errors.forEach((err) => {
        if (err.field && err.field in postSchema.shape) {
          form.setError(err.field as keyof PostFormValues, { message: err.message })
        }
      })
    }
  }, [error, form])

  const handleSubmit = (values: PostFormValues) => {
    onSubmit({
      name: values.name,
      media: values.media,
      dateTime: fromDateTimeLocalValue(values.dateTime).toISOString(),
      status: values.status,
      visibility: values.visibility,
      publishAt: values.publishAt
        ? fromDateTimeLocalValue(values.publishAt).toISOString()
        : undefined,
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

        {/* Date and Time (shown when PUBLISHED) */}
        {status === Status.Published && (
          <FormField
            control={form.control}
            name="dateTime"
            render={({ field }) => (
              <FormItem>
                <FormLabel>
                  {t('create.dateTimeLabel')} <span className="text-destructive">*</span>
                </FormLabel>
                <FormControl>
                  <Input type="datetime-local" {...field} />
                </FormControl>
                <FormDescription>{t('create.dateTimeHint')}</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        {/* Scheduled Publication (shown when DRAFT) */}
        {status === Status.Draft && (
          <FormField
            control={form.control}
            name="publishAt"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('create.publishAtLabel')}</FormLabel>
                <FormControl>
                  <Input type="datetime-local" min={toDateTimeLocalValue(new Date())} {...field} />
                </FormControl>
                <FormDescription>{tCommon('form.publishAtHint')}</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

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
