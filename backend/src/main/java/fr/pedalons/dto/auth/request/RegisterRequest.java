package fr.pedalons.dto.auth.request;

import fr.pedalons.dto.validation.AcceptableText;
import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User registration request")
@ValidateSchema
@Builder
public record RegisterRequest(
    @NotBlank
        @Email
        @Size(max = 250)
        @Schema(description = "Email address", examples = "user@example.com")
        String email,
    @NotBlank
        @Size(min = 1, max = 200)
        @AcceptableText
        @Schema(description = "Display name", examples = "John Doe")
        String displayName,
    @NotBlank @Size(min = 8, max = 100) @Schema(description = "Password (min 8 chars)")
        String password,
    @AssertTrue
        @Schema(
            description =
                "The member accepted the terms of service. Required, and must be true: the"
                    + " sign-up form asks for it with a checkbox.",
            required = true)
        boolean acceptTerms) {}
