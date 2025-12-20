import { useTranslation } from 'react-i18next'
import { useRoute } from '../../hooks/useRoute'
import { LoadingSpinner } from '../common/LoadingSpinner'

interface RoutePreviewCompactProps {
  routeId: string
  teamSlug: string
}

export function RoutePreviewCompact({ routeId, teamSlug }: RoutePreviewCompactProps) {
  const { t } = useTranslation('routes')
  const { data: route, isLoading } = useRoute(teamSlug, routeId)

  if (isLoading)
    return (
      <div className="flex items-center gap-1 text-xs text-gray-400">
        <LoadingSpinner size="sm" />
        <span>{t('preview.loading')}</span>
      </div>
    )

  if (!route) return <div className="text-xs text-red-500">{t('preview.notFound')}</div>

  return (
    <div className="text-xs text-gray-600">
      <span className="font-medium">{route.name}</span>
      <span className="ml-2">
        ({(route.distance / 1000).toFixed(1)} km, {route.elevationGain}m)
      </span>
    </div>
  )
}
