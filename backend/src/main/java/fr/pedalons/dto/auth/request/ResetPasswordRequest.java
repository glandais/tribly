package fr.pedalons.dto.auth.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Reset password request")
@ValidateSchema
@Builder
public record ResetPasswordRequest(
    @NotBlank @Size(max = 100) @Schema(description = "Password reset token") String token,
    @NotBlank @Size(min = 8, max = 100) @Schema(description = "New password (min 8 chars)")
        String newPassword) {}
