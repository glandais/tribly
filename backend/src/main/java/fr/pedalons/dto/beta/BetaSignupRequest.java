package fr.pedalons.dto.beta;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "A request to be notified and added to a beta program once one opens")
@ValidateSchema
@Builder
public record BetaSignupRequest(
    @NotBlank
        @Email
        @Schema(
            description = "Email to contact once a beta is open",
            examples = "rider@example.com",
            required = true)
        String email) {}
