package com.tribly.dto.teams.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.TeamRole;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Team member information")
@ValidateSchema
public record MemberDto(
    @Schema(description = "Team", required = true) TeamPublicationDto team,
    @Schema(description = "Membership ID (TSID)", required = true) String id,
    @Schema(description = "User", required = true) PublicUserDto user,
    @Schema(description = "Member role", required = true) TeamRole role,
    @Nullable @Schema(description = "When the user joined the team") Instant joinedAt) {
  public static MemberDto from(UserTeam userTeam) {
    User user = userTeam.getUser();
    return new MemberDto(
        TeamPublicationDto.from(userTeam.getTeam()),
        TsidUtils.toString(userTeam.getId()),
        PublicUserDto.from(userTeam.getUser()),
        userTeam.getRole(),
        userTeam.getJoinedAt());
  }
}
