package fr.pedalons.api.publications;

import fr.pedalons.dto.publications.response.PublicationListResponse;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.service.common.PublicationService;
import fr.pedalons.service.team.request.MinRole;
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
public class PublicationResource {

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
      @Parameter(description = "Types") @QueryParam("type") @Nullable PublicationType type,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(
              description =
                  "Only publications from teams where the user has at least this role. Yields"
                      + " nothing for an anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    PublicationListResponse response =
        publicationService.listAll(type, search, from, to, minRole, page, size);

    return Response.ok(response).build();
  }
}
