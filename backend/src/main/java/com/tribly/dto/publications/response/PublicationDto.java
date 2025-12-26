package com.tribly.dto.publications.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tribly.domain.common.Publication;
import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.dto.rides.response.RideDto;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

// Response DTOs
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type" // discriminator
    )
@JsonSubTypes({
  @JsonSubTypes.Type(value = RideDto.class, name = "ride"),
  @JsonSubTypes.Type(value = PostDto.class, name = "post")
})
@Schema(
    description = "Publication data",
    discriminatorProperty = "type",
    discriminatorMapping = {
      @DiscriminatorMapping(value = "ride", schema = RideDto.class),
      @DiscriminatorMapping(value = "post", schema = PostDto.class)
    },
    oneOf = {RideDto.class, PostDto.class})
public interface PublicationDto {

  static PublicationDto from(Publication publication) {
    return switch (publication) {
      case Post post -> PostDto.from(post);
      case Ride ride -> RideDto.from(ride, false);
      default -> throw new IllegalStateException("Invalid Publication object");
    };
  }
}
