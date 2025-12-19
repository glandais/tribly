package com.tribly.api.config;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Tag(name = "Configuration", description = "Application configuration endpoints")
public class ConfigResource {

    @ConfigProperty(name = "tribly.keycloak.url")
    String keycloakUrl = "";

    @ConfigProperty(name = "tribly.keycloak.realm")
    String keycloakRealm = "";

    @ConfigProperty(name = "tribly.keycloak.client-id")
    String keycloakClientId = "";

    @ConfigProperty(name = "tribly.map.tile-url")
    String mapTileUrl = "";

    @ConfigProperty(name = "tribly.map.attribution")
    String mapAttribution = "";

    @GET
    @Operation(summary = "Get application configuration", description = "Get frontend configuration including Keycloak and map settings")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Configuration retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ConfigDto.class))
            )
    })
    public Response getConfig() {
        return Response.ok(new ConfigDto(
                new KeycloakConfig(keycloakUrl, keycloakRealm, keycloakClientId),
                new MapConfig(mapTileUrl, mapAttribution)
        )).build();
    }

    @Schema(description = "Application configuration")
    public record ConfigDto(
            @Schema(description = "Keycloak authentication configuration", required = true)
            KeycloakConfig keycloak,

            @Schema(description = "Map configuration", required = true)
            MapConfig map
    ) {
    }

    @Schema(description = "Keycloak configuration")
    public record KeycloakConfig(
            @Schema(description = "Keycloak server URL", examples = "http://localhost:8180", required = true)
            String url,

            @Schema(description = "Keycloak realm name", examples = "quarkus", required = true)
            String realm,

            @Schema(description = "Keycloak client ID", examples = "tribly-frontend", required = true)
            String clientId
    ) {
    }

    @Schema(description = "Map configuration")
    public record MapConfig(
            @Schema(description = "Map tile URL template", examples = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", required = true)
            String tileUrl,

            @Schema(description = "Map attribution text", required = true)
            String attribution
    ) {
    }
}
