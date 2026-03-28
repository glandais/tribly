package fr.pedalons.dto.device.response;

import fr.pedalons.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Device OAuth token response")
@Builder
@ValidateSchema
public record DeviceTokenResponse(
    @Schema(description = "Access token", required = true) String accessToken,
    @Schema(description = "Token type (always 'Bearer')", required = true) String tokenType,
    @Schema(description = "Token expiry in seconds", required = true) int expiresIn,
    @Schema(description = "Refresh token") @Nullable String refreshToken) {}
