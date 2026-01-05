import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { UserAvatar } from '../common/UserAvatar'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { ConfirmDialog } from '../common/ConfirmDialog'
import type { MemberDto } from '@/api/dto'
import { TeamRole } from '@/api/dto'
import { useFormattedDate } from '../../utils/dateFormat'

interface TeamMemberListProps {
  members: MemberDto[]
  currentUserRole: TeamRole | null
  currentUserId: string | null
  onUpdateRole?: (memberId: string, role: TeamRole) => void
  onRemoveMember?: (memberId: string) => void
  isUpdating?: boolean
  isRemoving?: boolean
}

const roleBadgeColors = {
  ADMIN: 'bg-purple-100 text-purple-800',
  ORGANIZER: 'bg-blue-100 text-blue-800',
  MEMBER: 'bg-gray-100 text-gray-800',
}

export function TeamMemberList({
  members,
  currentUserRole,
  currentUserId,
  onUpdateRole,
  onRemoveMember,
  isUpdating = false,
  isRemoving = false,
}: TeamMemberListProps) {
  const { t } = useTranslation()
  const { formatDate } = useFormattedDate()
  const [editingMemberId, setEditingMemberId] = useState<string | null>(null)
  const [selectedRole, setSelectedRole] = useState<TeamRole>(TeamRole.MEMBER)
  const [showRemoveConfirm, setShowRemoveConfirm] = useState(false)
  const [memberToRemove, setMemberToRemove] = useState<string | null>(null)

  const canManageMembers = currentUserRole === TeamRole.ADMIN
  const canAssignOrganizers =
    currentUserRole === TeamRole.ADMIN || currentUserRole === TeamRole.ORGANIZER

  const handleRoleChange = (memberId: string) => {
    if (onUpdateRole) {
      onUpdateRole(memberId, selectedRole)
      setEditingMemberId(null)
    }
  }

  const handleRemove = (memberId: string) => {
    setMemberToRemove(memberId)
    setShowRemoveConfirm(true)
  }

  const handleConfirmRemove = () => {
    if (onRemoveMember && memberToRemove) {
      onRemoveMember(memberToRemove)
      setShowRemoveConfirm(false)
      setMemberToRemove(null)
    }
  }

  return (
    <div className="bg-white shadow-xs rounded-lg border border-gray-200">
      <ul className="divide-y divide-gray-200">
        {members.map((member) => {
          const isCurrentUser = member.user.id === currentUserId
          const canEdit = canManageMembers && !isCurrentUser && member.role !== 'ADMIN'
          const canRemove = canManageMembers && !isCurrentUser

          return (
            <li key={member.id} className="p-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center min-w-0">
                  <UserAvatar
                    user={{
                      displayName: member.user.displayName,
                      avatarUrl: member.user.avatarUrl,
                    }}
                    size="md"
                  />
                  <div className="ml-3 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      {member.user.displayName}
                      {isCurrentUser && (
                        <span className="ml-2 text-xs text-gray-500">
                          {t('teams.detail.members.you')}
                        </span>
                      )}
                    </p>
                    <p className="text-xs text-gray-400">
                      {t('teams.detail.members.joined', {
                        date: formatDate(member.joinedAt) || t('unknown'),
                      })}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {editingMemberId === member.id ? (
                    <>
                      <select
                        value={selectedRole}
                        onChange={(e) => setSelectedRole(e.target.value as TeamRole)}
                        className="text-sm border border-gray-300 rounded-md px-2 py-1 focus:ring-indigo-500 focus:border-indigo-500"
                        disabled={isUpdating}
                      >
                        {canManageMembers && <option value="ADMIN">{t('roles.ADMIN')}</option>}
                        {canAssignOrganizers && (
                          <option value="ORGANIZER">{t('roles.ORGANIZER')}</option>
                        )}
                        <option value="MEMBER">{t('roles.MEMBER')}</option>
                      </select>
                      <button
                        onClick={() => handleRoleChange(member.user.id)}
                        disabled={isUpdating}
                        className="text-sm text-indigo-600 hover:text-indigo-900 disabled:opacity-50"
                      >
                        {isUpdating ? <LoadingSpinner size="sm" /> : t('actions.save')}
                      </button>
                      <button
                        onClick={() => setEditingMemberId(null)}
                        disabled={isUpdating}
                        className="text-sm text-gray-600 hover:text-gray-900 disabled:opacity-50"
                      >
                        {t('actions.cancelAction')}
                      </button>
                    </>
                  ) : (
                    <>
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${roleBadgeColors[member.role]}`}
                      >
                        {t(`roles.${member.role satisfies 'ADMIN' | 'ORGANIZER' | 'MEMBER'}`)}
                      </span>

                      {canEdit && (
                        <button
                          onClick={() => {
                            setSelectedRole(member.role)
                            setEditingMemberId(member.id)
                          }}
                          className="text-sm text-gray-600 hover:text-gray-900"
                        >
                          {t('actions.edit')}
                        </button>
                      )}

                      {canRemove && (
                        <button
                          onClick={() => handleRemove(member.user.id)}
                          disabled={isRemoving}
                          className="text-sm text-red-600 hover:text-red-900 disabled:opacity-50"
                        >
                          {isRemoving ? (
                            <LoadingSpinner size="sm" />
                          ) : (
                            t('teams.detail.members.remove')
                          )}
                        </button>
                      )}
                    </>
                  )}
                </div>
              </div>
            </li>
          )
        })}
      </ul>

      <ConfirmDialog
        isOpen={showRemoveConfirm}
        onClose={() => {
          setShowRemoveConfirm(false)
          setMemberToRemove(null)
        }}
        onConfirm={handleConfirmRemove}
        title={t('teams.detail.members.remove')}
        message={t('teams.detail.members.confirmRemove')}
        confirmText={t('teams.detail.members.remove')}
        variant="danger"
        isLoading={isRemoving}
      />
    </div>
  )
}

export function TeamMemberListSkeleton({ count = 3 }: { count?: number }) {
  return (
    <div className="bg-white shadow-xs rounded-lg border border-gray-200">
      <ul className="divide-y divide-gray-200">
        {Array.from({ length: count }).map((_, i) => (
          <li key={i} className="p-4 animate-pulse">
            <div className="flex items-center">
              <div className="w-10 h-10 bg-gray-200 rounded-full" />
              <div className="ml-3 flex-1">
                <div className="h-4 bg-gray-200 rounded-sm w-1/4 mb-2" />
                <div className="h-3 bg-gray-200 rounded-sm w-1/3" />
              </div>
              <div className="h-5 bg-gray-200 rounded-sm w-16" />
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
