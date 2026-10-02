import 'package:flutter_test/flutter_test.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:intl/intl.dart';
import 'package:pedalons/core/utils/formatters.dart';

/// Le fuseau d'affichage (docs/LEDGER_*.md API-15) : `UserDto.timezone` quand
/// l'utilisateur en a choisi un, l'appareil sinon — la règle du web.
///
/// Les cas posent tous un fuseau explicite : le fuseau de la machine qui lance
/// les tests ne doit rien décider.
void main() {
  // L'étape du lundi 17 août 2026 à 08:00 à Auckland (NZST, UTC+12) : le
  // dimanche 16 à 20:00 UTC, et le dimanche 16 à 13:00 à Los Angeles (PDT).
  final DateTime stage = DateTime.utc(2026, 8, 16, 20);

  setUpAll(() => initializeDateFormatting('fr'));

  tearDown(() => AppFormatters.setDisplayTimezone(null));

  test(
    'un instant du contrat est rendu à l\'heure murale du fuseau choisi',
    () {
      AppFormatters.setDisplayTimezone('Pacific/Auckland');
      final DateTime wall = AppFormatters.toDisplayTime(stage);

      expect(
        <int>[wall.year, wall.month, wall.day, wall.hour],
        <int>[2026, 8, 17, 8],
      );
      expect(wall.weekday, DateTime.monday);
    },
  );

  test('le même instant change de jour avec le fuseau', () {
    AppFormatters.setDisplayTimezone('America/Los_Angeles');
    final DateTime wall = AppFormatters.toDisplayTime(stage);

    expect(<int>[wall.day, wall.hour], <int>[16, 13]);
    expect(wall.weekday, DateTime.sunday);
  });

  test('aucune double conversion : une heure murale reste ce qu\'elle est', () {
    AppFormatters.setDisplayTimezone('Pacific/Auckland');
    final DateTime once = AppFormatters.toDisplayTime(stage);

    expect(AppFormatters.toDisplayTime(once), once);
    expect(AppFormatters.tryParseDisplayTime('2026-08-16T20:00:00Z'), once);
  });

  test('les formateurs rendent l\'heure du fuseau choisi', () {
    AppFormatters.setDisplayTimezone('Pacific/Auckland');

    expect(
      Intl.withLocale('fr', () => AppFormatters.formatTime(stage)),
      '08:00',
    );
  });

  test(
    'une borne de mois repart vers l\'API en UTC, dans le fuseau choisi',
    () {
      AppFormatters.setDisplayTimezone('Pacific/Auckland');

      expect(
        AppFormatters.displayWallClockToUtc(DateTime(2026, 8)),
        DateTime.utc(2026, 7, 31, 12),
      );
      expect(
        AppFormatters.displayWallClockToUtc(AppFormatters.toDisplayTime(stage)),
        stage,
      );
    },
  );

  test('sans préférence, ou avec un nom inconnu, c\'est l\'appareil', () {
    AppFormatters.setDisplayTimezone(null);
    expect(AppFormatters.toDisplayTime(stage), stage.toLocal());

    AppFormatters.setDisplayTimezone('Mars/Olympus_Mons');
    expect(AppFormatters.toDisplayTime(stage), stage.toLocal());
  });
}
