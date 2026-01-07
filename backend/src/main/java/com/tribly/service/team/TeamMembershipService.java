package com.tribly.service.team;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.*;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.teams.response.MemberDto;
import com.tribly.dto.teams.response.MemberListResponse;
import com.tribly.enums.AllEntityType;
import com.tribly.enums.TeamRole;
import com.tribly.infrastructure.exception.*;
import com.tribly.service.security.TeamSecurityService;
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

  public MemberListResponse getTeamMembers(Team team, User user, int page, int size) {
    // Security check: only team admins can view member list
    securityService.requireAdmin(user, team);
    TriblyPage<UserTeam> members = userTeamRepository.findByTeam(team.getId(), page, size);
    List<MemberDto> dtos = members.items().stream().map(MemberDto::from).toList();
    return new MemberListResponse(dtos, members.total(), page, size);
  }

  @Transactional
  public MemberDto joinTeam(Team team, User user) {
    // Security checks
    securityService.requirePublicTeamForJoin(team);

    return doAddMember(user, team, TeamRole.MEMBER, user);
  }

  @Transactional
  public MemberDto addMember(Team team, Long targetUserId, TeamRole role, User actingUser) {
    // Security checks
    securityService.requireAdmin(actingUser, team);

    User targetUser =
        userRepository
            .findActiveById(targetUserId)
            .orElseThrow(() -> new NotFoundException(AllEntityType.USER, targetUserId));

    return doAddMember(actingUser, team, role, targetUser);
  }

  private MemberDto doAddMember(User creator, Team team, TeamRole role, User user) {
    // Check for existing membership (including soft-deleted)
    Optional<UserTeam> existingMembership =
        userTeamRepository.findByUserAndTeamIncludingDeleted(user.getId(), team.getId());

    if (existingMembership.isPresent()) {
      UserTeam membership = existingMembership.get();
      if (!membership.isDeleted()) {
        throw new ConflictException(ErrorCode.ALREADY_REGISTERED);
      }
      // Restore soft-deleted membership
      membership.setDeleted(false);
      membership.setRole(role);
      membership.setJoinedAt(Instant.now());
      userTeamRepository.persist(membership);
      LOG.infov("User {0} rejoined team {1}", user.getId(), team.getId());
      return MemberDto.from(membership);
    }

    // Create new membership
    UserTeam membership = new UserTeam(creator, user, team, role);
    userTeamRepository.persist(membership);

    LOG.infov("User {0} joined team {1}", user.getId(), team.getId());
    return MemberDto.from(membership);
  }

  @Transactional
  public MemberDto updateMemberRole(
      Team team, Long targetUserId, TeamRole newRole, User actingUser) {
    // Security checks
    securityService.requireAdmin(actingUser, team);

    UserTeam targetMembership =
        userTeamRepository
            .findByUserAndTeam(targetUserId, team.getId())
            .orElseThrow(() -> new NotFoundException(AllEntityType.USER_TEAM, targetUserId));

    requireNotLastAdminDemotion(team, targetMembership, newRole);

    targetMembership.setRole(newRole);
    userTeamRepository.persist(targetMembership);

    LOG.infov(
        "User {0} role updated to {1} in team {2} by user {3}",
        targetUserId, newRole, team.getId(), actingUser);
    return MemberDto.from(targetMembership);
  }

  @Transactional
  public void removeMember(Team team, Long targetUserId, User actingUser) {
    User targetUser =
        userRepository
            .findActiveById(targetUserId)
            .orElseThrow(() -> new NotFoundException(AllEntityType.USER, targetUserId));
    // Security checks
    requireCanRemoveMember(actingUser, targetUser, team);

    UserTeam targetMembership =
        userTeamRepository
            .findByUserAndTeam(targetUser.getId(), team.getId())
            .orElseThrow(() -> new NotFoundException(AllEntityType.USER_TEAM, targetUserId));

    requireNotLastAdmin(team, targetMembership);

    targetMembership.setDeleted(true);
    userTeamRepository.persist(targetMembership);

    LOG.infov(
        "User {0} removed from team {1} by user {2}", targetUser.getId(), team.getId(), actingUser);
  }

  void requireCanRemoveMember(User actor, User target, Team team) {
    boolean isSelfRemoval = target.getId().equals(actor.getId());
    if (isSelfRemoval) {
      // Users can always remove themselves (leave the team)
      if (securityService.getMembership(actor, team) == null) {
        throw new ForbiddenException();
      }
    }
    // Non-self removal requires admin rights
    if (securityService.getAdmin(target, team) == null) {
      throw new ForbiddenException();
    }
  }

  void requireNotLastAdmin(Team team, UserTeam targetMembership) {
    if (targetMembership.getRole() == TeamRole.ADMIN) {
      long adminCount = userTeamRepository.countAdminsByTeam(team.getId());
      if (adminCount <= 1) {
        throw new BusinessException(ErrorCode.LAST_ADMIN);
      }
    }
  }

  void requireNotLastAdminDemotion(Team team, UserTeam targetMembership, TeamRole newRole) {
    if (targetMembership.getRole() == TeamRole.ADMIN && newRole != TeamRole.ADMIN) {
      requireNotLastAdmin(team, targetMembership);
    }
  }

  @Transactional
  public void leaveTeam(Team team, User user) {
    removeMember(team, user.getId(), user);
  }
}
