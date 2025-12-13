import { Link, useParams } from 'react-router-dom';
import {
  useTeam,
  useTeamMembers,
  useJoinTeam,
  useLeaveTeam,
  useUpdateMemberRole,
  useRemoveMember,
} from '../../hooks/useTeam';
import { useAuth } from '../../hooks/useAuth';
import { LoadingPage } from '../../components/common/LoadingSpinner';
import { TeamMemberList, TeamMemberListSkeleton } from '../../components/team/TeamMemberList';

export function TeamDetailPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>();
  const { user, isAuthenticated } = useAuth();

  const { data: team, isLoading, error } = useTeam(teamSlug);
  const { data: membersData, isLoading: isLoadingMembers } = useTeamMembers(teamSlug);

  const joinMutation = useJoinTeam(teamSlug || '');
  const leaveMutation = useLeaveTeam(teamSlug || '');
  const updateRoleMutation = useUpdateMemberRole(teamSlug || '');
  const removeMemberMutation = useRemoveMember(teamSlug || '');

  if (isLoading) {
    return <LoadingPage message="Loading team..." />;
  }

  if (error) {
    const is404 = error instanceof Error && error.message.includes('not found');
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">
            {is404 ? 'Team Not Found' : 'Error Loading Team'}
          </h1>
          <p className="text-gray-600 mb-6">
            {is404
              ? "The team you're looking for doesn't exist or has been removed."
              : 'There was an error loading this team. Please try again.'}
          </p>
          <Link
            to="/teams"
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            Back to Teams
          </Link>
        </div>
      </div>
    );
  }

  if (!team) {
    return null;
  }

  const isMember = !!team.userRole;
  const isAdmin = team.userRole === 'ADMIN';
  const canJoin = isAuthenticated && !isMember && team.isPublic;
  const canLeave = isMember && !isAdmin;

  const handleJoin = () => {
    joinMutation.mutate();
  };

  const handleLeave = () => {
    if (confirm('Are you sure you want to leave this team?')) {
      leaveMutation.mutate();
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="relative mb-8">
        {team.coverImageUrl ? (
          <img
            src={team.coverImageUrl}
            alt=""
            className="w-full h-48 sm:h-64 object-cover rounded-lg"
          />
        ) : (
          <div className="w-full h-48 sm:h-64 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-lg" />
        )}

        <div className="absolute -bottom-12 left-6 flex items-end gap-4">
          {team.logoUrl ? (
            <img
              src={team.logoUrl}
              alt={team.name}
              className="w-24 h-24 rounded-xl border-4 border-white bg-white shadow-lg"
            />
          ) : (
            <div className="w-24 h-24 rounded-xl border-4 border-white bg-indigo-600 shadow-lg flex items-center justify-center">
              <span className="text-3xl font-bold text-white">
                {team.name.charAt(0).toUpperCase()}
              </span>
            </div>
          )}
        </div>
      </div>

      <div className="pt-14 sm:pt-8 sm:pl-32">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-3xl font-bold text-gray-900">{team.name}</h1>
              {!team.isPublic && (
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                  <svg
                    className="w-3 h-3 mr-1"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                      clipRule="evenodd"
                    />
                  </svg>
                  Private
                </span>
              )}
            </div>
            {team.description && (
              <p className="mt-2 text-gray-600 max-w-2xl">{team.description}</p>
            )}
            <div className="mt-3 flex items-center gap-4 text-sm text-gray-500">
              <span className="flex items-center">
                <svg
                  className="w-4 h-4 mr-1"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                  />
                </svg>
                {team.memberCount} {team.memberCount === 1 ? 'member' : 'members'}
                {team.maxMembers && ` / ${team.maxMembers} max`}
              </span>
              {team.createdAt && (
                <span>
                  Created{' '}
                  {new Date(team.createdAt).toLocaleDateString(undefined, {
                    year: 'numeric',
                    month: 'long',
                  })}
                </span>
              )}
            </div>
          </div>

          <div className="flex items-center gap-3">
            {canJoin && (
              <button
                onClick={handleJoin}
                disabled={joinMutation.isPending}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
              >
                {joinMutation.isPending ? 'Joining...' : 'Join Team'}
              </button>
            )}

            {canLeave && (
              <button
                onClick={handleLeave}
                disabled={leaveMutation.isPending}
                className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
              >
                {leaveMutation.isPending ? 'Leaving...' : 'Leave Team'}
              </button>
            )}

            {isAdmin && (
              <Link
                to={`/teams/${teamSlug}/settings`}
                className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                <svg
                  className="w-4 h-4 mr-2"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
                  />
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                </svg>
                Settings
              </Link>
            )}
          </div>
        </div>
      </div>

      {/* Team Navigation */}
      {isMember && (
        <div className="mt-8 border-b border-gray-200">
          <nav className="-mb-px flex space-x-8">
            <Link
              to={`/teams/${teamSlug}`}
              className="border-indigo-500 text-indigo-600 py-4 px-1 border-b-2 font-medium text-sm"
            >
              Overview
            </Link>
            <Link
              to={`/teams/${teamSlug}/rides`}
              className="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 py-4 px-1 border-b-2 font-medium text-sm"
            >
              Rides
            </Link>
            <Link
              to={`/teams/${teamSlug}/trips`}
              className="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 py-4 px-1 border-b-2 font-medium text-sm"
            >
              Trips
            </Link>
            <Link
              to={`/teams/${teamSlug}/routes`}
              className="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 py-4 px-1 border-b-2 font-medium text-sm"
            >
              Routes
            </Link>
          </nav>
        </div>
      )}

      {/* Members Section */}
      <div className="mt-8">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Members</h2>
        {isLoadingMembers ? (
          <TeamMemberListSkeleton count={5} />
        ) : membersData?.members && membersData.members.length > 0 ? (
          <TeamMemberList
            members={membersData.members}
            currentUserRole={team.userRole}
            currentUserId={user?.dbId ?? null}
            onUpdateRole={(memberId, role) =>
              updateRoleMutation.mutate({ memberId, role })
            }
            onRemoveMember={(memberId) => removeMemberMutation.mutate(memberId)}
            isUpdating={updateRoleMutation.isPending}
            isRemoving={removeMemberMutation.isPending}
          />
        ) : (
          <p className="text-gray-500">No members found.</p>
        )}
      </div>
    </div>
  );
}
