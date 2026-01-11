import { useTranslation } from 'react-i18next'
import { IconMap, IconArrowUp, IconArrowDown, IconDownload } from '@tabler/icons-react'
import { Box, Button, Group, Paper, SimpleGrid, Stack, Text, Title, Badge } from '@mantine/core'
import type { RouteDetailDto } from '@/api/dto'
import { RouteMapView } from './RouteMapView'

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

  return (
    <Stack gap="lg">
      {/* Download Section */}
      {showDownload && (route.media.assets.gpx || route.media.assets.fit) && (
        <Paper shadow="xs" p="lg">
          <Group gap="md" wrap="wrap">
            {route.media.assets.gpx && (
              <Button
                variant="default"
                component="a"
                href={route.media.assets.gpx.url}
                leftSection={<IconDownload size={20} />}
              >
                {t('routes.detail.download.gpx')}
              </Button>
            )}
            {route.media.assets.fit && (
              <Button
                variant="default"
                component="a"
                href={route.media.assets.fit.url}
                leftSection={<IconDownload size={20} />}
              >
                {t('routes.detail.download.fit')}
              </Button>
            )}
          </Group>
        </Paper>
      )}

      {/* Interactive Map with Elevation Chart */}
      <Box>
        <RouteMapView route={route} />
      </Box>

      {/* Stats Grid */}
      <SimpleGrid cols={{ base: 1, md: 3 }} spacing="lg">
        <Paper shadow="xs" p="lg">
          <Group>
            <IconMap size={32} color="var(--mantine-color-indigo-6)" />
            <Stack gap={0}>
              <Text size="sm" c="dimmed">
                {t('routes.detail.stats.distance')}
              </Text>
              <Text size="xl" fw={700}>
                {t('distance', { distance: (route.distance / 1000).toFixed(1) })}
              </Text>
            </Stack>
          </Group>
        </Paper>

        <Paper shadow="xs" p="lg">
          <Group>
            <IconArrowUp size={32} color="var(--mantine-color-green-6)" />
            <Stack gap={0}>
              <Text size="sm" c="dimmed">
                {t('routes.detail.stats.elevationGain')}
              </Text>
              <Text size="xl" fw={700}>
                {t('elevation', { elevation: route.elevationGain })}
              </Text>
            </Stack>
          </Group>
        </Paper>

        <Paper shadow="xs" p="lg">
          <Group>
            <IconArrowDown size={32} color="var(--mantine-color-red-6)" />
            <Stack gap={0}>
              <Text size="sm" c="dimmed">
                {t('routes.detail.stats.elevationLoss')}
              </Text>
              <Text size="xl" fw={700}>
                {t('elevation', { elevation: route.elevationLoss })}
              </Text>
            </Stack>
          </Group>
        </Paper>
      </SimpleGrid>

      {/* Route Info */}
      {showInfo && (
        <Paper shadow="xs" p="lg">
          <Title order={3} mb="md">
            {t('routes.detail.info.title')}
          </Title>
          <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
            {route.surfaceType && (
              <>
                <Text size="sm" fw={500} c="dimmed">
                  {t('routes.detail.info.surfaceType')}
                </Text>
                <Box>
                  <Badge color="green" variant="light">
                    {t(
                      `routes.surfaceType.${route.surfaceType satisfies 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'}`
                    )}
                  </Badge>
                </Box>
              </>
            )}
            <Text size="sm" fw={500} c="dimmed">
              {t('visibility.label')}
            </Text>
            <Box>
              <Badge color="gray" variant="light">
                {t(`visibility.${route.visibility.toLowerCase() as 'public' | 'team'}`)}
              </Badge>
            </Box>
            <Text size="sm" fw={500} c="dimmed">
              {t('routes.detail.info.createdAt')}
            </Text>
            <Text size="sm">{new Date(route.createdAt).toLocaleDateString()}</Text>
          </SimpleGrid>
        </Paper>
      )}

      {/* Climbs Section */}
      {(() => {
        const allClimbs = route.tracks?.flatMap((track) => track.climbs) || []
        return (
          allClimbs.length > 0 && (
            <Paper shadow="xs" p="lg">
              <Title order={3} mb="md">
                {t('routes.detail.climbs.title')} ({allClimbs.length})
              </Title>
              <Stack gap="md">
                {allClimbs.map((climb, index) => (
                  <Paper key={index} withBorder p="md">
                    <Group justify="space-between" mb="xs" align="flex-start">
                      <Stack gap={2}>
                        <Text fw={600}>
                          {t('routes.detail.climbs.unnamed', { number: index + 1 })}
                        </Text>
                        <Text size="sm" c="dimmed">
                          {t('routes.detail.climbs.distance', {
                            start: (climb.startDistance / 1000).toFixed(1),
                            end: (climb.endDistance / 1000).toFixed(1),
                          })}
                        </Text>
                      </Stack>
                      {climb.category && (
                        <Badge color={getClimbCategoryColor(climb.category)} variant="light">
                          {t(
                            `routes.climbCategory.${climb.category satisfies 'HC' | 'CAT1' | 'CAT2' | 'CAT3' | 'CAT4'}`
                          )}
                        </Badge>
                      )}
                    </Group>
                    <SimpleGrid cols={3} spacing="md">
                      <Box>
                        <Text size="sm" c="dimmed" component="span">
                          {t('routes.detail.climbs.gain')}:{' '}
                        </Text>
                        <Text size="sm" fw={500} component="span">
                          {t('elevation', { elevation: climb.elevationGain })}
                        </Text>
                      </Box>
                      <Box>
                        <Text size="sm" c="dimmed" component="span">
                          {t('routes.detail.climbs.avgGradient')}:{' '}
                        </Text>
                        <Text size="sm" fw={500} component="span">
                          {climb.averageGradient.toFixed(1)}%
                        </Text>
                      </Box>
                      <Box>
                        <Text size="sm" c="dimmed" component="span">
                          {t('routes.detail.climbs.maxGradient')}:{' '}
                        </Text>
                        <Text size="sm" fw={500} component="span">
                          {climb.maxGradient.toFixed(1)}%
                        </Text>
                      </Box>
                    </SimpleGrid>
                  </Paper>
                ))}
              </Stack>
            </Paper>
          )
        )
      })()}
    </Stack>
  )
}
