import { useTranslation } from 'react-i18next'
import { Box, Group, Paper, Stack, Text, Title } from '@mantine/core'
import { IconMail, IconShieldLock, IconCircleDashed } from '@tabler/icons-react'
import { TypeBadge } from '@/components/card/common'
import { VisualFrame } from './VisualFrame'
import { DemoButton } from './DemoButton'

/**
 * An ad: its position drawn as a blurred **sector**, never a pin (the ads invariant of the root
 * CLAUDE.md), and the contact through the e-mail relay — no address shown.
 */
export function AdVisual() {
  const { t } = useTranslation()

  return (
    <VisualFrame color="orange">
      <Stack gap="sm">
        <Box
          pos="relative"
          h={180}
          style={{
            borderRadius: 'var(--mantine-radius-lg)',
            overflow: 'hidden',
            backgroundColor: 'var(--mantine-color-body)',
            border: '1px solid var(--mantine-color-default-border)',
          }}
        >
          {/* A schematic street plan, no tiles: the sector is what matters. */}
          <svg
            viewBox="0 0 400 180"
            width="100%"
            height="100%"
            preserveAspectRatio="xMidYMid slice"
            style={{ display: 'block', color: 'var(--mantine-color-default-border)' }}
          >
            <g stroke="currentColor" strokeWidth={6} fill="none" strokeLinecap="round">
              <path d="M-10 40 C 90 60, 160 20, 410 70" />
              <path d="M-10 140 C 120 120, 260 160, 410 120" />
              <path d="M120 -10 C 110 60, 150 120, 130 190" />
              <path d="M290 -10 C 270 70, 300 110, 280 190" />
            </g>
            <g stroke="currentColor" strokeWidth={2} fill="none">
              <path d="M-10 95 L 410 85" />
              <path d="M200 -10 L 215 190" />
            </g>
          </svg>
          <Box
            pos="absolute"
            top="50%"
            left="50%"
            w={120}
            h={120}
            style={{
              transform: 'translate(-50%, -50%)',
              borderRadius: '50%',
              backgroundColor: 'var(--mantine-color-orange-light)',
              border: '2px dashed var(--mantine-color-orange-filled)',
            }}
          />
          <Group
            pos="absolute"
            left={12}
            bottom={12}
            gap={6}
            px="sm"
            py={4}
            wrap="nowrap"
            style={{
              borderRadius: 'var(--mantine-radius-xl)',
              backgroundColor: 'var(--mantine-color-body)',
              border: '1px solid var(--mantine-color-default-border)',
            }}
          >
            <IconCircleDashed size={14} />
            <Text size="xs" fw={500}>
              {t('features.demo.ad.blurred')}
            </Text>
          </Group>
        </Box>
        <Paper withBorder radius="lg" p="md">
          <Stack gap="xs">
            <Group justify="space-between" align="flex-start" wrap="nowrap" gap="sm">
              <Title order={3} size="h4">
                {t('features.demo.ad.title')}
              </Title>
              <Box style={{ flex: 'none' }}>
                <TypeBadge type="SALE">{t('ads.adType.SALE')}</TypeBadge>
              </Box>
            </Group>
            <Text size="xl" fw={600}>
              {t('features.demo.ad.price')}
            </Text>
            <DemoButton size="sm" leftSection={<IconMail size={16} />}>
              {t('ads.contact.button')}
            </DemoButton>
            <Group gap={6} wrap="nowrap" align="flex-start">
              <IconShieldLock size={14} style={{ flex: 'none', marginTop: 2 }} />
              <Text size="xs" c="dimmed">
                {t('features.demo.ad.relay')}
              </Text>
            </Group>
          </Stack>
        </Paper>
      </Stack>
    </VisualFrame>
  )
}
