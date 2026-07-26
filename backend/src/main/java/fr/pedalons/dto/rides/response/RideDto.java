package fr.pedalons.dto.rides.response;

import fr.pedalons.common.MarkdownExcerpt;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.dto.comments.response.CommentCounts;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.places.response.PlaceDetailDto;
import fr.pedalons.dto.publications.response.PublicationDto;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.publications.response.UserParticipations;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ListViewMode;
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

  @Nullable
  @Schema(
      description =
          "Plain-text opening of the markdown body, flattened (links become their label) and cut on"
              + " a word boundary at about 200 characters. Null when the body holds no text. Lets a"
              + " list row render its two lines without the body being sent at all — see the 'view'"
              + " parameter.")
  final String excerpt;

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

  @Nullable
  @Schema(
      description =
          "The one thumbnail to show when the client does not theme its cards: the light variant if"
              + " there is one, else the dark one. Saves a compact row from carrying"
              + " media.assets just to find a picture.")
  final String thumbnailUrl;

  @Schema(description = "Whether the ride is soft-deleted", required = true)
  final boolean deleted;

  @Schema(
      description =
          "Whether the current user is registered in one of this ride's groups. False if"
              + " anonymous.",
      required = true)
  final boolean registered;

  @Nullable
  @Schema(description = "ID (TSID) of the group the current user joined, null if not registered")
  final String registeredGroupId;

  @Schema(
      description =
          "Whether every group of the ride has reached its capacity. False when the ride has no"
              + " group, or when at least one group has no maxParticipants.",
      required = true)
  final boolean full;

  @Nullable
  @Schema(
      description =
          "Number of comments, replies included. Absent when the caller may not read the comments"
              + " of this ride — comments are members-only, so an outsider is told nothing, not"
              + " even zero.")
  final Integer commentCount;

  public RideDto(
      TeamPublicationDto team,
      String id,
      String slug,
      String name,
      MediaDto media,
      @Nullable String excerpt,
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
      @Nullable String thumbnailUrl,
      boolean deleted,
      boolean registered,
      @Nullable String registeredGroupId,
      boolean full,
      @Nullable Integer commentCount) {
    super();
    this.team = team;
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.media = media;
    this.excerpt = excerpt;
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
    this.thumbnailUrl = thumbnailUrl;
    this.deleted = deleted;
    this.registered = registered;
    this.registeredGroupId = registeredGroupId;
    this.full = full;
    this.commentCount = commentCount;
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
    return fromListItem(ride, summary, assetService, UserParticipations.NONE, CommentCounts.NONE);
  }

  /**
   * @param participations the current user's registrations for this whole page, resolved in one
   *     query by {@code ParticipationLookup} — never one lookup per row
   */
  public static RideDto fromListItem(
      Ride ride,
      RideListSummary summary,
      AssetService assetService,
      UserParticipations participations,
      CommentCounts commentCounts) {
    return fromListItem(
        ride, summary, assetService, participations, commentCounts, ListViewMode.FULL);
  }

  /**
   * @param view {@link ListViewMode#COMPACT} leaves the markdown body and the asset inventory out of the
   *     row; {@code excerpt} and {@code thumbnailUrl} carry what it renders instead
   */
  public static RideDto fromListItem(
      Ride ride,
      RideListSummary summary,
      AssetService assetService,
      UserParticipations participations,
      CommentCounts commentCounts,
      @Nullable ListViewMode view) {
    return build(
        ride,
        List.of(),
        summary.groupCount(),
        summary.participantCount(),
        summary.topParticipants(),
        assetService,
        participations.registeredGroupId(ride.getId()),
        summary.full(),
        commentCounts.forEntity(ride.getId()),
        view);
  }

  public static RideDto from(Ride ride, boolean groupDetails, AssetService assetService) {
    return from(ride, groupDetails, assetService, UserParticipations.NONE, CommentCounts.NONE);
  }

  public static RideDto from(
      Ride ride,
      boolean groupDetails,
      AssetService assetService,
      UserParticipations participations,
      CommentCounts commentCounts) {
    Long registeredGroupId = participations.registeredGroupId(ride.getId());

    List<RideGroupDto> groupDtos =
        groupDetails
            ? ride.getGroups().stream()
                .sorted(Comparator.comparing(RideGroup::getSortOrder))
                .map(group -> RideGroupDto.from(group, registeredGroupId))
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

    // The detail path already holds the groups, so "every group is at capacity" is a fold, not a
    // query. A ride with no group is not full.
    List<RideGroup> groups = ride.getGroups();
    boolean full = !groups.isEmpty() && groups.stream().noneMatch(RideGroup::hasCapacity);

    return build(
        ride,
        groupDtos,
        ride.getGroupCount(),
        ride.getParticipantCount(),
        topParticipants,
        assetService,
        registeredGroupId,
        full,
        commentCounts.forEntity(ride.getId()),
        ListViewMode.FULL);
  }

  private static RideDto build(
      Ride ride,
      List<RideGroupDto> groupDtos,
      int groupCount,
      int participantCount,
      List<PublicUserDto> topParticipants,
      AssetService assetService,
      @Nullable Long registeredGroupId,
      boolean full,
      @Nullable Integer commentCount,
      @Nullable ListViewMode view) {
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
        MediaDto.from(ride, assetService, view),
        MarkdownExcerpt.of(ride.getMarkdown()),
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
        thumbnailLightUrl != null ? thumbnailLightUrl : thumbnailDarkUrl,
        ride.isDeleted(),
        registeredGroupId != null,
        registeredGroupId != null ? TsidUtils.toString(registeredGroupId) : null,
        full,
        commentCount);
  }
}
