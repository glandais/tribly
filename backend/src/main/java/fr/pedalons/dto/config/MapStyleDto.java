package fr.pedalons.dto.config;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * One basemap the clients may offer in their style switcher.
 *
 * <p>The list is served rather than compiled in because it is currently hard-coded on both clients —
 * the web ships its own {@code mapStyles.ts} and the mobile app a single {@code
 * tiles.versatiles.org/...} template — so changing a tile provider means shipping two releases. A
 * served list changes with a restart.
 */
@Schema(description = "A basemap style the clients may offer")
@ValidateSchema
public record MapStyleDto(
    @Schema(description = "Stable style identifier, e.g. 'colorful'", required = true) String id,
    @Schema(description = "Human-readable label for the style switcher", required = true)
        String label,
    @Schema(
            description =
                "URL of the MapLibre style document to load in light mode (or at all times when"
                    + " darkVariant is null)",
            required = true)
        String url,
    @Nullable
        @Schema(
            description =
                "URL of the style document to load instead of 'url' when the client renders in dark"
                    + " mode. Null when the style has no dark counterpart — the client then keeps"
                    + " using 'url'.")
        String darkVariant) {}
