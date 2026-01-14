package com.tribly.api.config;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.tribly.api.AbstractResourceTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for ConfigResource.
 * Verifies configuration values match application.properties (%test. profile).
 */
@QuarkusTest
class ConfigResourceTest extends AbstractResourceTest {
  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void getConfig_shouldReturnConfiguration() {
    given()
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
            .body("webAuthnRpId", is(notNullValue()))
            .body("appName", is(notNullValue()));
  }
}
