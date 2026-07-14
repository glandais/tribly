package fr.pedalons.infrastructure.gps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Hammerhead GPS device integration client.
 * Implements OAuth 2.0 and route upload via Hammerhead API.
 */
@ApplicationScoped
public class HammerheadClient implements GpsServiceClient {

  private static final Logger LOG = Logger.getLogger(HammerheadClient.class);

  private static final String AUTH_URL = "https://api.hammerhead.io/v1/auth/oauth/authorize";
  private static final String TOKEN_URL = "https://api.hammerhead.io/v1/auth/oauth/token";
  private static final String ROUTE_UPLOAD_URL = "https://api.hammerhead.io/v1/api/routes/file";
  private static final String SCOPE = "route:write";

  @Inject DomainGpsCredentialService credentialService;

  @Inject HttpClient httpClient;

  @Inject ObjectMapper objectMapper;

  private DomainGpsCredential getCredential() {
    return credentialService
        .getCredentials(GpsServiceType.HAMMERHEAD)
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
    return GpsServiceType.HAMMERHEAD;
  }

  @Override
  public String getAuthorizationUrl(String state, String redirectUri) {
    return AUTH_URL
        + "?response_type=code"
        + "&client_id="
        + urlEncode(getClientId())
        + "&redirect_uri="
        + urlEncode(redirectUri)
        + "&scope="
        + urlEncode(SCOPE)
        + "&state="
        + urlEncode(state);
  }

  @Override
  public TokenResponse exchangeCode(String code, String redirectUri) {
    String body =
        "grant_type=authorization_code"
            + "&code="
            + urlEncode(code)
            + "&redirect_uri="
            + urlEncode(redirectUri)
            + "&client_id="
            + urlEncode(getClientId())
            + "&client_secret="
            + urlEncode(getClientSecret());

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
        LOG.errorf(
            "Hammerhead token request failed: %d %s", response.statusCode(), response.body());
        throw new InternalException(
            ErrorCode.GPS_TOKEN_EXCHANGE_FAILED,
            new IOException("Hammerhead token request failed: " + response.statusCode()));
      }

      return parseTokenResponse(response.body());
    } catch (IOException | InterruptedException e) {
      throw new InternalException(ErrorCode.GPS_TOKEN_EXCHANGE_FAILED, e);
    }
  }

  private TokenResponse parseTokenResponse(String json) {
    try {
      JsonNode root = objectMapper.readTree(json);
      String accessToken = jsonString(root, "access_token");
      String refreshToken = jsonString(root, "refresh_token");
      Long expiresIn = jsonLong(root, "expires_in");
      String userId = jsonString(root, "user_id");

      return new TokenResponse(accessToken, refreshToken, expiresIn, userId);
    } catch (JsonProcessingException e) {
      throw new InternalException(ErrorCode.GPS_TOKEN_EXCHANGE_FAILED, e);
    }
  }

  @Override
  public RouteUploadResult uploadRoute(String accessToken, byte[] gpxContent, String routeName) {
    try {
      String boundary = "----" + UUID.randomUUID().toString().replace("-", "");

      byte[] multipartBody = buildMultipartBody(boundary, gpxContent, routeName);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(ROUTE_UPLOAD_URL))
              .header("Authorization", "Bearer " + accessToken)
              .header("Content-Type", "multipart/form-data; boundary=" + boundary)
              .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        String routeId = jsonString(objectMapper.readTree(response.body()), "id");
        LOG.infof("Successfully uploaded route to Hammerhead: %s", routeId);
        return RouteUploadResult.success(routeId);
      } else {
        LOG.errorf("Hammerhead route upload failed: %d %s", response.statusCode(), response.body());
        return RouteUploadResult.failure("Upload failed: " + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
      LOG.error("Failed to upload route to Hammerhead", e);
      return RouteUploadResult.failure("Upload failed: " + e.getMessage());
    }
  }

  private byte[] buildMultipartBody(String boundary, byte[] gpxContent, String routeName) {
    StringBuilder sb = new StringBuilder();

    // File part
    sb.append("--").append(boundary).append("\r\n");
    sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
        .append(routeName.replaceAll("[^a-zA-Z0-9_-]", "_"))
        .append(".gpx\"\r\n");
    sb.append("Content-Type: application/gpx+xml\r\n\r\n");

    byte[] header = sb.toString().getBytes(StandardCharsets.UTF_8);
    byte[] footer = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

    byte[] result = new byte[header.length + gpxContent.length + footer.length];
    System.arraycopy(header, 0, result, 0, header.length);
    System.arraycopy(gpxContent, 0, result, header.length, gpxContent.length);
    System.arraycopy(footer, 0, result, header.length + gpxContent.length, footer.length);

    return result;
  }

  private String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private static String jsonString(JsonNode root, String key) {
    JsonNode node = root.path(key);
    return node.isMissingNode() || node.isNull() ? null : node.asText();
  }

  private static Long jsonLong(JsonNode root, String key) {
    JsonNode node = root.path(key);
    if (node.isMissingNode() || node.isNull()) {
      return null;
    }
    if (node.canConvertToLong()) {
      return node.asLong();
    }
    // Some providers return numeric fields as JSON strings (e.g. "3600").
    try {
      return Long.parseLong(node.asText().trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
