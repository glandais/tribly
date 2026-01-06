package com.tribly.api.assets;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.user.User;
import com.tribly.service.asset.AssetService;
import com.tribly.service.asset.response.DownloadableAsset;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

public class AbstractDownloadAssetResource extends AbstractAuthenticatedResource {

  @Inject AssetService assetService;

  @GET
  @Path("assets/{assetId}/{fileName}")
  @PermitAll
  @Operation(hidden = true)
  public Response downloadAsset(
      @PathParam("assetId") String assetId, @PathParam("fileName") String fileName) {
    User user = getCurrentUserOrNull();
    DownloadableAsset downloadableAsset = assetService.getDownloadableAsset(assetId, user);

    return Response.ok(downloadableAsset.file())
        .type(downloadableAsset.contentType())
        .header("Content-Disposition", "inline")
        .build();
  }

  @GET
  @Path("images/{assetId}/{size}")
  @PermitAll
  @Operation(hidden = true)
  public Response downloadImage(
      @HeaderParam("Accept") String accept,
      @PathParam("assetId") String assetId,
      @PathParam("size") int size) {
    User user = getCurrentUserOrNull();
    return assetService.getImage(assetId, user, size, accept);
  }
}
