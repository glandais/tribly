import { useTranslation } from 'react-i18next'
import { Badge as MantineBadge, Box, Group, Paper, Stack, Text, Title } from '@mantine/core'
import {
  IconArrowUp,
  IconDeviceWatch,
  IconDownload,
  IconMap,
  IconMountain,
} from '@tabler/icons-react'
import { BADGE_VARIANTS, CLIMB_CATEGORY_COLORS } from '@/lib/badgeColors.generated'
import { Stat, SurfaceBadge, VisibilityBadge } from '@/components/card/common'
import { VisualFrame } from './VisualFrame'
import { DemoButton } from './DemoButton'
import { ElevationSketch, RouteTrace } from './RouteSketch'

/** A route page in miniature: trace, figures, gradient-coloured profile, climbs and exports. */
export function RouteVisual() {
  const { t } = useTranslation()
  const climbs = [
    {
      category: 'CAT2' as const,
      name: t('features.demo.route.climb1'),
      stats: t('features.demo.route.climb1Stats'),
    },
    {
      category: 'CAT3' as const,
      name: t('features.demo.route.climb2'),
      stats: t('features.demo.route.climb2Stats'),
    },
  ]

  return (
    <VisualFrame color="orange">
      <Paper withBorder radius="lg" style={{ overflow: 'hidden' }}>
        <Box bg="var(--mantine-color-default-hover)" p="xs">
          <RouteTrace height={110} />
        </Box>
        <Stack gap="sm" p="md">
          <Group justify="space-between" align="flex-start" wrap="nowrap" gap="sm">
            <Title order={3} size="h4">
              {t('features.demo.route.title')}
            </Title>
            <Group gap={4} wrap="nowrap" style={{ flex: 'none' }}>
              <SurfaceBadge surface="GRAVEL">{t('routes.surfaceType.GRAVEL')}</SurfaceBadge>
              <VisibilityBadge visibility="PUBLIC" showIcon={false} />
            </Group>
          </Group>
          <Group gap="md">
            <Stat icon={<IconMap size={16} />}>{t('features.demo.route.distance')}</Stat>
            <Stat icon={<IconArrowUp size={16} />}>{t('features.demo.route.elevation')}</Stat>
            <Stat icon={<IconMountain size={16} />}>
              {t('routes.list.filters.hilliness.MOUNTAINOUS')}
            </Stat>
          </Group>
          <Box
            p={6}
            pb={0}
            style={{
              borderRadius: 'var(--mantine-radius-md)',
              backgroundColor: 'var(--mantine-color-default-hover)',
            }}
          >
            <ElevationSketch />
          </Box>
          <Stack gap={6}>
            <Text size="sm" fw={700}>
              {t('routes.detail.climbs.title')}
            </Text>
            {climbs.map((climb) => (
              <Group key={climb.name} gap="sm" wrap="nowrap">
                <MantineBadge
                  color={CLIMB_CATEGORY_COLORS[climb.category]}
                  variant={BADGE_VARIANTS.ClimbCategory}
                  size="sm"
                >
                  {t(`routes.climbCategory.${climb.category satisfies 'CAT2' | 'CAT3'}`)}
                </MantineBadge>
                <Text size="sm" style={{ flex: 1, minWidth: 0 }} truncate>
                  {climb.name}
                </Text>
                <Text size="sm" c="dimmed" style={{ whiteSpace: 'nowrap' }}>
                  {climb.stats}
                </Text>
              </Group>
            ))}
          </Stack>
          <Group gap="xs">
            <DemoButton variant="default" leftSection={<IconDownload size={14} />}>
              {t('routes.detail.download.gpx')}
            </DemoButton>
            <DemoButton variant="default">{t('rides.detail.groups.route.downloadFit')}</DemoButton>
            <DemoButton variant="default" leftSection={<IconDeviceWatch size={14} />}>
              {t('routes.detail.sendToDevice')}
            </DemoButton>
          </Group>
        </Stack>
      </Paper>
    </VisualFrame>
  )
}
