import { useTranslation } from 'react-i18next'
import { useGetRoute } from '@/api/endpoints/routes/routes'
import { LoadingSpinner } from '../common/LoadingSpinner'

interface RoutePreviewCompactProps {
  routeSlug: string
  teamSlug: string
}

export function RoutePreviewCompact({ routeSlug, teamSlug }: RoutePreviewCompactProps) {
  const { t } = useTranslation()
  const { data: route, isLoading } = useGetRoute(teamSlug, routeSlug)

  if (isLoading)
    return (
      <div className="flex items-center gap-1 text-xs text-gray-400">
        <LoadingSpinner size="sm" />
        <span>{t('loading')}</span>
      </div>
    )

  if (!route) return <div className="text-xs text-red-500">{t('routes.preview.notFound')}</div>

  return (
    <div className="text-xs text-gray-600">
      <span className="font-medium">{route.name}</span>
      <span className="ml-2">
        ({(route.distance / 1000).toFixed(1)} {t('units.km')}, {route.elevationGain}
        {t('units.m')})
      </span>
    </div>
  )
}
