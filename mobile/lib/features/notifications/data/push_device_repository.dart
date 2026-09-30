import 'dart:io' show Platform;

import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/utils/device_description.dart';

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
  ///
  /// Le nom de l'appareil sert au membre à reconnaître ses téléphones dans une
  /// future liste. Jamais bloquant : un nom manquant est accepté par le
  /// contrat, une inscription ratée priverait de push.
  Future<void> register(String token) async {
    final DeviceDescription device = await DeviceDescription.load();
    await _client.registerPushDevice(
      body: PushDeviceRegistration(
        token: token,
        platform: (Platform.isIOS ? PushPlatform.ios : PushPlatform.android)
            .toJson(),
        deviceName: device.deviceName,
        appVersion: device.appVersionWithBuild,
      ),
    );
  }

  /// Le jeton part dans le corps : dans le chemin, il finissait dans le journal
  /// d'accès (`docs/LEDGER_*.md API-45`).
  Future<void> unregister(String token) => _client.unregisterPushDevice(
    body: PushDeviceUnregistration(token: token),
  );
}
