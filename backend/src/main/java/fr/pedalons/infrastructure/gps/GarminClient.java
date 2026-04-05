package fr.pedalons.infrastructure.gps;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.InternalException;
import fr.pedalons.domain.gps.DomainGpsCredential;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.service.gps.DomainGpsCredentialService;
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
 * Garmin Connect GPS device integration client.
 * Implements OAuth 2.0 with PKCE and course upload via Garmin Training API.
 */
@ApplicationScoped
public class GarminClient implements GpsServiceClient {

  private static final Logger LOG = Logger.getLogger(GarminClient.class);

  private static final String AUTH_URL = "https://connect.garmin.com/oauth2Confirm";
  private static final String TOKEN_URL = "https://diauth.garmin.com/di-oauth2-service/oauth/token";
  private static final String COURSE_UPLOAD_URL =
      "https://apis.garmin.com/training-api/courses/v1/course";
  private static final String SCOPE = "COURSE_WRITE";

  @Inject DomainGpsCredentialService credentialService;

  @Inject HttpClient httpClient;

  @Inject GarminCourseConverter courseConverter;

  private DomainGpsCredential getCredential() {
    return credentialService
        .getCredentials(GpsServiceType.GARMIN)
        .orElseThrow(() -> new BusinessException(ErrorCode.GPS_SERVICE_NOT_CONFIGURED));
  }

  private String getClientId() {
    return getCredential().getClientId();
  }

  private String getClientSecret() {
    DomainGpsCredential credential = getCredential();
    String secret = credentialService.getDecryptedClientSecret(credential);
    if (secret == null) {
      throw new BusinessException(ErrorCode.GPS_SERVICE_NOT_CONFIGURED);
    }
    return secret;
  }

  @Override
  public GpsServiceType getServiceType() {
    return GpsServiceType.GARMIN;
  }

  @Override
  public boolean supportsPkce() {
    return true;
  }

  @Override
  public String getAuthorizationUrl(String state, String redirectUri) {
    // This should not be called for PKCE clients
    throw new UnsupportedOperationException("Garmin requires PKCE flow");
  }

  @Override
  public String getAuthorizationUrl(String state, String redirectUri, String codeChallenge) {
    return AUTH_URL
        + "?response_type=code"
        + "&client_id="
        + urlEncode(getClientId())
        + "&redirect_uri="
        + urlEncode(redirectUri)
        + "&scope="
        + urlEncode(SCOPE)
        + "&state="
        + urlEncode(state)
        + "&code_challenge="
        + urlEncode(codeChallenge)
        + "&code_challenge_method=S256";
  }

  @Override
  public TokenResponse exchangeCode(String code, String redirectUri) {
    // This should not be called for PKCE clients
    throw new UnsupportedOperationException("Garmin requires PKCE flow");
  }

  @Override
  public TokenResponse exchangeCode(String code, String redirectUri, String codeVerifier) {
    String body =
        "grant_type=authorization_code"
            + "&code="
            + urlEncode(code)
            + "&redirect_uri="
            + urlEncode(redirectUri)
            + "&client_id="
            + urlEncode(getClientId())
            + "&client_secret="
            + urlEncode(getClientSecret())
            + "&code_verifier="
            + urlEncode(codeVerifier);

    return requestToken(body);
  }

  @Override
  public TokenResponse refreshToken(String refreshToken) {
    String body =
        "grant_type=refresh_token"
            + "&refresh_token="
            + urlEncode(refreshToken)
            + "&client_id="
            + urlEncode(getClientId())
            + "&client_secret="
            + urlEncode(getClientSecret());

    return requestToken(body);
  }

  private TokenResponse requestToken(String body) {
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(TOKEN_URL))
              .header("Content-Type", "application/x-www-form-urlencoded")
              .POST(HttpRequest.BodyPublishers.ofString(body))
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        LOG.errorf("Garmin token request failed: %d %s", response.statusCode(), response.body());
        throw new InternalException(
            ErrorCode.GPS_TOKEN_EXCHANGE_FAILED,
            new IOException("Garmin token request failed: " + response.statusCode()));
      }

      return parseTokenResponse(response.body());
    } catch (IOException | InterruptedException e) {
      throw new InternalException(ErrorCode.GPS_TOKEN_EXCHANGE_FAILED, e);
    }
  }

  private TokenResponse parseTokenResponse(String json) {
    String accessToken = extractJsonString(json, "access_token");
    String refreshToken = extractJsonString(json, "refresh_token");
    Long expiresIn = extractJsonLong(json, "expires_in");
    // Garmin may return user info differently
    String userId = extractJsonString(json, "user_id");

    return new TokenResponse(accessToken, refreshToken, expiresIn, userId);
  }

  @Override
  public RouteUploadResult uploadRoute(String accessToken, byte[] gpxContent, String routeName) {
    try {
      // Convert GPX to Garmin course JSON format
      String courseJson = courseConverter.convertToGarminCourse(gpxContent, routeName);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(COURSE_UPLOAD_URL))
              .header("Authorization", "Bearer " + accessToken)
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(courseJson, StandardCharsets.UTF_8))
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        String courseId = extractJsonString(response.body(), "courseId");
        if (courseId == null) {
          courseId = extractJsonString(response.body(), "id");
        }
        LOG.infof("Successfully uploaded course to Garmin: %s", courseId);
        return RouteUploadResult.success(courseId);
      } else {
        LOG.errorf("Garmin course upload failed: %d %s", response.statusCode(), response.body());
        return RouteUploadResult.failure("Upload failed: " + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
      LOG.error("Failed to upload course to Garmin", e);
      return RouteUploadResult.failure("Upload failed: " + e.getMessage());
    }
  }

  private String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private String extractJsonString(String json, String key) {
    String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
    java.util.regex.Matcher m = p.matcher(json);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  private Long extractJsonLong(String json, String key) {
    String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
    java.util.regex.Matcher m = p.matcher(json);
    if (m.find()) {
      return Long.parseLong(m.group(1));
    }
    return null;
  }
}
