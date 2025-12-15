import { useParams, Navigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useTeam, useTeamMembers, useUpdateMemberRole, useRemoveMember } from '../../hooks/useTeam';
import { useAuth } from '../../hooks/useAuth';
import { LoadingPage } from '../../components/common/LoadingSpinner';
import { TeamMemberList, TeamMemberListSkeleton } from '../../components/team/TeamMemberList';
import { TeamLayout } from '../../components/team/TeamLayout';

export function TeamMembersPage() {
  const { t } = useTranslation('teams');
  const { teamSlug } = useParams<{ teamSlug: string }>();
  const { user } = useAuth();

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug);
  const { data: membersData, isLoading: isLoadingMembers } = useTeamMembers(teamSlug);
  const updateRoleMutation = useUpdateMemberRole(teamSlug || '');
  const removeMemberMutation = useRemoveMember(teamSlug || '');

  if (isLoadingTeam) {
    return <LoadingPage message={t('detail.loading')} />;
  }

  if (!team) {
    return <Navigate to="/teams" replace />;
  }

  // Only admins can see member list
  if (team.userRole !== 'ADMIN') {
    return <Navigate to={`/teams/${teamSlug}/rides`} replace />;
  }

  return (
    <TeamLayout team={team} currentTab="members">
      <div className="py-6">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">{t('detail.members.title')}</h2>
        {isLoadingMembers ? (
          <TeamMemberListSkeleton count={5} />
        ) : membersData?.members && membersData.members.length > 0 ? (
          <TeamMemberList
            members={membersData.members}
            currentUserRole={team.userRole}
            currentUserId={user?.dbId ?? null}
            onUpdateRole={(memberId, role) => updateRoleMutation.mutate({ memberId, role })}
            onRemoveMember={(memberId) => removeMemberMutation.mutate(memberId)}
            isUpdating={updateRoleMutation.isPending}
            isRemoving={removeMemberMutation.isPending}
          />
        ) : (
          <p className="text-gray-500">{t('detail.members.empty')}</p>
        )}
      </div>
    </TeamLayout>
  );
}
