/// Application configuration
class AppConfig {
  AppConfig._();

  /// API base URL
  static const String apiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'https://www.pedalons.fr',
  );

  /// WebAuthn Relying Party ID
  static const String webAuthnRpId = String.fromEnvironment(
    'WEBAUTHN_RP_ID',
    defaultValue: 'www.pedalons.fr',
  );

  /// Deep link scheme and host
  static const String deepLinkScheme = 'https';
  static const String deepLinkHost = String.fromEnvironment(
    'DEEP_LINK_HOST',
    defaultValue: 'www.pedalons.fr',
  );
}
