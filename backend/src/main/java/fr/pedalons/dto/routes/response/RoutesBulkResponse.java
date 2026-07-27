package fr.pedalons.dto.routes.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * The detail of several routes fetched in one round-trip.
 *
 * <p>{@code routes} only ever holds the slugs the caller passed that both exist and are readable
 * by the caller — see {@code GET .../routes/bulk}. {@code extent} is the bounding box of exactly
 * the track geometry {@code routes} carries (after {@code simplify}/{@code points} decimation),
 * so a map can frame the batch without a second request. It deliberately excludes waypoints: a
 * waypoint imported from a GPX for a meeting cafe or a car park can sit tens of km off a route's
 * actual line, and including it would widen the extent around geometry the track itself never
 * reaches — both clients do draw waypoints, just not as part of what this extent needs to frame.
 */
@Schema(description = "Detail of several routes fetched at once, plus their combined extent")
@ValidateSchema
public record RoutesBulkResponse(
    @Schema(description = "Route details, one per readable requested slug", required = true)
        List<RouteDetailDto> routes,
    @Schema(description = "Bounding box of every returned route, or null when routes is empty")
        @Nullable BoundsDto extent) {}
