package fr.pedalons.dto.teams.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Create or change a team's webhook")
@ValidateSchema
public record TeamWebhookRequest(
    @Schema(
            description =
                "The https URL to post to. Omit it to keep the one already set — the API never"
                    + " returns it in full. Required when the team has no webhook yet.",
            examples = "https://hooks.slack.com/services/T000/B000/XXXX")
        @Size(max = 1000)
        @Nullable String url,
    @Schema(description = "Language the messages are written in", examples = "fr", required = true)
        @NotNull
        @Pattern(regexp = "fr|en")
        String language,
    @Schema(description = "Whether announcements are posted", required = true) boolean enabled) {}
