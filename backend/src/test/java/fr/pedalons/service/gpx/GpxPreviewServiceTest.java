package fr.pedalons.service.gpx;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.gpx.GpxPreview;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.gpx.GpxPreviewRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpxPreviewServiceTest extends AbstractBaseTest {

  @Inject GpxPreviewService gpxPreviewService;
  @Inject GpxPreviewRepository gpxPreviewRepository;
  @Inject StorageService storageService;
  @Inject PedalonsQueryContext context;
  @Inject DomainResolver domainResolver;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private User user;
  private User otherUser;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createUser("owner@example.com", "Owner");
    otherUser = dataService.createUser("other@example.com", "Other");
    context.setUserForTest(user);
  }

  private Path exampleGpx() {
    return new File("src/test/resources/example.gpx").toPath();
  }

  private static String key(GpxPreview preview, String fileName) {
    return "gpx-previews/" + preview.getPublicId() + "/" + fileName;
  }

  @Test
  void create_shouldAnalyseGpxAndStoreBothFiles() {
    GpxPreview preview = gpxPreviewService.create(exampleGpx(), "fallback.gpx");

    assertNotNull(preview.getId());
    assertNotNull(preview.getPublicId());
    assertEquals(domain.getId(), preview.getDomain().getId());
    assertEquals(user.getId(), preview.getCreatedBy().getId());
    assertTrue(preview.getDistance() > 0);
    assertFalse(preview.getTracks().isEmpty());
    assertFalse(preview.getTracks().getFirst().trackPoints().isEmpty());

    assertTrue(storageService.exists(key(preview, "original.gpx")));
    assertTrue(storageService.exists(key(preview, "filtered.gpx")));
  }

  @Test
  void create_shouldFallBackToTheFileNameWhenTheGpxHasNoName() {
    GpxPreview preview = gpxPreviewService.create(exampleGpx(), "fallback.gpx");

    assertNotNull(preview.getName());
    assertFalse(preview.getName().isBlank());
  }

  @Test
  void create_shouldThrowForEmptyGpx() {
    Path empty = new File("src/test/resources/empty.gpx").toPath();

    assertThrows(PedalonsException.class, () -> gpxPreviewService.create(empty, "empty.gpx"));
  }

  @Test
  void getByPublicId_shouldReturnPreviewOfCurrentDomain() {
    GpxPreview created = gpxPreviewService.create(exampleGpx(), "example.gpx");

    GpxPreview found = gpxPreviewService.getByPublicId(created.getPublicId().toString());

    assertEquals(created.getId(), found.getId());
  }

  @Test
  void getByPublicId_shouldNotLeakAcrossDomains() {
    GpxPreview created = gpxPreviewService.create(exampleGpx(), "example.gpx");
    String publicId = created.getPublicId().toString();

    Domain otherDomain = dataService.createDomain("other.example.com", "Other", "http://other");
    domainResolver.setDomainForTest(otherDomain);

    assertThrows(NotFoundException.class, () -> gpxPreviewService.getByPublicId(publicId));
  }

  @Test
  void getPreview_shouldBeReadableAnonymouslyAndReportItAsNotOwned() {
    GpxPreview created = gpxPreviewService.create(exampleGpx(), "example.gpx");
    context.setUserForTest(null);

    var dto = gpxPreviewService.getPreview(created.getPublicId().toString());

    assertEquals(created.getPublicId().toString(), dto.id());
    assertFalse(dto.owned());
    assertFalse(dto.tracks().isEmpty());
  }

  @Test
  void getPreview_shouldReportOwnershipForTheCreator() {
    GpxPreview created = gpxPreviewService.create(exampleGpx(), "example.gpx");

    assertTrue(gpxPreviewService.getPreview(created.getPublicId().toString()).owned());
  }

  @Test
  void getByPublicId_shouldThrowForUnknownOrMalformedId() {
    String unknown = UUID.randomUUID().toString();

    assertThrows(NotFoundException.class, () -> gpxPreviewService.getByPublicId(unknown));
    assertThrows(NotFoundException.class, () -> gpxPreviewService.getByPublicId("not-a-uuid"));
  }

  @Test
  void getFilteredGpxContent_shouldReturnStoredBytes() {
    GpxPreview preview = gpxPreviewService.create(exampleGpx(), "example.gpx");

    byte[] content = gpxPreviewService.getFilteredGpxContent(preview);

    assertTrue(content.length > 0);
  }

  @Test
  void downloadOriginalGpx_shouldWriteATempFileTheCallerOwns() throws Exception {
    GpxPreview preview = gpxPreviewService.create(exampleGpx(), "example.gpx");

    Path downloaded = gpxPreviewService.downloadOriginalGpx(preview);
    try {
      assertTrue(java.nio.file.Files.size(downloaded) > 0);
    } finally {
      java.nio.file.Files.deleteIfExists(downloaded);
    }
  }

  @Test
  void delete_shouldRemoveRowAndFilesForTheCreator() {
    GpxPreview preview = gpxPreviewService.create(exampleGpx(), "example.gpx");
    String publicId = preview.getPublicId().toString();

    gpxPreviewService.delete(publicId);

    assertThrows(NotFoundException.class, () -> gpxPreviewService.getByPublicId(publicId));
    assertFalse(storageService.exists(key(preview, "original.gpx")));
    assertFalse(storageService.exists(key(preview, "filtered.gpx")));
  }

  @Test
  void delete_shouldRejectANonCreator() {
    GpxPreview preview = gpxPreviewService.create(exampleGpx(), "example.gpx");
    String publicId = preview.getPublicId().toString();

    context.setUserForTest(otherUser);

    assertThrows(ForbiddenException.class, () -> gpxPreviewService.delete(publicId));
    assertTrue(storageService.exists(key(preview, "original.gpx")));
  }

  @Test
  void purgeExpired_shouldOnlyDeletePreviewsPastTheRetentionWindow() {
    GpxPreview fresh = gpxPreviewService.create(exampleGpx(), "fresh.gpx");
    GpxPreview old = gpxPreviewService.create(exampleGpx(), "old.gpx");
    dataService.backdateGpxPreview(old, GpxPreviewService.RETENTION_DAYS + 1);

    int deleted = gpxPreviewService.purgeExpired();

    assertEquals(1, deleted);
    assertTrue(
        gpxPreviewRepository.findByPublicId(domain.getId(), fresh.getPublicId()).isPresent());
    assertTrue(gpxPreviewRepository.findByPublicId(domain.getId(), old.getPublicId()).isEmpty());
    assertFalse(storageService.exists(key(old, "original.gpx")));
    assertTrue(storageService.exists(key(fresh, "original.gpx")));
  }
}
