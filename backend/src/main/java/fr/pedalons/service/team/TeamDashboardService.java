package fr.pedalons.service.team;

import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.ads.request.AdSearchParams;
import fr.pedalons.dto.ads.response.AdListResponse;
import fr.pedalons.dto.dashboard.response.TeamDashboardAdminDto;
import fr.pedalons.dto.dashboard.response.TeamDashboardDto;
import fr.pedalons.dto.dashboard.response.TeamDashboardOrganizerDto;
import fr.pedalons.dto.publications.response.PublicationListResponse;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.ridetemplates.response.RideTemplateListResponse;
import fr.pedalons.dto.routes.response.RouteListResponse;
import fr.pedalons.dto.teams.response.TeamDetailDto;
import fr.pedalons.enums.ListViewMode;
import fr.pedalons.enums.MemberSortBy;
import fr.pedalons.enums.SortDirection;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.service.ad.AdService;
import fr.pedalons.service.common.PublicationService;
import fr.pedalons.service.moderation.ModerationService;
import fr.pedalons.service.notification.TeamWebhookService;
import fr.pedalons.service.ridetemplate.RideTemplateService;
import fr.pedalons.service.route.RouteService;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;

/**
 * A team's « Tableau de bord »: every section a member, an organizer or an administrator sees on
 * it, in one response.
 *
 * <p>Nothing is computed here that a list endpoint does not already compute: each section is a short
 * page of an existing listing, built by its own service with its own per-page lookups. The cost of
 * the dashboard is therefore a fixed number of queries per section, whatever the rows hold ({@code
 * TeamDashboardQueryCountTest}) — and a section shows exactly the rows its « Voir tout » opens.
 *
 * <p>Two departures from the plain listings, both deliberate: deleted content is left out even for
 * an administrator (the team lists show it to them, a dashboard is not where it gets restored), and
 * every row is {@link ListViewMode#COMPACT}.
 */
@ApplicationScoped
public class TeamDashboardService {

  /** « Vos prochaines sorties ». */
  static final int MY_UPCOMING_SIZE = 3;

  /** « Sorties à venir ». */
  static final int UPCOMING_RIDES_SIZE = 3;

  /** « Dernières publications », « Nouveaux parcours », « Annonces ». */
  static final int LATEST_SIZE = 3;

  /** The « À traiter » tiles and the templates panel: a figure and the first few names. */
  static final int TILE_SIZE = 5;

  /** « Nouveaux membres » of the administration panel. */
  static final int NEWEST_MEMBERS_SIZE = 3;

  @Inject PedalonsQueryContext pedalonsContext;
  @Inject TeamService teamService;
  @Inject TeamMembershipService membershipService;
  @Inject PublicationService publicationService;
  @Inject RouteService routeService;
  @Inject AdService adService;
  @Inject RideTemplateService rideTemplateService;
  @Inject ModerationService moderationService;
  @Inject TeamWebhookService teamWebhookService;

  /**
   * The dashboard of one team for the current user.
   *
   * @throws ForbiddenException when the caller is not a member of the team — a visitor, member of
   *     another team or not, keeps the public team page
   */
  @Logged
  @Transactional
  public TeamDashboardDto getDashboard(String teamSlug) {
    Team team = teamService.getTeam(teamSlug);
    TeamRole role = roleOf(team);
    TeamDetailDto detail = teamService.getTeamDetailDto(teamSlug);
    Instant now = Instant.now();

    // The same module rules as the publication query: a ride or a trip needs routes too.
    boolean rides = team.isEnableRides() && team.isEnableRoutes();
    boolean trips = team.isEnableTrips() && team.isEnableRoutes();

    PublicationListResponse myUpcoming =
        rides || trips
            ? publicationService.listTeamSection(
                team, MY_UPCOMING_SIZE, q -> q.participating(true).from(now).ascending(true))
            : null;
    PublicationListResponse upcomingRides = rides ? upcomingRides(team, now, false, false) : null;
    PublicationListResponse latestPosts =
        team.isEnablePosts()
            ? publicationService.listTeamSection(
                team, LATEST_SIZE, q -> q.type(PublicationType.POST).status(Status.PUBLISHED))
            : null;
    RouteListResponse newRoutes =
        team.isEnableRoutes() ? routeService.listTeamSection(team, LATEST_SIZE) : null;
    AdListResponse latestAds =
        team.isEnableAds()
            ? adService.listAds(
                teamSlug, AdSearchParams.builder().build(), ListViewMode.COMPACT, 0, LATEST_SIZE)
            : null;

    TeamDashboardOrganizerDto organizer =
        role.isOrganizer() ? organizer(team, teamSlug, rides, now) : null;
    TeamDashboardAdminDto admin = role.isAdmin() ? admin(teamSlug) : null;

    return new TeamDashboardDto(
        detail,
        role,
        myUpcoming,
        upcomingRides,
        latestPosts,
        newRoutes,
        latestAds,
        organizer,
        admin);
  }

  /** The caller's role in the team; a platform admin is an administrator everywhere. */
  private TeamRole roleOf(Team team) {
    if (pedalonsContext.isPlatformAdmin()) {
      return TeamRole.ADMIN;
    }
    TeamRole role = pedalonsContext.getContext(team).teamRole();
    if (role == null) {
      throw new ForbiddenException();
    }
    return role;
  }

  private TeamDashboardOrganizerDto organizer(
      Team team, String teamSlug, boolean rides, Instant now) {
    PublicationListResponse drafts =
        publicationService.listTeamSection(team, TILE_SIZE, q -> q.status(Status.DRAFT));
    RideTemplateListResponse templates =
        rides ? rideTemplateService.listTemplates(teamSlug, null, 0, TILE_SIZE) : null;
    return new TeamDashboardOrganizerDto(
        drafts,
        rides ? upcomingRides(team, now, true, false) : null,
        rides ? upcomingRides(team, now, false, true) : null,
        moderationService.teamOpenSummary(team),
        templates);
  }

  /**
   * The team's published rides from now on, soonest first: all of them, or only those routed
   * nowhere, or only those with a full group.
   */
  private PublicationListResponse upcomingRides(
      Team team, Instant now, boolean withoutRoute, boolean withFullGroup) {
    return publicationService.listTeamSection(
        team,
        withoutRoute || withFullGroup ? TILE_SIZE : UPCOMING_RIDES_SIZE,
        q ->
            q.type(PublicationType.RIDE)
                .status(Status.PUBLISHED)
                .from(now)
                .withoutRoute(withoutRoute)
                .withFullGroup(withFullGroup)
                .ascending(true));
  }

  private TeamDashboardAdminDto admin(String teamSlug) {
    return new TeamDashboardAdminDto(
        membershipService.getTeamMembers(
            teamSlug,
            0,
            NEWEST_MEMBERS_SIZE,
            null,
            null,
            MemberSortBy.JOINED_AT,
            SortDirection.DESC),
        teamWebhookService.get(teamSlug));
  }
}
