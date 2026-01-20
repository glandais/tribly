package com.tribly.dto.users.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.user.User;
import com.tribly.dto.gps.response.GpsServiceConnectionDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.PlatformRole;
import com.tribly.enums.UnitSystem;
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
    @Schema(description = "Connected GPS services")
        List<GpsServiceConnectionDto> connectedServices) {

  public static UserDto from(User user) {
    return from(user, List.of());
  }

  public static UserDto from(User user, List<GpsServiceConnectionDto> connectedServices) {
    return new UserDto(
        TsidUtils.toString(user.getId()),
        user.getEmail(),
        user.getDisplayName(),
        user.getAvatarUrl(),
        user.getCreatedAt(),
        user.getUnitSystem(),
        user.getPlatformRole(),
        connectedServices);
  }
}
