package com.tribly.service.team;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamSlugRedirect;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.repository.TeamQuery;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.teams.response.TeamDetailDto;
import com.tribly.dto.teams.response.TeamListResponse;
import com.tribly.enums.TeamRole;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.service.asset.AssetService;
import com.tribly.service.common.SlugService;
import com.tribly.service.security.TeamSecurityService;
import com.tribly.service.team.request.MinRole;
import com.tribly.service.team.response.TeamAndRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamService {

  private static final Logger LOG = Logger.getLogger(TeamService.class);

  @Inject UserTeamRepository userTeamRepository;

  @Inject protected TeamSecurityService securityService;

  @Inject protected TeamRepository teamRepository;

  @Inject protected UserRepository userRepository;

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  protected TeamAndRole getTeam(String slug, @Nullable Long userId, boolean throwRedirect) {
    try {
      return teamRepository
          .findOne(slug, userId)
          .orElseThrow(() -> BusinessException.notFound("Team", slug));
    } catch (NotFoundException e) {
      Optional<TeamSlugRedirect> redirect = slugService.resolveTeamRedirect(slug);
      if (redirect.isPresent()) {
        String newSlug = redirect.get().getTeam().getSlug();
        Optional<TeamAndRole> newOne = teamRepository.findOne(newSlug, userId);
        if (newOne.isPresent()) {
          if (throwRedirect) {
            throw BusinessException.newSlug(slug, newSlug, newSlug);
          } else {
            return newOne.get();
          }
        }
      }
      throw e;
    }
  }

  @Transactional
  public TeamDetailDto createTeam(TeamRequest request, Long creatorId) {
    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    String slug = slugService.generateSlug(request.name(), teamRepository::existsBySlug);
    slugService.clearTeamRedirect(slug);

    Team team = new Team(creator, request.name(), slug, request.visibility());
    team.setVisibility(request.visibility());

    teamRepository.persistAndFlush(team);
    updateMedia(team.getAboutPage(), request.media());
    teamRepository.persist(team);

    UserTeam membership = new UserTeam(creator, creator, team, TeamRole.ADMIN);
    userTeamRepository.persist(membership);

    LOG.infov("Team {0} created by user {1}", team.getSlug(), creatorId);
    return TeamDetailDto.from(new TeamAndRole(team, TeamRole.ADMIN, 1L), assetService);
  }

  @Transactional
  public TeamListResponse listTeams(
      @Nullable Long userId, MinRole minRole, @Nullable String search, int page, int size) {
    TriblyPage<TeamAndRole> teams =
        teamRepository.find(
            TeamQuery.builder()
                .userId(userId)
                .minRole(minRole)
                .search(search)
                .page(page)
                .size(size)
                .build());
    List<TeamDetailDto> dtos =
        teams.items().stream()
            .map(teamAndRole -> TeamDetailDto.from(teamAndRole, assetService))
            .toList();
    return new TeamListResponse(dtos, teams.total(), page, size);
  }

  public TeamDetailDto getTeamDetailDto(String slug, @Nullable Long userId) {
    TeamAndRole teamAndRole = getTeam(slug, userId, true);
    return TeamDetailDto.from(teamAndRole, assetService);
  }

  @Transactional
  public TeamDetailDto updateTeam(String teamSlug, TeamRequest request, Long userId) {
    Team team = getTeam(teamSlug, userId, false).team();

    securityService.requireAdmin(userId, teamSlug);

    team.setName(request.name());
    team.setVisibility(request.visibility());
    team.setEnableTrips(request.enableTrips());
    team.setEnableAds(request.enableAds());
    updateMedia(team.getAboutPage(), request.media());

    teamRepository.persist(team);
    LOG.infov("Team {0} updated by user {1}", team.getSlug(), userId);
    return getTeamDetailDto(team.getSlug(), userId);
  }

  @Transactional
  public void deleteTeam(String teamSlug, Long userId) {
    Team team = getTeam(teamSlug, userId, false).team();

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

  @Transactional
  public TeamDetailDto updateSlug(String currentSlug, String newSlug, Long userId) {
    Team team = getTeam(currentSlug, userId, false).team();

    securityService.requireAdmin(userId, currentSlug);

    // Validate new slug format
    if (!slugService.isValidSlug(newSlug)) {
      throw BusinessException.validation("Invalid slug format");
    }

    // No change needed
    if (currentSlug.equals(newSlug)) {
      return getTeamDetailDto(currentSlug, userId);
    }

    // Check if new slug is already taken (by a non-deleted team)
    if (teamRepository.existsBySlug(newSlug)) {
      throw BusinessException.conflict("Slug already in use", "SLUG_TAKEN");
    }

    // Clear any existing redirect TO this new slug (reuse scenario)
    slugService.clearTeamRedirect(newSlug);

    // Create redirect from old slug to this team
    slugService.createTeamRedirect(team, currentSlug);

    // Update the slug
    team.setSlug(newSlug);
    teamRepository.persist(team);

    LOG.infov("Team slug changed from {0} to {1} by user {2}", currentSlug, newSlug, userId);
    return getTeamDetailDto(newSlug, userId);
  }

  protected void updateMedia(TeamEntity teamEntity, MediaDto mediaDto) {
    // FIXME MediaService
    teamEntity.setMarkdown(mediaDto.markdown());
    assetService.updateAssets(teamEntity, mediaDto.assets());
  }
}
