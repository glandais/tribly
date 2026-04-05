package fr.pedalons.dto.auth.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Reset password request")
@ValidateSchema
@Builder
public record ResetPasswordRequest(
    @NotBlank @Email @Size(max = 250) @Schema(description = "Email address") String email,
    @NotBlank @Pattern(regexp = "^\\d{6}$") @Schema(description = "6-digit OTP code") String code,
    @NotBlank @Size(min = 8, max = 100) @Schema(description = "New password (min 8 chars)")
        String newPassword) {}
