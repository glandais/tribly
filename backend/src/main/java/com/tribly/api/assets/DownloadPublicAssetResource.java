package com.tribly.api.assets;

import com.tribly.service.user.UserAvatarService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/api/download/public")
public class DownloadPublicAssetResource extends AbstractDownloadAssetResource {

  @Inject UserAvatarService userAvatarService;

  @GET
  @Path("/avatars/{fileId}/{size}")
  @Operation(hidden = true)
  @PermitAll
  public Response getAvatar(
      @HeaderParam("Accept") String accept,
      @PathParam("fileId") String fileId,
      @PathParam("size") int size) {
    return userAvatarService.getAvatar(fileId, size, accept);
  }
}
