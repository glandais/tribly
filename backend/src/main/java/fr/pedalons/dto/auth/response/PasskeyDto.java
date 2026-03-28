package fr.pedalons.dto.auth.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.auth.Passkey;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Passkey information")
@Builder
@ValidateSchema
public record PasskeyDto(
    @Schema(description = "Passkey ID") String id,
    @Schema(description = "Credential ID (base64url)") String credentialId,
    @Nullable @Schema(description = "Device name") String deviceName,
    @Nullable @Schema(description = "Transport methods") List<String> transports,
    @Schema(description = "Created timestamp") Instant createdAt,
    @Nullable @Schema(description = "Last used timestamp") Instant lastUsedAt) {

  public static PasskeyDto from(Passkey passkey) {
    return PasskeyDto.builder()
        .id(TsidUtils.toString(passkey.getId()))
        .credentialId(
            Base64.getUrlEncoder().withoutPadding().encodeToString(passkey.getCredentialId()))
        .deviceName(passkey.getDeviceName())
        .transports(passkey.getTransports())
        .createdAt(passkey.getCreatedAt())
        .lastUsedAt(passkey.getLastUsedAt())
        .build();
  }
}
