package fr.pedalons.dto.publications.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.pedalons.domain.common.Publication;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.dto.comments.response.CommentCounts;
import fr.pedalons.dto.posts.response.PostDto;
import fr.pedalons.dto.rides.response.RideDto;
import fr.pedalons.dto.trips.response.TripDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ListViewMode;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.asset.AssetService;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// Response DTOs
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type" // discriminator
    )
@JsonSubTypes({
  @JsonSubTypes.Type(value = RideDto.class, name = "RIDE"),
  @JsonSubTypes.Type(value = PostDto.class, name = "POST"),
  @JsonSubTypes.Type(value = TripDto.class, name = "TRIP")
})
@Schema(
    description = "Publication data",
    discriminatorProperty = "type",
    discriminatorMapping = {
      @DiscriminatorMapping(value = "RIDE", schema = RideDto.class),
      @DiscriminatorMapping(value = "POST", schema = PostDto.class),
      @DiscriminatorMapping(value = "TRIP", schema = TripDto.class)
    },
    oneOf = {RideDto.class, PostDto.class, TripDto.class})
@ValidateSchema
public interface PublicationDto {

  PublicationType getType();

  Visibility getVisibility();

  String getName();

  static PublicationDto from(Publication publication, AssetService assetService) {
    return from(
        publication,
        assetService,
        PublicationListSummaries.EMPTY,
        UserParticipations.NONE,
        CommentCounts.NONE);
  }

  static PublicationDto from(
      Publication publication, AssetService assetService, PublicationListSummaries summaries) {
    return from(publication, assetService, summaries, UserParticipations.NONE, CommentCounts.NONE);
  }

  /**
   * Builds one row of a publication list.
   *
   * @param summaries the association aggregates for this page, loaded in bulk so no row has to walk
   *     an association it only needs a count of
   * @param participations what the current user is registered to on this page, likewise loaded in
   *     bulk — the "me" fields must not cost a query per row
   * @param commentCounts the comment count of each row the caller may read, also loaded in bulk; a
   *     row absent from it renders no {@code commentCount} at all
   */
  static PublicationDto from(
      Publication publication,
      AssetService assetService,
      PublicationListSummaries summaries,
      UserParticipations participations,
      CommentCounts commentCounts) {
    return from(
        publication, assetService, summaries, participations, commentCounts, ListViewMode.FULL);
  }

  /**
   * Builds one row of a publication list at the requested level of detail.
   *
   * @param view {@link ListViewMode#COMPACT} leaves the markdown body and the asset inventory out of
   *     every row — a feed of twenty publications stops carrying twenty full articles for forty
   *     rendered lines. {@code excerpt} and {@code thumbnailUrl} are what a compact row reads, and
   *     they are present either way.
   */
  static PublicationDto from(
      Publication publication,
      AssetService assetService,
      PublicationListSummaries summaries,
      UserParticipations participations,
      CommentCounts commentCounts,
      @Nullable ListViewMode view) {
    return switch (publication) {
      case Post post -> PostDto.from(post, assetService, commentCounts, view);
      case Ride ride ->
          RideDto.fromListItem(
              ride,
              summaries.ride(ride.getId()),
              assetService,
              participations,
              commentCounts,
              view);
      case Trip trip ->
          TripDto.fromListItem(
              trip,
              summaries.trip(trip.getId()),
              assetService,
              participations,
              commentCounts,
              view);
      default -> throw new IllegalStateException("Invalid Publication object");
    };
  }
}
