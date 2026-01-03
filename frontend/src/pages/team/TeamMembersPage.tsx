import { useState } from 'react'
import { useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { PlusIcon } from '@heroicons/react/24/outline'
import {
  useTeam,
  useTeamMembers,
  useUpdateMemberRole,
  useRemoveMember,
  useAddMember,
} from '../../hooks/useTeam'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamMemberList, TeamMemberListSkeleton } from '../../components/team/TeamMemberList'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { UserAutocomplete } from '../../components/common/UserAutocomplete'
import type { PublicUserDto } from '../../hooks/useUserSearch'
import { TeamRole } from '@/api'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { Modal } from '../../components/common/Modal'

export function TeamMembersPage() {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { user } = useAuth()
  const [showAddMember, setShowAddMember] = useState(false)
  const [selectedRole, setSelectedRole] = useState<TeamRole>(TeamRole.Member)
  const [page, setPage] = useState(0)
  const pageSize = 50

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: membersData, isLoading: isLoadingMembers } = useTeamMembers(
    teamSlug,
    page,
    pageSize
  )

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: membersData?.total ?? 0,
  })
  const updateRoleMutation = useUpdateMemberRole(teamSlug || '')
  const removeMemberMutation = useRemoveMember(teamSlug || '')
  const addMemberMutation = useAddMember(teamSlug || '')

  if (isLoadingTeam) {
    return <LoadingPage message={tCommon('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  // Only admins can see member list
  if (team.role !== 'ADMIN') {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const handleAddMember = (selectedUser: PublicUserDto) => {
    addMemberMutation.mutate(
      { userId: selectedUser.id, role: selectedRole },
      {
        onSuccess: () => {
          setShowAddMember(false)
          setSelectedRole(TeamRole.Member)
        },
      }
    )
  }

  return (
    <TeamAdminLayout team={team} currentTab="members">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-900">{t('detail.members.title')}</h2>
          <button
            onClick={() => setShowAddMember(true)}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <PlusIcon className="w-4 h-4 mr-2" />
            {t('detail.members.addMember')}
          </button>
        </div>

        {isLoadingMembers ? (
          <TeamMemberListSkeleton count={5} />
        ) : membersData?.members && membersData.members.length > 0 ? (
          <>
            <TeamMemberList
              members={membersData.members}
              currentUserRole={team.role}
              currentUserId={user?.id ?? null}
              onUpdateRole={(memberId, role) => updateRoleMutation.mutate({ memberId, role })}
              onRemoveMember={(memberId) => removeMemberMutation.mutate(memberId)}
              isUpdating={updateRoleMutation.isPending}
              isRemoving={removeMemberMutation.isPending}
            />

            <Pagination
              currentPage={page}
              totalPages={totalPages}
              onPageChange={setPage}
              className="mt-8"
            />
          </>
        ) : (
          <p className="text-gray-500">{t('detail.members.empty')}</p>
        )}

        {/* Add Member Modal */}
        <Modal
          isOpen={showAddMember}
          onClose={() => {
            setShowAddMember(false)
            setSelectedRole(TeamRole.Member)
          }}
          title={t('detail.members.addMember')}
          size="md"
          footer={
            <button
              onClick={() => {
                setShowAddMember(false)
                setSelectedRole(TeamRole.Member)
              }}
              disabled={addMemberMutation.isPending}
              className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
            >
              {tCommon('actions.cancelAction')}
            </button>
          }
        >
          <div className="space-y-4">
            <div>
              <label htmlFor="user-search" className="block text-sm font-medium text-gray-700 mb-2">
                {t('detail.members.searchUser')}
              </label>
              <UserAutocomplete
                onSelect={handleAddMember}
                placeholder={t('detail.members.searchPlaceholder')}
              />
            </div>
            <div>
              <label htmlFor="role" className="block text-sm font-medium text-gray-700 mb-2">
                {t('detail.members.role')}
              </label>
              <select
                id="role"
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value as TeamRole)}
                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-xs focus:ring-indigo-500 focus:border-indigo-500"
              >
                <option value={TeamRole.Member}>{tCommon('roles.MEMBER')}</option>
                <option value={TeamRole.Organizer}>{tCommon('roles.ORGANIZER')}</option>
                <option value={TeamRole.Admin}>{tCommon('roles.ADMIN')}</option>
              </select>
            </div>
            {addMemberMutation.error && (
              <div className="text-sm text-red-600">
                {addMemberMutation.error instanceof Error
                  ? addMemberMutation.error.message
                  : t('detail.members.addError')}
              </div>
            )}
          </div>
        </Modal>
      </div>
    </TeamAdminLayout>
  )
}
