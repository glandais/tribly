package com.tribly.dto.pages.response;

import com.tribly.domain.team.TeamPage;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.AssetService;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Team page detail")
@ValidateSchema
public record TeamPageDto(
    @Schema(description = "Team", required = true) TeamPublicationDto team,
    @Schema(description = "Page ID (TSID)", required = true) String id,
    @Schema(description = "Page title", required = true) String title,
    @Schema(description = "Page URL slug", required = true) String slug,
    @Schema(description = "Page content", required = true) MediaDto media,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Schema(description = "Page order", required = true) int order) {

  public static TeamPageDto from(TeamPage page, AssetService assetService) {
    return new TeamPageDto(
        TeamPublicationDto.from(page.getTeam()),
        TsidUtils.toString(page.getId()),
        page.getName(),
        page.getSlug(),
        MediaDto.from(page, assetService),
        page.getVisibility(),
        page.getPageOrder() != null ? page.getPageOrder() : 0);
  }
}
