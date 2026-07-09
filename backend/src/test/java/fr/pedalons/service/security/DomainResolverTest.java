package fr.pedalons.service.security;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.DomainAlias;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DomainResolver}: the direct accessor / {@link ResolvedSite} behaviour (via the
 * test setters) and the real host-based resolution path (via HTTP requests to {@code /api/config}
 * with an {@code X-Forwarded-Host} header).
 */
@QuarkusTest
class DomainResolverTest extends AbstractResourceTest {

  private static final String ALIAS_HOST = "myteam.localhost";
  private static final String ALIAS_NAME = "My Team Site";
  private static final String ALIAS_BASE_URL = "http://myteam.localhost";

  @Inject DomainResolver domainResolver;

  private DomainAlias alias;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    alias = dataService.createDomainAlias(ALIAS_HOST, domain, team1, ALIAS_NAME, ALIAS_BASE_URL);
  }

  // --- Direct accessor behaviour (test setters, no HTTP) ---

  @Test
  void setDomainForTest_resolvesRegularDomainWithoutPinnedTeam() {
    domainResolver.setDomainForTest(domain);

    assertEquals(domain.getId(), domainResolver.getDomainId());
    assertEquals(domain.getDomain(), domainResolver.getEffectiveHost());
    assertEquals(domain.getBaseUrl(), domainResolver.getEffectiveBaseUrl());
    assertEquals(domain.getName(), domainResolver.getEffectiveName());
    assertNull(domainResolver.getPinnedTeamIdNullable());

    ResolvedSite site = domainResolver.getResolvedSite();
    assertFalse(site.isAlias());
    assertFalse(site.singleTeam());
  }

  @Test
  void setAliasForTest_keepsParentDomainButExposesAliasBrandingAndPinnedTeam() {
    domainResolver.setAliasForTest(alias);

    // getDomainId() must stay the PARENT domain so all existing queries are unchanged.
    assertEquals(domain.getId(), domainResolver.getDomainId());

    // Effective branding comes from the alias.
    assertEquals(ALIAS_HOST, domainResolver.getEffectiveHost());
    assertEquals(ALIAS_BASE_URL, domainResolver.getEffectiveBaseUrl());
    assertEquals(ALIAS_NAME, domainResolver.getEffectiveName());

    // Pinned team is exposed and single-team mode is on.
    assertEquals(team1.getId(), domainResolver.getPinnedTeamIdNullable());

    ResolvedSite site = domainResolver.getResolvedSite();
    assertTrue(site.isAlias());
    assertTrue(site.singleTeam());
    assertEquals(team1.getId(), site.pinnedTeamId());
    assertEquals(domain.getId(), site.domain().getId());
  }

  // --- Real host resolution via HTTP (exercises DomainResolver.init()) ---

  @Test
  void request_onAliasHost_resolvesToAliasSingleTeamConfig() {
    given()
        .header("X-Forwarded-Host", ALIAS_HOST)
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
        .body("webAuthnRpId", is(ALIAS_HOST))
        .body("appName", is(ALIAS_NAME))
        .body("singleTeam", is(true))
        .body("pinnedTeamSlug", is(team1Slug));
  }

  @Test
  void request_onParentDomainHost_resolvesToRegularConfig() {
    given()
        .header("X-Forwarded-Host", domain.getDomain())
        .when()
        .get("/api/config")
        .then()
        .statusCode(200)
        .body("webAuthnRpId", is(domain.getDomain()))
        .body("singleTeam", is(false))
        .body("pinnedTeamSlug", is(nullValue()));
  }

  @Test
  void request_onInactiveAliasHost_isNotResolved() {
    dataService.setDomainAliasActive(alias, false);

    given().header("X-Forwarded-Host", ALIAS_HOST).when().get("/api/config").then().statusCode(404);
  }

  @Test
  void request_onUnknownHost_returnsDomainNotFound() {
    given()
        .header("X-Forwarded-Host", "unknown.localhost")
        .when()
        .get("/api/config")
        .then()
        .statusCode(404);
  }
}
