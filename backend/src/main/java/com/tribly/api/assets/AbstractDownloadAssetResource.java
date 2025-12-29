package com.tribly.api.assets;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.service.asset.AssetService;
import com.tribly.service.asset.response.DownloadableAsset;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

public class AbstractDownloadAssetResource extends AbstractAuthenticatedResource {

  @Inject AssetService assetService;

  /**
   * Download filtered GPX file.
   */
  @GET
  @PermitAll
  @Operation(hidden = true)
  public Response downloadAsset(
      @PathParam("assetId") String assetId, @PathParam("fileName") String fileName) {
    Long userId = getCurrentUserIdOrNull();
    DownloadableAsset downloadableAsset = assetService.getDownloadableAsset(assetId, userId);

    return Response.ok(downloadableAsset.file())
        .type(downloadableAsset.contentType())
        .header("Content-Disposition", "inline")
        .build();
  }
}
