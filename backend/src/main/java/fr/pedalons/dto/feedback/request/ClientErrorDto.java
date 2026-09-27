package fr.pedalons.dto.feedback.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "An unhandled error caught by a client")
@ValidateSchema
public record ClientErrorDto(
    @Schema(description = "Error class, e.g. TypeError or _TypeError", required = true)
        @NotBlank
        @Size(max = 200)
        String type,
    @Schema(description = "Error message", required = true) @Size(max = 2000) String message,
    @Nullable @Schema(description = "Stack trace, as the client printed it") @Size(max = 16000)
        String stack) {}
