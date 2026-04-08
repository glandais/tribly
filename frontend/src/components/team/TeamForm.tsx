import { Link, useNavigate } from 'react-router-dom'
import { useForm } from '@mantine/form'
import { zodFormValidator } from '@/lib/formUtils'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { useState } from 'react'
import {
  TextInput,
  Select,
  Checkbox,
  Switch,
  Button,
  Group,
  Stack,
  Text,
  Divider,
  Title,
} from '@mantine/core'
import {
  useCreateTeam,
  useUpdateTeam,
  getListTeamsQueryKey,
  getGetTeamQueryKey,
} from '@/api/endpoints/teams/teams'
import { useAdminUpdateTeamAttributes } from '@/api/endpoints/admin-teams/admin-teams'
import { SlugEditor } from '../common/SlugEditor'
import { Visibility, TeamDetailDto, TeamRequest, GeoJsonPoint } from '@/api/dto'
import { MediaEditor } from '../common/MediaEditor'
import { GeocoderAutocomplete } from '../common/GeocoderAutocomplete'
import { paths } from '@/config/paths'
import { CreateTeamBody } from '@/api/zod/teams/teams.zod'
import { useAuthStore, selectIsPlatformAdmin } from '@/store/authStore'

const teamSchema = CreateTeamBody

interface GovernanceAttrs {
  visibilityEditable: boolean
  joinable: boolean
  addMemberAllowed: boolean
}

interface GovernancePanelProps {
  attrs: GovernanceAttrs
  onChange: (attrs: GovernanceAttrs) => void
}

function TeamGovernancePanel({ attrs, onChange }: GovernancePanelProps) {
  const { t } = useTranslation()

  return (
    <>
      <Divider mt="md" />
      <Stack gap="xs">
        <Title order={4}>{t('teams.settings.platformAdmin.title')}</Title>
        <Switch
          label={t('teams.settings.platformAdmin.visibilityEditable')}
          checked={attrs.visibilityEditable}
          onChange={(e) => onChange({ ...attrs, visibilityEditable: e.currentTarget.checked })}
        />
        <Switch
          label={t('teams.settings.platformAdmin.joinable')}
          checked={attrs.joinable}
          onChange={(e) => onChange({ ...attrs, joinable: e.currentTarget.checked })}
        />
        <Switch
          label={t('teams.settings.platformAdmin.addMemberAllowed')}
          checked={attrs.addMemberAllowed}
          onChange={(e) => onChange({ ...attrs, addMemberAllowed: e.currentTarget.checked })}
        />
      </Stack>
    </>
  )
}

interface TeamFormProps {
  teamSlug?: string
  teamId?: string
  initialValues: TeamRequest & {
    visibilityEditable?: boolean
    joinable?: boolean
    addMemberAllowed?: boolean
  }
  onSuccess: (team: TeamDetailDto) => void
  create: boolean
  onSlugChange?: (newSlug: string) => Promise<void>
}

export function TeamForm({
  teamSlug,
  teamId,
  initialValues,
  onSuccess,
  create,
  onSlugChange,
}: TeamFormProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const navigate = useNavigate()
  const isPlatformAdmin = useAuthStore(selectIsPlatformAdmin)
  const [governanceAttrs, setGovernanceAttrs] = useState<GovernanceAttrs>({
    visibilityEditable: initialValues.visibilityEditable ?? false,
    joinable: initialValues.joinable ?? false,
    addMemberAllowed: initialValues.addMemberAllowed ?? false,
  })

  const createMutation = useCreateTeam()
  const updateMutation = useUpdateTeam()
  const adminAttrsMutation = useAdminUpdateTeamAttributes()
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
            if (isPlatformAdmin && teamId) {
              adminAttrsMutation.mutate(
                { teamId, data: governanceAttrs },
                {
                  onSuccess: () => {
                    queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(team.slug) })
                    notifications.show({
                      message: i18next.t('teams.notifications.updated'),
                      color: 'green',
                    })
                    onSuccess(team)
                    navigate(paths.team(team.slug))
                  },
                  onError: () => {
                    notifications.show({
                      message: i18next.t('teams.notifications.governanceUpdateFailed'),
                      color: 'red',
                    })
                  },
                }
              )
            } else {
              notifications.show({
                message: i18next.t('teams.notifications.updated'),
                color: 'green',
              })
              onSuccess(team)
              navigate(paths.team(team.slug))
            }
          },
        }
      )
    }
  }

  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Stack>
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

        {!create && (initialValues.visibilityEditable || isPlatformAdmin) && (
          <Select
            label={t('teams.create.form.visibility.label')}
            description={t('teams.create.form.visibility.hint')}
            data={[
              { value: Visibility.TEAM, label: t('visibility.team') },
              { value: Visibility.PUBLIC_UNLISTED, label: t('visibility.public_unlisted') },
              { value: Visibility.PUBLIC, label: t('visibility.public') },
            ]}
            {...form.getInputProps('visibility')}
          />
        )}

        <GeocoderAutocomplete
          value={form.values.geometry as GeoJsonPoint | null | undefined}
          onChange={(point) => form.setFieldValue('geometry', point ?? undefined)}
          label={t('geocoder.label')}
          disabled={mutation.isPending}
        />

        <Checkbox
          label={t('teams.create.form.enableRoutes.label')}
          description={t('teams.create.form.enableRoutes.hint')}
          {...form.getInputProps('enableRoutes', { type: 'checkbox' })}
          onChange={(e) => {
            form.getInputProps('enableRoutes', { type: 'checkbox' }).onChange(e)
            if (!e.currentTarget.checked) {
              form.setFieldValue('enableRides', false)
              form.setFieldValue('enableTrips', false)
            }
          }}
        />

        <Checkbox
          label={t('teams.create.form.enableRides.label')}
          description={t('teams.create.form.enableRides.hint')}
          {...form.getInputProps('enableRides', { type: 'checkbox' })}
          disabled={!form.values.enableRoutes}
        />

        <Checkbox
          label={t('teams.create.form.enableTrips.label')}
          description={t('teams.create.form.enableTrips.hint')}
          {...form.getInputProps('enableTrips', { type: 'checkbox' })}
          disabled={!form.values.enableRoutes}
        />

        <Checkbox
          label={t('teams.create.form.enablePosts.label')}
          description={t('teams.create.form.enablePosts.hint')}
          {...form.getInputProps('enablePosts', { type: 'checkbox' })}
        />

        <Checkbox
          label={t('teams.create.form.enableAds.label')}
          description={t('teams.create.form.enableAds.hint')}
          {...form.getInputProps('enableAds', { type: 'checkbox' })}
        />

        {!create && isPlatformAdmin && teamId && (
          <TeamGovernancePanel attrs={governanceAttrs} onChange={setGovernanceAttrs} />
        )}

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
