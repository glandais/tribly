package com.tribly.dto.publications.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tribly.domain.common.Publication;
import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.dto.rides.response.RideDto;
import com.tribly.service.asset.AssetService;
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
  @JsonSubTypes.Type(value = PostDto.class, name = "POST")
})
@Schema(
    description = "Publication data",
    discriminatorProperty = "type",
    discriminatorMapping = {
      @DiscriminatorMapping(value = "RIDE", schema = RideDto.class),
      @DiscriminatorMapping(value = "POST", schema = PostDto.class)
    },
    oneOf = {RideDto.class, PostDto.class})
public interface PublicationDto {

  PublicationType getType();

  static PublicationDto from(Publication publication, AssetService assetService) {
    return switch (publication) {
      case Post post -> PostDto.from(post, assetService);
      case Ride ride -> RideDto.from(ride, false, assetService);
      default -> throw new IllegalStateException("Invalid Publication object");
    };
  }
}
