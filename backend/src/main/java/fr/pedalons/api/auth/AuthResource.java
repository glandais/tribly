package fr.pedalons.api.auth;

import fr.pedalons.dto.auth.request.ForgotPasswordRequest;
import fr.pedalons.dto.auth.request.LoginRequest;
import fr.pedalons.dto.auth.request.OtpRequest;
import fr.pedalons.dto.auth.request.RegisterRequest;
import fr.pedalons.dto.auth.request.ResetPasswordRequest;
import fr.pedalons.dto.auth.request.VerifyOtpRequest;
import fr.pedalons.dto.auth.request.VerifyTokenRequest;
import fr.pedalons.dto.auth.response.AuthResponse;
import fr.pedalons.dto.auth.response.AuthResult;
import fr.pedalons.dto.auth.response.MessageResponse;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.social.request.EmailChangeRequest;
import fr.pedalons.service.auth.AuthService;
import fr.pedalons.service.auth.RefreshTokenCookieFactory;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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

  private static final String REFRESH_TOKEN_COOKIE = RefreshTokenCookieFactory.REFRESH_TOKEN_COOKIE;

  @Inject AuthService authService;
  @Inject RefreshTokenCookieFactory refreshTokenCookies;

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
        .cookie(refreshTokenCookies.issue(result.refreshToken()))
        .build();
  }

  @POST
  @Path("/otp")
  @PermitAll
  @Operation(
      summary = "Request OTP",
      description = "Send a 6-digit OTP code to the user's email for passwordless login")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "OTP sent (if email exists)",
        content = @Content(schema = @Schema(implementation = MessageResponse.class)))
  })
  public Response requestOtp(@Valid OtpRequest request) {
    authService.requestOtp(request);
    return Response.ok(new MessageResponse("If the email exists, a code has been sent")).build();
  }

  @POST
  @Path("/otp/verify")
  @PermitAll
  @Operation(summary = "Verify OTP", description = "Verify OTP code and authenticate")
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
  public Response verifyOtp(
      @Valid VerifyOtpRequest request,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    String userAgent = headers.getHeaderString(HttpHeaders.USER_AGENT);
    String ipAddress = getClientIp(forwardedFor, realIp);

    AuthResult result =
        authService.verifyOtp(request.email(), request.code(), userAgent, ipAddress);
    return Response.ok(result.response())
        .cookie(refreshTokenCookies.issue(result.refreshToken()))
        .build();
  }

  @POST
  @Path("/login")
  @PermitAll
  @Operation(summary = "Login with password", description = "Authenticate using email and password")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Login successful",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid credentials or password not set",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response loginWithPassword(
      @Valid LoginRequest request,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    String userAgent = headers.getHeaderString(HttpHeaders.USER_AGENT);
    String ipAddress = getClientIp(forwardedFor, realIp);

    AuthResult result =
        authService.loginWithPassword(request.email(), request.password(), userAgent, ipAddress);
    return Response.ok(result.response())
        .cookie(refreshTokenCookies.issue(result.refreshToken()))
        .build();
  }

  @POST
  @Path("/forgot-password")
  @PermitAll
  @Operation(
      summary = "Request password reset",
      description = "Send a 6-digit code to the user's email to reset their password")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Reset code sent (if email exists)",
        content = @Content(schema = @Schema(implementation = MessageResponse.class)))
  })
  public Response forgotPassword(@Valid ForgotPasswordRequest request) {
    authService.requestPasswordReset(request.email());
    return Response.ok(new MessageResponse("If the email exists, a reset code has been sent"))
        .build();
  }

  @POST
  @Path("/reset-password")
  @PermitAll
  @Operation(
      summary = "Reset password",
      description = "Verify the reset token and set a new password")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Password reset, logged in",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid or expired token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response resetPassword(
      @Valid ResetPasswordRequest request,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    String userAgent = headers.getHeaderString(HttpHeaders.USER_AGENT);
    String ipAddress = getClientIp(forwardedFor, realIp);

    AuthResult result =
        authService.resetPassword(request.token(), request.newPassword(), userAgent, ipAddress);
    return Response.ok(result.response())
        .cookie(refreshTokenCookies.issue(result.refreshToken()))
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
      @CookieParam(REFRESH_TOKEN_COOKIE) @Nullable String refreshTokenCookie,
      @HeaderParam("X-Refresh-Token") @Nullable String refreshTokenHeader,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    // Cookie (web) or header (mobile) — cookie takes priority
    boolean fromCookie = refreshTokenCookie != null && !refreshTokenCookie.isBlank();
    String refreshToken = fromCookie ? refreshTokenCookie : refreshTokenHeader;
    if (refreshToken == null || refreshToken.isBlank()) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    AuthResponse authResponse = authService.refreshToken(refreshToken);
    Response.ResponseBuilder response = Response.ok(authResponse);
    if (fromCookie) {
      // Re-issue on path=/ and drop any leftover path=/api cookie. The token itself is unchanged
      // (refresh does not rotate it); this is what migrates a pre-existing session to the new path
      // without forcing a reconnection.
      response.cookie(refreshTokenCookies.issue(refreshToken));
    }
    return response.build();
  }

  @POST
  @Path("/email/change-request")
  @RolesAllowed("user")
  @Operation(
      summary = "Request email change",
      description =
          "Set/change the account's real email (e.g. recover a migrated Strava account). Sends a"
              + " verification link to the new address.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Verification email sent to the new address",
        content = @Content(schema = @Schema(implementation = MessageResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Email already used by another account",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response requestEmailChange(@Valid EmailChangeRequest request) {
    authService.requestEmailChange(request.email());
    return Response.ok(new MessageResponse("Verification email sent")).build();
  }

  @POST
  @Path("/logout")
  @PermitAll
  @Operation(summary = "Logout", description = "Logout and invalidate the refresh token")
  @APIResponses({@APIResponse(responseCode = "204", description = "Logged out successfully")})
  public Response logout(
      @CookieParam(REFRESH_TOKEN_COOKIE) @Nullable String refreshTokenCookie,
      @HeaderParam("X-Refresh-Token") @Nullable String refreshTokenHeader) {
    String refreshToken =
        (refreshTokenCookie != null && !refreshTokenCookie.isBlank())
            ? refreshTokenCookie
            : refreshTokenHeader;
    authService.logout(refreshToken);
    return Response.noContent().cookie(refreshTokenCookies.revoke()).build();
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
    return Response.noContent().cookie(refreshTokenCookies.revoke()).build();
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
