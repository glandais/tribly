import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/profile/presentation/widgets/connected_services_section.dart';

/// docs/LEDGER_*.md API-14 — chaque service connu a son logo, et le fichier est
/// bien déclaré dans `pubspec.yaml` : un chemin faux ne se verrait qu'à
/// l'écran, en cadre d'erreur.
void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  for (final GpsServiceType service in GpsServiceType.values) {
    if (service == GpsServiceType.$unknown) continue;
    test('${service.json} a un logo embarqué', () async {
      final String? asset = gpsServiceLogoAsset(service);

      expect(asset, isNotNull);
      final ByteData bytes = await rootBundle.load(asset!);
      expect(bytes.lengthInBytes, greaterThan(0));
    });
  }

  test('un service inconnu de ce build n\'a pas de logo', () {
    expect(gpsServiceLogoAsset(GpsServiceType.$unknown), isNull);
  });
}
