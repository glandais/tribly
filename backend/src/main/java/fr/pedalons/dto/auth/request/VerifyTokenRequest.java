package fr.pedalons.dto.auth.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Token verification request")
@ValidateSchema
@Builder
public record VerifyTokenRequest(
    @NotBlank @Size(max = 100) @Schema(description = "Verification token") String token) {}
