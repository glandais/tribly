package fr.pedalons.dto.tiles.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A minted tile token.
 *
 * <p>Both expiry fields are here on purpose, and they are not interchangeable: schedule the renewal
 * on {@code expiresIn}, never on {@code expiresAt}. A phone's clock can be minutes off without
 * anyone noticing, and an absolute instant compared against a wrong clock renews either far too
 * early or not at all. {@code expiresAt} is for the human reading a log.
 */
@Schema(description = "Short-lived token authorizing vector tile requests")
@ValidateSchema
public record TileTokenDto(
    @Schema(
            description = "Token to pass as the 't' query parameter of the .mvt endpoints",
            required = true)
        String token,
    @Schema(
            description = "Absolute expiry instant (ISO 8601), for logs and diagnostics",
            required = true)
        Instant expiresAt,
    @Schema(
            description =
                "Seconds until expiry, measured at issuance. Schedule renewal on this, not on"
                    + " expiresAt: it is immune to a device clock that drifts.",
            required = true)
        int expiresIn) {}
