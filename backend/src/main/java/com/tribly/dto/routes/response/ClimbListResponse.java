package com.tribly.dto.routes.response;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Climb list response")
public record ClimbListResponse(
    @Schema(description = "List of climbs on the route", required = true)
        List<RouteClimbDto> climbs) {}
