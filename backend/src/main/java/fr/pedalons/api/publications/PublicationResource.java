package fr.pedalons.api.publications;

import fr.pedalons.dto.common.CountResponse;
import fr.pedalons.dto.publications.response.PublicationListResponse;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.enums.ListViewMode;
import fr.pedalons.enums.Status;
import fr.pedalons.service.common.PublicationService;
import fr.pedalons.service.team.request.MinRole;
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

@Path("/api/publications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Publications", description = "Publication listing")
public class PublicationResource {

  /** Publication rows carry per-user fields, so no shared cache may reuse this response. */
  static final String PRIVATE_NO_STORE = "private, no-store";

  /** Shared by every list endpoint that accepts {@code view}, so they cannot describe it apart. */
  static final String VIEW_PARAM_DESCRIPTION =
      "How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and"
          + " media.assets trimmed to the logo, the first image and the themed thumbnails — read"
          + " 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. The"
          + " markdown body, the attachments, the GPX and FIT files and every image past the first"
          + " are dropped. Omitted, or FULL, is the previous behaviour, byte for byte.";

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

    PublicationListResponse response =
        publicationService.listAll(
            type, search, from, to, minRole, status, participating, view, page, size);

    // Rows carry per-user fields (registered, registeredGroupId): never let a shared cache keep
    // one user's answer for the next one.
    return Response.ok(response).header(HttpHeaders.CACHE_CONTROL, PRIVATE_NO_STORE).build();
  }

  @GET
  @PermitAll
  @Path("/count")
  @Operation(
      operationId = "countAllPublications",
      summary = "Count all publications",
      description =
          "How many publications match the filters, with none of them read. Accepts exactly the"
              + " same filters as the listing, minus pagination, so a count and the list it opens"
              + " can never disagree.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Count computed successfully",
        content = @Content(schema = @Schema(implementation = CountResponse.class)))
  })
  public Response countAllPublications(
      @Parameter(description = "Types") @QueryParam("type") @Nullable PublicationType type,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(
              description =
                  "Only publications from teams where the user has at least this role. Yields zero"
                      + " for an anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
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
        publicationService.countAll(type, search, from, to, minRole, status, participating);

    // The figure depends on who is asking — both through the visibility rules and through
    // 'participating': not shareable between users.
    return Response.ok(count).header(HttpHeaders.CACHE_CONTROL, PRIVATE_NO_STORE).build();
  }
}
