package com.tribly.service.team;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.*;
import com.tribly.domain.team.repository.TeamQuery;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.enums.TeamRole;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.security.TeamSecurityService;
import com.tribly.service.team.request.CreateTeamRequest;
import com.tribly.service.team.request.UpdateTeamRequest;
import com.tribly.service.team.response.TeamAndRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamService {

  private static final Logger LOG = Logger.getLogger(TeamService.class);
  private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
  private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

  @Inject TeamRepository teamRepository;

  @Inject UserTeamRepository userTeamRepository;

  @Inject UserRepository userRepository;

  @Inject TeamSecurityService securityService;

  @Transactional
  public TeamAndRole createTeam(CreateTeamRequest request, Long creatorId) {
    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    String slug = generateSlug(request.name());
    if (teamRepository.existsBySlug(slug)) {
      slug = slug + "-" + System.currentTimeMillis() % 10000;
    }

    Team team = new Team(request.name(), slug);
    team.setDescription(request.description());
    team.setVisibility(request.visibility());
    team.setMaxMembers(request.maxMembers());

    teamRepository.persist(team);

    UserTeam membership = new UserTeam(creator, team, TeamRole.ADMIN);
    userTeamRepository.persist(membership);

    LOG.infov("Team {0} created by user {1}", team.getSlug(), creatorId);
    return new TeamAndRole(team, TeamRole.ADMIN, 1L);
  }

  @Transactional
  public TriblyPage<TeamAndRole> listTeams(
      @Nullable Long userId,
      @Nullable Boolean member,
      @Nullable String search,
      int page,
      int size) {
    return teamRepository.find(new TeamQuery(page, size, null, userId, member, search));
  }

  public TeamAndRole getTeam(String slug, @Nullable Long userId) {
    return teamRepository
        .findOne(slug, userId)
        .orElseThrow(() -> BusinessException.notFound("Team"));
  }

  @Transactional
  public TeamAndRole updateTeam(String teamSlug, UpdateTeamRequest request, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    securityService.requireAdmin(userId, teamSlug);

    if (request.name() != null) {
      team.setName(request.name());
    }
    if (request.description() != null) {
      team.setDescription(request.description());
    }
    if (request.visibility() != null) {
      team.setVisibility(request.visibility());
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
    return getTeam(team.getSlug(), userId);
  }

  @Transactional
  public void deleteTeam(String teamSlug, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    securityService.requireAdmin(userId, teamSlug);

    team.softDelete();
    teamRepository.persist(team);
    LOG.infov("Team {0} deleted by user {1}", team.getSlug(), userId);
  }

  public Optional<TeamRole> getUserRole(Long userId, String teamSlug) {
    Optional<UserTeam> userTeam = userTeamRepository.findByUserAndTeam(userId, teamSlug);
    LOG.infov(
        "getUserRole: userId={0}, teamSlug={1}, found={2}", userId, teamSlug, userTeam.isPresent());
    return userTeam.map(UserTeam::getRole);
  }

  private String generateSlug(String input) {
    String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
    String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
    String slug = NONLATIN.matcher(normalized).replaceAll("");
    return slug.toLowerCase(Locale.ENGLISH).replaceAll("-+", "-").replaceAll("^-|-$", "");
  }
}
