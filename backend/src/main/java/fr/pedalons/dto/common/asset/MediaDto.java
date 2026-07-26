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
   * The media of {@code teamEntity}, trimmed to what a list row draws when the caller asked for a
   * compact list.
   *
   * @param view {@code null} or {@link ListViewMode#FULL} yields exactly what {@link
   *     #from(TeamEntity, AssetService)} always yielded
   */
  public static MediaDto from(
      TeamEntity teamEntity, AssetService assetService, @Nullable ListViewMode view) {
    return ListViewMode.isCompact(view)
        ? compact(assetService.getAssetsDto(teamEntity))
        : from(teamEntity, assetService);
  }

  /**
   * The media a compact list row carries: no markdown, and only the few small assets a card
   * actually draws — the logo, the first image and the two themed thumbnails.
   *
   * <p>Empty rather than absent: {@code markdown} and {@code assets} are required by the contract
   * and are also sent back in request bodies, so relaxing them to optional would have turned them
   * nullable in every generated client.
   *
   * <p>They are kept rather than dropped because no list DTO hoists them: {@code thumbnailUrl} is
   * the map preview on a ride or a trip, not the header photo; {@code RouteDto} collapses its two
   * themed thumbnails into a single light-else-dark URL, so a card that themes its own picture
   * cannot rebuild the dark one; and no {@code logoUrl} exists anywhere outside {@code
   * TeamDetailDto}. An inventory emptied wholesale left a compact card unable to draw its own logo,
   * which made the mode unusable for the very lists it was built for.
   *
   * <p>What compact still drops is all the bulk: the markdown body, the attachments, the GPX and FIT
   * files, and every image past the first. The rule is that a compact row keeps the handful of small
   * URLs it renders and nothing else.
   */
  public static MediaDto compact(AssetsDto assets) {
    return new MediaDto(
        "",
        AssetsDto.builder()
            .logo(assets.logo())
            .images(assets.images().stream().limit(1).toList())
            .thumbnailLight(assets.thumbnailLight())
            .thumbnailDark(assets.thumbnailDark())
            .build());
  }
}
