package fr.pedalons.service.team;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.domain.team.*;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.teams.response.MemberDto;
import fr.pedalons.dto.teams.response.MemberListResponse;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.infrastructure.exception.*;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.Context;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TeamMembershipService {

  @Inject UserTeamRepository userTeamRepository;

  @Inject UserRepository userRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  @Inject TeamService teamService;

  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.LIST)
  public MemberListResponse getTeamMembers(String teamSlug, int page, int size) {
    Team team = teamService.getTeam(teamSlug);
    PedalonsPage<UserTeam> members = userTeamRepository.findByTeam(team.getId(), page, size);
    List<MemberDto> dtos = members.items().stream().map(MemberDto::from).toList();
    return new MemberListResponse(dtos, members.total(), page, size);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.JOIN)
  public MemberDto joinTeam(String teamSlug) {
    Team team = teamService.getTeam(teamSlug);
    return doAddMember(team, TeamRole.MEMBER, pedalonsContext.getUser());
  }

  @Transactional
  @CheckAccess(entityType = EntityType.USER_TEAM, action = ActionType.CREATE)
  public MemberDto addMember(String teamSlug, Long targetUserId, TeamRole role) {
    Team team = teamService.getTeam(teamSlug);
    User actingUser = pedalonsContext.getUser();
    boolean isPlatformAdmin = actingUser.isPlatformAdmin();
    if (!isPlatformAdmin && !team.isAddMemberAllowed()) {
      throw new BusinessException(ErrorCode.TEAM_ADD_MEMBER_NOT_ALLOWED);
    }
    User targetUser =
        userRepository
            .findActiveById(targetUserId)
            .orElseThrow(() -> new NotFoundException(EntityType.USER, targetUserId));

    return doAddMember(team, role, targetUser);
  }

  private MemberDto doAddMember(Team team, TeamRole role, User user) {
    User creator = pedalonsContext.getUser();
    // Check for existing membership
    Optional<UserTeam> existingMembership =
        userTeamRepository.findByUserAndTeam(user.getId(), team.getId());

    if (existingMembership.isPresent()) {
      throw new ConflictException(ErrorCode.ALREADY_REGISTERED);
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
    Context context = pedalonsContext.getContext(team);
    User actingUser = context.user();
    TeamRole teamRole = context.teamRole();
    User targetUser =
        userRepository
            .findActiveById(targetUserId)
            .orElseThrow(() -> new NotFoundException(EntityType.USER, targetUserId));
    requireCanRemoveMember(actingUser, teamRole, targetUser);

    UserTeam targetMembership =
        userTeamRepository
            .findByUserAndTeam(targetUser.getId(), team.getId())
            .orElseThrow(() -> new NotFoundException(EntityType.USER_TEAM, targetUserId));

    requireNotLastAdmin(team, targetMembership);

    userTeamRepository.delete(targetMembership);
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
    doRemoveMember(team, pedalonsContext.getUserId());
  }
}
