package com.tribly.dto.router.request;

import com.tribly.common.GeoPoint;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record RouterRequest(
    @Schema(required = true) GeoPoint from,
    @Schema(required = true) GeoPoint to,
    @Schema(required = true) String profile) {}
