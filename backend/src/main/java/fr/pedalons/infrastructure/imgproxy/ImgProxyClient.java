package fr.pedalons.infrastructure.imgproxy;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "imgproxy")
@Path("/")
public interface ImgProxyClient {

  @GET
  @Path("/insecure/rs:fit:{width}:{height}/plain/{path}")
  Response getImage(
      @HeaderParam("Accept") String accept,
      @PathParam("width") int width,
      @PathParam("height") int height,
      @PathParam("path") @Encoded String path);

  @GET
  @Path("/insecure/rs:fit:{width}:{height}/plain/{path}")
  InputStream getImageContent(
      @HeaderParam("Accept") String accept,
      @PathParam("width") int width,
      @PathParam("height") int height,
      @PathParam("path") @Encoded String path);

  /**
   * The image at {@code path}, decoded and written again as {@code extension} at its full size:
   * without any metadata ({@code sm:1}, {@code kcr:0} — imgproxy keeps the copyright by default),
   * rotated upright ({@code ar:1}). The format is forced by the extension, so no {@code Accept}
   * negotiation applies. docs/LEDGER_*.md API-43.
   */
  @GET
  @Path("/insecure/sm:1/kcr:0/ar:1/q:90/plain/{path}@{extension}")
  byte[] reencode(
      @PathParam("path") @Encoded String path, @PathParam("extension") String extension);
}
