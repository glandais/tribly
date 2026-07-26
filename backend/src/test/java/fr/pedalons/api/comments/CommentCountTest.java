package fr.pedalons.api.comments;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code commentCount} on publications and routes.
 *
 * <p>The field follows the comments themselves: comments are members-only, so a visitor who would be
 * refused {@code GET …/comments} must not be told how many there are either. "Absent" and "zero" are
 * two different answers here, and most of these tests are about telling them apart.
 */
@QuarkusTest
class CommentCountTest extends AbstractResourceTest {

  private Ride ride;
  private Post post;
  private Trip trip;
  private Route route;

  @BeforeEach
  void setUpFixture() {
    super.setUp();
    ride =
        dataService.createRide(
            team1,
            user1,
            "Counted ride",
            "counted-ride",
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED);
    post = dataService.createPost(team1, user1, "Counted post", Instant.now());
    trip = dataService.createTrip(team1, user1, "Counted trip", Instant.now());
    route = dataService.createRoute(team1, user1, "Counted route", Visibility.PUBLIC);

    // 2 roots + 1 reply on the ride = 3; one comment on each of the others.
    Comment rideRoot = dataService.createComment(user1, ride, "ride root");
    dataService.createComment(user1, ride, "ride root 2");
    dataService.createReply(user3, ride, rideRoot, "ride reply");
    dataService.createComment(user1, post, "post comment");
    dataService.createComment(user1, trip, "trip comment");
    dataService.createComment(user1, route, "route comment");
  }

  // ==================== Detail ====================

  @Test
  void getRide_asMember_shouldCountRepliesToo() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + ride.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", equalTo(3));
  }

  @Test
  void getPost_asMember_shouldExposeTheCount() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/posts/" + post.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", equalTo(1));
  }

  @Test
  void getTrip_asMember_shouldExposeTheCount() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/trips/" + trip.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", equalTo(1));
  }

  @Test
  void getRoute_asMember_shouldExposeTheCount() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/routes/" + route.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", equalTo(1));
  }

  /** A public ride is readable by anyone; its comments are not. The field must simply not exist. */
  @Test
  void getRide_anonymously_shouldNotExposeAnyCount() {
    given()
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + ride.getSlug())
        .then()
        .statusCode(200)
        // Jackson is NON_NULL: no key at all, which reads as null through this path.
        .body("commentCount", nullValue());
  }

  @Test
  void getRide_asNonMember_shouldNotExposeAnyCount() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + ride.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", nullValue());
  }

  /** Zero is a real answer, and it must be distinguishable from "you may not know". */
  @Test
  void getRide_withoutComments_asMember_shouldExposeZero() {
    Ride quiet =
        dataService.createRide(
            team1,
            user1,
            "Quiet ride",
            "quiet-ride",
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + quiet.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", equalTo(0));
  }

  // ==================== Lists ====================

  @Test
  void listPublications_asMember_shouldCarryTheCountOnEveryRow() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("size", 50)
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.slug == '" + ride.getSlug() + "' }.commentCount", equalTo(3))
        .body("publications.find { it.slug == '" + post.getSlug() + "' }.commentCount", equalTo(1))
        .body("publications.find { it.slug == '" + trip.getSlug() + "' }.commentCount", equalTo(1));
  }

  @Test
  void listPublications_anonymously_shouldCarryNoCountAtAll() {
    given()
        .queryParam("size", 50)
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200)
        .body("publications.find { it.slug == '" + ride.getSlug() + "' }.commentCount", nullValue())
        .body(
            "publications.find { it.slug == '" + post.getSlug() + "' }.commentCount", nullValue());
  }

  @Test
  void listRoutes_asMember_shouldCarryTheCount() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("size", 50)
        .when()
        .get("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(200)
        .body("routes.find { it.slug == '" + route.getSlug() + "' }.commentCount", equalTo(1));
  }

  @Test
  void listRoutes_anonymously_shouldCarryNoCount() {
    given()
        .queryParam("size", 50)
        .when()
        .get("/api/teams/" + team1Slug + "/routes")
        .then()
        .statusCode(200)
        .body("routes.find { it.slug == '" + route.getSlug() + "' }.commentCount", nullValue());
  }

  // ==================== Tenancy ====================

  /**
   * Two domains, the same team slug, the same ride slug, the same person's email on both sides. The
   * comments of the first domain must not be counted for the second one's ride — the count query
   * takes a set of ids rather than one already-resolved id, which is exactly the shape that leaks if
   * the domain clause is forgotten.
   */
  @Test
  void getRide_fromAnotherDomain_shouldCountOnlyThatDomainsComments() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL3, "Test User 3 elsewhere");
    // createTeam already enrols its creator as ADMIN.
    Team otherTeam =
        dataService.createTeam(other, otherUser, "Team 1", team1Slug, Visibility.PUBLIC);
    Ride otherRide =
        dataService.createRide(
            otherTeam,
            otherUser,
            "Counted ride",
            ride.getSlug(),
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED);
    dataService.createComment(otherUser, otherRide, "other domain only");

    given()
        .header("X-Forwarded-Host", "other.localhost")
        .auth()
        .oauth2(jwtService.generateAccessToken(otherUser))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + ride.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", equalTo(1));
  }

  /**
   * Membership is domain-scoped too. The same email is a member of "team-1" on the default domain;
   * on the second domain that email is a different user, member of nothing. The publicly readable
   * ride still answers, but with no count at all — a membership in one tenant must not unlock
   * another tenant's threads.
   */
  @Test
  void getRide_onAnotherDomain_asANonMemberThere_shouldNotExposeAnyCount() {
    Domain other = dataService.createDomain("other2.localhost", "Other", "http://other2.localhost");
    User otherAdmin = dataService.createUser(other, EMAIL1, "Owner elsewhere");
    User otherUser = dataService.createUser(other, EMAIL3, "Test User 3 elsewhere");
    Team otherTeam =
        dataService.createTeam(other, otherAdmin, "Team 1", team1Slug, Visibility.PUBLIC);
    Ride otherRide =
        dataService.createRide(
            otherTeam,
            otherAdmin,
            "Counted ride",
            ride.getSlug(),
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED);
    dataService.createComment(otherAdmin, otherRide, "other domain only");

    given()
        .header("X-Forwarded-Host", "other2.localhost")
        .auth()
        .oauth2(jwtService.generateAccessToken(otherUser))
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + ride.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", nullValue());
  }
}
