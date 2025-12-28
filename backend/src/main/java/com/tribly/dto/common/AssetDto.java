package com.tribly.dto.common;

import com.tribly.domain.asset.Asset;
import com.tribly.enums.AssetType;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record AssetDto(
    @Schema(description = "ID (TSID)", required = true) String id,
    @Schema(description = "Type", required = true) AssetType type,
    @Schema(description = "Filename", required = true) String fileName) {
  public static AssetDto from(Asset asset) {
    return new AssetDto(TsidUtils.toString(asset.getId()), asset.getType(), asset.getFileName());
  }
}
