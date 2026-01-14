package com.tribly.api.auth;

import com.tribly.common.TsidUtils;
import com.tribly.dto.auth.request.PasskeyAuthenticationRequest;
import com.tribly.dto.auth.response.AuthResponse;
import com.tribly.dto.auth.response.AuthResult;
import com.tribly.dto.auth.response.PasskeyDto;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.service.auth.AuthService;
import com.tribly.service.auth.PasskeyService;
import com.tribly.service.security.TriblyQueryContext;
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
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/auth/passkeys")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Passkeys", description = "WebAuthn passkey management")
public class PasskeyResource {

  private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

  @Inject PasskeyService passkeyService;
  @Inject AuthService authService;
  @Inject TriblyQueryContext triblyContext;

  @ConfigProperty(name = "tribly.auth.refresh-token.expiry-days", defaultValue = "30")
  int refreshTokenExpiryDays;

  @ConfigProperty(name = "tribly.auth.cookie.secure", defaultValue = "true")
  boolean cookieSecure;

  @ConfigProperty(name = "tribly.auth.cookie.same-site", defaultValue = "strict")
  String cookieSameSite;

  @GET
  @Path("/registration-options")
  @RolesAllowed("user")
  @Operation(
      summary = "Get registration options",
      description = "Get WebAuthn options for registering a new passkey")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Registration options"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getRegistrationOptions() {
    Map<String, Object> options =
        passkeyService.generateRegistrationOptions(triblyContext.getUser());
    return Response.ok(options).build();
  }

  @POST
  @Path("/register")
  @RolesAllowed("user")
  @Operation(
      summary = "Register passkey",
      description = "Verify and register a new passkey for the current user")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Passkey registered",
        content = @Content(schema = @Schema(implementation = PasskeyDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid registration response",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response registerPasskey(
      Map<String, Object> response, @QueryParam("deviceName") @Nullable String deviceName) {
    PasskeyDto passkey =
        passkeyService.verifyRegistration(triblyContext.getUser(), response, deviceName);
    return Response.ok(passkey).build();
  }

  @POST
  @Path("/authentication-options")
  @PermitAll
  @Operation(
      summary = "Get authentication options",
      description = "Get WebAuthn options for authenticating with a passkey")
  @APIResponses({@APIResponse(responseCode = "200", description = "Authentication options")})
  public Response getAuthenticationOptions(@Valid @Nullable PasskeyAuthenticationRequest request) {
    String email = request != null ? request.email() : null;
    Map<String, Object> options = passkeyService.generateAuthenticationOptions(email);
    return Response.ok(options).build();
  }

  @POST
  @Path("/authenticate")
  @PermitAll
  @Operation(summary = "Authenticate with passkey", description = "Authenticate using a passkey")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Authentication successful",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Authentication failed",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response authenticate(
      Map<String, Object> response,
      @Context HttpHeaders headers,
      @HeaderParam("X-Forwarded-For") @Nullable String forwardedFor,
      @HeaderParam("X-Real-IP") @Nullable String realIp) {
    String userAgent = headers.getHeaderString(HttpHeaders.USER_AGENT);
    String ipAddress = getClientIp(forwardedFor, realIp);

    AuthResult result = authService.authenticateWithPasskey(response, userAgent, ipAddress);
    return Response.ok(result.response())
        .cookie(createRefreshTokenCookie(result.refreshToken()))
        .build();
  }

  @GET
  @RolesAllowed("user")
  @Operation(
      summary = "List passkeys",
      description = "List all passkeys registered for the current user")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "List of passkeys",
        content = @Content(schema = @Schema(implementation = PasskeyDto[].class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listPasskeys() {
    List<PasskeyDto> passkeys = passkeyService.listPasskeys(triblyContext.getUserId());
    return Response.ok(passkeys).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed("user")
  @Operation(summary = "Delete passkey", description = "Delete a passkey")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Passkey deleted"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Passkey not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deletePasskey(@PathParam("id") String id) {
    passkeyService.deletePasskey(TsidUtils.toLong(id), triblyContext.getUserId());
    return Response.noContent().build();
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
