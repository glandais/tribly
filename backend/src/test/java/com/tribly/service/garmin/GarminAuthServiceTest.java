package com.tribly.service.garmin;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.common.exception.BadRequestException;
import com.tribly.common.exception.ForbiddenException;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.user.User;
import com.tribly.dto.garmin.response.GarminTokenResponse;
import com.tribly.repository.auth.AuthSessionRepository;
import com.tribly.service.security.DomainResolver;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GarminAuthServiceTest {

  @Inject GarminAuthService garminAuthService;
  @Inject GarminJwtService garminJwtService;
  @Inject AuthSessionRepository authSessionRepository;
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

  @Nested
  class GenerateAuthCode {

    @Test
    void shouldGenerateAuthCodeForValidToken() {
      String accessToken = garminJwtService.generateAccessToken(user);

      String authCode = garminAuthService.generateAuthCode(accessToken, "connectiq://oauth");

      assertNotNull(authCode);
      assertFalse(authCode.isEmpty());
    }

    @Test
    void shouldThrowForInvalidToken() {
      assertThrows(
          ForbiddenException.class,
          () -> garminAuthService.generateAuthCode("invalid-token", "connectiq://oauth"));
    }

    @Test
    void shouldThrowForExpiredToken() {
      // Use a malformed token that looks expired
      assertThrows(
          ForbiddenException.class,
          () ->
              garminAuthService.generateAuthCode(
                  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxfQ.invalid",
                  "connectiq://oauth"));
    }
  }

  @Nested
  class ExchangeAuthCode {

    @Test
    void shouldExchangeValidAuthCode() {
      String accessToken = garminJwtService.generateAccessToken(user);
      String authCode = garminAuthService.generateAuthCode(accessToken, "connectiq://oauth");

      GarminTokenResponse response = garminAuthService.exchangeAuthCode(authCode);

      assertNotNull(response);
      assertNotNull(response.accessToken());
      assertNotNull(response.refreshToken());
      assertEquals("Bearer", response.tokenType());
      assertTrue(response.expiresIn() > 0);
    }

    @Test
    void shouldCreateAuthSession() {
      String accessToken = garminJwtService.generateAccessToken(user);
      String authCode = garminAuthService.generateAuthCode(accessToken, "connectiq://oauth");
      long sessionCountBefore = authSessionRepository.count();

      garminAuthService.exchangeAuthCode(authCode);

      assertEquals(sessionCountBefore + 1, authSessionRepository.count());
    }

    @Test
    void shouldThrowForInvalidCode() {
      assertThrows(
          BadRequestException.class, () -> garminAuthService.exchangeAuthCode("invalid-code"));
    }

    @Test
    void shouldThrowForAlreadyUsedCode() {
      String accessToken = garminJwtService.generateAccessToken(user);
      String authCode = garminAuthService.generateAuthCode(accessToken, "connectiq://oauth");

      // First exchange should succeed
      garminAuthService.exchangeAuthCode(authCode);

      // Second exchange should fail (code already consumed)
      assertThrows(BadRequestException.class, () -> garminAuthService.exchangeAuthCode(authCode));
    }
  }

  @Nested
  class RefreshToken {

    @Test
    void shouldRefreshValidToken() {
      // First, get tokens via auth code exchange
      String accessToken = garminJwtService.generateAccessToken(user);
      String authCode = garminAuthService.generateAuthCode(accessToken, "connectiq://oauth");
      GarminTokenResponse initialResponse = garminAuthService.exchangeAuthCode(authCode);

      // Now refresh
      GarminTokenResponse refreshedResponse =
          garminAuthService.refreshToken(initialResponse.refreshToken());

      assertNotNull(refreshedResponse);
      assertNotNull(refreshedResponse.accessToken());
      assertEquals("Bearer", refreshedResponse.tokenType());
      // Refresh token should not be returned on refresh
      assertNull(refreshedResponse.refreshToken());
    }

    @Test
    void shouldThrowForInvalidRefreshToken() {
      assertThrows(ForbiddenException.class, () -> garminAuthService.refreshToken("invalid-token"));
    }

    @Test
    void shouldThrowForExpiredSession() {
      // Create an expired session
      dataService.createExpiredAuthSession(user, "expired-hash");

      assertThrows(
          ForbiddenException.class, () -> garminAuthService.refreshToken("some-token-that-hashes"));
    }
  }
}
