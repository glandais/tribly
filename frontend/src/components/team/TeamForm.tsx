import { useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import {
  useCreateTeam,
  useUpdateTeam,
  getListTeamsQueryKey,
  getGetTeamQueryKey,
} from '@/api/endpoints/teams/teams'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { Visibility, TeamDetailDto, TeamRequest } from '@/api/dto'
import { MediaEditor } from '../common/MediaEditor'
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
import { paths } from '@/config/paths'
import { createTeamBody } from '@/api/zod/teams/teams.zod'

const teamSchema = createTeamBody

interface TeamFormProps {
  // Data
  teamSlug?: string
  initialValues: TeamRequest

  // Behavior
  onSuccess: (team: TeamDetailDto) => void

  create: boolean
}

export function TeamForm({ teamSlug, initialValues, onSuccess, create }: TeamFormProps) {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  // Conditional mutation based on context
  const createMutation = useCreateTeam()
  const updateMutation = useUpdateTeam()
  const mutation = teamSlug ? updateMutation : createMutation

  const form = useForm<TeamRequest>({
    resolver: zodResolver(teamSchema),
    mode: 'onChange',
  })

  // Sync form values when initial props change (for edit mode)
  useEffect(() => {
    form.reset(initialValues)
  }, [initialValues, form])

  const handleSubmit = (values: TeamRequest) => {
    if (create) {
      createMutation.mutate(
        { data: values },
        {
          onSuccess: (team) => {
            queryClient.invalidateQueries({ queryKey: getListTeamsQueryKey() })
            toast.success(i18next.t('teams:notifications.created'))
            onSuccess(team)
            navigate(paths.team(team.slug))
          },
        }
      )
    } else if (teamSlug) {
      updateMutation.mutate(
        { slug: teamSlug, data: values },
        {
          onSuccess: (team) => {
            queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
            queryClient.invalidateQueries({ queryKey: getListTeamsQueryKey() })
            queryClient.setQueryData(getGetTeamQueryKey(team.slug), team)
            toast.success(i18next.t('teams:notifications.updated'))
            onSuccess(team)
            navigate(paths.team(team.slug))
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
              <FormLabel>{tCommon('form.description')}</FormLabel>
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
                {tCommon('form.charCount', {
                  count: field.value.markdown?.length || 0,
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
                  <SelectItem value={Visibility.TEAM}>{tCommon('visibility.team')}</SelectItem>
                  <SelectItem value={Visibility.PUBLIC}>{tCommon('visibility.public')}</SelectItem>
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

        <FormField
          control={form.control}
          name="enableAds"
          render={({ field }) => (
            <FormItem className="flex items-start space-x-3 space-y-0">
              <FormControl>
                <Checkbox checked={field.value} onCheckedChange={field.onChange} className="mt-1" />
              </FormControl>
              <div className="space-y-1">
                <FormLabel>{t('create.form.enableAds.label')}</FormLabel>
                <FormDescription>{t('create.form.enableAds.hint')}</FormDescription>
              </div>
            </FormItem>
          )}
        />

        <div className="pt-4 flex items-center justify-end gap-3">
          <Button variant="outline" asChild>
            <Link to={create ? paths.teams() : paths.team(teamSlug!)}>
              {tCommon('actions.cancelAction')}
            </Link>
          </Button>
          <Button type="submit" disabled={mutation.isPending || !form.formState.isValid}>
            {mutation.isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {create ? tCommon('status.creating') : tCommon('status.saving')}
              </>
            ) : create ? (
              t('create.button')
            ) : (
              tCommon('actions.save')
            )}
          </Button>
        </div>
      </form>
    </Form>
  )
}
