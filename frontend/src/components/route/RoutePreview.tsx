import { useTranslation } from 'react-i18next'
import { useRoute } from '../../hooks/useRoute'
import { LoadingSpinner } from '../common/LoadingSpinner'

interface RoutePreviewProps {
  routeId: string
  teamSlug: string
}

export function RoutePreview({ routeId, teamSlug }: RoutePreviewProps) {
  const { t } = useTranslation('routes')
  const { data: route, isLoading } = useRoute(teamSlug, routeId)

  if (isLoading)
    return (
      <div className="flex items-center gap-2 text-sm text-gray-400">
        <LoadingSpinner size="sm" />
        <span>{t('preview.loading')}</span>
      </div>
    )

  if (!route) return <div className="text-sm text-red-500">{t('preview.notFound')}</div>

  return (
    <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg border border-gray-200">
      {route.thumbnailUrl && (
        <img
          src={`/api/download/${route.visibility.toLowerCase()}/teams/${teamSlug}/routes/${route.id}/thumbnail`}
          alt={route.name}
          className="w-16 h-16 object-cover rounded"
        />
      )}
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-gray-900 truncate">{route.name}</p>
        <div className="flex gap-3 mt-1 text-xs text-gray-500">
          <span>📏 {(route.distance / 1000).toFixed(1)} km</span>
          <span>⛰️ {route.elevationGain}m</span>
          {route.difficulty && <span>💪 {t(`difficulty.${route.difficulty}`)}</span>}
        </div>
      </div>
    </div>
  )
}
