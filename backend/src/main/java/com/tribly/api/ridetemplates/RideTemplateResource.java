package com.tribly.api.ridetemplates;

import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.ridetemplates.request.RideTemplateRequest;
import com.tribly.dto.ridetemplates.response.RideTemplateDto;
import com.tribly.dto.ridetemplates.response.RideTemplateListResponse;
import com.tribly.service.ridetemplate.RideTemplateService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/teams/{teamSlug}/ride-templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Ride Templates", description = "Ride template management operations")
@RolesAllowed("user")
public class RideTemplateResource {

  @Inject RideTemplateService templateService;

  @GET
  @Operation(
      summary = "List ride templates",
      description = "Get paginated list of ride templates for a team")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Templates retrieved successfully",
        content = @Content(schema = @Schema(implementation = RideTemplateListResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not a team member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listTemplates(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Search by name") @QueryParam("search") @Nullable String search,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    RideTemplateListResponse templates =
        templateService.listTemplates(teamSlug, search, page, size);
    return Response.ok(templates).build();
  }

  @GET
  @Path("/{templateSlug}")
  @Operation(summary = "Get ride template", description = "Get detailed ride template information")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Template retrieved successfully",
        content = @Content(schema = @Schema(implementation = RideTemplateDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not a team member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or template not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getTemplate(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Template URL slug") @PathParam("templateSlug")
          String templateSlug) {

    RideTemplateDto template = templateService.getTemplate(teamSlug, templateSlug);
    return Response.ok(template).build();
  }

  @POST
  @Operation(
      summary = "Create ride template",
      description = "Create a new ride template. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Template created successfully",
        content = @Content(schema = @Schema(implementation = RideTemplateDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not an organizer",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createTemplate(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid RideTemplateRequest request) {

    RideTemplateDto template = templateService.createTemplate(teamSlug, request);

    return Response.status(Response.Status.CREATED).entity(template).build();
  }

  @PUT
  @Path("/{templateSlug}")
  @Operation(
      summary = "Update ride template",
      description = "Update ride template information. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Template updated successfully",
        content = @Content(schema = @Schema(implementation = RideTemplateDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not an organizer",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or template not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateTemplate(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Template URL slug") @PathParam("templateSlug") String templateSlug,
      @Valid RideTemplateRequest request) {

    RideTemplateDto updatedTemplate =
        templateService.updateTemplate(teamSlug, templateSlug, request);
    return Response.ok(updatedTemplate).build();
  }

  @DELETE
  @Path("/{templateSlug}")
  @Operation(
      summary = "Delete ride template",
      description = "Soft delete a ride template. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Template deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not an organizer",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or template not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteTemplate(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Template URL slug") @PathParam("templateSlug")
          String templateSlug) {

    templateService.deleteTemplate(teamSlug, templateSlug);
    return Response.noContent().build();
  }
}
