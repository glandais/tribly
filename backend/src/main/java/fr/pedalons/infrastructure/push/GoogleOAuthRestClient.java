package fr.pedalons.infrastructure.push;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Google's OAuth 2 token endpoint, used with the JWT-bearer grant: we sign an assertion with the
 * service account's key and trade it for a short-lived access token.
 *
 * <p>Returns {@code Response} rather than a typed body so a 4xx is read, not thrown: an expired or
 * revoked key must produce a diagnosable message, not a bare {@code WebApplicationException}.
 */
@RegisterRestClient(configKey = "google-oauth")
public interface GoogleOAuthRestClient {

  String JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";

  @POST
  @Path("/token")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  Response token(
      @FormParam("grant_type") String grantType, @FormParam("assertion") String assertion);
}
