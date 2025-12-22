package com.tribly.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.routes.request.RouteRequest;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.builder.MultiPartSpecBuilder;
import jakarta.inject.Inject;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RouteResourceTest {

  public static final String USERNAME_TEST = "user1";
  private static final String TEST_EMAIL = "user1@example.com";

  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team testTeam;
  private User testUser;
  private Route testRoute;

  final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  protected String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName, userName, "tribly-backend");
  }

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();

    // Create test user
    testUser = dataService.createUser(TEST_EMAIL, "Test User");

    // Create test team with organizer
    testTeam = dataService.createTeamWithVisibility("Test Team", "test-team", Visibility.PUBLIC);
    dataService.addUserToTeam(testUser, testTeam, TeamRole.ORGANIZER);
  }

  @Test
  void listRoutes_shouldReturnRoutes() {
    given()
        .when()
        .get("/api/teams/test-team/routes")
        .then()
        .statusCode(200)
        .body("routes", notNullValue())
        .body("total", greaterThanOrEqualTo(0));
  }

  @Test
  void createRoute_shouldCreateWithGpxFile() {
    File gpxFile = new File("src/test/resources/example.gpx");

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .multiPart(new MultiPartSpecBuilder("Test Route").controlName("name").build())
        .multiPart(new MultiPartSpecBuilder("A test route").controlName("description").build())
        .multiPart(new MultiPartSpecBuilder("GRAVEL").controlName("surfaceType").build())
        .multiPart(new MultiPartSpecBuilder("PUBLIC").controlName("visibility").build())
        .multiPart("gpxFile", gpxFile, "application/gpx+xml")
        .when()
        .post("/api/teams/test-team/routes")
        .then()
        .statusCode(201)
        .body("name", equalTo("Test Route"))
        .body("description", equalTo("A test route"))
        .body("distance", greaterThan(0))
        .body("elevationGain", greaterThanOrEqualTo(0));
  }

  @Test
  void getRoute_shouldReturnRouteDetails() {
    // Create route for testing
    testRoute =
        dataService.createRouteWithVisibility(testTeam, testUser, "Test Route", Visibility.PUBLIC);

    given()
        .when()
        .get("/api/teams/test-team/routes/" + testRoute.getSlug())
        .then()
        .statusCode(200)
        .body("id", equalTo(TsidUtils.toString(testRoute.getId())))
        .body("name", equalTo("Test Route"));
  }

  @Test
  void updateRoute_shouldUpdateMetadata() {
    // Create route for testing
    testRoute =
        dataService.createRouteWithVisibility(
            testTeam, testUser, "Original Name", Visibility.PUBLIC);

    RouteRequest request =
        new RouteRequest("Updated Name", "Updated description", SurfaceType.MTB, Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .contentType("application/json")
        .body(request)
        .when()
        .patch("/api/teams/test-team/routes/" + testRoute.getSlug())
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Name"))
        .body("description", equalTo("Updated description"));
  }

  @Test
  void deleteRoute_shouldSoftDelete() {
    // Create route for testing
    testRoute =
        dataService.createRouteWithVisibility(testTeam, testUser, "To Delete", Visibility.PUBLIC);

    given()
        .auth()
        .oauth2(getAccessToken(USERNAME_TEST))
        .when()
        .delete("/api/teams/test-team/routes/" + testRoute.getSlug())
        .then()
        .statusCode(204);

    // Verify route is no longer accessible
    given().when().get("/api/teams/test-team/routes/" + testRoute.getSlug()).then().statusCode(404);
  }
}
