package fr.pedalons.service.gpx;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.gpx.GpxPreview;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.route.GpxProcessingService;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.GpxPrivacyAssertions;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.io.read.GPXFileReader;
import io.github.glandais.gpx.io.write.FitFileWriter;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * docs/LEDGER_*.md API-44: the one-off rewrite of files stored before import stripped timestamps
 * and sensors. Each test first lets the (now sanitizing) pipeline store clean files, then puts the
 * raw activity back under the same keys to reproduce what an older upload left in the bucket.
 */
@QuarkusTest
class GpxSanitizationBackfillTest extends AbstractBaseTest {

  @Inject GpxSanitizationBackfill backfill;
  @Inject GpxProcessingService gpxProcessingService;
  @Inject GpxPreviewService gpxPreviewService;
  @Inject AssetService assetService;
  @Inject StorageService storageService;
  @Inject GPXFileReader gpxFileReader;
  @Inject FitFileWriter fitFileWriter;
  @Inject PedalonsQueryContext context;
  @Inject DomainResolver domainResolver;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;
  private Route route;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    Domain domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createUser("admin@example.com", "Admin");
    Team team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    route = dataService.createRoute(team, user, "route", Visibility.PUBLIC);
    route.getTracks().clear();
    dataService.updateRoute(route);
    context.setUserForTest(user);
    storageService.delete(GpxSanitizationBackfill.MARKER_KEY);
  }

  /** The bucket is shared by the whole test run: a marker left behind would skip every pass. */
  @AfterEach
  void removeMarker() {
    storageService.delete(GpxSanitizationBackfill.MARKER_KEY);
  }

  // ==================== Routes ====================

  @Test
  void sanitizeAll_rewritesARouteStoredWithTimestampsAndSensors() throws Exception {
    gpxProcessingService.createTracks(route, gpxProcessingService.parseGpx(activity()));
    Map<AssetType, String> keys = routeKeys();
    putRawActivityBack(
        keys.get(AssetType.ROUTE_ORIGINAL_GPX),
        keys.get(AssetType.ROUTE_FILTERED_GPX),
        keys.get(AssetType.ROUTE_FIT));

    GpxSanitizationBackfill.Report report = backfill.sanitizeAll();

    assertEquals(1, report.rewritten(), report.toString());
    assertEquals(0, report.failed(), report.toString());
    assertStoredFilesClean(
        keys.get(AssetType.ROUTE_ORIGINAL_GPX),
        keys.get(AssetType.ROUTE_FILTERED_GPX),
        keys.get(AssetType.ROUTE_FIT));
  }

  @Test
  void sanitizeAll_leavesCleanFilesAloneAndIsIdempotent() throws Exception {
    gpxProcessingService.createTracks(route, gpxProcessingService.parseGpx(activity()));
    Map<AssetType, String> keys = routeKeys();
    putRawActivityBack(
        keys.get(AssetType.ROUTE_ORIGINAL_GPX),
        keys.get(AssetType.ROUTE_FILTERED_GPX),
        keys.get(AssetType.ROUTE_FIT));
    backfill.sanitizeAll();
    byte[] filteredAfterFirstPass = read(keys.get(AssetType.ROUTE_FILTERED_GPX));

    GpxSanitizationBackfill.Report second = backfill.sanitizeAll();

    assertEquals(0, second.rewritten(), second.toString());
    assertEquals(0, second.failed(), second.toString());
    assertArrayEquals(filteredAfterFirstPass, read(keys.get(AssetType.ROUTE_FILTERED_GPX)));
  }

  @Test
  void sanitizeAll_doesNotTouchARouteImportedAfterTheFix() throws Exception {
    gpxProcessingService.createTracks(route, gpxProcessingService.parseGpx(activity()));

    GpxSanitizationBackfill.Report report = backfill.sanitizeAll();

    assertEquals(1, report.sets(), report.toString());
    assertEquals(0, report.rewritten(), report.toString());
  }

  // ==================== GPX-tool previews ====================

  @Test
  void sanitizeAll_rewritesAPreviewStoredWithTimestampsAndSensors() throws Exception {
    GpxPreview preview = gpxPreviewService.create(activity(), "activity.gpx");
    Map<String, String> keys = gpxPreviewService.exportKeys(preview);
    putRawActivityBack(keys.get("original.gpx"), keys.get("filtered.gpx"), keys.get("route.fit"));

    GpxSanitizationBackfill.Report report = backfill.sanitizeAll();

    assertEquals(1, report.rewritten(), report.toString());
    assertStoredFilesClean(
        keys.get("original.gpx"), keys.get("filtered.gpx"), keys.get("route.fit"));
  }

  // ==================== Marker (docs/LEDGER_*.md API-51) ====================

  @Test
  void runOnce_writesTheMarkerAfterAPassWithoutFailure() throws Exception {
    gpxProcessingService.createTracks(route, gpxProcessingService.parseGpx(activity()));
    Map<AssetType, String> keys = routeKeys();
    putRawActivityBack(
        keys.get(AssetType.ROUTE_ORIGINAL_GPX),
        keys.get(AssetType.ROUTE_FILTERED_GPX),
        keys.get(AssetType.ROUTE_FIT));

    backfill.runOnce();

    assertStoredFilesClean(
        keys.get(AssetType.ROUTE_ORIGINAL_GPX),
        keys.get(AssetType.ROUTE_FILTERED_GPX),
        keys.get(AssetType.ROUTE_FIT));
    assertTrue(storageService.exists(GpxSanitizationBackfill.MARKER_KEY));
  }

  @Test
  void runOnce_doesNothingOnceTheMarkerExists() throws Exception {
    gpxProcessingService.createTracks(route, gpxProcessingService.parseGpx(activity()));
    Map<AssetType, String> keys = routeKeys();
    store(
        GpxSanitizationBackfill.MARKER_KEY,
        "done\n".getBytes(StandardCharsets.UTF_8),
        "text/plain");
    putRawActivityBack(
        keys.get(AssetType.ROUTE_ORIGINAL_GPX),
        keys.get(AssetType.ROUTE_FILTERED_GPX),
        keys.get(AssetType.ROUTE_FIT));

    backfill.runOnce();

    // Still the raw activity: the pass did not run.
    assertTrue(
        GpxSanitizationBackfill.isDirty(
            new String(read(keys.get(AssetType.ROUTE_FILTERED_GPX)), StandardCharsets.UTF_8)));
  }

  @Test
  void runOnce_writesNoMarkerWhenAFileFailed() throws Exception {
    gpxProcessingService.createTracks(route, gpxProcessingService.parseGpx(activity()));
    Map<AssetType, String> keys = routeKeys();
    // Dirty (a recorded time), and unreadable: the pass counts it as failed.
    store(
        keys.get(AssetType.ROUTE_FILTERED_GPX),
        "<gpx><trk><trkseg><trkpt><time>2025-11-22T07:00:00Z</time>"
            .getBytes(StandardCharsets.UTF_8),
        "application/gpx+xml");

    assertEquals(1, backfill.sanitizeAll().failed(), "the broken file must fail the pass");
    backfill.runOnce();

    assertFalse(storageService.exists(GpxSanitizationBackfill.MARKER_KEY));
  }

  // ==================== Dirty check ====================

  @Test
  void isDirty_spotsRecordedTimesAndExtensionsOnly() {
    assertTrue(GpxSanitizationBackfill.isDirty("<trkpt><time>2025-11-22T07:00:00Z</time></trkpt>"));
    assertTrue(
        GpxSanitizationBackfill.isDirty(
            "<trkpt><time>1970-01-01T00:00:00Z</time><extensions><power>2</power></extensions>"));
    assertFalse(
        GpxSanitizationBackfill.isDirty("<trkpt><time>1970-01-01T00:00:00Z</time></trkpt>"));
    assertFalse(GpxSanitizationBackfill.isDirty("<trkpt><ele>12.0</ele></trkpt>"));
  }

  // ==================== Helpers ====================

  private static Path activity() {
    return GpxPrivacyAssertions.activityGpx();
  }

  private Map<AssetType, String> routeKeys() {
    Map<AssetType, String> keys = new java.util.EnumMap<>(AssetType.class);
    for (Asset asset : route.getAssets()) {
      keys.put(asset.getType(), assetService.getAssetKey(asset.getTeam(), asset.getFileId()));
    }
    assertTrue(keys.containsKey(AssetType.ROUTE_ORIGINAL_GPX));
    assertTrue(keys.containsKey(AssetType.ROUTE_FILTERED_GPX));
    assertTrue(keys.containsKey(AssetType.ROUTE_FIT));
    return keys;
  }

  /** What an upload stored before API-44: the raw activity, and a FIT carrying its clock. */
  private void putRawActivityBack(String originalKey, String filteredKey, String fitKey)
      throws Exception {
    byte[] raw = Files.readAllBytes(activity());
    store(originalKey, raw, "application/gpx+xml");
    store(filteredKey, raw, "application/gpx+xml");
    GPX dirty;
    try (InputStream is = new FileInputStream(activity().toFile())) {
      dirty = gpxFileReader.parseGPX(is);
    }
    Path fit = Files.createTempFile("backfill-test-", ".fit");
    try {
      fitFileWriter.writeGPX(dirty, fit.toFile());
      store(fitKey, Files.readAllBytes(fit), "application/vnd.ant.fit");
    } finally {
      Files.deleteIfExists(fit);
    }
    assertTrue(GpxSanitizationBackfill.isDirty(new String(raw, StandardCharsets.UTF_8)));
  }

  private void assertStoredFilesClean(String originalKey, String filteredKey, String fitKey)
      throws Exception {
    GpxPrivacyAssertions.assertGpxHasNoPersonalData(
        new String(read(originalKey), StandardCharsets.UTF_8));
    String filtered = new String(read(filteredKey), StandardCharsets.UTF_8);
    GpxPrivacyAssertions.assertGpxHasNoPersonalData(filtered);
    assertTrue(filtered.contains("<trkpt lat=\"47.20626\" lon=\"-1.54564\">"), "geometry lost");
    GpxPrivacyAssertions.assertFitHasNoPersonalData(read(fitKey));
  }

  private void store(String key, byte[] content, String contentType) {
    storageService.store(key, new ByteArrayInputStream(content), contentType, content.length);
  }

  private byte[] read(String key) throws Exception {
    try (InputStream is = storageService.retrieve(key)) {
      return is.readAllBytes();
    }
  }
}
