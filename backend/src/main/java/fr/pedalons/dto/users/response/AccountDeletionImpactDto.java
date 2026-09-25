package fr.pedalons.dto.users.response;

import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * What deleting the current account would do to its teams, read before the member confirms.
 *
 * <p>Either list may be empty. When {@code blocked} is true, {@code DELETE /api/users/me} answers
 * {@code SOLE_TEAM_ADMIN} and nothing is deleted, {@code deletedTeams} included.
 */
@Schema(description = "What deleting the current user's account would do to their teams")
@ValidateSchema
public record AccountDeletionImpactDto(
    @Schema(
            description =
                "Whether the deletion is refused (SOLE_TEAM_ADMIN): the user is the only admin of"
                    + " at least one team that has other members",
            required = true)
        boolean blocked,
    @Schema(
            description =
                "Teams the user is the only admin of while other members remain; they must name"
                    + " another admin, or delete the team, before deleting their account",
            required = true)
        List<TeamPublicationDto> blockingTeams,
    @Schema(
            description =
                "Teams the user administers and is the only member of; they are deleted with the"
                    + " account",
            required = true)
        List<TeamPublicationDto> deletedTeams) {}
