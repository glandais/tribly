package fr.pedalons.dto.trips.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripParticipation;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.publications.response.PublicationDto;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.asset.AssetService;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Trip data")
@ValidateSchema
@Getter
public class TripDto implements PublicationDto {

  @Schema(description = "Type", required = true)
  final PublicationType type = PublicationType.TRIP;

  @Schema(description = "Team", required = true)
  final TeamPublicationDto team;

  @Schema(description = "Publication ID (TSID)", required = true)
  final String id;

  @Schema(description = "Publication URL slug", required = true)
  final String slug;

  @Schema(description = "Publication name", required = true)
  final String name;

  @Schema(description = "Publication media", required = true)
  final MediaDto media;

  @Schema(description = "Trip start date/time", required = true)
  final Instant dateTime;

  @Schema(description = "Publication status", required = true)
  final Status status;

  @Schema(description = "Visibility level", required = true)
  final Visibility visibility;

  @Nullable
  @Schema(description = "Publication timestamp")
  final Instant publishAt;

  @Nullable
  @Schema(description = "Creation timestamp")
  final Instant createdAt;

  @Nullable
  @Schema(description = "Route slug")
  final String routeSlug;

  @Schema(description = "Number of participants", required = true)
  final int participantCount;

  @Schema(description = "Number of stages", required = true)
  final int stageCount;

  @Schema(description = "Trip stages", required = true)
  final List<TripStageDto> stages;

  @Schema(description = "Trip participants", required = true)
  final List<PublicUserDto> participants;

  @Nullable
  @Schema(description = "Thumbnail URL (light)")
  final String thumbnailLightUrl;

  @Nullable
  @Schema(description = "Thumbnail URL (dark)")
  final String thumbnailDarkUrl;

  public TripDto(
      TeamPublicationDto team,
      String id,
      String slug,
      String name,
      MediaDto media,
      Instant dateTime,
      Status status,
      Visibility visibility,
      @Nullable Instant publishAt,
      @Nullable Instant createdAt,
      @Nullable String routeSlug,
      int participantCount,
      int stageCount,
      List<TripStageDto> stages,
      List<PublicUserDto> participants,
      @Nullable String thumbnailLightUrl,
      @Nullable String thumbnailDarkUrl) {
    super();
    this.team = team;
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.media = media;
    this.dateTime = dateTime;
    this.status = status;
    this.visibility = visibility;
    this.publishAt = publishAt;
    this.createdAt = createdAt;
    this.routeSlug = routeSlug;
    this.participantCount = participantCount;
    this.stageCount = stageCount;
    this.stages = stages;
    this.participants = participants;
    this.thumbnailLightUrl = thumbnailLightUrl;
    this.thumbnailDarkUrl = thumbnailDarkUrl;
  }

  public static TripDto from(Trip trip, boolean stageDetails, AssetService assetService) {
    List<TripStageDto> stageDtos =
        stageDetails
            ? trip.getStages().stream()
                .filter(s -> !s.isDeleted())
                .sorted(Comparator.comparing(TripStage::getSortOrder))
                .map(s -> TripStageDto.from(s, assetService))
                .toList()
            : List.of();
    List<PublicUserDto> participantDtos =
        stageDetails
            ? trip.getParticipations().stream()
                .map(TripParticipation::getUser)
                .map(PublicUserDto::from)
                .toList()
            : List.of();

    // Get thumbnail URLs from trip's own assets
    String thumbnailLightUrl = null;
    String thumbnailDarkUrl = null;
    for (var asset : trip.getAssets()) {
      switch (asset.getType()) {
        case TRIP_THUMBNAIL_LIGHT -> thumbnailLightUrl = assetService.getImageUrl(asset);
        case TRIP_THUMBNAIL_DARK -> thumbnailDarkUrl = assetService.getImageUrl(asset);
        default -> {}
      }
    }
    // Fallback to route thumbnail if trip has no own thumbnails
    if (thumbnailLightUrl == null && thumbnailDarkUrl == null && trip.getRoute() != null) {
      for (var asset : trip.getRoute().getAssets()) {
        switch (asset.getType()) {
          case ROUTE_THUMBNAIL_LIGHT -> thumbnailLightUrl = assetService.getImageUrl(asset);
          case ROUTE_THUMBNAIL_DARK -> thumbnailDarkUrl = assetService.getImageUrl(asset);
          default -> {}
        }
      }
    }

    return new TripDto(
        TeamPublicationDto.from(trip.getTeam()),
        TsidUtils.toString(trip.getId()),
        trip.getSlug(),
        trip.getName(),
        MediaDto.from(trip, assetService),
        trip.getDateTime(),
        trip.getStatus(),
        trip.getVisibility(),
        trip.getPublishAt(),
        trip.getCreatedAt(),
        trip.getRoute() != null ? trip.getRoute().getSlug() : null,
        trip.getParticipantCount(),
        trip.getStageCount(),
        stageDtos,
        participantDtos,
        thumbnailLightUrl,
        thumbnailDarkUrl);
  }
}
