package fr.pedalons.infrastructure.filetype;

import edu.kit.kastel.mcse.ardoco.magika.FileTypePredictor;
import edu.kit.kastel.mcse.ardoco.magika.Prediction;
import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.AssetType;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Content-type detection backed by Google Magika (ArDoCo Java port). The predictor loads an ONNX
 * model on startup and is safe to share across threads; sessions are opened per call by the
 * underlying library.
 *
 * <p>Magika returns coarse labels (e.g. {@code xml}, {@code unknown}). For formats where the
 * filename carries signal that Magika does not (GPX under XML, FIT as generic binary) we prefer the
 * extension-based MIME type.
 *
 * <p><strong>Concurrency / performance:</strong> the upstream {@code FileTypePredictor} creates a
 * fresh ONNX Runtime session per {@code predictFileType} call, which dominates the cost of
 * detection (model load + tensor allocation) and serializes naturally on the underlying ORT
 * environment. Under bursty concurrent uploads this becomes the bottleneck before any I/O does. If
 * upload throughput becomes an issue, options include (a) wrapping the predictor in a small worker
 * pool with cached ORT sessions, (b) bumping ORT thread settings, or (c) replacing this dependency
 * with a thin direct ORT integration that reuses a single session across requests.
 */
@ApplicationScoped
public class FileTypeDetector {

  private static final Logger LOG = Logger.getLogger(FileTypeDetector.class);

  private static final Map<String, String> LABEL_TO_MIME =
      Map.ofEntries(
          Map.entry("png", "image/png"),
          Map.entry("jpeg", "image/jpeg"),
          Map.entry("gif", "image/gif"),
          Map.entry("webp", "image/webp"),
          Map.entry("bmp", "image/bmp"),
          Map.entry("tiff", "image/tiff"),
          Map.entry("ico", "image/x-icon"),
          Map.entry("svg", "image/svg+xml"),
          Map.entry("pdf", "application/pdf"),
          Map.entry("xml", "application/xml"),
          Map.entry("gpx", "application/gpx+xml"),
          Map.entry("fit", "application/vnd.ant.fit"),
          Map.entry("json", "application/json"),
          Map.entry("html", "text/html"),
          Map.entry("txt", "text/plain"),
          Map.entry("csv", "text/csv"),
          Map.entry("zip", "application/zip"));

  private FileTypePredictor predictor;

  @PostConstruct
  void init() {
    try {
      this.predictor = new FileTypePredictor();
      LOG.info("Magika FileTypePredictor initialized successfully");
    } catch (Exception e) {
      LOG.error("Failed to initialize Magika FileTypePredictor", e);
      throw e;
    }
  }

  /** Runs Magika against {@code file} and returns a label + confidence + resolved MIME. */
  public DetectedFileType detect(File file, @Nullable String fileName) {
    Prediction prediction;
    try {
      prediction = predictor.predictFileType(file.toPath());
    } catch (Exception e) {
      LOG.errorf(
          e, "Magika file type detection failed fileName=%s path=%s", fileName, file.toPath());
      throw new BadRequestException(ErrorCode.FILE_DETECTION_FAILED, e);
    }
    String label = prediction.label();
    String mime = resolveMime(label, fileName);
    LOG.debugf(
        "Magika detected label=%s confidence=%.3f mime=%s for file=%s",
        label, prediction.probability(), mime, fileName);
    return new DetectedFileType(label, prediction.probability(), mime);
  }

  /**
   * Detects the file type and validates it against the category derived from {@code assetType}.
   *
   * <p>Acceptance has two stages: first the Magika label must satisfy the category policy (an
   * allowlist for IMAGE/GPX/FIT, a blocklist for ATTACHMENT — see {@link FileTypeCategory}); if
   * the label check fails, a filename-extension fallback is consulted (see {@link
   * #matchesByExtension}) which covers small/empty GPX and FIT files where Magika cannot reach a
   * confident decision. When the extension fallback is the deciding factor a WARN is logged so a
   * silent Magika regression is still visible.
   *
   * <p>Throws {@link BadRequestException} with {@link ErrorCode#FILE_TYPE_REJECTED} when neither
   * the label policy nor the extension fallback accepts the file, or {@link
   * ErrorCode#FILE_DETECTION_FAILED} when Magika itself errors out (see {@link #detect}).
   */
  public DetectedFileType detectAndValidate(
      File file, @Nullable String fileName, AssetType assetType) {
    DetectedFileType detected = detect(file, fileName);
    FileTypeCategory category = FileTypeCategory.forAssetType(assetType);
    boolean acceptedByLabel = category.accepts(detected.label());
    if (!acceptedByLabel) {
      if (!matchesByExtension(category, fileName)) {
        LOG.warnf(
            "Rejecting upload fileName=%s assetType=%s detectedLabel=%s category=%s %s=%s",
            fileName,
            assetType,
            detected.label(),
            category,
            policyKey(category),
            category.getLabels());
        throw new BadRequestException(ErrorCode.FILE_TYPE_REJECTED);
      }
      LOG.warnf(
          "Accepting upload via extension fallback (Magika label rejected by category policy):"
              + " fileName=%s assetType=%s detectedLabel=%s confidence=%.3f category=%s %s=%s",
          fileName,
          assetType,
          detected.label(),
          detected.confidence(),
          category,
          policyKey(category),
          category.getLabels());
    }
    return detected;
  }

  private static String policyKey(FileTypeCategory category) {
    return category.isExcludeMode() ? "excluded" : "allowed";
  }

  private static String resolveMime(String label, @Nullable String fileName) {
    String extensionMime = mimeFromExtension(fileName);
    if (extensionMime != null
        && ("xml".equals(label)
            || "unknown".equals(label)
            || "empty".equals(label)
            || "txt".equals(label))) {
      return extensionMime;
    }
    String mapped = LABEL_TO_MIME.get(label);
    if (mapped != null) {
      return mapped;
    }
    return extensionMime != null ? extensionMime : "application/octet-stream";
  }

  private static @Nullable String mimeFromExtension(@Nullable String fileName) {
    if (fileName == null) {
      return null;
    }
    String lower = fileName.toLowerCase(Locale.ROOT);
    if (lower.endsWith(".png")) return "image/png";
    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
    if (lower.endsWith(".gif")) return "image/gif";
    if (lower.endsWith(".webp")) return "image/webp";
    if (lower.endsWith(".svg")) return "image/svg+xml";
    if (lower.endsWith(".gpx")) return "application/gpx+xml";
    if (lower.endsWith(".fit")) return "application/vnd.ant.fit";
    if (lower.endsWith(".pdf")) return "application/pdf";
    if (lower.endsWith(".xml")) return "application/xml";
    if (lower.endsWith(".json")) return "application/json";
    return null;
  }

  private static boolean matchesByExtension(FileTypeCategory category, @Nullable String fileName) {
    if (fileName == null) {
      return false;
    }
    String lower = fileName.toLowerCase(Locale.ROOT);
    return switch (category) {
      case GPX -> lower.endsWith(".gpx");
      case FIT -> lower.endsWith(".fit");
      case IMAGE, ATTACHMENT -> false;
    };
  }
}
