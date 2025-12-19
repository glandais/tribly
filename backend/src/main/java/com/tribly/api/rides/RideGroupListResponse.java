package com.tribly.api.rides;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Ride group list response")
public record RideGroupListResponse(
    @Schema(description = "List of ride groups", required = true) List<RideGroupDto> data) {}
