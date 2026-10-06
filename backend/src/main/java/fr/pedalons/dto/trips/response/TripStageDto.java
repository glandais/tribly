package fr.pedalons.dto.trips.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.dto.comments.response.CommentCounts;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.places.response.PlaceDetailDto;
import fr.pedalons.dto.routes.response.RouteDto;
import fr.pedalons.dto.tags.response.ContentTags;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.service.asset.AssetService;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Trip stage information")
@ValidateSchema
public record TripStageDto(
    @Schema(description = "Stage ID (TSID)", required = true) String id,
    @Schema(description = "Stage slug", required = true) String slug,
    @Schema(description = "Stage name", required = true) String name,
    @Schema(description = "Stage date/time", required = true) Instant dateTime,
    @Nullable @Schema(description = "Average speed in km/h") Float averageSpeed,
    @Nullable @Schema(description = "Route") RouteDto route,
    @Nullable @Schema(description = "Start place") PlaceDetailDto startPlace,
    @Nullable @Schema(description = "End place") PlaceDetailDto endPlace,
    @Schema(description = "Stage media", required = true) MediaDto media,
    @Schema(description = "Sort order", required = true) int sortOrder,
    @Schema(
            description =
                "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a"
                    + " stage header. Unlike sortOrder, which is a persisted rank that may have"
                    + " gaps, this is a rank a client can print.",
            required = true)
        int stageIndex,
    @Schema(
            description = "How many live stages the trip has — the '/ 5' of 'Day 2 / 5'.",
            required = true)
        int stageCount,
    @Nullable
        @Schema(
            description =
                "Number of comments on the stage's own thread. Absent when the caller cannot read"
                    + " comments (not a member of the team), like TripDto.commentCount.")
        Integer commentCount) {

  /**
   * @param routeTags the tags of the stage's route, resolved with the trip's own by {@code
   *     TagLookup} — never one lookup per stage
   * @param stageIndex 1-based position among the trip's live stages
   * @param stageCount how many live stages the trip has
   * @param commentCounts the stages' comment counts, resolved with the trip's own by {@code
   *     CommentCountLookup}
   */
  public static TripStageDto from(
      TripStage stage,
      AssetService assetService,
      ContentTags routeTags,
      CommentCounts commentCounts,
      int stageIndex,
      int stageCount) {
    return new TripStageDto(
        TsidUtils.toString(stage.getId()),
        stage.getSlug(),
        stage.getName(),
        stage.getDateTime(),
        stage.getAverageSpeed(),
        stage.getRoute() != null
            ? RouteDto.from(stage.getRoute(), assetService, CommentCounts.NONE, routeTags)
            : null,
        stage.getStartPlace() != null ? PlaceDetailDto.from(stage.getStartPlace()) : null,
        stage.getEndPlace() != null ? PlaceDetailDto.from(stage.getEndPlace()) : null,
        MediaDto.from(stage, assetService),
        stage.getSortOrder(),
        stageIndex,
        stageCount,
        commentCounts.forEntity(stage.getId()));
  }
}
