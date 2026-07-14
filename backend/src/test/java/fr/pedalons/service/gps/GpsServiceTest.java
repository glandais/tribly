package fr.pedalons.service.gps;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.gps.GpsServiceConnection;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.gps.response.GpsOAuthUrlResponse;
import fr.pedalons.dto.gps.response.GpsServiceConnectionDto;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.repository.gps.GpsServiceConnectionRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
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
  @Inject PedalonsQueryContext pedalonsContext;
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
      pedalonsContext.setUserForTest(user);

      GpsOAuthUrlResponse response = gpsService.initiateOAuth(GpsServiceType.HAMMERHEAD);

      assertNotNull(response);
      assertNotNull(response.authorizationUrl());
      assertTrue(response.authorizationUrl().contains("state="));
    }

    @Test
    void shouldReturnAuthUrlForGarmin() {
      pedalonsContext.setUserForTest(user);

      GpsOAuthUrlResponse response = gpsService.initiateOAuth(GpsServiceType.GARMIN);

      assertNotNull(response);
      assertNotNull(response.authorizationUrl());
      assertTrue(response.authorizationUrl().contains("state="));
    }

    @Test
    void shouldThrowIfAlreadyConnected() {
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);

      pedalonsContext.setUserForTest(user);

      assertThrows(
          BusinessException.class, () -> gpsService.initiateOAuth(GpsServiceType.HAMMERHEAD));
    }

    @Test
    void shouldAllowDifferentServiceWhenOneConnected() {
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);

      pedalonsContext.setUserForTest(user);

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

      pedalonsContext.setUserForTest(userOnNewDomain);

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
      pedalonsContext.setUserForTest(user);
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

      pedalonsContext.setUserForTest(user);

      gpsService.disconnect(GpsServiceType.HAMMERHEAD);

      // Verify connection is soft deleted
      assertTrue(
          connectionRepository
              .findByUserAndService(user.getId(), GpsServiceType.HAMMERHEAD)
              .isEmpty());
    }

    @Test
    void shouldThrowForNonexistentConnection() {
      pedalonsContext.setUserForTest(user);

      assertThrows(BusinessException.class, () -> gpsService.disconnect(GpsServiceType.HAMMERHEAD));
    }
  }

  @Nested
  class GetConnectionsForUser {

    @Test
    void shouldReturnEmptyListWhenNoConnections() {
      pedalonsContext.setUserForTest(user);

      List<GpsServiceConnectionDto> connections = gpsService.getConnectionsForUser();

      assertNotNull(connections);
      assertTrue(connections.isEmpty());
    }

    @Test
    void shouldReturnAllConnections() {
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);

      pedalonsContext.setUserForTest(user);

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

      pedalonsContext.setUserForTest(user);

      List<GpsServiceConnectionDto> connections = gpsService.getConnectionsForUser();

      assertEquals(1, connections.size());
      assertEquals(GpsServiceType.HAMMERHEAD, connections.get(0).serviceType());
    }

    @Test
    void shouldNotReturnOtherUsersConnections() {
      User otherUser = dataService.createVerifiedUser("other@example.com", "Other User");
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);
      dataService.createGpsServiceConnection(otherUser, GpsServiceType.GARMIN);

      pedalonsContext.setUserForTest(user);

      List<GpsServiceConnectionDto> connections = gpsService.getConnectionsForUser();

      assertEquals(1, connections.size());
      assertEquals(GpsServiceType.HAMMERHEAD, connections.get(0).serviceType());
    }
  }

  @Nested
  class UploadRoute {

    @Test
    void shouldThrowForNonConnectedService() {
      pedalonsContext.setUserForTest(user);

      // The not-connected guard lives in uploadGpx, which uploadRoute delegates to once it has
      // resolved the route and its GPX bytes. Exercise it directly: driving it through uploadRoute
      // would need a persisted route carrying a GPX asset, and the missing-route lookup would throw
      // first, masking the guard under test.
      assertThrows(
          BusinessException.class,
          () -> gpsService.uploadGpx(GpsServiceType.HAMMERHEAD, new byte[0], "route-name"));
    }
  }

  @Nested
  class GetAvailableServices {

    @Test
    void shouldReturnConfiguredServices() {
      pedalonsContext.setUserForTest(user);

      // Credentials already set up in setUp() for HAMMERHEAD and GARMIN
      List<GpsServiceType> result = gpsService.getAvailableServices();

      assertEquals(2, result.size());
      assertTrue(result.contains(GpsServiceType.HAMMERHEAD));
      assertTrue(result.contains(GpsServiceType.GARMIN));
    }

    @Test
    void shouldReturnEmptyListWhenNoCredentialsConfigured() {
      pedalonsContext.setUserForTest(user);

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
      pedalonsContext.setUserForTest(user);

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
