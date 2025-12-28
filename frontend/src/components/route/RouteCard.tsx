import { useTranslation } from 'react-i18next'
import { MapIcon, ArrowUpIcon } from '@heroicons/react/24/outline'
import type { RouteDto } from '../../api/api'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'

interface RouteCardProps {
  route: RouteDto
  teamSlug: string
}

export function RouteCard({ route, teamSlug }: RouteCardProps) {
  const { t } = useTranslation('routes')

  const distanceIcon = <MapIcon />
  const elevationIcon = <ArrowUpIcon />

  return (
    <Card to={`/teams/${teamSlug}/routes/${route.slug}`}>
      {/* Thumbnail */}
      <img
        src={`/api/download/${route.visibility.toLowerCase()}/teams/${teamSlug}/routes/${route.slug}/thumbnail`}
        alt={route.name}
        className="w-full h-48 object-cover"
      />

      <CardContent>
        <CardTitle>{route.name}</CardTitle>
        <CardDescription markdown={true} media={route.media} />

        <StatGroup className="mb-3">
          <Stat icon={distanceIcon}>{(route.distance / 1000).toFixed(1)} km</Stat>
          <Stat icon={elevationIcon}>{route.elevationGain}m</Stat>
        </StatGroup>

        <div className="flex flex-wrap gap-2">
          {route.surfaceType && (
            <Badge variant="green">{t(`surfaceType.${route.surfaceType}`)}</Badge>
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
  return <CardSkeleton count={count} hasImage imageHeight="h-48" statCount={2} badgeCount={3} />
}
