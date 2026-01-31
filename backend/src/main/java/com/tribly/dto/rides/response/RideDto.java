package com.tribly.dto.rides.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.place.Place;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.service.asset.AssetService;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// Response DTOs
@Schema(description = "Ride summary data")
@ValidateSchema
@Getter
public class RideDto implements PublicationDto {

  @Schema(description = "Type", required = true)
  final PublicationType type = PublicationType.RIDE;

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

  @Schema(description = "Publication date/time", required = true)
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

  @Schema(description = "Number of groups", required = true)
  final int groupCount;

  @Schema(description = "Ride groups", required = true)
  final List<RideGroupDto> groups;

  @Nullable
  @Schema(description = "Start place")
  final PlaceDetailDto startPlace;

  @Nullable
  @Schema(description = "End place")
  final PlaceDetailDto endPlace;

  @Schema(description = "Preview of first participants (max 5)", required = true)
  final List<PublicUserDto> topParticipants;

  @Nullable
  @Schema(description = "Route thumbnail URL (light)")
  final String routeThumbnailLightUrl;

  @Nullable
  @Schema(description = "Route thumbnail URL (dark)")
  final String routeThumbnailDarkUrl;

  public RideDto(
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
      int groupCount,
      List<RideGroupDto> groups,
      @Nullable PlaceDetailDto startPlace,
      @Nullable PlaceDetailDto endPlace,
      List<PublicUserDto> topParticipants,
      @Nullable String routeThumbnailLightUrl,
      @Nullable String routeThumbnailDarkUrl) {
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
    this.groupCount = groupCount;
    this.groups = groups;
    this.startPlace = startPlace;
    this.endPlace = endPlace;
    this.topParticipants = topParticipants;
    this.routeThumbnailLightUrl = routeThumbnailLightUrl;
    this.routeThumbnailDarkUrl = routeThumbnailDarkUrl;
  }

  public static RideDto from(Ride ride, boolean groupDetails, AssetService assetService) {
    List<RideGroupDto> groupDtos =
        groupDetails
            ? ride.getGroups().stream()
                .filter(g -> !g.isDeleted())
                .sorted(Comparator.comparing(RideGroup::getSortOrder))
                .map(RideGroupDto::from)
                .toList()
            : List.of();
    Place startPlace = ride.getStart();
    Place endPlace = ride.getEnd();

    // Extract top 5 unique participants across all groups
    Set<Long> seenUserIds = new HashSet<>();
    List<PublicUserDto> topParticipants =
        ride.getGroups().stream()
            .filter(g -> !g.isDeleted())
            .flatMap(g -> g.getParticipations().stream())
            .filter(p -> !p.isDeleted())
            .sorted(Comparator.comparing(RideParticipation::getRegisteredAt))
            .map(RideParticipation::getUser)
            .filter(user -> seenUserIds.add(user.getId()))
            .limit(5)
            .map(PublicUserDto::from)
            .toList();

    // Get route thumbnail URLs if route exists
    String routeThumbnailLightUrl = null;
    String routeThumbnailDarkUrl = null;
    if (ride.getRoute() != null) {
      for (var asset : ride.getRoute().getAssets()) {
        switch (asset.getType()) {
          case ROUTE_THUMBNAIL_LIGHT -> routeThumbnailLightUrl = assetService.getImageUrl(asset);
          case ROUTE_THUMBNAIL_DARK -> routeThumbnailDarkUrl = assetService.getImageUrl(asset);
          default -> {}
        }
      }
    }

    return new RideDto(
        TeamPublicationDto.from(ride.getTeam()),
        TsidUtils.toString(ride.getId()),
        ride.getSlug(),
        ride.getName(),
        MediaDto.from(ride, assetService),
        ride.getDateTime(),
        ride.getStatus(),
        ride.getVisibility(),
        ride.getPublishAt(),
        ride.getCreatedAt(),
        ride.getRoute() != null ? ride.getRoute().getSlug() : null,
        ride.getParticipantCount(),
        ride.getGroupCount(),
        groupDtos,
        startPlace != null ? PlaceDetailDto.from(startPlace) : null,
        endPlace != null ? PlaceDetailDto.from(endPlace) : null,
        topParticipants,
        routeThumbnailLightUrl,
        routeThumbnailDarkUrl);
  }
}
