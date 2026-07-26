package fr.pedalons.dto.trips.response;

import fr.pedalons.common.MarkdownExcerpt;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripParticipation;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.dto.comments.response.CommentCounts;
import fr.pedalons.dto.common.asset.MediaDto;
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
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
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

  @Nullable
  @Schema(
      description =
          "Plain-text opening of the markdown body, flattened (links become their label) and cut on"
              + " a word boundary at about 200 characters. Null when the body holds no text. Lets a"
              + " list row render its two lines without the body being sent at all — see the 'view'"
              + " parameter.")
  final String excerpt;

  @Schema(description = "Trip start date/time", required = true)
  final Instant dateTime;

  @Nullable
  @Schema(
      description =
          "Date of the last stage — the day the trip ends. Null when the trip has no stage, in"
              + " which case it lasts a day and dateTime is both ends.")
  final Instant endDate;

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

  @Nullable
  @Schema(
      description =
          "Distance in metres over every stage that has a route. Null when no stage has one — an"
              + " unrouted trip has no distance, which is not the same as a distance of zero.")
  final Float totalDistance;

  @Nullable
  @Schema(
      description =
          "Elevation gain in metres over every stage that has a route. Null when no stage has one.")
  final Float totalElevationGain;

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

  @Nullable
  @Schema(
      description =
          "The one thumbnail to show when the client does not theme its cards: the light variant if"
              + " there is one, else the dark one. Saves a compact row from carrying media.assets"
              + " just to find a picture.")
  final String thumbnailUrl;

  @Schema(description = "Whether the trip is soft-deleted", required = true)
  final boolean deleted;

  @Schema(
      description = "Whether the current user is registered for this trip. False if anonymous.",
      required = true)
  final boolean registered;

  @Nullable
  @Schema(
      description =
          "Number of comments, replies included. Absent when the caller may not read the comments"
              + " of this trip — comments are members-only, so an outsider is told nothing, not"
              + " even zero.")
  final Integer commentCount;

  public TripDto(
      TeamPublicationDto team,
      String id,
      String slug,
      String name,
      MediaDto media,
      @Nullable String excerpt,
      Instant dateTime,
      @Nullable Instant endDate,
      Status status,
      Visibility visibility,
      @Nullable Instant publishAt,
      @Nullable Instant createdAt,
      @Nullable String routeSlug,
      int participantCount,
      int stageCount,
      @Nullable Float totalDistance,
      @Nullable Float totalElevationGain,
      List<TripStageDto> stages,
      List<PublicUserDto> participants,
      @Nullable String thumbnailLightUrl,
      @Nullable String thumbnailDarkUrl,
      @Nullable String thumbnailUrl,
      boolean deleted,
      boolean registered,
      @Nullable Integer commentCount) {
    super();
    this.team = team;
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.media = media;
    this.excerpt = excerpt;
    this.dateTime = dateTime;
    this.endDate = endDate;
    this.status = status;
    this.visibility = visibility;
    this.publishAt = publishAt;
    this.createdAt = createdAt;
    this.routeSlug = routeSlug;
    this.participantCount = participantCount;
    this.stageCount = stageCount;
    this.totalDistance = totalDistance;
    this.totalElevationGain = totalElevationGain;
    this.stages = stages;
    this.participants = participants;
    this.thumbnailLightUrl = thumbnailLightUrl;
    this.thumbnailDarkUrl = thumbnailDarkUrl;
    this.thumbnailUrl = thumbnailUrl;
    this.deleted = deleted;
    this.registered = registered;
    this.commentCount = commentCount;
  }

  /**
   * Builds a list row without touching {@code trip.getStages()} or {@code trip.getParticipations()}.
   *
   * <p>A list row renders neither stages nor participants — only their counts — and {@link
   * TripListSummary} carries those, loaded in bulk for the whole page. Going through {@link
   * #from(Trip, boolean, AssetService)} here would load both collections of every trip on the page
   * just to call {@code size()} on them.
   */
  public static TripDto fromListItem(
      Trip trip, TripListSummary summary, AssetService assetService) {
    return fromListItem(trip, summary, assetService, UserParticipations.NONE, CommentCounts.NONE);
  }

  /**
   * @param participations the current user's registrations for this whole page, resolved in one
   *     query by {@code ParticipationLookup} — never one lookup per row
   */
  public static TripDto fromListItem(
      Trip trip,
      TripListSummary summary,
      AssetService assetService,
      UserParticipations participations,
      CommentCounts commentCounts) {
    return fromListItem(
        trip, summary, assetService, participations, commentCounts, ListViewMode.FULL);
  }

  /**
   * @param view {@link ListViewMode#COMPACT} leaves the markdown body and the asset inventory out of the
   *     row; {@code excerpt} and {@code thumbnailUrl} carry what it renders instead
   */
  public static TripDto fromListItem(
      Trip trip,
      TripListSummary summary,
      AssetService assetService,
      UserParticipations participations,
      CommentCounts commentCounts,
      @Nullable ListViewMode view) {
    return build(
        trip,
        List.of(),
        List.of(),
        summary.stageCount(),
        summary.participantCount(),
        summary.totalDistance(),
        summary.totalElevationGain(),
        summary.endDate(),
        assetService,
        participations.isRegisteredToTrip(trip.getId()),
        commentCounts.forEntity(trip.getId()),
        view);
  }

  public static TripDto from(Trip trip, boolean stageDetails, AssetService assetService) {
    return from(trip, stageDetails, assetService, UserParticipations.NONE, CommentCounts.NONE);
  }

  public static TripDto from(
      Trip trip,
      boolean stageDetails,
      AssetService assetService,
      UserParticipations participations,
      CommentCounts commentCounts) {
    List<TripStage> liveStages =
        trip.getStages().stream()
            .filter(s -> !s.isDeleted())
            .sorted(Comparator.comparing(TripStage::getSortOrder))
            .toList();

    List<TripStageDto> stageDtos =
        stageDetails
            ? IntStream.range(0, liveStages.size())
                .mapToObj(
                    i ->
                        TripStageDto.from(
                            liveStages.get(i), assetService, i + 1, liveStages.size()))
                .toList()
            : List.of();
    List<PublicUserDto> participantDtos =
        stageDetails
            ? trip.getParticipations().stream()
                .map(TripParticipation::getUser)
                .map(PublicUserDto::from)
                .toList()
            : List.of();

    return build(
        trip,
        stageDtos,
        participantDtos,
        trip.getStageCount(),
        trip.getParticipantCount(),
        totalOf(liveStages, Route::getDistance),
        totalOf(liveStages, Route::getElevationGain),
        endDateOf(liveStages),
        assetService,
        participations.isRegisteredToTrip(trip.getId()),
        commentCounts.forEntity(trip.getId()),
        ListViewMode.FULL);
  }

  /**
   * Sums one route figure over the stages that have a route.
   *
   * <p>Null rather than zero when no stage has a route: a trip nobody has drawn yet has no distance,
   * which a client must be able to tell from a distance of zero. The list path gets the same numbers
   * from {@link TripListSummary}, computed by the database for the whole page at once — this is the
   * detail path, where the stages are in hand already.
   */
  private static @Nullable Float totalOf(
      List<TripStage> stages, Function<Route, @Nullable Float> figure) {
    Float total = null;
    for (TripStage stage : stages) {
      Route route = stage.getRoute();
      if (route == null) {
        continue;
      }
      Float value = figure.apply(route);
      if (value != null) {
        total = total == null ? value : total + value;
      }
    }
    return total;
  }

  /** The date of the last stage, or null for a trip with no stage. */
  private static @Nullable Instant endDateOf(List<TripStage> stages) {
    return stages.stream().map(TripStage::getDateTime).max(Comparator.naturalOrder()).orElse(null);
  }

  private static TripDto build(
      Trip trip,
      List<TripStageDto> stageDtos,
      List<PublicUserDto> participantDtos,
      int stageCount,
      int participantCount,
      @Nullable Float totalDistance,
      @Nullable Float totalElevationGain,
      @Nullable Instant endDate,
      AssetService assetService,
      boolean registered,
      @Nullable Integer commentCount,
      @Nullable ListViewMode view) {
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
        MediaDto.from(trip, assetService, view),
        MarkdownExcerpt.of(trip.getMarkdown()),
        trip.getDateTime(),
        endDate,
        trip.getStatus(),
        trip.getVisibility(),
        trip.getPublishAt(),
        trip.getCreatedAt(),
        trip.getRoute() != null ? trip.getRoute().getSlug() : null,
        participantCount,
        stageCount,
        totalDistance,
        totalElevationGain,
        stageDtos,
        participantDtos,
        thumbnailLightUrl,
        thumbnailDarkUrl,
        thumbnailLightUrl != null ? thumbnailLightUrl : thumbnailDarkUrl,
        trip.isDeleted(),
        registered,
        commentCount);
  }
}
