import { useEffect } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import type { TeamDetailDto } from '@/api/dto'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
import { SlugEditor } from '../common/SlugEditor'
import { Status, PostRequest } from '@/api/dto'
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
import { createPostBody } from '@/api/zod/posts/posts.zod'
import { InputDateTime } from '../ui/input-datetime'

const postSchema = createPostBody.refine(
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

interface PostEditorProps {
  // Context
  team: TeamDetailDto
  teamSlug: string

  // Initial values (REQUIRED - each page prepares these)
  initialValues: PostRequest

  // Submission
  onSubmit: (data: PostRequest) => void | Promise<void>
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

export function PostEditor({
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
}: PostEditorProps) {
  const { t: t } = useTranslation()

  const form = useForm<PostRequest>({
    resolver: zodResolver(postSchema),
    mode: 'onChange',
    defaultValues: initialValues,
  })

  const status = useWatch({ control: form.control, name: 'status' })

  useEffect(() => {
    form.trigger('publishAt')
  }, [status, form])

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
                <Input placeholder={t('posts.create.namePlaceholder')} {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Slug Editor (only in edit mode) */}
        {currentSlug && onSlugChange && (
          <SlugEditor
            currentSlug={currentSlug}
            baseUrl={`/teams/${teamSlug}/posts/`}
            onSlugChange={onSlugChange}
            disabled={!canEditSlug}
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
                  placeholder={t('posts.create.descriptionPlaceholder')}
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

        {/* Date and Time (shown when PUBLISHED) */}
        {status === Status.PUBLISHED && (
          <FormField
            control={form.control}
            name="dateTime"
            render={({ field }) => (
              <FormItem>
                <FormLabel>
                  {t('posts.create.dateTimeLabel')} <span className="text-destructive">*</span>
                </FormLabel>
                <FormControl>
                  <InputDateTime {...field} />
                </FormControl>
                <FormDescription>{t('posts.create.dateTimeHint')}</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        {/* Scheduled Publication (shown when DRAFT) */}
        {status === Status.DRAFT && (
          <FormField
            control={form.control}
            name="publishAt"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('posts.create.publishAtLabel')}</FormLabel>
                <FormControl>
                  <InputDateTime {...field} />
                </FormControl>
                <FormDescription>{t('form.publishAtHint')}</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

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
