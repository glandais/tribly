package com.tribly.api.device;

import com.tribly.common.TsidUtils;
import com.tribly.dto.device.request.DeviceTokenRequest;
import com.tribly.dto.device.response.DeviceCodeResponse;
import com.tribly.dto.device.response.DeviceTokenResponse;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.service.device.DeviceAuthService;
import com.tribly.service.security.DomainResolver;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * OAuth 2.0 Device Code endpoints for GPS device authentication (RFC 8628).
 *
 * <p>Supports multiple device types (Karoo, Garmin, etc.) through client_id differentiation.
 *
 * <p>Flow:
 *
 * <ol>
 *   <li>POST /device → device_code + user_code + verification_uri
 *   <li>Device displays QR code with verification_uri_complete
 *   <li>User scans QR, authenticates on /device/verify
 *   <li>POST /token (polling) until authorized → access_token + refresh_token
 * </ol>
 */
@Path("/api/device/oauth")
@Tag(name = "Device OAuth", description = "OAuth 2.0 Device Code endpoints for GPS devices")
public class DeviceOAuthResource {

  private static final String DEFAULT_CLIENT_ID = "device";

  @Inject DeviceAuthService deviceAuthService;
  @Inject DomainResolver domainResolver;

  @POST
  @Path("/device")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Request device code",
      description = "Start device code flow - returns user code and verification URL")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Device code response",
        content = @Content(schema = @Schema(implementation = DeviceCodeResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response device(
      @RequestBody(description = "Device auth request (optional client_id)")
          DeviceRequest request) {

    String clientId = DEFAULT_CLIENT_ID;
    if (request != null && request.clientId() != null && !request.clientId().isBlank()) {
      clientId = request.clientId();
    }

    DeviceCodeResponse response = deviceAuthService.initiateDeviceCodeFlow(clientId);
    return Response.ok(response).build();
  }

  @POST
  @Path("/complete")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      operationId = "deviceComplete",
      summary = "Complete device authorization",
      description = "Called by frontend after user authenticates via OTP")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Authorization completed"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid or expired code",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response complete(@Valid CompleteRequest request) {
    Long domainId = domainResolver.getDomain().getId();
    Long userId = TsidUtils.toLong(request.userId());
    deviceAuthService.completeDeviceCodeFlow(request.userCode(), userId, domainId);
    return Response.ok().build();
  }

  @POST
  @Path("/token")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      operationId = "deviceToken",
      summary = "Exchange code for tokens",
      description =
          "Exchange device code or refresh token for access tokens. "
              + "Returns 'authorization_pending' error while waiting for user.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Token response",
        content = @Content(schema = @Schema(implementation = DeviceTokenResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "authorization_pending, expired_token, or invalid_grant",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response token(@Valid DeviceTokenRequest request) {
    DeviceTokenResponse response;

    // Determine client ID for refresh token grants
    String clientId = DEFAULT_CLIENT_ID;

    switch (request.grantType()) {
      case DeviceTokenRequest.DEVICE_CODE_GRANT -> {
        if (request.deviceCode() == null || request.deviceCode().isBlank()) {
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(ErrorResponse.badRequest())
              .build();
        }
        response = deviceAuthService.exchangeDeviceCode(request.deviceCode());
      }
      case DeviceTokenRequest.REFRESH_TOKEN_GRANT -> {
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(ErrorResponse.badRequest())
              .build();
        }
        response = deviceAuthService.refreshToken(request.refreshToken(), clientId);
      }
      default -> {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(ErrorResponse.badRequest())
            .build();
      }
    }

    return Response.ok(response).build();
  }

  @GET
  @Path("/verify")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Check user code validity",
      description = "Frontend uses this to verify user code before showing auth flow")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Code is valid",
        content = @Content(schema = @Schema(implementation = VerifyResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Code not found or expired",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response verify(@QueryParam("code") String userCode) {
    if (userCode == null || userCode.isBlank()) {
      return Response.status(Response.Status.NOT_FOUND).entity(ErrorResponse.notFound()).build();
    }

    var codeData = deviceAuthService.getDeviceCodeByUserCode(userCode);
    if (codeData == null) {
      return Response.status(Response.Status.NOT_FOUND).entity(ErrorResponse.notFound()).build();
    }

    return Response.ok(new VerifyResponse(userCode.toUpperCase(), codeData.authorized())).build();
  }

  @Schema(description = "Device auth request")
  @ValidateSchema
  public record DeviceRequest(
      @Schema(description = "Client ID (e.g., 'karoo', 'garmin')") String clientId) {}

  @Schema(description = "Complete device authorization request")
  @ValidateSchema
  public record CompleteRequest(
      @Schema(description = "User code from device display", required = true) String userCode,
      @Schema(description = "Authenticated user ID (TSID string)", required = true)
          String userId) {}

  @Schema(description = "User code verification response")
  @ValidateSchema
  public record VerifyResponse(
      @Schema(description = "User code", required = true) String userCode,
      @Schema(description = "Whether authorization is already completed") boolean authorized) {}
}
