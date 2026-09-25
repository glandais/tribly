import { useTranslation } from 'react-i18next'
import { Alert, Anchor, Group, List, Loader, Stack, Text } from '@mantine/core'
import { IconAlertTriangle } from '@tabler/icons-react'
import type { AccountDeletionImpactDto } from '@/api/dto'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { paths } from '@/config/paths'

interface AccountDeletionImpactProps {
  impact: AccountDeletionImpactDto | undefined
  isLoading: boolean
}

/**
 * The body of the account-deletion confirmation: what the deletion does to the member's teams,
 * said before they confirm. A blocked deletion names the teams that block it, and links to their
 * members, where another admin is named; the teams deleted with the account are named too.
 *
 * Without an answer (the request failed) it falls back to the generic warning: the server still
 * refuses a blocked deletion.
 */
export function AccountDeletionImpact({ impact, isLoading }: AccountDeletionImpactProps) {
  const { t } = useTranslation()

  if (isLoading && !impact) {
    return (
      <Group gap="xs">
        <Loader size="xs" />
        <Text size="sm" c="dimmed">
          {t('profile.account.dangerZone.impactLoading')}
        </Text>
      </Group>
    )
  }

  if (impact?.blocked) {
    return (
      <Alert color="danger" icon={<IconAlertTriangle size={18} />}>
        <Stack gap="xs">
          <Text size="sm">
            {t('profile.account.dangerZone.blocked', { count: impact.blockingTeams.length })}
          </Text>
          <List size="sm">
            {impact.blockingTeams.map((team) => (
              <List.Item key={team.id}>
                <Anchor component={PrefetchLink} to={paths.teamAdminMembers(team.slug)} size="sm">
                  {team.name}
                </Anchor>
              </List.Item>
            ))}
          </List>
          <Text size="sm">{t('profile.account.dangerZone.blockedHint')}</Text>
        </Stack>
      </Alert>
    )
  }

  const deletedTeams = impact?.deletedTeams ?? []
  return (
    <Stack gap="sm">
      {deletedTeams.length > 0 && (
        <Alert color="warning" icon={<IconAlertTriangle size={18} />}>
          <Stack gap="xs">
            <Text size="sm">
              {t('profile.account.dangerZone.teamsDeleted', { count: deletedTeams.length })}
            </Text>
            <List size="sm">
              {deletedTeams.map((team) => (
                <List.Item key={team.id}>{team.name}</List.Item>
              ))}
            </List>
          </Stack>
        </Alert>
      )}
      <Text c="dimmed">{t('profile.account.dangerZone.confirmMessage')}</Text>
    </Stack>
  )
}
