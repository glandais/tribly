package fr.pedalons.dto.notifications.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Mute or unmute one of the current user's teams")
@ValidateSchema
public record NotificationTeamPreferenceUpdate(
    @Schema(description = "Team slug — a team the user belongs to", required = true) @NotBlank
        String teamSlug,
    @Schema(description = "Whether to mute its announcements", required = true) boolean muted) {}
