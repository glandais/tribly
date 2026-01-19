package com.tribly.api.garmin;

import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.garmin.request.GarminCallbackRequest;
import com.tribly.dto.garmin.request.GarminTokenRequest;
import com.tribly.dto.garmin.response.GarminTokenResponse;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.service.garmin.GarminAuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

/**
 * OAuth 2.0 endpoints for Garmin Connect IQ authentication.
 *
 * <p>Flow: 1. GET /authorize → 302 to frontend /garmin/login 2. User authenticates on frontend 3.
 * POST /callback with user token → 302 to connectiq://oauth?code=... 4. POST /token with code →
 * access_token + refresh_token
 */
@Path("/api/garmin/oauth")
@Tag(name = "Garmin OAuth", description = "OAuth 2.0 endpoints for Garmin Connect IQ devices")
public class GarminOAuthResource {

  private static final String GARMIN_CLIENT_ID = "garmin-connect-iq";

  @Inject GarminAuthService garminAuthService;

  @GET
  @Path("/authorize")
  @PermitAll
  @Operation(
      summary = "Start OAuth authorization",
      description = "Redirects to frontend login page for Garmin OAuth flow")
  @APIResponses({
    @APIResponse(responseCode = "302", description = "Redirect to frontend login"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response authorize(
      @Parameter(description = "OAuth client ID (must be 'garmin-connect-iq')")
          @QueryParam("client_id")
          String clientId,
      @Parameter(description = "Redirect URI (connectiq://oauth)") @QueryParam("redirect_uri")
          String redirectUri,
      @Parameter(description = "Response type (must be 'code')") @QueryParam("response_type")
          String responseType,
      @Parameter(description = "State parameter for CSRF protection") @QueryParam("state")
          @Nullable String state) {

    // Validate client_id
    if (!GARMIN_CLIENT_ID.equals(clientId)) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(ErrorResponse.badRequest())
          .build();
    }

    // Validate redirect_uri (must be connectiq:// scheme for Garmin)
    if (redirectUri == null || !redirectUri.startsWith("connectiq://")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(ErrorResponse.badRequest())
          .build();
    }

    // Build frontend URL with OAuth params
    StringBuilder loginUrl = new StringBuilder(garminAuthService.getFrontendBaseUrl());
    loginUrl.append("/garmin/login");
    loginUrl.append("?client_id=").append(encode(clientId));
    loginUrl.append("&redirect_uri=").append(encode(redirectUri));
    if (state != null) {
      loginUrl.append("&state=").append(encode(state));
    }

    return Response.seeOther(URI.create(loginUrl.toString())).build();
  }

  @POST
  @Path("/complete")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Complete OAuth flow from frontend",
      description = "Receives auth from frontend and returns redirect URL with auth code")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Returns redirect URL with auth code",
        content = @Content(schema = @Schema(implementation = CallbackResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Invalid access token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response complete(@Valid GarminCallbackRequest request) {
    // Generate auth code from user's access token
    String code = garminAuthService.generateAuthCode(request.accessToken(), request.redirectUri());

    // Build redirect URL
    StringBuilder redirectUrl = new StringBuilder(request.redirectUri());
    redirectUrl.append("?code=").append(encode(code));
    if (request.state() != null) {
      redirectUrl.append("&state=").append(encode(request.state()));
    }

    // Return the redirect URL (frontend will redirect)
    return Response.ok(new CallbackResponse(redirectUrl.toString())).build();
  }

  @POST
  @Path("/token")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Exchange code for tokens",
      description = "Exchange authorization code or refresh token for access tokens")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Token response",
        content = @Content(schema = @Schema(implementation = GarminTokenResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid grant",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response token(@Valid GarminTokenRequest request) {
    GarminTokenResponse response;

    switch (request.grantType()) {
      case "authorization_code" -> {
        if (request.code() == null || request.code().isBlank()) {
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(ErrorResponse.badRequest())
              .build();
        }
        response = garminAuthService.exchangeAuthCode(request.code());
      }
      case "refresh_token" -> {
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(ErrorResponse.badRequest())
              .build();
        }
        response = garminAuthService.refreshToken(request.refreshToken());
      }
      default -> {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(ErrorResponse.badRequest())
            .build();
      }
    }

    return Response.ok(response).build();
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  @Schema(description = "OAuth callback response with redirect URL")
  @ValidateSchema
  public record CallbackResponse(
      @Schema(description = "Redirect URL with auth code", required = true) String redirectUrl) {}
}
