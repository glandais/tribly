package com.tribly.service.team;

import static com.tribly.dto.error.ErrorCode.INVALID_SLUG;
import static com.tribly.dto.error.ErrorCode.SLUG_TAKEN;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamSlugRedirect;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.repository.TeamQuery;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.teams.response.TeamDetailDto;
import com.tribly.dto.teams.response.TeamListResponse;
import com.tribly.enums.AllEntityType;
import com.tribly.enums.TeamRole;
import com.tribly.infrastructure.exception.*;
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

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  public Team getTeam(String teamSlug) {
    Optional<Team> optionalTeam = teamRepository.findBySlug(teamSlug);
    if (optionalTeam.isPresent()) {
      return optionalTeam.get();
    }
    Optional<TeamSlugRedirect> redirect = slugService.resolveTeamRedirect(teamSlug);
    if (redirect.isPresent()) {
      return redirect.get().getTeam();
    }
    throw new NotFoundException(AllEntityType.TEAM, teamSlug);
  }

  protected TeamAndRole getTeam(Long id, @Nullable User user) {
    return teamRepository
        .findOne(id, user == null ? null : user.getId())
        .orElseThrow(() -> new NotFoundException(AllEntityType.TEAM, id));
  }

  @Transactional
  public TeamDetailDto createTeam(TeamRequest request, User creator) {
    String slug = slugService.generateSlug(request.name(), teamRepository::existsBySlug);
    slugService.clearTeamRedirect(slug);

    Team team = new Team(creator, request.name(), slug, request.visibility());
    team.setVisibility(request.visibility());

    teamRepository.persistAndFlush(team);
    updateMedia(team.getAboutPage(), request.media());
    teamRepository.persist(team);

    UserTeam membership = new UserTeam(creator, creator, team, TeamRole.ADMIN);
    userTeamRepository.persist(membership);

    LOG.infov("Team {0} created by user {1}", team.getSlug(), creator.getId());
    return TeamDetailDto.from(new TeamAndRole(team, TeamRole.ADMIN, 1L), assetService);
  }

  @Transactional
  public TeamListResponse listTeams(
      @Nullable User user, MinRole minRole, @Nullable String search, int page, int size) {
    TriblyPage<TeamAndRole> teams =
        teamRepository.find(
            TeamQuery.builder()
                .userId(user == null ? null : user.getId())
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

  public TeamDetailDto getTeamDetailDto(Team team, @Nullable User user) {
    TeamAndRole teamAndRole = getTeam(team.getId(), user);
    return TeamDetailDto.from(teamAndRole, assetService);
  }

  @Transactional
  public TeamDetailDto updateTeam(Team teamParam, TeamRequest request, User user) {
    Team team = getTeam(teamParam.getId(), user).team();
    securityService.requireAdmin(user, team);

    team.setName(request.name());
    team.setVisibility(request.visibility());
    team.setEnableTrips(request.enableTrips());
    team.setEnableAds(request.enableAds());
    updateMedia(team.getAboutPage(), request.media());

    teamRepository.persist(team);
    LOG.infov("Team {0} updated by user {1}", team.getSlug(), user.getId());
    return getTeamDetailDto(team, user);
  }

  @Transactional
  public void deleteTeam(Team teamParam, User user) {
    Team team = getTeam(teamParam.getId(), user).team();

    securityService.requireAdmin(user, team);

    team.setDeleted(true);
    teamRepository.persist(team);
    LOG.infov("Team {0} deleted by user {1}", team.getSlug(), user.getId());
  }

  @Transactional
  public TeamDetailDto updateSlug(Team teamParam, String newSlug, User user) {
    Team team = getTeam(teamParam.getId(), user).team();
    String currentSlug = team.getSlug();

    securityService.requireAdmin(user, team);

    // Validate new slug format
    if (!slugService.isValidSlug(newSlug)) {
      throw new BusinessException(INVALID_SLUG);
    }

    // No change needed
    if (currentSlug.equals(newSlug)) {
      return getTeamDetailDto(team, user);
    }

    // Check if new slug is already taken (by a non-deleted team)
    if (teamRepository.existsBySlug(newSlug)) {
      throw new ConflictException(SLUG_TAKEN);
    }

    // Clear any existing redirect TO this new slug (reuse scenario)
    slugService.clearTeamRedirect(newSlug);

    // Create redirect from old slug to this team
    slugService.createTeamRedirect(team, currentSlug);

    // Update the slug
    team.setSlug(newSlug);
    teamRepository.persist(team);

    LOG.infov("Team slug changed from {0} to {1} by user {2}", currentSlug, newSlug, user.getId());
    return getTeamDetailDto(team, user);
  }

  protected void updateMedia(TeamEntity teamEntity, MediaDto mediaDto) {
    // FIXME MediaService
    teamEntity.setMarkdown(mediaDto.markdown());
    assetService.updateAssets(teamEntity, mediaDto.assets());
  }
}
