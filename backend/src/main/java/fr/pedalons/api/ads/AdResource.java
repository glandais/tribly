package fr.pedalons.api.ads;

import fr.pedalons.dto.ads.request.AdContactRequest;
import fr.pedalons.dto.ads.request.AdRequest;
import fr.pedalons.dto.ads.request.AdSearchParams;
import fr.pedalons.dto.ads.response.AdDto;
import fr.pedalons.dto.ads.response.AdEditDto;
import fr.pedalons.dto.ads.response.AdListResponse;
import fr.pedalons.dto.common.request.SlugChangeRequest;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.enums.AdSortBy;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.ListViewMode;
import fr.pedalons.enums.SortDirection;
import fr.pedalons.service.ad.AdService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/teams/{teamSlug}/classifieds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Ads", description = "Ad management operations")
@RolesAllowed("user")
public class AdResource {

  @Inject AdService adService;

  @GET
  @Operation(
      summary = "List ads",
      description = "Get paginated list of ads for a team with optional filtering")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ads retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listAds(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Search by name/description") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Filter by ad type") @QueryParam("adType") @Nullable AdType adType,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(
              description =
                  "Lowest asking price to include. Ads with no price ('à négocier') are excluded by"
                      + " either price bound.")
          @QueryParam("minPrice")
          @Nullable BigDecimal minPrice,
      @Parameter(description = "Highest asking price to include") @QueryParam("maxPrice")
          @Nullable BigDecimal maxPrice,
      @Parameter(description = "Latitude for proximity search") @QueryParam("nearLat")
          @Nullable Double nearLat,
      @Parameter(description = "Longitude for proximity search") @QueryParam("nearLon")
          @Nullable Double nearLon,
      @Parameter(
              description =
                  "Search radius in metres around nearLat/nearLon (default 25000, capped at"
                      + " 500000). Ads with no location are excluded when a centre is given.")
          @QueryParam("nearRadius")
          @Nullable Double nearRadius,
      @Parameter(description = "Sort column (default: publication date)") @QueryParam("sortBy")
          @Nullable AdSortBy sortBy,
      @Parameter(description = "Sort direction (default: DESC)") @QueryParam("sortDir")
          @Nullable SortDirection sortDir,
      @Parameter(
              description =
                  "How much of each row to send. COMPACT (case-insensitive) returns media.markdown"
                      + " empty and media.assets trimmed to the logo, the first image and the"
                      + " themed thumbnails — read 'excerpt', 'thumbnailUrl' and 'images' instead,"
                      + " all of which are present either way. The markdown body, the attachments,"
                      + " the GPX and FIT files and every image past the first are dropped."
                      + " Omitted, or FULL, is the previous behaviour, byte for byte.")
          @QueryParam("view")
          @Nullable ListViewMode view,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    AdSearchParams params =
        AdSearchParams.builder()
            .search(search)
            .adType(adType)
            .from(from)
            .to(to)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .nearLat(nearLat)
            .nearLon(nearLon)
            .nearRadius(nearRadius)
            .sortBy(sortBy)
            .sortDir(sortDir)
            .build();
    AdListResponse ads = adService.listAds(teamSlug, params, view, page, size);

    return Response.ok(ads).build();
  }

  @POST
  @Operation(
      summary = "Create ad",
      description = "Create a new ad. Any team member can create ads.")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Ad created successfully",
        content = @Content(schema = @Schema(implementation = AdDto.class))),
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
        description = "User is not a team member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createAd(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid AdRequest request) {

    AdDto ad = adService.createAd(teamSlug, request);

    return Response.status(Response.Status.CREATED).entity(ad).build();
  }

  @POST
  @Path("/{slug}/contact")
  @Operation(
      operationId = "contactAdAuthor",
      summary = "Contact an ad's author",
      description =
          "Relays a message to the author of an ad. Neither party's address appears anywhere in"
              + " this API: the server sends the email and sets Reply-To to the caller, so the"
              + " author can answer directly. Writing therefore discloses the caller's address to"
              + " the author — a one-way disclosure the caller chose — while the author's address"
              + " is never disclosed at all. Requires the same access as reading the ad.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Message relayed"),
    @APIResponse(
        responseCode = "400",
        description =
            "VALIDATION when the message is missing or outside the allowed length,"
                + " AD_CONTACT_SELF when writing to your own ad, AD_CONTACT_OPTED_OUT when the"
                + " author turned the relay off. These two are rules about the message, not"
                + " authorization failures — the caller may use this endpoint on this ad, they"
                + " just may not send this particular message.",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Caller may not read this ad",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "429",
        description = "AD_CONTACT_RATE_LIMITED — too many messages relayed in the window",
        headers =
            @Header(
                name = "Retry-After",
                description = "Seconds until the sender's quota frees up",
                schema = @Schema(type = SchemaType.INTEGER)),
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "500",
        description =
            "AD_CONTACT_DELIVERY_FAILED — the mail provider refused the message. Nothing was"
                + " recorded and the attempt does not count against the quota.",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response contactAdAuthor(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ad URL slug") @PathParam("slug") String slug,
      @Valid AdContactRequest request) {

    adService.contactAuthor(teamSlug, slug, request);

    return Response.noContent().build();
  }

  @GET
  @Path("/{slug}")
  @Operation(summary = "Get ad details", description = "Get detailed ad information")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ad retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getAd(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ad URL slug") @PathParam("slug") String slug) {

    AdDto ad = adService.getDto(teamSlug, slug);
    return Response.ok(ad).build();
  }

  @GET
  @Path("/{slug}/edit")
  @Operation(summary = "Get ad details for edit", description = "Get detailed ad information")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ad retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdEditDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getAdEdit(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ad URL slug") @PathParam("slug") String slug) {

    AdEditDto ad = adService.getDtoEdit(teamSlug, slug);
    return Response.ok(ad).build();
  }

  @PUT
  @Path("/{slug}")
  @Transactional
  @Operation(
      summary = "Update ad",
      description = "Update ad information. Only the creator or an admin can update.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ad updated successfully",
        content = @Content(schema = @Schema(implementation = AdDto.class))),
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
        description = "User is not authorized to update this ad",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateAd(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ad URL slug") @PathParam("slug") String slug,
      @Valid AdRequest request) {

    AdDto updatedAd = adService.updateAd(teamSlug, slug, request);

    return Response.ok(updatedAd).build();
  }

  @DELETE
  @Path("/{slug}")
  @Operation(
      summary = "Delete ad",
      description = "Soft delete an ad. Only the creator or an admin can delete.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Ad deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to delete this ad",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteAd(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ad URL slug") @PathParam("slug") String slug) {

    adService.deleteAd(teamSlug, slug);
    return Response.noContent().build();
  }

  @POST
  @Path("/{slug}/undelete")
  @Operation(
      operationId = "undeleteAd",
      summary = "Restore ad",
      description = "Restore a soft-deleted ad. Only the creator or an admin can restore.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ad restored successfully",
        content = @Content(schema = @Schema(implementation = AdEditDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this ad",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response undeleteAd(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ad URL slug") @PathParam("slug") String slug) {
    AdEditDto dto = adService.undeleteAd(teamSlug, slug);
    return Response.ok(dto).build();
  }

  @PATCH
  @Path("/{slug}/slug")
  @Operation(
      operationId = "changeAdSlug",
      summary = "Change ad slug",
      description = "Change ad URL slug. Requires admin permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = AdDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid slug format",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to change this ad's slug",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Slug already in use",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Current ad URL slug") @PathParam("slug") String slug,
      @Valid SlugChangeRequest request) {

    AdDto ad = adService.updateSlug(teamSlug, slug, request.slug());
    return Response.ok(ad).build();
  }
}
