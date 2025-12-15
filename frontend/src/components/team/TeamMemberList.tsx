import { useState } from 'react';
import { UserAvatar } from '../common/UserAvatar';
import { LoadingSpinner } from '../common/LoadingSpinner';
import type { TeamMember } from '../../hooks/useTeam';

interface TeamMemberListProps {
  members: TeamMember[];
  currentUserRole: 'ADMIN' | 'ORGANIZER' | 'MEMBER' | null;
  currentUserId: string | null;
  onUpdateRole?: (memberId: string, role: 'ADMIN' | 'ORGANIZER' | 'MEMBER') => void;
  onRemoveMember?: (memberId: string) => void;
  isUpdating?: boolean;
  isRemoving?: boolean;
}

const roleLabels = {
  ADMIN: 'Admin',
  ORGANIZER: 'Organizer',
  MEMBER: 'Member',
};

const roleBadgeColors = {
  ADMIN: 'bg-purple-100 text-purple-800',
  ORGANIZER: 'bg-blue-100 text-blue-800',
  MEMBER: 'bg-gray-100 text-gray-800',
};

export function TeamMemberList({
  members,
  currentUserRole,
  currentUserId,
  onUpdateRole,
  onRemoveMember,
  isUpdating = false,
  isRemoving = false,
}: TeamMemberListProps) {
  const [editingMemberId, setEditingMemberId] = useState<string | null>(null);
  const [selectedRole, setSelectedRole] = useState<'ADMIN' | 'ORGANIZER' | 'MEMBER'>('MEMBER');

  const canManageMembers = currentUserRole === 'ADMIN';
  const canAssignOrganizers = currentUserRole === 'ADMIN' || currentUserRole === 'ORGANIZER';

  const handleRoleChange = (memberId: string) => {
    if (onUpdateRole) {
      onUpdateRole(memberId, selectedRole);
      setEditingMemberId(null);
    }
  };

  const handleRemove = (memberId: string) => {
    if (onRemoveMember && confirm('Are you sure you want to remove this member?')) {
      onRemoveMember(memberId);
    }
  };

  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return 'Unknown';
    return new Date(dateStr).toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  return (
    <div className="bg-white shadow-sm rounded-lg border border-gray-200">
      <ul className="divide-y divide-gray-200">
        {members.map((member) => {
          const isCurrentUser = member.userId === currentUserId;
          const canEdit =
            canManageMembers &&
            !isCurrentUser &&
            member.role !== 'ADMIN';
          const canRemove = canManageMembers && !isCurrentUser;

          return (
            <li key={member.id} className="p-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center min-w-0">
                  <UserAvatar
                    user={{
                      displayName: member.displayName,
                      avatarUrl: member.avatarUrl,
                    }}
                    size="md"
                  />
                  <div className="ml-3 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      {member.displayName}
                      {isCurrentUser && (
                        <span className="ml-2 text-xs text-gray-500">(you)</span>
                      )}
                    </p>
                    <p className="text-sm text-gray-500 truncate">{member.email}</p>
                    <p className="text-xs text-gray-400">
                      Joined {formatDate(member.joinedAt)}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {editingMemberId === member.id ? (
                    <>
                      <select
                        value={selectedRole}
                        onChange={(e) =>
                          setSelectedRole(e.target.value as 'ADMIN' | 'ORGANIZER' | 'MEMBER')
                        }
                        className="text-sm border border-gray-300 rounded-md px-2 py-1 focus:ring-indigo-500 focus:border-indigo-500"
                        disabled={isUpdating}
                      >
                        {canManageMembers && <option value="ADMIN">Admin</option>}
                        {canAssignOrganizers && <option value="ORGANIZER">Organizer</option>}
                        <option value="MEMBER">Member</option>
                      </select>
                      <button
                        onClick={() => handleRoleChange(member.id)}
                        disabled={isUpdating}
                        className="text-sm text-indigo-600 hover:text-indigo-900 disabled:opacity-50"
                      >
                        {isUpdating ? <LoadingSpinner size="sm" /> : 'Save'}
                      </button>
                      <button
                        onClick={() => setEditingMemberId(null)}
                        disabled={isUpdating}
                        className="text-sm text-gray-600 hover:text-gray-900 disabled:opacity-50"
                      >
                        Cancel
                      </button>
                    </>
                  ) : (
                    <>
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${roleBadgeColors[member.role]}`}
                      >
                        {roleLabels[member.role]}
                      </span>

                      {canEdit && (
                        <button
                          onClick={() => {
                            setSelectedRole(member.role);
                            setEditingMemberId(member.id);
                          }}
                          className="text-sm text-gray-600 hover:text-gray-900"
                        >
                          Edit
                        </button>
                      )}

                      {canRemove && (
                        <button
                          onClick={() => handleRemove(member.id)}
                          disabled={isRemoving}
                          className="text-sm text-red-600 hover:text-red-900 disabled:opacity-50"
                        >
                          {isRemoving ? <LoadingSpinner size="sm" /> : 'Remove'}
                        </button>
                      )}
                    </>
                  )}
                </div>
              </div>
            </li>
          );
        })}
      </ul>
    </div>
  );
}

export function TeamMemberListSkeleton({ count = 3 }: { count?: number }) {
  return (
    <div className="bg-white shadow-sm rounded-lg border border-gray-200">
      <ul className="divide-y divide-gray-200">
        {Array.from({ length: count }).map((_, i) => (
          <li key={i} className="p-4 animate-pulse">
            <div className="flex items-center">
              <div className="w-10 h-10 bg-gray-200 rounded-full" />
              <div className="ml-3 flex-1">
                <div className="h-4 bg-gray-200 rounded w-1/4 mb-2" />
                <div className="h-3 bg-gray-200 rounded w-1/3" />
              </div>
              <div className="h-5 bg-gray-200 rounded w-16" />
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
