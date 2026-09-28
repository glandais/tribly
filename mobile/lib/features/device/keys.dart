import 'package:flutter/widgets.dart';

class _DeviceVerifyKey extends ValueKey<String> {
  const _DeviceVerifyKey(String value) : super('deviceVerify_$value');
}

class DeviceVerifyKeys {
  final codeField = const _DeviceVerifyKey('codeField');
  final submitButton = const _DeviceVerifyKey('submitButton');
  final success = const _DeviceVerifyKey('success');
  final error = const _DeviceVerifyKey('error');
  final tryAgainButton = const _DeviceVerifyKey('tryAgainButton');
}
