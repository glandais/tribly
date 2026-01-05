package com.tribly.dto.config;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Application configuration")
@ValidateSchema
public record ConfigDto(
    @Schema(description = "Keycloak authentication configuration", required = true)
        KeycloakConfig keycloak) {}
