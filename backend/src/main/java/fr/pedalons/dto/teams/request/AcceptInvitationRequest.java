package fr.pedalons.dto.teams.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Redeem an invitation token")
@ValidateSchema
public record AcceptInvitationRequest(
    @Schema(
            description =
                "The token from the invitation e-mail. Sent in the body rather than in the path,"
                    + " because a bearer secret in a URL path ends up in access logs and in the"
                    + " Referer of anything the page loads.",
            required = true)
        @NotBlank
        @Size(max = 200)
        String token) {}
