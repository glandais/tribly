package fr.pedalons.service.asset;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.infrastructure.image.ImageFormat;
import fr.pedalons.infrastructure.image.ImageMetadataStripper;
import fr.pedalons.infrastructure.image.MalformedImageException;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.repository.asset.AssetRepository.MetadataPendingRow;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

/**
 * Strips the metadata of the files stored before storage did it on write (docs/LEDGER_*.md
 * API-43): the assets V47 flagged {@code metadata_pending}, one batch at a time.
 *
 * <p><strong>Idempotent.</strong> Each file is downloaded, stripped, and written back under the same
 * key only if stripping changed it; its flag is then cleared. A file already clean, a file that is
 * not an image, a file gone from the bucket, are just unflagged. Running it again on a file — the
 * flag cleared in between or not — leaves it as it is, stripping being a fixed point.
 *
 * <p>No transaction spans the storage calls: the flag of each asset is cleared in its own, after
 * its file is written, so a crash between the two only means the file is looked at again.
 */
@ApplicationScoped
public class AssetMetadataBackfill {

  private static final Logger LOG = Logger.getLogger(AssetMetadataBackfill.class);

  /** What happened to one file. */
  public enum Outcome {
    /** Metadata removed, file rewritten. */
    STRIPPED,
    /** Nothing to remove. */
    ALREADY_CLEAN,
    /** Not an image, or one without metadata (BMP, ICO, SVG). */
    NOT_AN_IMAGE,
    /** TIFF, HEIF…: kept as it is, logged — see the WARN. */
    UNSTRIPPABLE,
    /** Announces an image but cannot be walked: kept as it is, logged. */
    MALFORMED,
    /** No object under the key. */
    MISSING,
    /** Storage failed: the asset stays flagged and is tried again on a later run. */
    FAILED
  }

  /** @param lastId the highest asset id looked at, to resume after it */
  public record BatchResult(int processed, long lastId, Map<Outcome, Integer> outcomes) {}

  @Inject AssetRepository assetRepository;
  @Inject AssetService assetService;
  @Inject StorageService storageService;

  /**
   * Processes up to {@code limit} flagged assets with an id above {@code afterId}. Starting after
   * the last id of the previous batch keeps a file that keeps failing from blocking the others;
   * start again from 0 once a batch comes back short.
   */
  public BatchResult processBatch(long afterId, int limit) {
    List<MetadataPendingRow> rows =
        QuarkusTransaction.requiringNew()
            .call(() -> assetRepository.findMetadataPending(afterId, limit));
    Map<Outcome, Integer> outcomes = new java.util.EnumMap<>(Outcome.class);
    long lastId = afterId;
    for (MetadataPendingRow row : rows) {
      Outcome outcome = process(row);
      outcomes.merge(outcome, 1, Integer::sum);
      lastId = row.assetId();
    }
    if (!rows.isEmpty()) {
      LOG.infof("Asset metadata backfill: %d files, %s", rows.size(), outcomes);
    }
    return new BatchResult(rows.size(), lastId, outcomes);
  }

  Outcome process(MetadataPendingRow row) {
    Outcome outcome;
    try {
      outcome = strip(row);
    } catch (IOException | RuntimeException e) {
      LOG.warnf(
          e,
          "Asset metadata backfill failed for asset %s, will retry",
          TsidUtils.toString(row.assetId()));
      return Outcome.FAILED;
    }
    QuarkusTransaction.requiringNew()
        .run(() -> assetRepository.clearMetadataPending(row.assetId()));
    return outcome;
  }

  private Outcome strip(MetadataPendingRow row) throws IOException {
    String key = assetService.getAssetKey(row.teamId(), row.fileId());
    if (!storageService.exists(key)) {
      return Outcome.MISSING;
    }
    // Sniffed from a ranged read first: an attachment can be a large video or PDF, and only an
    // image is worth downloading
    ImageFormat format =
        ImageFormat.sniff(storageService.retrieveHead(key, ImageFormat.SNIFF_LENGTH));
    if (format.isRefused()) {
      LOG.warnf(
          "Asset %s (%s, %s) is a %s image: its metadata cannot be removed, left as is",
          TsidUtils.toString(row.assetId()), row.fileName(), key, format);
      return Outcome.UNSTRIPPABLE;
    }
    if (!format.isStrippable()) {
      return Outcome.NOT_AN_IMAGE;
    }
    Path original = Files.createTempFile("pedalons-backfill-", ".orig");
    Path stripped = Files.createTempFile("pedalons-backfill-", ".stripped");
    try {
      try (InputStream in = storageService.retrieve(key)) {
        Files.copy(in, original, StandardCopyOption.REPLACE_EXISTING);
      }
      try (InputStream in = Files.newInputStream(original);
          OutputStream out = Files.newOutputStream(stripped)) {
        ImageMetadataStripper.strip(format, in, out);
      } catch (MalformedImageException e) {
        LOG.warnf(
            "Asset %s (%s, %s) is a malformed %s, left as is: %s",
            TsidUtils.toString(row.assetId()), row.fileName(), key, format, e.getMessage());
        return Outcome.MALFORMED;
      }
      if (Files.mismatch(original, stripped) == -1) {
        return Outcome.ALREADY_CLEAN;
      }
      Map<String, String> metadata =
          Map.of(
              "asset-id", TsidUtils.toString(row.assetId()),
              "file-id", TsidUtils.toString(row.fileId()),
              "team-id", TsidUtils.toString(row.teamId()),
              "file-name", row.fileName());
      try (InputStream in = Files.newInputStream(stripped)) {
        storageService.store(key, in, row.contentType(), Files.size(stripped), metadata);
      }
      return Outcome.STRIPPED;
    } finally {
      Files.deleteIfExists(original);
      Files.deleteIfExists(stripped);
    }
  }
}
