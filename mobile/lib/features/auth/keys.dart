import 'package:flutter/widgets.dart';

class _LoginPageKey extends ValueKey<String> {
  const _LoginPageKey(String value) : super('loginPage_$value');
}

class LoginPageKeys {
  final emailField = const _LoginPageKey('emailField');
  final passwordField = const _LoginPageKey('passwordField');
  final submitButton = const _LoginPageKey('submitButton');
}
