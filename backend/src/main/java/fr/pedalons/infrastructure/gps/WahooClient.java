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
import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.io.read.GPXFileReader;
import io.github.glandais.gpx.io.write.FitFileWriter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Wahoo Cloud API GPS device integration client.
 *
 * <p>Implements OAuth 2.0 (confidential client, no PKCE) and route upload via the Wahoo Cloud API.
 * Unlike Hammerhead (which accepts raw GPX), Wahoo's {@code POST /v1/routes} expects a FIT-format
 * route file, so the GPX is converted to FIT in-process before upload — mirroring how {@link
 * GarminClient} converts GPX to Garmin's own course format.
 *
 * <p>Wahoo has no companion device application: the route is pushed to the user's Wahoo account and
 * syncs to their ELEMNT head unit from the cloud.
 */
@ApplicationScoped
public class WahooClient implements GpsServiceClient {

  private static final Logger LOG = Logger.getLogger(WahooClient.class);

  private static final String AUTH_URL = "https://api.wahooligan.com/oauth/authorize";
  private static final String TOKEN_URL = "https://api.wahooligan.com/oauth/token";
  private static final String ROUTE_UPLOAD_URL = "https://api.wahooligan.com/v1/routes";
  private static final String SCOPE = "user_read routes_write";
  private static final String FIT_CONTENT_TYPE = "application/vnd.ant.fit";

  @Inject DomainGpsCredentialService credentialService;

  @Inject HttpClient httpClient;

  @Inject ObjectMapper objectMapper;

  @Inject GPXFileReader gpxFileReader;

  @Inject FitFileWriter fitFileWriter;

  private DomainGpsCredential getCredential() {
    return credentialService
        .getCredentials(GpsServiceType.WAHOO)
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
    return GpsServiceType.WAHOO;
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
        LOG.errorf("Wahoo token request failed: %d %s", response.statusCode(), response.body());
        throw new InternalException(
            ErrorCode.GPS_TOKEN_EXCHANGE_FAILED,
            new IOException("Wahoo token request failed: " + response.statusCode()));
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
      // Wahoo does not return the user id in the token response; it stays null.
      String userId = jsonString(root, "user_id");

      return new TokenResponse(accessToken, refreshToken, expiresIn, userId);
    } catch (JsonProcessingException e) {
      throw new InternalException(ErrorCode.GPS_TOKEN_EXCHANGE_FAILED, e);
    }
  }

  @Override
  public RouteUploadResult uploadRoute(String accessToken, byte[] gpxContent, String routeName) {
    File fitFile = null;
    try {
      fitFile = convertGpxToFit(gpxContent);
      byte[] fitContent = Files.readAllBytes(fitFile.toPath());

      String boundary = "----" + UUID.randomUUID().toString().replace("-", "");
      byte[] multipartBody = buildMultipartBody(boundary, fitContent, routeName);

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
        LOG.infof("Successfully uploaded route to Wahoo: %s", routeId);
        return RouteUploadResult.success(routeId);
      } else {
        LOG.errorf("Wahoo route upload failed: %d %s", response.statusCode(), response.body());
        return RouteUploadResult.failure("Upload failed: " + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
      LOG.error("Failed to upload route to Wahoo", e);
      return RouteUploadResult.failure("Upload failed: " + e.getMessage());
    } finally {
      if (fitFile != null && fitFile.exists() && !fitFile.delete()) {
        LOG.warnf("Failed to delete temp FIT file %s", fitFile.getAbsolutePath());
      }
    }
  }

  /** Converts GPX bytes to a temporary FIT route file the caller is responsible for deleting. */
  private File convertGpxToFit(byte[] gpxContent) throws IOException {
    File fitFile = File.createTempFile("wahoo-route-", ".fit");
    try {
      GPX gpx = gpxFileReader.parseGPX(new ByteArrayInputStream(gpxContent));
      fitFileWriter.writeGPX(gpx, fitFile);
      return fitFile;
    } catch (Exception e) {
      fitFile.delete();
      throw new IOException("Failed to convert GPX to FIT", e);
    }
  }

  /**
   * Builds a {@code multipart/form-data} body with the route name and the FIT file, matching the
   * Wahoo Cloud API {@code POST /v1/routes} contract ({@code route[name]}, {@code route[file]}).
   */
  private byte[] buildMultipartBody(String boundary, byte[] fitContent, String routeName) {
    String namePart =
        "--"
            + boundary
            + "\r\n"
            + "Content-Disposition: form-data; name=\"route[name]\"\r\n\r\n"
            + routeName
            + "\r\n";

    String fileHeader =
        "--"
            + boundary
            + "\r\n"
            + "Content-Disposition: form-data; name=\"route[file]\"; filename=\""
            + routeName.replaceAll("[^a-zA-Z0-9_-]", "_")
            + ".fit\"\r\n"
            + "Content-Type: "
            + FIT_CONTENT_TYPE
            + "\r\n\r\n";

    byte[] header = (namePart + fileHeader).getBytes(StandardCharsets.UTF_8);
    byte[] footer = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

    byte[] result = new byte[header.length + fitContent.length + footer.length];
    System.arraycopy(header, 0, result, 0, header.length);
    System.arraycopy(fitContent, 0, result, header.length, fitContent.length);
    System.arraycopy(footer, 0, result, header.length + fitContent.length, footer.length);

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
