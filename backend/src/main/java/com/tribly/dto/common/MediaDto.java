package com.tribly.dto.common;

import com.tribly.domain.common.TeamEntity;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Builder
public record MediaDto(@Nullable @Schema(description = "Markdown") String markdown) {
  public static MediaDto from(TeamEntity teamEntity) {
    return new MediaDto(teamEntity.getMarkdown());
  }
}
