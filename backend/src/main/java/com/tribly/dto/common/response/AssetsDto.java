package com.tribly.dto.common.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Builder
public record AssetsDto(
    @Nullable @Schema(description = "Logo") AssetDto logo,
    @Schema(description = "Images", required = true) List<AssetDto> images,
    @Schema(description = "Videos", required = true) List<AssetDto> videos,
    @Schema(description = "Attachments", required = true) List<AssetDto> attachments,
    @Nullable @Schema(description = "Original GPX") AssetDto originalGpx,
    @Nullable @Schema(description = "GPX") AssetDto gpx,
    @Nullable @Schema(description = "FIT") AssetDto fit,
    @Nullable @Schema(description = "Thumbnail") AssetDto thumbnail) {

  public static class AssetsDtoBuilder {
    AssetsDtoBuilder() {
      images = new ArrayList<>();
      videos = new ArrayList<>();
      attachments = new ArrayList<>();
    }
  }
}
