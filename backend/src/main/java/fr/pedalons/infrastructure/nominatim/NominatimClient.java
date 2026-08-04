package fr.pedalons.infrastructure.nominatim;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * The OpenStreetMap geocoder, called from the server rather than from the browser.
 *
 * <p>The clients used to hit {@code nominatim.openstreetmap.org} directly. That breaks the Nominatim
 * usage policy in a way a browser cannot fix: the policy requires an identifying {@code User-Agent}
 * naming the application and a way to reach its author, and {@code User-Agent} is a forbidden header
 * for {@code fetch}. Going through the server is what makes that header — and the result cache the
 * policy also asks for — possible at all.
 */
@RegisterRestClient(configKey = "nominatim")
@Path("/search")
@ClientHeaderParam(name = "User-Agent", value = "${pedalons.geocode.user-agent}")
public interface NominatimClient {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  List<NominatimPlace> search(
      @QueryParam("q") String query,
      @QueryParam("format") String format,
      @QueryParam("limit") int limit,
      @QueryParam("accept-language") String language);
}
