import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { MapIcon, ArrowUpIcon, UsersIcon, ChevronRightIcon } from '@heroicons/react/24/outline'
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

  const distanceIcon = <MapIcon />
  const elevationIcon = <ArrowUpIcon />

  return (
    <Card to={paths.route(route.team.slug, route.slug)}>
      {/* Thumbnail */}
      <img
        src={route.media.assets.thumbnail?.url}
        alt={route.name}
        className="w-full aspect-square object-contain"
      />

      <CardContent>
        {/* Team header - clickable link to team page */}
        {showTeam && (
          <Link
            to={paths.team(route.team.slug)}
            onClick={(e) => e.stopPropagation()}
            className="flex items-center gap-2 mb-3 text-sm text-gray-600 hover:text-indigo-600 transition-colors group"
          >
            <UsersIcon className="h-4 w-4" />
            <span className="font-medium">{route.team.name}</span>
            <ChevronRightIcon className="h-3 w-3 opacity-0 -translate-x-1 group-hover:opacity-100 group-hover:translate-x-0 transition-all" />
          </Link>
        )}

        <div className="flex items-start gap-3">
          <EntityLogo
            logo={route.media.assets.logo}
            alt={route.name}
            size="md"
            className="shrink-0"
          />
          <div className="flex-1 min-w-0">
            <CardTitle>{route.name}</CardTitle>
            <CardDescription markdown={true} media={route.media} />
          </div>
        </div>

        <StatGroup className="mb-3">
          <Stat icon={distanceIcon}>
            {(route.distance / 1000).toFixed(1)} {t('units.km')}
          </Stat>
          <Stat icon={elevationIcon}>
            {route.elevationGain}
            {t('units.m')}
          </Stat>
        </StatGroup>

        <div className="flex flex-wrap gap-2">
          {route.surfaceType && (
            <Badge variant="green">
              {t(
                `routes.surfaceType.${route.surfaceType satisfies 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'}`
              )}
            </Badge>
          )}
          <VisibilityBadge visibility={route.visibility} />
        </div>
      </CardContent>
    </Card>
  )
}

interface RouteCardSkeletonProps {
  count?: number
}

export function RouteCardSkeleton({ count = 1 }: RouteCardSkeletonProps) {
  return (
    <CardSkeleton count={count} hasImage imageHeight="aspect-square" statCount={2} badgeCount={3} />
  )
}
