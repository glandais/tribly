package fr.pedalons.dto.social.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to set/change the account's real email address")
@ValidateSchema
public record EmailChangeRequest(
    @NotBlank
        @Email
        @Size(max = 250)
        @Schema(description = "New email address", examples = "user@example.com", required = true)
        String email) {}
