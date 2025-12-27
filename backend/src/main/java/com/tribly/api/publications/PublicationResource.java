package com.tribly.api.publications;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.dto.publications.response.PublicationListResponse;
import com.tribly.service.common.PublicationService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/publications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Publications", description = "Publication listing")
public class PublicationResource extends AbstractAuthenticatedResource {

  @Inject PublicationService publicationService;

  @GET
  @PermitAll
  @Operation(
      summary = "List all publications",
      description = "Get publications from all accessible teams (user's teams + public teams)")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Publications retrieved successfully",
        content = @Content(schema = @Schema(implementation = PublicationListResponse.class)))
  })
  public Response listAllPublications(
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Long userId = getCurrentUserIdOrNull();

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    PublicationListResponse response = publicationService.list(null, userId, from, to, page, size);

    return Response.ok(response).build();
  }
}
