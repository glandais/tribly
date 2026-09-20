import 'dart:io' show Platform;

import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:package_info_plus/package_info_plus.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final pushDeviceRepositoryProvider = Provider<PushDeviceRepository>((Ref ref) {
  return PushDeviceRepository(ref.watch(notificationsClientProvider));
});

/// Les deux appels d'appareil de l'API : s'inscrire, se désinscrire.
///
/// Ils vivent à part de [NotificationsRepository] parce qu'ils ne servent pas
/// la boîte de réception mais le canal push, et que la boîte doit rester
/// utilisable sur une plateforme qui n'a pas de jeton.
class PushDeviceRepository {
  PushDeviceRepository(this._client);

  final NotificationsClient _client;

  /// Enregistre ou rafraîchit le jeton. L'endpoint est idempotent : appelé à
  /// chaque lancement, il ne crée pas de ligne de plus, il repousse la date de
  /// dernière vue — et déplace le jeton si l'appareil a changé de main.
  Future<void> register(String token) async {
    await _client.registerPushDevice(
      body: PushDeviceRegistration(
        token: token,
        platform: (Platform.isIOS ? PushPlatform.ios : PushPlatform.android)
            .toJson(),
        deviceName: await _deviceName(),
        appVersion: await _appVersion(),
      ),
    );
  }

  Future<void> unregister(String token) =>
      _client.unregisterPushDevice(token: token);

  /// Le modèle de l'appareil, pour que le membre reconnaisse ses téléphones
  /// dans une future liste. Jamais bloquant : un nom manquant est accepté par
  /// le contrat, une inscription ratée priverait de push.
  Future<String?> _deviceName() async {
    try {
      final DeviceInfoPlugin info = DeviceInfoPlugin();
      if (Platform.isIOS) return (await info.iosInfo).name;
      final AndroidDeviceInfo android = await info.androidInfo;
      return '${android.manufacturer} ${android.model}';
    } catch (_) {
      return null;
    }
  }

  Future<String?> _appVersion() async {
    try {
      final PackageInfo info = await PackageInfo.fromPlatform();
      return '${info.version}+${info.buildNumber}';
    } catch (_) {
      return null;
    }
  }
}
