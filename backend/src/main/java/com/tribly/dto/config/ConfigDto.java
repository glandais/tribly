package com.tribly.dto.config;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Application configuration")
public record ConfigDto(
    @Schema(description = "Keycloak authentication configuration", required = true)
        KeycloakConfig keycloak,
    @Schema(description = "Map configuration", required = true) MapConfig map) {}
