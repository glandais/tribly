import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import type { RouteDto } from '../../api/api'

interface RouteCardProps {
  route: RouteDto
  teamSlug: string
}

export function RouteCard({ route, teamSlug }: RouteCardProps) {
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')

  return (
    <Link
      to={`/teams/${teamSlug}/routes/${route.id}`}
      className="group block bg-white rounded-lg shadow-xs border border-gray-200 hover:shadow-md hover:border-gray-300 transition-all overflow-hidden"
    >
      {/* Thumbnail */}
      {route.thumbnailUrl ? (
        <img
          src={`/api/download/teams/${teamSlug}/routes/${route.id}/thumbnail`}
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

      {/* Content */}
      <div className="p-4">
        <h3 className="text-lg font-semibold text-gray-900 group-hover:text-indigo-600 mb-2">
          {route.name}
        </h3>

        {route.description && (
          <p className="text-sm text-gray-600 mb-3 line-clamp-2">{route.description}</p>
        )}

        {/* Stats */}
        <div className="flex items-center gap-4 text-sm text-gray-500 mb-3">
          <span className="flex items-center">
            <svg
              className="h-4 w-4 mr-1"
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
            {(route.distance / 1000).toFixed(1)} km
          </span>
          <span className="flex items-center">
            <svg
              className="h-4 w-4 mr-1"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 10l7-7m0 0l7 7m-7-7v18"
              />
            </svg>
            {route.elevationGain}m
          </span>
        </div>

        {/* Badges */}
        <div className="flex flex-wrap gap-2">
          {route.difficulty && (
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
              {t(`difficulty.${route.difficulty}`)}
            </span>
          )}
          {route.surfaceType && (
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
              {t(`surfaceType.${route.surfaceType}`)}
            </span>
          )}
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
            {tCommon(`visibility.${route.visibility.toLowerCase()}`)}
          </span>
        </div>
      </div>
    </Link>
  )
}

interface RouteCardSkeletonProps {
  count?: number
}

export function RouteCardSkeleton({ count = 1 }: RouteCardSkeletonProps) {
  return (
    <>
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="bg-white rounded-lg shadow-xs border border-gray-200 animate-pulse">
          <div className="w-full h-32 bg-gray-200 rounded-t-lg" />
          <div className="p-4">
            <div className="h-5 bg-gray-200 rounded-sm w-3/4 mb-2" />
            <div className="h-4 bg-gray-200 rounded-sm w-full mb-1" />
            <div className="h-4 bg-gray-200 rounded-sm w-2/3 mb-4" />
            <div className="flex gap-4 mb-3">
              <div className="h-4 bg-gray-200 rounded-sm w-16" />
              <div className="h-4 bg-gray-200 rounded-sm w-16" />
            </div>
            <div className="flex gap-2">
              <div className="h-5 bg-gray-200 rounded-full w-16" />
              <div className="h-5 bg-gray-200 rounded-full w-16" />
              <div className="h-5 bg-gray-200 rounded-full w-16" />
            </div>
          </div>
        </div>
      ))}
    </>
  )
}
