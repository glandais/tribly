package com.tribly.service.team;

import static com.tribly.dto.error.ErrorCode.INVALID_SLUG;
import static com.tribly.dto.error.ErrorCode.SLUG_TAKEN;

import com.tribly.common.exception.BusinessException;
import com.tribly.common.exception.ConflictException;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamSlugRedirect;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.teams.response.TeamDetailDto;
import com.tribly.dto.teams.response.TeamListResponse;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.infrastructure.exception.*;
import com.tribly.repository.common.TriblyPage;
import com.tribly.repository.team.TeamQuery;
import com.tribly.repository.team.TeamRepository;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.service.asset.AssetService;
import com.tribly.service.common.SlugService;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.CheckAccess;
import com.tribly.service.team.request.MinRole;
import com.tribly.service.team.response.TeamAndRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamService {

  @Inject UserTeamRepository userTeamRepository;

  @Inject protected TeamRepository teamRepository;

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  @Inject TriblyQueryContext triblyContext;

  public Team getTeam(String teamSlug) {
    Optional<Team> optionalTeam = teamRepository.findBySlug(teamSlug);
    if (optionalTeam.isPresent()) {
      return optionalTeam.get();
    }
    Optional<TeamSlugRedirect> redirect = slugService.resolveTeamRedirect(teamSlug);
    if (redirect.isPresent()) {
      return redirect.get().getTeam();
    }
    throw new NotFoundException(EntityType.TEAM, teamSlug);
  }

  protected TeamAndRole getTeamAndRole(Long id) {
    return teamRepository
        .findOne(id, triblyContext.getUserIdNullable())
        .orElseThrow(() -> new NotFoundException(EntityType.TEAM, id));
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.CREATE)
  public TeamDetailDto createTeam(TeamRequest request) {
    User creator = triblyContext.getUser();
    String slug = slugService.generateSlug(request.name(), teamRepository::existsBySlug);
    slugService.clearTeamRedirect(slug);

    Team team = new Team(creator, request.name(), slug, request.visibility());
    team.setVisibility(request.visibility());

    teamRepository.persistAndFlush(team);
    updateMedia(team.getAboutPage(), request.media());
    teamRepository.persist(team);

    UserTeam membership = new UserTeam(creator, creator, team, TeamRole.ADMIN);
    userTeamRepository.persist(membership);

    return TeamDetailDto.from(new TeamAndRole(team, TeamRole.ADMIN, 1L), assetService);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.LIST)
  public TeamListResponse listTeams(MinRole minRole, @Nullable String search, int page, int size) {
    TriblyPage<TeamAndRole> teams =
        teamRepository.find(
            TeamQuery.builder()
                .userId(triblyContext.getUserIdNullable())
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

  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.READ)
  public TeamDetailDto getTeamDetailDto(String teamSlug) {
    Team team = getTeam(teamSlug);
    TeamAndRole teamAndRole = getTeamAndRole(team.getId());
    return TeamDetailDto.from(teamAndRole, assetService);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.UPDATE)
  public TeamDetailDto updateTeam(String teamSlug, TeamRequest request) {
    Team team = getTeam(teamSlug);

    team.setName(request.name());
    team.setVisibility(request.visibility());
    team.setEnableTrips(request.enableTrips());
    team.setEnableAds(request.enableAds());
    updateMedia(team.getAboutPage(), request.media());

    teamRepository.persist(team);
    return getTeamDetailDto(teamSlug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.DELETE)
  public void deleteTeam(String teamSlug) {
    Team team = getTeam(teamSlug);
    team.setDeleted(true);
    teamRepository.persist(team);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.UPDATE)
  public TeamDetailDto updateSlug(String teamSlug, String newSlug) {
    Team team = getTeam(teamSlug);
    String currentSlug = team.getSlug();
    // Validate new slug format
    if (!slugService.isValidSlug(newSlug)) {
      throw new BusinessException(INVALID_SLUG);
    }

    // No change needed
    if (currentSlug.equals(newSlug)) {
      return getTeamDetailDto(teamSlug);
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

    return getTeamDetailDto(teamSlug);
  }

  protected void updateMedia(TeamEntity teamEntity, @Valid MediaDto mediaRequest) {
    // FIXME MediaService
    teamEntity.setMarkdown(mediaRequest.markdown());
    assetService.updateAssets(teamEntity, mediaRequest.assets());
  }
}
