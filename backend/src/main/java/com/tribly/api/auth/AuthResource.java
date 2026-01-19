package com.tribly.api.auth;

import com.tribly.dto.auth.request.MagicLinkRequest;
import com.tribly.dto.auth.request.RegisterRequest;
import com.tribly.dto.auth.request.VerifyTokenRequest;
import com.tribly.dto.auth.response.AuthResponse;
import com.tribly.dto.auth.response.AuthResult;
import com.tribly.dto.auth.response.MessageResponse;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.service.auth.AuthService;
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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "User authentication operations")
public class AuthResource {

  private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

  @Inject AuthService authService;

  @ConfigProperty(name = "tribly.auth.refresh-token.expiry-days", defaultValue = "30")
  int refreshTokenExpiryDays;

  @ConfigProperty(name = "tribly.auth.cookie.secure", defaultValue = "true")
  boolean cookieSecure;

  @ConfigProperty(name = "tribly.auth.cookie.same-site", defaultValue = "strict")
  String cookieSameSite;

  @POST
  @Path("/register")
  @PermitAll
  @Operation(
      summary = "Register new user",
      description = "Register a new user. A verification email will be sent.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Registration initiated, verification email sent",
        content = @Content(schema = @Schema(implementation = MessageResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request or email already registered",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response register(@Valid RegisterRequest request) {
    authService.register(request);
    return Response.ok(new MessageResponse("Verification email sent")).build();
  }

  @POST
  @Path("/verify-email")
  @PermitAll
  @Operation(
      summary = "Verify email",
      description = "Verify email address and complete registration")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Email verified successfully",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid or expired token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response verifyEmail(
      @Valid VerifyTokenRequest request,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    String userAgent = headers.getHeaderString(HttpHeaders.USER_AGENT);
    String ipAddress = getClientIp(forwardedFor, realIp);

    AuthResult result = authService.verifyEmail(request.token(), userAgent, ipAddress);
    return Response.ok(result.response())
        .cookie(createRefreshTokenCookie(result.refreshToken()))
        .build();
  }

  @POST
  @Path("/magic-link")
  @PermitAll
  @Operation(
      summary = "Request magic link",
      description = "Send a magic link to the user's email for passwordless login")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Magic link sent (if email exists)",
        content = @Content(schema = @Schema(implementation = MessageResponse.class)))
  })
  public Response requestMagicLink(@Valid MagicLinkRequest request) {
    authService.requestMagicLink(request);
    return Response.ok(new MessageResponse("If the email exists, a login link has been sent"))
        .build();
  }

  @POST
  @Path("/magic-link/verify")
  @PermitAll
  @Operation(
      summary = "Verify magic link",
      description = "Verify magic link token and authenticate")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Login successful",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid or expired token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response verifyMagicLink(
      @Valid VerifyTokenRequest request,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    String userAgent = headers.getHeaderString(HttpHeaders.USER_AGENT);
    String ipAddress = getClientIp(forwardedFor, realIp);

    AuthResult result = authService.verifyMagicLink(request.token(), userAgent, ipAddress);
    return Response.ok(result.response())
        .cookie(createRefreshTokenCookie(result.refreshToken()))
        .build();
  }

  @POST
  @Path("/refresh")
  @PermitAll
  @Operation(
      summary = "Refresh access token",
      description = "Get a new access token using the refresh token cookie")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Token refreshed successfully",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Invalid or expired refresh token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response refresh(
      @CookieParam(REFRESH_TOKEN_COOKIE) @Nullable String refreshToken,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    AuthResponse authResponse = authService.refreshToken(refreshToken);
    return Response.ok(authResponse).build();
  }

  @POST
  @Path("/logout")
  @PermitAll
  @Operation(summary = "Logout", description = "Logout and invalidate the refresh token")
  @APIResponses({@APIResponse(responseCode = "204", description = "Logged out successfully")})
  public Response logout(@CookieParam(REFRESH_TOKEN_COOKIE) @Nullable String refreshToken) {
    authService.logout(refreshToken);
    return Response.noContent().cookie(deleteRefreshTokenCookie()).build();
  }

  @POST
  @Path("/logout-all")
  @RolesAllowed("user")
  @Operation(
      summary = "Logout all sessions",
      description = "Logout from all devices by invalidating all refresh tokens")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "All sessions invalidated"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response logoutAll() {
    authService.logoutAll();
    return Response.noContent().cookie(deleteRefreshTokenCookie()).build();
  }

  private NewCookie createRefreshTokenCookie(String refreshToken) {
    return new NewCookie.Builder(REFRESH_TOKEN_COOKIE)
        .value(refreshToken)
        .path("/api")
        .maxAge(refreshTokenExpiryDays * 24 * 60 * 60)
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite(NewCookie.SameSite.valueOf(cookieSameSite.toUpperCase()))
        .build();
  }

  private NewCookie deleteRefreshTokenCookie() {
    return new NewCookie.Builder(REFRESH_TOKEN_COOKIE)
        .value("")
        .path("/api")
        .maxAge(0)
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite(NewCookie.SameSite.valueOf(cookieSameSite.toUpperCase()))
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
