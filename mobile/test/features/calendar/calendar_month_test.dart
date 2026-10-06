import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/utils/formatters.dart';
import 'package:pedalons/features/calendar/presentation/widgets/calendar_subscription_card.dart';
import 'package:pedalons/features/calendar/providers/calendar_month_provider.dart';

CalendarEventDto event({
  required String start,
  String type = 'RIDE',
  bool registered = false,
  String status = 'PUBLISHED',
  String? groupName,
  bool allDay = false,
  String timezone = 'Europe/Paris',
}) => CalendarEventDto(
  timezone: timezone,
  id: start,
  title: 'Sortie $start',
  start: start,
  allDay: allDay,
  type: type,
  teamSlug: 'n-peloton',
  teamName: 'N-Peloton',
  entitySlug: 'sortie',
  registered: registered,
  status: status,
  groupName: groupName,
  finished: DateTime.parse(start).isBefore(DateTime.now()),
);

void main() {
  group('CalendarMonth', () {
    test('les événements sont triés et groupés par jour local', () {
      final CalendarMonth month = CalendarMonth.fromEvents(<CalendarEventDto>[
        event(start: '2026-07-22T17:30:00Z'),
        event(start: '2026-07-19T05:00:00Z'),
        event(start: '2026-07-22T06:00:00Z'),
      ]);

      expect(month.events.first.start, '2026-07-19T05:00:00Z');
      expect(month.byDay.length, 2);
      expect(month.days.first.day, 19);
      expect(month.byDay[month.days.last]!.length, 2);
    });

    test('le filtre de type ne coûte aucun appel et reste exclusif', () {
      final CalendarMonth month = CalendarMonth.fromEvents(<CalendarEventDto>[
        event(start: '2026-07-22T17:30:00Z'),
        event(start: '2026-07-26T06:00:00Z', type: 'TRIP_STAGE'),
      ]);

      expect(month.filtered(CalendarTypeFilter.all).events.length, 2);
      expect(month.filtered(CalendarTypeFilter.rides).events.length, 1);
      expect(
        month.filtered(CalendarTypeFilter.trips).events.single.type,
        'TRIP_STAGE',
      );
    });

    test('un instant UTC n\'est converti qu\'une fois — §5.3-3', () {
      // `dayOf` doit rendre exactement le jour local de l'instant : une
      // seconde conversion (par exemple `toLocal()` appliqué deux fois, ou un
      // `DateTime.parse` déjà local re-décalé) ferait glisser la date d'un
      // jour sur un appareil réglé loin de UTC.
      const String instant = '2026-08-17T06:00:00Z';
      final DateTime expected = DateTime.parse(instant).toLocal();
      final DateTime day = CalendarMonth.dayOf(event(start: instant));

      expect(day.year, expected.year);
      expect(day.month, expected.month);
      expect(day.day, expected.day);
      expect(day.hour, 0);
    });
  });

  group('la grille est dans le fuseau du lecteur, même en journée entière', () {
    tearDown(() => AppFormatters.setDisplayTimezone(null));

    test('une étape du 12 à Tokyo tombe le 11 lue depuis Paris', () {
      // Plan §7 : une même grille ne porte qu'un fuseau, celui du lecteur —
      // parité avec la grille web (docs/LEDGER_*.md API-60). 00:30 le 12 à
      // Tokyo = 15:30Z le 11 = 17:30 le 11 à Paris.
      AppFormatters.setDisplayTimezone('Europe/Paris');
      const String start = '2026-10-11T15:30:00Z';

      expect(
        CalendarMonth.dayOf(
          event(start: start, allDay: true, timezone: 'Asia/Tokyo'),
        ),
        DateTime(2026, 10, 11),
      );
      expect(
        CalendarMonth.dayOf(event(start: start, timezone: 'Asia/Tokyo')),
        DateTime(2026, 10, 11),
      );
    });
  });

  group('CalendarMonthKey', () {
    test('la fenêtre couvre le mois entier, dernier jour inclus', () {
      const CalendarMonthKey key = CalendarMonthKey(year: 2026, month: 7);
      expect(key.start, DateTime(2026, 7, 1));
      expect(key.end, DateTime(2026, 7, 31, 23, 59, 59));
    });

    test('le décalage traverse les fins d\'année', () {
      const CalendarMonthKey december = CalendarMonthKey(
        year: 2026,
        month: 12,
        teamSlug: 'n-peloton',
      );
      final CalendarMonthKey january = december.shifted(1);
      expect(january.year, 2027);
      expect(january.month, 1);
      expect(january.teamSlug, 'n-peloton', reason: 'la portée survit');
    });
  });

  group('masquage du jeton ICS — §5.3-4', () {
    test('le jeton disparaît, l\'extension et l\'hôte restent', () {
      expect(
        maskCalendarFeedUrl('https://pedalons.fr/api/calendar/0k3n53cr37.ics'),
        'https://pedalons.fr/api/calendar/••••••••.ics',
      );
      expect(
        maskCalendarFeedUrl(
          'https://pedalons.fr/api/teams/n-peloton/calendar/0k3n53cr37.ics',
        ),
        'https://pedalons.fr/api/teams/n-peloton/calendar/••••••••.ics',
      );
    });

    test('aucun caractère du jeton ne survit, requête comprise', () {
      const String token = '0k3n53cr37';
      for (final String url in <String>[
        'https://pedalons.fr/api/calendar/$token.ics',
        'https://pedalons.fr/api/teams/n-peloton/calendar/$token.ics',
        'https://pedalons.fr/api/calendar/ics?token=$token',
      ]) {
        expect(maskCalendarFeedUrl(url).contains(token), isFalse, reason: url);
      }
    });
  });
}
