package fr.pedalons.api.auth;

import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.dto.auth.response.AuthResponse;
import fr.pedalons.dto.auth.response.AuthResult;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.social.request.StravaSessionRequest;
import fr.pedalons.dto.social.response.StravaAuthUrlResponse;
import fr.pedalons.service.social.StravaAuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/auth/strava")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Strava Authentication", description = "Login with Strava and account recovery/binding")
public class StravaAuthResource {

  private static final String REFRESH_TOKEN_COOKIE = "refresh_token";
  private static final String CALLBACK_LANDING = "/strava/callback";

  @Inject StravaAuthService stravaAuthService;

  @ConfigProperty(name = "pedalons.auth.refresh-token.expiry-days", defaultValue = "30")
  int refreshTokenExpiryDays;

  @ConfigProperty(name = "pedalons.auth.cookie.secure", defaultValue = "true")
  boolean cookieSecure;

  @ConfigProperty(name = "pedalons.auth.cookie.same-site", defaultValue = "strict")
  String cookieSameSite;

  @GET
  @Path("/login-url")
  @PermitAll
  @Operation(
      operationId = "getStravaLoginUrl",
      summary = "Get Strava login URL",
      description = "Get the Strava OAuth authorization URL to log in / recover an account")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Authorization URL",
        content = @Content(schema = @Schema(implementation = StravaAuthUrlResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Strava not configured",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getLoginUrl() {
    return Response.ok(stravaAuthService.initiateLogin()).build();
  }

  @GET
  @Path("/connect")
  @RolesAllowed("user")
  @Operation(
      operationId = "getStravaConnectUrl",
      summary = "Get Strava link URL",
      description = "Get the Strava OAuth authorization URL to link Strava to the current account")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Authorization URL",
        content = @Content(schema = @Schema(implementation = StravaAuthUrlResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Already linked or Strava not configured",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getConnectUrl() {
    return Response.ok(stravaAuthService.initiateLink()).build();
  }

  @GET
  @Path("/callback")
  @PermitAll
  @Operation(
      operationId = "stravaCallback",
      summary = "Strava OAuth callback",
      description = "Handles the Strava OAuth redirect and hands off to the frontend")
  @APIResponses({@APIResponse(responseCode = "302", description = "Redirects to frontend")})
  public Response handleCallback(
      @QueryParam("code") @Nullable String code,
      @QueryParam("state") @Nullable String state,
      @QueryParam("error") @Nullable String error) {

    if (error != null) {
      return errorRedirect("access_denied");
    }
    if (code == null || state == null) {
      return errorRedirect("missing_params");
    }

    try {
      String redirectUrl = stravaAuthService.handleCallback(code, state);
      return Response.temporaryRedirect(URI.create(redirectUrl)).build();
    } catch (PedalonsException e) {
      String reason =
          e.getErrorCode() == ErrorCode.SOCIAL_NO_ACCOUNT ? "no_account" : "callback_failed";
      return errorRedirect(reason);
    } catch (Exception e) {
      return errorRedirect("callback_failed");
    }
  }

  @POST
  @Path("/session")
  @PermitAll
  @Operation(
      operationId = "createStravaSession",
      summary = "Exchange Strava login code for a session",
      description =
          "Exchange the one-time code from the Strava callback for an authenticated session")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Login successful",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid or expired code",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createSession(
      @Valid StravaSessionRequest request,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    String userAgent = headers.getHeaderString(HttpHeaders.USER_AGENT);
    String ipAddress = getClientIp(forwardedFor, realIp);

    AuthResult result = stravaAuthService.exchangeLoginCode(request.code(), userAgent, ipAddress);
    return Response.ok(result.response())
        .cookie(createRefreshTokenCookie(result.refreshToken()))
        .build();
  }

  @DELETE
  @Path("/identity")
  @RolesAllowed("user")
  @Operation(
      operationId = "unlinkStrava",
      summary = "Unlink Strava",
      description = "Remove the Strava identity from the account")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Strava unlinked"),
    @APIResponse(
        responseCode = "400",
        description = "Not linked or Strava is the only login method",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response unlink() {
    stravaAuthService.unlinkStrava();
    return Response.noContent().build();
  }

  private Response errorRedirect(String reason) {
    return Response.temporaryRedirect(
            URI.create(
                stravaAuthService.getFrontendBaseUrl()
                    + CALLBACK_LANDING
                    + "?strava_error="
                    + reason))
        .build();
  }

  private NewCookie.SameSite parsedSameSite() {
    try {
      return NewCookie.SameSite.valueOf(cookieSameSite.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(
          "Invalid pedalons.auth.cookie.same-site value: '"
              + cookieSameSite
              + "'. Valid values: STRICT, LAX, NONE",
          e);
    }
  }

  private NewCookie createRefreshTokenCookie(String refreshToken) {
    return new NewCookie.Builder(REFRESH_TOKEN_COOKIE)
        .value(refreshToken)
        .path("/api")
        .maxAge(refreshTokenExpiryDays * 24 * 60 * 60)
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite(parsedSameSite())
        .build();
  }

  private String getClientIp(@Nullable String forwardedFor, @Nullable String realIp) {
    if (forwardedFor != null && !forwardedFor.isBlank()) {
      return forwardedFor.split(",")[0].trim();
    }
    if (realIp != null && !realIp.isBlank()) {
      return realIp;
    }
    return "unknown";
  }
}
