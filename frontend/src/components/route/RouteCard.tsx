import { useTranslation } from 'react-i18next'
import type { RouteDto } from '../../api/api'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'

interface RouteCardProps {
  route: RouteDto
  teamSlug: string
}

export function RouteCard({ route, teamSlug }: RouteCardProps) {
  const { t } = useTranslation('routes')

  const distanceIcon = (
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
      />
    </svg>
  )

  const elevationIcon = (
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M5 10l7-7m0 0l7 7m-7-7v18"
      />
    </svg>
  )

  return (
    <Card to={`/teams/${teamSlug}/routes/${route.id}`}>
      {/* Thumbnail */}
      {route.thumbnailUrl ? (
        <img
          src={`/api/download/${route.visibility.toLowerCase()}/teams/${teamSlug}/routes/${route.id}/thumbnail`}
          alt={route.name}
          className="w-full h-48 object-cover"
        />
      ) : (
        <div className="w-full h-48 bg-gray-100 flex items-center justify-center">
          <svg
            className="h-12 w-12 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
            />
          </svg>
        </div>
      )}

      <CardContent>
        <CardTitle>{route.name}</CardTitle>
        {route.description && <CardDescription>{route.description}</CardDescription>}

        <StatGroup className="mb-3">
          <Stat icon={distanceIcon}>{(route.distance / 1000).toFixed(1)} km</Stat>
          <Stat icon={elevationIcon}>{route.elevationGain}m</Stat>
        </StatGroup>

        <div className="flex flex-wrap gap-2">
          {route.difficulty && <Badge variant="blue">{t(`difficulty.${route.difficulty}`)}</Badge>}
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
