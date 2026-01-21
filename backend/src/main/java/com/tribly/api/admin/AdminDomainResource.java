package com.tribly.api.admin;

import com.tribly.dto.admin.AdminDomainDto;
import com.tribly.dto.admin.AdminDomainListResponse;
import com.tribly.dto.admin.AdminGpsCredentialDto;
import com.tribly.dto.admin.AdminStatsDto;
import com.tribly.dto.admin.CreateDomainRequest;
import com.tribly.dto.admin.CreateGpsCredentialRequest;
import com.tribly.dto.admin.UpdateDomainRequest;
import com.tribly.dto.admin.UpdateGpsCredentialRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.repository.common.TriblyPage;
import com.tribly.repository.platform.DomainRepository;
import com.tribly.repository.team.TeamRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.admin.AdminDomainGpsCredentialService;
import com.tribly.service.admin.AdminDomainService;
import com.tribly.service.security.annotation.Admin;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/admin/domains")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Admin - Domains", description = "Platform admin domain management")
public class AdminDomainResource {

  @Inject AdminDomainService adminDomainService;

  @Inject AdminDomainGpsCredentialService gpsCredentialService;

  @Inject DomainRepository domainRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @GET
  @Path("/stats")
  @Admin
  @Operation(
      operationId = "getStats",
      summary = "Get platform statistics",
      description = "Get overall platform statistics")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Statistics retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdminStatsDto.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getStats() {
    long totalDomains = domainRepository.count("deleted = false");
    long totalTeams = teamRepository.count("deleted = false");
    long totalUsers = userRepository.count("deleted = false");
    AdminStatsDto stats = new AdminStatsDto(totalDomains, totalTeams, totalUsers);
    return Response.ok(stats).build();
  }

  @GET
  @Admin
  @Operation(
      operationId = "listDomains",
      summary = "List all domains",
      description = "Get a paginated list of all domains")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Domains retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdminDomainListResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listDomains(
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    TriblyPage<AdminDomainDto> result = adminDomainService.listDomains(page, size);
    AdminDomainListResponse response =
        new AdminDomainListResponse(result.items(), result.total(), page, size);
    return Response.ok(response).build();
  }

  @GET
  @Path("/{domainId}")
  @Admin
  @Operation(
      operationId = "getDomain",
      summary = "Get domain details",
      description = "Get detailed domain information")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Domain retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdminDomainDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Domain not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getDomain(
      @Parameter(description = "Domain ID") @PathParam("domainId") String domainId) {

    AdminDomainDto domain = adminDomainService.getDomain(domainId);
    return Response.ok(domain).build();
  }

  @POST
  @Admin
  @Operation(
      operationId = "createDomain",
      summary = "Create domain",
      description = "Create a new domain")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Domain created successfully",
        content = @Content(schema = @Schema(implementation = AdminDomainDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createDomain(@Valid CreateDomainRequest request) {

    AdminDomainDto domain =
        adminDomainService.createDomain(
            request.domain(), request.name(), request.baseUrl(), request.singleTeam());
    return Response.status(Response.Status.CREATED).entity(domain).build();
  }

  @PUT
  @Path("/{domainId}")
  @Admin
  @Operation(
      operationId = "updateDomain",
      summary = "Update domain",
      description = "Update domain information")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Domain updated successfully",
        content = @Content(schema = @Schema(implementation = AdminDomainDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Domain not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateDomain(
      @Parameter(description = "Domain ID") @PathParam("domainId") String domainId,
      @Valid UpdateDomainRequest request) {

    AdminDomainDto domain =
        adminDomainService.updateDomain(
            domainId, request.name(), request.baseUrl(), request.singleTeam());
    return Response.ok(domain).build();
  }

  @POST
  @Path("/{domainId}/toggle-active")
  @Admin
  @Operation(
      operationId = "toggleDomainActive",
      summary = "Toggle domain active status",
      description = "Enable or disable a domain")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Domain status toggled successfully",
        content = @Content(schema = @Schema(implementation = AdminDomainDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Domain not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response toggleDomainActive(
      @Parameter(description = "Domain ID") @PathParam("domainId") String domainId) {

    AdminDomainDto domain = adminDomainService.toggleDomainActive(domainId);
    return Response.ok(domain).build();
  }

  // GPS Credentials endpoints

  @GET
  @Path("/{domainId}/gps-credentials")
  @Admin
  @Operation(
      operationId = "listDomainGpsCredentials",
      summary = "List GPS credentials",
      description = "Get all GPS credentials for a domain")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Credentials retrieved successfully",
        content =
            @Content(
                schema =
                    @Schema(
                        type = org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY,
                        implementation = AdminGpsCredentialDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Domain not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listDomainGpsCredentials(
      @Parameter(description = "Domain ID") @PathParam("domainId") String domainId) {

    List<AdminGpsCredentialDto> credentials = gpsCredentialService.listCredentials(domainId);
    return Response.ok(credentials).build();
  }

  @POST
  @Path("/{domainId}/gps-credentials")
  @Admin
  @Operation(
      operationId = "createDomainGpsCredential",
      summary = "Create GPS credential",
      description = "Create a new GPS credential for a domain")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Credential created successfully",
        content = @Content(schema = @Schema(implementation = AdminGpsCredentialDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Domain not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Credential already exists for this service type",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createDomainGpsCredential(
      @Parameter(description = "Domain ID") @PathParam("domainId") String domainId,
      @Valid CreateGpsCredentialRequest request) {

    AdminGpsCredentialDto credential = gpsCredentialService.createCredential(domainId, request);
    return Response.status(Response.Status.CREATED).entity(credential).build();
  }

  @PUT
  @Path("/{domainId}/gps-credentials/{credentialId}")
  @Admin
  @Operation(
      operationId = "updateDomainGpsCredential",
      summary = "Update GPS credential",
      description = "Update a GPS credential for a domain")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Credential updated successfully",
        content = @Content(schema = @Schema(implementation = AdminGpsCredentialDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Domain or credential not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateDomainGpsCredential(
      @Parameter(description = "Domain ID") @PathParam("domainId") String domainId,
      @Parameter(description = "Credential ID") @PathParam("credentialId") String credentialId,
      @Valid UpdateGpsCredentialRequest request) {

    AdminGpsCredentialDto credential =
        gpsCredentialService.updateCredential(domainId, credentialId, request);
    return Response.ok(credential).build();
  }

  @DELETE
  @Path("/{domainId}/gps-credentials/{credentialId}")
  @Admin
  @Operation(
      operationId = "deleteDomainGpsCredential",
      summary = "Delete GPS credential",
      description = "Delete a GPS credential for a domain")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Credential deleted successfully"),
    @APIResponse(
        responseCode = "404",
        description = "Domain or credential not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteDomainGpsCredential(
      @Parameter(description = "Domain ID") @PathParam("domainId") String domainId,
      @Parameter(description = "Credential ID") @PathParam("credentialId") String credentialId) {

    gpsCredentialService.deleteCredential(domainId, credentialId);
    return Response.noContent().build();
  }
}
