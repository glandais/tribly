package com.tribly.dto.karoo.response;

import com.tribly.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Karoo device code response (RFC 8628)")
@Builder
@ValidateSchema
public record KarooDeviceCodeResponse(
    @Schema(description = "Device code for polling", required = true) String deviceCode,
    @Schema(description = "User code to display (e.g., 'ABCD12')", required = true) String userCode,
    @Schema(description = "Verification URL for user to visit", required = true)
        String verificationUri,
    @Schema(description = "Verification URL with user code embedded", required = true)
        String verificationUriComplete,
    @Schema(description = "Code expiry in seconds", required = true) int expiresIn,
    @Schema(description = "Minimum polling interval in seconds", required = true) int interval) {}
