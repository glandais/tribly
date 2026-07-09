package fr.pedalons.dto.admin;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.platform.DomainAlias;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Admin view of a domain alias (dedicated hostname pinned to a team)")
@ValidateSchema
public record AdminDomainAliasDto(
    @Schema(description = "Alias ID (TSID)", required = true) String id,
    @Schema(description = "Alias hostname", required = true) String hostname,
    @Schema(description = "Parent domain ID (TSID)", required = true) String domainId,
    @Schema(description = "Pinned team ID (TSID)", required = true) String pinnedTeamId,
    @Schema(description = "Pinned team slug", required = true) String pinnedTeamSlug,
    @Schema(description = "Alias display name", required = true) String name,
    @Schema(description = "Base URL for the alias", required = true) String baseUrl,
    @Schema(
            description =
                "Android app SHA-256 certificate fingerprints for passkey origin verification"
                    + " (comma-separated, colon-hex format)")
        String androidFingerprints,
    @Schema(description = "Whether alias is active", required = true) boolean active,
    @Schema(description = "Alias creation timestamp", required = true) Instant createdAt) {

  public static AdminDomainAliasDto from(DomainAlias alias) {
    return new AdminDomainAliasDto(
        TsidUtils.toString(alias.getId()),
        alias.getHostname(),
        TsidUtils.toString(alias.getDomain().getId()),
        TsidUtils.toString(alias.getPinnedTeam().getId()),
        alias.getPinnedTeam().getSlug(),
        alias.getName(),
        alias.getBaseUrl(),
        alias.getAndroidFingerprints(),
        alias.isActive(),
        alias.getCreatedAt());
  }
}
