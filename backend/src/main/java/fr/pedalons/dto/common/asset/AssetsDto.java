package fr.pedalons.dto.common.asset;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Builder
@Schema(
    description =
        "Assets of a content. On a write, the logo, images and attachments must each be an asset"
            + " uploaded to this team and attached to no other content, else 400"
            + " ASSET_NOT_AVAILABLE naming the asset id — the same answer whether the id is"
            + " unknown, of another team or held by another content.")
public record AssetsDto(
    @Nullable @Schema(description = "Logo") @Valid AssetDto logo,
    @Schema(description = "Images", required = true) List<@Valid AssetDto> images,
    @Schema(description = "Attachments", required = true) List<@Valid AssetDto> attachments,
    @Nullable @Schema(description = "Original GPX") AssetDto originalGpx,
    @Nullable @Schema(description = "GPX") AssetDto gpx,
    @Nullable @Schema(description = "FIT") AssetDto fit,
    @Nullable @Schema(description = "Light thumbnail") AssetDto thumbnailLight,
    @Nullable @Schema(description = "Dark thumbnail") AssetDto thumbnailDark) {

  /**
   * Missing lists become empty ones. Jackson builds a record through this constructor, never
   * through the builder, and {@code required = true} validates nothing: a body with {@code "assets":
   * {}} used to reach {@code AssetService.updateAssets} with null lists and end in a 500. Normalised
   * rather than rejected — clients always send both lists (docs/LEDGER_*.md API-28).
   */
  public AssetsDto {
    images = images == null ? new ArrayList<>() : images;
    attachments = attachments == null ? new ArrayList<>() : attachments;
  }

  public static class AssetsDtoBuilder {
    AssetsDtoBuilder() {
      images = new ArrayList<>();
      attachments = new ArrayList<>();
    }
  }
}
