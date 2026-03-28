package fr.pedalons.dto.routes.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated route list response")
@ValidateSchema
public record RouteListResponse(
    @Schema(description = "List of routes", required = true) List<RouteDto> routes,
    @Schema(description = "Total number of routes", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
