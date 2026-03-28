package fr.pedalons.repository.gps;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.gps.DomainGpsCredential;
import fr.pedalons.domain.platform.Domain;
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
class DomainGpsCredentialRepositoryTest extends AbstractBaseTest {

  @Inject DomainGpsCredentialRepository domainGpsCredentialRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
  }

  @Nested
  @DisplayName("findByDomainAndServiceType")
  class FindByDomainAndServiceType {

    @Test
    @DisplayName("Should find credential by domain and service type")
    void findByDomainAndServiceType_shouldFindCredential() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

      Optional<DomainGpsCredential> result =
          domainGpsCredentialRepository.findByDomainAndServiceType(
              domain.getId(), GpsServiceType.GARMIN);

      assertTrue(result.isPresent());
      assertEquals(domain.getId(), result.get().getDomain().getId());
      assertEquals(GpsServiceType.GARMIN, result.get().getServiceType());
      assertEquals("garmin-client-id", result.get().getClientId());
    }

    @Test
    @DisplayName("Should return empty for non-existent service type")
    void findByDomainAndServiceType_nonExistentServiceType_shouldReturnEmpty() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

      Optional<DomainGpsCredential> result =
          domainGpsCredentialRepository.findByDomainAndServiceType(
              domain.getId(), GpsServiceType.HAMMERHEAD);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for non-existent domain")
    void findByDomainAndServiceType_nonExistentDomain_shouldReturnEmpty() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

      Optional<DomainGpsCredential> result =
          domainGpsCredentialRepository.findByDomainAndServiceType(999999L, GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not return inactive credentials")
    void findByDomainAndServiceType_inactiveCredential_shouldReturnEmpty() {
      DomainGpsCredential credential =
          dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.deactivateDomainGpsCredential(credential);

      Optional<DomainGpsCredential> result =
          domainGpsCredentialRepository.findByDomainAndServiceType(
              domain.getId(), GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find correct credential when domain has multiple services")
    void findByDomainAndServiceType_multipleServices_shouldFindCorrectOne() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");

      Optional<DomainGpsCredential> result =
          domainGpsCredentialRepository.findByDomainAndServiceType(
              domain.getId(), GpsServiceType.HAMMERHEAD);

      assertTrue(result.isPresent());
      assertEquals(GpsServiceType.HAMMERHEAD, result.get().getServiceType());
      assertEquals("hammerhead-client-id", result.get().getClientId());
    }

    @Test
    @DisplayName("Should not find credential from different domain")
    void findByDomainAndServiceType_differentDomain_shouldReturnEmpty() {
      Domain otherDomain =
          dataService.createDomain("other.example.com", "Other", "https://other.example.com");
      dataService.createDomainGpsCredential(otherDomain, GpsServiceType.GARMIN, "garmin-client-id");

      Optional<DomainGpsCredential> result =
          domainGpsCredentialRepository.findByDomainAndServiceType(
              domain.getId(), GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findActiveByDomain")
  class FindActiveByDomain {

    @Test
    @DisplayName("Should find all active credentials for domain")
    void findActiveByDomain_shouldFindAllCredentials() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");

      List<DomainGpsCredential> result =
          domainGpsCredentialRepository.findActiveByDomain(domain.getId());

      assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return empty list when domain has no credentials")
    void findActiveByDomain_noCredentials_shouldReturnEmptyList() {
      List<DomainGpsCredential> result =
          domainGpsCredentialRepository.findActiveByDomain(domain.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not return inactive credentials")
    void findActiveByDomain_inactiveCredentials_shouldNotBeReturned() {
      DomainGpsCredential credential =
          dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");
      dataService.deactivateDomainGpsCredential(credential);

      List<DomainGpsCredential> result =
          domainGpsCredentialRepository.findActiveByDomain(domain.getId());

      assertEquals(1, result.size());
      assertEquals(GpsServiceType.HAMMERHEAD, result.get(0).getServiceType());
    }

    @Test
    @DisplayName("Should not return credentials from other domains")
    void findActiveByDomain_otherDomains_shouldNotBeReturned() {
      Domain otherDomain =
          dataService.createDomain("other.example.com", "Other", "https://other.example.com");
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          otherDomain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");

      List<DomainGpsCredential> result =
          domainGpsCredentialRepository.findActiveByDomain(domain.getId());

      assertEquals(1, result.size());
      assertEquals(GpsServiceType.GARMIN, result.get(0).getServiceType());
    }

    @Test
    @DisplayName("Should return empty list for non-existent domain")
    void findActiveByDomain_nonExistentDomain_shouldReturnEmptyList() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

      List<DomainGpsCredential> result = domainGpsCredentialRepository.findActiveByDomain(999999L);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findAvailableServiceTypes")
  class FindAvailableServiceTypes {

    @Test
    @DisplayName("Should return all available service types for domain")
    void findAvailableServiceTypes_shouldReturnAllTypes() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");

      List<GpsServiceType> result =
          domainGpsCredentialRepository.findAvailableServiceTypes(domain.getId());

      assertEquals(2, result.size());
      assertTrue(result.contains(GpsServiceType.GARMIN));
      assertTrue(result.contains(GpsServiceType.HAMMERHEAD));
    }

    @Test
    @DisplayName("Should return empty list when no credentials configured")
    void findAvailableServiceTypes_noCredentials_shouldReturnEmptyList() {
      List<GpsServiceType> result =
          domainGpsCredentialRepository.findAvailableServiceTypes(domain.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not return inactive service types")
    void findAvailableServiceTypes_inactiveCredentials_shouldNotBeReturned() {
      DomainGpsCredential credential =
          dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");
      dataService.deactivateDomainGpsCredential(credential);

      List<GpsServiceType> result =
          domainGpsCredentialRepository.findAvailableServiceTypes(domain.getId());

      assertEquals(1, result.size());
      assertEquals(GpsServiceType.HAMMERHEAD, result.get(0));
    }

    @Test
    @DisplayName("Should not return service types from other domains")
    void findAvailableServiceTypes_otherDomains_shouldNotBeReturned() {
      Domain otherDomain =
          dataService.createDomain("other.example.com", "Other", "https://other.example.com");
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          otherDomain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");

      List<GpsServiceType> result =
          domainGpsCredentialRepository.findAvailableServiceTypes(domain.getId());

      assertEquals(1, result.size());
      assertEquals(GpsServiceType.GARMIN, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list for non-existent domain")
    void findAvailableServiceTypes_nonExistentDomain_shouldReturnEmptyList() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

      List<GpsServiceType> result =
          domainGpsCredentialRepository.findAvailableServiceTypes(999999L);

      assertTrue(result.isEmpty());
    }
  }
}
