package fr.pedalons.dto.notifications.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/** A partial update: cells and teams not listed keep their value, as does an absent digest. */
@Schema(description = "Notification preference cells to change")
@ValidateSchema
public record NotificationPreferencesRequest(
    @Schema(description = "The cells to change", required = true) @NotNull @Size(max = 200)
        List<@Valid @NotNull NotificationPreferenceUpdate> preferences,
    @Schema(description = "The teams to mute or unmute") @Size(max = 200)
        @Nullable List<@Valid @NotNull NotificationTeamPreferenceUpdate> teams,
    @Schema(description = "Switch the daily e-mail digest on or off; absent leaves it")
        @Nullable Boolean emailDigest) {

  /** Cells only. */
  public NotificationPreferencesRequest(List<NotificationPreferenceUpdate> preferences) {
    this(preferences, null, null);
  }
}
