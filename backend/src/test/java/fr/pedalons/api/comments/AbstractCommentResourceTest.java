package fr.pedalons.api.comments;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.comments.request.CommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractCommentResourceTest extends AbstractResourceTest {

  protected String entitySlug;
  protected String privateEntitySlug;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    entitySlug = createEntity(team1, user1);
    privateEntitySlug = createEntity(team2, user1);
  }

  /** Returns the entity type path segment (e.g., "posts", "rides", "routes", "trips") */
  protected abstract String getEntityPath();

  /** Creates an entity and returns its slug */
  protected abstract String createEntity(Team team, User creator);

  private String getCommentsPath(String teamSlug, String entitySlug) {
    return "/api/teams/" + teamSlug + "/" + getEntityPath() + "/" + entitySlug + "/comments";
  }

  private CommentRequest createCommentRequest(String content) {
    return new CommentRequest(content, null);
  }

  private String createTestComment(String teamSlug, String entitySlug, String user) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(createCommentRequest("Test comment"))
        .when()
        .post(getCommentsPath(teamSlug, entitySlug))
        .then()
        .statusCode(201)
        .extract()
        .path("id");
  }

  // ==================== List Comments ====================

  @Test
  void listComments_asMember_shouldSucceed() {
    createTestComment(team1Slug, entitySlug, USER1);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(getCommentsPath(team1Slug, entitySlug))
        .then()
        .statusCode(200)
        .body("items", notNullValue())
        .body("items.size()", greaterThanOrEqualTo(1));
  }

  @Test
  void listComments_withoutAuth_shouldReturn401() {
    given().when().get(getCommentsPath(team1Slug, entitySlug)).then().statusCode(401);
  }

  @Test
  void listComments_asNonMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get(getCommentsPath(team1Slug, entitySlug))
        .then()
        .statusCode(403);
  }

  @Test
  void listComments_onPrivateTeam_asMember_shouldSucceed() {
    createTestComment(team2Slug, privateEntitySlug, USER1);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(getCommentsPath(team2Slug, privateEntitySlug))
        .then()
        .statusCode(200)
        .body("items", notNullValue());
  }

  @Test
  void listComments_onPrivateTeam_asNonMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get(getCommentsPath(team2Slug, privateEntitySlug))
        .then()
        .statusCode(403);
  }

  // ==================== Create Comment ====================

  @Test
  void createComment_asMember_shouldSucceed() {
    CommentRequest request = createCommentRequest("New comment");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(request)
        .when()
        .post(getCommentsPath(team1Slug, entitySlug))
        .then()
        .statusCode(201)
        .body("content", equalTo("New comment"))
        .body("id", notNullValue())
        .body("author", notNullValue());
  }

  @Test
  void createComment_withoutAuth_shouldReturn401() {
    CommentRequest request = createCommentRequest("Unauth comment");

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post(getCommentsPath(team1Slug, entitySlug))
        .then()
        .statusCode(401);
  }

  @Test
  void createComment_asNonMember_shouldReturn403() {
    CommentRequest request = createCommentRequest("NonMember comment");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .contentType("application/json")
        .body(request)
        .when()
        .post(getCommentsPath(team1Slug, entitySlug))
        .then()
        .statusCode(403);
  }

  @Test
  void createComment_withEmptyContent_shouldReturn400() {
    CommentRequest request = new CommentRequest("", null);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(request)
        .when()
        .post(getCommentsPath(team1Slug, entitySlug))
        .then()
        .statusCode(400);
  }

  @Test
  void createComment_asReply_shouldSucceed() {
    String parentCommentId = createTestComment(team1Slug, entitySlug, USER1);
    CommentRequest request = new CommentRequest("Reply comment", parentCommentId);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(request)
        .when()
        .post(getCommentsPath(team1Slug, entitySlug))
        .then()
        .statusCode(201)
        .body("content", equalTo("Reply comment"))
        .body("parentId", equalTo(parentCommentId));
  }

  // ==================== Delete Comment ====================

  @Test
  void deleteComment_asCreator_shouldSucceed() {
    String commentId = createTestComment(team1Slug, entitySlug, USER3);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .delete(getCommentsPath(team1Slug, entitySlug) + "/" + commentId)
        .then()
        .statusCode(204);
  }

  @Test
  void deleteComment_asAdmin_shouldSucceed() {
    String commentId = createTestComment(team1Slug, entitySlug, USER3);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete(getCommentsPath(team1Slug, entitySlug) + "/" + commentId)
        .then()
        .statusCode(204);
  }

  @Test
  void deleteComment_asNonCreatorMember_shouldReturn403() {
    String commentId = createTestComment(team1Slug, entitySlug, USER1);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .delete(getCommentsPath(team1Slug, entitySlug) + "/" + commentId)
        .then()
        .statusCode(403);
  }

  @Test
  void deleteComment_withoutAuth_shouldReturn401() {
    String commentId = createTestComment(team1Slug, entitySlug, USER1);

    given()
        .when()
        .delete(getCommentsPath(team1Slug, entitySlug) + "/" + commentId)
        .then()
        .statusCode(401);
  }

  @Test
  void deleteComment_nonexistent_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete(getCommentsPath(team1Slug, entitySlug) + "/" + TsidUtils.toString(999999L))
        .then()
        .statusCode(403);
  }
}
