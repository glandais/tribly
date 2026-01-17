package com.tribly.dto.garmin.response;

import com.tribly.dto.validation.ValidateSchema;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "List of routes for Garmin device")
@Builder
@ValidateSchema
public record GarminRoutesResponse(
    @Schema(description = "Routes list", required = true) List<GarminRouteDto> routes) {}
