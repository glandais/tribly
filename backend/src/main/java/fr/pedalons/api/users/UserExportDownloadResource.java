package fr.pedalons.api.users;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.users.response.DownloadableExport;
import fr.pedalons.service.user.UserExportService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Download endpoint for a completed GDPR data export.
 *
 * <p>Only its owner, signed in, downloads it: the emailed token proves the mailbox, the session the
 * account. {@code @PermitAll} only because the link is opened straight from an email client — a
 * browser navigation, which carries the session cookie but no bearer. A visitor without a session
 * is sent to sign in and brought back.
 */
@Path("/api/export")
@Tag(name = "Users", description = "User profile management operations")
public class UserExportDownloadResource {

  @Inject UserExportService userExportService;

  @GET
  @Path("/download/{token}")
  @PermitAll
  @Produces("application/zip")
  @Operation(
      operationId = "downloadDataExport",
      summary = "Download a personal data export",
      description =
          "Download a prepared data export archive using the token from the notification email."
              + " Only its owner, signed in, may download it.")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "The export archive"),
    @APIResponse(responseCode = "303", description = "No session: sent to sign in first"),
    @APIResponse(
        responseCode = "404",
        description = "Unknown, expired or already-purged export",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response download(
      @Parameter(description = "Download token from the notification email", required = true)
          @PathParam("token")
          String token) {
    URI signIn = userExportService.signInFirst(token);
    if (signIn != null) {
      return Response.seeOther(signIn).build();
    }
    DownloadableExport export = userExportService.download(token);

    Response.ResponseBuilder response =
        Response.ok(export.content())
            .type("application/zip")
            .header("Content-Disposition", "attachment; filename=\"" + export.fileName() + "\"");
    Long size = export.size();
    if (size != null) {
      response.header("Content-Length", size);
    }
    return response.build();
  }
}
