package fr.pedalons.api.config;

import fr.pedalons.service.config.MapStyleService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Serves the MapLibre style documents the server generates for the raster basemaps listed in {@code
 * GET /api/config}.
 *
 * <p>Public and cacheable: a style document is deployment configuration, identical for every visitor
 * of the instance, and it is fetched by the map engine itself — MapLibre takes a URL and issues its
 * own request, with none of our headers on it, so anything but {@code PermitAll} would simply never
 * load.
 */
@Path("/api/map/styles")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Tag(name = "Configuration", description = "Application configuration endpoints")
public class MapStyleResource {

  /** A day: the list only changes on a restart, and a stale basemap is harmless. */
  private static final String CACHE_CONTROL = "public, max-age=86400";

  @Inject MapStyleService mapStyleService;

  @GET
  @Path("{id}.json")
  @Operation(
      summary = "Get a generated map style",
      description =
          "Returns the MapLibre style document for a raster basemap whose provider serves tiles"
              + " only. The URL is the one already carried by MapStyleDto.url — clients hand it to"
              + " the map engine rather than building it themselves.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "The MapLibre style document",
        content =
            @Content(
                schema =
                    @Schema(
                        type = SchemaType.OBJECT,
                        description = "A MapLibre GL style specification document"))),
    @APIResponse(responseCode = "404", description = "No generated style with this id")
  })
  public Response getStyle(@PathParam("id") String id) {
    return Response.ok(mapStyleService.generatedStyle(id))
        .header("Cache-Control", CACHE_CONTROL)
        .build();
  }
}
