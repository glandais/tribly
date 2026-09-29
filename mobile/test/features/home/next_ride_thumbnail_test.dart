import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/features/home/presentation/widgets/next_ride_card.dart';

import '../rides/ride_fixtures.dart';

/// `docs/LEDGER_*.md API-3` : la vignette de « Ma prochaine sortie » est celle
/// du parcours du groupe, celle de la sortie restant le repli.
void main() {
  final ride = fixtureRide().copyWith(
    thumbnailLightUrl: 'ride-light',
    thumbnailDarkUrl: 'ride-dark',
  );

  test('le parcours du groupe l’emporte sur la sortie, par thème', () {
    final group = fixtureGroup().copyWith(
      thumbnailLightUrl: 'group-light',
      thumbnailDarkUrl: 'group-dark',
    );
    expect(nextRideThumbnailUrl(ride, group, dark: false), 'group-light');
    expect(nextRideThumbnailUrl(ride, group, dark: true), 'group-dark');
  });

  test('une seule variante du groupe sert aux deux thèmes', () {
    final group = fixtureGroup().copyWith(thumbnailDarkUrl: 'group-dark');
    expect(nextRideThumbnailUrl(ride, group, dark: false), 'group-dark');
  });

  test('sans vignette de groupe, ni sans groupe, la sortie sert de repli', () {
    expect(nextRideThumbnailUrl(ride, fixtureGroup(), dark: true), 'ride-dark');
    expect(nextRideThumbnailUrl(ride, null, dark: false), 'ride-light');
  });
}
