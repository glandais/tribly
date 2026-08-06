import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/trips/providers/trip_tracks_provider.dart';

/// L'emprise d'un voyage, lot après lot.
///
/// Un voyage se charge par paquets de parcours, et chaque réponse porte
/// l'emprise **de son paquet**. La garder telle quelle recadrait la carte sur
/// les dernières étapes arrivées ; il faut l'union, comme le fait
/// `useRoutesBulk.mergeExtents` côté web.
void main() {
  const BoundsDto ouest = BoundsDto(
    minLon: -1.6,
    minLat: 47.1,
    maxLon: -1.0,
    maxLat: 47.3,
  );
  const BoundsDto est = BoundsDto(
    minLon: 2.2,
    minLat: 48.7,
    maxLon: 2.5,
    maxLat: 48.9,
  );

  test('un seul lot donne son emprise', () {
    expect(unionExtents(null, ouest), ouest);
    expect(unionExtents(ouest, null), ouest);
    expect(unionExtents(null, null), isNull);
  });

  test('deux lots donnent leur union, pas le dernier', () {
    final BoundsDto? merged = unionExtents(ouest, est);
    expect(merged, isNotNull);
    expect(merged!.minLon, -1.6);
    expect(merged.minLat, 47.1);
    expect(merged.maxLon, 2.5);
    expect(merged.maxLat, 48.9);
  });
}
