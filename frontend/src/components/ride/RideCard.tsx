import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { RideStatus, Visibility } from '../../hooks/useRide'
import type { RideDto } from '../../hooks/useRide'

interface RideCardProps {
  ride: RideDto
  teamSlug: string
}

const statusColors: Record<RideStatus, string> = {
  [RideStatus.Draft]: 'bg-gray-100 text-gray-800',
  [RideStatus.Published]: 'bg-green-100 text-green-800',
  [RideStatus.Cancelled]: 'bg-red-100 text-red-800',
}

const visibilityColors: Record<Visibility, string> = {
  [Visibility.Public]: 'bg-indigo-100 text-indigo-800',
  [Visibility.Team]: 'bg-gray-100 text-gray-600',
}

export function RideCard({ ride, teamSlug }: RideCardProps) {
  const { t, i18n } = useTranslation('rides')
  const rideDate = new Date(ride.date)
  const formattedDate = rideDate.toLocaleDateString(i18n.language, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  })

  return (
    <Link
      to={`/teams/${teamSlug}/rides/${ride.slug}`}
      className="block bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow border border-gray-200"
    >
      <div className="p-5">
        <div className="flex items-start justify-between">
          <div className="flex-1 min-w-0">
            <h3 className="text-lg font-semibold text-gray-900 truncate">{ride.title}</h3>
            {ride.description && (
              <p className="mt-1 text-sm text-gray-600 line-clamp-2">{ride.description}</p>
            )}
          </div>
          <div className="ml-3 flex flex-col items-end gap-1">
            <span
              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[ride.status]}`}
            >
              {t(`status.${ride.status}`)}
            </span>
            <span
              className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${visibilityColors[ride.visibility]}`}
            >
              {ride.visibility === Visibility.Public && (
                <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                  <path
                    fillRule="evenodd"
                    d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z"
                    clipRule="evenodd"
                  />
                </svg>
              )}
              {ride.visibility === 'TEAM' && (
                <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
                </svg>
              )}
              {t(`visibility.${ride.visibility}`)}
            </span>
          </div>
        </div>

        <div className="mt-4 flex items-center gap-4 text-sm text-gray-500">
          <span className="flex items-center">
            <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
            {formattedDate}
          </span>
          {ride.startTime && (
            <span className="flex items-center">
              <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              {ride.startTime.substring(0, 5)}
            </span>
          )}
          <span className="flex items-center">
            <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
              />
            </svg>
            {t('card.participantCount', { count: ride.participantCount })}
          </span>
          <span className="flex items-center">
            <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"
              />
            </svg>
            {t('card.groupCount', { count: ride.groupCount })}
          </span>
        </div>
      </div>
    </Link>
  )
}

export function RideCardSkeleton() {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-5 animate-pulse">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="h-5 bg-gray-200 rounded-sm w-3/4 mb-2"></div>
          <div className="h-4 bg-gray-200 rounded-sm w-full"></div>
        </div>
        <div className="h-5 bg-gray-200 rounded-sm w-16 ml-3"></div>
      </div>
      <div className="mt-4 flex items-center gap-4">
        <div className="h-4 bg-gray-200 rounded-sm w-20"></div>
        <div className="h-4 bg-gray-200 rounded-sm w-16"></div>
        <div className="h-4 bg-gray-200 rounded-sm w-24"></div>
      </div>
    </div>
  )
}
