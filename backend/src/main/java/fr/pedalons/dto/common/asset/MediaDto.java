package fr.pedalons.dto.common.asset;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.enums.ListViewMode;
import fr.pedalons.service.asset.AssetService;
import jakarta.validation.Valid;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Builder
public record MediaDto(
    @Schema(description = "Markdown", required = true) String markdown,
    @Schema(description = "Assets", required = true) @Valid AssetsDto assets) {

  public static class MediaDtoBuilder {
    MediaDtoBuilder() {
      markdown = "";
      assets = AssetsDto.builder().build();
    }
  }

  public static MediaDto from(TeamEntity teamEntity, AssetService assetService) {
    AssetsDto assets = assetService.getAssetsDto(teamEntity);
    return new MediaDto(teamEntity.getMarkdown(), assets);
  }

  /**
   * The media of {@code teamEntity}, or an empty shell when the caller asked for a compact list.
   *
   * @param view {@code null} or {@link ListViewMode#FULL} yields exactly what {@link
   *     #from(TeamEntity, AssetService)} always yielded
   */
  public static MediaDto from(
      TeamEntity teamEntity, AssetService assetService, @Nullable ListViewMode view) {
    return ListViewMode.isCompact(view) ? compact() : from(teamEntity, assetService);
  }

  /**
   * The empty media a compact list row carries.
   *
   * <p>Empty rather than absent: {@code markdown} and {@code assets} are required by the contract
   * and are also sent back in request bodies, so relaxing them to optional would have turned them
   * nullable in every generated client. A row that asked to be compact knows why its body is empty;
   * it reads {@code excerpt} and {@code thumbnailUrl}.
   */
  public static MediaDto compact() {
    return new MediaDto("", AssetsDto.builder().build());
  }
}
