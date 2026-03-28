package fr.pedalons.infrastructure.brouter;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "brouter")
@Path("/")
public interface BRouterClient {

  @GET
  RouterResult route(
      @QueryParam("lonlats") String lonlats,
      @QueryParam("profile") String profile,
      @QueryParam("alternativeidx") Integer alternativeidx,
      @QueryParam("format") String format);
}
