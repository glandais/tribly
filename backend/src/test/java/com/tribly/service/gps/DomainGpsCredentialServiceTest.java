package com.tribly.service.gps;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.gps.DomainGpsCredential;
import com.tribly.domain.platform.Domain;
import com.tribly.enums.GpsServiceType;
import com.tribly.infrastructure.security.TokenEncryptionService;
import com.tribly.service.security.DomainResolver;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DomainGpsCredentialServiceTest extends AbstractBaseTest {

  @Inject DomainGpsCredentialService credentialService;
  @Inject TokenEncryptionService encryptionService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  private Domain domain;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
  }

  @Nested
  @DisplayName("getCredentials")
  class GetCredentials {

    @Test
    @DisplayName("Should return credentials when configured for domain")
    void getCredentials_shouldReturnCredentials() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

      Optional<DomainGpsCredential> result =
          credentialService.getCredentials(GpsServiceType.GARMIN);

      assertTrue(result.isPresent());
      assertEquals("garmin-client-id", result.get().getClientId());
      assertEquals(GpsServiceType.GARMIN, result.get().getServiceType());
    }

    @Test
    @DisplayName("Should return empty when not configured for domain")
    void getCredentials_notConfigured_shouldReturnEmpty() {
      Optional<DomainGpsCredential> result =
          credentialService.getCredentials(GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when credentials are inactive")
    void getCredentials_inactive_shouldReturnEmpty() {
      DomainGpsCredential credential =
          dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.deactivateDomainGpsCredential(credential);

      Optional<DomainGpsCredential> result =
          credentialService.getCredentials(GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not return credentials from different domain")
    void getCredentials_differentDomain_shouldReturnEmpty() {
      Domain otherDomain =
          dataService.createDomain("other.example.com", "Other", "https://other.example.com");
      dataService.createDomainGpsCredential(otherDomain, GpsServiceType.GARMIN, "garmin-client-id");

      Optional<DomainGpsCredential> result =
          credentialService.getCredentials(GpsServiceType.GARMIN);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return correct credentials when multiple services configured")
    void getCredentials_multipleServices_shouldReturnCorrectOne() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");

      Optional<DomainGpsCredential> result =
          credentialService.getCredentials(GpsServiceType.HAMMERHEAD);

      assertTrue(result.isPresent());
      assertEquals("hammerhead-client-id", result.get().getClientId());
    }
  }

  @Nested
  @DisplayName("getDecryptedClientSecret")
  class GetDecryptedClientSecret {

    @Test
    @DisplayName("Should decrypt client secret")
    void getDecryptedClientSecret_shouldDecryptSecret() {
      String originalSecret = "my-secret-value";
      byte[] encryptedSecret = encryptionService.encrypt(originalSecret);
      DomainGpsCredential credential =
          dataService.createDomainGpsCredential(
              domain, GpsServiceType.HAMMERHEAD, "client-id", encryptedSecret);

      String decrypted = credentialService.getDecryptedClientSecret(credential);

      assertEquals(originalSecret, decrypted);
    }

    @Test
    @DisplayName("Should return null when client secret not set")
    void getDecryptedClientSecret_notSet_shouldReturnNull() {
      DomainGpsCredential credential =
          dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

      String decrypted = credentialService.getDecryptedClientSecret(credential);

      assertNull(decrypted);
    }
  }

  @Nested
  @DisplayName("getAvailableServices")
  class GetAvailableServices {

    @Test
    @DisplayName("Should return all configured service types")
    void getAvailableServices_shouldReturnAllConfigured() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");

      List<GpsServiceType> result = credentialService.getAvailableServices();

      assertEquals(2, result.size());
      assertTrue(result.contains(GpsServiceType.GARMIN));
      assertTrue(result.contains(GpsServiceType.HAMMERHEAD));
    }

    @Test
    @DisplayName("Should return empty list when no services configured")
    void getAvailableServices_noneConfigured_shouldReturnEmptyList() {
      List<GpsServiceType> result = credentialService.getAvailableServices();

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not include inactive services")
    void getAvailableServices_inactive_shouldNotBeIncluded() {
      DomainGpsCredential credential =
          dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          domain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");
      dataService.deactivateDomainGpsCredential(credential);

      List<GpsServiceType> result = credentialService.getAvailableServices();

      assertEquals(1, result.size());
      assertEquals(GpsServiceType.HAMMERHEAD, result.get(0));
    }

    @Test
    @DisplayName("Should not include services from other domains")
    void getAvailableServices_otherDomains_shouldNotBeIncluded() {
      Domain otherDomain =
          dataService.createDomain("other.example.com", "Other", "https://other.example.com");
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.createDomainGpsCredential(
          otherDomain, GpsServiceType.HAMMERHEAD, "hammerhead-client-id");

      List<GpsServiceType> result = credentialService.getAvailableServices();

      assertEquals(1, result.size());
      assertEquals(GpsServiceType.GARMIN, result.get(0));
    }
  }

  @Nested
  @DisplayName("isServiceAvailable")
  class IsServiceAvailable {

    @Test
    @DisplayName("Should return true when service is configured")
    void isServiceAvailable_configured_shouldReturnTrue() {
      dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");

      boolean result = credentialService.isServiceAvailable(GpsServiceType.GARMIN);

      assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when service is not configured")
    void isServiceAvailable_notConfigured_shouldReturnFalse() {
      boolean result = credentialService.isServiceAvailable(GpsServiceType.GARMIN);

      assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when service is inactive")
    void isServiceAvailable_inactive_shouldReturnFalse() {
      DomainGpsCredential credential =
          dataService.createDomainGpsCredential(domain, GpsServiceType.GARMIN, "garmin-client-id");
      dataService.deactivateDomainGpsCredential(credential);

      boolean result = credentialService.isServiceAvailable(GpsServiceType.GARMIN);

      assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for service configured on different domain")
    void isServiceAvailable_differentDomain_shouldReturnFalse() {
      Domain otherDomain =
          dataService.createDomain("other.example.com", "Other", "https://other.example.com");
      dataService.createDomainGpsCredential(otherDomain, GpsServiceType.GARMIN, "garmin-client-id");

      boolean result = credentialService.isServiceAvailable(GpsServiceType.GARMIN);

      assertFalse(result);
    }
  }
}
