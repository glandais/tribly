package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.GpsOAuthVersion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Request to update a GPS credential")
@ValidateSchema
public record UpdateGpsCredentialRequest(
    @NotBlank @Size(max = 255) @Schema(description = "OAuth client ID", required = true)
        String clientId,
    @Nullable @Size(max = 500) @Schema(description = "OAuth client secret (null = keep current)")
        String clientSecret,
    @Schema(description = "Whether credential is active") boolean active,
    @Nullable
        @Schema(
            description =
                "OAuth protocol of the client ID and secret (null = keep current). OAUTH1 is"
                    + " accepted for GARMIN only. Switching it leaves existing connections"
                    + " unusable: each is dropped at its next upload and must be reconnected")
        GpsOAuthVersion oauthVersion) {}
