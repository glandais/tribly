package fr.pedalons.infrastructure.filetype;

import fr.pedalons.enums.AssetType;
import java.util.Set;

/**
 * High-level group of Magika labels permitted for a given {@link AssetType}. A single AssetType
 * maps to exactly one category; each category lists the Magika labels (plus filename-based hints
 * when Magika returns a generic label) that we consider acceptable.
 *
 * <p>Categories work in one of two modes:
 *
 * <ul>
 *   <li><strong>ALLOW</strong> (IMAGE, GPX, FIT): the set is an allowlist; a label is accepted only
 *       if it is in the set. Appropriate for narrow categories where the set of legitimate formats
 *       is small and well known.
 *   <li><strong>EXCLUDE</strong> (ATTACHMENT): the set is a blocklist; a label is accepted unless
 *       it is in the set. Appropriate for broad categories where "anything a user might reasonably
 *       attach" is easier to enumerate by listing what we refuse (native executables, scripts,
 *       installer packages, shortcut files, ambiguous Magika labels that defeat the security
 *       purpose).
 * </ul>
 */
public enum FileTypeCategory {
  IMAGE(Set.of("png", "jpeg", "gif", "webp", "bmp", "tiff", "ico", "svg"), false),
  // Magika labels GPX as `xml`. The `.gpx` extension fallback in FileTypeDetector covers
  // small/empty GPX files where Magika cannot reach a confident decision.
  GPX(Set.of("xml", "gpx"), false),
  // Magika has no dedicated FIT label and consistently classifies FIT binaries as `unknown`.
  // The `.fit` extension fallback covers cases where the model returns a different binary label.
  FIT(Set.of("fit", "unknown"), false),
  // Blocklist: these Magika labels are refused for ATTACHMENT uploads. Everything else Magika
  // can identify is accepted (PDFs, office docs, images, archives, source code, config, data,
  // subtitles, audio/video, fonts, etc.). Categories of refused labels:
  //   - Native binaries / object files
  //   - Mobile / installer / app-bundle formats that execute code when opened
  //   - Bytecode and serialization formats that are known RCE vectors
  //   - Script sources (anything a user might double-click to run)
  //   - Web-executable markup (HTML / server-side templates — XSS + phishing risk if
  //     re-served from our domain)
  //   - Windows help / registry / shortcut files
  //   - OS housekeeping artifacts that should never be uploaded deliberately
  //   - Ambiguous Magika labels that defeat the whole point of content-based validation
  ATTACHMENT(
      Set.of(
          // Native binaries / object files
          "pebin",
          "elf",
          "macho",
          "coff",
          // Mobile / installers / app bundles
          "apk",
          "dex",
          "smali",
          "msi",
          "deb",
          "rpm",
          "dmg",
          "iso",
          "cab",
          "xar",
          "squashfs",
          "snap",
          "nupkg",
          "xpi",
          "crx",
          "jar",
          "mscompress",
          // Bytecode / serialization (RCE vectors when deserialized/executed)
          "wasm",
          "javabytecode",
          "pythonbytecode",
          "pickle",
          "pytorch",
          "onnx",
          // Script source
          "javascript",
          "typescript",
          "python",
          "shell",
          "batch",
          "powershell",
          "vba",
          "perl",
          "ruby",
          "php",
          "lua",
          "autohotkey",
          "autoit",
          "awk",
          "tcl",
          "coffeescript",
          // Web-executable markup
          "html",
          "mht",
          "asp",
          "erb",
          "jinja",
          "twig",
          "handlebars",
          "vue",
          // Windows help / registry / flash
          "chm",
          "hlp",
          "winregistry",
          "htaccess",
          "swf",
          // Shortcuts (can launch arbitrary programs)
          "lnk",
          "internetshortcut",
          // OS housekeeping artifacts
          "thumbsdb",
          "dsstore",
          // Ambiguous Magika outputs (defeat content-based validation)
          "unknown",
          "empty",
          "randombytes",
          "randomtxt"),
      true);

  private final Set<String> labels;
  private final boolean excludeMode;

  FileTypeCategory(Set<String> labels, boolean excludeMode) {
    this.labels = labels;
    this.excludeMode = excludeMode;
  }

  public boolean accepts(String label) {
    boolean inSet = labels.contains(label);
    return excludeMode ? !inSet : inSet;
  }

  /**
   * Package-private: only used by {@link FileTypeDetector} for diagnostic logging. The semantics
   * depend on {@link #isExcludeMode()} — either the allowlist or the blocklist, never both.
   */
  Set<String> getLabels() {
    return labels;
  }

  /**
   * {@code true} if {@link #getLabels()} is a blocklist (accept everything <em>not</em> in the
   * set), {@code false} if it is an allowlist.
   */
  boolean isExcludeMode() {
    return excludeMode;
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
