package com.tribly.service.team;

import com.tribly.domain.team.*;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.infrastructure.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TeamMembershipService {

    private static final Logger LOG = Logger.getLogger(TeamMembershipService.class);

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserTeamRepository userTeamRepository;

    @Inject
    UserRepository userRepository;

    public List<UserTeam> getTeamMembers(Long teamId, int page, int size) {
        return userTeamRepository.findByTeam(teamId, page, size);
    }

    public List<UserTeam> getTeamMembers(Long teamId) {
        return userTeamRepository.findByTeam(teamId);
    }

    public long countTeamMembers(Long teamId) {
        return userTeamRepository.countByTeam(teamId);
    }

    public Optional<UserTeam> getMembership(Long userId, Long teamId) {
        return userTeamRepository.findByUserAndTeam(userId, teamId);
    }

    @Transactional
    public UserTeam joinTeam(Long teamId, Long userId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> BusinessException.notFound("User", userId));

        if (!team.isPublic()) {
            throw BusinessException.forbidden("This team is private. You need an invitation to join.");
        }

        if (userTeamRepository.existsByUserAndTeam(userId, teamId)) {
            throw BusinessException.conflict("You are already a member of this team", "ALREADY_MEMBER");
        }

        if (!team.hasCapacity()) {
            throw BusinessException.businessRule("This team has reached its maximum member capacity", "TEAM_FULL");
        }

        UserTeam membership = new UserTeam(user, team, TeamRole.MEMBER);
        userTeamRepository.persist(membership);

        LOG.infov("User {0} joined team {1}", userId, team.getSlug());
        return membership;
    }

    @Transactional
    public UserTeam addMember(Long teamId, Long targetUserId, TeamRole role, Long actingUserId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        UserTeam actorMembership = userTeamRepository.findByUserAndTeam(actingUserId, teamId)
                .orElseThrow(() -> BusinessException.forbidden("You are not a member of this team"));

        if (!actorMembership.canManageMembers()) {
            throw BusinessException.forbidden("Only admins can add members");
        }

        User targetUser = userRepository.findActiveById(targetUserId)
                .orElseThrow(() -> BusinessException.notFound("User", targetUserId));

        if (userTeamRepository.existsByUserAndTeam(targetUserId, teamId)) {
            throw BusinessException.conflict("User is already a member of this team", "ALREADY_MEMBER");
        }

        if (!team.hasCapacity()) {
            throw BusinessException.businessRule("This team has reached its maximum member capacity", "TEAM_FULL");
        }

        User actingUser = userRepository.findActiveById(actingUserId).orElse(null);
        UserTeam membership = new UserTeam(targetUser, team, role, actingUser);
        userTeamRepository.persist(membership);

        LOG.infov("User {0} added to team {1} with role {2} by user {3}",
                targetUserId, team.getSlug(), role, actingUserId);
        return membership;
    }

    @Transactional
    public UserTeam updateMemberRole(Long teamId, Long targetUserId, TeamRole newRole, Long actingUserId) {
        UserTeam actorMembership = userTeamRepository.findByUserAndTeam(actingUserId, teamId)
                .orElseThrow(() -> BusinessException.forbidden("You are not a member of this team"));

        if (!actorMembership.canManageMembers()) {
            throw BusinessException.forbidden("Only admins can update member roles");
        }

        UserTeam targetMembership = userTeamRepository.findByUserAndTeam(targetUserId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Membership not found"));

        if (targetMembership.getRole() == TeamRole.ADMIN && newRole != TeamRole.ADMIN) {
            long adminCount = userTeamRepository.countAdminsByTeam(teamId);
            if (adminCount <= 1) {
                throw BusinessException.businessRule("Cannot remove the last admin", "LAST_ADMIN");
            }
        }

        targetMembership.setRole(newRole);
        userTeamRepository.persist(targetMembership);

        LOG.infov("User {0} role updated to {1} in team {2} by user {3}",
                targetUserId, newRole, teamId, actingUserId);
        return targetMembership;
    }

    @Transactional
    public void removeMember(Long teamId, Long targetUserId, Long actingUserId) {
        UserTeam actorMembership = userTeamRepository.findByUserAndTeam(actingUserId, teamId)
                .orElseThrow(() -> BusinessException.forbidden("You are not a member of this team"));

        UserTeam targetMembership = userTeamRepository.findByUserAndTeam(targetUserId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Membership not found"));

        boolean isSelfRemoval = targetUserId.equals(actingUserId);

        if (!isSelfRemoval && !actorMembership.canManageMembers()) {
            throw BusinessException.forbidden("Only admins can remove members");
        }

        if (targetMembership.getRole() == TeamRole.ADMIN) {
            long adminCount = userTeamRepository.countAdminsByTeam(teamId);
            if (adminCount <= 1) {
                throw BusinessException.businessRule("Cannot remove the last admin", "LAST_ADMIN");
            }
        }

        targetMembership.softDelete();
        userTeamRepository.persist(targetMembership);

        LOG.infov("User {0} removed from team {1} by user {2}",
                targetUserId, teamId, actingUserId);
    }

    @Transactional
    public void leaveTeam(Long teamId, Long userId) {
        removeMember(teamId, userId, userId);
    }
}
