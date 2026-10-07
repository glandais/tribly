package fr.pedalons.api.rides;

import static fr.pedalons.util.WallTimes.wall;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.rides.request.GroupRequest;
import fr.pedalons.dto.rides.request.RideRequest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A group leader is a claim about a person, published to everyone who can read the ride. These
 * tests are mostly about who may be named, and about the fallback that must never happen.
 */
@QuarkusTest
class RideGroupLeaderTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private RideRequest rideWithLeader(String name, String leaderId) {
    return new RideRequest(
        name,
        MediaDto.builder().markdown("Sortie du dimanche").build(),
        wall(Instant.parse("2026-09-06T07:30:00Z")),
        Status.PUBLISHED,
        Visibility.TEAM,
        null,
        null,
        null,
        null,
        List.of(
            GroupRequest.builder()
                .name("Groupe rapide")
                .time(LocalTime.of(7, 30))
                .leaderId(leaderId)
                .build()),
        null);
  }

  private io.restassured.response.Response create(RideRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/rides");
  }

  @Test
  void createRide_withAMemberAsLeader_exposesThemOnTheGroup() {
    String leaderId = TsidUtils.toString(user3.getId());

    create(rideWithLeader("Sortie avec meneur", leaderId))
        .then()
        .statusCode(201)
        .body("groups[0].leader.id", Matchers.equalTo(leaderId))
        .body("groups[0].leader.displayName", Matchers.equalTo(user3.getDisplayName()));
  }

  @Test
  void createRide_withoutALeader_leavesItNullRatherThanFallingBackOnTheCreator() {
    // The whole reason this column exists: created_by holds the ride's creator and is identical on
    // every group, so any fallback would attribute the group to someone who never led it.
    create(rideWithLeader("Sortie sans meneur", null))
        .then()
        .statusCode(201)
        .body("groups[0].leader", Matchers.nullValue());
  }

  @Test
  void createRide_withANonMemberAsLeader_isRejected() {
    // user5 belongs to no team. Without the membership check, any user id in the domain could be
    // published as the leader of a ride they never agreed to lead.
    create(rideWithLeader("Sortie meneur étranger", TsidUtils.toString(user5.getId())))
        .then()
        .statusCode(400)
        .body("code", Matchers.equalTo("RIDE_GROUP_LEADER_NOT_MEMBER"));
  }

  @Test
  void createRide_withAMemberOfAnotherTeamAsLeader_isRejected() {
    // user4 is in the fixture but in neither team; naming them on team1's ride must fail for the
    // same reason, and must not leak whether the id exists.
    create(rideWithLeader("Sortie meneur autre équipe", TsidUtils.toString(user4.getId())))
        .then()
        .statusCode(400)
        .body("code", Matchers.equalTo("RIDE_GROUP_LEADER_NOT_MEMBER"));
  }

  @Test
  void updateRide_canDesignateThenClearTheLeader() {
    String leaderId = TsidUtils.toString(user2.getId());
    String slug =
        create(rideWithLeader("Sortie modifiable", null))
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(rideWithLeader("Sortie modifiable", leaderId))
        .when()
        .put("/api/teams/" + team1Slug + "/rides/" + slug)
        .then()
        .statusCode(200)
        .body("groups[0].leader.id", Matchers.equalTo(leaderId));

    // Clearing is a real operation, not an omission: sending null must remove the designation.
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(rideWithLeader("Sortie modifiable", null))
        .when()
        .put("/api/teams/" + team1Slug + "/rides/" + slug)
        .then()
        .statusCode(200)
        .body("groups[0].leader", Matchers.nullValue());
  }

  @Test
  void updateRide_keepsALeaderWhoHasSinceLeftTheTeam() {
    // The client sends the whole ride back on every save, leader included. Re-checking a leader
    // the group already has would block publishing, cancelling or editing the ride as soon as they
    // left the team.
    String leaderId = TsidUtils.toString(user3.getId());
    io.restassured.response.Response created =
        create(rideWithLeader("Sortie meneur parti", leaderId));
    created.then().statusCode(201);
    String slug = created.path("slug");
    String groupId = created.path("groups[0].id");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .when()
        .post("/api/teams/" + team1Slug + "/members/leave")
        .then()
        .statusCode(204);

    RideRequest cancelled =
        new RideRequest(
            "Sortie meneur parti",
            MediaDto.builder().markdown("Sortie du dimanche").build(),
            wall(Instant.parse("2026-09-06T07:30:00Z")),
            Status.CANCELLED,
            Visibility.TEAM,
            null,
            null,
            null,
            null,
            List.of(
                GroupRequest.builder()
                    .id(groupId)
                    .name("Groupe rapide")
                    .time(LocalTime.of(7, 30))
                    .leaderId(leaderId)
                    .build()),
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(cancelled)
        .when()
        .put("/api/teams/" + team1Slug + "/rides/" + slug)
        .then()
        .statusCode(200)
        .body("status", Matchers.equalTo("CANCELLED"))
        .body("groups[0].leader.id", Matchers.equalTo(leaderId));
  }

  @Test
  void groupLeader_isNotTheRideCreator() {
    String leaderId = TsidUtils.toString(user3.getId());
    String creatorId = TsidUtils.toString(user1.getId());
    assertNotEquals(leaderId, creatorId, "the fixture must distinguish the two roles");

    create(rideWithLeader("Sortie distincte", leaderId))
        .then()
        .statusCode(201)
        .body("groups[0].leader.id", Matchers.not(Matchers.equalTo(creatorId)));
  }
}
