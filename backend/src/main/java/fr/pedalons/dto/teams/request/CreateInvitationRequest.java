package fr.pedalons.dto.teams.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TeamRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Invite an e-mail address to join a team")
@ValidateSchema
public record CreateInvitationRequest(
    @Schema(
            description =
                "Address to invite. The response is the same whether or not an account already"
                    + " exists for it — only the wording of the e-mail differs. Answering"
                    + " differently would turn this into an account-existence oracle for anyone who"
                    + " administers a team.",
            examples = "member@example.com",
            required = true)
        @NotBlank
        @Email
        @Size(max = 250)
        String email,
    @Schema(description = "Role to grant on acceptance. Defaults to MEMBER.", examples = "MEMBER")
        @Nullable TeamRole role) {}
