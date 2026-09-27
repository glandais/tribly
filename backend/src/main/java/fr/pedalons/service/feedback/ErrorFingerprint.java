package fr.pedalons.service.feedback;

import fr.pedalons.enums.ClientPlatform;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Tells two unhandled errors apart: same fingerprint, same bug, same GitHub issue.
 *
 * <p>Computed by the server, never trusted from a client. It hashes the platform, the error class,
 * the message with its variable parts blanked (numbers, ids), and the first {@value #FRAMES} frames
 * of the application's own code without their line and column — so that the same bug hit on two
 * rides, or after an unrelated edit higher in the file, is one issue.
 *
 * <p>A web frame keeps only its chunk file, without the Vite content hash: function names are
 * minified and change with every build, a chunk name does not. A Dart frame keeps its function and
 * its {@code package:pedalons/} file.
 */
final class ErrorFingerprint {

  static final int FRAMES = 3;

  private static final Pattern UUID =
      Pattern.compile(
          "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

  /** A TSID as the API prints it: 13 lowercase base-32 characters, at least one digit. */
  private static final Pattern TSID = Pattern.compile("\\b(?=[0-9a-z]*[0-9])[0-9a-z]{13}\\b");

  private static final Pattern HEX = Pattern.compile("\\b0x[0-9a-fA-F]+\\b|\\b[0-9a-f]{16,}\\b");
  private static final Pattern NUMBER = Pattern.compile("\\d+");
  private static final Pattern SPACES = Pattern.compile("\\s+");

  /** {@code #3      State.build (package:pedalons/features/x.dart:12:5)} */
  private static final Pattern DART_FRAME =
      Pattern.compile("^#\\d+\\s+(.+?)\\s+\\((package:[^)]+?)(?::\\d+)*\\)$");

  /** {@code at fn (https://host/assets/Page-AbC123x_.js:1:2345)} or {@code fn@https://…:1:2} */
  private static final Pattern WEB_FILE =
      Pattern.compile(
          "(?:https?://[^/\\s]+)?(/[^\\s():?]+\\.(?:m?js|jsx|tsx?))(?:\\?[^\\s:)]*)?(?::\\d+)*");

  /** Vite's {@code -[hash]} before the extension. */
  private static final Pattern VITE_HASH =
      Pattern.compile("-[A-Za-z0-9_-]{8}(\\.(?:m?js|jsx|tsx?))$");

  private ErrorFingerprint() {}

  static String compute(
      ClientPlatform platform, String type, String message, @Nullable String stack) {
    StringBuilder key =
        new StringBuilder()
            .append(platform.name())
            .append('\n')
            .append(type.trim())
            .append('\n')
            .append(normalizeMessage(message));
    for (String frame : appFrames(platform, stack)) {
      key.append('\n').append(frame);
    }
    return sha256(key.toString());
  }

  static String normalizeMessage(String message) {
    String s = UUID.matcher(message).replaceAll("#");
    s = TSID.matcher(s).replaceAll("#");
    s = HEX.matcher(s).replaceAll("#");
    s = NUMBER.matcher(s).replaceAll("#");
    s = SPACES.matcher(s).replaceAll(" ").trim();
    return s.length() > 300 ? s.substring(0, 300) : s;
  }

  /**
   * The first {@value #FRAMES} frames of the application's code, normalized. Falls back on the
   * first frames of any code when none is the application's — a crash inside a library is still
   * better told apart by where it happened than lumped with every other error of that class.
   */
  static List<String> appFrames(ClientPlatform platform, @Nullable String stack) {
    if (stack == null || stack.isBlank()) {
      return List.of();
    }
    List<String> app = new ArrayList<>();
    List<String> any = new ArrayList<>();
    for (String raw : stack.split("\\R")) {
      String line = raw.trim();
      if (line.isEmpty()) {
        continue;
      }
      String frame = platform == ClientPlatform.WEB ? webFrame(line) : dartFrame(line);
      if (frame == null) {
        continue;
      }
      if (any.size() < FRAMES) {
        any.add(frame);
      }
      if (isAppFrame(platform, frame)) {
        app.add(frame);
        if (app.size() == FRAMES) {
          break;
        }
      }
    }
    return app.isEmpty() ? any : app;
  }

  private static boolean isAppFrame(ClientPlatform platform, String frame) {
    return platform == ClientPlatform.WEB
        ? frame.startsWith("/assets/") || frame.startsWith("/src/")
        : frame.contains("package:pedalons/");
  }

  private static @Nullable String webFrame(String line) {
    if (line.contains("-extension://")) {
      return null;
    }
    Matcher m = WEB_FILE.matcher(line);
    if (!m.find()) {
      return null;
    }
    return VITE_HASH.matcher(m.group(1)).replaceFirst("$1");
  }

  private static @Nullable String dartFrame(String line) {
    Matcher m = DART_FRAME.matcher(line);
    if (!m.matches()) {
      return null;
    }
    return m.group(1) + " " + m.group(2);
  }

  private static String sha256(String s) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
