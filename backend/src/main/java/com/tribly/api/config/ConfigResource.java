package com.tribly.api.config;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class ConfigResource {

    @ConfigProperty(name = "tribly.keycloak.url")
    String keycloakUrl;

    @ConfigProperty(name = "tribly.keycloak.realm")
    String keycloakRealm;

    @ConfigProperty(name = "tribly.keycloak.client-id")
    String keycloakClientId;

    @ConfigProperty(name = "tribly.map.tile-url")
    String mapTileUrl;

    @ConfigProperty(name = "tribly.map.attribution")
    String mapAttribution;

    @GET
    public Response getConfig() {
        return Response.ok(new ConfigDto(
                new KeycloakConfig(keycloakUrl, keycloakRealm, keycloakClientId),
                new MapConfig(mapTileUrl, mapAttribution)
        )).build();
    }

    public record ConfigDto(
            KeycloakConfig keycloak,
            MapConfig map
    ) {}

    public record KeycloakConfig(
            String url,
            String realm,
            String clientId
    ) {}

    public record MapConfig(
            String tileUrl,
            String attribution
    ) {}
}
