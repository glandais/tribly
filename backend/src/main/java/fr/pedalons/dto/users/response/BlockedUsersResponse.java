package fr.pedalons.dto.users.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "The members the current user blocked, most recent first")
@ValidateSchema
public record BlockedUsersResponse(
    @Schema(description = "Blocked members", required = true) List<PublicUserDto> users) {}
