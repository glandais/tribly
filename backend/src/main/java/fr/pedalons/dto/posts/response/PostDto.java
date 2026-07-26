package fr.pedalons.dto.posts.response;

import fr.pedalons.common.MarkdownExcerpt;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.post.Post;
import fr.pedalons.dto.comments.response.CommentCounts;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.publications.response.PublicationDto;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ListViewMode;
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

  @Nullable
  @Schema(
      description =
          "Plain-text opening of the markdown body, flattened (links become their label) and cut on"
              + " a word boundary at about 200 characters. Null when the body holds no text. Lets a"
              + " list row render its two lines without the body being sent at all — see the 'view'"
              + " parameter.")
  final String excerpt;

  @Nullable
  @Schema(
      description =
          "URL template of the post's first image, the one a card shows. Saves a compact row from"
              + " carrying media.assets just to find a picture.")
  final String thumbnailUrl;

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

  @Schema(description = "Whether the post is soft-deleted", required = true)
  final boolean deleted;

  @Nullable
  @Schema(
      description =
          "Number of comments, replies included. Absent when the caller may not read the comments"
              + " of this post — comments are members-only, so an outsider is told nothing, not"
              + " even zero.")
  final Integer commentCount;

  public PostDto(
      TeamPublicationDto team,
      String id,
      String slug,
      String name,
      MediaDto media,
      @Nullable String excerpt,
      @Nullable String thumbnailUrl,
      Instant dateTime,
      Status status,
      Visibility visibility,
      @Nullable Instant publishAt,
      @Nullable Instant createdAt,
      boolean deleted,
      @Nullable Integer commentCount) {
    super();
    this.team = team;
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.media = media;
    this.excerpt = excerpt;
    this.thumbnailUrl = thumbnailUrl;
    this.dateTime = dateTime;
    this.status = status;
    this.visibility = visibility;
    this.publishAt = publishAt;
    this.createdAt = createdAt;
    this.deleted = deleted;
    this.commentCount = commentCount;
  }

  public static PostDto from(Post post, AssetService assetService) {
    return from(post, assetService, CommentCounts.NONE);
  }

  public static PostDto from(Post post, AssetService assetService, CommentCounts commentCounts) {
    return from(post, assetService, commentCounts, ListViewMode.FULL);
  }

  /**
   * @param view {@link ListViewMode#COMPACT} leaves the markdown body and the asset inventory out of the
   *     row; {@code excerpt} and {@code thumbnailUrl} carry what it renders instead
   */
  public static PostDto from(
      Post post,
      AssetService assetService,
      CommentCounts commentCounts,
      @Nullable ListViewMode view) {
    return new PostDto(
        TeamPublicationDto.from(post.getTeam()),
        TsidUtils.toString(post.getId()),
        post.getSlug(),
        post.getName(),
        MediaDto.from(post, assetService, view),
        MarkdownExcerpt.of(post.getMarkdown()),
        assetService.getFirstImageUrl(post),
        post.getDateTime(),
        post.getStatus(),
        post.getVisibility(),
        post.getPublishAt(),
        post.getCreatedAt(),
        post.isDeleted(),
        commentCounts.forEntity(post.getId()));
  }
}
