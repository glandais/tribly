package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to update a domain")
@ValidateSchema
public record UpdateDomainRequest(
    @NotBlank @Size(max = 250) @Schema(description = "Domain display name", required = true)
        String name,
    @NotBlank @Size(max = 500) @Schema(description = "Base URL for the domain", required = true)
        String baseUrl,
    @Schema(description = "Whether domain is single-team mode") boolean singleTeam,
    @Schema(description = "Whether the interactive planner is open in the GPX tools")
        boolean enableGpxPlanner,
    @Size(max = 1000)
        @Schema(
            description =
                "Android app SHA-256 certificate fingerprints for passkey origin verification"
                    + " (comma-separated, colon-hex format)")
        String androidFingerprints) {}
