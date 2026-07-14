package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * External identity providers a user can authenticate with. Only {@code STRAVA} is wired today
 * (biketeam recovery), but the identity model and OAuth plumbing are generic so Facebook/Google can
 * be added without a schema change.
 */
@Getter
@RequiredArgsConstructor
public enum SocialProvider {
  STRAVA("Strava");

  private final String displayName;
}
