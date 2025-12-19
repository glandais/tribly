package com.tribly.service.team;

import com.tribly.domain.team.*;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.security.TeamSecurityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
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

    @Inject
    TeamSecurityService securityService;

    public List<UserTeam> getTeamMembers(Team team, Long userId, int page, int size) {
        // Security check: only team admins can view member list
        securityService.requireAdmin(userId, team.getId());
        return userTeamRepository.findByTeam(team.getId(), page, size);
    }

    public long countTeamMembers(Team team, Long userId) {
        // Security check: only team admins can view member list
        securityService.requireAdmin(userId, team.getId());
        return userTeamRepository.countByTeam(team.getId());
    }

    @Transactional
    public UserTeam joinTeam(Long teamId, Long userId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> BusinessException.notFound("User", userId));

        // Security checks
        securityService.requirePublicTeamForJoin(team);
        securityService.requireTeamCapacity(team);

        // Check for existing membership (including soft-deleted)
        Optional<UserTeam> existingMembership = userTeamRepository.findByUserAndTeamIncludingDeleted(userId, teamId);

        if (existingMembership.isPresent()) {
            UserTeam membership = existingMembership.get();
            if (!membership.isDeleted()) {
                throw BusinessException.conflict("User is already a member of this team");
            }
            // Restore soft-deleted membership
            membership.setDeleted(false);
            membership.setRole(TeamRole.MEMBER);
            membership.setJoinedAt(java.time.Instant.now());
            userTeamRepository.persist(membership);
            LOG.infov("User {0} rejoined team {1}", userId, team.getSlug());
            return membership;
        }

        // Create new membership
        UserTeam membership = new UserTeam(user, team, TeamRole.MEMBER);
        userTeamRepository.persist(membership);

        LOG.infov("User {0} joined team {1}", userId, team.getSlug());
        return membership;
    }

    @Transactional
    public UserTeam addMember(Long teamId, Long targetUserId, TeamRole role, Long actingUserId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        // Security checks
        securityService.requireCanManageMembers(actingUserId, teamId);
        securityService.requireTeamCapacity(team);

        User targetUser = userRepository.findActiveById(targetUserId)
                .orElseThrow(() -> BusinessException.notFound("User", targetUserId));

        // Check for existing membership (including soft-deleted)
        Optional<UserTeam> existingMembership = userTeamRepository.findByUserAndTeamIncludingDeleted(targetUserId, teamId);

        if (existingMembership.isPresent()) {
            UserTeam membership = existingMembership.get();
            if (!membership.isDeleted()) {
                throw BusinessException.conflict("User is already a member of this team");
            }
            // Restore soft-deleted membership
            User actingUser = userRepository.findActiveById(actingUserId).orElse(null);
            membership.setDeleted(false);
            membership.setRole(role);
            membership.setJoinedAt(java.time.Instant.now());
            membership.setInvitedBy(actingUser);
            userTeamRepository.persist(membership);
            LOG.infov("User {0} re-added to team {1} with role {2} by user {3}",
                    targetUserId, team.getSlug(), role, actingUserId);
            return membership;
        }

        // Create new membership
        User actingUser = userRepository.findActiveById(actingUserId).orElseThrow(NotFoundException::new);
        UserTeam membership = new UserTeam(targetUser, team, role, actingUser);
        userTeamRepository.persist(membership);

        LOG.infov("User {0} added to team {1} with role {2} by user {3}",
                targetUserId, team.getSlug(), role, actingUserId);
        return membership;
    }

    @Transactional
    public UserTeam updateMemberRole(Long teamId, Long targetUserId, TeamRole newRole, Long actingUserId) {
        // Security checks
        securityService.requireCanManageMembers(actingUserId, teamId);

        UserTeam targetMembership = userTeamRepository.findByUserAndTeam(targetUserId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Membership not found"));

        securityService.requireNotLastAdminDemotion(teamId, targetMembership, newRole);

        targetMembership.setRole(newRole);
        userTeamRepository.persist(targetMembership);

        LOG.infov("User {0} role updated to {1} in team {2} by user {3}",
                targetUserId, newRole, teamId, actingUserId);
        return targetMembership;
    }

    @Transactional
    public void removeMember(Long teamId, Long targetUserId, Long actingUserId) {
        // Security checks
        securityService.requireCanRemoveMember(actingUserId, targetUserId, teamId);

        UserTeam targetMembership = userTeamRepository.findByUserAndTeam(targetUserId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Membership not found"));

        securityService.requireNotLastAdmin(teamId, targetMembership);

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
