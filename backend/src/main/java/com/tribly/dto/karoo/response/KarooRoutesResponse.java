package com.tribly.dto.karoo.response;

import com.tribly.dto.validation.ValidateSchema;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "List of routes for Karoo device")
@Builder
@ValidateSchema
public record KarooRoutesResponse(
    @Schema(description = "Available routes", required = true) List<KarooRouteDto> routes) {}
