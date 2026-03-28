package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.GpsServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Request to create a new GPS credential")
@ValidateSchema
public record CreateGpsCredentialRequest(
    @NotNull @Schema(description = "GPS service type", required = true) GpsServiceType serviceType,
    @NotBlank @Size(max = 255) @Schema(description = "OAuth client ID", required = true)
        String clientId,
    @Nullable
        @Size(max = 500)
        @Schema(description = "OAuth client secret (nullable for PKCE services)")
        String clientSecret,
    @Schema(description = "Whether credential is active") boolean active) {}
