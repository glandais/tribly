package com.tribly.service.team;

import com.tribly.domain.team.*;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.security.TeamSecurityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@ApplicationScoped
public class TeamService {

    private static final Logger LOG = Logger.getLogger(TeamService.class);
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserTeamRepository userTeamRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TeamSecurityService securityService;

    @Transactional
    public Team createTeam(CreateTeamRequest request, Long creatorId) {
        User creator = userRepository.findActiveById(creatorId)
                .orElseThrow(() -> BusinessException.notFound("User", creatorId));

        String slug = generateSlug(request.name());
        if (teamRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 10000;
        }

        Team team = new Team(request.name(), slug);
        team.setDescription(request.description());
        team.setPublic(request.isPublic() != null && request.isPublic());
        team.setMaxMembers(request.maxMembers());

        teamRepository.persist(team);

        UserTeam membership = new UserTeam(creator, team, TeamRole.ADMIN);
        userTeamRepository.persist(membership);

        LOG.infov("Team {0} created by user {1}", team.getSlug(), creatorId);
        return team;
    }

    public Optional<Team> getTeamBySlug(String slug) {
        return teamRepository.findBySlug(slug);
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findActiveById(id);
    }

    public List<Team> getUserTeams(Long userId) {
        return teamRepository.findByUserId(userId);
    }

    public List<Team> getPublicTeams(int page, int size) {
        return teamRepository.findPublicTeams(page, size);
    }

    public long countPublicTeams() {
        return teamRepository.countPublicTeams();
    }

    public List<Team> searchPublicTeams(String query, int page, int size) {
        return teamRepository.searchPublicTeams(query, page, size);
    }

    @Transactional
    public Team updateTeam(Long teamId, UpdateTeamRequest request, Long userId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        securityService.requireAdmin(userId, teamId);

        if (request.name() != null) {
            team.setName(request.name());
        }
        if (request.description() != null) {
            team.setDescription(request.description());
        }
        if (request.isPublic() != null) {
            team.setPublic(request.isPublic());
        }
        if (request.logoUrl() != null) {
            team.setLogoUrl(request.logoUrl());
        }
        if (request.coverImageUrl() != null) {
            team.setCoverImageUrl(request.coverImageUrl());
        }
        if (request.maxMembers() != null) {
            team.setMaxMembers(request.maxMembers());
        }

        teamRepository.persist(team);
        LOG.infov("Team {0} updated by user {1}", team.getSlug(), userId);
        return team;
    }

    @Transactional
    public void deleteTeam(Long teamId, Long userId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        securityService.requireAdmin(userId, teamId);

        team.softDelete();
        teamRepository.persist(team);
        LOG.infov("Team {0} deleted by user {1}", team.getSlug(), userId);
    }

    public Optional<TeamRole> getUserRole(Long userId, Long teamId) {
        return userTeamRepository.findByUserAndTeam(userId, teamId)
                .map(UserTeam::getRole);
    }

    public Optional<TeamRole> getUserRoleBySlug(Long userId, String teamSlug) {
        Optional<UserTeam> userTeam = userTeamRepository.findByUserAndTeamSlug(userId, teamSlug);
        LOG.infov("getUserRoleBySlug: userId={0}, teamSlug={1}, found={2}",
                userId, teamSlug, userTeam.isPresent());
        return userTeam.map(UserTeam::getRole);
    }

    private String generateSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-+", "-").replaceAll("^-|-$", "");
    }

    public record CreateTeamRequest(
            String name,
            String description,
            Boolean isPublic,
            Integer maxMembers
    ) {}

    public record UpdateTeamRequest(
            String name,
            String description,
            Boolean isPublic,
            String logoUrl,
            String coverImageUrl,
            Integer maxMembers
    ) {}
}
