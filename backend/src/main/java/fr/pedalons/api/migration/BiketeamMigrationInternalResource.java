package fr.pedalons.api.migration;

import fr.pedalons.common.exception.BiketeamMigrationRunningException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.migration.internal.BiketeamJobTriggerRequest;
import fr.pedalons.dto.migration.internal.BiketeamJobTriggerResult;
import fr.pedalons.service.migration.live.BiketeamMigrationJobService;
import fr.pedalons.service.security.annotation.BiketeamM2M;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jspecify.annotations.Nullable;

/**
 * Machine-to-machine endpoints biketeam calls over HTTPS (docs/plans/
 * 2026-09-22-biketeam-live-migration.md §5): redeem a grant into a job, then poll it.
 *
 * <p>Hidden from the public contract but reachable through the normal public entry point (no VPN,
 * §3.4), and answering a bare 404 until the feature is configured. {@link BiketeamM2M} checks the shared
 * secret before anything here runs; {@code @PermitAll} only lets the request past Quarkus security,
 * which knows nothing of that secret.
 */
@Path("/api/internal/biketeam-migration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@BiketeamM2M
@PermitAll
public class BiketeamMigrationInternalResource {

  @Inject BiketeamMigrationJobService jobService;

  /** 202 on creation, 200 on an idempotent replay of the same trigger. */
  @POST
  @Path("/jobs")
  @Operation(hidden = true)
  public Response triggerBiketeamMigrationJob(@Valid BiketeamJobTriggerRequest request) {
    try {
      BiketeamJobTriggerResult result = jobService.trigger(request);
      return Response.status(result.created() ? Response.Status.ACCEPTED : Response.Status.OK)
          .entity(result.job())
          .header(HttpHeaders.CACHE_CONTROL, "no-store")
          .build();
    } catch (BiketeamMigrationRunningException e) {
      return Response.status(Response.Status.CONFLICT)
          .entity(runningBody(e.getActiveJobId()))
          .type(MediaType.APPLICATION_JSON)
          .build();
    }
  }

  @GET
  @Path("/jobs/{jobId}")
  @Operation(hidden = true)
  public Response getBiketeamMigrationJob(@PathParam("jobId") String jobId) {
    return Response.ok(jobService.status(jobId))
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .build();
  }

  /** The usual error body, plus the job that holds the team. */
  private static Map<String, @Nullable Object> runningBody(@Nullable String activeJobId) {
    Map<String, @Nullable Object> body = new LinkedHashMap<>();
    body.put("code", ErrorCode.BIKETEAM_MIGRATION_RUNNING.name());
    body.put("activeJobId", activeJobId);
    return body;
  }
}
