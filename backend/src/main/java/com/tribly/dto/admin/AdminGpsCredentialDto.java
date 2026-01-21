package com.tribly.dto.admin;

import com.tribly.common.TsidUtils;
import com.tribly.domain.gps.DomainGpsCredential;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.GpsServiceType;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Admin GPS credential view (without secret)")
@ValidateSchema
public record AdminGpsCredentialDto(
    @Schema(description = "Credential ID (TSID)", examples = "0h4a8xzk8jv80", required = true)
        String id,
    @Schema(description = "GPS service type", required = true) GpsServiceType serviceType,
    @Schema(description = "OAuth client ID", required = true) String clientId,
    @Schema(description = "Whether credential is active", required = true) boolean active,
    @Schema(description = "Credential creation timestamp", required = true) Instant createdAt) {

  public static AdminGpsCredentialDto from(DomainGpsCredential credential) {
    return new AdminGpsCredentialDto(
        TsidUtils.toString(credential.getId()),
        credential.getServiceType(),
        credential.getClientId(),
        credential.isActive(),
        credential.getCreatedAt());
  }
}
