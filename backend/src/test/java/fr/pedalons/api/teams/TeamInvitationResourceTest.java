package fr.pedalons.api.teams;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.enums.TeamRole;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * HTTP contract of the invitation endpoints: who may call them, and what comes back.
 *
 * <p>The behaviour of the invitations themselves — the state machine, the idempotence, who ends up
 * a member — is asserted in {@code TeamInvitationServiceTest}, where it can be read without a layer
 * of RestAssured in the way.
 *
 * <p>Fixture reminder: on {@code team1}, user1 is ADMIN, user2 ORGANIZER, user3 MEMBER; user4 and
 * user5 are not members. {@code team1.addMemberAllowed} is true.
 */
@QuarkusTest
class TeamInvitationResourceTest extends AbstractResourceTest {

  @Inject MockMailbox mailbox;

  @BeforeEach
  void setUpTest() {
    setUp();
    mailbox.clear();
  }

  private String invitationsPath() {
    return "/api/teams/" + team1Slug + "/invitations";
  }

  @Test
  void invite_asAdmin_shouldCreate() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(Map.of("email", "newcomer@example.com", "role", TeamRole.MEMBER.name()))
        .when()
        .post(invitationsPath())
        .then()
        .statusCode(201)
        .body("email", equalTo("newcomer@example.com"))
        .body("status", equalTo("PENDING"));
  }

  @Test
  void invite_asOrganizer_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType(ContentType.JSON)
        .body(Map.of("email", "newcomer@example.com"))
        .when()
        .post(invitationsPath())
        .then()
        .statusCode(403);
  }

  @Test
  void invite_asPlainMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType(ContentType.JSON)
        .body(Map.of("email", "newcomer@example.com"))
        .when()
        .post(invitationsPath())
        .then()
        .statusCode(403);
  }

  @Test
  void invite_withoutAuth_shouldReturn401() {
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("email", "newcomer@example.com"))
        .when()
        .post(invitationsPath())
        .then()
        .statusCode(401);
  }

  @Test
  void invite_withAMalformedAddress_shouldReturn400() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(Map.of("email", "not-an-address"))
        .when()
        .post(invitationsPath())
        .then()
        .statusCode(400);
  }

  @Test
  void invite_onAnUnknownTeam_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .body(Map.of("email", "newcomer@example.com"))
        .when()
        .post("/api/teams/no-such-team/invitations")
        .then()
        .statusCode(404);
  }

  /**
   * The listing is gated on {@code USER_TEAM/CREATE}, not on {@code LIST}. {@code LIST} now means
   * "may read the member directory", which organisers hold and members can be given — but who has
   * been invited and has not yet answered stays with the people who did the inviting.
   */
  @Test
  void listInvitations_shouldBeAdminOnly() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get(invitationsPath())
        .then()
        .statusCode(200);

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .get(invitationsPath())
        .then()
        .statusCode(403);
  }

  @Test
  void revoke_anUnknownInvitation_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete(invitationsPath() + "/0h4a8xzk8jv80")
        .then()
        .statusCode(404);
  }

  // ==================== The invitee's side ====================

  @Test
  void preview_withAnUnknownToken_shouldReturn400WithItsCode() {
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("token", "not-a-real-token"))
        .when()
        .post("/api/invitations/preview")
        .then()
        .statusCode(400)
        .body("code", equalTo("TEAM_INVITE_INVALID"));
  }

  /** Public on purpose — the page has to be able to explain itself before anyone signs in. */
  @Test
  void preview_shouldNotRequireASession() {
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("token", "not-a-real-token"))
        .when()
        .post("/api/invitations/preview")
        .then()
        .statusCode(400);
  }

  /**
   * Accepting, unlike previewing, is never public: it is a consent, and a consent attaches to an
   * account. A public accept returning a session would turn the invitation link into a login
   * credential.
   */
  @Test
  void accept_withoutAuth_shouldReturn401() {
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("token", "not-a-real-token"))
        .when()
        .post("/api/invitations/accept")
        .then()
        .statusCode(401);
  }

  @Test
  void listMyInvitations_shouldRequireASession() {
    given().when().get("/api/users/me/invitations").then().statusCode(401);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/users/me/invitations")
        .then()
        .statusCode(200)
        .body("invitations.size()", equalTo(0));
  }
}
