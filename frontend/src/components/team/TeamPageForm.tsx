import { Link, useNavigate } from 'react-router-dom'
import { useForm } from '@mantine/form'
import { zodFormValidator } from '@/lib/formUtils'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { TextInput, Select, Stack, Group, Button, Text } from '@mantine/core'
import {
  useCreatePage,
  useUpdatePage,
  useChangePageSlug,
  getListPagesQueryKey,
  getGetPageQueryKey,
} from '@/api/endpoints/team-pages/team-pages'
import { getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import { SlugEditor } from '../common/SlugEditor'
import { Visibility, TeamPageRequest } from '@/api/dto'
import { MediaEditor } from '../common/MediaEditor'
import { paths } from '@/config/paths'
import { CreatePageBody } from '@/api/zod/team-pages/team-pages.zod'

const teamPageSchema = CreatePageBody

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
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const createMutation = useCreatePage()
  const updateMutation = useUpdatePage()
  const changeSlugMutation = useChangePageSlug()
  const mutation = isCreate ? createMutation : updateMutation

  const form = useForm<TeamPageRequest>({
    validate: zodFormValidator<TeamPageRequest>(teamPageSchema),
    initialValues,
    validateInputOnChange: true,
  })

  const handleSubmit = (values: TeamPageRequest) => {
    if (isCreate) {
      createMutation.mutate(
        { teamSlug: teamSlug, data: values },
        {
          onSuccess: (page) => {
            queryClient.invalidateQueries({ queryKey: getListPagesQueryKey(teamSlug) })
            queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
            notifications.show({
              message: i18next.t('teams.pages.notifications.created'),
              color: 'green',
            })
            onSuccess(page)
            navigate(paths.teamAdminPages(teamSlug))
          },
        }
      )
    } else if (pageSlug) {
      updateMutation.mutate(
        { teamSlug: teamSlug, pageSlug, data: values },
        {
          onSuccess: (page) => {
            queryClient.invalidateQueries({ queryKey: getListPagesQueryKey(teamSlug) })
            queryClient.invalidateQueries({ queryKey: getGetPageQueryKey(teamSlug, pageSlug) })
            queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
            queryClient.setQueryData(getGetPageQueryKey(teamSlug, page.slug), page)
            notifications.show({
              message: i18next.t('teams.pages.notifications.updated'),
              color: 'green',
            })
            onSuccess(page)
            navigate(paths.teamAdminPages(teamSlug))
          },
        }
      )
    }
  }

  const handleSlugChange = async (newSlug: string) => {
    if (!pageSlug) return
    await changeSlugMutation.mutateAsync(
      { teamSlug: teamSlug, pageSlug, data: { slug: newSlug } },
      {
        onSuccess: (updatedPage) => {
          queryClient.invalidateQueries({ queryKey: getListPagesQueryKey(teamSlug) })
          queryClient.invalidateQueries({ queryKey: getGetPageQueryKey(teamSlug, pageSlug) })
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
          navigate(paths.teamAdminPageEdit(teamSlug, updatedPage.slug), { replace: true })
        },
      }
    )
  }

  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Stack>
        <TextInput
          label={
            <>
              {t('teams.pages.form.title.label')}{' '}
              <Text span c="red">
                *
              </Text>
            </>
          }
          placeholder={t('teams.pages.form.title.placeholder')}
          description={t('teams.pages.form.title.hint')}
          {...form.getInputProps('title')}
        />

        {!isCreate && pageSlug && (
          <SlugEditor
            currentSlug={pageSlug}
            baseUrl={`/teams/${teamSlug}/pages/`}
            onSlugChange={handleSlugChange}
          />
        )}

        <Stack gap="xs">
          <Text size="sm" fw={500}>
            {t('teams.pages.form.content.label')}
          </Text>
          <MediaEditor
            value={form.values.media}
            onChange={(val) => form.setFieldValue('media', val)}
            placeholder={t('teams.pages.form.content.placeholder')}
            minHeight="200px"
            disabled={mutation.isPending}
            ariaLabel={t('teams.pages.form.content.placeholder')}
            teamSlug={teamSlug}
          />
          <Text size="xs" c="dimmed">
            {t('form.charCount', {
              count: form.values.media.markdown.length || 0,
              max: 10000,
            })}
          </Text>
        </Stack>

        <Select
          label={t('visibility.label')}
          description={t('teams.pages.form.visibility.hint')}
          value={form.values.visibility}
          onChange={(val) => form.setFieldValue('visibility', val as Visibility)}
          data={[
            { value: Visibility.TEAM, label: t('visibility.team') },
            { value: Visibility.PUBLIC, label: t('visibility.public') },
          ]}
        />

        <Group justify="flex-end" pt="md">
          <Button variant="default" component={Link} to={paths.teamAdminPages(teamSlug)}>
            {t('actions.cancelAction')}
          </Button>
          <Button
            type="submit"
            disabled={mutation.isPending || !form.isValid()}
            loading={mutation.isPending}
          >
            {isCreate ? t('teams.pages.form.create') : t('actions.save')}
          </Button>
        </Group>
      </Stack>
    </form>
  )
}
