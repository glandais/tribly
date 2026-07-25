package fr.pedalons.dto.rides.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.places.response.PlaceDetailDto;
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
  @Schema(description = "Thumbnail URL (light)")
  final String thumbnailLightUrl;

  @Nullable
  @Schema(description = "Thumbnail URL (dark)")
  final String thumbnailDarkUrl;

  @Schema(description = "Whether the ride is soft-deleted", required = true)
  final boolean deleted;

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
      @Nullable String thumbnailLightUrl,
      @Nullable String thumbnailDarkUrl,
      boolean deleted) {
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
    this.thumbnailLightUrl = thumbnailLightUrl;
    this.thumbnailDarkUrl = thumbnailDarkUrl;
    this.deleted = deleted;
  }

  /**
   * Builds a list row without touching {@code ride.getGroups()}.
   *
   * <p>The group/participant numbers come from {@link RideListSummary}, which the caller loaded in
   * bulk for the whole page. Going through {@link #from(Ride, boolean, AssetService)} here would
   * hydrate every participation and every participant of every ride on the page just to count them
   * and keep five.
   */
  public static RideDto fromListItem(
      Ride ride, RideListSummary summary, AssetService assetService) {
    return build(
        ride,
        List.of(),
        summary.groupCount(),
        summary.participantCount(),
        summary.topParticipants(),
        assetService);
  }

  public static RideDto from(Ride ride, boolean groupDetails, AssetService assetService) {
    List<RideGroupDto> groupDtos =
        groupDetails
            ? ride.getGroups().stream()
                .sorted(Comparator.comparing(RideGroup::getSortOrder))
                .map(RideGroupDto::from)
                .toList()
            : List.of();

    // Extract top 5 unique participants across all groups
    Set<Long> seenUserIds = new HashSet<>();
    List<PublicUserDto> topParticipants =
        ride.getGroups().stream()
            .flatMap(g -> g.getParticipations().stream())
            .sorted(Comparator.comparing(RideParticipation::getRegisteredAt))
            .map(RideParticipation::getUser)
            .filter(user -> seenUserIds.add(user.getId()))
            .limit(5)
            .map(PublicUserDto::from)
            .toList();

    return build(
        ride,
        groupDtos,
        ride.getGroupCount(),
        ride.getParticipantCount(),
        topParticipants,
        assetService);
  }

  private static RideDto build(
      Ride ride,
      List<RideGroupDto> groupDtos,
      int groupCount,
      int participantCount,
      List<PublicUserDto> topParticipants,
      AssetService assetService) {
    Place startPlace = ride.getStart();
    Place endPlace = ride.getEnd();

    // Get thumbnail URLs from ride's own assets
    String thumbnailLightUrl = null;
    String thumbnailDarkUrl = null;
    for (var asset : ride.getAssets()) {
      switch (asset.getType()) {
        case RIDE_THUMBNAIL_LIGHT -> thumbnailLightUrl = assetService.getImageUrl(asset);
        case RIDE_THUMBNAIL_DARK -> thumbnailDarkUrl = assetService.getImageUrl(asset);
        default -> {}
      }
    }
    // Fallback to route thumbnail if ride has no own thumbnails
    if (thumbnailLightUrl == null && thumbnailDarkUrl == null && ride.getRoute() != null) {
      for (var asset : ride.getRoute().getAssets()) {
        switch (asset.getType()) {
          case ROUTE_THUMBNAIL_LIGHT -> thumbnailLightUrl = assetService.getImageUrl(asset);
          case ROUTE_THUMBNAIL_DARK -> thumbnailDarkUrl = assetService.getImageUrl(asset);
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
        participantCount,
        groupCount,
        groupDtos,
        startPlace != null ? PlaceDetailDto.from(startPlace) : null,
        endPlace != null ? PlaceDetailDto.from(endPlace) : null,
        topParticipants,
        thumbnailLightUrl,
        thumbnailDarkUrl,
        ride.isDeleted());
  }
}
