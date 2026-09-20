package fr.pedalons.infrastructure.push;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * FCM HTTP v1 — one message, one device token, one call. The legacy server-key API is gone, so the
 * Bearer token comes from {@link GoogleOAuthRestClient}.
 *
 * <p>{@code Response} again, because the body of a 404 is what says whether the token is dead
 * ({@code UNREGISTERED}) or the request was wrong.
 */
@RegisterRestClient(configKey = "fcm")
public interface FcmRestClient {

  @POST
  @Path("/v1/projects/{projectId}/messages:send")
  @Consumes(MediaType.APPLICATION_JSON)
  Response send(
      @PathParam("projectId") String projectId,
      @HeaderParam("Authorization") String authorization,
      Map<String, Object> body);
}
