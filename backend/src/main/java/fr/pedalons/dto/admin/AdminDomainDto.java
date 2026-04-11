package fr.pedalons.dto.admin;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Admin domain view with statistics")
@ValidateSchema
public record AdminDomainDto(
    @Schema(description = "Domain ID (TSID)", examples = "0h4a8xzk8jv80", required = true)
        String id,
    @Schema(description = "Domain hostname", required = true) String domain,
    @Schema(description = "Domain display name", required = true) String name,
    @Schema(description = "Base URL for the domain", required = true) String baseUrl,
    @Schema(description = "Whether domain is single-team mode", required = true) boolean singleTeam,
    @Schema(
            description =
                "Android app SHA-256 certificate fingerprints for passkey origin verification"
                    + " (comma-separated, colon-hex format)")
        String androidFingerprints,
    @Schema(description = "Whether domain is active", required = true) boolean active,
    @Schema(description = "Number of teams in this domain", required = true) long teamCount,
    @Schema(description = "Number of users in this domain", required = true) long userCount,
    @Schema(description = "Domain creation timestamp", required = true) Instant createdAt) {

  public static AdminDomainDto from(Domain domain, long teamCount, long userCount) {
    return new AdminDomainDto(
        TsidUtils.toString(domain.getId()),
        domain.getDomain(),
        domain.getName(),
        domain.getBaseUrl(),
        domain.isSingleTeam(),
        domain.getAndroidFingerprints(),
        domain.isActive(),
        teamCount,
        userCount,
        domain.getCreatedAt());
  }
}
