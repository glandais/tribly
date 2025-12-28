package com.tribly.dto.common.response;

import com.tribly.domain.common.TeamEntity;
import com.tribly.service.asset.AssetService;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Builder
public record MediaDto(
    @Nullable @Schema(description = "Markdown") String markdown,
    @Schema(description = "Assets", required = true) AssetsDto assets) {
  public static MediaDto from(TeamEntity teamEntity, AssetService assetService) {
    AssetsDto assets = assetService.getAssetsDto(teamEntity);
    return new MediaDto(teamEntity.getMarkdown(), assets);
  }
}
