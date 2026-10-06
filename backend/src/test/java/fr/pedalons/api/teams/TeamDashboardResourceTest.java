package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportTargetType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code GET /api/teams/{teamSlug}/dashboard}: one response, graded by role. Fixture: team1 has
 * user1 (ADMIN), user2 (ORGANIZER), user3 (MEMBER); user4 and user5 belong to no team.
 */
@QuarkusTest
class TeamDashboardResourceTest extends AbstractResourceTest {

  private Instant soon;
  private Ride routedRide;
  private Ride bareRide;
  private Ride fullRide;
  private String tripSlug;
  private String reportedSlug;

  private String dashboard() {
    return "/api/teams/" + team1Slug + "/dashboard";
  }

  private ValidatableResponse dashboardAs(String user) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .when()
        .get(dashboard())
        .then()
        .statusCode(200);
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    soon = Instant.now().plus(3, ChronoUnit.DAYS);

    Route route = dataService.createRoute(team1, user1, "Monts d'Or");
    dataService.setRouteMetrics(route, 72000f, 980f);

    // Three upcoming rides, soonest first: routed, without route, with a full group.
    routedRide = dataService.createRide(team1, user1, "Routed", "routed", soon);
    dataService.setRideRoute(routedRide, route);
    RideGroup routedGroup = dataService.createRideGroup(user1, routedRide, "Groupe B", 0);
    dataService.createParticipation(routedGroup, user3);

    bareRide = dataService.createRide(team1, user1, "Bare", "bare", soon.plus(1, ChronoUnit.DAYS));

    fullRide = dataService.createRide(team1, user1, "Full", "full", soon.plus(2, ChronoUnit.DAYS));
    dataService.setRideRoute(fullRide, route);
    RideGroup fullGroup = dataService.createRideGroupWithMaxParticipants(user1, fullRide, "A", 1);
    dataService.createParticipation(fullGroup, user2);

    // A past ride and a draft: in neither upcoming list; the draft is an organizer's « À traiter ».
    dataService.createRide(team1, user1, "Past", "past", Instant.now().minus(1, ChronoUnit.DAYS));
    dataService.createRide(
        team1, user1, "Draft ride", "draft-ride", soon, Visibility.PUBLIC, Status.DRAFT);

    Trip trip = dataService.createTrip(team1, user1, "Traversée", soon.plus(5, ChronoUnit.DAYS));
    dataService.createTripParticipation(trip, user3);
    tripSlug = trip.getSlug();

    for (int i = 0; i < 4; i++) {
      dataService.createPost(
          team1, user1, "Post " + i, Instant.now().minus(4 - i, ChronoUnit.HOURS));
    }

    Ad ad = dataService.createAd(team1, user3, "Vélo", AdType.SALE);
    dataService.setAdDetails(ad, null, null, 45.76, 4.83);
    dataService.createAd(team1, user3, "Roue", AdType.WANTED);

    dataService.createRideTemplate(team1, user2, "Sortie du samedi", "sortie-du-samedi");

    Post reported = dataService.createPost(team1, user3, "Reported", Instant.now());
    reportedSlug = reported.getSlug();
    dataService.createReport(
        team1, user1, ReportTargetType.POST, reported.getId(), user3, ReportReason.SPAM);
  }

  @Test
  void anonymous_shouldBeUnauthorized() {
    given().when().get(dashboard()).then().statusCode(401);
  }

  @Test
  void nonMember_shouldBeForbidden() {
    given().auth().oauth2(getAccessToken(USER4)).when().get(dashboard()).then().statusCode(403);
  }

  @Test
  void unknownTeam_shouldBeNotFound() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/no-such-team/dashboard")
        .then()
        .statusCode(404);
  }

  @Test
  void member_shouldGetTheMemberSections_only() {
    dashboardAs(USER3)
        .header("Cache-Control", containsString("no-store"))
        .body("role", equalTo("MEMBER"))
        .body("team.slug", equalTo(team1Slug))
        .body("team.role", equalTo("MEMBER"))
        .body("team.memberCountByRole", nullValue())
        .body("organizer", nullValue())
        .body("admin", nullValue())
        // « Vos prochaines sorties »: the ride and the trip registered to, soonest first.
        .body("myUpcoming.publications.slug", contains("routed", tripSlug))
        .body("myUpcoming.publications[0].registeredGroup.name", equalTo("Groupe B"))
        // « Sorties à venir »: published, from now, soonest first, at most 3.
        .body("upcomingRides.publications.slug", contains("routed", "bare", "full"))
        .body("upcomingRides.total", equalTo(3))
        .body("upcomingRides.publications[0].registered", equalTo(true))
        .body("upcomingRides.publications[0].distance", equalTo(72000f))
        .body("upcomingRides.publications[0].groupSummaries[0].countParticipants", equalTo(1))
        .body("upcomingRides.publications[2].groupSummaries[0].full", equalTo(true))
        // Compact rows: the markdown body is not sent.
        .body("upcomingRides.publications[0].media.markdown", equalTo(""))
        // « Dernières publications »: newest first, at most 3.
        .body("latestPosts.publications", hasSize(3))
        .body("latestPosts.publications[0].slug", equalTo(reportedSlug))
        .body("latestPosts.total", equalTo(5))
        .body("newRoutes.routes[0].name", equalTo("Monts d'Or"))
        // « Annonces »: the null price is « Prix à négocier » on the client; no exact position.
        .body("latestAds.ads", hasSize(2))
        .body("latestAds.ads.price", contains(nullValue(), nullValue()));
  }

  @Test
  void organizer_shouldGetTheOrganizerBlock_butNoAdminBlock() {
    dashboardAs(USER2)
        .body("role", equalTo("ORGANIZER"))
        .body("admin", nullValue())
        .body("organizer", notNullValue())
        .body("organizer.drafts.publications.slug", contains("draft-ride"))
        .body("organizer.drafts.total", equalTo(1))
        .body("organizer.ridesWithoutRoute.publications.slug", contains("bare"))
        .body("organizer.ridesWithFullGroup.publications.slug", contains("full"))
        .body("organizer.reports.openCount", equalTo(1))
        .body("organizer.reports.latestReason", equalTo("SPAM"))
        .body("organizer.reports.latestTargetType", equalTo("POST"))
        .body("organizer.rideTemplates.templates.name", contains("Sortie du samedi"))
        // The draft never leaks into the member sections, organizer or not.
        .body("upcomingRides.publications.slug", not(contains("draft-ride")));
  }

  @Test
  void admin_shouldGetEveryBlock() {
    dashboardAs(USER1)
        .body("role", equalTo("ADMIN"))
        .body("organizer", notNullValue())
        .body("team.memberCountByRole.admins", equalTo(1))
        .body("team.memberCountByRole.organizers", equalTo(1))
        .body("team.memberCountByRole.members", equalTo(1))
        .body(
            "admin.newestMembers.members.user.displayName",
            contains("Test User 3", "Test User 2", "Test User 1"))
        .body("admin.newestMembers.members[0].role", equalTo("MEMBER"))
        .body("admin.newestMembers.members[0].joinedAt", notNullValue())
        .body("admin.newestMembers.total", equalTo(3))
        .body("admin.webhook.configured", equalTo(false));
  }

  @Test
  void platformAdmin_notAMember_shouldGetTheAdminDashboard() {
    dataService.createPlatformAdminUser("root@example.com", "Root");
    dashboardAs("root").body("role", equalTo("ADMIN")).body("admin", notNullValue());
  }

  /** The team lists show an administrator the deleted content; the dashboard never does. */
  @Test
  void admin_shouldNotSeeDeletedContent() {
    dataService.deleteRide(bareRide);
    dashboardAs(USER1)
        .body("upcomingRides.publications.slug", contains("routed", "full"))
        .body("organizer.ridesWithoutRoute.publications", empty());
  }

  /** A moderator is never told about a report on themselves. */
  @Test
  void reports_aboutTheModeratorThemself_shouldNotCount() {
    Post own = dataService.createPost(team1, user2, "Own", Instant.now());
    dataService.createReport(
        team1, user3, ReportTargetType.POST, own.getId(), user2, ReportReason.OTHER);
    dashboardAs(USER2).body("organizer.reports.openCount", equalTo(1));
    dashboardAs(USER1).body("organizer.reports.openCount", equalTo(2));
  }

  @Test
  void reports_withNoneOpen_shouldBeZeroAndNoLatest() {
    dataService.deleteAllReports();
    dashboardAs(USER2)
        .body("organizer.reports.openCount", equalTo(0))
        .body("organizer.reports.latestReason", nullValue());
  }

  @Test
  void disabledModules_shouldNullTheirSections() {
    dataService.setTeamFeatureFlags(team1, false, true, false, false, false);
    dashboardAs(USER1)
        .body("myUpcoming", nullValue())
        .body("upcomingRides", nullValue())
        .body("latestPosts", nullValue())
        .body("latestAds", nullValue())
        .body("newRoutes", notNullValue())
        .body("organizer.ridesWithoutRoute", nullValue())
        .body("organizer.ridesWithFullGroup", nullValue())
        .body("organizer.rideTemplates", nullValue())
        .body("organizer.drafts", notNullValue());
  }

  /** Rides and trips need routes too: with routes off, nothing of the three is shown. */
  @Test
  void routesDisabled_shouldNullRidesTripsAndRoutes() {
    dataService.setTeamFeatureFlags(team1, true, false, true, true, true);
    dashboardAs(USER3)
        .body("myUpcoming", nullValue())
        .body("upcomingRides", nullValue())
        .body("newRoutes", nullValue())
        .body("latestPosts", notNullValue());
  }

  /** A member who left is a visitor again. */
  @Test
  void formerMember_shouldBeForbidden() {
    dataService.addUserToTeam(user5, team1, TeamRole.MEMBER);
    dashboardAs(USER5);
    dataService.removeMember(user5, team1);
    given().auth().oauth2(getAccessToken(USER5)).when().get(dashboard()).then().statusCode(403);
  }

  /** The private team's dashboard is its members' only. */
  @Test
  void privateTeam_member_shouldGetIt_outsiderNot() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team2Slug + "/dashboard")
        .then()
        .statusCode(200)
        .body("role", equalTo("MEMBER"));
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team2Slug + "/dashboard")
        .then()
        .statusCode(403);
  }
}
