package com.tribly.dto.teams.response;

import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Team member information")
public record MemberDto(
    @Schema(description = "Membership ID (TSID)", required = true) String id,
    @Schema(description = "User ID (TSID)", required = true) String userId,
    @Schema(description = "User display name", required = true) String displayName,
    @Nullable @Schema(description = "User avatar URL", nullable = true) String avatarUrl,
    @Schema(description = "Member role", required = true) TeamRole role,
    @Nullable @Schema(description = "When the user joined the team", nullable = true)
        String joinedAt) {
  public static MemberDto from(UserTeam userTeam) {
    User user = userTeam.getUser();
    return new MemberDto(
        TsidUtils.toString(userTeam.getId()),
        TsidUtils.toString(user.getId()),
        user.getDisplayName(),
        user.getAvatarUrl(),
        userTeam.getRole(),
        userTeam.getJoinedAt().toString());
  }
}
