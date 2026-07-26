package fr.pedalons.api.calendar;

import fr.pedalons.dto.calendar.request.AuthMode;
import fr.pedalons.dto.calendar.response.CalendarEventsResponse;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.calendar.CalendarService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/teams/{teamSlug}/calendar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Calendar", description = "Team calendar operations")
public class TeamCalendarResource {

  @Inject CalendarService calendarService;

  @GET
  @Path("/events")
  @PermitAll
  @Operation(
      summary = "Get team calendar events",
      description = "Get calendar events for a specific team")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Events retrieved successfully",
        content = @Content(schema = @Schema(implementation = CalendarEventsResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getTeamEvents(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Start date (ISO 8601)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date (ISO 8601)") @QueryParam("to") @Nullable String toStr) {

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    CalendarEventsResponse events =
        calendarService.getEventsForTeam(AuthMode.WEB, teamSlug, from, to);
    // Each event carries "registered" and "groupName": the answer is specific to the caller and
    // must never be served to another one from a shared cache.
    return Response.ok(events).header(HttpHeaders.CACHE_CONTROL, "private, no-store").build();
  }

  @GET
  @Path("/ics")
  @PermitAll
  @Produces("text/calendar")
  @Operation(
      summary = "Get team ICS feed",
      description = "Get ICS calendar feed for a specific team (requires token)")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "ICS feed retrieved successfully"),
    @APIResponse(
        responseCode = "403",
        description = "Invalid token or user not member of team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getTeamIcsFeed(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Calendar access token") @QueryParam("token") String token) {

    String icsContent = calendarService.generateIcsForTeam(AuthMode.TOKEN, token, teamSlug);

    return Response.ok(icsContent)
        .type("text/calendar; charset=utf-8")
        .header(
            "Content-Disposition", "inline; filename=\"pedalons-" + teamSlug + "-calendar.ics\"")
        .header("Cache-Control", "no-cache, no-store, must-revalidate")
        .header("Pragma", "no-cache")
        .header("Expires", "0")
        .build();
  }
}
