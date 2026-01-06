package com.tribly.service.security;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

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

  @Inject UserTeamRepository userTeamRepository;

  // ==================== Membership Checks ====================

  /**
   * Checks if a user is a member of a team.
   *
   * @param user the user
   * @param team   the team
   * @return true if the user is a member
   */
  public boolean isMember(@Nullable User user, Team team) {
    if (user == null) {
      return false;
    }
    return userTeamRepository.findByUserAndTeam(user.getId(), team.getId()).isPresent();
  }

  public boolean canSeeDrafts(@Nullable User user, Team team) {
    if (user == null) {
      return false;
    }
    return userTeamRepository
        .findByUserAndTeam(user.getId(), team.getId())
        .map(UserTeam::isOrganizer)
        .orElse(false);
  }

  /**
   * Requires the user to be a member of the team.
   *
   * @param user   the user
   * @param team the team
   * @return the membership
   * @throws BusinessException with FORBIDDEN if not a member
   */
  public UserTeam requireMembership(@Nullable User user, Team team) {
    if (user == null) {
      throw BusinessException.forbidden("You are not a member of this team");
    }
    return userTeamRepository
        .findByUserAndTeam(user.getId(), team.getId())
        .orElseThrow(() -> BusinessException.forbidden("You are not a member of this team"));
  }

  // ==================== Role-Based Checks ====================

  /**
   * Requires the user to be an admin of the team.
   *
   * @param user the user
   * @param team   the team
   * @throws BusinessException with FORBIDDEN if not an admin
   */
  public void requireAdmin(User user, Team team) {
    UserTeam membership = requireMembership(user, team);
    if (!membership.isAdmin()) {
      throw BusinessException.forbidden("Only admins can perform this action");
    }
  }

  // ==================== Ride Permission Checks ====================

  /**
   * Requires the user to have permission to edit an item.
   *
   * <p>Edit permissions:
   * <ul>
   *   <li>Admins and organizers can edit any item</li>
   *   <li>Members cannot edit rides</li>
   * </ul>
   *
   * @param user   the user
   * @param team the team
   * @return
   * @throws BusinessException with FORBIDDEN
   */
  public UserTeam requireOrganizer(@Nullable User user, Team team) {
    UserTeam membership = requireMembership(user, team);
    if (!membership.isOrganizer()) {
      throw BusinessException.forbidden("Not organizer");
    }
    return membership;
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
   * Checks that removing a user won't leave the team without an admin.
   *
   * @param team         the team
   * @param targetMembership the membership being removed or demoted
   * @throws BusinessException with BUSINESS_RULE if this would remove the last admin
   */
  public void requireNotLastAdmin(Team team, UserTeam targetMembership) {
    if (targetMembership.getRole() == TeamRole.ADMIN) {
      long adminCount = userTeamRepository.countAdminsByTeam(team.getId());
      if (adminCount <= 1) {
        throw BusinessException.businessRule("Cannot remove the last admin", "LAST_ADMIN");
      }
    }
  }

  /**
   * Checks that demoting a user won't leave the team without an admin.
   *
   * @param team         the team
   * @param targetMembership the membership being demoted
   * @param newRole          the new role
   * @throws BusinessException with BUSINESS_RULE if this would remove the last admin
   */
  public void requireNotLastAdminDemotion(Team team, UserTeam targetMembership, TeamRole newRole) {
    if (targetMembership.getRole() == TeamRole.ADMIN && newRole != TeamRole.ADMIN) {
      requireNotLastAdmin(team, targetMembership);
    }
  }

  // ==================== Self-Action Checks ====================

  /**
   * Checks if a user can remove a member (self or with admin rights).
   *
   * @param actor  the user performing the action
   * @param target the user being removed
   * @param team the team
   * @throws BusinessException with FORBIDDEN if not allowed
   */
  public void requireCanRemoveMember(User actor, User target, Team team) {
    boolean isSelfRemoval = target.getId().equals(actor.getId());
    if (isSelfRemoval) {
      // Users can always remove themselves (leave the team)
      requireMembership(actor, team);
      return;
    }

    // Non-self removal requires admin rights
    requireAdmin(actor, team);
  }
}
