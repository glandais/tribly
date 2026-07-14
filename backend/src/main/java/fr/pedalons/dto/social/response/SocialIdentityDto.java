package fr.pedalons.dto.social.response;

import fr.pedalons.domain.social.UserSocialIdentity;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.SocialProvider;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "A linked external identity (e.g. Strava)")
@ValidateSchema
public record SocialIdentityDto(
    @Schema(description = "Provider identifier", required = true) SocialProvider provider,
    @Schema(description = "Display name of the provider", required = true) String displayName,
    @Schema(description = "When the identity was linked", required = true) Instant linkedAt) {

  public static SocialIdentityDto from(UserSocialIdentity identity) {
    return new SocialIdentityDto(
        identity.getProvider(), identity.getProvider().getDisplayName(), identity.getCreatedAt());
  }
}
