import { useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import {
  useCreatePage,
  useUpdatePage,
  getListPagesQueryKey,
  getGetPageQueryKey,
} from '@/api/endpoints/team-pages/team-pages'
import { getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { Visibility, TeamPageRequest } from '@/api/dto'
import { MediaEditor } from '../common/MediaEditor'
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
import { createPageBody } from '@/api/zod/team-pages/team-pages.zod'

const teamPageSchema = createPageBody

interface TeamPageFormProps {
  teamSlug: string
  pageSlug?: string
  initialValues: TeamPageRequest
  onSuccess: (page: TeamPageRequest) => void
  isCreate: boolean
}

export function TeamPageForm({
  teamSlug,
  pageSlug,
  initialValues,
  onSuccess,
  isCreate,
}: TeamPageFormProps) {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const createMutation = useCreatePage()
  const updateMutation = useUpdatePage()
  const mutation = isCreate ? createMutation : updateMutation

  const form = useForm<TeamPageRequest>({
    resolver: zodResolver(teamPageSchema),
    mode: 'onChange',
  })

  // Sync form values when initial props change (for edit mode)
  useEffect(() => {
    form.reset(initialValues)
  }, [initialValues, form])

  const handleSubmit = (values: TeamPageRequest) => {
    if (isCreate) {
      createMutation.mutate(
        { slug: teamSlug, data: values },
        {
          onSuccess: (page) => {
            queryClient.invalidateQueries({ queryKey: getListPagesQueryKey(teamSlug) })
            queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
            toast.success(i18next.t('teams:pages.notifications.created'))
            onSuccess(page)
            navigate(paths.teamAdminPages(teamSlug))
          },
        }
      )
    } else if (pageSlug) {
      updateMutation.mutate(
        { slug: teamSlug, pageSlug, data: values },
        {
          onSuccess: (page) => {
            queryClient.invalidateQueries({ queryKey: getListPagesQueryKey(teamSlug) })
            queryClient.invalidateQueries({ queryKey: getGetPageQueryKey(teamSlug, pageSlug) })
            queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
            queryClient.setQueryData(getGetPageQueryKey(teamSlug, page.slug), page)
            toast.success(i18next.t('teams:pages.notifications.updated'))
            onSuccess(page)
            navigate(paths.teamAdminPages(teamSlug))
          },
        }
      )
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
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
                {tCommon('form.charCount', {
                  count: field.value.markdown?.length || 0,
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
                  <SelectItem value={Visibility.TEAM}>{tCommon('visibility.team')}</SelectItem>
                  <SelectItem value={Visibility.PUBLIC}>{tCommon('visibility.public')}</SelectItem>
                </SelectContent>
              </Select>
              <FormDescription>{t('pages.form.visibility.hint')}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="pt-4 flex items-center justify-end gap-3">
          <Button variant="outline" asChild>
            <Link to={paths.teamAdminPages(teamSlug)}>{tCommon('actions.cancelAction')}</Link>
          </Button>
          <Button type="submit" disabled={mutation.isPending || !form.formState.isValid}>
            {mutation.isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {isCreate ? tCommon('status.creating') : tCommon('status.saving')}
              </>
            ) : isCreate ? (
              t('pages.form.create')
            ) : (
              tCommon('actions.save')
            )}
          </Button>
        </div>
      </form>
    </Form>
  )
}
