package com.tribly.dto.gps.response;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "OAuth authorization URL response")
@ValidateSchema
public record GpsOAuthUrlResponse(
    @Schema(description = "URL to redirect user for OAuth authorization", required = true)
        String authorizationUrl) {}
