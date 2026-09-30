package fr.pedalons.dto.auth.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "Activates an account from its verification link: the password is chosen here, by whoever"
            + " holds the mailbox, never at sign-up.")
@ValidateSchema
@Builder
public record ActivateAccountRequest(
    @NotBlank @Size(max = 100) @Schema(description = "Verification token") String token,
    @NotBlank @Size(min = 8, max = 100) @Schema(description = "Password (min 8 chars)")
        String password) {}
