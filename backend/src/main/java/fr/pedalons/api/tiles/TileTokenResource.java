package fr.pedalons.api.tiles;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.tiles.response.TileTokenDto;
import fr.pedalons.service.security.TileTokenService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/tiles")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Tiles", description = "Vector tile access")
public class TileTokenResource {

  @Inject TileTokenService tileTokenService;

  /**
   * Mints a tile token for the caller.
   *
   * <p>POST rather than GET because it mints a credential: POST says "do not cache, do not replay".
   * It takes no parameter — the token is bound to the user and the domain, never to the tile
   * filters, which stay in the tile URL in the clear where they already are.
   */
  @POST
  @Path("/token")
  @RolesAllowed("user")
  @Operation(
      summary = "Mint a tile token",
      description =
          "Short-lived token authorizing the caller's own vector tile requests, to be passed as the"
              + " 't' query parameter of the .mvt endpoints. Meant for clients whose map renderer"
              + " has its own HTTP stack and can carry neither an Authorization header nor a"
              + " session cookie. It grants nothing but tiles: no other endpoint accepts it.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Token minted successfully",
        content = @Content(schema = @Schema(implementation = TileTokenDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response mint() {
    return Response.ok(tileTokenService.mintForCurrentUser())
        .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
        .build();
  }
}
