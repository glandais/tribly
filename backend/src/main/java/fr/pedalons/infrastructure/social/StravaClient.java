package fr.pedalons.infrastructure.social;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.InternalException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.service.social.SocialCredentialService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.jboss.logging.Logger;

/**
 * Strava OAuth2 client (confidential client, no PKCE). Used purely for authentication: the token
 * exchange returns the {@code athlete} object inline, so we read the athlete id and discard the
 * tokens — nothing is persisted. Unlike the GPS clients, the Strava response nests {@code
 * athlete.id}, so it is parsed with Jackson rather than regex.
 *
 * <p>The host is configuration-driven ({@code pedalons.social.strava.base-url}): the same paths are
 * served by Strava itself and by a drop-in proxy such as strava-auth-proxy.
 */
@ApplicationScoped
public class StravaClient {

  private static final Logger LOG = Logger.getLogger(StravaClient.class);

  private static final String AUTHORIZE_PATH = "/oauth/authorize";
  private static final String TOKEN_PATH = "/oauth/token";
  private static final String SCOPE = "read";

  @Inject SocialCredentialService credentialService;

  @Inject HttpClient httpClient;

  @Inject ObjectMapper objectMapper;

  private String getClientId() {
    return credentialService
        .getStravaClientId()
        .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_SERVICE_NOT_CONFIGURED));
  }

  private String getClientSecret() {
    return credentialService
        .getStravaClientSecret()
        .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_SERVICE_NOT_CONFIGURED));
  }

  public String getAuthorizationUrl(String state, String redirectUri) {
    return credentialService.getStravaBaseUrl()
        + AUTHORIZE_PATH
        + "?client_id="
        + urlEncode(getClientId())
        + "&response_type=code"
        + "&redirect_uri="
        + urlEncode(redirectUri)
        + "&approval_prompt=auto"
        + "&scope="
        + urlEncode(SCOPE)
        + "&state="
        + urlEncode(state);
  }

  /**
   * Exchanges an authorization code for the athlete identity. Tokens in the response are ignored on
   * purpose — this integration is identity-only.
   */
  public StravaAthlete exchangeCode(String code, String redirectUri) {
    String body =
        "client_id="
            + urlEncode(getClientId())
            + "&client_secret="
            + urlEncode(getClientSecret())
            + "&code="
            + urlEncode(code)
            + "&grant_type=authorization_code"
            + "&redirect_uri="
            + urlEncode(redirectUri);

    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(credentialService.getStravaBaseUrl() + TOKEN_PATH))
              .header("Content-Type", "application/x-www-form-urlencoded")
              .header("Accept", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(body))
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        LOG.errorf("Strava token exchange failed: %d", response.statusCode());
        throw new InternalException(
            ErrorCode.GPS_TOKEN_EXCHANGE_FAILED,
            new IOException("Strava token exchange failed: " + response.statusCode()));
      }

      return parseAthlete(response.body());
    } catch (IOException e) {
      throw new InternalException(ErrorCode.GPS_TOKEN_EXCHANGE_FAILED, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InternalException(ErrorCode.GPS_TOKEN_EXCHANGE_FAILED, e);
    }
  }

  private StravaAthlete parseAthlete(String json) {
    try {
      JsonNode root = objectMapper.readTree(json);
      JsonNode athlete = root.path("athlete");
      if (athlete.isMissingNode() || athlete.path("id").isMissingNode()) {
        throw new InternalException(
            ErrorCode.GPS_TOKEN_EXCHANGE_FAILED,
            new IOException("Strava token response missing athlete id"));
      }
      String athleteId = athlete.path("id").asText();
      return new StravaAthlete(
          athleteId,
          textOrNull(athlete, "firstname"),
          textOrNull(athlete, "lastname"),
          textOrNull(athlete, "profile"));
    } catch (IOException e) {
      throw new InternalException(ErrorCode.GPS_TOKEN_EXCHANGE_FAILED, e);
    }
  }

  private static @org.jspecify.annotations.Nullable String textOrNull(JsonNode node, String field) {
    JsonNode value = node.path(field);
    return value.isMissingNode() || value.isNull() || value.asText().isBlank()
        ? null
        : value.asText();
  }

  private String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
