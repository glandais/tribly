package com.tribly.dto.garmin.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Garmin OAuth token exchange request")
@Builder
@ValidateSchema
public record GarminTokenRequest(
    @Schema(description = "Grant type (authorization_code or refresh_token)", required = true)
        @NotBlank
        String grantType,
    @Schema(description = "Authorization code (for authorization_code grant)") String code,
    @Schema(description = "Refresh token (for refresh_token grant)") String refreshToken) {}
