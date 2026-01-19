package com.tribly.service.garmin;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.platform.Domain;
import com.tribly.domain.user.User;
import com.tribly.service.security.DomainResolver;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GarminJwtServiceTest {

  @Inject GarminJwtService garminJwtService;
  @Inject JWTParser jwtParser;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createVerifiedUser("garmin@example.com", "Garmin User");
  }

  @Test
  void generateAccessToken_shouldReturnValidJwt() throws Exception {
    String token = garminJwtService.generateAccessToken(user);

    assertNotNull(token);
    JsonWebToken jwt = jwtParser.parse(token);
    assertEquals(user.getEmail(), jwt.getSubject());
    assertEquals(user.getEmail(), jwt.getClaim("email"));
    assertEquals(user.getDisplayName(), jwt.getClaim("displayName"));
    assertEquals("garmin", jwt.getClaim("client"));
    assertTrue(jwt.getGroups().contains("user"));
  }

  @Test
  void generateAccessToken_shouldIncludeUserIdClaim() throws Exception {
    String token = garminJwtService.generateAccessToken(user);

    JsonWebToken jwt = jwtParser.parse(token);
    assertNotNull(jwt.getClaim("userId"));
  }

  @Test
  void generateAccessToken_shouldIncludeDomainIdClaim() throws Exception {
    String token = garminJwtService.generateAccessToken(user);

    JsonWebToken jwt = jwtParser.parse(token);
    assertNotNull(jwt.getClaim("domainId"));
  }

  @Test
  void getAccessTokenExpirySeconds_shouldReturnPositiveValue() {
    int expirySeconds = garminJwtService.getAccessTokenExpirySeconds();

    assertTrue(expirySeconds > 0);
  }

  @Test
  void getRefreshTokenExpiryDays_shouldReturnPositiveValue() {
    int expiryDays = garminJwtService.getRefreshTokenExpiryDays();

    assertTrue(expiryDays > 0);
  }
}
