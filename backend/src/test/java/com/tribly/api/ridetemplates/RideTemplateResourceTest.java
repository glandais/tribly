package com.tribly.api.ridetemplates;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import com.tribly.dto.ridetemplates.request.RideTemplateGroupRequest;
import com.tribly.dto.ridetemplates.request.RideTemplateRequest;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideTemplateResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private RideTemplateRequest createValidRequest(String name) {
    return new RideTemplateRequest(
        name,
        "Template description in **markdown**",
        Visibility.TEAM,
        Status.PUBLISHED,
        List.of(
            RideTemplateGroupRequest.builder()
                .name("Group A")
                .averageSpeed(25)
                .maxParticipants(20)
                .build(),
            RideTemplateGroupRequest.builder()
                .name("Group B")
                .averageSpeed(30)
                .maxParticipants(15)
                .build()));
  }

  // ==================== List Templates Tests ====================

  @Test
  void listTemplates_asAdmin_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(200)
        .body("templates", is(notNullValue()))
        .body("total", greaterThanOrEqualTo(0))
        .body("page", equalTo(0))
        .body("size", equalTo(20));
  }

  @Test
  void listTemplates_asOrganizer_shouldSucceed() {
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(200)
        .body("templates", is(notNullValue()));
  }

  @Test
  void listTemplates_asMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(403);
  }

  @Test
  void listTemplates_withoutAuth_shouldReturn401() {
    given().when().get("/api/teams/" + team1Slug + "/ride-templates").then().statusCode(401);
  }

  @Test
  void listTemplates_asNonMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(403);
  }

  @Test
  void listTemplates_toNonexistentTeam_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/nonexistent-team/ride-templates")
        .then()
        .statusCode(403);
  }

  @Test
  void listTemplates_withSearch_shouldSucceed() {
    // Create a template first
    RideTemplateRequest request = createValidRequest("Searchable Template");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(201);

    // Search for it
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("search", "Searchable")
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(200)
        .body("templates", is(notNullValue()));
  }

  // ==================== Create Template Tests ====================

  @Test
  void createTemplate_asAdmin_shouldSucceed() {
    RideTemplateRequest request = createValidRequest("Admin Template");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(201)
        .body("slug", notNullValue())
        .body("name", equalTo("Admin Template"))
        .body("markdown", equalTo("Template description in **markdown**"))
        .body("visibility", equalTo("TEAM"))
        .body("status", equalTo("PUBLISHED"))
        .body("groups", hasSize(2))
        .body("groups[0].name", equalTo("Group A"))
        .body("groups[0].averageSpeed", equalTo(25))
        .body("groups[0].maxParticipants", equalTo(20))
        .body("groups[1].name", equalTo("Group B"));
  }

  @Test
  void createTemplate_asOrganizer_shouldSucceed() {
    RideTemplateRequest request = createValidRequest("Organizer Template");

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(201)
        .body("name", equalTo("Organizer Template"));
  }

  @Test
  void createTemplate_asMember_shouldBeDenied() {
    RideTemplateRequest request = createValidRequest("Member Template");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(403);
  }

  @Test
  void createTemplate_withoutAuth_shouldReturn401() {
    RideTemplateRequest request = createValidRequest("Unauth Template");

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(401);
  }

  @Test
  void createTemplate_asNonMember_shouldReturn403() {
    RideTemplateRequest request = createValidRequest("NonMember Template");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(403);
  }

  @Test
  void createTemplate_toNonexistentTeam_shouldReturn404() {
    RideTemplateRequest request = createValidRequest("Test Template");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/nonexistent-team/ride-templates")
        .then()
        .statusCode(404);
  }

  @Test
  void createTemplate_withEmptyGroups_shouldSucceed() {
    RideTemplateRequest request =
        new RideTemplateRequest(
            "No Groups Template", null, Visibility.TEAM, Status.DRAFT, List.of());

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/ride-templates")
        .then()
        .statusCode(201)
        .body("name", equalTo("No Groups Template"))
        .body("groups", hasSize(0));
  }

  // ==================== Get Template Tests ====================

  @Test
  void getTemplate_asAdmin_shouldSucceed() {
    RideTemplateRequest request = createValidRequest("Get Test Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(request)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(200)
        .body("slug", equalTo(templateSlug))
        .body("name", equalTo("Get Test Template"));
  }

  @Test
  void getTemplate_asOrganizer_shouldSucceed() {
    RideTemplateRequest request = createValidRequest("Organizer Get Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(request)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Organizer Get Template"));
  }

  @Test
  void getTemplate_asMember_shouldReturn403() {
    RideTemplateRequest request = createValidRequest("Member Get Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(request)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(403);
  }

  @Test
  void getTemplate_nonexistent_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates/nonexistent-slug")
        .then()
        .statusCode(404);
  }

  @Test
  void getTemplate_asNonMember_shouldReturn403() {
    RideTemplateRequest request = createValidRequest("NonMember Get Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(request)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(403);
  }

  // ==================== Update Template Tests ====================

  @Test
  void updateTemplate_asAdmin_shouldSucceed() {
    RideTemplateRequest createRequest = createValidRequest("Original Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(createRequest)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    RideTemplateRequest updateRequest =
        new RideTemplateRequest(
            "Updated Template",
            "New description",
            Visibility.PUBLIC,
            Status.DRAFT,
            List.of(
                RideTemplateGroupRequest.builder()
                    .name("Updated Group")
                    .averageSpeed(35)
                    .maxParticipants(10)
                    .build()));

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(updateRequest)
        .when()
        .put("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Template"))
        .body("markdown", equalTo("New description"))
        .body("visibility", equalTo("PUBLIC"))
        .body("status", equalTo("DRAFT"))
        .body("groups", hasSize(1))
        .body("groups[0].name", equalTo("Updated Group"));
  }

  @Test
  void updateTemplate_asOrganizer_shouldSucceed() {
    RideTemplateRequest createRequest = createValidRequest("Organizer Update Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(createRequest)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    RideTemplateRequest updateRequest =
        new RideTemplateRequest(
            "Updated by Organizer", null, Visibility.TEAM, Status.PUBLISHED, List.of());

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(updateRequest)
        .when()
        .put("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated by Organizer"));
  }

  @Test
  void updateTemplate_asMember_shouldBeDenied() {
    RideTemplateRequest createRequest = createValidRequest("Member Update Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(createRequest)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    RideTemplateRequest updateRequest =
        new RideTemplateRequest(
            "Hacked by Member", null, Visibility.TEAM, Status.PUBLISHED, List.of());

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(updateRequest)
        .when()
        .put("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(403);
  }

  @Test
  void updateTemplate_nonexistent_shouldReturn404() {
    RideTemplateRequest updateRequest =
        new RideTemplateRequest("Nonexistent", null, Visibility.TEAM, Status.PUBLISHED, List.of());

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(updateRequest)
        .when()
        .put("/api/teams/" + team1Slug + "/ride-templates/nonexistent-slug")
        .then()
        .statusCode(404);
  }

  // ==================== Delete Template Tests ====================

  @Test
  void deleteTemplate_asAdmin_shouldSucceed() {
    RideTemplateRequest createRequest = createValidRequest("Delete Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(createRequest)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(204);

    // Verify template is no longer accessible
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(404);
  }

  @Test
  void deleteTemplate_asOrganizer_shouldSucceed() {
    RideTemplateRequest createRequest = createValidRequest("Organizer Delete Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(createRequest)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .when()
        .delete("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(204);
  }

  @Test
  void deleteTemplate_asMember_shouldBeDenied() {
    RideTemplateRequest createRequest = createValidRequest("Member Delete Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(createRequest)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .delete("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(403);
  }

  @Test
  void deleteTemplate_asNonMember_shouldReturn403() {
    RideTemplateRequest createRequest = createValidRequest("NonMember Delete Template");

    String templateSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(createRequest)
            .when()
            .post("/api/teams/" + team1Slug + "/ride-templates")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .delete("/api/teams/" + team1Slug + "/ride-templates/" + templateSlug)
        .then()
        .statusCode(403);
  }

  @Test
  void deleteTemplate_nonexistent_shouldReturn404() {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete("/api/teams/" + team1Slug + "/ride-templates/nonexistent-slug")
        .then()
        .statusCode(404);
  }
}
