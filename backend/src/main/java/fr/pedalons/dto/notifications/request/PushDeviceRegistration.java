package fr.pedalons.dto.notifications.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.PushPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/** What an app sends on every launch once the member has allowed notifications. */
@Schema(description = "A device to receive push notifications on")
@ValidateSchema
public record PushDeviceRegistration(
    @Schema(
            description =
                "The FCM registration token. Registering a token already known moves it to the"
                    + " current user and refreshes its last-seen date.",
            required = true)
        @NotBlank
        @Size(max = 512)
        String token,
    @Schema(description = "The device's platform", required = true) @NotNull PushPlatform platform,
    @Schema(description = "A human-readable device name, for the member's own device list")
        @Size(max = 120)
        @Nullable String deviceName,
    @Schema(description = "The app version that registered, for support") @Size(max = 40)
        @Nullable String appVersion) {}
