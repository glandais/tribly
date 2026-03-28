package fr.pedalons.dto.auth.response;

import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Authentication response")
@Builder
@ValidateSchema
public record AuthResponse(
    @Schema(description = "JWT access token") String accessToken,
    @Schema(description = "Token expiry in seconds") int expiresIn,
    @Schema(description = "Authenticated user") UserDto user,
    @Schema(description = "Refresh token (for mobile clients)") String refreshToken) {}
