package fr.pedalons.repository.gpx;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.gpx.GpxPreview;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpxPreviewRepositoryTest extends AbstractBaseTest {

  @Inject GpxPreviewRepository gpxPreviewRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private Domain otherDomain;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    otherDomain = dataService.createDomain("other.example.com", "Other", "http://other");
    user = dataService.createUser("owner@example.com", "Owner");
  }

  private GpxPreview persistPreview(Domain owningDomain, UUID publicId) {
    return dataService.createGpxPreview(owningDomain, user, publicId);
  }

  @Test
  void findByPublicId_shouldReturnPreviewOfTheGivenDomain() {
    UUID publicId = UUID.randomUUID();
    persistPreview(domain, publicId);

    assertTrue(gpxPreviewRepository.findByPublicId(domain.getId(), publicId).isPresent());
  }

  @Test
  void findByPublicId_shouldNotReturnPreviewOfAnotherDomain() {
    UUID publicId = UUID.randomUUID();
    persistPreview(otherDomain, publicId);

    assertTrue(gpxPreviewRepository.findByPublicId(domain.getId(), publicId).isEmpty());
  }

  @Test
  void findByPublicId_shouldReturnEmptyForUnknownId() {
    assertTrue(gpxPreviewRepository.findByPublicId(domain.getId(), UUID.randomUUID()).isEmpty());
  }

  @Test
  void findExpired_shouldOnlyReturnPreviewsCreatedBeforeTheCutoff() {
    GpxPreview fresh = persistPreview(domain, UUID.randomUUID());

    List<GpxPreview> expired =
        gpxPreviewRepository.findExpired(Instant.now().minus(1, ChronoUnit.DAYS));
    assertTrue(expired.isEmpty());

    dataService.backdateGpxPreview(fresh, 2);
    expired = gpxPreviewRepository.findExpired(Instant.now().minus(1, ChronoUnit.DAYS));
    assertEquals(1, expired.size());
    assertEquals(fresh.getId(), expired.getFirst().getId());
  }
}
