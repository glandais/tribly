package fr.pedalons.infrastructure.github;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * The four calls of the GitHub issues API the feedback pipeline needs. {@code Response}, as in
 * {@code FcmRestClient}: {@link GitHubIssueClient} reads the status itself rather than have a 404
 * or a 422 thrown at it.
 *
 * <p>GitHub rejects requests without a {@code User-Agent}.
 */
@RegisterRestClient(configKey = "github")
@Path("/repos/{owner}/{repo}/issues")
@Consumes(MediaType.APPLICATION_JSON)
@Produces("application/vnd.github+json")
@ClientHeaderParam(name = "User-Agent", value = "pedalons-feedback")
@ClientHeaderParam(name = "X-GitHub-Api-Version", value = "2022-11-28")
public interface GitHubRestClient {

  @POST
  Response createIssue(
      @PathParam("owner") String owner,
      @PathParam("repo") String repo,
      @HeaderParam("Authorization") String authorization,
      Map<String, Object> body);

  @GET
  @Path("/{number}")
  Response getIssue(
      @PathParam("owner") String owner,
      @PathParam("repo") String repo,
      @PathParam("number") int number,
      @HeaderParam("Authorization") String authorization);

  @PATCH
  @Path("/{number}")
  Response updateIssue(
      @PathParam("owner") String owner,
      @PathParam("repo") String repo,
      @PathParam("number") int number,
      @HeaderParam("Authorization") String authorization,
      Map<String, Object> body);

  @POST
  @Path("/{number}/comments")
  Response comment(
      @PathParam("owner") String owner,
      @PathParam("repo") String repo,
      @PathParam("number") int number,
      @HeaderParam("Authorization") String authorization,
      Map<String, Object> body);
}
