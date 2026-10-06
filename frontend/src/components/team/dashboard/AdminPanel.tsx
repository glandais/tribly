import { useTranslation } from 'react-i18next'
import {
  ActionIcon,
  Badge,
  Box,
  Button,
  Group,
  Progress,
  Stack,
  Text,
  Tooltip,
} from '@mantine/core'
import { IconCheck, IconSettings, IconUserPlus, IconWebhook } from '@tabler/icons-react'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { UserAvatar } from '@/components/common/UserAvatar'
import { FormattedDate } from '@/components/common/FormattedDate'
import type { TeamDashboardAdminDto, TeamDetailDto, TeamRole } from '@/api/dto'
import { TEAM_ROLE_COLORS } from '@/lib/badgeColors.generated'
import { paths } from '@/config/paths'
import { DashboardSection } from './DashboardSection'
import { OPEN_INVITE_STATE } from './dashboardHelpers'

interface AdminPanelProps {
  team: TeamDetailDto
  admin: TeamDashboardAdminDto
}

/**
 * « Administration », for a team administrator: the members by role (`team.memberCountByRole`),
 * the newest members, the enabled features (the team's own flags) and the webhook's health.
 */
export function AdminPanel({ team, admin }: AdminPanelProps) {
  const { t } = useTranslation()

  return (
    <DashboardSection
      id="dashboard-admin"
      title={t('teams.dashboard.admin.title')}
      framed
      aside={
        <Tooltip label={t('teams.settings.title')}>
          <ActionIcon
            component={PrefetchLink}
            to={paths.teamSettings(team.slug)}
            variant="default"
            size="lg"
            aria-label={t('teams.settings.title')}
          >
            <IconSettings size={18} />
          </ActionIcon>
        </Tooltip>
      }
    >
      <Stack gap="md">
        <RoleSplit team={team} />
        <NewestMembers admin={admin} />
        <Features team={team} />
        <WebhookStatus webhook={admin.webhook} />
        <Group gap="xs">
          {team.addMemberAllowed && (
            <Button
              component={PrefetchLink}
              to={paths.teamAdminMembers(team.slug)}
              state={OPEN_INVITE_STATE}
              size="xs"
              leftSection={<IconUserPlus size={14} />}
            >
              {t('teams.dashboard.admin.invite')}
            </Button>
          )}
          <Button
            component={PrefetchLink}
            to={paths.teamAdminMembers(team.slug)}
            size="xs"
            variant="default"
          >
            {t('teams.dashboard.admin.manageMembers')}
          </Button>
        </Group>
      </Stack>
    </DashboardSection>
  )
}

const ROLES: TeamRole[] = ['ADMIN', 'ORGANIZER', 'MEMBER']

function RoleSplit({ team }: { team: TeamDetailDto }) {
  const { t } = useTranslation()
  const split = team.memberCountByRole
  const counts: Record<TeamRole, number> | undefined = split
    ? { ADMIN: split.admins, ORGANIZER: split.organizers, MEMBER: split.members }
    : undefined
  const total = counts ? counts.ADMIN + counts.ORGANIZER + counts.MEMBER : team.memberCount

  const legend = (role: TeamRole, count: number) => {
    switch (role) {
      case 'ADMIN':
        return t('teams.dashboard.admin.admins', { count })
      case 'ORGANIZER':
        return t('teams.dashboard.admin.organizers', { count })
      case 'MEMBER':
        return t('teams.dashboard.admin.members', { count })
    }
  }

  return (
    <Stack gap="xs">
      <Text fw={700} fz="lg">
        {t('memberCount', { count: team.memberCount })}
      </Text>
      {counts && total > 0 && (
        <>
          <Progress.Root size="lg" aria-label={t('teams.dashboard.admin.roleSplit')}>
            {ROLES.filter((role) => counts[role] > 0).map((role) => (
              <Progress.Section
                key={role}
                value={(counts[role] / total) * 100}
                color={TEAM_ROLE_COLORS[role]}
              />
            ))}
          </Progress.Root>
          <Group gap="md" wrap="wrap">
            {ROLES.map((role) => (
              <Group key={role} gap={6} wrap="nowrap">
                <Box
                  w={10}
                  h={10}
                  style={{
                    borderRadius: '50%',
                    background: `var(--mantine-color-${TEAM_ROLE_COLORS[role]}-filled)`,
                  }}
                />
                <Text size="sm">{legend(role, counts[role])}</Text>
              </Group>
            ))}
          </Group>
        </>
      )}
    </Stack>
  )
}

function NewestMembers({ admin }: { admin: TeamDashboardAdminDto }) {
  const { t } = useTranslation()
  const members = admin.newestMembers.members
  if (members.length === 0) return null
  return (
    <Stack gap="xs">
      <Text size="sm" fw={600}>
        {t('teams.dashboard.admin.newestMembers')}
      </Text>
      {members.map((member) => (
        <Group key={member.id} gap="sm" wrap="nowrap">
          <UserAvatar user={member.user} size="sm" />
          <Text size="sm" style={{ flex: 1, minWidth: 0 }} truncate>
            {member.user.displayName}
          </Text>
          {member.joinedAt && (
            <Text size="xs" c="dimmed">
              <FormattedDate date={member.joinedAt} />
            </Text>
          )}
        </Group>
      ))}
    </Stack>
  )
}

function Features({ team }: { team: TeamDetailDto }) {
  const { t } = useTranslation()
  const features = [
    { on: team.enableRides, label: t('teams.dashboard.admin.feature.rides') },
    { on: team.enableRoutes, label: t('teams.dashboard.admin.feature.routes') },
    { on: team.enableTrips, label: t('teams.dashboard.admin.feature.trips') },
    { on: team.enablePosts, label: t('teams.dashboard.admin.feature.posts') },
    { on: team.enableAds, label: t('teams.dashboard.admin.feature.ads') },
    { on: team.enableMemberDirectory, label: t('teams.dashboard.admin.feature.directory') },
  ].filter((f) => f.on)
  if (features.length === 0) return null
  return (
    <Stack gap="xs">
      <Text size="sm" fw={600}>
        {t('teams.dashboard.admin.features')}
      </Text>
      <Group gap={6} wrap="wrap">
        {features.map((f) => (
          <Badge
            key={f.label}
            color="success"
            variant="light"
            size="sm"
            leftSection={<IconCheck size={12} />}
          >
            {f.label}
          </Badge>
        ))}
      </Group>
    </Stack>
  )
}

function WebhookStatus({ webhook }: { webhook: TeamDashboardAdminDto['webhook'] }) {
  const { t } = useTranslation()

  if (!webhook.configured) {
    return (
      <Group gap="xs" wrap="nowrap">
        <IconWebhook size={18} color="var(--mantine-color-dimmed)" />
        <Text size="sm" c="dimmed">
          {t('teams.dashboard.admin.webhookNone')}
        </Text>
      </Group>
    )
  }

  const kind = webhook.kind
    ? t(
        `teams.settings.webhook.kind.${webhook.kind satisfies 'SLACK' | 'DISCORD' | 'MATTERMOST' | 'GENERIC'}`
      )
    : undefined
  const failed = webhook.lastStatus === 'FAILED'

  return (
    <Group gap="xs" wrap="nowrap" align="flex-start">
      <IconWebhook size={18} style={{ flexShrink: 0, marginTop: 2 }} />
      <Text size="sm" style={{ flex: 1, minWidth: 0 }}>
        {kind ? t('teams.dashboard.admin.webhook', { kind }) : t('teams.settings.webhook.title')}
        {webhook.lastAttemptAt && (
          <Text span size="sm" c="dimmed">
            {' · '}
            {t('teams.dashboard.admin.webhookLastAttempt')}{' '}
            <FormattedDate date={webhook.lastAttemptAt} />
          </Text>
        )}
      </Text>
      {!webhook.enabled ? (
        <Badge color="gray" variant="light" size="sm">
          {t('teams.dashboard.admin.webhookDisabled')}
        </Badge>
      ) : failed ? (
        <Badge color="danger" variant="light" size="sm">
          {t('teams.settings.webhook.status.FAILED')}
        </Badge>
      ) : (
        <Badge color="success" variant="light" size="sm">
          {t('teams.dashboard.admin.webhookActive')}
        </Badge>
      )}
    </Group>
  )
}
