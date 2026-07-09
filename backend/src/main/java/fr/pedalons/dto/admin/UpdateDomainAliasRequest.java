package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to update a domain alias")
@ValidateSchema
public record UpdateDomainAliasRequest(
    @NotBlank
        @Schema(
            description = "Slug of the team to pin (must belong to the domain)",
            required = true)
        String teamSlug,
    @NotBlank @Size(max = 250) @Schema(description = "Alias display name", required = true)
        String name,
    @NotBlank @Size(max = 500) @Schema(description = "Base URL for the alias", required = true)
        String baseUrl,
    @Size(max = 1000)
        @Schema(
            description =
                "Android app SHA-256 certificate fingerprints for passkey origin verification"
                    + " (comma-separated, colon-hex format)")
        String androidFingerprints) {}
