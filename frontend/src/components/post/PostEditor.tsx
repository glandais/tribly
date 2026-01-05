import { useEffect } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import type { TeamDetailDto } from '@/api/dto'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
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
}: PostEditorProps) {
  const { t } = useTranslation('posts')
  const { t: tCommon } = useTranslation('common')

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
        {status === Status.PUBLISHED && (
          <FormField
            control={form.control}
            name="dateTime"
            render={({ field }) => (
              <FormItem>
                <FormLabel>
                  {t('create.dateTimeLabel')} <span className="text-destructive">*</span>
                </FormLabel>
                <FormControl>
                  <InputDateTime {...field} />
                </FormControl>
                <FormDescription>{t('create.dateTimeHint')}</FormDescription>
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
                <FormLabel>{t('create.publishAtLabel')}</FormLabel>
                <FormControl>
                  <InputDateTime {...field} />
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
