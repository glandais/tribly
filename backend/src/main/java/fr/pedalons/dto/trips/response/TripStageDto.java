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
    @Schema(description = "Sort order", required = true) int sortOrder) {

  public static TripStageDto from(TripStage stage, AssetService assetService) {
    return new TripStageDto(
        TsidUtils.toString(stage.getId()),
        stage.getSlug(),
        stage.getName(),
        stage.getDateTime(),
        stage.getRoute() != null ? RouteDto.from(stage.getRoute(), assetService) : null,
        stage.getStartPlace() != null ? PlaceDetailDto.from(stage.getStartPlace()) : null,
        stage.getEndPlace() != null ? PlaceDetailDto.from(stage.getEndPlace()) : null,
        MediaDto.from(stage, assetService),
        stage.getSortOrder());
  }
}
