package fr.pedalons.dto.auth.response;

import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Authentication response")
@Builder(toBuilder = true)
@ValidateSchema
public record AuthResponse(
    @Schema(description = "JWT access token") String accessToken,
    @Schema(description = "Token expiry in seconds") int expiresIn,
    @Schema(description = "Authenticated user") UserDto user,
    @Schema(
            description =
                "Refresh token, for mobile clients. On a refresh, the rotated token — absent when"
                    + " the refresh came within the grace of a rotation made by another one, whose"
                    + " token stands.")
        String refreshToken) {}
