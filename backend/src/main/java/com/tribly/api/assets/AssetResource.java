package com.tribly.api.assets;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.dto.common.response.AssetDto;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.asset.AssetService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.FileInputStream;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jspecify.annotations.Nullable;

@Path("/api/teams/{slug}/assets")
@Produces(MediaType.APPLICATION_JSON)
public class AssetResource extends AbstractAuthenticatedResource {
  @Inject AssetService assetService;

  /**
   * Create a new route with GPX upload.
   * Uses multipart/form-data for file upload.
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @RolesAllowed("user")
  @Operation(hidden = true)
  public Response uploadAsset(
      @PathParam("slug") String teamSlug, @RestForm("file") @Nullable FileUpload fileUpload)
      throws Exception {

    Long userId = getCurrentUserId();

    // Validate file
    if (fileUpload == null || fileUpload.filePath() == null) {
      throw BusinessException.validation("File is required");
    }
    String fileName = fileUpload.fileName();

    AssetDto assetDto =
        assetService.createAsset(
            teamSlug, userId, new FileInputStream(fileUpload.filePath().toFile()), fileName);

    return Response.status(Response.Status.CREATED).entity(assetDto).build();
  }
}
