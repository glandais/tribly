package com.tribly.dto.common.asset;

import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Builder
public record AssetDto(
    @Schema(description = "ID (TSID)", required = true) String id,
    @Schema(description = "Filename", required = true) String fileName,
    @Schema(description = "Content-Type", required = true) String contentType,
    @Schema(description = "url", required = true) String url,
    @Schema(description = "image template url") @Nullable String imageUrl,
    @Schema(description = "image dimensions") @Nullable AssetDimensionsDto imageDimensions) {}
