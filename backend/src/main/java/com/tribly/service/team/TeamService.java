package com.tribly.service.team;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.repository.TeamQuery;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.teams.response.TeamDetailDto;
import com.tribly.dto.teams.response.TeamListResponse;
import com.tribly.enums.TeamRole;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.security.TeamSecurityService;
import com.tribly.service.team.response.TeamAndRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.util.List;
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
  public TeamDetailDto createTeam(TeamRequest request, Long creatorId) {
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

    teamRepository.persist(team);

    UserTeam membership = new UserTeam(creator, team, TeamRole.ADMIN);
    userTeamRepository.persist(membership);

    LOG.infov("Team {0} created by user {1}", team.getSlug(), creatorId);
    return TeamDetailDto.from(new TeamAndRole(team, TeamRole.ADMIN, 1L));
  }

  @Transactional
  public TeamListResponse listTeams(
      @Nullable Long userId,
      @Nullable Boolean member,
      @Nullable String search,
      int page,
      int size) {
    TriblyPage<TeamAndRole> teams =
        teamRepository.find(new TeamQuery(page, size, null, userId, member, search));
    List<TeamDetailDto> dtos = teams.items().stream().map(TeamDetailDto::from).toList();
    return new TeamListResponse(dtos, teams.total(), page, size);
  }

  public TeamDetailDto getTeam(String slug, @Nullable Long userId) {
    return teamRepository
        .findOne(slug, userId)
        .map(TeamDetailDto::from)
        .orElseThrow(() -> BusinessException.notFound("Team"));
  }

  @Transactional
  public TeamDetailDto updateTeam(String teamSlug, TeamRequest request, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    securityService.requireAdmin(userId, teamSlug);

    team.setName(request.name());
    team.setDescription(request.description());
    team.setVisibility(request.visibility());

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

    team.setDeleted(true);
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
