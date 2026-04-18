package fr.pedalons.infrastructure.filetype;

import fr.pedalons.enums.AssetType;
import java.util.Set;

/**
 * High-level group of Magika labels permitted for a given {@link AssetType}. A single AssetType
 * maps to exactly one category; each category lists the Magika labels (plus filename-based hints
 * when Magika returns a generic label) that we consider acceptable.
 */
public enum FileTypeCategory {
  IMAGE(Set.of("png", "jpeg", "gif", "webp", "bmp", "tiff", "ico", "svg")),
  // Magika labels GPX as `xml`. The `.gpx` extension fallback in FileTypeDetector covers
  // small/empty GPX files where Magika cannot reach a confident decision.
  GPX(Set.of("xml", "gpx")),
  // Magika has no dedicated FIT label and consistently classifies FIT binaries as `unknown`.
  // The `.fit` extension fallback covers cases where the model returns a different binary label.
  FIT(Set.of("fit", "unknown")),
  ATTACHMENT(
      Set.of(
          "pdf",
          "png",
          "jpeg",
          "gif",
          "webp",
          "bmp",
          "tiff",
          "ico",
          "svg",
          "doc",
          "docx",
          "xls",
          "xlsx",
          "xlsb",
          "ppt",
          "pptx",
          "odt",
          "ods",
          "odp",
          "rtf",
          "epub",
          "markdown",
          "zip",
          "sevenzip",
          "gzip",
          "bzip",
          "xz",
          "tar",
          "rar",
          "txt",
          "csv",
          "tsv",
          "json",
          "xml",
          "yaml",
          "ics"));

  private final Set<String> allowedLabels;

  FileTypeCategory(Set<String> allowedLabels) {
    this.allowedLabels = allowedLabels;
  }

  public boolean accepts(String label) {
    return allowedLabels.contains(label);
  }

  /** Package-private: only used by {@link FileTypeDetector} for diagnostic logging. */
  Set<String> getAllowedLabels() {
    return allowedLabels;
  }

  public static FileTypeCategory forAssetType(AssetType assetType) {
    return switch (assetType) {
      case LOGO,
          IMAGE,
          ROUTE_THUMBNAIL_LIGHT,
          ROUTE_THUMBNAIL_DARK,
          RIDE_THUMBNAIL_LIGHT,
          RIDE_THUMBNAIL_DARK,
          TRIP_THUMBNAIL_LIGHT,
          TRIP_THUMBNAIL_DARK ->
          IMAGE;
      case ROUTE_ORIGINAL_GPX, ROUTE_FILTERED_GPX -> GPX;
      case ROUTE_FIT -> FIT;
      case ATTACHMENT -> ATTACHMENT;
    };
  }
}
