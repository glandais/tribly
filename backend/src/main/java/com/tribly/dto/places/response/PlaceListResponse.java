package com.tribly.dto.places.response;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated place list response")
public record PlaceListResponse(
    @Schema(description = "List of places", required = true) List<PlaceDetailDto> places,
    @Schema(description = "Total number of places", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
