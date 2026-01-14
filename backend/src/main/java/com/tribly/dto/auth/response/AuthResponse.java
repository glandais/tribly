package com.tribly.dto.auth.response;

import com.tribly.dto.users.response.UserDto;
import com.tribly.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Authentication response")
@Builder
@ValidateSchema
public record AuthResponse(
    @Schema(description = "JWT access token") String accessToken,
    @Schema(description = "Token expiry in seconds") int expiresIn,
    @Schema(description = "Authenticated user") UserDto user) {}
