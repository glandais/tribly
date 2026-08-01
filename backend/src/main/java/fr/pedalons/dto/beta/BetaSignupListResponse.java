package fr.pedalons.dto.beta;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated beta sign-up list response")
@ValidateSchema
public record BetaSignupListResponse(
    @Schema(description = "List of sign-ups", required = true) List<BetaSignupDto> signups,
    @Schema(description = "Total number of sign-ups", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
