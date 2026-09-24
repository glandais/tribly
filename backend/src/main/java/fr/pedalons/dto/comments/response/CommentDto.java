package fr.pedalons.dto.comments.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Comment data")
@ValidateSchema
public record CommentDto(
    @Schema(description = "Comment ID (TSID)", required = true) String id,
    @Schema(
            description =
                "Comment content. Empty when the comment is deleted — see the deleted flag.",
            required = true)
        String content,
    @Schema(description = "Comment author", required = true) PublicUserDto author,
    @Schema(description = "Creation timestamp", required = true) Instant createdAt,
    @Nullable @Schema(description = "Parent comment ID (for replies)") String parentId,
    @Schema(description = "Replies to this comment", required = true) List<CommentDto> replies,
    @Schema(
            description =
                "How many replies this comment has. Equal to replies.size() when the whole thread"
                    + " is embedded; a client that loads threads on demand uses it to decide"
                    + " whether ?parentId= is worth a call. Always 0 on a reply — threading is one"
                    + " level deep.",
            required = true)
        int replyCount,
    @Schema(
            description =
                "True for the comment of a deleted account that others had answered. It stays"
                    + " only to carry its replies: the content is empty, and clients render a"
                    + " placeholder with neither author nor actions.",
            required = true)
        boolean deleted) {

  /**
   * A comment masked for this reader — its author blocked, reported by the reader, or hidden by
   * reports — kept only to carry replies the reader may still see. Rendered like the tombstone of an
   * erased account: {@code deleted}, empty content, no actions.
   */
  public static CommentDto masked(Comment comment, List<CommentDto> replies) {
    return new CommentDto(
        TsidUtils.toString(comment.getId()),
        "",
        PublicUserDto.from(comment.getCreatedBy()),
        comment.getCreatedAt(),
        comment.getParent() != null ? TsidUtils.toString(comment.getParent().getId()) : null,
        replies,
        replies.size(),
        true);
  }

  public static CommentDto from(Comment comment, List<CommentDto> replies) {
    return from(comment, replies, replies.size());
  }

  /**
   * @param replyCount how many replies exist, which is not always how many {@code replies} carries —
   *     a thread can be answered without being embedded
   */
  public static CommentDto from(Comment comment, List<CommentDto> replies, int replyCount) {
    // An erased account's surviving comments are exactly the tombstones: the erasure deletes the
    // others. Checking the author also covers an account flagged deleted but not yet erased.
    boolean deleted = comment.getCreatedBy().isDeleted();
    return new CommentDto(
        TsidUtils.toString(comment.getId()),
        deleted ? "" : comment.getContent(),
        PublicUserDto.from(comment.getCreatedBy()),
        comment.getCreatedAt(),
        comment.getParent() != null ? TsidUtils.toString(comment.getParent().getId()) : null,
        replies,
        replyCount,
        deleted);
  }
}
