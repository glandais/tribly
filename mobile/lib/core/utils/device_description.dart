import 'dart:io' show Platform;

import 'package:device_info_plus/device_info_plus.dart';
import 'package:package_info_plus/package_info_plus.dart';

/// Ce que l'app sait dire d'elle-même et de l'appareil : version, modèle,
/// système.
///
/// Partagé par l'inscription push (qui nomme l'appareil) et les rapports
/// d'erreur (qui décrivent l'environnement). **Chaque lecture est isolée** :
/// un plugin qui échoue laisse son champ nul, jamais l'appel entier en échec —
/// une inscription push ou un rapport ratés pour un nom de modèle seraient
/// un mauvais échange.
class DeviceDescription {
  const DeviceDescription({
    this.appVersion,
    this.buildNumber,
    this.deviceName,
    this.model,
    this.osVersion,
  });

  /// `1.0.0`
  final String? appVersion;

  /// `55`
  final String? buildNumber;

  /// Le nom sous lequel le membre reconnaît son appareil : le nom donné par
  /// l'utilisateur sur iOS, fabricant et modèle sur Android.
  final String? deviceName;

  /// Le modèle **sans** nom personnel — `iPhone15,2`, `Google Pixel 8`. C'est
  /// lui, et non [deviceName], qui part dans un rapport : « l'iPhone de
  /// Gabriel » n'aide personne à reproduire un bug.
  final String? model;

  /// `Android 15 (SDK 35)`, `iOS 18.1`
  final String? osVersion;

  /// `1.0.0+55`, la forme qu'attend l'inscription push.
  String? get appVersionWithBuild {
    if (appVersion == null) return null;
    if (buildNumber == null || buildNumber!.isEmpty) return appVersion;
    return '$appVersion+$buildNumber';
  }

  static Future<DeviceDescription> load() async {
    String? appVersion;
    String? buildNumber;
    try {
      final PackageInfo info = await PackageInfo.fromPlatform();
      appVersion = info.version;
      buildNumber = info.buildNumber;
    } catch (_) {}

    String? deviceName;
    String? model;
    String? osVersion;
    try {
      final DeviceInfoPlugin info = DeviceInfoPlugin();
      if (Platform.isIOS) {
        final IosDeviceInfo ios = await info.iosInfo;
        deviceName = ios.name;
        model = ios.utsname.machine;
        osVersion = '${ios.systemName} ${ios.systemVersion}';
      } else if (Platform.isAndroid) {
        final AndroidDeviceInfo android = await info.androidInfo;
        deviceName = '${android.manufacturer} ${android.model}';
        model = deviceName;
        osVersion =
            'Android ${android.version.release} (SDK ${android.version.sdkInt})';
      }
    } catch (_) {}

    return DeviceDescription(
      appVersion: appVersion,
      buildNumber: buildNumber,
      deviceName: deviceName,
      model: model,
      osVersion: osVersion,
    );
  }
}
