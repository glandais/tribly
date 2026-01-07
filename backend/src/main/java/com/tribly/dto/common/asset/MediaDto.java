package com.tribly.dto.common.asset;

import com.tribly.domain.common.TeamEntity;
import com.tribly.service.asset.AssetService;
import jakarta.validation.Valid;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
}
