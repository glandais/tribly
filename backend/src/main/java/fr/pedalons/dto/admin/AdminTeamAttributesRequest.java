package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Platform admin request to update team governance attributes")
@ValidateSchema
public record AdminTeamAttributesRequest(
    @Schema(
            description = "Whether team admins can change visibility",
            examples = "false",
            required = true)
        boolean visibilityEditable,
    @Schema(
            description = "Whether any domain user can join this public team",
            examples = "false",
            required = true)
        boolean joinable,
    @Schema(
            description = "Whether team admins can add members",
            examples = "false",
            required = true)
        boolean addMemberAllowed,
    @Schema(
            description = "Whether the interactive route planner is open to this team",
            examples = "false",
            required = true)
        boolean enableRoutePlanner) {}
