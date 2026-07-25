package fr.pedalons.dto.publications.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.pedalons.domain.common.Publication;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.dto.posts.response.PostDto;
import fr.pedalons.dto.rides.response.RideDto;
import fr.pedalons.dto.rides.response.RideListSummary;
import fr.pedalons.dto.trips.response.TripDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.asset.AssetService;
import java.util.Map;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
    return from(publication, assetService, Map.of());
  }

  /**
   * Builds one row of a publication list.
   *
   * @param rideSummaries group/participant counts for every ride on the page, loaded in bulk by
   *     {@code RideSummaryRepository}. A ride missing from the map has no groups.
   */
  static PublicationDto from(
      Publication publication,
      AssetService assetService,
      Map<Long, RideListSummary> rideSummaries) {
    return switch (publication) {
      case Post post -> PostDto.from(post, assetService);
      case Ride ride ->
          RideDto.fromListItem(
              ride, rideSummaries.getOrDefault(ride.getId(), RideListSummary.EMPTY), assetService);
      case Trip trip -> TripDto.from(trip, false, assetService);
      default -> throw new IllegalStateException("Invalid Publication object");
    };
  }
}
