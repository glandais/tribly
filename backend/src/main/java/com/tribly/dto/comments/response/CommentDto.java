package com.tribly.dto.comments.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.comment.Comment;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.validation.ValidateSchema;
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
    @Schema(description = "Replies to this comment", required = true) List<CommentDto> replies) {

  public static CommentDto from(Comment comment, List<CommentDto> replies) {
    return new CommentDto(
        TsidUtils.toString(comment.getId()),
        comment.getContent(),
        PublicUserDto.from(comment.getCreatedBy()),
        comment.getCreatedAt(),
        comment.getParent() != null ? TsidUtils.toString(comment.getParent().getId()) : null,
        replies);
  }
}
