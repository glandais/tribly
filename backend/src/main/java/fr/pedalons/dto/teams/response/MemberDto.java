package fr.pedalons.dto.teams.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TeamRole;
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
