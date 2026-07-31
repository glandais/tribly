package fr.pedalons.dto.teams.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated list of a team's invitations")
@ValidateSchema
public record TeamInvitationListResponse(
    @Schema(description = "Invitations", required = true) List<TeamInvitationDto> invitations,
    @Schema(description = "Total number of invitations", required = true) long total,
    @Schema(description = "Current page (0-indexed)", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
