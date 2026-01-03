package com.tribly.dto.pages.response;

import com.tribly.domain.team.TeamPage;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Team page summary for listings")
@ValidateSchema
public record TeamPageSummaryDto(
    @Schema(description = "Page ID (TSID)", required = true) String id,
    @Schema(description = "Page title", required = true) String title,
    @Schema(description = "Page URL slug", required = true) String slug,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Schema(description = "Page order", required = true) int order) {

  public static TeamPageSummaryDto from(TeamPage page) {
    return new TeamPageSummaryDto(
        TsidUtils.toString(page.getId()),
        page.getName(),
        page.getSlug(),
        page.getVisibility(),
        page.getPageOrder() != null ? page.getPageOrder() : 0);
  }
}
