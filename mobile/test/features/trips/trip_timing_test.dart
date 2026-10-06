import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/features/trips/providers/trip_detail_provider.dart';

import 'trip_fixtures.dart';

// docs/LEDGER_*.md API-60 : la fin d'un voyage se lit dans le fuseau de son
// étape la plus tardive, comme `TripDto.endDate` (le plus grand `dateTime`).
void main() {
  test(
    "endTimezone suit l'étape la plus tardive, pas la dernière par ordre",
    () {
      final trip = fixtureTrip(
        stages: [
          fixtureStage(
            index: 1,
            dateTime: '2099-10-20T08:00:00Z',
            timezone: 'Europe/Paris',
          ),
          fixtureStage(
            index: 2,
            dateTime: '2099-10-19T00:00:00Z',
            timezone: 'America/New_York',
          ),
        ],
      );
      expect(trip.endTimezone, 'Europe/Paris');
    },
  );

  test('endTimezone retombe sur le fuseau du voyage sans étapes', () {
    final trip = fixtureTrip(stages: []);
    expect(trip.endTimezone, trip.timezone);
  });
}
