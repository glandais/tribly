import { useTranslation } from 'react-i18next'
import { Box, Flex, Group, Paper, Text, ThemeIcon, Title } from '@mantine/core'
import { IconDeviceMobile, IconDeviceWatch, type IconProps } from '@tabler/icons-react'
import type { ComponentType } from 'react'

type DeviceKey = 'karoo' | 'garmin' | 'wahoo' | 'phone'

const DEVICES: { key: DeviceKey; icon: ComponentType<IconProps> }[] = [
  { key: 'karoo', icon: IconDeviceWatch },
  { key: 'garmin', icon: IconDeviceWatch },
  { key: 'wahoo', icon: IconDeviceWatch },
  { key: 'phone', icon: IconDeviceMobile },
]

/**
 * « Du site à votre compteur »: the bike computers a route can be sent to, listed alike — no
 * availability note, and no word on how each one receives it.
 */
export function DevicesStrip() {
  const { t } = useTranslation()

  return (
    <Paper
      withBorder
      radius="lg"
      p={{ base: 'md', sm: 'xl' }}
      component="section"
      aria-labelledby="home-devices-title"
    >
      <Flex direction={{ base: 'column', md: 'row' }} gap="lg" align={{ md: 'center' }}>
        <Box style={{ flex: 1 }}>
          <Title id="home-devices-title" order={2}>
            {t('home.visitor.devices.title')}
          </Title>
          <Text c="dimmed" mt={4} maw={560}>
            {t('home.visitor.devices.text')}
          </Text>
        </Box>
        <Group gap="sm">
          {DEVICES.map(({ key, icon: Icon }) => (
            <Paper key={key} withBorder radius="xl" px="md" py={6}>
              <Group gap="xs" wrap="nowrap">
                <ThemeIcon variant="light" color="gray" radius="xl" size={26}>
                  <Icon size={16} />
                </ThemeIcon>
                <Text size="sm" fw={500}>
                  {t(`home.visitor.devices.${key}`)}
                </Text>
              </Group>
            </Paper>
          ))}
        </Group>
      </Flex>
    </Paper>
  )
}
