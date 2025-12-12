package com.tribly.service.auth;

import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StravaOAuthService {

    private static final Logger LOG = Logger.getLogger(StravaOAuthService.class);

    @ConfigProperty(name = "tribly.strava.client-id")
    String clientId;

    @ConfigProperty(name = "tribly.strava.client-secret")
    String clientSecret;

    @ConfigProperty(name = "tribly.strava.redirect-uri")
    String redirectUri;

    @ConfigProperty(name = "tribly.strava.authorization-url")
    String authorizationUrl;

    @ConfigProperty(name = "tribly.strava.token-url")
    String tokenUrl;

    @Inject
    UserRepository userRepository;

    public String generateAuthorizationUrl(String redirectUri) {
        String state = UUID.randomUUID().toString();
        String finalRedirectUri = redirectUri != null ? redirectUri : this.redirectUri;

        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=read,read_all&state=%s",
                authorizationUrl,
                clientId,
                URLEncoder.encode(finalRedirectUri, StandardCharsets.UTF_8),
                state);
    }

    @Transactional
    public AuthResult handleCallback(String code, String state) {
        StravaTokenResponse tokenResponse = exchangeCodeForToken(code);
        if (tokenResponse == null) {
            LOG.error("Failed to exchange code for token");
            return AuthResult.failure("Failed to authenticate with Strava");
        }

        StravaAthlete athlete = tokenResponse.athlete();
        if (athlete == null) {
            LOG.error("No athlete data in token response");
            return AuthResult.failure("Failed to get athlete data from Strava");
        }

        User user = findOrCreateUser(athlete);
        user.recordLogin();
        userRepository.persist(user);

        return AuthResult.success(user, tokenResponse.accessToken(), tokenResponse.refreshToken());
    }

    private StravaTokenResponse exchangeCodeForToken(String code) {
        try (Client client = ClientBuilder.newClient()) {
            Form form = new Form()
                    .param("client_id", clientId)
                    .param("client_secret", clientSecret)
                    .param("code", code)
                    .param("grant_type", "authorization_code");

            Response response = client.target(tokenUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.form(form));

            if (response.getStatus() != 200) {
                LOG.errorv("Strava token exchange failed with status {0}", response.getStatus());
                return null;
            }

            Map<String, Object> json = response.readEntity(Map.class);
            return parseTokenResponse(json);
        } catch (Exception e) {
            LOG.error("Error exchanging code for token", e);
            return null;
        }
    }

    private StravaTokenResponse parseTokenResponse(Map<String, Object> json) {
        String accessToken = (String) json.get("access_token");
        String refreshToken = (String) json.get("refresh_token");
        Number expiresAt = (Number) json.get("expires_at");

        Map<String, Object> athleteJson = (Map<String, Object>) json.get("athlete");
        if (athleteJson == null) {
            return null;
        }

        StravaAthlete athlete = new StravaAthlete(
                String.valueOf(athleteJson.get("id")),
                (String) athleteJson.get("firstname"),
                (String) athleteJson.get("lastname"),
                (String) athleteJson.get("profile"),
                (String) athleteJson.get("email")
        );

        return new StravaTokenResponse(accessToken, refreshToken, expiresAt.longValue(), athlete);
    }

    private User findOrCreateUser(StravaAthlete athlete) {
        Optional<User> existingUser = userRepository.findByStravaId(athlete.id());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setDisplayName(athlete.fullName());
            user.setAvatarUrl(athlete.profile());
            if (athlete.email() != null && !athlete.email().isBlank()) {
                user.setEmail(athlete.email());
            }
            return user;
        }

        String email = athlete.email();
        if (email == null || email.isBlank()) {
            email = athlete.id() + "@strava.user";
        }

        return User.fromStrava(
                athlete.id(),
                email,
                athlete.fullName(),
                athlete.profile()
        );
    }

    public record StravaTokenResponse(
            String accessToken,
            String refreshToken,
            long expiresAt,
            StravaAthlete athlete
    ) {}

    public record StravaAthlete(
            String id,
            String firstName,
            String lastName,
            String profile,
            String email
    ) {
        public String fullName() {
            return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        }
    }

    public record AuthResult(
            boolean success,
            User user,
            String accessToken,
            String refreshToken,
            String errorMessage
    ) {
        public static AuthResult success(User user, String accessToken, String refreshToken) {
            return new AuthResult(true, user, accessToken, refreshToken, null);
        }

        public static AuthResult failure(String errorMessage) {
            return new AuthResult(false, null, null, null, errorMessage);
        }
    }
}
