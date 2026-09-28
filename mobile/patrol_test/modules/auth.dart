import 'package:flutter/widgets.dart';

import 'module.dart';

/// Signing in, signing up, and the pages the mailed links open: e-mail verification, forgotten
/// password, new password.
final class Auth extends Module {
  Auth(super.$);

  Future<void> logInWithPassword(String email, String password) async {
    await $(keys.login.emailField).enterText(email);
    await $(keys.login.passwordField).enterText(password);
    await $(keys.login.submitButton).tap();
  }

  Future<void> waitUntilLoginPageIsVisible() async {
    await $(keys.login.submitButton).waitUntilVisible();
  }

  /// The login form's error banner, once a login was refused.
  Future<void> waitUntilLoginErrorIsShown() async {
    await $(keys.login.loginError).waitUntilVisible();
  }

  /// The address the login form's e-mail field holds.
  String get loginEmail => _fieldText(keys.login.emailField);

  // ── Sign-up ─────────────────────────────────────────────────────────────

  Future<void> showRegisterForm() async {
    await (await scrolledTo(keys.login.showRegisterButton)).tap();
    await $(keys.login.registerEmailField).waitUntilVisible();
  }

  Future<void> fillRegisterForm({
    required String email,
    required String displayName,
    required String password,
  }) async {
    await (await scrolledTo(keys.login.registerEmailField)).enterText(email);
    await (await scrolledTo(
      keys.login.registerDisplayNameField,
    )).enterText(displayName);
    await (await scrolledTo(
      keys.login.registerPasswordField,
    )).enterText(password);
    await (await scrolledTo(
      keys.login.registerConfirmField,
    )).enterText(password);
  }

  Future<void> acceptTerms() async {
    await (await scrolledTo(keys.login.termsCheckbox)).tap();
  }

  Future<void> submitRegistration() async {
    await (await scrolledTo(keys.login.registerSubmitButton)).tap();
  }

  /// The inline « conditions required » message under the terms box.
  Future<void> waitUntilTermsRequiredIsShown() async {
    await $(keys.login.termsError).waitUntilExists();
  }

  /// Whether the sign-up form is still the one shown.
  bool get showsRegisterForm => isShown(keys.login.registerSubmitButton);

  // ── E-mail verification ─────────────────────────────────────────────────

  /// Waits for the verification page to succeed, then leaves it: « Continuer », or « Plus tard »
  /// when the page first offers a passkey — depending on whether the simulator supports them.
  Future<void> continueAfterVerification() async {
    await $(keys.login.verifySuccess).waitUntilExists();
    final button = await waitUntilAnyIsShown([
      keys.login.verifyContinueButton,
      keys.login.verifyLaterButton,
    ]);
    await $(button).tap();
  }

  /// The verification page's error state, with its way back to the login.
  Future<void> waitUntilVerificationIsRefused() async {
    await $(keys.login.verifyError).waitUntilExists();
    await $(keys.login.verifyBackToLoginButton).waitUntilVisible();
  }

  bool get showsVerificationSuccess => isShown(keys.login.verifySuccess);

  /// « Retour à la connexion », from the verification page's error state.
  Future<void> backToLoginFromVerification() async {
    await $(keys.login.verifyBackToLoginButton).tap();
    await waitUntilLoginPageIsVisible();
  }

  // ── Forgotten password ──────────────────────────────────────────────────

  Future<void> openForgotPassword() async {
    await (await scrolledTo(keys.login.forgotPasswordButton)).tap();
    await $(keys.login.forgotEmailField).waitUntilVisible();
  }

  /// Asks for a reset link for [email], and waits for the « sent » screen — the same whether the
  /// address exists or not.
  Future<void> requestPasswordReset(String email) async {
    await $(keys.login.forgotEmailField).enterText(email);
    await $(keys.login.forgotSubmitButton).tap();
    await $(keys.login.forgotSentState).waitUntilVisible();
  }

  /// Fills the new-password page with [password], twice, and submits it.
  Future<void> setNewPassword(String password) async {
    await $(keys.login.resetPasswordField).enterText(password);
    await $(keys.login.resetConfirmField).enterText(password);
    await $(keys.login.resetSubmitButton).tap();
  }

  /// The new-password page's « invalid link » screen.
  Future<void> waitUntilResetLinkIsRefused() async {
    await $(keys.login.resetInvalidState).waitUntilVisible();
  }

  /// « Demander un nouveau lien », from the invalid-link screen: back to the forgotten password.
  Future<void> requestNewResetLink() async {
    await $(keys.login.resetRequestNewButton).tap();
    await $(keys.login.forgotEmailField).waitUntilVisible();
  }

  String _fieldText(Key key) =>
      ($(key).$(EditableText).evaluate().first.widget as EditableText)
          .controller
          .text;
}
