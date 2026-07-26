package fr.pedalons.api.publications;

import fr.pedalons.dto.common.CountResponse;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.publications.response.PublicationListResponse;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.enums.ListViewMode;
import fr.pedalons.enums.Status;
import fr.pedalons.service.common.PublicationService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
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

@Path("/api/teams/{teamSlug}/publications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Publications", description = "Publication listing")
public class TeamPublicationResource {

  @Inject PublicationService publicationService;

  @GET
  @PermitAll
  @Operation(
      summary = "List publications",
      description = "Get paginated list of publications for a team with optional filtering")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Publications retrieved successfully",
        content = @Content(schema = @Schema(implementation = PublicationListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listPublications(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Type") @QueryParam("type") @Nullable PublicationType type,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(
              description =
                  "Only publications with this status. Narrows the visibility rules, never widens"
                      + " them.")
          @QueryParam("status")
          @Nullable Status status,
      @Parameter(
              description =
                  "Only publications the current user is registered to (rides and trips). Yields"
                      + " nothing for an anonymous visitor.")
          @QueryParam("participating")
          @DefaultValue("false")
          boolean participating,
      @Parameter(description = PublicationResource.VIEW_PARAM_DESCRIPTION) @QueryParam("view")
          @Nullable ListViewMode view,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    PublicationListResponse publications =
        publicationService.listTeam(
            teamSlug, type, search, from, to, status, participating, view, page, size);

    // Rows carry per-user fields (registered, registeredGroupId): not shareable between users.
    return Response.ok(publications)
        .header(HttpHeaders.CACHE_CONTROL, PublicationResource.PRIVATE_NO_STORE)
        .build();
  }

  @GET
  @PermitAll
  @Path("/count")
  @Operation(
      operationId = "countPublications",
      summary = "Count publications",
      description =
          "How many of the team's publications match the filters, with none of them read. Accepts"
              + " exactly the same filters as the listing, minus pagination, so a count and the"
              + " list it opens can never disagree.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Count computed successfully",
        content = @Content(schema = @Schema(implementation = CountResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response countPublications(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Type") @QueryParam("type") @Nullable PublicationType type,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(
              description =
                  "Only publications with this status. Narrows the visibility rules, never widens"
                      + " them.")
          @QueryParam("status")
          @Nullable Status status,
      @Parameter(
              description =
                  "Only publications the current user is registered to (rides and trips). Yields"
                      + " zero for an anonymous visitor.")
          @QueryParam("participating")
          @DefaultValue("false")
          boolean participating) {

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    CountResponse count =
        publicationService.countTeam(teamSlug, type, search, from, to, status, participating);

    // The figure depends on who is asking — both through the visibility rules and through
    // 'participating': not shareable between users.
    return Response.ok(count)
        .header(HttpHeaders.CACHE_CONTROL, PublicationResource.PRIVATE_NO_STORE)
        .build();
  }
}
