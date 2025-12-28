package com.tribly.dto.common.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record AssetDto(
    @Schema(description = "ID (TSID)", required = true) String id,
    @Schema(description = "Filename", required = true) String fileName,
    @Schema(description = "url", required = true) String url) {}
