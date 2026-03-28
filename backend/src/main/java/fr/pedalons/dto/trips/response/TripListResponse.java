package fr.pedalons.dto.trips.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated trip list response")
@ValidateSchema
public record TripListResponse(
    @Schema(description = "List of trips", required = true) List<TripDto> trips,
    @Schema(description = "Total number of trips", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
