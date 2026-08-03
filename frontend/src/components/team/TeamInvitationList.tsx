import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { ActionIcon, Badge, Box, Group, Table, Text, Title, Tooltip } from '@mantine/core'
import { IconTrash, IconSend } from '@tabler/icons-react'
import {
  useListInvitations,
  useRevoke,
  useInvite,
  getListInvitationsQueryKey,
} from '@/api/endpoints/team-invitations/team-invitations'
import { InvitationStatus, type TeamInvitationDto } from '@/api/dto'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { FormattedDate } from '../common/FormattedDate'

interface TeamInvitationListProps {
  teamSlug: string
}

/**
 * The invitations a team has sent and nobody has answered yet.
 *
 * This is not decoration. Creating an invitation deliberately cannot tell the administrator whether
 * the address is known — answering differently would make it an account-existence probe — so a
 * mistyped address produces a perfectly ordinary-looking invitation that will never be accepted.
 * This list is where that typo becomes visible, and the revoke button is how it gets undone.
 */
export function TeamInvitationList({ teamSlug }: TeamInvitationListProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const [toRevoke, setToRevoke] = useState<TeamInvitationDto | null>(null)

  const { data } = useListInvitations(teamSlug, { status: InvitationStatus.PENDING })
  const revokeMutation = useRevoke()
  const inviteMutation = useInvite()

  const invitations = data?.invitations ?? []

  // Hidden entirely when empty: most teams never invite anyone, and an empty table on every visit
  // to the members screen is weight for nothing.
  if (invitations.length === 0) {
    return null
  }

  const invalidate = () =>
    queryClient.invalidateQueries({ queryKey: getListInvitationsQueryKey(teamSlug) })

  const handleRevoke = () => {
    if (!toRevoke) return
    revokeMutation.mutate(
      { teamSlug, invitationId: toRevoke.id },
      {
        onSuccess: () => {
          invalidate()
          notifications.show({
            message: i18next.t('teams.invitations.revoked'),
            color: 'green',
          })
          setToRevoke(null)
        },
      }
    )
  }

  // Re-inviting the same address is the resend: the server revokes the live invitation and issues a
  // fresh token. One intention, one path — no separate resend endpoint to keep in step.
  const handleResend = (invitation: TeamInvitationDto) => {
    inviteMutation.mutate(
      { teamSlug, data: { email: invitation.email, role: invitation.role } },
      {
        onSuccess: () => {
          invalidate()
          notifications.show({
            message: i18next.t('teams.invitations.sent', { email: invitation.email }),
            color: 'green',
          })
        },
      }
    )
  }

  return (
    <Box>
      <Title order={3} mb="sm">
        {t('teams.invitations.pendingTitle')}
      </Title>
      <Text size="sm" c="dimmed" mb="md">
        {t('teams.invitations.pendingHelp')}
      </Text>

      <Table.ScrollContainer minWidth={520}>
        <Table verticalSpacing="sm">
          <Table.Thead>
            <Table.Tr>
              <Table.Th>{t('teams.invitations.email')}</Table.Th>
              <Table.Th>{t('teams.detail.members.role')}</Table.Th>
              <Table.Th>{t('teams.invitations.sentBy')}</Table.Th>
              <Table.Th>{t('teams.invitations.expiresAt')}</Table.Th>
              <Table.Th />
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {invitations.map((invitation) => (
              <Table.Tr key={invitation.id}>
                <Table.Td>{invitation.email}</Table.Td>
                <Table.Td>
                  <Badge variant="light">{t(`roles.${invitation.role}`)}</Badge>
                </Table.Td>
                <Table.Td>{invitation.invitedBy.displayName}</Table.Td>
                <Table.Td>
                  <FormattedDate date={invitation.expiresAt} />
                </Table.Td>
                <Table.Td>
                  <Group gap="xs" justify="flex-end" wrap="nowrap">
                    <Tooltip label={t('teams.invitations.resend')}>
                      <ActionIcon
                        variant="subtle"
                        onClick={() => handleResend(invitation)}
                        loading={inviteMutation.isPending}
                        aria-label={t('teams.invitations.resend')}
                      >
                        <IconSend size={16} />
                      </ActionIcon>
                    </Tooltip>
                    <Tooltip label={t('teams.invitations.revoke')}>
                      <ActionIcon
                        variant="subtle"
                        color="red"
                        onClick={() => setToRevoke(invitation)}
                        aria-label={t('teams.invitations.revoke')}
                      >
                        <IconTrash size={16} />
                      </ActionIcon>
                    </Tooltip>
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </Table.ScrollContainer>

      <ConfirmDialog
        isOpen={toRevoke !== null}
        onClose={() => setToRevoke(null)}
        onConfirm={handleRevoke}
        title={t('teams.invitations.revoke')}
        message={t('teams.invitations.revokeConfirm', { email: toRevoke?.email ?? '' })}
        confirmText={t('teams.invitations.revoke')}
        isLoading={revokeMutation.isPending}
        variant="danger"
      />
    </Box>
  )
}
