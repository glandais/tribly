package fr.pedalons.dto.users.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.gps.response.GpsServiceConnectionDto;
import fr.pedalons.dto.social.response.SocialIdentityDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.PlatformRole;
import fr.pedalons.enums.ThemePreference;
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
    @Nullable
        @Schema(
            description =
                "Preferred colour scheme. Null means the user never chose one — distinct from"
                    + " SYSTEM, which they did choose — so a client is free to follow the device.")
        ThemePreference theme,
    @Nullable
        @Schema(
            description =
                "Preferred language as a BCP-47 tag. Null means the user never chose one; the"
                    + " client then follows the device or the domain.")
        String language,
    @Nullable
        @Schema(
            description =
                "Preferred IANA timezone (e.g. 'Europe/Paris'). Null means the user never chose"
                    + " one; the client then follows the browser.")
        String timezone,
    @Schema(
            description =
                "Whether team members may reach this user through the classified-ad relay. True"
                    + " unless they explicitly opted out, so an account that predates the"
                    + " preference is contactable.",
            required = true)
        boolean contactableByMembers,
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
        user.getTheme(),
        user.getLanguage(),
        user.getTimezone(),
        user.isContactableByMembers(),
        user.getPlatformRole(),
        user.isEmailVerified(),
        !user.isEmailVerified(),
        connectedServices,
        socialIdentities);
  }
}
