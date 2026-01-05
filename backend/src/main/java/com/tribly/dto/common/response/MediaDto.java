package com.tribly.dto.common.response;

import com.tribly.domain.common.TeamEntity;
import com.tribly.service.asset.AssetService;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Builder
public record MediaDto(
    @Schema(description = "Markdown", required = true) String markdown,
    @Schema(description = "Assets", required = true) AssetsDto assets) {
  public static MediaDto from(TeamEntity teamEntity, AssetService assetService) {
    AssetsDto assets = assetService.getAssetsDto(teamEntity);
    return new MediaDto(teamEntity.getMarkdown(), assets);
  }
}
