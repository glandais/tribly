import { useTranslation } from 'react-i18next'
import { IconMap, IconArrowUp, IconArrowDown, IconDownload } from '@tabler/icons-react'
import { Box, Button, Group, Stack, Text, Title, Badge } from '@mantine/core'
import type { RouteDetailDto } from '@/api/dto'
import { RouteMapView } from './RouteMapView'
import { useUnits } from '@/hooks/useUnits'

interface RouteDetailViewProps {
  route: RouteDetailDto
  /** Show download buttons (default: true) */
  showDownload?: boolean
  /** Show route info section with visibility and surface type (default: true) */
  showInfo?: boolean
}

const getClimbCategoryColor = (category: string): string => {
  switch (category) {
    case 'HC':
      return 'grape'
    case 'CAT1':
      return 'red'
    case 'CAT2':
      return 'orange'
    case 'CAT3':
      return 'yellow'
    case 'CAT4':
      return 'green'
    default:
      return 'gray'
  }
}

/**
 * Detailed route view component showing map, stats, info, and climbs.
 * Used in RouteDetailPage and StageDetailPage.
 */
export function RouteDetailView({
  route,
  showDownload = true,
  showInfo = true,
}: RouteDetailViewProps) {
  const { t } = useTranslation()
  const { distance, elevation, formatDistance, config } = useUnits()

  return (
    <Stack gap="md">
      {/* Interactive Map with Elevation Chart */}
      <Box>
        <RouteMapView route={route} />
      </Box>

      {/* Download Buttons - inline without wrapper */}
      {showDownload && (route.media.assets.gpx || route.media.assets.fit) && (
        <Group gap="sm" wrap="wrap">
          {route.media.assets.gpx && (
            <Button
              variant="default"
              size="sm"
              component="a"
              href={route.media.assets.gpx.url}
              leftSection={<IconDownload size={16} />}
            >
              {t('routes.detail.download.gpx')}
            </Button>
          )}
          {route.media.assets.fit && (
            <Button
              variant="default"
              size="sm"
              component="a"
              href={route.media.assets.fit.url}
              leftSection={<IconDownload size={16} />}
            >
              {t('routes.detail.download.fit')}
            </Button>
          )}
        </Group>
      )}

      {/* Stats - compact inline display */}
      <Group gap="xl" wrap="wrap">
        <Group gap="xs">
          <IconMap size={24} color="var(--mantine-primary-color-filled)" />
          <Box>
            <Text size="xs" c="dimmed">
              {t('routes.detail.stats.distance')}
            </Text>
            <Text size="lg" fw={700}>
              {distance(route.distance)}
            </Text>
          </Box>
        </Group>

        <Group gap="xs">
          <IconArrowUp size={24} color="var(--mantine-color-green-text)" />
          <Box>
            <Text size="xs" c="dimmed">
              {t('routes.detail.stats.elevationGain')}
            </Text>
            <Text size="lg" fw={700}>
              {elevation(route.elevationGain)}
            </Text>
          </Box>
        </Group>

        <Group gap="xs">
          <IconArrowDown size={24} color="var(--mantine-color-red-text)" />
          <Box>
            <Text size="xs" c="dimmed">
              {t('routes.detail.stats.elevationLoss')}
            </Text>
            <Text size="lg" fw={700}>
              {elevation(route.elevationLoss)}
            </Text>
          </Box>
        </Group>
      </Group>

      {/* Route Info - compact inline badges */}
      {showInfo && (
        <Group gap="sm" wrap="wrap">
          {route.surfaceType && (
            <Badge color="green" variant="light" size="lg">
              {t(
                `routes.surfaceType.${route.surfaceType satisfies 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'}`
              )}
            </Badge>
          )}
          <Badge color="gray" variant="light" size="lg">
            {t(`visibility.${route.visibility.toLowerCase() as 'public' | 'team'}`)}
          </Badge>
          <Text size="sm" c="dimmed">
            {t('routes.detail.info.createdAt')}: {new Date(route.createdAt).toLocaleDateString()}
          </Text>
        </Group>
      )}

      {/* Climbs Section - compact list */}
      {(() => {
        const allClimbs = route.tracks?.flatMap((track) => track.climbs) || []
        return (
          allClimbs.length > 0 && (
            <Box>
              <Title order={4} mb="sm">
                {t('routes.detail.climbs.title')} ({allClimbs.length})
              </Title>
              <Stack gap="xs">
                {allClimbs.map((climb, index) => (
                  <Box
                    key={index}
                    py="xs"
                    style={{ borderBottom: '1px solid var(--mantine-color-default-border)' }}
                  >
                    <Group justify="space-between" wrap="wrap" gap="xs">
                      <Group gap="xs">
                        {climb.category && (
                          <Badge
                            color={getClimbCategoryColor(climb.category)}
                            variant="filled"
                            size="sm"
                          >
                            {t(
                              `routes.climbCategory.${climb.category satisfies 'HC' | 'CAT1' | 'CAT2' | 'CAT3' | 'CAT4'}`
                            )}
                          </Badge>
                        )}
                        <Text size="sm" fw={500}>
                          {t('routes.detail.climbs.unnamed', { number: index + 1 })}
                        </Text>
                        <Text size="sm" c="dimmed">
                          {t('routes.detail.climbs.distance', {
                            start: formatDistance(climb.startDistance),
                            end: formatDistance(climb.endDistance),
                            unit: config.distanceUnit,
                          })}
                        </Text>
                      </Group>
                      <Group gap="md">
                        <Text size="sm">
                          <Text span c="dimmed">
                            {t('routes.detail.climbs.gain')}:
                          </Text>{' '}
                          <Text span fw={500}>
                            {elevation(climb.elevationGain)}
                          </Text>
                        </Text>
                        <Text size="sm">
                          <Text span c="dimmed">
                            {t('routes.detail.climbs.avgGradient')}:
                          </Text>{' '}
                          <Text span fw={500}>
                            {climb.averageGradient.toFixed(1)}%
                          </Text>
                        </Text>
                        <Text size="sm">
                          <Text span c="dimmed">
                            {t('routes.detail.climbs.maxGradient')}:
                          </Text>{' '}
                          <Text span fw={500}>
                            {climb.maxGradient.toFixed(1)}%
                          </Text>
                        </Text>
                      </Group>
                    </Group>
                  </Box>
                ))}
              </Stack>
            </Box>
          )
        )
      })()}
    </Stack>
  )
}
