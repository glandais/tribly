package fr.pedalons.enums;

/**
 * The OAuth protocol a domain's GPS credential belongs to. Only Garmin may use {@link #OAUTH1}: its
 * OAuth 2.0 programme admits no new application, while applications declared earlier still work
 * over OAuth 1.0a (docs/LEDGER_*.md API-62).
 */
public enum GpsOAuthVersion {
  OAUTH1,
  OAUTH2
}
