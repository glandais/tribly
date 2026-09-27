package fr.pedalons.service.feedback;

import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Scrubs what a client log must never carry to GitHub: tokens and e-mail addresses.
 *
 * <p>A safety net, not the policy — the clients log paths without query strings and no request
 * bodies. It runs on everything a client sends except the member's own message, which is redacted
 * only when rendered into the issue.
 */
final class LogRedactor {

  static final String REDACTED = "[redacted]";

  private static final Pattern JWT =
      Pattern.compile("eyJ[A-Za-z0-9_-]{5,}\\.[A-Za-z0-9_-]{5,}\\.[A-Za-z0-9_-]*");
  private static final Pattern BEARER = Pattern.compile("(?i)(bearer\\s+)[A-Za-z0-9._~+/=-]+");
  private static final Pattern SECRET_PARAM =
      Pattern.compile(
          "(?i)([?&;\\s\"'](?:token|access_token|refresh_token|otp|secret|password)"
              + "[\"']?\\s*[=:]\\s*[\"']?)[^&\\s\"',}]+");
  private static final Pattern EMAIL =
      Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

  private LogRedactor() {}

  static String redact(String text) {
    String s = JWT.matcher(text).replaceAll(REDACTED);
    s = BEARER.matcher(s).replaceAll("$1" + REDACTED);
    s = SECRET_PARAM.matcher(s).replaceAll("$1" + REDACTED);
    return EMAIL.matcher(s).replaceAll(REDACTED);
  }

  static @Nullable String redactNullable(@Nullable String text) {
    return text == null ? null : redact(text);
  }
}
