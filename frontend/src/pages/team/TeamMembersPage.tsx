import { useState } from 'react'
import { useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { PlusIcon } from '@heroicons/react/24/outline'
import { useGetTeam, getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import {
  useGetMembers,
  useUpdateMemberRole,
  useRemoveMember,
  useAddMember,
  getGetMembersQueryKey,
} from '@/api/endpoints/team-members/team-members'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamMemberList, TeamMemberListSkeleton } from '../../components/team/TeamMemberList'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { UserAutocomplete } from '../../components/common/UserAutocomplete'
import type { PublicUserDto } from '@/api/dto'
import { TeamRole } from '@/api/dto'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { Modal } from '../../components/common/Modal'

export function TeamMembersPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { user } = useAuth()
  const queryClient = useQueryClient()
  const [showAddMember, setShowAddMember] = useState(false)
  const [selectedRole, setSelectedRole] = useState<TeamRole>(TeamRole.MEMBER)
  const [page, setPage] = useState(0)
  const pageSize = 50

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: membersData, isLoading: isLoadingMembers } = useGetMembers(
    teamSlug!,
    { page, size: pageSize },
    { query: { enabled: !!teamSlug } }
  )

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: membersData?.total ?? 0,
  })
  const updateRoleMutation = useUpdateMemberRole()
  const removeMemberMutation = useRemoveMember()
  const addMemberMutation = useAddMember()

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  // Only admins can see member list
  if (team.role !== 'ADMIN') {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const handleAddMember = (selectedUser: PublicUserDto) => {
    if (!teamSlug) return
    addMemberMutation.mutate(
      { slug: teamSlug, data: { userId: selectedUser.id, role: selectedRole } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
          queryClient.invalidateQueries({ queryKey: getGetMembersQueryKey(teamSlug) })
          toast.success(i18next.t('teams.notifications.memberAdded'))
          setShowAddMember(false)
          setSelectedRole(TeamRole.MEMBER)
        },
      }
    )
  }

  const handleUpdateRole = (memberId: string, role: TeamRole) => {
    if (!teamSlug) return
    updateRoleMutation.mutate(
      { slug: teamSlug, memberId, data: { role } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetMembersQueryKey(teamSlug) })
        },
      }
    )
  }

  const handleRemoveMember = (memberId: string) => {
    if (!teamSlug) return
    removeMemberMutation.mutate(
      { slug: teamSlug, memberId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
          queryClient.invalidateQueries({ queryKey: getGetMembersQueryKey(teamSlug) })
          toast.success(i18next.t('teams.notifications.memberRemoved'))
        },
      }
    )
  }

  return (
    <TeamAdminLayout team={team} currentTab="members">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-gray-900">{t('teams.detail.members.title')}</h2>
          <button
            onClick={() => setShowAddMember(true)}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <PlusIcon className="w-4 h-4 mr-2" />
            {t('teams.detail.members.addMember')}
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
              onUpdateRole={handleUpdateRole}
              onRemoveMember={handleRemoveMember}
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
          <p className="text-gray-500">{t('teams.detail.members.empty')}</p>
        )}

        {/* Add Member Modal */}
        <Modal
          isOpen={showAddMember}
          onClose={() => {
            setShowAddMember(false)
            setSelectedRole(TeamRole.MEMBER)
          }}
          title={t('teams.detail.members.addMember')}
          size="md"
          footer={
            <button
              onClick={() => {
                setShowAddMember(false)
                setSelectedRole(TeamRole.MEMBER)
              }}
              disabled={addMemberMutation.isPending}
              className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
            >
              {t('actions.cancelAction')}
            </button>
          }
        >
          <div className="space-y-4">
            <div>
              <label htmlFor="user-search" className="block text-sm font-medium text-gray-700 mb-2">
                {t('teams.detail.members.searchUser')}
              </label>
              <UserAutocomplete
                onSelect={handleAddMember}
                placeholder={t('teams.detail.members.searchPlaceholder')}
              />
            </div>
            <div>
              <label htmlFor="role" className="block text-sm font-medium text-gray-700 mb-2">
                {t('teams.detail.members.role')}
              </label>
              <select
                id="role"
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value as TeamRole)}
                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-xs focus:ring-indigo-500 focus:border-indigo-500"
              >
                <option value={TeamRole.MEMBER}>{t('roles.MEMBER')}</option>
                <option value={TeamRole.ORGANIZER}>{t('roles.ORGANIZER')}</option>
                <option value={TeamRole.ADMIN}>{t('roles.ADMIN')}</option>
              </select>
            </div>
            {addMemberMutation.error && (
              <div className="text-sm text-red-600">
                {addMemberMutation.error instanceof Error
                  ? addMemberMutation.error.message
                  : t('teams.detail.members.addError')}
              </div>
            )}
          </div>
        </Modal>
      </div>
    </TeamAdminLayout>
  )
}
