import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Stack, Title, Text, Button, Group, Paper, ActionIcon, Badge } from '@mantine/core'
import { IconLink, IconUnlink, IconDevices } from '@tabler/icons-react'
import { useGpsConnections } from '@/hooks/useGpsConnections'
import { ConfirmDialog } from '../common/ConfirmDialog'
import type { GpsServiceType } from '@/api/dto'

// Available GPS services
const GPS_SERVICES: { type: GpsServiceType; icon: React.ReactNode }[] = [
  { type: 'HAMMERHEAD', icon: <IconDevices size={20} /> },
  { type: 'GARMIN', icon: <IconDevices size={20} /> },
]

export function GpsConnectionsManager() {
  const { t } = useTranslation()
  const { isConnected, getConnection, initiateConnect, disconnect, isDisconnecting } =
    useGpsConnections()

  const [disconnectServiceType, setDisconnectServiceType] = useState<GpsServiceType | null>(null)

  const handleConnect = (serviceType: GpsServiceType) => {
    initiateConnect(serviceType)
  }

  const handleDisconnect = () => {
    if (disconnectServiceType) {
      disconnect(disconnectServiceType, {
        onSuccess: () => setDisconnectServiceType(null),
      })
    }
  }

  return (
    <Stack>
      <Group justify="space-between">
        <Title order={3} size="h5">
          {t('gps.title')}
        </Title>
      </Group>

      <Text size="sm" c="dimmed">
        {t('gps.description')}
      </Text>

      <Stack gap="xs">
        {GPS_SERVICES.map(({ type, icon }) => {
          const connected = isConnected(type)
          const connection = getConnection(type)

          return (
            <Paper key={type} withBorder p="sm">
              <Group justify="space-between">
                <Group>
                  {icon}
                  <div>
                    <Text size="sm" fw={500}>
                      {t(`gps.services.${type.toLowerCase() as 'hammerhead' | 'garmin'}`)}
                    </Text>
                    {connected && connection && (
                      <Text size="xs" c="dimmed">
                        {t('gps.connectedSince', {
                          date: new Date(connection.connectedAt).toLocaleDateString(),
                        })}
                      </Text>
                    )}
                  </div>
                </Group>
                <Group gap="xs">
                  {connected ? (
                    <>
                      <Badge color="green" variant="light">
                        {t('gps.connected')}
                      </Badge>
                      <ActionIcon
                        variant="subtle"
                        color="red"
                        onClick={() => setDisconnectServiceType(type)}
                        title={t('gps.disconnect')}
                      >
                        <IconUnlink size={16} />
                      </ActionIcon>
                    </>
                  ) : (
                    <Button
                      size="xs"
                      leftSection={<IconLink size={14} />}
                      onClick={() => handleConnect(type)}
                    >
                      {t('gps.connect')}
                    </Button>
                  )}
                </Group>
              </Group>
            </Paper>
          )
        })}
      </Stack>

      <ConfirmDialog
        isOpen={disconnectServiceType !== null}
        onClose={() => setDisconnectServiceType(null)}
        onConfirm={handleDisconnect}
        title={t('gps.disconnectConfirm.title')}
        message={t('gps.disconnectConfirm.message', {
          service: disconnectServiceType
            ? t(`gps.services.${disconnectServiceType.toLowerCase() as 'hammerhead' | 'garmin'}`)
            : '',
        })}
        confirmText={t('gps.disconnectConfirm.confirm')}
        variant="danger"
        isLoading={isDisconnecting}
      />
    </Stack>
  )
}
