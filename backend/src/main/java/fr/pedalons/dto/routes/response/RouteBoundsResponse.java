package fr.pedalons.dto.routes.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * The extent a map should open on to show every route matching a filter set. Wrapping the box
 * rather than returning it bare leaves room for the empty case: no route matches, hence no extent.
 */
@Schema(description = "Bounding box of the routes matching a filter set")
@ValidateSchema
public record RouteBoundsResponse(
    @Schema(description = "Bounding box, or null when no route matches")
        @Nullable BoundsDto bounds) {}
