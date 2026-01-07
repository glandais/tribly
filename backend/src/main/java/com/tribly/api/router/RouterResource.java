package com.tribly.api.router;

import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.router.request.RouterRequest;
import com.tribly.dto.router.response.RouterResponse;
import com.tribly.service.brouter.BRouterService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/router")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Router", description = "Route calculation using BRouter")
public class RouterResource {

  @Inject BRouterService bRouterService;

  @POST
  @Operation(
      summary = "Calculate route",
      description = "Calculate a cycling route between two points using BRouter")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route calculated successfully",
        content = @Content(schema = @Schema(implementation = RouterResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request parameters",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "503",
        description = "BRouter service unavailable",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response route(@Valid RouterRequest request) {
    RouterResponse resp = bRouterService.getRoute(request);
    return Response.ok(resp).build();
  }
}
