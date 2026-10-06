import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/features/teams/domain/publication_timing.dart';

import '../../support/localization.dart';

/// Ledger `MOB-60` — « En cours » et la fin affichée, d'après la fin stockée
/// (`endDateTime`, ledger `API-85`). Même règle que `isUnderWay` au web.
void main() {
  setUpAll(loadTestTranslations);

  final DateTime now = DateTime.utc(2026, 10, 11, 10);

  group('isUnderWay', () {
    bool at(String start, String end, {String status = 'PUBLISHED'}) =>
        isUnderWay(dateTime: start, endDateTime: end, status: status, now: now);

    test('parti et pas rentré : en cours', () {
      expect(at('2026-10-11T08:30:00Z', '2026-10-11T14:10:00Z'), isTrue);
    });

    test('au départ pile : en cours ; au retour pile : plus', () {
      expect(at('2026-10-11T10:00:00Z', '2026-10-11T14:00:00Z'), isTrue);
      expect(at('2026-10-11T06:00:00Z', '2026-10-11T10:00:00Z'), isFalse);
    });

    test('pas encore parti, ou déjà rentré : pas en cours', () {
      expect(at('2026-10-12T08:30:00Z', '2026-10-12T14:00:00Z'), isFalse);
      expect(at('2026-10-10T08:30:00Z', '2026-10-10T14:00:00Z'), isFalse);
    });

    test('annulée : jamais en cours', () {
      expect(
        at('2026-10-11T08:30:00Z', '2026-10-11T14:10:00Z', status: 'CANCELLED'),
        isFalse,
      );
    });

    test('une date illisible : pas en cours', () {
      expect(at('pas une date', '2026-10-11T14:10:00Z'), isFalse);
    });
  });

  group('rideTimeSpan', () {
    test('« départ → retour vers … »', () {
      final String? span = rideTimeSpan(
        '2026-10-11T08:30:00Z',
        '2026-10-11T10:10:00Z',
        now: now,
      );
      expect(span, contains(' → retour vers '));
    });

    test('une date illisible : rien, l\'appelant garde la date seule', () {
      expect(rideTimeSpan('?', '2026-10-11T10:10:00Z'), isNull);
    });
  });

  group('tripDaySpan', () {
    test('plusieurs jours : une flèche ; un seul : pas de flèche', () {
      expect(
        tripDaySpan('2026-10-16T08:00:00Z', '2026-10-18T16:00:00Z'),
        contains(' → '),
      );
      expect(
        tripDaySpan('2026-10-16T08:00:00Z', '2026-10-16T16:00:00Z'),
        isNot(contains(' → ')),
      );
    });
  });
}
