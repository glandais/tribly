package fr.pedalons.dto.common.asset;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Builder
public record AssetsDto(
    @Nullable @Schema(description = "Logo") @Valid AssetDto logo,
    @Schema(description = "Images", required = true) List<@Valid AssetDto> images,
    @Schema(description = "Attachments", required = true) List<@Valid AssetDto> attachments,
    @Nullable @Schema(description = "Original GPX") AssetDto originalGpx,
    @Nullable @Schema(description = "GPX") AssetDto gpx,
    @Nullable @Schema(description = "FIT") AssetDto fit,
    @Nullable @Schema(description = "Light thumbnail") AssetDto thumbnailLight,
    @Nullable @Schema(description = "Dark thumbnail") AssetDto thumbnailDark) {

  public static class AssetsDtoBuilder {
    AssetsDtoBuilder() {
      images = new ArrayList<>();
      attachments = new ArrayList<>();
    }
  }
}
