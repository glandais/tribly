import 'package:flutter/widgets.dart';

class _LoginPageKey extends ValueKey<String> {
  const _LoginPageKey(String value) : super('loginPage_$value');
}

/// Les clés du parcours d'authentification : connexion, inscription, et les
/// pages où mènent les liens reçus par e-mail (vérification, mot de passe
/// oublié, nouveau mot de passe) — un seul groupe, celui de `keys.login`.
class LoginPageKeys {
  // Connexion.
  final emailField = const _LoginPageKey('emailField');
  final passwordField = const _LoginPageKey('passwordField');
  final submitButton = const _LoginPageKey('submitButton');

  /// Le bandeau d'erreur du formulaire de connexion.
  final loginError = const _LoginPageKey('loginError');

  /// « Mot de passe oublié ? », sous le mot de passe.
  final forgotPasswordButton = const _LoginPageKey('forgotPasswordButton');

  /// Le lien qui bascule le formulaire en inscription.
  final showRegisterButton = const _LoginPageKey('showRegisterButton');

  // Inscription.
  final registerEmailField = const _LoginPageKey('registerEmailField');
  final registerDisplayNameField = const _LoginPageKey(
    'registerDisplayNameField',
  );
  final registerPasswordField = const _LoginPageKey('registerPasswordField');
  final registerConfirmField = const _LoginPageKey('registerConfirmField');
  final termsCheckbox = const _LoginPageKey('termsCheckbox');

  /// Le message sous la case des conditions quand elle n'est pas cochée.
  final termsError = const _LoginPageKey('termsError');
  final registerSubmitButton = const _LoginPageKey('registerSubmitButton');

  // Vérification de l'e-mail.
  final verifySuccess = const _LoginPageKey('verifySuccess');
  final verifyError = const _LoginPageKey('verifyError');
  final verifyBackToLoginButton = const _LoginPageKey(
    'verifyBackToLoginButton',
  );
  final verifyContinueButton = const _LoginPageKey('verifyContinueButton');

  /// « Plus tard », quand la page propose d'abord une clé d'accès.
  final verifyLaterButton = const _LoginPageKey('verifyLaterButton');

  // Mot de passe oublié.
  final forgotEmailField = const _LoginPageKey('forgotEmailField');
  final forgotSubmitButton = const _LoginPageKey('forgotSubmitButton');
  final forgotSentState = const _LoginPageKey('forgotSentState');

  // Nouveau mot de passe.
  final resetPasswordField = const _LoginPageKey('resetPasswordField');
  final resetConfirmField = const _LoginPageKey('resetConfirmField');
  final resetSubmitButton = const _LoginPageKey('resetSubmitButton');

  /// L'écran « lien invalide » : jeton déjà servi, expiré ou inconnu.
  final resetInvalidState = const _LoginPageKey('resetInvalidState');
  final resetRequestNewButton = const _LoginPageKey('resetRequestNewButton');
}
