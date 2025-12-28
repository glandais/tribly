package com.tribly.dto.common;

import com.tribly.domain.common.TeamEntity;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Builder
public record MediaDto(
    @Nullable @Schema(description = "Markdown") String markdown,
    @Schema(description = "Assets", required = true) List<AssetDto> assets) {
  public static MediaDto from(TeamEntity teamEntity) {
    List<AssetDto> assets = teamEntity.getAssets().stream().map(AssetDto::from).toList();
    return new MediaDto(teamEntity.getMarkdown(), assets);
  }
}
