import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  BoltIcon,
  UsersIcon,
  ClockIcon,
  MapIcon,
  ArrowDownTrayIcon,
} from '@heroicons/react/24/outline'
import type { RideGroupDto } from '../../hooks/useRide'
import { useRoute } from '../../hooks/useRoute'
import { UserAvatarGroup } from '../common/UserAvatar'
import { ParticipantListModal } from './ParticipantListModal'

interface RideGroupCardProps {
  group: RideGroupDto
  teamSlug: string
  rideRouteSlug?: string
  isJoined?: boolean
  canJoin?: boolean
  onJoin?: () => void
  onLeave?: () => void
  isLoading?: boolean
  onHover?: (groupId: string | null) => void
}

export function RideGroupCard({
  group,
  teamSlug,
  rideRouteSlug,
  isJoined,
  canJoin,
  onJoin,
  onLeave,
  isLoading,
  onHover,
}: RideGroupCardProps) {
  const { t } = useTranslation('rides')
  const [showParticipants, setShowParticipants] = useState(false)
  const isFull = group.maxParticipants && group.countParticipants >= group.maxParticipants

  // Determine effective route slug (group route or ride route as fallback)
  const effectiveRouteSlug = group.routeSlug || rideRouteSlug

  // Fetch route details for download links
  const { data: route } = useRoute(teamSlug, effectiveRouteSlug)

  return (
    <div
      className={`bg-white rounded-lg border p-4 ${isJoined ? 'border-indigo-500 ring-1 ring-indigo-500' : 'border-gray-200'}`}
      onMouseEnter={() => onHover?.(group.id)}
      onMouseLeave={() => onHover?.(null)}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center gap-2">
            <h4 className="text-base font-medium text-gray-900">{group.name}</h4>
            {isJoined && (
              <span className="inline-flex items-center px-2 py-0.5 rounded-sm text-xs font-medium bg-indigo-100 text-indigo-800">
                {t('detail.groups.joined')}
              </span>
            )}
          </div>
          <div className="mt-2 flex items-center gap-4 text-sm text-gray-500">
            {group.time && (
              <span className="flex items-center">
                <ClockIcon className="w-4 h-4 mr-1" />
                {group.time}
              </span>
            )}
            {group.averageSpeed && (
              <span className="flex items-center">
                <BoltIcon className="w-4 h-4 mr-1" />
                {t('detail.groups.speed', { speed: group.averageSpeed })}
              </span>
            )}
            <button
              onClick={() => setShowParticipants(true)}
              className="flex items-center gap-2 rounded-lg p-1 -ml-1 transition-all hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
              title={t('detail.groups.viewParticipants')}
            >
              {group.participants.length > 0 && (
                <UserAvatarGroup users={group.participants} max={5} size="sm" />
              )}
              <span className="flex items-center text-sm text-gray-500">
                <UsersIcon className="w-4 h-4 mr-1" />
                {group.maxParticipants
                  ? t('detail.groups.participants', {
                      current: group.countParticipants,
                      max: group.maxParticipants,
                    })
                  : t('detail.groups.participantsNoMax', { current: group.countParticipants })}
              </span>
              {group.participants.length > 5 && (
                <span className="text-xs font-medium text-indigo-600 group-hover:text-indigo-700">
                  {t('detail.groups.viewAll')}
                </span>
              )}
            </button>
          </div>

          {/* Route actions */}
          {effectiveRouteSlug && (
            <div className="mt-3 flex items-center gap-2">
              <Link
                to={`/teams/${teamSlug}/routes/${effectiveRouteSlug}`}
                className="inline-flex items-center px-2 py-1 text-xs font-medium text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded"
              >
                <MapIcon className="w-4 h-4 mr-1" />
                {t('detail.groups.route.view')}
              </Link>
              {route?.media?.assets?.gpx?.url && (
                <a
                  href={route.media.assets.gpx.url}
                  className="inline-flex items-center px-2 py-1 text-xs font-medium text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded"
                  download
                >
                  <ArrowDownTrayIcon className="w-4 h-4 mr-1" />
                  GPX
                </a>
              )}
              {route?.media?.assets?.fit?.url && (
                <a
                  href={route.media.assets.fit.url}
                  className="inline-flex items-center px-2 py-1 text-xs font-medium text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded"
                  download
                >
                  <ArrowDownTrayIcon className="w-4 h-4 mr-1" />
                  FIT
                </a>
              )}
            </div>
          )}
        </div>

        {(canJoin || isJoined) && (
          <div className="ml-4">
            {isJoined ? (
              <button
                onClick={onLeave}
                disabled={isLoading}
                className="inline-flex items-center px-3 py-1.5 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
              >
                {t('detail.groups.leave')}
              </button>
            ) : canJoin && !isFull ? (
              <button
                onClick={onJoin}
                disabled={isLoading}
                className="inline-flex items-center px-3 py-1.5 border border-transparent rounded-md text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
              >
                {t('detail.groups.join')}
              </button>
            ) : isFull ? (
              <span className="inline-flex items-center px-3 py-1.5 rounded-md text-sm font-medium text-gray-500 bg-gray-100">
                {t('detail.groups.full')}
              </span>
            ) : null}
          </div>
        )}
      </div>

      <ParticipantListModal
        isOpen={showParticipants}
        onClose={() => setShowParticipants(false)}
        participants={group.participants}
        groupName={group.name}
      />
    </div>
  )
}
