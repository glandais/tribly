package fr.pedalons.dto.posts.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.post.Post;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.publications.response.PublicationDto;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.asset.AssetService;
import java.time.Instant;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// Response DTOs
@Schema(description = "Post summary data")
@ValidateSchema
@Getter
public class PostDto implements PublicationDto {

  @Schema(description = "Type", required = true)
  final PublicationType type = PublicationType.POST;

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

  public PostDto(
      TeamPublicationDto team,
      String id,
      String slug,
      String name,
      MediaDto media,
      Instant dateTime,
      Status status,
      Visibility visibility,
      @Nullable Instant publishAt,
      @Nullable Instant createdAt) {
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
  }

  public static PostDto from(Post post, AssetService assetService) {
    return new PostDto(
        TeamPublicationDto.from(post.getTeam()),
        TsidUtils.toString(post.getId()),
        post.getSlug(),
        post.getName(),
        MediaDto.from(post, assetService),
        post.getDateTime(),
        post.getStatus(),
        post.getVisibility(),
        post.getPublishAt(),
        post.getCreatedAt());
  }
}
