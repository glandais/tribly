import { Link, useNavigate } from 'react-router-dom'
import { useForm } from '@mantine/form'
import { zodFormValidator } from '@/lib/formUtils'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { TextInput, Select, Checkbox, Button, Group, Stack, Text } from '@mantine/core'
import {
  useCreateTeam,
  useUpdateTeam,
  getListTeamsQueryKey,
  getGetTeamQueryKey,
} from '@/api/endpoints/teams/teams'
import { SlugEditor } from '../common/SlugEditor'
import { Visibility, TeamDetailDto, TeamRequest, GeoJsonPoint } from '@/api/dto'
import { MediaEditor } from '../common/MediaEditor'
import { GeocoderAutocomplete } from '../common/GeocoderAutocomplete'
import { paths } from '@/config/paths'
import { createTeamBody } from '@/api/zod/teams/teams.zod'

const teamSchema = createTeamBody

interface TeamFormProps {
  teamSlug?: string
  initialValues: TeamRequest
  onSuccess: (team: TeamDetailDto) => void
  create: boolean
  onSlugChange?: (newSlug: string) => Promise<void>
}

export function TeamForm({
  teamSlug,
  initialValues,
  onSuccess,
  create,
  onSlugChange,
}: TeamFormProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const createMutation = useCreateTeam()
  const updateMutation = useUpdateTeam()
  const mutation = teamSlug ? updateMutation : createMutation

  function getSubmitButtonText(): string {
    if (mutation.isPending) {
      return create ? t('status.creating') : t('status.saving')
    }
    return create ? t('teams.create.button') : t('actions.save')
  }

  const form = useForm<TeamRequest>({
    validate: zodFormValidator<TeamRequest>(teamSchema),
    initialValues,
    validateInputOnChange: true,
  })

  const handleSubmit = (values: TeamRequest) => {
    if (create) {
      createMutation.mutate(
        { data: values },
        {
          onSuccess: (team) => {
            queryClient.invalidateQueries({ queryKey: getListTeamsQueryKey() })
            notifications.show({
              message: i18next.t('teams.notifications.created'),
              color: 'green',
            })
            onSuccess(team)
            navigate(paths.team(team.slug))
          },
        }
      )
    } else if (teamSlug) {
      updateMutation.mutate(
        { teamSlug, data: values },
        {
          onSuccess: (team) => {
            queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
            queryClient.invalidateQueries({ queryKey: getListTeamsQueryKey() })
            queryClient.setQueryData(getGetTeamQueryKey(team.slug), team)
            notifications.show({
              message: i18next.t('teams.notifications.updated'),
              color: 'green',
            })
            onSuccess(team)
            navigate(paths.team(team.slug))
          },
        }
      )
    }
  }

  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Stack gap="lg">
        <TextInput
          label={
            <>
              {t('teams.create.form.name.label')}
              {create && (
                <Text span c="red">
                  {' '}
                  *
                </Text>
              )}
            </>
          }
          placeholder={create ? t('teams.create.form.name.placeholder') : undefined}
          {...form.getInputProps('name')}
        />

        {!create && teamSlug && onSlugChange && (
          <SlugEditor currentSlug={teamSlug} baseUrl="/teams/" onSlugChange={onSlugChange} />
        )}

        <Stack gap="xs">
          <Text size="sm" fw={500}>
            {t('form.description')}
          </Text>
          <MediaEditor
            value={form.values.media}
            onChange={(val) => form.setFieldValue('media', val)}
            placeholder={t('teams.create.form.description.placeholder')}
            minHeight="150px"
            disabled={mutation.isPending}
            ariaLabel={t('teams.create.form.description.placeholder')}
            teamSlug={teamSlug}
          />
          <Text size="xs" c="dimmed">
            {t('form.charCount', {
              count: form.values.media.markdown.length || 0,
              max: 2000,
            })}
          </Text>
        </Stack>

        <Select
          label={t('teams.create.form.visibility.label')}
          description={t('teams.create.form.visibility.hint')}
          data={[
            { value: Visibility.TEAM, label: t('visibility.team') },
            { value: Visibility.PUBLIC, label: t('visibility.public') },
          ]}
          {...form.getInputProps('visibility')}
        />

        <GeocoderAutocomplete
          value={form.values.geometry as GeoJsonPoint | null | undefined}
          onChange={(point) => form.setFieldValue('geometry', point ?? undefined)}
          label={t('geocoder.label')}
          disabled={mutation.isPending}
        />

        <Checkbox
          label={t('teams.create.form.enableTrips.label')}
          description={t('teams.create.form.enableTrips.hint')}
          {...form.getInputProps('enableTrips', { type: 'checkbox' })}
        />

        <Checkbox
          label={t('teams.create.form.enableAds.label')}
          description={t('teams.create.form.enableAds.hint')}
          {...form.getInputProps('enableAds', { type: 'checkbox' })}
        />

        <Group justify="flex-end" pt="md">
          <Button
            variant="default"
            component={Link}
            to={create ? paths.teams() : paths.team(teamSlug!)}
          >
            {t('actions.cancelAction')}
          </Button>
          <Button
            type="submit"
            disabled={mutation.isPending || !form.isValid()}
            loading={mutation.isPending}
          >
            {getSubmitButtonText()}
          </Button>
        </Group>
      </Stack>
    </form>
  )
}
