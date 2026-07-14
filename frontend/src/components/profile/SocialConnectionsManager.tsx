import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { Stack, Title, Text, Button, Group, Paper, ActionIcon, Badge } from '@mantine/core'
import { IconBrandStrava, IconUnlink } from '@tabler/icons-react'
import { useAuth } from '@/hooks/useAuth'
import { getGetMeQueryKey } from '@/api/endpoints/users/users'
import {
  getStravaConnectUrl,
  unlinkStrava,
} from '@/api/endpoints/strava-authentication/strava-authentication'
import { ConfirmDialog } from '../common/ConfirmDialog'

/** Manage external identities (Strava) linked to the current account, in the security hub. */
export function SocialConnectionsManager() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const { user, refetchUser } = useAuth()

  const [isConnecting, setIsConnecting] = useState(false)
  const [isUnlinking, setIsUnlinking] = useState(false)
  const [confirmUnlink, setConfirmUnlink] = useState(false)

  const stravaLinked = (user?.socialIdentities ?? []).some((s) => s.provider === 'STRAVA')

  const handleConnect = async () => {
    setIsConnecting(true)
    try {
      const data = await getStravaConnectUrl()
      window.location.href = data.authorizationUrl
    } catch {
      notifications.show({ message: t('auth.strava.errors.startFailed'), color: 'red' })
      setIsConnecting(false)
    }
  }

  const handleUnlink = async () => {
    setIsUnlinking(true)
    try {
      await unlinkStrava()
      queryClient.invalidateQueries({ queryKey: getGetMeQueryKey() })
      refetchUser()
      notifications.show({ message: t('profile.social.unlinked'), color: 'green' })
      setConfirmUnlink(false)
    } catch {
      notifications.show({ message: t('profile.social.unlinkFailed'), color: 'red' })
    } finally {
      setIsUnlinking(false)
    }
  }

  return (
    <Stack>
      <Title order={3} size="h5">
        {t('profile.social.title')}
      </Title>
      <Text size="sm" c="dimmed">
        {t('profile.social.description')}
      </Text>

      <Paper withBorder p="sm">
        <Group justify="space-between">
          <Group>
            <IconBrandStrava size={20} color="var(--mantine-color-orange-6)" />
            <Text size="sm" fw={500}>
              {t('profile.social.strava')}
            </Text>
          </Group>
          <Group gap="xs">
            {stravaLinked ? (
              <>
                <Badge color="green" variant="light">
                  {t('profile.social.linked')}
                </Badge>
                <ActionIcon
                  variant="subtle"
                  color="red"
                  onClick={() => setConfirmUnlink(true)}
                  title={t('profile.social.unlink')}
                >
                  <IconUnlink size={16} />
                </ActionIcon>
              </>
            ) : (
              <Button
                size="xs"
                color="orange"
                leftSection={<IconBrandStrava size={14} />}
                onClick={handleConnect}
                loading={isConnecting}
              >
                {t('profile.social.link')}
              </Button>
            )}
          </Group>
        </Group>
      </Paper>

      <ConfirmDialog
        isOpen={confirmUnlink}
        onClose={() => setConfirmUnlink(false)}
        onConfirm={handleUnlink}
        title={t('profile.social.unlinkConfirm.title')}
        message={t('profile.social.unlinkConfirm.message')}
        confirmText={t('profile.social.unlink')}
        variant="danger"
        isLoading={isUnlinking}
      />
    </Stack>
  )
}
