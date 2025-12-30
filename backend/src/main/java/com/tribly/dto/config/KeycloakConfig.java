package com.tribly.dto.config;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Keycloak configuration")
@ValidateSchema
public record KeycloakConfig(
    @Schema(
            description = "Keycloak server URL",
            examples = "http://localhost:8180",
            required = true)
        String url,
    @Schema(description = "Keycloak realm name", examples = "quarkus", required = true)
        String realm,
    @Schema(description = "Keycloak client ID", examples = "tribly-frontend", required = true)
        String clientId) {}
