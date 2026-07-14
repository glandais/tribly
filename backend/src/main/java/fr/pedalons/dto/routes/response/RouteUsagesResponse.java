package fr.pedalons.dto.routes.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** The rides and trips that use a route, most recent first. */
@Schema(description = "Rides and trips that use a route")
@ValidateSchema
public record RouteUsagesResponse(
    @Schema(description = "Usages of the route, most recent first", required = true)
        List<RouteUsageDto> usages) {}
