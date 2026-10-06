package fr.pedalons.dto.dashboard.response;

import fr.pedalons.dto.teams.response.MemberListResponse;
import fr.pedalons.dto.teams.response.TeamWebhookDto;
import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The « Administration » panel. The member split per role is on {@code team.memberCountByRole} and
 * the enabled modules on the team's flags: not repeated here.
 */
@Schema(
    description =
        "The administration panel of a team dashboard. The split of the members per role is"
            + " team.memberCountByRole, the enabled modules the team's enable* flags.")
@ValidateSchema
public record TeamDashboardAdminDto(
    @Schema(
            description =
                "The newest members, latest joined first (at most 3), with role and joinedAt."
                    + " total is the member count.",
            required = true)
        MemberListResponse newestMembers,
    @Schema(
            description =
                "The team's webhook: configured, kind, enabled, lastStatus, lastAttemptAt. The URL"
                    + " only comes masked.",
            required = true)
        TeamWebhookDto webhook) {}
