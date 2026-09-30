package fr.pedalons.service.asset;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.infrastructure.image.ImageFormat;
import fr.pedalons.infrastructure.image.TestImages;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.service.asset.AssetMetadataBackfill.BatchResult;
import fr.pedalons.service.asset.AssetMetadataBackfill.Outcome;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * docs/LEDGER_*.md API-43: the images stored before storage re-encoded them are re-encoded by the
 * backfill, once.
 */
@QuarkusTest
class AssetMetadataBackfillTest extends AbstractBaseTest {

  @Inject AssetMetadataBackfill backfill;
  @Inject AssetService assetService;
  @Inject AssetRepository assetRepository;
  @Inject StorageService storageService;
  @Inject S3Client s3Client;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  @ConfigProperty(name = "storage.bucket", defaultValue = "pedalons")
  String bucket;

  private Team team;
  private User admin;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    Domain domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
  }

  /** An asset as it was before API-43: its original, metadata and all, in the bucket, flagged. */
  private Asset legacyAsset(String fileName, byte[] content) {
    Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, fileName);
    // Straight to S3: StorageService would re-encode it
    s3Client.putObject(
        PutObjectRequest.builder().bucket(bucket).key(key(asset)).build(),
        RequestBody.fromBytes(content));
    QuarkusTransaction.requiringNew()
        .run(() -> assetRepository.findById(asset.getId()).setMetadataPending(true));
    return asset;
  }

  private String key(Asset asset) {
    return assetService.getAssetKey(team, asset.getFileId());
  }

  private byte[] stored(Asset asset) throws IOException {
    try (InputStream in = storageService.retrieve(key(asset))) {
      return in.readAllBytes();
    }
  }

  private boolean pending(Asset asset) {
    return QuarkusTransaction.requiringNew()
        .call(() -> assetRepository.findById(asset.getId()).isMetadataPending());
  }

  @Test
  void reencodesLegacyOriginalsAndUnflagsThem() throws IOException {
    Asset jpeg = legacyAsset("photo.jpg", TestImages.load(TestImages.JPEG));
    Asset png = legacyAsset("photo.png", TestImages.load(TestImages.PNG));
    byte[] text = "not an image at all".getBytes(StandardCharsets.UTF_8);
    Asset notAnImage = legacyAsset("notes.txt", text);

    BatchResult result = backfill.processBatch(0, 10);

    assertEquals(3, result.processed());
    assertEquals(2, result.outcomes().get(Outcome.REENCODED));
    assertEquals(1, result.outcomes().get(Outcome.NOT_AN_IMAGE));
    assertFalse(TestImages.contains(stored(jpeg), TestImages.SECRET));
    assertFalse(TestImages.contains(stored(png), TestImages.SECRET));
    assertArrayEquals(text, stored(notAnImage));
    assertFalse(pending(jpeg));
    assertFalse(pending(png));
    assertFalse(pending(notAnImage));
  }

  @Test
  void aTiffBecomesAJpegAndItsAssetFollows() throws IOException {
    Asset tiff = legacyAsset("scan.tif", TestImages.load(TestImages.TIFF));

    BatchResult result = backfill.processBatch(0, 10);

    assertEquals(1, result.outcomes().get(Outcome.REENCODED));
    assertEquals(ImageFormat.JPEG, ImageFormat.sniff(stored(tiff)));
    Asset after =
        QuarkusTransaction.requiringNew().call(() -> assetRepository.findById(tiff.getId()));
    assertEquals("scan.jpg", after.getFileName());
    assertEquals("image/jpeg", after.getContentType());
    // docs/LEDGER_*.md API-7: the size follows the re-encoded file
    assertEquals((long) stored(tiff).length, after.getFileSize());
  }

  @Test
  void leavesABrokenImageAsItIs() throws IOException {
    byte[] broken = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1, 0x10, 0x00, 'E', 'x'};
    Asset asset = legacyAsset("broken.jpg", broken);

    BatchResult result = backfill.processBatch(0, 10);

    assertEquals(1, result.outcomes().get(Outcome.UNREADABLE));
    assertArrayEquals(broken, stored(asset));
    assertFalse(pending(asset));
  }

  @Test
  void doesNothingOnceUnflagged() throws IOException {
    Asset jpeg = legacyAsset("photo.jpg", TestImages.load(TestImages.JPEG));
    backfill.processBatch(0, 10);
    byte[] once = stored(jpeg);

    assertEquals(0, backfill.processBatch(0, 10).processed());
    assertArrayEquals(once, stored(jpeg));
  }

  @Test
  void unflagsAMissingFile() throws IOException {
    Asset asset = legacyAsset("photo.jpg", TestImages.load(TestImages.JPEG));
    storageService.delete(key(asset));

    BatchResult result = backfill.processBatch(0, 10);

    assertEquals(1, result.outcomes().get(Outcome.MISSING));
    assertFalse(pending(asset));
  }

  @Test
  void resumesAfterTheCursor() throws IOException {
    Asset first = legacyAsset("a.jpg", TestImages.load(TestImages.JPEG));
    Asset second = legacyAsset("b.jpg", TestImages.load(TestImages.JPEG));

    BatchResult result = backfill.processBatch(first.getId(), 10);

    assertEquals(1, result.processed());
    assertEquals(second.getId(), result.lastId());
    assertTrue(pending(first));
    assertFalse(pending(second));
  }

  @Test
  void newAssetsAreNeverFlagged() throws IOException {
    Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "new.jpg");
    assertFalse(pending(asset));
  }
}
