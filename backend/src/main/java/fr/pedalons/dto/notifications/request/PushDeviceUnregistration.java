package fr.pedalons.dto.notifications.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * What an app sends on sign-out. The token travels in the body, not in the path: a path is written
 * whole to the access log (docs/LEDGER_*.md API-45).
 */
@Schema(description = "A device to stop sending push notifications to")
@ValidateSchema
public record PushDeviceUnregistration(
    @Schema(description = "The FCM registration token to drop", required = true)
        @NotBlank
        @Size(max = 512)
        String token) {}
