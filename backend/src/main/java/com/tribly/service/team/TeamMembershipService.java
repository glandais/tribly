package com.tribly.service.team;

import com.tribly.common.exception.BusinessException;
import com.tribly.common.exception.ConflictException;
import com.tribly.common.exception.ForbiddenException;
import com.tribly.domain.team.*;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.teams.response.MemberDto;
import com.tribly.dto.teams.response.MemberListResponse;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.*;
import com.tribly.repository.common.TriblyPage;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.security.Context;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.CheckAccess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TeamMembershipService {

  @Inject UserTeamRepository userTeamRepository;

  @Inject UserRepository userRepository;

  @Inject TriblyQueryContext triblyContext;

  @Inject TeamService teamService;

  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.LIST)
  public MemberListResponse getTeamMembers(String teamSlug, int page, int size) {
    Team team = teamService.getTeam(teamSlug);
    TriblyPage<UserTeam> members = userTeamRepository.findByTeam(team.getId(), page, size);
    List<MemberDto> dtos = members.items().stream().map(MemberDto::from).toList();
    return new MemberListResponse(dtos, members.total(), page, size);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.JOIN)
  public MemberDto joinTeam(String teamSlug) {
    Team team = teamService.getTeam(teamSlug);
    // Security checks
    if (team.getVisibility() != Visibility.PUBLIC) {
      throw new ForbiddenException();
    }

    return doAddMember(team, TeamRole.MEMBER, triblyContext.getUser());
  }

  @Transactional
  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.CREATE)
  public MemberDto addMember(String teamSlug, Long targetUserId, TeamRole role) {
    Team team = teamService.getTeam(teamSlug);
    User targetUser =
        userRepository
            .findActiveById(targetUserId)
            .orElseThrow(() -> new NotFoundException(EntityType.USER, targetUserId));

    return doAddMember(team, role, targetUser);
  }

  private MemberDto doAddMember(Team team, TeamRole role, User user) {
    User creator = triblyContext.getUser();
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
      return MemberDto.from(membership);
    }

    // Create new membership
    UserTeam membership = new UserTeam(creator, user, team, role);
    userTeamRepository.persist(membership);

    return MemberDto.from(membership);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.UPDATE)
  public MemberDto updateMemberRole(String teamSlug, Long targetUserId, TeamRole newRole) {
    Team team = teamService.getTeam(teamSlug);
    UserTeam targetMembership =
        userTeamRepository
            .findByUserAndTeam(targetUserId, team.getId())
            .orElseThrow(() -> new NotFoundException(EntityType.USER_TEAM, targetUserId));

    requireNotLastAdminDemotion(team, targetMembership, newRole);

    targetMembership.setRole(newRole);
    userTeamRepository.persist(targetMembership);

    return MemberDto.from(targetMembership);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.DELETE)
  public void removeMember(String teamSlug, Long targetUserId) {
    Team team = teamService.getTeam(teamSlug);
    doRemoveMember(team, targetUserId);
  }

  private void doRemoveMember(Team team, Long targetUserId) {
    Context context = triblyContext.getContext(team);
    User actingUser = context.user();
    TeamRole teamRole = context.teamRole();
    User targetUser =
        userRepository
            .findActiveById(targetUserId)
            .orElseThrow(() -> new NotFoundException(EntityType.USER, targetUserId));
    // Security checks
    requireCanRemoveMember(actingUser, teamRole, targetUser);

    UserTeam targetMembership =
        userTeamRepository
            .findByUserAndTeam(targetUser.getId(), team.getId())
            .orElseThrow(() -> new NotFoundException(EntityType.USER_TEAM, targetUserId));

    requireNotLastAdmin(team, targetMembership);

    targetMembership.setDeleted(true);
    userTeamRepository.persist(targetMembership);
  }

  void requireCanRemoveMember(User actor, TeamRole teamRole, User target) {
    boolean isSelfRemoval = target.getId().equals(actor.getId());
    if (isSelfRemoval) {
      // Users can always remove themselves (leave the team)
      return;
    }
    // Non-self removal requires admin rights
    if (teamRole == null || !teamRole.isAdmin()) {
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
  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.LEAVE)
  public void leaveTeam(String teamSlug) {
    Team team = teamService.getTeam(teamSlug);
    doRemoveMember(team, triblyContext.getUserId());
  }
}
