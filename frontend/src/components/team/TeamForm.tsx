import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import { useCreateTeam, useUpdateTeam } from '../../hooks/useTeam'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ApiClientError } from '../../lib/apiClient'
import { Visibility, TeamDetailDto, MediaDto } from '../../api/api'
import { MediaEditor } from '../common/MediaEditor'
import { defaultMedia } from '@/lib/apiUtils'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
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

const teamSchema = z.object({
  name: z.string().min(2).max(255),
  media: z.custom<MediaDto>(),
  visibility: z.nativeEnum(Visibility),
  enableTrips: z.boolean(),
})

type TeamFormValues = z.infer<typeof teamSchema>

interface TeamFormProps {
  // Data
  teamSlug?: string
  initialName?: string
  initialMedia?: MediaDto
  initialVisibility?: Visibility
  initialEnableTrips?: boolean

  // Behavior
  onSuccess: (team: TeamDetailDto) => void

  create: boolean
}

export function TeamForm({
  teamSlug,
  initialName = '',
  initialMedia,
  initialVisibility = Visibility.Public,
  initialEnableTrips = true,
  onSuccess,
  create,
}: TeamFormProps) {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')

  // Conditional mutation based on context
  const createMutation = useCreateTeam()
  const updateMutation = useUpdateTeam(teamSlug || '')
  const mutation = teamSlug ? updateMutation : createMutation

  const form = useForm<TeamFormValues>({
    resolver: zodResolver(teamSchema),
    defaultValues: {
      name: initialName,
      media: initialMedia ?? defaultMedia(),
      visibility: initialVisibility,
      enableTrips: initialEnableTrips,
    },
  })

  const media = useWatch({ control: form.control, name: 'media' })

  // Sync form values when initial props change (for edit mode)
  useEffect(() => {
    if (initialName) {
      form.setValue('name', initialName)
    }
    if (initialMedia?.markdown || initialMedia?.assets?.images?.length) {
      form.setValue('media', initialMedia)
    }
    if (initialVisibility) {
      form.setValue('visibility', initialVisibility)
    }
    if (initialEnableTrips !== undefined) {
      form.setValue('enableTrips', initialEnableTrips)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    initialName,
    initialMedia?.markdown,
    initialMedia?.assets?.images?.length,
    initialVisibility,
    initialEnableTrips,
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
        if (err.field && err.field in teamSchema.shape) {
          form.setError(err.field as keyof TeamFormValues, { message: err.message })
        }
      })
    }
  }, [mutation.error, form])

  const handleSubmit = (values: TeamFormValues) => {
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
                  : create
                    ? t('create.error')
                    : t('settings.error')}
              </p>
            </div>
          )}

        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>
                {t('create.form.name.label')}
                {create && <span className="text-destructive"> *</span>}
              </FormLabel>
              <FormControl>
                <Input
                  placeholder={create ? t('create.form.name.placeholder') : undefined}
                  {...field}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="media"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{t('create.form.description.label')}</FormLabel>
              <FormControl>
                <MediaEditor
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={t('create.form.description.placeholder')}
                  minHeight="150px"
                  maxHeight="300px"
                  disabled={mutation.isPending}
                  ariaLabel={t('create.form.description.placeholder')}
                  teamSlug={teamSlug}
                />
              </FormControl>
              <FormDescription>
                {t('create.form.description.charCount', {
                  count: media.markdown?.length || 0,
                  max: 2000,
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
              <FormLabel>{t('create.form.visibility.label')}</FormLabel>
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
              <FormDescription>{t('create.form.visibility.hint')}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="enableTrips"
          render={({ field }) => (
            <FormItem className="flex items-start space-x-3 space-y-0">
              <FormControl>
                <Checkbox checked={field.value} onCheckedChange={field.onChange} className="mt-1" />
              </FormControl>
              <div className="space-y-1">
                <FormLabel>{t('create.form.enableTrips.label')}</FormLabel>
                <FormDescription>{t('create.form.enableTrips.hint')}</FormDescription>
              </div>
            </FormItem>
          )}
        />

        <div className="pt-4 flex items-center justify-end gap-3">
          <Button variant="outline" asChild>
            <Link to={create ? '/teams' : `/teams/${teamSlug}`}>{tCommon('buttons.cancel')}</Link>
          </Button>
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {create ? t('create.creating') : t('settings.saving')}
              </>
            ) : create ? (
              t('create.button')
            ) : (
              t('settings.saveChanges')
            )}
          </Button>
        </div>
      </form>
    </Form>
  )
}
