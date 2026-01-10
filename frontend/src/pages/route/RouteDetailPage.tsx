import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { IconMap, IconArrowUp, IconArrowDown, IconDownload } from '@tabler/icons-react'
import {
  Box,
  Button,
  Group,
  Paper,
  SimpleGrid,
  Skeleton,
  Stack,
  Text,
  Title,
  Badge,
  Center,
} from '@mantine/core'
import { useGetRoute, useDeleteRoute, getListRoutesQueryKey } from '@/api/endpoints/routes/routes'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { RouteMapView } from '../../components/route/RouteMapView'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function RouteDetailPage() {
  const { teamSlug, routeSlug } = useParams<{ teamSlug: string; routeSlug: string }>()
  const { t } = useTranslation()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const { data: team } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: route, isLoading: routeLoading } = useGetRoute(teamSlug!, routeSlug!, {
    query: { enabled: !!teamSlug && !!routeSlug },
  })
  const deleteRouteMutation = useDeleteRoute()

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  useCanonicalPath(team && route ? paths.route(team.slug, route.slug) : undefined)

  const isMember = !!team?.role
  const canEdit = team && (team.role === 'ADMIN' || team.role === 'ORGANIZER')

  const handleDelete = async () => {
    if (routeSlug && teamSlug) {
      await deleteRouteMutation.mutateAsync(
        { teamSlug: teamSlug, routeSlug },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(teamSlug) })
            notifications.show({
              message: i18next.t('routes.notifications.deleted'),
              color: 'green',
            })
            navigate(paths.routes(teamSlug))
          },
        }
      )
    }
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

  if (routeLoading) {
    return (
      <Box maw={1280} mx="auto" px="md" py="xl">
        <Stack gap="md">
          <Skeleton height={32} width="25%" />
          <Skeleton height={384} />
          <SimpleGrid cols={{ base: 1, md: 3 }} spacing="lg">
            {[...Array(3)].map((_, i) => (
              <Skeleton key={i} height={96} />
            ))}
          </SimpleGrid>
        </Stack>
      </Box>
    )
  }

  if (!route || !team) {
    return (
      <Box maw={1280} mx="auto" px="md" py="xl">
        <Center>
          <Text c="dimmed">{t('errors.api.notFound')}</Text>
        </Center>
      </Box>
    )
  }

  return (
    <Box maw={1280} mx="auto" px="md" py="xl">
      {/* Header */}
      <Stack gap="lg" mb="lg">
        <Group justify="space-between" align="flex-start" wrap="wrap">
          <Stack gap="xs">
            <Group gap="md">
              <EntityLogo logo={route.media.assets.logo} alt={route.name} size="lg" />
              <Title order={1}>{route.name}</Title>
            </Group>
            <MediaDisplay media={route.media} />
          </Stack>
          {canEdit && (
            <Group gap="md" mt={{ base: 'md', sm: 0 }}>
              <Button variant="default" component="a" href={paths.routeEdit(teamSlug!, routeSlug!)}>
                {t('actions.edit')}
              </Button>
              <Button variant="outline" color="red" onClick={() => setShowDeleteConfirm(true)}>
                {t('actions.delete')}
              </Button>
            </Group>
          )}
        </Group>
      </Stack>

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('routes.detail.deleteConfirm.title')}
        message={t('routes.detail.deleteConfirm.message')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={deleteRouteMutation.isPending}
      />

      {/* Download Section */}
      <Paper shadow="xs" p="lg" mb="xl">
        <Group gap="md" wrap="wrap">
          <Button
            variant="default"
            component="a"
            href={route.media.assets.gpx?.url}
            leftSection={<IconDownload size={20} />}
          >
            {t('routes.detail.download.gpx')}
          </Button>
          <Button
            variant="default"
            component="a"
            href={route.media.assets.fit?.url}
            leftSection={<IconDownload size={20} />}
          >
            {t('routes.detail.download.fit')}
          </Button>
        </Group>
      </Paper>

      {/* Interactive Map with Elevation Chart */}
      <Box mb="xl">
        <RouteMapView route={route} />
      </Box>

      {/* Stats Grid */}
      <SimpleGrid cols={{ base: 1, md: 3 }} spacing="lg" mb="xl">
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
      <Paper shadow="xs" p="lg" mb="xl">
        <Title order={2} mb="md">
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

      {/* Climbs Section */}
      {(() => {
        const allClimbs = route.tracks?.flatMap((track) => track.climbs) || []
        return (
          allClimbs.length > 0 && (
            <Paper shadow="xs" p="lg" mb="xl">
              <Title order={2} mb="md">
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

      {/* Comments Section - only visible to team members */}
      {isMember && (
        <CommentSection
          teamSlug={teamSlug!}
          entityType="routes"
          entitySlug={routeSlug!}
          isOrganizer={!!canEdit}
        />
      )}
    </Box>
  )
}
