package fr.pedalons.dto.users.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Public user information (limited fields)")
@ValidateSchema
public record PublicUserDto(
    @Schema(description = "User ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "User display name", required = true) String displayName,
    @Nullable @Schema(description = "User avatar URL") String avatarUrl) {
  public static PublicUserDto from(User user) {
    return new PublicUserDto(
        TsidUtils.toString(user.getId()), user.getDisplayName(), user.getAvatarUrl());
  }
}
