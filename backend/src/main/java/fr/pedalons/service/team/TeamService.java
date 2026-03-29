package fr.pedalons.service.team;

import static fr.pedalons.dto.error.ErrorCode.INVALID_SLUG;
import static fr.pedalons.dto.error.ErrorCode.SLUG_TAKEN;
import static fr.pedalons.dto.error.ErrorCode.TEAM_CREATION_DISABLED;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.TeamSlugRedirect;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.teams.request.TeamRequest;
import fr.pedalons.dto.teams.response.TeamDetailDto;
import fr.pedalons.dto.teams.response.TeamListResponse;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.infrastructure.exception.*;
import fr.pedalons.repository.team.TeamQuery;
import fr.pedalons.repository.team.TeamRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.common.SlugService;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.team.request.MinRole;
import fr.pedalons.service.team.response.TeamAndRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamService {

  @Inject UserTeamRepository userTeamRepository;

  @Inject protected TeamRepository teamRepository;

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  @Inject PedalonsQueryContext pedalonsContext;

  public Team getTeam(String teamSlug) {
    Long domainId = pedalonsContext.getDomainId();
    Optional<Team> optionalTeam = teamRepository.findBySlugAndDomain(domainId, teamSlug);
    if (optionalTeam.isPresent()) {
      return optionalTeam.get();
    }
    Optional<TeamSlugRedirect> redirect = slugService.resolveTeamRedirect(domainId, teamSlug);
    if (redirect.isPresent()) {
      return redirect.get().getTeam();
    }
    throw new NotFoundException(EntityType.TEAM, teamSlug);
  }

  protected TeamAndRole getTeamAndRole(Long id) {
    return teamRepository
        .findOne(pedalonsContext.getDomainId(), id, pedalonsContext.getUserIdNullable())
        .orElseThrow(() -> new NotFoundException(EntityType.TEAM, id));
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.CREATE)
  public TeamDetailDto createTeam(TeamRequest request) {
    Domain domain = pedalonsContext.getDomain();
    if (domain.isSingleTeam() && teamRepository.existsByDomain(domain.getId())) {
      throw new BusinessException(TEAM_CREATION_DISABLED);
    }
    User creator = pedalonsContext.getUser();
    Long domainId = domain.getId();
    String slug =
        slugService.generateSlug(
            request.name(), s -> teamRepository.existsBySlugAndDomain(domainId, s));
    slugService.clearTeamRedirect(domainId, slug);

    Team team = new Team(domain, creator, request.name(), slug, request.visibility());
    team.setVisibility(request.visibility());
    team.setGeometry(request.geometry());

    teamRepository.persistAndFlush(team);
    assetService.updateAssets(team.getAboutPage(), request.media());
    teamRepository.persist(team);

    UserTeam membership = new UserTeam(creator, creator, team, TeamRole.ADMIN);
    userTeamRepository.persist(membership);

    return TeamDetailDto.from(new TeamAndRole(team, TeamRole.ADMIN, 1L), assetService);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.LIST)
  public TeamListResponse listTeams(MinRole minRole, @Nullable String search, int page, int size) {
    PedalonsPage<TeamAndRole> teams =
        teamRepository.find(
            TeamQuery.builder()
                .domainId(pedalonsContext.getDomainId())
                .userId(pedalonsContext.getUserIdNullable())
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
    team.setEnablePosts(request.enablePosts());
    team.setEnableRides(request.enableRides());
    team.setEnableRoutes(request.enableRoutes());
    team.setGeometry(request.geometry());
    assetService.updateAssets(team.getAboutPage(), request.media());

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
    Long domainId = pedalonsContext.getDomainId();
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

    // Check if new slug is already taken (by a non-deleted team in this domain)
    if (teamRepository.existsBySlugAndDomain(domainId, newSlug)) {
      throw new ConflictException(SLUG_TAKEN);
    }

    // Clear any existing redirect TO this new slug (reuse scenario)
    slugService.clearTeamRedirect(domainId, newSlug);

    // Create redirect from old slug to this team
    slugService.createTeamRedirect(team, currentSlug);

    // Update the slug
    team.setSlug(newSlug);
    teamRepository.persist(team);

    return getTeamDetailDto(teamSlug);
  }
}
