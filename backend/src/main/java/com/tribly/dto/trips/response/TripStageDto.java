package com.tribly.dto.trips.response;

import com.tribly.domain.trip.TripStage;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.AssetService;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Trip stage information")
@ValidateSchema
public record TripStageDto(
    @Schema(description = "Stage ID (TSID)", required = true) String id,
    @Schema(description = "Stage name", required = true) String name,
    @Schema(description = "Stage date/time", required = true) Instant dateTime,
    @Nullable @Schema(description = "Route slug") String routeSlug,
    @Nullable @Schema(description = "Start place") PlaceDetailDto startPlace,
    @Nullable @Schema(description = "End place") PlaceDetailDto endPlace,
    @Schema(description = "Stage media", required = true) MediaDto media,
    @Schema(description = "Sort order", required = true) int sortOrder) {

  public static TripStageDto from(TripStage stage, AssetService assetService) {
    return new TripStageDto(
        TsidUtils.toString(stage.getId()),
        stage.getName(),
        stage.getDateTime(),
        stage.getRoute() != null ? stage.getRoute().getSlug() : null,
        stage.getStartPlace() != null ? PlaceDetailDto.from(stage.getStartPlace()) : null,
        stage.getEndPlace() != null ? PlaceDetailDto.from(stage.getEndPlace()) : null,
        MediaDto.from(stage, assetService),
        stage.getSortOrder());
  }
}
