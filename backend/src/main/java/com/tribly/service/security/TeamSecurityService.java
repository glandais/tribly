package com.tribly.service.security;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.repository.UserTeamRepository;
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
   * @param userId the user ID
   * @param team   the team
   * @return true if the user is a member
   */
  public boolean isMember(@Nullable Long userId, Team team) {
    if (userId == null) {
      return false;
    }
    return userTeamRepository.findByUserAndTeam(userId, team.getSlug()).isPresent();
  }

  public boolean canSeeDrafts(@Nullable Long userId, Team team) {
    if (userId == null) {
      return false;
    }
    return userTeamRepository
        .findByUserAndTeam(userId, team.getSlug())
        .map(UserTeam::isOrganizer)
        .orElse(false);
  }

  /**
   * Requires the user to be a member of the team.
   *
   * @param userId   the user ID
   * @param teamSlug the team slug
   * @return the membership
   * @throws BusinessException with FORBIDDEN if not a member
   */
  public UserTeam requireMembership(@Nullable Long userId, String teamSlug) {
    if (userId == null) {
      throw BusinessException.forbidden("You are not a member of this team");
    }
    return userTeamRepository
        .findByUserAndTeam(userId, teamSlug)
        .orElseThrow(() -> BusinessException.forbidden("You are not a member of this team"));
  }

  // ==================== Role-Based Checks ====================

  /**
   * Requires the user to be an admin of the team.
   *
   * @param userId the user ID
   * @param slug   the team slug
   * @throws BusinessException with FORBIDDEN if not an admin
   */
  public void requireAdmin(Long userId, String slug) {
    UserTeam membership = requireMembership(userId, slug);
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
   * @param userId   the user ID
   * @param teamSlug the team slug
   * @throws BusinessException with FORBIDDEN
   */
  public void requireOrganizer(@Nullable Long userId, String teamSlug) {
    UserTeam membership = requireMembership(userId, teamSlug);
    if (!membership.isOrganizer()) {
      throw BusinessException.forbidden("Not organizer");
    }
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
   * @param teamSlug         the team slug
   * @param targetMembership the membership being removed or demoted
   * @throws BusinessException with BUSINESS_RULE if this would remove the last admin
   */
  public void requireNotLastAdmin(String teamSlug, UserTeam targetMembership) {
    if (targetMembership.getRole() == TeamRole.ADMIN) {
      long adminCount = userTeamRepository.countAdminsByTeam(teamSlug);
      if (adminCount <= 1) {
        throw BusinessException.businessRule("Cannot remove the last admin", "LAST_ADMIN");
      }
    }
  }

  /**
   * Checks that demoting a user won't leave the team without an admin.
   *
   * @param teamSlug         the team slug
   * @param targetMembership the membership being demoted
   * @param newRole          the new role
   * @throws BusinessException with BUSINESS_RULE if this would remove the last admin
   */
  public void requireNotLastAdminDemotion(
      String teamSlug, UserTeam targetMembership, TeamRole newRole) {
    if (targetMembership.getRole() == TeamRole.ADMIN && newRole != TeamRole.ADMIN) {
      requireNotLastAdmin(teamSlug, targetMembership);
    }
  }

  // ==================== Self-Action Checks ====================

  /**
   * Checks if a user can remove a member (self or with admin rights).
   *
   * @param actorId  the user performing the action
   * @param targetId the user being removed
   * @param teamSlug the team slug
   * @throws BusinessException with FORBIDDEN if not allowed
   */
  public void requireCanRemoveMember(Long actorId, Long targetId, String teamSlug) {
    boolean isSelfRemoval = targetId.equals(actorId);
    if (isSelfRemoval) {
      // Users can always remove themselves (leave the team)
      requireMembership(actorId, teamSlug);
      return;
    }

    // Non-self removal requires admin rights
    requireAdmin(actorId, teamSlug);
  }
}
