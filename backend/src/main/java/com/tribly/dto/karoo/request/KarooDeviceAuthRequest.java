package com.tribly.dto.karoo.request;

import com.tribly.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Karoo device code request (RFC 8628)")
@Builder
@ValidateSchema
public record KarooDeviceAuthRequest(
    @Schema(description = "Client ID (must be 'karoo-device')") @Nullable String clientId) {}
