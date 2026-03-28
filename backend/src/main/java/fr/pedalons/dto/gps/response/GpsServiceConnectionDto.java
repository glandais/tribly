package fr.pedalons.dto.gps.response;

import fr.pedalons.domain.gps.GpsServiceConnection;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.GpsServiceType;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "GPS service connection information")
@ValidateSchema
public record GpsServiceConnectionDto(
    @Schema(description = "Service type identifier", required = true) GpsServiceType serviceType,
    @Schema(description = "Display name of the service", required = true) String displayName,
    @Schema(description = "When the service was connected", required = true) Instant connectedAt) {

  public static GpsServiceConnectionDto from(GpsServiceConnection connection) {
    return new GpsServiceConnectionDto(
        connection.getServiceType(),
        connection.getServiceType().getDisplayName(),
        connection.getConnectedAt());
  }
}
