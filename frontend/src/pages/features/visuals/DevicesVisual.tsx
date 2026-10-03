import { useTranslation } from 'react-i18next'
import { Box, Group, Stack, Text, ThemeIcon } from '@mantine/core'
import { IconDeviceWatch } from '@tabler/icons-react'
import { VisualFrame } from './VisualFrame'
import { DemoButton } from './DemoButton'
import { RouteTrace } from './RouteSketch'

/**
 * A bike computer showing the route, the « Envoyer vers l'appareil » button, and the supported
 * devices — listed alike, with no availability note and no word on how each one receives the route.
 */
export function DevicesVisual() {
  const { t } = useTranslation()
  const devices = [
    t('features.devices.karoo'),
    t('features.devices.garmin'),
    t('features.devices.wahoo'),
  ]

  return (
    <VisualFrame color="gray">
      <Group gap="lg" justify="center" align="center">
        {/* The device stays dark in both colour schemes: it is a screen, not a surface. */}
        <Stack
          w={170}
          h={250}
          p={14}
          gap={8}
          style={{
            flex: 'none',
            borderRadius: 24,
            backgroundColor: 'var(--mantine-color-dark-7)',
            border: '2px solid var(--mantine-color-dark-4)',
          }}
        >
          <Box
            style={{
              flex: 1,
              borderRadius: 10,
              overflow: 'hidden',
              display: 'flex',
              alignItems: 'center',
              backgroundColor: 'var(--mantine-color-dark-5)',
            }}
          >
            <RouteTrace height={140} stroke="var(--mantine-color-blue-4)" />
          </Box>
          <Group justify="space-between" c="white">
            <Stack gap={0}>
              <Text size="10px" c="gray.5">
                {t('features.demo.device.distanceLabel')}
              </Text>
              <Text size="xs">{t('features.demo.device.distance')}</Text>
            </Stack>
            <Stack gap={0} align="flex-end">
              <Text size="10px" c="gray.5">
                {t('features.demo.device.elevationLabel')}
              </Text>
              <Text size="xs">{t('features.demo.device.elevation')}</Text>
            </Stack>
          </Group>
        </Stack>
        <Stack gap="sm" style={{ flex: '1 1 200px' }}>
          <DemoButton size="sm" leftSection={<IconDeviceWatch size={16} />}>
            {t('routes.detail.sendToDevice')}
          </DemoButton>
          {devices.map((device) => (
            <Group key={device} gap="sm" wrap="nowrap">
              <ThemeIcon variant="light" size={28} radius="md">
                <IconDeviceWatch size={16} />
              </ThemeIcon>
              <Text size="sm">{device}</Text>
            </Group>
          ))}
        </Stack>
      </Group>
    </VisualFrame>
  )
}
