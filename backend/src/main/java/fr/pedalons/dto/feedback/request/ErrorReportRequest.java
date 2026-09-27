package fr.pedalons.dto.feedback.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "An unhandled error, reported automatically by a client")
@ValidateSchema
public record ErrorReportRequest(
    @Schema(description = "Client, device and screen", required = true) @Valid
        ClientContextDto context,
    @Schema(description = "The error", required = true) @Valid ClientErrorDto error,
    @Nullable @Schema(description = "The client's recent log, oldest first") @Size(max = 50)
        List<@Valid ClientLogEntryDto> logs) {}
