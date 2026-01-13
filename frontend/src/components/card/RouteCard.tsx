import { useTranslation } from 'react-i18next'
import { IconMap, IconArrowUp } from '@tabler/icons-react'
import { useUnits } from '@/hooks/useUnits'
import { Group, Image, Box } from '@mantine/core'
import type { RouteDto } from '@/api/dto'
import { Card, CardContent, CardTitle, CardDescription, CardTeamLink } from './common'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from './common'
import { EntityLogo } from '../common/EntityLogo'
import { paths } from '@/config/paths'

interface RouteCardProps {
  route: RouteDto
  showTeam: boolean
}

export function RouteCard({ route, showTeam }: RouteCardProps) {
  const { t } = useTranslation()
  const { distance, elevation } = useUnits()

  return (
    <Card to={paths.route(route.team.slug, route.slug)}>
      <Image
        src={route.media.assets.thumbnail?.imageUrl?.replace('{size}', '400')}
        alt={route.name}
      />

      <CardContent>
        {showTeam && <CardTeamLink teamSlug={route.team.slug} teamName={route.team.name} />}

        <Group gap="sm" wrap="nowrap" align="flex-start">
          <EntityLogo logo={route.media.assets.logo} alt={route.name} size="md" />
          <Box style={{ flex: 1, minWidth: 0 }}>
            <CardTitle>{route.name}</CardTitle>
            <CardDescription markdown={true} media={route.media} />
          </Box>
        </Group>

        <StatGroup>
          <Stat icon={<IconMap size={16} />}>{distance(route.distance)}</Stat>
          <Stat icon={<IconArrowUp size={16} />}>{elevation(route.elevationGain)}</Stat>
        </StatGroup>

        <Group gap="xs">
          {route.surfaceType && (
            <Badge variant="green">
              {t(
                `routes.surfaceType.${route.surfaceType satisfies 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'}`
              )}
            </Badge>
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
  return <CardSkeleton count={count} hasImage imageHeight="200px" statCount={2} badgeCount={3} />
}
