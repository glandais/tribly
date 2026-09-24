package fr.pedalons.dto.users.response;

import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * What deleting the current account would do to its teams, read before the member confirms.
 *
 * <p>Any list may be empty, and a team appears in one list at most: a team migrated from biketeam
 * is in {@code migratedTeams} only. When {@code blocked} is true, {@code DELETE /api/users/me}
 * answers {@code SOLE_MIGRATED_TEAM_ADMIN} (a migrated team) or {@code SOLE_TEAM_ADMIN} and nothing
 * is deleted, {@code deletedTeams} included.
 */
@Schema(description = "What deleting the current user's account would do to their teams")
@ValidateSchema
public record AccountDeletionImpactDto(
    @Schema(
            description =
                "Whether the deletion is refused: the user is the only admin of at least one team"
                    + " that has other members (SOLE_TEAM_ADMIN), or of a team migrated from"
                    + " biketeam (SOLE_MIGRATED_TEAM_ADMIN)",
            required = true)
        boolean blocked,
    @Schema(
            description =
                "Teams the user is the only admin of while other members remain; they must name"
                    + " another admin, or delete the team, before deleting their account. Excludes"
                    + " the teams listed in migratedTeams",
            required = true)
        List<TeamPublicationDto> blockingTeams,
    @Schema(
            description =
                "Teams the user administers and is the only member of; they are deleted with the"
                    + " account. Excludes the teams listed in migratedTeams",
            required = true)
        List<TeamPublicationDto> deletedTeams,
    @Schema(
            description =
                "Teams the user is the only admin of, with or without other members, that were"
                    + " migrated from biketeam: their old biketeam addresses redirect to them. Each"
                    + " one refuses the deletion until another admin is named (or, for a platform"
                    + " admin, the switch-over is cancelled on biketeam and the team deleted)",
            required = true)
        List<TeamPublicationDto> migratedTeams) {}
