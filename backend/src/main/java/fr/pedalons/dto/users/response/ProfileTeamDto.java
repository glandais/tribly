package fr.pedalons.dto.users.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TeamRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "One of the current user's teams, with the user's role in it")
@ValidateSchema
public record ProfileTeamDto(
    @Schema(description = "Team URL slug", required = true) String slug,
    @Schema(description = "Team name", required = true) String name,
    @Schema(description = "The user's role in the team", required = true) TeamRole role) {}
