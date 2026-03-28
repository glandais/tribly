package fr.pedalons.api.posts;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.common.request.SlugChangeRequest;
import fr.pedalons.dto.posts.request.PostRequest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PostResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private PostRequest createPostRequest(String name) {
    return new PostRequest(
        name,
        MediaDto.builder().markdown("Post content").build(),
        Instant.now().plus(7, ChronoUnit.DAYS),
        Status.PUBLISHED,
        Visibility.PUBLIC,
        null);
  }

  private String createTestPost(String name) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(createPostRequest(name))
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then()
        .statusCode(201)
        .extract()
        .path("slug");
  }

  // ==================== Create Post Tests ====================

  @Test
  void createPost_asAdmin_shouldSucceed() {
    PostRequest request = createPostRequest("Admin Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then()
        .statusCode(201)
        .body("name", equalTo("Admin Post"))
        .body("media.markdown", equalTo("Post content"))
        .body("status", equalTo("PUBLISHED"))
        .body("visibility", equalTo("PUBLIC"));
  }

  @Test
  void createPost_asOrganizer_shouldSucceed() {
    PostRequest request = createPostRequest("Organizer Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then()
        .statusCode(201)
        .body("name", equalTo("Organizer Post"));
  }

  @Test
  void createPost_asMember_shouldBeDenied() {
    PostRequest request = createPostRequest("Member Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then()
        .statusCode(403);
  }

  @Test
  void createPost_withoutAuth_shouldReturn401() {
    PostRequest request = createPostRequest("Unauth Post");

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then()
        .statusCode(401);
  }

  @Test
  void createPost_asNonMember_shouldReturn403() {
    PostRequest request = createPostRequest("NonMember Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then()
        .statusCode(403);
  }

  @Test
  void createPost_toNonexistentTeam_shouldReturn404() {
    PostRequest request = createPostRequest("Test Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/nonexistent-team/posts")
        .then()
        .statusCode(404);
  }

  @Test
  void createPost_withPublishAt_shouldSucceed() {
    PostRequest request =
        new PostRequest(
            "Scheduled Post",
            MediaDto.builder().markdown("Scheduled content").build(),
            Instant.now().plus(14, ChronoUnit.DAYS),
            Status.DRAFT,
            Visibility.PUBLIC,
            Instant.now().plus(7, ChronoUnit.DAYS));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then()
        .statusCode(201)
        .body("name", equalTo("Scheduled Post"))
        .body("publishAt", notNullValue());
  }

  // ==================== Get Post Tests ====================

  @Test
  void getPost_withoutAuth_shouldSucceed() {
    String postSlug = createTestPost("Public Post");

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(200)
        .body("slug", equalTo(postSlug))
        .body("name", equalTo("Public Post"));
  }

  @Test
  void getPost_asMember_shouldSucceed() {
    String postSlug = createTestPost("Member Get Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Member Get Post"));
  }

  @Test
  void getPost_asNonMember_shouldSucceed() {
    String postSlug = createTestPost("NonMember Get Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("NonMember Get Post"));
  }

  @Test
  void getPost_nonexistent_shouldReturn404() {
    given()
        .when()
        .get("/api/teams/" + team1Slug + "/posts/nonexistent-post")
        .then()
        .statusCode(404);
  }

  @Test
  void getPost_toNonexistentTeam_shouldReturn404() {
    given().when().get("/api/teams/nonexistent-team/posts/some-post").then().statusCode(404);
  }

  // ==================== Update Post Tests ====================

  @Test
  void updatePost_asAdmin_shouldSucceed() {
    String postSlug = createTestPost("Original Post");

    PostRequest request =
        new PostRequest(
            "Updated Post",
            MediaDto.builder().markdown("Updated content").build(),
            Instant.now().plus(14, ChronoUnit.DAYS),
            Status.PUBLISHED,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Post"))
        .body("media.markdown", equalTo("Updated content"))
        .body("status", equalTo("PUBLISHED"));
  }

  @Test
  void updatePost_asOrganizer_shouldSucceed() {
    String postSlug = createTestPost("Organizer Update Post");

    PostRequest request =
        new PostRequest(
            "Updated by Organizer",
            MediaDto.builder().markdown("Organizer updated").build(),
            Instant.now().plus(7, ChronoUnit.DAYS),
            Status.DRAFT,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated by Organizer"));
  }

  @Test
  void updatePost_asMember_shouldBeDenied() {
    String postSlug = createTestPost("Member Update Post");

    PostRequest request =
        new PostRequest(
            "Hacked by Member",
            MediaDto.builder().build(),
            Instant.now().plus(7, ChronoUnit.DAYS),
            Status.DRAFT,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(403);
  }

  @Test
  void updatePost_withoutAuth_shouldReturn401() {
    String postSlug = createTestPost("Unauth Update Post");

    PostRequest request =
        new PostRequest(
            "Unauthorized Update",
            MediaDto.builder().build(),
            Instant.now().plus(7, ChronoUnit.DAYS),
            Status.DRAFT,
            Visibility.PUBLIC,
            null);

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(401);
  }

  @Test
  void updatePost_asNonMember_shouldReturn403() {
    String postSlug = createTestPost("NonMember Update Post");

    PostRequest request =
        new PostRequest(
            "Hacked by NonMember",
            MediaDto.builder().build(),
            Instant.now().plus(7, ChronoUnit.DAYS),
            Status.DRAFT,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(403);
  }

  @Test
  void updatePost_nonexistent_shouldReturn404() {
    PostRequest request =
        new PostRequest(
            "Nonexistent",
            MediaDto.builder().build(),
            Instant.now().plus(7, ChronoUnit.DAYS),
            Status.DRAFT,
            Visibility.PUBLIC,
            null);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/posts/nonexistent-post")
        .then()
        .statusCode(404);
  }

  // ==================== Delete Post Tests ====================

  @Test
  void deletePost_asAdmin_shouldSucceed() {
    String postSlug = createTestPost("To Delete");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(204);

    // Verify post is no longer accessible
    given().when().get("/api/teams/" + team1Slug + "/posts/" + postSlug).then().statusCode(404);
  }

  @Test
  void deletePost_asOrganizer_shouldSucceed() {
    String postSlug = createTestPost("Organizer Delete");

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .delete("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(204);
  }

  @Test
  void deletePost_asMember_shouldBeDenied() {
    String postSlug = createTestPost("Member Delete");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .delete("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(403);
  }

  @Test
  void deletePost_withoutAuth_shouldReturn401() {
    String postSlug = createTestPost("Unauth Delete");

    given().when().delete("/api/teams/" + team1Slug + "/posts/" + postSlug).then().statusCode(401);
  }

  @Test
  void deletePost_asNonMember_shouldReturn403() {
    String postSlug = createTestPost("NonMember Delete");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .delete("/api/teams/" + team1Slug + "/posts/" + postSlug)
        .then()
        .statusCode(403);
  }

  @Test
  void deletePost_nonexistent_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + team1Slug + "/posts/nonexistent-post")
        .then()
        .statusCode(404);
  }

  // ==================== Change Slug Tests ====================

  @Test
  void changeSlug_asAdmin_shouldSucceed() {
    String postSlug = createTestPost("Slug Change Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(new SlugChangeRequest("new-post-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/posts/" + postSlug + "/slug")
        .then()
        .statusCode(200)
        .body("slug", equalTo("new-post-slug"));
  }

  @Test
  void changeSlug_asOrganizer_shouldSucceed() {
    String postSlug = createTestPost("Organizer Slug Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(new SlugChangeRequest("organizer-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/posts/" + postSlug + "/slug")
        .then()
        .statusCode(200)
        .body("slug", equalTo("organizer-slug"));
  }

  @Test
  void changeSlug_asMember_shouldReturn403() {
    String postSlug = createTestPost("Member Slug Post");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(new SlugChangeRequest("hacked-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/posts/" + postSlug + "/slug")
        .then()
        .statusCode(403);
  }

  @Test
  void changeSlug_withoutAuth_shouldReturn401() {
    String postSlug = createTestPost("Unauth Slug Post");

    given()
        .contentType("application/json")
        .body(new SlugChangeRequest("unauth-slug"))
        .when()
        .patch("/api/teams/" + team1Slug + "/posts/" + postSlug + "/slug")
        .then()
        .statusCode(401);
  }
}
