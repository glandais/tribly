package fr.pedalons.dto.users.export;

import com.fasterxml.jackson.databind.JsonNode;
import fr.pedalons.domain.feedback.FeedbackReport;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * {@code account/feedback.json}: the bug reports and suggestions the user wrote, with the technical
 * context their client attached. The automatic error reports are left out: they are the client's,
 * carry nothing the user wrote, and go after 90 days.
 */
public record FeedbackExport(
    String kind,
    String platform,
    @Nullable String message,
    JsonNode context,
    @Nullable JsonNode error,
    @Nullable JsonNode logs,
    Instant createdAt) {

  public static FeedbackExport from(FeedbackReport report) {
    return new FeedbackExport(
        report.getKind().name(),
        report.getPlatform().name(),
        report.getMessage(),
        report.getContext(),
        report.getError(),
        report.getLogs(),
        report.getCreatedAt());
  }
}
