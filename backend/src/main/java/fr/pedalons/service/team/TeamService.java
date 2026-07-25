package fr.pedalons.service.team;

import static fr.pedalons.dto.error.ErrorCode.*;

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
import fr.pedalons.enums.Visibility;
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
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamService {

  private static final int MAX_ADMIN_TEAMS_PER_USER = 1;

  @Inject UserTeamRepository userTeamRepository;

  @Inject protected TeamRepository teamRepository;

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  @Inject PedalonsQueryContext pedalonsContext;

  /**
   * Resolves a team by slug.
   *
   * <p>Deliberately NOT memoized per request, even though a team-scoped endpoint resolves the same
   * slug two or three times (the {@code @CheckAccess} interceptor, then the service method). Caching
   * it broke two invariants the tests pin down: {@link #requirePinnedTeam} is an authorization guard
   * that must run on every resolution, not just the first, and a team mutated through a different
   * transaction mid-request (see {@code TripServiceTest.shouldThrowOnLeaveWhenTripsDisabled}) must
   * be re-read rather than served stale. One extra indexed SELECT is the cheaper trade.
   */
  public Team getTeam(String teamSlug) {
    Long domainId = pedalonsContext.getDomainId();
    Team team =
        teamRepository
            .findBySlugAndDomain(domainId, teamSlug)
            .or(
                () ->
                    slugService
                        .resolveTeamRedirect(domainId, teamSlug)
                        .map(TeamSlugRedirect::getTeam))
            .orElseThrow(() -> new NotFoundException(EntityType.TEAM, teamSlug));
    // A pinned alias host serves only its team; every other team of the parent domain is invisible,
    // even after a slug redirect. Checked here so all team-scoped endpoints inherit it.
    requirePinnedTeam(team.getId(), () -> new NotFoundException(EntityType.TEAM, teamSlug));
    return team;
  }

  protected TeamAndRole getTeamAndRole(Long id) {
    boolean platformAdmin = isPlatformAdmin();
    requirePinnedTeam(id, () -> new NotFoundException(EntityType.TEAM, id));
    return teamRepository
        .findOne(
            pedalonsContext.getDomainId(), id, pedalonsContext.getUserIdNullable(), platformAdmin)
        .orElseThrow(() -> new NotFoundException(EntityType.TEAM, id));
  }

  private void requirePinnedTeam(Long teamId, Supplier<RuntimeException> notFound) {
    Long pinnedTeamId = pedalonsContext.getPinnedTeamIdNullable();
    if (pinnedTeamId != null && !pinnedTeamId.equals(teamId)) {
      throw notFound.get();
    }
  }

  private boolean isPlatformAdmin() {
    return pedalonsContext.isPlatformAdmin();
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
    if (!creator.isPlatformAdmin()) {
      long existingAdminTeams =
          userTeamRepository.countAdminTeamsByUserAndDomain(creator.getId(), domainId);
      if (existingAdminTeams >= MAX_ADMIN_TEAMS_PER_USER) {
        throw new BusinessException(USER_TEAM_LIMIT_REACHED);
      }
    }
    String slug =
        slugService.generateSlug(
            request.name(), s -> teamRepository.existsBySlugAndDomain(domainId, s));
    slugService.clearTeamRedirect(domainId, slug);

    if (request.visibility() != Visibility.TEAM) {
      throw new BusinessException(INVALID_VISIBILITY);
    }
    Team team = new Team(domain, creator, request.name(), slug, Visibility.TEAM);
    team.setVisibilityEditable(false);
    team.setJoinable(false);
    team.setAddMemberAllowed(false);
    team.setGeometry(request.geometry());

    teamRepository.persistAndFlush(team);
    assetService.updateAssets(team.getAboutPage(), request.media());
    teamRepository.persist(team);

    UserTeam membership = new UserTeam(creator, creator, team, TeamRole.ADMIN);
    userTeamRepository.persist(membership);

    return TeamDetailDto.from(new TeamAndRole(team, TeamRole.ADMIN, 1L), assetService, false);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.LIST)
  public TeamListResponse listTeams(
      @Nullable MinRole minRole, @Nullable String search, int page, int size) {
    boolean platformAdmin = isPlatformAdmin();
    PedalonsPage<TeamAndRole> teams =
        teamRepository.find(
            TeamQuery.builder()
                .domainId(pedalonsContext.getDomainId())
                .pinnedTeamId(pedalonsContext.getPinnedTeamIdNullable())
                .userId(pedalonsContext.getUserIdNullable())
                .minRole(minRole)
                .search(search)
                .page(page)
                .size(size)
                .platformAdmin(platformAdmin)
                .build());
    List<TeamDetailDto> dtos =
        teams.items().stream()
            .map(teamAndRole -> TeamDetailDto.from(teamAndRole, assetService, platformAdmin))
            .toList();
    return new TeamListResponse(dtos, teams.total(), page, size);
  }

  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.READ)
  public TeamDetailDto getTeamDetailDto(String teamSlug) {
    Team team = getTeam(teamSlug);
    TeamAndRole teamAndRole = getTeamAndRole(team.getId());
    return TeamDetailDto.from(teamAndRole, assetService, isPlatformAdmin());
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TEAM, action = ActionType.UPDATE)
  public TeamDetailDto updateTeam(String teamSlug, TeamRequest request) {
    Team team = getTeam(teamSlug);

    team.setName(request.name());
    boolean isPlatformAdmin = isPlatformAdmin();
    if (team.isVisibilityEditable() || isPlatformAdmin) {
      team.setVisibility(request.visibility());
    } else if (request.visibility() != team.getVisibility()) {
      throw new BusinessException(INVALID_VISIBILITY);
    }
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
