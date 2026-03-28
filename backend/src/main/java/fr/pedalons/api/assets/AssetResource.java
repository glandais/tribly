package fr.pedalons.api.assets;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.common.asset.AssetDto;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.asset.AssetService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.FileInputStream;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jspecify.annotations.Nullable;

@Path("/api/teams/{teamSlug}/assets")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Assets", description = "Assets management operations")
@RolesAllowed("user")
public class AssetResource {
  @Inject AssetService assetService;

  /**
   * Create a new route with GPX upload.
   * Uses multipart/form-data for file upload.
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Operation(summary = "Create asset")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Asset created successfully",
        content = @Content(schema = @Schema(implementation = AssetDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request or file",
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
  public Response uploadAsset(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @RestForm("file") @Nullable FileUpload fileUpload)
      throws Exception {

    // Validate file
    if (fileUpload == null || fileUpload.filePath() == null) {
      throw new BusinessException(ErrorCode.FILE_REQUIRED);
    }
    String fileName = fileUpload.fileName();

    AssetDto assetDto =
        assetService.createAsset(
            teamSlug, new FileInputStream(fileUpload.filePath().toFile()), fileName);

    return Response.status(Response.Status.CREATED).entity(assetDto).build();
  }
}
