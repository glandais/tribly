package com.tribly.dto.gps.response;

import com.tribly.domain.gps.GpsServiceConnection;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.GpsServiceType;
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
