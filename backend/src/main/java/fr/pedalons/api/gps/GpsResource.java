package fr.pedalons.api.gps;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.gps.response.GpsOAuthUrlResponse;
import fr.pedalons.dto.gps.response.RouteUploadResponse;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.service.gps.GpsService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/gps")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "GPS Services", description = "GPS device integration operations")
public class GpsResource {

  @Inject GpsService gpsService;

  @GET
  @Path("/available")
  @RolesAllowed("user")
  @Operation(
      summary = "Get available GPS services",
      description = "Get list of GPS service types configured for this domain")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "List of available GPS service types",
        content = @Content(schema = @Schema(implementation = GpsServiceType[].class)))
  })
  public Response getAvailableServices() {
    List<GpsServiceType> available = gpsService.getAvailableServices();
    return Response.ok(available).build();
  }

  @GET
  @Path("/connect/{serviceType}")
  @RolesAllowed("user")
  @Operation(
      summary = "Get OAuth authorization URL",
      description = "Get the OAuth authorization URL to connect a GPS service")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Authorization URL",
        content = @Content(schema = @Schema(implementation = GpsOAuthUrlResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Service already connected",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getConnectUrl(
      @Parameter(description = "GPS service type") @PathParam("serviceType")
          GpsServiceType serviceType) {
    GpsOAuthUrlResponse response = gpsService.initiateOAuth(serviceType);
    return Response.ok(response).build();
  }

  @GET
  @Path("/callback/{serviceType}")
  @PermitAll
  @Operation(
      summary = "OAuth callback",
      description = "Handles OAuth callback from GPS service and redirects to frontend")
  @APIResponses({
    @APIResponse(responseCode = "302", description = "Redirects to frontend"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid state or code",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response handleCallback(
      @Parameter(description = "GPS service type") @PathParam("serviceType")
          GpsServiceType serviceType,
      @QueryParam("code") @Nullable String code,
      @QueryParam("state") @Nullable String state,
      @QueryParam("error") @Nullable String error) {

    // Handle OAuth error
    if (error != null) {
      return Response.temporaryRedirect(
              URI.create(gpsService.getFrontendBaseUrl() + "/profile?gps_error=" + error))
          .build();
    }

    if (code == null || state == null) {
      return Response.temporaryRedirect(
              URI.create(gpsService.getFrontendBaseUrl() + "/profile?gps_error=missing_params"))
          .build();
    }

    try {
      gpsService.handleCallback(serviceType, code, state);
      return Response.temporaryRedirect(
              URI.create(
                  gpsService.getFrontendBaseUrl()
                      + "/profile?gps_connected="
                      + serviceType.name().toLowerCase()))
          .build();
    } catch (Exception e) {
      return Response.temporaryRedirect(
              URI.create(gpsService.getFrontendBaseUrl() + "/profile?gps_error=connection_failed"))
          .build();
    }
  }

  @DELETE
  @Path("/disconnect/{serviceType}")
  @RolesAllowed("user")
  @Operation(summary = "Disconnect GPS service", description = "Disconnect a connected GPS service")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Service disconnected"),
    @APIResponse(
        responseCode = "400",
        description = "Service not connected",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response disconnect(
      @Parameter(description = "GPS service type") @PathParam("serviceType")
          GpsServiceType serviceType) {
    gpsService.disconnect(serviceType);
    return Response.noContent().build();
  }

  @POST
  @Path("/upload/{serviceType}/{teamSlug}/{routeSlug}")
  @RolesAllowed("user")
  @Operation(
      summary = "Upload route to GPS service",
      description = "Upload a route to a connected GPS service")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route uploaded",
        content = @Content(schema = @Schema(implementation = RouteUploadResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Service not connected or upload failed",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response uploadRoute(
      @Parameter(description = "GPS service type") @PathParam("serviceType")
          GpsServiceType serviceType,
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Route URL slug") @PathParam("routeSlug") String routeSlug) {
    RouteUploadResponse response = gpsService.uploadRoute(serviceType, teamSlug, routeSlug);
    return Response.ok(response).build();
  }
}
