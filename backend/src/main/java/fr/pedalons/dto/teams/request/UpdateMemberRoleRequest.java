package fr.pedalons.dto.teams.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TeamRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to update a member's role")
@ValidateSchema
public record UpdateMemberRoleRequest(
    @Schema(description = "New role", required = true) TeamRole role) {}
