package fr.pedalons.dto.trips.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.places.response.PlaceDetailDto;
import fr.pedalons.dto.routes.response.RouteDto;
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
        int stageCount) {

  /**
   * A stage read on its own, with no trip around it to number it.
   *
   * <p>{@code stageIndex} and {@code stageCount} both come back as 1: a lone stage is the first of
   * one.
   */
  public static TripStageDto from(TripStage stage, AssetService assetService) {
    return from(stage, assetService, 1, 1);
  }

  /**
   * @param stageIndex 1-based position among the trip's live stages
   * @param stageCount how many live stages the trip has
   */
  public static TripStageDto from(
      TripStage stage, AssetService assetService, int stageIndex, int stageCount) {
    return new TripStageDto(
        TsidUtils.toString(stage.getId()),
        stage.getSlug(),
        stage.getName(),
        stage.getDateTime(),
        stage.getRoute() != null ? RouteDto.from(stage.getRoute(), assetService) : null,
        stage.getStartPlace() != null ? PlaceDetailDto.from(stage.getStartPlace()) : null,
        stage.getEndPlace() != null ? PlaceDetailDto.from(stage.getEndPlace()) : null,
        MediaDto.from(stage, assetService),
        stage.getSortOrder(),
        stageIndex,
        stageCount);
  }
}
