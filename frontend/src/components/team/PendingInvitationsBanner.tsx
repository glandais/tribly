import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { Alert, Button, Group, Stack, Text } from '@mantine/core'
import { IconMailOpened } from '@tabler/icons-react'
import {
  useListMyInvitations,
  useAcceptMyInvitation,
  getListMyInvitationsQueryKey,
} from '@/api/endpoints/invitations/invitations'
import { getListTeamsQueryKey } from '@/api/endpoints/teams/teams'
import { useAuthStore } from '@/store/authStore'

/**
 * "You have invitations waiting."
 *
 * This is the only thing that catches someone invited before they had an account who then signs up
 * through the ordinary form rather than by clicking the link in the e-mail. Nothing enrols them
 * automatically — signing up is not joining a team — so without this banner their invitations would
 * simply sit in the database until they lapsed.
 */
export function PendingInvitationsBanner() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)

  const { data } = useListMyInvitations({ query: { enabled: isAuthenticated } })
  const acceptMutation = useAcceptMyInvitation()

  const invitations = data?.invitations ?? []
  if (invitations.length === 0) {
    return null
  }

  const handleAccept = (invitationId: string, teamName: string) => {
    acceptMutation.mutate(
      { invitationId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListMyInvitationsQueryKey() })
          queryClient.invalidateQueries({ queryKey: getListTeamsQueryKey() })
          notifications.show({
            message: i18next.t('invitations.banner.joined', { team: teamName }),
            color: 'green',
          })
        },
      }
    )
  }

  return (
    <Alert icon={<IconMailOpened size={18} />} color="primary" mb="lg">
      <Stack gap="sm">
        <Text fw={500}>{t('invitations.banner.title', { count: invitations.length })}</Text>
        {invitations.map((invitation) => (
          <Group key={invitation.id} justify="space-between" wrap="wrap">
            <Text size="sm">
              {t('invitations.banner.line', {
                inviter: invitation.inviterName,
                team: invitation.team.name,
              })}
            </Text>
            <Button
              size="compact-sm"
              loading={acceptMutation.isPending}
              onClick={() => handleAccept(invitation.id, invitation.team.name)}
            >
              {t('invitations.banner.accept')}
            </Button>
          </Group>
        ))}
      </Stack>
    </Alert>
  )
}
