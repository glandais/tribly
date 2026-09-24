package fr.pedalons.dto.users.export;

import fr.pedalons.domain.moderation.ContentReport;
import fr.pedalons.domain.moderation.UserBlock;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/** The members the user blocked, and the reports the user filed. */
public final class ModerationExport {

  private ModerationExport() {}

  /** {@code account/blocked-users.json}. */
  public record BlockEntry(String displayName, Instant blockedAt) {

    public static BlockEntry from(UserBlock block) {
      return new BlockEntry(block.getBlocked().getDisplayName(), block.getCreatedAt());
    }
  }

  /**
   * {@code account/reports.json}. Without the reported text: it is someone else's content, and the
   * target may since have been removed.
   */
  public record ReportEntry(
      String targetType,
      String reason,
      @Nullable String message,
      Instant createdAt,
      String status) {

    public static ReportEntry from(ContentReport report) {
      return new ReportEntry(
          report.getTargetType().name(),
          report.getReason().name(),
          report.getMessage(),
          report.getCreatedAt(),
          report.getStatus().name());
    }
  }
}
