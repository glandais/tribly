import { useTranslation } from 'react-i18next'
import { RideGroup } from '../../hooks/useRide'

interface RideGroupCardProps {
  group: RideGroup
  isJoined?: boolean
  canJoin?: boolean
  onJoin?: () => void
  onLeave?: () => void
  isLoading?: boolean
}

export function RideGroupCard({
  group,
  isJoined,
  canJoin,
  onJoin,
  onLeave,
  isLoading,
}: RideGroupCardProps) {
  const { t } = useTranslation('rides')
  const isFull =
    group.maxParticipants !== null && group.currentParticipants >= group.maxParticipants

  return (
    <div
      className={`bg-white rounded-lg border p-4 ${isJoined ? 'border-indigo-500 ring-1 ring-indigo-500' : 'border-gray-200'}`}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center gap-2">
            <h4 className="text-base font-medium text-gray-900">{group.name}</h4>
            {isJoined && (
              <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-indigo-100 text-indigo-800">
                {t('detail.groups.joined')}
              </span>
            )}
          </div>
          {group.description && <p className="mt-1 text-sm text-gray-600">{group.description}</p>}
          <div className="mt-2 flex items-center gap-4 text-sm text-gray-500">
            {group.averageSpeed && (
              <span className="flex items-center">
                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M13 10V3L4 14h7v7l9-11h-7z"
                  />
                </svg>
                {t('detail.groups.speed', { speed: group.averageSpeed })}
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
              {group.maxParticipants
                ? t('detail.groups.participants', {
                    current: group.currentParticipants,
                    max: group.maxParticipants,
                  })
                : t('detail.groups.participantsNoMax', { current: group.currentParticipants })}
            </span>
          </div>
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
    </div>
  )
}
