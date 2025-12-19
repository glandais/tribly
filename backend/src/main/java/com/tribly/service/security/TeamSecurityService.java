package com.tribly.service.security;

import com.tribly.domain.common.Visibility;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.team.*;
import com.tribly.infrastructure.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Centralized security service for team-related authorization checks.
 *
 * <p>This service provides a single source of truth for all team security decisions,
 * ensuring consistent authorization logic across the application.
 *
 * <h2>Role Hierarchy</h2>
 * <ul>
 *   <li><b>ADMIN</b> - Full permissions: manage members, edit team, create/edit all rides, delete team</li>
 *   <li><b>ORGANIZER</b> - Can create rides and edit own rides, no member management</li>
 *   <li><b>MEMBER</b> - Can view team, participate in rides</li>
 * </ul>
 *
 * <h2>Visibility Rules</h2>
 * <ul>
 *   <li><b>Public teams</b> - Anyone can view, authenticated users can join</li>
 *   <li><b>Private teams</b> - Only members can view, join requires admin invitation</li>
 * </ul>
 */
@ApplicationScoped
public class TeamSecurityService {

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserTeamRepository userTeamRepository;

    // ==================== Membership Checks ====================

    /**
     * Checks if a user is a member of a team.
     *
     * @param userId the user ID
     * @param teamId the team ID
     * @return true if the user is a member
     */
    public boolean isMember(@Nullable Long userId, Long teamId) {
        if (userId == null) {
            return false;
        }
        return userTeamRepository.findByUserAndTeam(userId, teamId).isPresent();
    }

    public boolean canSeeDrafts(@Nullable Long userId, Long teamId) {
        if (userId == null) {
            return false;
        }
        return userTeamRepository.findByUserAndTeam(userId, teamId)
                .map(UserTeam::isOrganizer)
                .orElse(false);
    }

    /**
     * Requires the user to be a member of the team.
     *
     * @param userId the user ID
     * @param teamId the team ID
     * @return the membership
     * @throws BusinessException with FORBIDDEN if not a member
     */
    public UserTeam requireMembership(Long userId, Long teamId) {
        return userTeamRepository.findByUserAndTeam(userId, teamId)
                .orElseThrow(() -> BusinessException.forbidden("You are not a member of this team"));
    }

    // ==================== Role-Based Checks ====================

    /**
     * Requires the user to be an admin of the team.
     *
     * @param userId the user ID
     * @param teamId the team ID
     * @throws BusinessException with FORBIDDEN if not an admin
     */
    public void requireAdmin(Long userId, Long teamId) {
        UserTeam membership = requireMembership(userId, teamId);
        if (!membership.isAdmin()) {
            throw BusinessException.forbidden("Only admins can perform this action");
        }
    }

    /**
     * Requires the user to have member management permissions (admin only).
     *
     * @param userId the user ID
     * @param teamId the team ID
     * @throws BusinessException with FORBIDDEN if cannot manage members
     */
    public void requireCanManageMembers(Long userId, Long teamId) {
        UserTeam membership = requireMembership(userId, teamId);
        if (!membership.canManageMembers()) {
            throw BusinessException.forbidden("Only admins can manage team members");
        }
    }

    // ==================== Ride Permission Checks ====================

    /**
     * Requires the user to have permission to create rides in the team.
     *
     * @param userId the user ID
     * @param teamId the team ID
     * @throws BusinessException with FORBIDDEN if cannot create rides
     */
    public void requireCanCreateRide(Long userId, Long teamId) {
        UserTeam membership = requireMembership(userId, teamId);
        if (!membership.canCreateRides()) {
            throw BusinessException.forbidden("Only admins and organizers can create rides");
        }
    }

    /**
     * Requires the user to have permission to edit a specific ride.
     *
     * <p>Edit permissions:
     * <ul>
     *   <li>Admins can edit any ride</li>
     *   <li>Organizers can only edit rides they created</li>
     *   <li>Members cannot edit rides</li>
     * </ul>
     *
     * @param userId the user ID
     * @param teamId the team ID
     * @param ride   the ride to edit
     * @throws BusinessException with FORBIDDEN if cannot edit the ride
     */
    public void requireCanEditRide(Long userId, Long teamId, Ride ride) {
        UserTeam membership = requireMembership(userId, teamId);
        if (!canEditRide(membership, ride, userId)) {
            throw BusinessException.forbidden("You don't have permission to edit this ride");
        }
    }

    /**
     * Checks if a user can edit a specific ride.
     *
     * @param membership the user's team membership
     * @param ride       the ride to check
     * @param userId     the user ID
     * @return true if the user can edit the ride
     */
    public boolean canEditRide(UserTeam membership, Ride ride, Long userId) {
        if (membership.isAdmin()) {
            return true;
        }
        return membership.getRole() == TeamRole.ORGANIZER && ride.getCreatedBy().getId().equals(userId);
    }

    // ==================== Team Visibility Checks ====================

    /**
     * Requires the team to be public for self-join operations.
     *
     * @param team the team to join
     * @throws BusinessException with FORBIDDEN if the team is private
     */
    public void requirePublicTeamForJoin(Team team) {
        if (team.getVisibility() != Visibility.PUBLIC) {
            throw BusinessException.forbidden("This team is private. You need an invitation to join.");
        }
    }

    // ==================== Business Rule Checks ====================

    /**
     * Checks that a team has capacity for more members.
     *
     * @param team the team to check
     * @throws BusinessException with BUSINESS_RULE if the team is full
     */
    public void requireTeamCapacity(Team team) {
        if (!team.hasCapacity()) {
            throw BusinessException.businessRule("This team has reached its maximum member capacity", "TEAM_FULL");
        }
    }

    /**
     * Checks that removing a user won't leave the team without an admin.
     *
     * @param teamId           the team ID
     * @param targetMembership the membership being removed or demoted
     * @throws BusinessException with BUSINESS_RULE if this would remove the last admin
     */
    public void requireNotLastAdmin(Long teamId, UserTeam targetMembership) {
        if (targetMembership.getRole() == TeamRole.ADMIN) {
            long adminCount = userTeamRepository.countAdminsByTeam(teamId);
            if (adminCount <= 1) {
                throw BusinessException.businessRule("Cannot remove the last admin", "LAST_ADMIN");
            }
        }
    }

    /**
     * Checks that demoting a user won't leave the team without an admin.
     *
     * @param teamId           the team ID
     * @param targetMembership the membership being demoted
     * @param newRole          the new role
     * @throws BusinessException with BUSINESS_RULE if this would remove the last admin
     */
    public void requireNotLastAdminDemotion(Long teamId, UserTeam targetMembership, TeamRole newRole) {
        if (targetMembership.getRole() == TeamRole.ADMIN && newRole != TeamRole.ADMIN) {
            requireNotLastAdmin(teamId, targetMembership);
        }
    }

    // ==================== Self-Action Checks ====================

    /**
     * Checks if a user can remove a member (self or with admin rights).
     *
     * @param actorId  the user performing the action
     * @param targetId the user being removed
     * @param teamId   the team ID
     * @throws BusinessException with FORBIDDEN if not allowed
     */
    public void requireCanRemoveMember(Long actorId, Long targetId, Long teamId) {
        boolean isSelfRemoval = targetId.equals(actorId);
        if (isSelfRemoval) {
            // Users can always remove themselves (leave the team)
            requireMembership(actorId, teamId);
            return;
        }

        // Non-self removal requires admin rights
        requireCanManageMembers(actorId, teamId);
    }

    // ==================== Duplicate Membership Checks ====================

    /**
     * Checks that a user is not already a member of the team.
     *
     * @param userId the user ID
     * @param teamId the team ID
     * @throws BusinessException with CONFLICT if already a member
     */
    public void requireNotAlreadyMember(Long userId, Long teamId) {
        if (userTeamRepository.existsByUserAndTeam(userId, teamId)) {
            throw BusinessException.conflict("User is already a member of this team", "ALREADY_MEMBER");
        }
    }
}
