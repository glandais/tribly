package fr.pedalons.service.user;

import fr.pedalons.infrastructure.email.EmailService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Sends the "your data export is ready" email.
 *
 * <p>Unlike {@code AuthEmailService}, this injects no {@code DomainResolver}: it runs from a
 * scheduler with no HTTP request, so the site name and base URL come from the job row, which
 * snapshotted them when the user asked.
 */
@ApplicationScoped
public class UserExportEmailService {

  private static final DateTimeFormatter EXPIRY_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("Europe/Paris"));

  @Inject EmailService emailService;

  public void sendExportReady(
      ExportJobContext ctx, String token, Instant expiresAt, long sizeBytes) {
    emailService.sendEmail(
        ctx.email(),
        EmailService.DATA_EXPORT,
        ctx.language(),
        Map.of(
            "displayName", ctx.displayName(),
            "appName", ctx.siteName(),
            "downloadUrl", ctx.baseUrl() + "/api/export/download/" + token,
            "expiresAt", EXPIRY_FORMAT.format(expiresAt),
            "fileSize", humanReadable(sizeBytes)));
  }

  /** Rounded to one decimal, base 1024 — enough for "is this the size I expected?". */
  static String humanReadable(long bytes) {
    if (bytes < 1024) {
      return bytes + " o";
    }
    String[] units = {"Ko", "Mo", "Go"};
    double value = bytes;
    int unit = -1;
    while (value >= 1024 && unit < units.length - 1) {
      value /= 1024;
      unit++;
    }
    return String.format("%.1f %s", value, units[unit]);
  }
}
