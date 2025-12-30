import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { XMarkIcon, UsersIcon, ChevronRightIcon } from '@heroicons/react/24/outline'
import { ShieldCheckIcon } from '@heroicons/react/20/solid'
import { UserAvatar } from '../common/UserAvatar'

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
  const { t } = useTranslation(['rides', 'common'])

  // Handle escape key and body scroll lock
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose()
    }

    if (isOpen) {
      document.addEventListener('keydown', handleEscape)
      document.body.style.overflow = 'hidden'
    }

    return () => {
      document.removeEventListener('keydown', handleEscape)
      document.body.style.overflow = 'unset'
    }
  }, [isOpen, onClose])

  if (!isOpen) return null

  const participantCount = participants.length

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Backdrop with blur effect */}
      <div
        className="fixed inset-0 bg-black bg-opacity-50 backdrop-blur-sm transition-opacity"
        onClick={onClose}
        aria-hidden="true"
        style={{ animation: 'fadeIn 0.2s ease-out' }}
      />

      {/* Modal Container */}
      <div className="flex min-h-full items-center justify-center p-4">
        <div
          className="relative w-full max-w-md transform overflow-hidden rounded-xl bg-white shadow-2xl transition-all"
          onClick={(e) => e.stopPropagation()}
          style={{ animation: 'modalSlideIn 0.3s ease-out' }}
          role="dialog"
          aria-modal="true"
          aria-labelledby="modal-title"
        >
          {/* Header with gradient accent */}
          <div className="relative border-b border-gray-100 bg-gradient-to-br from-indigo-50 via-white to-white px-6 py-5">
            <div className="flex items-start justify-between">
              <div className="flex-1 min-w-0">
                <h2
                  id="modal-title"
                  className="text-xl font-bold text-gray-900 tracking-tight truncate"
                >
                  {groupName}
                </h2>
                <p className="mt-1.5 text-sm font-semibold text-indigo-600">
                  {t('detail.groups.participantsNoMax', { current: participantCount })}
                </p>
              </div>
              <button
                onClick={onClose}
                className="ml-4 flex-shrink-0 rounded-lg p-1.5 text-gray-400 transition-all hover:bg-white hover:text-gray-600 hover:shadow-md focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                aria-label={t('common:aria.closeDialog')}
              >
                <XMarkIcon className="h-5 w-5" />
              </button>
            </div>
          </div>

          {/* Participant List with scroll */}
          <div className="max-h-[28rem] overflow-y-auto px-6 py-4">
            {participants.length === 0 ? (
              <div className="py-12 text-center">
                <UsersIcon className="mx-auto h-12 w-12 text-gray-300" />
                <p className="mt-3 text-sm font-medium text-gray-500">{t('detail.groups.empty')}</p>
              </div>
            ) : (
              <ul className="space-y-2" role="list">
                {participants.map((participant, index) => (
                  <li
                    key={participant.id}
                    className="group relative flex items-center gap-3 rounded-lg p-3 transition-all duration-200 hover:bg-gradient-to-r hover:from-indigo-50 hover:to-purple-50 hover:shadow-sm"
                    style={{
                      animationDelay: `${index * 30}ms`,
                      animation: 'slideInLeft 0.3s ease-out both',
                    }}
                  >
                    {/* Avatar with organizer badge */}
                    <div className="relative flex-shrink-0">
                      <UserAvatar user={participant} size="md" />
                      {participant.isOrganizer && (
                        <div
                          className="absolute -bottom-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full bg-indigo-600 ring-2 ring-white shadow-sm"
                          title={t('common:roles.ORGANIZER')}
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
                        <p className="text-xs text-gray-500 mt-0.5">{t('common:groupOrganizer')}</p>
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
        </div>
      </div>

      {/* Animations */}
      <style>{`
        @keyframes fadeIn {
          from {
            opacity: 0;
          }
          to {
            opacity: 1;
          }
        }

        @keyframes modalSlideIn {
          from {
            opacity: 0;
            transform: scale(0.95) translateY(10px);
          }
          to {
            opacity: 1;
            transform: scale(1) translateY(0);
          }
        }

        @keyframes slideInLeft {
          from {
            opacity: 0;
            transform: translateX(-10px);
          }
          to {
            opacity: 1;
            transform: translateX(0);
          }
        }
      `}</style>
    </div>
  )
}
