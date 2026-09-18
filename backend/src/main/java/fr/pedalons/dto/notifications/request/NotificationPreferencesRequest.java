package fr.pedalons.dto.notifications.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** A partial update: cells not listed keep their value. */
@Schema(description = "Notification preference cells to change")
@ValidateSchema
public record NotificationPreferencesRequest(
    @Schema(description = "The cells to change", required = true) @NotNull @Size(max = 200)
        List<@Valid @NotNull NotificationPreferenceUpdate> preferences) {}
