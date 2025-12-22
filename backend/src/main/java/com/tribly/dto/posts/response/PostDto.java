package com.tribly.dto.posts.response;

import com.tribly.domain.post.Post;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// Response DTOs
@Schema(description = "Post summary data")
public record PostDto(
    @Schema(description = "Post ID (TSID)", required = true) String id,
    @Schema(description = "Post URL slug", required = true) String slug,
    @Schema(description = "Post name", required = true) String name,
    @Nullable @Schema(description = "Post description") String description,
    @Schema(description = "Post date/time", required = true) LocalDateTime dateTime,
    @Schema(description = "Post status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Publication timestamp") Instant publishAt,
    @Nullable @Schema(description = "Creation timestamp") Instant createdAt) {
  public static PostDto from(Post post) {
    return new PostDto(
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
