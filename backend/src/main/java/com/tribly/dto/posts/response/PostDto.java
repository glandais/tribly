package com.tribly.dto.posts.response;

import com.tribly.domain.post.Post;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import java.time.Instant;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// Response DTOs
@Schema(description = "Post summary data", allOf = PublicationDto.class)
@Getter
public class PostDto extends PublicationDto {

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

  @Nullable
  @Schema(description = "Publication description")
  final String description;

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
      @Nullable String description,
      Instant dateTime,
      Status status,
      Visibility visibility,
      @Nullable Instant publishAt,
      @Nullable Instant createdAt) {
    this.team = team;
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.description = description;
    this.dateTime = dateTime;
    this.status = status;
    this.visibility = visibility;
    this.publishAt = publishAt;
    this.createdAt = createdAt;
  }

  public static PostDto from(Post post) {
    return new PostDto(
        TeamPublicationDto.from(post.getTeam()),
        TsidUtils.toString(post.getId()),
        post.getSlug(),
        post.getName(),
        post.getDescription(),
        post.getDateTime(),
        post.getStatus(),
        post.getVisibility(),
        post.getPublishAt(),
        post.getCreatedAt());
  }
}
