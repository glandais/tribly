import { useEffect, useRef, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { notifications } from '@mantine/notifications'
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
  Image,
} from '@mantine/core'
import { IconLink, IconUnlink } from '@tabler/icons-react'
import { useGpsConnections } from '@/hooks/useGpsConnections'
import { ConfirmDialog } from '../common/ConfirmDialog'
import type { GpsServiceType } from '@/api/dto'
import { useFormattedDate } from '@/utils/dateFormat'
import { GPS_SECTION_ANCHOR } from './profileAnchors'

import garminLogo from '@/assets/gps/garmin.svg'
import hammerheadLogo from '@/assets/gps/hammerhead.svg'
import wahooLogo from '@/assets/gps/wahoo.svg'

// The logo of each service, shipped with the client: `GpsServiceType` is a closed enum, so the
// contract carries no logo URL (docs/LEDGER_*.md API-14). Each is a square tile with its own dark
// background, readable in both colour schemes; the mobile app ships the same tiles as PNGs.
const GPS_SERVICE_LOGOS: Record<GpsServiceType, string> = {
  HAMMERHEAD: hammerheadLogo,
  GARMIN: garminLogo,
  WAHOO: wahooLogo,
}

export function GpsConnectionsManager() {
  const { t } = useTranslation()
  const { formatDate } = useFormattedDate()
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

  // The OAuth callback lands here with its outcome in the query (docs/LEDGER_*.md API-63): say it
  // once, then drop it so a reload does not say it again.
  const [searchParams, setSearchParams] = useSearchParams()
  useEffect(() => {
    const connected = searchParams.get('gps_connected')
    const error = searchParams.get('gps_error')
    if (connected === null && error === null) return
    const service = connected ? t(`gps.services.${connected}`, { defaultValue: connected }) : ''
    notifications.show(
      connected
        ? { message: t('gps.notifications.connected', { service }), color: 'green' }
        : {
            message: t(
              error === 'access_denied'
                ? 'gps.notifications.returnDenied'
                : 'gps.notifications.returnFailed'
            ),
            color: 'red',
          }
    )
    const next = new URLSearchParams(searchParams)
    next.delete('gps_connected')
    next.delete('gps_error')
    setSearchParams(next, { replace: true })
  }, [searchParams, setSearchParams, t])

  // The profile renders after its own query, by which time the browser has given up on the
  // fragment: scroll to the section ourselves, once (same as NotificationPreferences).
  const scrolled = useRef(false)
  useEffect(() => {
    if (scrolled.current || window.location.hash !== `#${GPS_SECTION_ANCHOR}`) return
    scrolled.current = true
    document
      .getElementById(GPS_SECTION_ANCHOR)
      ?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }, [])

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
    <Stack
      id={GPS_SECTION_ANCHOR}
      style={{
        scrollMarginTop: 'calc(var(--app-shell-header-height, 60px) + var(--mantine-spacing-md))',
      }}
    >
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
            const connected = isConnected(type)
            const connection = getConnection(type)

            return (
              <Paper key={type} withBorder p="sm">
                <Group justify="space-between">
                  <Group>
                    {/* Decorative: the service's name is the text right beside it */}
                    <Image src={GPS_SERVICE_LOGOS[type]} alt="" w={32} h={32} radius="sm" />
                    <div>
                      <Text size="sm" fw={500}>
                        {t(`gps.services.${type.toLowerCase() as Lowercase<GpsServiceType>}`)}
                      </Text>
                      {connected && connection && (
                        <Text size="xs" c="dimmed">
                          {t('gps.connectedSince', {
                            date: formatDate(connection.connectedAt),
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
