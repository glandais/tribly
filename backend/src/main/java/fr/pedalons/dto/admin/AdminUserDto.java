package fr.pedalons.dto.admin;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.PlatformRole;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Admin user view with domain info")
@ValidateSchema
public record AdminUserDto(
    @Schema(description = "User ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "User email address", required = true) String email,
    @Schema(description = "User display name", required = true) String displayName,
    @Schema(description = "Domain ID this user belongs to", required = true) String domainId,
    @Schema(description = "Domain hostname", required = true) String domainName,
    @Nullable @Schema(description = "Platform role (null if regular user)")
        PlatformRole platformRole,
    @Schema(description = "Whether email is verified", required = true) boolean emailVerified,
    @Schema(description = "User creation timestamp", required = true) Instant createdAt,
    @Nullable @Schema(description = "Last login timestamp") Instant lastLoginAt) {

  public static AdminUserDto from(User user) {
    return new AdminUserDto(
        TsidUtils.toString(user.getId()),
        user.getEmail(),
        user.getDisplayName(),
        TsidUtils.toString(user.getDomain().getId()),
        user.getDomain().getDomain(),
        user.getPlatformRole(),
        user.isEmailVerified(),
        user.getCreatedAt(),
        user.getLastLoginAt());
  }
}
