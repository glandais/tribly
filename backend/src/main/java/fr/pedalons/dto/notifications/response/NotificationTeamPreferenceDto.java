package fr.pedalons.dto.notifications.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Whether the current user silenced one of their teams")
@ValidateSchema
public record NotificationTeamPreferenceDto(
    @Schema(description = "Team slug", required = true) String teamSlug,
    @Schema(description = "Team name", required = true) String teamName,
    @Schema(
            description =
                "Muted: none of the team's announcements (publications) reach the user, inbox"
                    + " included. What concerns them personally — a cancelled ride they joined, a"
                    + " reply — still does.",
            required = true)
        boolean muted) {}
