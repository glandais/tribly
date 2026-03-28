package fr.pedalons.dto.device.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Device OAuth token request")
@Builder
@ValidateSchema
public record DeviceTokenRequest(
    @Schema(
            description =
                "Grant type: 'urn:ietf:params:oauth:grant-type:device_code' or 'refresh_token'",
            required = true)
        @NotBlank
        String grantType,
    @Schema(description = "Device code (for device_code grant)") @Nullable String deviceCode,
    @Schema(description = "Refresh token (for refresh_token grant)")
        @Nullable String refreshToken) {

  public static final String DEVICE_CODE_GRANT = "urn:ietf:params:oauth:grant-type:device_code";
  public static final String REFRESH_TOKEN_GRANT = "refresh_token";
}
