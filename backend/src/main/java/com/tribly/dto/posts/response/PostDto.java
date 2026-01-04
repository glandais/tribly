package com.tribly.dto.posts.response;

import com.tribly.domain.post.Post;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.AssetService;
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
