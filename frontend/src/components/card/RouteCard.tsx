import { useTranslation } from 'react-i18next'
import { IconMap, IconArrowUp, IconMessageCircle } from '@tabler/icons-react'
import { useUnits } from '@/hooks/useUnits'
import { Group, Image, Box, useComputedColorScheme } from '@mantine/core'
import type { RouteDto } from '@/api/dto'
import { Card, CardContent, CardTitle, CardDescription, CardTeamLink } from './common'
import { SurfaceBadge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from './common'
import { EntityLogo } from '../common/EntityLogo'
import { paths } from '@/config/paths'

interface RouteCardProps {
  route: RouteDto
  showTeam: boolean
}

export function RouteCard({ route, showTeam }: RouteCardProps) {
  const { t } = useTranslation()
  const { distance, elevation } = useUnits()
  const colorScheme = useComputedColorScheme('light')
  // A compact row carries the themed variants (contract 1.5.1); `thumbnailUrl` stays the
  // fallback for a route that only ever had the collapsed one.
  const themedThumbnail =
    colorScheme === 'dark' ? route.media.assets.thumbnailDark : route.media.assets.thumbnailLight
  const thumbnailUrl = themedThumbnail?.imageUrl ?? route.thumbnailUrl

  return (
    <Card to={paths.route(route.team.slug, route.slug)}>
      <Image src={thumbnailUrl?.replace('{size}', '400')} alt={route.name} />

      <CardContent>
        {showTeam && <CardTeamLink teamSlug={route.team.slug} teamName={route.team.name} />}

        <Group gap="sm" wrap="nowrap" align="flex-start">
          <EntityLogo logo={route.media.assets.logo} alt={route.name} size="md" />
          <Box style={{ flex: 1, minWidth: 0 }}>
            <CardTitle>{route.name}</CardTitle>
            <CardDescription excerpt={route.excerpt} markdown={true} media={route.media} />
          </Box>
        </Group>

        <StatGroup>
          <Stat icon={<IconMap size={16} />}>{distance(route.distance)}</Stat>
          <Stat icon={<IconArrowUp size={16} />}>{elevation(route.elevationGain)}</Stat>
          {route.commentCount !== undefined && (
            <Stat icon={<IconMessageCircle size={16} />}>
              {t('comments.count', { count: route.commentCount })}
            </Stat>
          )}
        </StatGroup>

        <Group gap="xs">
          {route.surfaceType && (
            <SurfaceBadge surface={route.surfaceType}>
              {t(
                `routes.surfaceType.${route.surfaceType satisfies 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'}`
              )}
            </SurfaceBadge>
          )}
          <VisibilityBadge visibility={route.visibility} />
        </Group>
      </CardContent>
    </Card>
  )
}

interface RouteCardSkeletonProps {
  count?: number
}

export function RouteCardSkeleton({ count = 1 }: RouteCardSkeletonProps) {
  return (
    <CardSkeleton count={count} hasImage imageHeight="200px" hasLogo statCount={2} badgeCount={2} />
  )
}
