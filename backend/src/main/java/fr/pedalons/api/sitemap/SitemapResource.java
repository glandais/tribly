package fr.pedalons.api.sitemap;

import fr.pedalons.dto.sitemap.SitemapDto;
import fr.pedalons.service.sitemap.SitemapService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/sitemap")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Tag(name = "Sitemap", description = "Pages a search engine may index")
public class SitemapResource {

  @Inject SitemapService sitemapService;

  @GET
  @Operation(
      summary = "Get the indexable pages of this site",
      description =
          "Public teams and their public content, as an anonymous visitor sees it, for the site the"
              + " request arrived on (a pinned host lists its one team). Classified ads are never"
              + " listed. Anonymous by construction: the caller's session does not widen it.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Indexable pages",
        content = @Content(schema = @Schema(implementation = SitemapDto.class)))
  })
  public Response getSitemap() {
    return Response.ok(sitemapService.getSitemap()).build();
  }
}
