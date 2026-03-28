package fr.pedalons.dto.rides.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated ride list response")
@ValidateSchema
public record RideListResponse(
    @Schema(description = "List of rides", required = true) List<RideDto> rides,
    @Schema(description = "Total number of rides", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
