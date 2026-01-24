package com.tribly.api.assets;

import com.tribly.common.TsidUtils;
import com.tribly.dto.assets.response.DownloadableAsset;
import com.tribly.service.asset.AssetService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

public class AbstractDownloadAssetResource {

  @Inject AssetService assetService;

  @GET
  @Path("assets/{teamSlug}/{assetId}/{fileName}")
  @PermitAll
  @Operation(hidden = true)
  public Response downloadAsset(
      @PathParam("teamSlug") String teamSlug,
      @PathParam("assetId") String assetId,
      @PathParam("fileName") String fileName) {

    DownloadableAsset downloadableAsset =
        assetService.getDownloadableAsset(teamSlug, TsidUtils.toLong(assetId));

    return Response.ok(downloadableAsset.content())
        .type(downloadableAsset.contentType())
        .header("Content-Disposition", "inline")
        .build();
  }

  @GET
  @Path("images/{teamSlug}/{assetId}/{size}")
  @PermitAll
  @Operation(hidden = true)
  public Response downloadImage(
      @HeaderParam("Accept") String accept,
      @PathParam("teamSlug") String teamSlug,
      @PathParam("assetId") String assetId,
      @PathParam("size") int size) {

    return assetService.getImage(teamSlug, TsidUtils.toLong(assetId), size, accept);
  }
}
