package fr.pedalons.dto.beta;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "A beta program sign-up, as seen by an admin")
@ValidateSchema
public record BetaSignupDto(
    @Schema(description = "Sign-up ID", required = true) String id,
    @Schema(description = "Email", required = true) String email,
    @Schema(description = "When the sign-up was submitted", required = true) Instant createdAt) {}
