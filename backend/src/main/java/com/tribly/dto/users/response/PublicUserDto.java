package com.tribly.dto.users.response;

import com.tribly.domain.user.User;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Public user information (limited fields)")
public record PublicUserDto(
    @Schema(description = "User ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "User display name", required = true) String displayName,
    @Nullable @Schema(description = "User avatar URL") String avatarUrl) {
  public static PublicUserDto from(User user) {
    return new PublicUserDto(
        TsidUtils.toString(user.getId()), user.getDisplayName(), user.getAvatarUrl());
  }
}
