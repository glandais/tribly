package fr.pedalons.dto.users.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.gps.response.GpsServiceConnectionDto;
import fr.pedalons.dto.social.response.SocialIdentityDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.PlatformRole;
import fr.pedalons.enums.UnitSystem;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "User profile data")
@ValidateSchema
public record UserDto(
    @Schema(description = "User ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "User email address", required = true) String email,
    @Schema(description = "User display name", required = true) String displayName,
    @Nullable @Schema(description = "User avatar URL") String avatarUrl,
    @Nullable @Schema(description = "Account creation timestamp") Instant createdAt,
    @Nullable @Schema(description = "Preferred unit system (metric or imperial)")
        UnitSystem unitSystem,
    @Nullable @Schema(description = "Platform role (null if regular user)")
        PlatformRole platformRole,
    @Schema(description = "Whether the account's email has been verified", required = true)
        boolean emailVerified,
    @Schema(
            description =
                "True when the account still needs a real, verified email (e.g. a migrated Strava"
                    + " account with a placeholder address)",
            required = true)
        boolean requiresEmail,
    @Schema(description = "Connected GPS services") List<GpsServiceConnectionDto> connectedServices,
    @Schema(description = "Linked external identities (e.g. Strava)")
        List<SocialIdentityDto> socialIdentities) {

  public static UserDto from(User user) {
    return from(user, List.of(), List.of());
  }

  public static UserDto from(User user, List<GpsServiceConnectionDto> connectedServices) {
    return from(user, connectedServices, List.of());
  }

  public static UserDto from(
      User user,
      List<GpsServiceConnectionDto> connectedServices,
      List<SocialIdentityDto> socialIdentities) {
    return new UserDto(
        TsidUtils.toString(user.getId()),
        user.getEmail(),
        user.getDisplayName(),
        user.getAvatarUrl(),
        user.getCreatedAt(),
        user.getUnitSystem(),
        user.getPlatformRole(),
        user.isEmailVerified(),
        !user.isEmailVerified(),
        connectedServices,
        socialIdentities);
  }
}
