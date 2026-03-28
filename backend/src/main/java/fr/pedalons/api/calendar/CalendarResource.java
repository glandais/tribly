package fr.pedalons.api.calendar;

import fr.pedalons.dto.calendar.request.AuthMode;
import fr.pedalons.dto.calendar.response.CalendarEventsResponse;
import fr.pedalons.dto.calendar.response.CalendarTokenDto;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.calendar.CalendarService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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

@Path("/api/calendar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Calendar", description = "Calendar and ICS feed operations")
public class CalendarResource {

  @Inject CalendarService calendarService;

  @GET
  @Path("/events")
  @RolesAllowed("user")
  @Operation(
      summary = "Get calendar events",
      description = "Get calendar events for all teams the user belongs to")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Events retrieved successfully",
        content = @Content(schema = @Schema(implementation = CalendarEventsResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getEvents(
      @Parameter(description = "Start date (ISO 8601)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date (ISO 8601)") @QueryParam("to") @Nullable String toStr) {

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    CalendarEventsResponse events = calendarService.getEventsForUser(AuthMode.WEB, from, to);
    return Response.ok(events).build();
  }

  @GET
  @Path("/token")
  @RolesAllowed("user")
  @Operation(
      summary = "Get calendar token",
      description = "Get or create the user's calendar token for ICS feed access")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Token retrieved successfully",
        content = @Content(schema = @Schema(implementation = CalendarTokenDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getToken() {
    CalendarTokenDto token = calendarService.getOrCreateToken();
    return Response.ok(token).build();
  }

  @POST
  @Path("/token/regenerate")
  @RolesAllowed("user")
  @Operation(
      summary = "Regenerate calendar token",
      description = "Regenerate the user's calendar token, invalidating the old one")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Token regenerated successfully",
        content = @Content(schema = @Schema(implementation = CalendarTokenDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response regenerateToken() {
    CalendarTokenDto token = calendarService.regenerateToken();
    return Response.ok(token).build();
  }

  @GET
  @Path("/ics")
  @PermitAll
  @Produces("text/calendar")
  @Operation(
      summary = "Get global ICS feed",
      description = "Get ICS calendar feed for all user's teams (requires token)")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "ICS feed retrieved successfully"),
    @APIResponse(
        responseCode = "403",
        description = "Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getGlobalIcsFeed(
      @Parameter(description = "Calendar access token") @QueryParam("token") String token) {

    String icsContent = calendarService.generateIcs(AuthMode.TOKEN, token);

    return Response.ok(icsContent)
        .type("text/calendar; charset=utf-8")
        .header("Content-Disposition", "inline; filename=\"pedalons-calendar.ics\"")
        .header("Cache-Control", "no-cache, no-store, must-revalidate")
        .header("Pragma", "no-cache")
        .header("Expires", "0")
        .build();
  }
}
