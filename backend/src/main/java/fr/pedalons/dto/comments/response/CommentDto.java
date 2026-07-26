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
    @Schema(description = "Comment content", required = true) String content,
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
        int replyCount) {

  public static CommentDto from(Comment comment, List<CommentDto> replies) {
    return from(comment, replies, replies.size());
  }

  /**
   * @param replyCount how many replies exist, which is not always how many {@code replies} carries —
   *     a thread can be answered without being embedded
   */
  public static CommentDto from(Comment comment, List<CommentDto> replies, int replyCount) {
    return new CommentDto(
        TsidUtils.toString(comment.getId()),
        comment.getContent(),
        PublicUserDto.from(comment.getCreatedBy()),
        comment.getCreatedAt(),
        comment.getParent() != null ? TsidUtils.toString(comment.getParent().getId()) : null,
        replies,
        replyCount);
  }
}
