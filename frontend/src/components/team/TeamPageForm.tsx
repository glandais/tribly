import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import { useCreateTeamPage, useUpdateTeamPage } from '../../hooks/useTeamPages'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ApiClientError } from '../../lib/apiClient'
import { Visibility, TeamPageDto, MediaDto } from '../../api/api'
import { MediaEditor } from '../common/MediaEditor'
import { defaultMedia } from '@/lib/apiUtils'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { paths } from '@/config/paths'

const teamPageSchema = z.object({
  title: z.string().min(2).max(100),
  media: z.custom<MediaDto>(),
  visibility: z.nativeEnum(Visibility),
})

type TeamPageFormValues = z.infer<typeof teamPageSchema>

interface TeamPageFormProps {
  teamSlug: string
  pageSlug?: string
  initialTitle?: string
  initialMedia?: MediaDto
  initialVisibility?: Visibility
  onSuccess: (page: TeamPageDto) => void
  isCreate: boolean
}

export function TeamPageForm({
  teamSlug,
  pageSlug,
  initialTitle = '',
  initialMedia,
  initialVisibility = Visibility.Team,
  onSuccess,
  isCreate,
}: TeamPageFormProps) {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')

  const createMutation = useCreateTeamPage(teamSlug)
  const updateMutation = useUpdateTeamPage(teamSlug, pageSlug || '')
  const mutation = isCreate ? createMutation : updateMutation

  const form = useForm<TeamPageFormValues>({
    resolver: zodResolver(teamPageSchema),
    defaultValues: {
      title: initialTitle,
      media: initialMedia ?? defaultMedia(),
      visibility: initialVisibility,
    },
  })

  const media = useWatch({ control: form.control, name: 'media' })

  // Sync form values when initial props change (for edit mode)
  useEffect(() => {
    if (initialTitle) {
      form.setValue('title', initialTitle)
    }
    if (initialMedia?.markdown || initialMedia?.assets?.images?.length) {
      form.setValue('media', initialMedia)
    }
    if (initialVisibility) {
      form.setValue('visibility', initialVisibility)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    initialTitle,
    initialMedia?.markdown,
    initialMedia?.assets?.images?.length,
    initialVisibility,
  ])

  // Call onSuccess when mutation succeeds
  useEffect(() => {
    if (mutation.isSuccess && mutation.data) {
      onSuccess(mutation.data)
    }
  }, [mutation.isSuccess, mutation.data, onSuccess])

  // Set server-side errors on form fields
  useEffect(() => {
    if (mutation.error instanceof ApiClientError && mutation.error.error.errors) {
      mutation.error.error.errors.forEach((err) => {
        if (err.field && err.field in teamPageSchema.shape) {
          form.setError(err.field as keyof TeamPageFormValues, { message: err.message })
        }
      })
    }
  }, [mutation.error, form])

  const handleSubmit = (values: TeamPageFormValues) => {
    mutation.mutate(values)
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
        {mutation.error &&
          !(mutation.error instanceof ApiClientError && mutation.error.error.errors) && (
            <div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg">
              <p className="text-destructive">
                {mutation.error instanceof ApiClientError
                  ? mutation.error.error.message
                  : isCreate
                    ? t('pages.form.createError')
                    : t('pages.form.updateError')}
              </p>
            </div>
          )}

        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>
                {t('pages.form.title.label')}
                <span className="text-destructive"> *</span>
              </FormLabel>
              <FormControl>
                <Input placeholder={t('pages.form.title.placeholder')} {...field} />
              </FormControl>
              <FormDescription>{t('pages.form.title.hint')}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="media"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{t('pages.form.content.label')}</FormLabel>
              <FormControl>
                <MediaEditor
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={t('pages.form.content.placeholder')}
                  minHeight="200px"
                  maxHeight="400px"
                  disabled={mutation.isPending}
                  ariaLabel={t('pages.form.content.placeholder')}
                  teamSlug={teamSlug}
                />
              </FormControl>
              <FormDescription>
                {t('pages.form.content.charCount', {
                  count: media.markdown?.length || 0,
                  max: 10000,
                })}
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="visibility"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{t('pages.form.visibility.label')}</FormLabel>
              <Select value={field.value} onValueChange={field.onChange}>
                <FormControl>
                  <SelectTrigger className="w-full">
                    <SelectValue />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value={Visibility.Team}>{tCommon('visibility.team')}</SelectItem>
                  <SelectItem value={Visibility.Public}>{tCommon('visibility.public')}</SelectItem>
                </SelectContent>
              </Select>
              <FormDescription>{t('pages.form.visibility.hint')}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="pt-4 flex items-center justify-end gap-3">
          <Button variant="outline" asChild>
            <Link to={paths.teamAdminPages(teamSlug)}>{tCommon('buttons.cancel')}</Link>
          </Button>
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {isCreate ? t('pages.form.creating') : t('pages.form.saving')}
              </>
            ) : isCreate ? (
              t('pages.form.create')
            ) : (
              t('pages.form.save')
            )}
          </Button>
        </div>
      </form>
    </Form>
  )
}
