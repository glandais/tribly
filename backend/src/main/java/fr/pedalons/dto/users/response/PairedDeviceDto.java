package fr.pedalons.dto.users.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.auth.AuthSession;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.PairedDeviceType;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "A device (Karoo, Garmin watch) paired with the account by code")
@ValidateSchema
public record PairedDeviceDto(
    @Schema(description = "Pairing ID, to unpair the device", required = true) String id,
    @Schema(description = "Kind of device", required = true) PairedDeviceType type,
    @Schema(description = "When the device was paired", required = true) Instant pairedAt,
    @Nullable @Schema(description = "When the device last renewed its access") Instant lastUsedAt) {

  public static PairedDeviceDto from(AuthSession session) {
    return new PairedDeviceDto(
        TsidUtils.toString(session.getId()),
        PairedDeviceType.fromClientId(session.getDeviceClient()),
        session.getCreatedAt(),
        session.getLastUsedAt());
  }
}
