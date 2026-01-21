package com.tribly.api.karoo;

import com.tribly.common.TsidUtils;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.karoo.request.KarooTokenRequest;
import com.tribly.dto.karoo.response.KarooDeviceCodeResponse;
import com.tribly.dto.karoo.response.KarooTokenResponse;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.service.karoo.KarooAuthService;
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
 * OAuth 2.0 Device Code endpoints for Karoo authentication (RFC 8628).
 *
 * <p>Flow:
 *
 * <ol>
 *   <li>POST /device → device_code + user_code + verification_uri
 *   <li>Device displays QR code with verification_uri_complete
 *   <li>User scans QR, authenticates on /karoo/verify
 *   <li>POST /token (polling) until authorized → access_token + refresh_token
 * </ol>
 */
@Path("/api/karoo/oauth")
@Tag(name = "Karoo OAuth", description = "OAuth 2.0 Device Code endpoints for Karoo devices")
public class KarooOAuthResource {

  private static final String KAROO_CLIENT_ID = "karoo-device";

  @Inject KarooAuthService karooAuthService;
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
        content = @Content(schema = @Schema(implementation = KarooDeviceCodeResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response device(
      @RequestBody(description = "Device auth request (optional client_id)")
          DeviceRequest request) {

    // Validate client_id if provided
    if (request != null
        && request.clientId() != null
        && !KAROO_CLIENT_ID.equals(request.clientId())) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(ErrorResponse.badRequest())
          .build();
    }

    KarooDeviceCodeResponse response = karooAuthService.initiateDeviceCodeFlow();
    return Response.ok(response).build();
  }

  @POST
  @Path("/complete")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      operationId = "karooComplete",
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
    karooAuthService.completeDeviceCodeFlow(request.userCode(), userId, domainId);
    return Response.ok().build();
  }

  @POST
  @Path("/token")
  @PermitAll
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      operationId = "karooToken",
      summary = "Exchange code for tokens",
      description =
          "Exchange device code or refresh token for access tokens. "
              + "Returns 'authorization_pending' error while waiting for user.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Token response",
        content = @Content(schema = @Schema(implementation = KarooTokenResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "authorization_pending, expired_token, or invalid_grant",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response token(@Valid KarooTokenRequest request) {
    KarooTokenResponse response;

    switch (request.grantType()) {
      case KarooTokenRequest.DEVICE_CODE_GRANT -> {
        if (request.deviceCode() == null || request.deviceCode().isBlank()) {
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(ErrorResponse.badRequest())
              .build();
        }
        response = karooAuthService.exchangeDeviceCode(request.deviceCode());
      }
      case KarooTokenRequest.REFRESH_TOKEN_GRANT -> {
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(ErrorResponse.badRequest())
              .build();
        }
        response = karooAuthService.refreshToken(request.refreshToken());
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

    var codeData = karooAuthService.getDeviceCodeByUserCode(userCode);
    if (codeData == null) {
      return Response.status(Response.Status.NOT_FOUND).entity(ErrorResponse.notFound()).build();
    }

    return Response.ok(new VerifyResponse(userCode.toUpperCase(), codeData.authorized())).build();
  }

  @Schema(description = "Device auth request")
  @ValidateSchema
  public record DeviceRequest(
      @Schema(description = "Client ID (optional, defaults to 'karoo-device')") String clientId) {}

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
