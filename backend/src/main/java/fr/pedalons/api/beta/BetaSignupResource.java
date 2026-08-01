package fr.pedalons.api.beta;

import fr.pedalons.dto.beta.BetaSignupRequest;
import fr.pedalons.service.beta.BetaSignupService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/beta-signups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Beta signups", description = "Sign up to be notified about upcoming apps")
public class BetaSignupResource {

  @Inject BetaSignupService betaSignupService;

  @POST
  @PermitAll
  @Operation(
      operationId = "signUpForBeta",
      summary = "Sign up for a beta program",
      description =
          "Record interest in being added to a beta program (TestFlight, Play Console, Garmin"
              + " Connect IQ) once one opens. Submitting the same email again has no effect.")
  @APIResponses({@APIResponse(responseCode = "204", description = "Sign-up recorded")})
  public Response signUp(@Valid BetaSignupRequest request) {
    betaSignupService.signup(request);
    return Response.noContent().build();
  }
}
