package com.tribly.dto.users.response;

import com.tribly.domain.user.User;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "User profile data")
public record UserDto(
    @Schema(description = "User ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "User email address", required = true) String email,
    @Schema(description = "User display name", required = true) String displayName,
    @Nullable @Schema(description = "User avatar URL", nullable = true) String avatarUrl,
    @Nullable
        @Schema(description = "User locale (e.g. 'en', 'fr')", examples = "en", nullable = true)
        String locale,
    @Nullable
        @Schema(
            description = "User timezone (e.g. 'Europe/Paris')",
            examples = "Europe/Paris",
            nullable = true)
        String timezone,
    @Nullable @Schema(description = "Account creation timestamp", nullable = true)
        String createdAt) {
  public static UserDto from(User user) {
    return new UserDto(
        TsidUtils.toString(user.getId()),
        user.getEmail(),
        user.getDisplayName(),
        user.getAvatarUrl(),
        user.getLocale(),
        user.getTimezone(),
        user.getCreatedAt().toString());
  }
}
