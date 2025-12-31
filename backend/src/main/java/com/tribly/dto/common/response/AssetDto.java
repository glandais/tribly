package com.tribly.dto.common.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

public record AssetDto(
    @Schema(description = "ID (TSID)", required = true) String id,
    @Schema(description = "Filename", required = true) String fileName,
    @Schema(description = "Content-Type", required = true) String contentType,
    @Schema(description = "url", required = true) String url,
    @Schema(description = "image template url") @Nullable String imageUrl,
    @Schema(description = "image dimensions") @Nullable AssetDimensionsDto imageDimensions) {}
