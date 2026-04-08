package fr.pedalons.infrastructure.email;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "brevo")
@Path("/v3/smtp")
public interface BrevoRestClient {

  @POST
  @Path("/email")
  @Consumes(MediaType.APPLICATION_JSON)
  void send(@HeaderParam("api-key") String apiKey, BrevoEmailRequest request);
}
