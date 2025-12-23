import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { BoltIcon, UsersIcon } from '@heroicons/react/24/outline'
import type { RideGroupDto } from '../../hooks/useRide'
import { UserAvatarGroup } from '../common/UserAvatar'
import { MarkdownDisplay } from '../../components/common/MarkdownDisplay'
import { ParticipantListModal } from './ParticipantListModal'

interface RideGroupCardProps {
  group: RideGroupDto
  isJoined?: boolean
  canJoin?: boolean
  onJoin?: () => void
  onLeave?: () => void
  isLoading?: boolean
  onHover?: (groupId: string | null) => void
}

export function RideGroupCard({
  group,
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
          {group.description && (
            <MarkdownDisplay
              content={group.description}
              preview={true}
              maxLength={150}
              className="mt-1 text-sm text-gray-600"
            />
          )}
          <div className="mt-2 flex items-center gap-4 text-sm text-gray-500">
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
