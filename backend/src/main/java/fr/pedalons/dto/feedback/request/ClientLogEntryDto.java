package fr.pedalons.dto.feedback.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ClientLogLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "One entry of the client's recent log")
@ValidateSchema
public record ClientLogEntryDto(
    @Schema(description = "When it was logged", required = true) Instant ts,
    @Schema(description = "Severity", required = true) ClientLogLevel level,
    @Schema(description = "What logged it: console, http, navigation, error…", required = true)
        @NotBlank
        @Size(max = 50)
        String source,
    @Schema(description = "The entry, truncated by the client", required = true) @Size(max = 1000)
        String message) {}
