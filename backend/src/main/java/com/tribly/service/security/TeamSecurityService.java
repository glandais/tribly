package com.tribly.service.security;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.ForbiddenException;
import com.tribly.infrastructure.exception.TriblyException;
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
   * @throws TriblyException with FORBIDDEN if not a member
   */
  @Nullable
  public UserTeam getMembership(@Nullable User user, Team team) {
    if (user == null) {
      return null;
    }
    return userTeamRepository.findByUserAndTeam(user.getId(), team.getId()).orElse(null);
  }

  // ==================== Role-Based Checks ====================

  /**
   * Requires the user to be an admin of the team.
   *
   * @param user the user
   * @param team   the team
   * @throws TriblyException with FORBIDDEN if not an admin
   */
  @Nullable
  public UserTeam getAdmin(@Nullable User user, Team team) {
    UserTeam membership = getMembership(user, team);
    if (membership == null || !membership.isAdmin()) {
      return null;
    }
    return membership;
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
   * @throws TriblyException with FORBIDDEN
   */
  @Nullable
  public UserTeam getOrganizer(@Nullable User user, Team team) {
    UserTeam membership = getMembership(user, team);
    if (membership == null || !membership.isOrganizer()) {
      return null;
    }
    return membership;
  }

  // ==================== Team Visibility Checks ====================

  /**
   * Requires the team to be public for self-join operations.
   *
   * @param team the team to join
   * @throws TriblyException with FORBIDDEN if the team is private
   */
  public void requirePublicTeamForJoin(Team team) {
    if (team.getVisibility() != Visibility.PUBLIC) {
      throw new ForbiddenException();
    }
  }

  @Deprecated
  public UserTeam requireMembership(@Nullable User user, Team team) {
    UserTeam membership = getMembership(user, team);
    if (membership == null) {
      throw new ForbiddenException();
    }
    return membership;
  }

  @Deprecated
  public UserTeam requireOrganizer(@Nullable User user, Team team) {
    UserTeam membership = getOrganizer(user, team);
    if (membership == null) {
      throw new ForbiddenException();
    }
    return membership;
  }

  @Deprecated
  public UserTeam requireAdmin(@Nullable User user, Team team) {
    UserTeam membership = getAdmin(user, team);
    if (membership == null) {
      throw new ForbiddenException();
    }
    return membership;
  }
}
