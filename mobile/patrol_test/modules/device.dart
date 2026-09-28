import 'module.dart';

/// The page that pairs a Garmin or Karoo device (`/garmin`, `/karoo`).
final class Device extends Module {
  Device(super.$);

  Future<void> waitUntilPaired() async {
    await $(keys.device.success).waitUntilVisible();
  }

  Future<void> waitUntilCodeIsRefused() async {
    await $(keys.device.error).waitUntilVisible();
  }

  Future<void> tryAgain() async {
    await $(keys.device.tryAgainButton).tap();
    await $(keys.device.codeField).waitUntilVisible();
  }

  /// Types [code] as the rider reads it off the device, and continues.
  Future<void> enterCode(String code) async {
    await $(keys.device.codeField).enterText(code);
    await $(keys.device.submitButton).tap();
  }

  /// What the code field holds once typed — the field upper-cases as it goes.
  String? get typedCode => $(keys.device.codeField).text;
}
