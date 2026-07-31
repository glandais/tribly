package fr.pedalons.dto.config;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The elevation source (raster-DEM) the clients shade the relief with.
 *
 * <p>Served for the same reason the basemaps are: it is a third-party tile provider, and it was
 * hard-coded in the web client while the mobile app had no relief at all.
 *
 * <p>Only a TileJSON URL is carried, deliberately. The DEM's <em>encoding</em> (Terrarium vs Mapbox
 * RGB) is declared by the TileJSON document itself, and that is the only channel both mobile
 * platforms actually read: on Android and iOS the plugin builds the native source from the
 * configuration URL and drops any encoding passed alongside it. Serving an encoding field would look
 * authoritative while changing nothing.
 */
@Schema(description = "The elevation source used to shade the relief")
@ValidateSchema
public record MapTerrainDto(
    @Schema(
            description =
                "URL of a TileJSON document describing raster-DEM tiles. The document declares the"
                    + " encoding; the clients do not.",
            required = true)
        String url,
    @Schema(
            description =
                "Deepest zoom the provider renders. Honoured by the web, which sets it on the"
                    + " source; the mobile SDKs take their zoom range from the TileJSON instead.",
            required = true)
        int maxZoom) {}
