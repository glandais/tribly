package com.tribly.service.gps;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.common.exception.BusinessException;
import com.tribly.domain.gps.GpsServiceConnection;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.user.User;
import com.tribly.dto.gps.response.GpsOAuthUrlResponse;
import com.tribly.dto.gps.response.GpsServiceConnectionDto;
import com.tribly.enums.GpsServiceType;
import com.tribly.repository.gps.GpsServiceConnectionRepository;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpsServiceTest extends AbstractBaseTest {

  @Inject GpsService gpsService;
  @Inject GpsServiceConnectionRepository connectionRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext triblyContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createVerifiedUser("gps@example.com", "GPS User");
    // Set up domain GPS credentials for tests
    dataService.createDomainGpsCredential(
        domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");
    dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
  }

  @Nested
  class InitiateOAuth {

    @Test
    void shouldReturnAuthUrlForHammerhead() {
      triblyContext.setUserForTest(user);

      GpsOAuthUrlResponse response = gpsService.initiateOAuth(GpsServiceType.HAMMERHEAD);

      assertNotNull(response);
      assertNotNull(response.authorizationUrl());
      assertTrue(response.authorizationUrl().contains("state="));
    }

    @Test
    void shouldReturnAuthUrlForGarmin() {
      triblyContext.setUserForTest(user);

      GpsOAuthUrlResponse response = gpsService.initiateOAuth(GpsServiceType.GARMIN);

      assertNotNull(response);
      assertNotNull(response.authorizationUrl());
      assertTrue(response.authorizationUrl().contains("state="));
    }

    @Test
    void shouldThrowIfAlreadyConnected() {
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);

      triblyContext.setUserForTest(user);

      assertThrows(
          BusinessException.class, () -> gpsService.initiateOAuth(GpsServiceType.HAMMERHEAD));
    }

    @Test
    void shouldAllowDifferentServiceWhenOneConnected() {
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);

      triblyContext.setUserForTest(user);

      // Should not throw for different service type
      GpsOAuthUrlResponse response = gpsService.initiateOAuth(GpsServiceType.GARMIN);
      assertNotNull(response);
    }

    @Test
    void shouldThrowWhenCredentialsNotConfigured() {
      // Create domain without credentials
      Domain domainWithoutCredentials =
          dataService.createDomain(
              "nocreds.example.com", "No Creds", "https://nocreds.example.com");
      domainResolver.setDomainForTest(domainWithoutCredentials);
      User userOnNewDomain =
          dataService.createVerifiedUser(
              domainWithoutCredentials, "user@nocreds.example.com", "User");

      triblyContext.setUserForTest(userOnNewDomain);

      assertThrows(
          BusinessException.class, () -> gpsService.initiateOAuth(GpsServiceType.HAMMERHEAD));
    }
  }

  @Nested
  class HandleCallback {

    @Test
    void shouldThrowForInvalidState() {
      assertThrows(
          BusinessException.class,
          () -> gpsService.handleCallback(GpsServiceType.HAMMERHEAD, "code", "invalid-state"));
    }

    @Test
    void shouldThrowForMismatchedServiceType() {
      triblyContext.setUserForTest(user);
      GpsOAuthUrlResponse oauthResponse = gpsService.initiateOAuth(GpsServiceType.HAMMERHEAD);

      // Extract state from URL
      String state = extractStateFromUrl(oauthResponse.authorizationUrl());

      // Try to use state with different service type
      assertThrows(
          BusinessException.class,
          () -> gpsService.handleCallback(GpsServiceType.GARMIN, "code", state));
    }

    private String extractStateFromUrl(String url) {
      int stateStart = url.indexOf("state=") + 6;
      int stateEnd = url.indexOf("&", stateStart);
      if (stateEnd == -1) stateEnd = url.length();
      return url.substring(stateStart, stateEnd);
    }
  }

  @Nested
  class Disconnect {

    @Test
    void shouldDisconnectExistingConnection() {
      GpsServiceConnection connection =
          dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);

      triblyContext.setUserForTest(user);

      gpsService.disconnect(GpsServiceType.HAMMERHEAD);

      // Verify connection is soft deleted
      assertTrue(
          connectionRepository
              .findByUserAndService(user.getId(), GpsServiceType.HAMMERHEAD)
              .isEmpty());
    }

    @Test
    void shouldThrowForNonexistentConnection() {
      triblyContext.setUserForTest(user);

      assertThrows(BusinessException.class, () -> gpsService.disconnect(GpsServiceType.HAMMERHEAD));
    }
  }

  @Nested
  class GetConnectionsForUser {

    @Test
    void shouldReturnEmptyListWhenNoConnections() {
      triblyContext.setUserForTest(user);

      List<GpsServiceConnectionDto> connections = gpsService.getConnectionsForUser();

      assertNotNull(connections);
      assertTrue(connections.isEmpty());
    }

    @Test
    void shouldReturnAllConnections() {
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);

      triblyContext.setUserForTest(user);

      List<GpsServiceConnectionDto> connections = gpsService.getConnectionsForUser();

      assertEquals(2, connections.size());
    }

    @Test
    void shouldNotReturnDeletedConnections() {
      GpsServiceConnection activeConnection =
          dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);
      GpsServiceConnection deletedConnection =
          dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);
      dataService.deleteGpsServiceConnection(deletedConnection);

      triblyContext.setUserForTest(user);

      List<GpsServiceConnectionDto> connections = gpsService.getConnectionsForUser();

      assertEquals(1, connections.size());
      assertEquals(GpsServiceType.HAMMERHEAD, connections.get(0).serviceType());
    }

    @Test
    void shouldNotReturnOtherUsersConnections() {
      User otherUser = dataService.createVerifiedUser("other@example.com", "Other User");
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);
      dataService.createGpsServiceConnection(otherUser, GpsServiceType.GARMIN);

      triblyContext.setUserForTest(user);

      List<GpsServiceConnectionDto> connections = gpsService.getConnectionsForUser();

      assertEquals(1, connections.size());
      assertEquals(GpsServiceType.HAMMERHEAD, connections.get(0).serviceType());
    }
  }

  @Nested
  class UploadRoute {

    @Test
    void shouldThrowForNonConnectedService() {
      triblyContext.setUserForTest(user);

      assertThrows(
          BusinessException.class,
          () -> gpsService.uploadRoute(GpsServiceType.HAMMERHEAD, "team-slug", "route-slug"));
    }
  }

  @Nested
  class GetAvailableServices {

    @Test
    void shouldReturnConfiguredServices() {
      triblyContext.setUserForTest(user);

      // Credentials already set up in setUp() for HAMMERHEAD and GARMIN
      List<GpsServiceType> result = gpsService.getAvailableServices();

      assertEquals(2, result.size());
      assertTrue(result.contains(GpsServiceType.HAMMERHEAD));
      assertTrue(result.contains(GpsServiceType.GARMIN));
    }

    @Test
    void shouldReturnEmptyListWhenNoCredentialsConfigured() {
      triblyContext.setUserForTest(user);

      // Create domain without credentials
      Domain domainWithoutCredentials =
          dataService.createDomain(
              "nocreds.example.com", "No Creds", "https://nocreds.example.com");
      domainResolver.setDomainForTest(domainWithoutCredentials);

      List<GpsServiceType> result = gpsService.getAvailableServices();

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldOnlyReturnServicesForCurrentDomain() {
      triblyContext.setUserForTest(user);

      // Create another domain with different credentials
      Domain otherDomain =
          dataService.createDomain("other.example.com", "Other", "https://other.example.com");
      dataService.createDomainGpsCredential(otherDomain, GpsServiceType.GARMIN, "other-garmin-id");

      // Current domain (from setUp) has both services
      List<GpsServiceType> result = gpsService.getAvailableServices();

      assertEquals(2, result.size());
      assertTrue(result.contains(GpsServiceType.HAMMERHEAD));
      assertTrue(result.contains(GpsServiceType.GARMIN));
    }
  }
}
