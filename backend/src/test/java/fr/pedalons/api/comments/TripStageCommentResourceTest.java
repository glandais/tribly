package fr.pedalons.api.comments;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.comments.request.CommentRequest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * Comments on a trip stage (docs/LEDGER_*.md API-11): the common comment contract, plus what is
 * particular to a stage — it is read like its trip, and its count rides on the trip's detail.
 */
@QuarkusTest
class TripStageCommentResourceTest extends AbstractCommentResourceTest {

  @Override
  protected String getEntityPath() {
    return "stages";
  }

  @Override
  protected String createEntity(Team team, User creator) {
    Visibility visibility = team.getVisibility();
    Trip trip =
        dataService.createTrip(
            team, creator, "Test Trip", Instant.now(), visibility, Status.PUBLISHED, null);
    return dataService.createTripStage(creator, trip, "Test Stage").getSlug();
  }

  private String commentsPath(String stageSlug) {
    return "/api/teams/" + team1Slug + "/stages/" + stageSlug + "/comments";
  }

  @Test
  void listComments_onAStageOfADraftTrip_asPlainMember_shouldReturn404() {
    Trip draft =
        dataService.createTrip(
            team1, user1, "Draft Trip", Instant.now(), Visibility.PUBLIC, Status.DRAFT, null);
    TripStage stage = dataService.createTripStage(user1, draft, "Draft Stage");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(commentsPath(stage.getSlug()))
        .then()
        .statusCode(404);
  }

  @Test
  void listComments_onADeletedStage_shouldReturn404() {
    Trip trip = dataService.createTrip(team1, user1, "Trip With Gone Stage", Instant.now());
    TripStage stage = dataService.createTripStage(user1, trip, "Gone Stage");
    dataService.deleteTripStage(stage);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(commentsPath(stage.getSlug()))
        .then()
        .statusCode(404);
  }

  @Test
  void stageThread_isItsOwn_notTheTrips() {
    Trip trip = dataService.createTrip(team1, user1, "Trip With Threads", Instant.now());
    TripStage stage = dataService.createTripStage(user1, trip, "Threaded Stage");
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(new CommentRequest("Sur l'étape", null))
        .when()
        .post(commentsPath(stage.getSlug()))
        .then()
        .statusCode(201);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/trips/" + trip.getSlug() + "/comments")
        .then()
        .statusCode(200)
        .body("items.size()", equalTo(0));
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(commentsPath(stage.getSlug()))
        .then()
        .statusCode(200)
        .body("items.size()", equalTo(1))
        .body("items[0].content", equalTo("Sur l'étape"));
  }

  @Test
  void getTrip_shouldCarryEachStagesCommentCount_forAMemberOnly() {
    Trip trip = dataService.createTrip(team1, user1, "Counted Stages Trip", Instant.now());
    TripStage commented = dataService.createTripStage(user1, trip, "Commented Stage", 0);
    dataService.createTripStage(user1, trip, "Quiet Stage", 1);
    dataService.createComment(user1, commented, "one");
    dataService.createComment(user3, commented, "two");
    dataService.createComment(user1, trip, "on the trip itself");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/trips/" + trip.getSlug())
        .then()
        .statusCode(200)
        .body("commentCount", equalTo(1))
        .body("stages[0].commentCount", equalTo(2))
        .body("stages[1].commentCount", equalTo(0));

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/trips/" + trip.getSlug())
        .then()
        .statusCode(200)
        .body("stages[0].commentCount", nullValue());
  }
}
