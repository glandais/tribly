import { useTranslation } from 'react-i18next'
import { Button, Menu } from '@mantine/core'
import { IconDeviceWatch } from '@tabler/icons-react'
import type { GpsServiceType } from '@/api/dto'
import { useGpsConnections } from '@/hooks/useGpsConnections'

interface SendToDeviceMenuProps {
  teamSlug: string
  routeSlug: string
}

/** « Envoyer vers l'appareil »: the route to one of the GPS services the user connected. */
export function SendToDeviceMenu({ teamSlug, routeSlug }: SendToDeviceMenuProps) {
  const { t } = useTranslation()
  const { connectedServices, uploadRoute, isUploading } = useGpsConnections()

  if (connectedServices.length === 0) return null

  return (
    <Menu shadow="md" width={200}>
      <Menu.Target>
        <Button
          variant="default"
          size="xs"
          loading={isUploading}
          leftSection={<IconDeviceWatch size={14} />}
        >
          {t('routes.detail.sendToDevice')}
        </Button>
      </Menu.Target>
      <Menu.Dropdown>
        {connectedServices.map((service) => (
          <Menu.Item
            key={service.serviceType}
            onClick={() => uploadRoute({ serviceType: service.serviceType, teamSlug, routeSlug })}
          >
            {t(`gps.services.${service.serviceType.toLowerCase() as Lowercase<GpsServiceType>}`)}
          </Menu.Item>
        ))}
      </Menu.Dropdown>
    </Menu>
  )
}
