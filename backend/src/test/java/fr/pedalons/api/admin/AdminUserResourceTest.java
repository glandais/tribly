package fr.pedalons.api.admin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.user.User;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminUserResourceTest extends AbstractResourceTest {

  private User platformAdmin;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    platformAdmin = dataService.createPlatformAdminUser("admin@example.com", "Platform Admin");
  }

  private void assignRole(User target, String roleJson, int expectedStatus) {
    given()
        .auth()
        .oauth2(jwtService.generateAccessToken(platformAdmin))
        .contentType("application/json")
        .body("{\"role\": " + roleJson + "}")
        .when()
        .put("/api/admin/users/" + TsidUtils.toString(target.getId()) + "/platform-role")
        .then()
        .statusCode(expectedStatus);
  }

  @Test
  void anAdminCannotRemoveTheirOwnRole() {
    // The UI disables the button: the server is the rule, not the button.
    assignRole(platformAdmin, "null", 403);

    given()
        .auth()
        .oauth2(jwtService.generateAccessToken(platformAdmin))
        .when()
        .get("/api/admin/users/" + TsidUtils.toString(platformAdmin.getId()))
        .then()
        .statusCode(200)
        .body("platformRole", equalTo("PLATFORM_ADMIN"));
  }

  @Test
  void anAdminGrantsAndRemovesSomeoneElsesRole() {
    assignRole(user1, "\"PLATFORM_ADMIN\"", 200);
    assignRole(user1, "null", 200);
  }
}
