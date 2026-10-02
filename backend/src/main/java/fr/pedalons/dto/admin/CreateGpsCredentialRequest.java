package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.GpsOAuthVersion;
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
        @Schema(
            description = "OAuth client secret, or consumer secret for OAuth 1.0a (required then)")
        String clientSecret,
    @Schema(description = "Whether credential is active") boolean active,
    @Nullable
        @Schema(
            description =
                "OAuth protocol of the client ID and secret (null = OAUTH2). OAUTH1 is accepted"
                    + " for GARMIN only; the client ID is then the consumer key")
        GpsOAuthVersion oauthVersion) {}
