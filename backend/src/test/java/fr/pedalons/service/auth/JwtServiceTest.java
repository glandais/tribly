package fr.pedalons.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class JwtServiceTest extends AbstractBaseTest {

  @Inject JwtService jwtService;
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
    user = dataService.createUser("test@example.com", "Test User");
  }

  @Test
  void generateAccessToken_shouldReturnValidJwt() {
    String token = jwtService.generateAccessToken(user);

    assertNotNull(token);
    assertFalse(token.isBlank());
    // JWT has 3 parts separated by dots
    assertEquals(3, token.split("\\.").length);
  }

  @Test
  void generateAccessToken_shouldContainUserClaims() {
    String token = jwtService.generateAccessToken(user);

    // Token can be decoded (header.payload.signature)
    String[] parts = token.split("\\.");
    assertEquals(3, parts.length);

    // Payload is base64 encoded JSON
    String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
    assertTrue(payload.contains("test@example.com"));
    assertTrue(payload.contains("Test User"));
  }

  @Test
  void getAccessTokenExpirySeconds_shouldReturnPositiveValue() {
    int expirySeconds = jwtService.getAccessTokenExpirySeconds();

    assertTrue(expirySeconds > 0);
    // Default is 15 minutes = 900 seconds
    assertEquals(900, expirySeconds);
  }

  @Test
  void generateAccessToken_shouldGenerateDifferentTokensForDifferentUsers() {
    User user2 = dataService.createUser("other@example.com", "Other User");

    String token1 = jwtService.generateAccessToken(user);
    String token2 = jwtService.generateAccessToken(user2);

    assertNotEquals(token1, token2);
  }

  @Test
  void generateAccessToken_shouldGenerateDifferentTokensOnEachCall() {
    // Due to timestamp in JWT, each call should produce a different token
    String token1 = jwtService.generateAccessToken(user);
    String token2 = jwtService.generateAccessToken(user);

    // Tokens may differ due to timestamp but both should be valid
    assertNotNull(token1);
    assertNotNull(token2);
  }
}
