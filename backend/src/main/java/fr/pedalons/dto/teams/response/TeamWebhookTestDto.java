package fr.pedalons.dto.teams.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Outcome of a test message sent to the team's webhook")
@ValidateSchema
public record TeamWebhookTestDto(
    @Schema(description = "Whether the endpoint accepted it (2xx)", required = true)
        boolean success,
    @Schema(description = "HTTP status the endpoint answered, if it answered")
        @Nullable Integer statusCode,
    @Schema(description = "Why it failed, when it did") @Nullable String error) {}
