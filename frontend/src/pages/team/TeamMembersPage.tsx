import { useState, useCallback } from 'react'
import { useParams, Navigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { IconPlus } from '@tabler/icons-react'
import { Alert, Box, Button, Group, Modal, Select, Stack, Text, Title } from '@mantine/core'
import { useGetTeam, getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import {
  useGetMembers,
  useUpdateMemberRole,
  useRemoveMember,
  useAddMember,
  getGetMembersQueryKey,
  getMembers,
} from '@/api/endpoints/team-members/team-members'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamMemberList, TeamMemberListSkeleton } from '../../components/team/TeamMemberList'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { UserAutocomplete } from '../../components/common/UserAutocomplete'
import type { PublicUserDto } from '@/api/dto'
import { TeamRole } from '@/api/dto'
import { Pagination } from '../../components/common/Pagination'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'

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

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getGetMembersQueryKey(teamSlug, { page: prefetchPageNum, size: pageSize }),
      queryFn: () => getMembers(teamSlug!, { page: prefetchPageNum, size: pageSize }),
    }),
    [teamSlug, pageSize]
  )

  const { totalPages } = usePaginatedQuery({
    page,
    pageSize,
    totalItems: membersData?.total ?? 0,
    prefetchPage,
  })

  const updateRoleMutation = useUpdateMemberRole()
  const removeMemberMutation = useRemoveMember()
  const addMemberMutation = useAddMember()

  useCanonicalPath(team ? paths.teamMembers(team.slug) : undefined)

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
      { teamSlug: teamSlug, data: { userId: selectedUser.id, role: selectedRole } },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
          queryClient.invalidateQueries({ queryKey: getGetMembersQueryKey(teamSlug) })
          notifications.show({
            message: i18next.t('teams.notifications.memberAdded'),
            color: 'green',
          })
          setShowAddMember(false)
          setSelectedRole(TeamRole.MEMBER)
        },
      }
    )
  }

  const handleUpdateRole = (memberId: string, role: TeamRole) => {
    if (!teamSlug) return
    updateRoleMutation.mutate(
      { teamSlug: teamSlug, memberId, data: { role } },
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
      { teamSlug: teamSlug, memberId },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug) })
          queryClient.invalidateQueries({ queryKey: getGetMembersQueryKey(teamSlug) })
          notifications.show({
            message: i18next.t('teams.notifications.memberRemoved'),
            color: 'green',
          })
        },
      }
    )
  }

  return (
    <TeamAdminLayout team={team} currentTab="members">
      <Box py="md">
        <Group justify="space-between" mb="lg">
          <Title order={2}>{t('teams.detail.members.title')}</Title>
          <Button onClick={() => setShowAddMember(true)} leftSection={<IconPlus size={16} />}>
            {t('teams.detail.members.addMember')}
          </Button>
        </Group>

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

            <Box mt="xl">
              <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
            </Box>
          </>
        ) : (
          <Text c="dimmed">{t('teams.detail.members.empty')}</Text>
        )}

        {/* Add Member Modal */}
        <Modal
          opened={showAddMember}
          onClose={() => {
            setShowAddMember(false)
            setSelectedRole(TeamRole.MEMBER)
          }}
          title={t('teams.detail.members.addMember')}
          size="md"
        >
          <Stack gap="md">
            <Box>
              <Text size="sm" fw={500} mb="xs">
                {t('teams.detail.members.searchUser')}
              </Text>
              <UserAutocomplete
                onSelect={handleAddMember}
                placeholder={t('teams.detail.members.searchPlaceholder')}
              />
            </Box>
            <Select
              label={t('teams.detail.members.role')}
              value={selectedRole}
              onChange={(value) => setSelectedRole(value as TeamRole)}
              data={[
                { value: TeamRole.MEMBER, label: t('roles.MEMBER') },
                { value: TeamRole.ORGANIZER, label: t('roles.ORGANIZER') },
                { value: TeamRole.ADMIN, label: t('roles.ADMIN') },
              ]}
            />
            {addMemberMutation.error && (
              <Alert color="red">
                {addMemberMutation.error instanceof Error
                  ? addMemberMutation.error.message
                  : t('teams.detail.members.addError')}
              </Alert>
            )}
          </Stack>
          <Box mt="md">
            <Button
              variant="default"
              onClick={() => {
                setShowAddMember(false)
                setSelectedRole(TeamRole.MEMBER)
              }}
              disabled={addMemberMutation.isPending}
            >
              {t('actions.cancelAction')}
            </Button>
          </Box>
        </Modal>
      </Box>
    </TeamAdminLayout>
  )
}
