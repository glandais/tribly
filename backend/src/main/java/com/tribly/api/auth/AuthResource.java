package com.tribly.api.auth;

import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.service.auth.JwtService;
import com.tribly.service.auth.StravaOAuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.Optional;

@Path("/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @Inject
    StravaOAuthService stravaOAuthService;

    @Inject
    JwtService jwtService;

    @Inject
    UserRepository userRepository;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/oauth/{provider}")
    public Response initiateOAuth(
            @PathParam("provider") String provider,
            @QueryParam("redirect_uri") String redirectUri) {

        if (!"strava".equalsIgnoreCase(provider)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto("UNSUPPORTED_PROVIDER", "Only Strava OAuth is currently supported"))
                    .build();
        }

        try {
            String authUrl = stravaOAuthService.generateAuthorizationUrl(redirectUri);
            return Response.status(Response.Status.FOUND)
                    .location(URI.create(authUrl))
                    .build();
        } catch (Exception e) {
            LOG.error("Failed to generate Strava authorization URL", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto("OAUTH_INIT_ERROR", "Failed to initialize OAuth: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/oauth/{provider}/callback")
    public Response handleOAuthCallback(
            @PathParam("provider") String provider,
            @QueryParam("code") String code,
            @QueryParam("state") String state,
            @QueryParam("error") String error) {

        if (error != null) {
            LOG.warnv("OAuth error: {0}", error);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorDto("OAUTH_ERROR", "OAuth authentication was denied: " + error))
                    .build();
        }

        if (code == null || code.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto("MISSING_CODE", "Authorization code is required"))
                    .build();
        }

        if (!"strava".equalsIgnoreCase(provider)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto("UNSUPPORTED_PROVIDER", "Only Strava OAuth is currently supported"))
                    .build();
        }

        StravaOAuthService.AuthResult result = stravaOAuthService.handleCallback(code, state);

        if (!result.success()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorDto("AUTH_FAILED", result.errorMessage()))
                    .build();
        }

        JwtService.TokenPair tokens = jwtService.generateTokenPair(result.user());

        return Response.ok(new AuthResponseDto(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresIn(),
                UserDto.from(result.user())
        )).build();
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(RefreshTokenRequest request) {
        if (request == null || request.refreshToken() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto("MISSING_TOKEN", "Refresh token is required"))
                    .build();
        }

        try {
            String subject = jwt.getSubject();
            if (subject == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorDto("INVALID_TOKEN", "Invalid refresh token"))
                        .build();
            }

            Long userId = Long.parseLong(subject);
            Optional<User> userOpt = userRepository.findActiveById(userId);

            if (userOpt.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorDto("USER_NOT_FOUND", "User not found"))
                        .build();
            }

            JwtService.TokenPair tokens = jwtService.generateTokenPair(userOpt.get());

            return Response.ok(new AuthResponseDto(
                    tokens.accessToken(),
                    tokens.refreshToken(),
                    tokens.expiresIn(),
                    UserDto.from(userOpt.get())
            )).build();

        } catch (Exception e) {
            LOG.error("Error refreshing token", e);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorDto("REFRESH_FAILED", "Failed to refresh token"))
                    .build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        return Response.noContent().build();
    }

    public record AuthResponseDto(
            String accessToken,
            String refreshToken,
            long expiresIn,
            UserDto user
    ) {}

    public record UserDto(
            Long id,
            String email,
            String displayName,
            String avatarUrl,
            String stravaId,
            String locale,
            String timezone
    ) {
        public static UserDto from(User user) {
            return new UserDto(
                    user.getId(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getAvatarUrl(),
                    user.getStravaId(),
                    user.getLocale(),
                    user.getTimezone()
            );
        }
    }

    public record RefreshTokenRequest(String refreshToken) {}

    public record ErrorDto(String code, String message) {}
}
