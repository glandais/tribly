package com.tribly.dto.garmin.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Garmin OAuth callback from frontend")
@Builder
@ValidateSchema
public record GarminCallbackRequest(
    @Schema(description = "User's access token from frontend auth", required = true) @NotBlank
        String accessToken,
    @Schema(description = "Original redirect URI from authorize request", required = true) @NotBlank
        String redirectUri,
    @Schema(description = "State parameter from authorize request") String state) {}
