package com.tribly.service.team;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.*;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.teams.response.MemberDto;
import com.tribly.dto.teams.response.MemberListResponse;
import com.tribly.enums.TeamRole;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.security.TeamSecurityService;
import com.tribly.service.team.response.TeamAndRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TeamMembershipService {

  private static final Logger LOG = Logger.getLogger(TeamMembershipService.class);

  @Inject TeamRepository teamRepository;

  @Inject UserTeamRepository userTeamRepository;

  @Inject UserRepository userRepository;

  @Inject TeamSecurityService securityService;

  public MemberListResponse getTeamMembers(String slug, Long userId, int page, int size) {
    // Security check: only team admins can view member list
    securityService.requireAdmin(userId, slug);
    TriblyPage<UserTeam> members = userTeamRepository.findByTeam(slug, page, size);
    List<MemberDto> dtos = members.items().stream().map(MemberDto::from).toList();
    return new MemberListResponse(dtos, members.total(), page, size);
  }

  @Transactional
  public MemberDto joinTeam(String teamSlug, Long userId) {
    TeamAndRole teamAndRole =
        teamRepository
            .findOne(teamSlug, userId)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Security checks
    securityService.requirePublicTeamForJoin(teamAndRole.team());

    return doAddMember(teamAndRole, TeamRole.MEMBER, user);
  }

  @Transactional
  public MemberDto addMember(String teamSlug, Long targetUserId, TeamRole role, Long actingUserId) {
    TeamAndRole teamAndRole =
        teamRepository
            .findOne(teamSlug, actingUserId)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    // Security checks
    securityService.requireAdmin(actingUserId, teamAndRole.team().getSlug());

    User targetUser =
        userRepository
            .findActiveById(targetUserId)
            .orElseThrow(() -> BusinessException.notFound("User", targetUserId));

    return doAddMember(teamAndRole, role, targetUser);
  }

  private MemberDto doAddMember(TeamAndRole teamAndRole, TeamRole role, User user) {
    securityService.requireTeamCapacity(teamAndRole);

    Long userId = user.getId();
    String teamSlug = teamAndRole.team().getSlug();

    // Check for existing membership (including soft-deleted)
    Optional<UserTeam> existingMembership =
        userTeamRepository.findByUserAndTeamIncludingDeleted(userId, teamSlug);

    if (existingMembership.isPresent()) {
      UserTeam membership = existingMembership.get();
      if (!membership.isDeleted()) {
        throw BusinessException.conflict("User is already a member of this team");
      }
      // Restore soft-deleted membership
      membership.setDeleted(false);
      membership.setRole(role);
      membership.setJoinedAt(Instant.now());
      userTeamRepository.persist(membership);
      LOG.infov("User {0} rejoined team {1}", userId, teamSlug);
      return MemberDto.from(membership);
    }

    // Create new membership
    UserTeam membership = new UserTeam(user, teamAndRole.team(), role);
    userTeamRepository.persist(membership);

    LOG.infov("User {0} joined team {1}", userId, teamSlug);
    return MemberDto.from(membership);
  }

  @Transactional
  public MemberDto updateMemberRole(
      String teamSlug, Long targetUserId, TeamRole newRole, Long actingUserId) {
    // Security checks
    securityService.requireAdmin(actingUserId, teamSlug);

    UserTeam targetMembership =
        userTeamRepository
            .findByUserAndTeam(targetUserId, teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Membership not found"));

    securityService.requireNotLastAdminDemotion(teamSlug, targetMembership, newRole);

    targetMembership.setRole(newRole);
    userTeamRepository.persist(targetMembership);

    LOG.infov(
        "User {0} role updated to {1} in team {2} by user {3}",
        targetUserId, newRole, teamSlug, actingUserId);
    return MemberDto.from(targetMembership);
  }

  @Transactional
  public void removeMember(String teamSlug, Long targetUserId, Long actingUserId) {
    // Security checks
    securityService.requireCanRemoveMember(actingUserId, targetUserId, teamSlug);

    UserTeam targetMembership =
        userTeamRepository
            .findByUserAndTeam(targetUserId, teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Membership not found"));

    securityService.requireNotLastAdmin(teamSlug, targetMembership);

    targetMembership.softDelete();
    userTeamRepository.persist(targetMembership);

    LOG.infov("User {0} removed from team {1} by user {2}", targetUserId, teamSlug, actingUserId);
  }

  @Transactional
  public void leaveTeam(String teamSlug, Long userId) {
    removeMember(teamSlug, userId, userId);
  }
}
