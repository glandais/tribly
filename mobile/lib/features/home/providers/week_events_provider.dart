import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../core/utils/formatters.dart';
import '../../calendar/data/calendar_repository.dart';

/// Combien de jours « Cette semaine » couvre, aujourd'hui compris.
const int kWeekWindowDays = 7;

/// Les sorties et étapes de voyage des sept prochains jours, **toutes mes
/// équipes**, dans l'ordre chronologique.
///
/// **Un seul appel** : `GET /api/calendar/events`, le même que l'écran
/// Calendrier, qui porte sur chaque événement tout ce que la ligne d'agenda
/// rend (équipe, heure, `registered`) — aucun appel de détail derrière.
///
/// La fenêtre part de minuit **dans le fuseau d'affichage** (ledger `API-15`),
/// pas de l'instant présent : une sortie de ce matin déjà commencée reste
/// visible tant qu'elle n'est pas finie. Ce que le serveur dit terminé
/// (`finished`) est écarté — le bloc parle de ce qui vient.
///
/// Comme « Ma prochaine sortie », c'est un enrichissement : un échec masque le
/// bloc, il ne casse pas l'accueil.
final weekEventsProvider = FutureProvider<List<CalendarEventDto>>((
  Ref ref,
) async {
  final DateTime now = AppFormatters.displayNow();
  final DateTime start = DateTime(now.year, now.month, now.day);
  // `DateTime(…, day + n)` et non `add(Duration(days: n))` : un changement
  // d'heure dans la semaine décalerait la borne d'une heure.
  final DateTime end = DateTime(now.year, now.month, now.day + kWeekWindowDays);
  final List<CalendarEventDto> events = await ref
      .watch(calendarRepositoryProvider)
      .getEvents(start: start, end: end);
  return weekAgendaEvents(events);
});

/// Les événements que l'agenda montre : non terminés, du plus proche au plus
/// lointain. Pur, pour les tests.
List<CalendarEventDto> weekAgendaEvents(List<CalendarEventDto> events) {
  final List<CalendarEventDto> upcoming = <CalendarEventDto>[
    for (final CalendarEventDto e in events)
      if (!e.finished) e,
  ];
  // Des instants ISO 8601 du contrat : les comparer en instants, pas en
  // chaînes, qu'un décalage horaire différent rendrait trompeuses.
  upcoming.sort(
    (CalendarEventDto a, CalendarEventDto b) =>
        DateTime.parse(a.start).compareTo(DateTime.parse(b.start)),
  );
  return upcoming;
}
