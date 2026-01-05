import { useTranslation } from 'react-i18next'
import { UsersIcon, ChevronRightIcon } from '@heroicons/react/24/outline'
import { ShieldCheckIcon } from '@heroicons/react/20/solid'
import { UserAvatar } from '../common/UserAvatar'
import { Modal } from '../common/Modal'

interface Participant {
  id: string
  displayName: string
  avatarUrl?: string
  isOrganizer?: boolean
}

interface ParticipantListModalProps {
  isOpen: boolean
  onClose: () => void
  participants: Participant[]
  groupName: string
}

export function ParticipantListModal({
  isOpen,
  onClose,
  participants,
  groupName,
}: ParticipantListModalProps) {
  const { t } = useTranslation()

  const participantCount = participants.length

  const headerSubtitle = (
    <p className="text-sm font-semibold text-indigo-600">
      {t('rides.detail.groups.participantsNoMax', { current: participantCount })}
    </p>
  )

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={groupName} size="md">
      {headerSubtitle}

      <div className="mt-4">
        {participants.length === 0 ? (
          <div className="py-12 text-center">
            <UsersIcon className="mx-auto h-12 w-12 text-gray-300" />
            <p className="mt-3 text-sm font-medium text-gray-500">
              {t('rides.detail.groups.empty')}
            </p>
          </div>
        ) : (
          <ul className="space-y-2" role="list">
            {participants.map((participant) => (
              <li
                key={participant.id}
                className="group relative flex items-center gap-3 rounded-lg p-3 transition-all duration-200 hover:bg-gradient-to-r hover:from-indigo-50 hover:to-purple-50 hover:shadow-sm"
              >
                {/* Avatar with organizer badge */}
                <div className="relative flex-shrink-0">
                  <UserAvatar user={participant} size="md" />
                  {participant.isOrganizer && (
                    <div
                      className="absolute -bottom-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full bg-indigo-600 ring-2 ring-white shadow-sm"
                      title={t('roles.ORGANIZER')}
                    >
                      <ShieldCheckIcon className="h-3 w-3 text-white" />
                    </div>
                  )}
                </div>

                {/* Participant info */}
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-semibold text-gray-900 truncate transition-colors group-hover:text-indigo-900">
                    {participant.displayName}
                  </p>
                  {participant.isOrganizer && (
                    <p className="text-xs text-gray-500 mt-0.5">{t('groups.groupOrganizer')}</p>
                  )}
                </div>

                {/* Subtle hover indicator */}
                <div className="flex-shrink-0 opacity-0 transition-opacity group-hover:opacity-100">
                  <ChevronRightIcon className="h-4 w-4 text-indigo-400" />
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </Modal>
  )
}
