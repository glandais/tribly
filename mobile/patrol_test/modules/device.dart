import 'module.dart';

/// The page that pairs a Garmin or Karoo device (`/garmin`, `/karoo`).
final class Device extends Module {
  Device(super.$);

  /// Existence, not hit-testing: the success state is a column of spaced blocks, and its centre
  /// can fall on one of the gaps between them — in French on an Android phone it does, and the
  /// column was never « visible » although fully on screen.
  Future<void> waitUntilPaired() async {
    await $(
      keys.device.success,
    ).waitUntilExists(timeout: const Duration(seconds: 10));
  }

  /// Existence too: the error state is the same kind of spaced column.
  Future<void> waitUntilCodeIsRefused() async {
    await $(
      keys.device.error,
    ).waitUntilExists(timeout: const Duration(seconds: 10));
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
