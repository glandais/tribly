package fr.pedalons.api.auth;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * An access token is bound to the domain that issued it: the same address may hold an account on
 * another domain, which a token from the first must not open.
 */
@QuarkusTest
class AccessTokenDomainTest extends AbstractResourceTest {

  private static final String OTHER_HOST = "other.localhost";

  private User sameEmailElsewhere;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Domain other = dataService.createDomain(OTHER_HOST, "Other", "http://" + OTHER_HOST);
    sameEmailElsewhere = dataService.createUser(other, EMAIL1, "Same address, other site");
  }

  @Test
  void aTokenOpensItsAccountOnItsOwnHost() {
    given()
        .header("X-Forwarded-Host", domain.getDomain())
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me")
        .then()
        .statusCode(200);
  }

  @Test
  void aTokenOfOneHostIsRefusedOnAnother() {
    given()
        .header("X-Forwarded-Host", OTHER_HOST)
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/users/me")
        .then()
        // Refused, whichever of the two the valid-but-foreign token earns: what matters is that no
        // account is opened.
        .statusCode(anyOf(is(401), is(403)));
  }

  @Test
  void theOtherAccountStillOpensWithItsOwnToken() {
    given()
        .header("X-Forwarded-Host", OTHER_HOST)
        .auth()
        .oauth2(jwtService.generateAccessToken(sameEmailElsewhere))
        .when()
        .get("/api/users/me")
        .then()
        .statusCode(200);
  }
}
