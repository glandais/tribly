import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconMap, IconArrowUp, IconUsers, IconChevronRight } from '@tabler/icons-react'
import { Group, Text, Image, Box } from '@mantine/core'
import type { RouteDto } from '@/api/dto'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'
import { EntityLogo } from '../common/EntityLogo'
import { paths } from '@/config/paths'

interface RouteCardProps {
  route: RouteDto
  showTeam: boolean
}

export function RouteCard({ route, showTeam }: RouteCardProps) {
  const { t } = useTranslation()

  return (
    <Card to={paths.route(route.team.slug, route.slug)}>
      <Image src={route.media.assets.thumbnail?.url} alt={route.name} h={200} fit="contain" />

      <CardContent>
        {showTeam && (
          <Box
            component={Link}
            to={paths.team(route.team.slug)}
            onClick={(e: React.MouseEvent) => e.stopPropagation()}
            mb="sm"
            style={{
              textDecoration: 'none',
              display: 'flex',
              alignItems: 'center',
              gap: 'var(--mantine-spacing-xs)',
            }}
          >
            <IconUsers size={16} color="var(--mantine-color-dimmed)" />
            <Text size="sm" c="dimmed" fw={500}>
              {route.team.name}
            </Text>
            <IconChevronRight size={12} color="var(--mantine-color-dimmed)" />
          </Box>
        )}

        <Group gap="sm" wrap="nowrap" align="flex-start">
          <EntityLogo logo={route.media.assets.logo} alt={route.name} size="md" />
          <Box style={{ flex: 1, minWidth: 0 }}>
            <CardTitle>{route.name}</CardTitle>
            <CardDescription markdown={true} media={route.media} />
          </Box>
        </Group>

        <StatGroup>
          <Stat icon={<IconMap size={16} />}>
            {t('distance', { distance: (route.distance / 1000).toFixed(1) })}
          </Stat>
          <Stat icon={<IconArrowUp size={16} />}>
            {t('elevation', { elevation: route.elevationGain })}
          </Stat>
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
