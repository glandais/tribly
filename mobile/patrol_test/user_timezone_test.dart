import 'package:pedalons/core/utils/formatters.dart';
import 'package:timezone/data/latest.dart' as tz_data;
import 'package:timezone/timezone.dart' as tz;

import 'api/profile_seed.dart';
import 'api/rides_home_trips_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-account.e2e.ts` › the profile's timezone picker — the web renders every
/// date in `UserDto.timezone` when it is set, and the app applies the same preference
/// (docs/LEDGER_*.md API-15). Choosing it in the app is `profile_timezone_test`.
///
/// A stage at 08:00 in Auckland is set on the server as an instant; a member whose preference is
/// `Pacific/Auckland` reads « 08:00 » and that day — whatever the device's own zone, which would
/// put it on another hour, and often another day.
void main() {
  const String zone = 'Pacific/Auckland';

  testApp(
    'A member who chose a timezone reads a stage’s date and time in it, not in the device’s',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Timezone owner');
      final member = await backend.newUser('Timezone member');
      final team = await backend.newTeam(owner, 'Equipe fuseau');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      await backend.setTimezone(member, zone);

      tz_data.initializeTimeZones();
      final tz.Location auckland = tz.getLocation(zone);
      final DateTime inTen = DateTime.now().add(const Duration(days: 10));
      final tz.TZDateTime start = tz.TZDateTime(
        auckland,
        inTen.year,
        inTen.month,
        inTen.day,
        8,
      );
      // The device's reading of the same instant: the test only means something if it differs.
      final DateTime onDevice = start.toUtc().toLocal();
      expect(
        onDevice.hour != 8 || onDevice.day != start.day,
        isTrue,
        reason: 'the device runs in a zone that reads like $zone',
      );

      final stageName = unique('Etape antipodes');
      final trip = await backend.newTrip(
        owner,
        teamSlug,
        'Voyage antipodes',
        dateTime: start,
        stages: [(name: stageName, at: start)],
      );
      final tripSlug = trip['slug'] as String;
      final stageSlug =
          ((trip['stages'] as List).cast<Json>().single)['slug'] as String;

      await openAppSignedIn($, member);
      await openLink($, Paths.stage(teamSlug, tripSlug, stageSlug));
      await modules.trip.waitUntilStageIs(stageName);

      // A wall clock is never converted again: the app's own formatter gives the expected label.
      final DateTime wall = DateTime(start.year, start.month, start.day, 8);
      expect($(AppFormatters.formatLongDate(wall)).exists, isTrue);
      expect($(RegExp(r'\b08:00\b')).exists, isTrue);
    },
  );
}
