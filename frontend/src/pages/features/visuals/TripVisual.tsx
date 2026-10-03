import { useTranslation } from 'react-i18next'
import { Box, Group, Paper, Stack, Text, Timeline } from '@mantine/core'
import { PUBLICATION_TYPE_COLORS } from '@/lib/badgeColors.generated'
import { TypeBadge } from '@/components/card/common'
import { VisualFrame } from './VisualFrame'
import { ElevationSketch } from './RouteSketch'

/** A trip and its dated stages, one route each. */
export function TripVisual() {
  const { t } = useTranslation()
  const stages = [
    {
      label: t('features.demo.trip.stage1Label'),
      title: t('features.demo.trip.stage1Title'),
      stats: t('features.demo.trip.stage1Stats'),
    },
    {
      label: t('features.demo.trip.stage2Label'),
      title: t('features.demo.trip.stage2Title'),
      stats: t('features.demo.trip.stage2Stats'),
    },
    {
      label: t('features.demo.trip.stage3Label'),
      title: t('features.demo.trip.stage3Title'),
      stats: t('features.demo.trip.stage3Stats'),
    },
    {
      label: t('features.demo.trip.stage4Label'),
      title: t('features.demo.trip.stage4Title'),
      stats: t('features.demo.trip.stage4Stats'),
    },
  ]
  const color = PUBLICATION_TYPE_COLORS.TRIP

  return (
    <VisualFrame color={color}>
      <Stack gap="md">
        <Group justify="space-between">
          <Text fw={700}>{t('features.demo.trip.title')}</Text>
          <TypeBadge type="TRIP">{t('publicationType.trip')}</TypeBadge>
        </Group>
        <Timeline active={stages.length - 1} color={color} bulletSize={28} lineWidth={2}>
          {stages.map((stage, index) => (
            <Timeline.Item
              key={stage.title}
              bullet={
                <Text size="sm" fw={700} c="white">
                  {index + 1}
                </Text>
              }
            >
              <Paper withBorder radius="md" px="sm" py={8}>
                <Group gap="sm" wrap="nowrap">
                  <Stack gap={0} style={{ flex: 1, minWidth: 0 }}>
                    <Text size="xs" c="dimmed">
                      {stage.label}
                    </Text>
                    <Text fw={600} truncate>
                      {stage.title}
                    </Text>
                    <Text size="sm" c="dimmed">
                      {stage.stats}
                    </Text>
                  </Stack>
                  <Box w={64} style={{ flex: 'none' }}>
                    <ElevationSketch height={28} />
                  </Box>
                </Group>
              </Paper>
            </Timeline.Item>
          ))}
        </Timeline>
      </Stack>
    </VisualFrame>
  )
}
