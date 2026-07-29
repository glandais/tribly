import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import {
  Stack,
  Title,
  Text,
  Button,
  Group,
  Paper,
  ActionIcon,
  Badge,
  Skeleton,
} from '@mantine/core'
import { IconLink, IconUnlink, IconDevices } from '@tabler/icons-react'
import { useGpsConnections } from '@/hooks/useGpsConnections'
import { ConfirmDialog } from '../common/ConfirmDialog'
import type { GpsServiceType } from '@/api/dto'

// All known GPS services with their icons
const GPS_SERVICE_ICONS: Record<GpsServiceType, React.ReactNode> = {
  HAMMERHEAD: <IconDevices size={20} />,
  GARMIN: <IconDevices size={20} />,
  WAHOO: <IconDevices size={20} />,
}

export function GpsConnectionsManager() {
  const { t } = useTranslation()
  const {
    availableServices,
    isLoadingAvailable,
    isConnected,
    getConnection,
    initiateConnect,
    disconnect,
    isDisconnecting,
  } = useGpsConnections()

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
        {isLoadingAvailable ? (
          <>
            <Skeleton height={60} radius="sm" />
            <Skeleton height={60} radius="sm" />
          </>
        ) : availableServices.length === 0 ? (
          <Text size="sm" c="dimmed" ta="center" py="md">
            {t('gps.noServicesConfigured')}
          </Text>
        ) : (
          availableServices.map((type) => {
            const icon = GPS_SERVICE_ICONS[type]
            const connected = isConnected(type)
            const connection = getConnection(type)

            return (
              <Paper key={type} withBorder p="sm">
                <Group justify="space-between">
                  <Group>
                    {icon}
                    <div>
                      <Text size="sm" fw={500}>
                        {t(`gps.services.${type.toLowerCase() as Lowercase<GpsServiceType>}`)}
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
                          aria-label={t('gps.disconnect')}
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
          })
        )}
      </Stack>

      <ConfirmDialog
        isOpen={disconnectServiceType !== null}
        onClose={() => setDisconnectServiceType(null)}
        onConfirm={handleDisconnect}
        title={t('gps.disconnectConfirm.title')}
        message={t('gps.disconnectConfirm.message', {
          service: disconnectServiceType
            ? t(`gps.services.${disconnectServiceType.toLowerCase() as Lowercase<GpsServiceType>}`)
            : '',
        })}
        confirmText={t('gps.disconnectConfirm.confirm')}
        variant="danger"
        isLoading={isDisconnecting}
      />
    </Stack>
  )
}
