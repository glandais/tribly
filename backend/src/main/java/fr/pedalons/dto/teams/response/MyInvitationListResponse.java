package fr.pedalons.dto.teams.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "The invitations waiting for the current user")
@ValidateSchema
public record MyInvitationListResponse(
    @Schema(description = "Invitations, newest first", required = true)
        List<MyInvitationDto> invitations) {}
