package fr.pedalons.repository.gps;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.gps.GpsServiceConnection;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpsServiceConnectionRepositoryTest extends AbstractBaseTest {

  @Inject GpsServiceConnectionRepository gpsServiceConnectionRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    user = dataService.createUser("test@example.com", "Test User");
  }

  @Nested
  @DisplayName("findByUserAndService")
  class FindByUserAndService {

    @Test
    @DisplayName("Should find connection by user and service type")
    void findByUserAndService_shouldFindConnection() {
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);

      Optional<GpsServiceConnection> result =
          gpsServiceConnectionRepository.findByUserAndService(user.getId(), GpsServiceType.GARMIN);

      assertTrue(result.isPresent());
      assertEquals(user.getId(), result.get().getUser().getId());
      assertEquals(GpsServiceType.GARMIN, result.get().getServiceType());
    }

    @Test
    @DisplayName("Should return empty for non-existent service type")
    void findByUserAndService_nonExistentServiceType_shouldReturnEmpty() {
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);

      Optional<GpsServiceConnection> result =
          gpsServiceConnectionRepository.findByUserAndService(
              user.getId(), GpsServiceType.HAMMERHEAD);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for non-existent user")
    void findByUserAndService_nonExistentUser_shouldReturnEmpty() {
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);

      Optional<GpsServiceConnection> result =
          gpsServiceConnectionRepository.findByUserAndService(999999L, GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not return deleted connections")
    void findByUserAndService_deletedConnection_shouldReturnEmpty() {
      GpsServiceConnection connection =
          dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);
      dataService.deleteGpsServiceConnection(connection);

      Optional<GpsServiceConnection> result =
          gpsServiceConnectionRepository.findByUserAndService(user.getId(), GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find correct connection when user has multiple services")
    void findByUserAndService_multipleServices_shouldFindCorrectOne() {
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);

      Optional<GpsServiceConnection> result =
          gpsServiceConnectionRepository.findByUserAndService(
              user.getId(), GpsServiceType.HAMMERHEAD);

      assertTrue(result.isPresent());
      assertEquals(GpsServiceType.HAMMERHEAD, result.get().getServiceType());
    }

    @Test
    @DisplayName("Should not find connection from different user")
    void findByUserAndService_differentUser_shouldReturnEmpty() {
      User otherUser = dataService.createUser("other@example.com", "Other User");
      dataService.createGpsServiceConnection(otherUser, GpsServiceType.GARMIN);

      Optional<GpsServiceConnection> result =
          gpsServiceConnectionRepository.findByUserAndService(user.getId(), GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findByUser")
  class FindByUser {

    @Test
    @DisplayName("Should find all connections for user")
    void findByUser_shouldFindAllConnections() {
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);

      List<GpsServiceConnection> result = gpsServiceConnectionRepository.findByUser(user.getId());

      assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return empty list when user has no connections")
    void findByUser_noConnections_shouldReturnEmptyList() {
      List<GpsServiceConnection> result = gpsServiceConnectionRepository.findByUser(user.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not return deleted connections")
    void findByUser_deletedConnections_shouldNotBeReturned() {
      GpsServiceConnection connection =
          dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);
      dataService.createGpsServiceConnection(user, GpsServiceType.HAMMERHEAD);
      dataService.deleteGpsServiceConnection(connection);

      List<GpsServiceConnection> result = gpsServiceConnectionRepository.findByUser(user.getId());

      assertEquals(1, result.size());
      assertEquals(GpsServiceType.HAMMERHEAD, result.get(0).getServiceType());
    }

    @Test
    @DisplayName("Should not return connections from other users")
    void findByUser_otherUsers_shouldNotBeReturned() {
      User otherUser = dataService.createUser("other@example.com", "Other User");
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);
      dataService.createGpsServiceConnection(otherUser, GpsServiceType.HAMMERHEAD);

      List<GpsServiceConnection> result = gpsServiceConnectionRepository.findByUser(user.getId());

      assertEquals(1, result.size());
      assertEquals(GpsServiceType.GARMIN, result.get(0).getServiceType());
    }

    @Test
    @DisplayName("Should return empty list for non-existent user")
    void findByUser_nonExistentUser_shouldReturnEmptyList() {
      dataService.createGpsServiceConnection(user, GpsServiceType.GARMIN);

      List<GpsServiceConnection> result = gpsServiceConnectionRepository.findByUser(999999L);

      assertTrue(result.isEmpty());
    }
  }
}
