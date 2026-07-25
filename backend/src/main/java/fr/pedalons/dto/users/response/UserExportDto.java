package fr.pedalons.dto.users.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.user.UserExport;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.UserExportStatus;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * The state of a GDPR data export job.
 *
 * <p>Carries no download link and no token: the link is delivered by email, so possession of the
 * mailbox is part of the check. {@code errorMessage} is deliberately absent too — it exists on the
 * row for operators and can hold internal detail.
 */
@Schema(description = "Status of a personal data export request")
@ValidateSchema
public record UserExportDto(
    @Schema(description = "Export job identifier", required = true) String id,
    @Schema(description = "Current status", required = true) UserExportStatus status,
    @Schema(description = "When the export was requested", required = true) Instant requestedAt,
    @Schema(description = "When the export finished building") @Nullable Instant completedAt,
    @Schema(description = "When the download link stops working") @Nullable Instant expiresAt,
    @Schema(description = "Size of the archive in bytes") @Nullable Long fileSize) {

  public static UserExportDto from(UserExport export) {
    return new UserExportDto(
        TsidUtils.toString(export.getId()),
        export.getStatus(),
        export.getRequestedAt(),
        export.getCompletedAt(),
        export.getExpiresAt(),
        export.getFileSize());
  }
}
