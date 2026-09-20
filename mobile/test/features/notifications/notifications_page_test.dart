import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/pagination/pagination.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/features/notifications/data/notifications_repository.dart';
import 'package:pedalons/features/notifications/presentation/notification_display.dart';
import 'package:pedalons/features/notifications/presentation/pages/notifications_page.dart';

import '../../support/localization.dart';

/// La boîte de réception : ce qu'elle rend, et surtout ce qu'elle formule
/// elle-même.
///
/// Le point de tout l'étage client est là : l'API n'envoie **aucun texte
/// rendu** (`docs/plans/2026-09-18-notifications.md` §3), donc un écran qui
/// afficherait `RIDE_PUBLISHED` tel quel serait passé à côté. Les cas ci-dessous
/// vérifient la phrase, la destination et la distinction lu / non lu.
class _StubRepository implements NotificationsRepository {
  _StubRepository(this.items, {this.unread = 0});

  final List<NotificationDto> items;
  final int unread;
  final List<String> markedRead = <String>[];
  bool markedAll = false;

  @override
  Future<PageResult<NotificationDto>> fetchPage({
    int page = 0,
    int size = 20,
    bool unreadOnly = false,
  }) async {
    final List<NotificationDto> visible = unreadOnly
        ? items.where((NotificationDto n) => !n.read).toList()
        : items;
    return PageResult<NotificationDto>(
      items: page == 0 ? visible : <NotificationDto>[],
      total: visible.length,
    );
  }

  @override
  Future<int> unreadCount() async => unread;

  @override
  Future<void> markRead(String id) async => markedRead.add(id);

  @override
  Future<void> markAllRead() async => markedAll = true;

  @override
  Future<NotificationPreferencesDto> preferences() async =>
      const NotificationPreferencesDto(
        channels: <NotificationChannel>[],
        preferences: <NotificationPreferenceDto>[],
      );

  @override
  Future<NotificationPreferencesDto> setPreference({
    required NotificationType type,
    required NotificationChannel channel,
    required bool enabled,
  }) async => preferences();
}

NotificationDto _notification({
  String id = 'n1',
  NotificationType type = NotificationType.ridePublished,
  NotificationSubjectType subject = NotificationSubjectType.ride,
  bool read = false,
  String? actorName = 'Gaby Landais',
}) {
  return NotificationDto(
    id: id,
    type: type.toJson(),
    read: read,
    createdAt: DateTime.now()
        .subtract(const Duration(minutes: 5))
        .toUtc()
        .toIso8601String(),
    actorName: actorName,
    teamSlug: 'gaby',
    teamName: 'Gaby',
    subjectType: subject.toJson(),
    subjectSlug: 'sortie-du-dimanche',
    subjectName: 'Sortie du dimanche',
  );
}

void main() {
  setUpAll(() async {
    // L'écran affiche un compteur : `.plural()` exige une locale chargée.
    await loadTestTranslations();
  });

  Widget app(_StubRepository repository) {
    return ProviderScope(
      overrides: [
        notificationsRepositoryProvider.overrideWithValue(repository),
      ],
      child: MaterialApp(
        theme: PedalonsTheme.build(Brightness.light),
        home: const NotificationsPage(),
      ),
    );
  }

  Future<void> open(WidgetTester tester, _StubRepository repository) async {
    await tester.pumpWidget(app(repository));
    for (int i = 0; i < 4; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
  }

  testWidgets('une notification est formulée par le client, pas par l\'API', (
    WidgetTester tester,
  ) async {
    await open(tester, _StubRepository(<NotificationDto>[_notification()]));

    expect(find.text('Gaby Landais a publié une sortie'), findsOneWidget);
    expect(find.text('Sortie du dimanche'), findsOneWidget);
    // Le type brut ne doit jamais atteindre l'écran.
    expect(find.textContaining('RIDE_PUBLISHED'), findsNothing);
  });

  testWidgets(
    'une publication sans acteur nomme l\'équipe plutôt qu\'un trou',
    (WidgetTester tester) async {
      await open(
        tester,
        _StubRepository(<NotificationDto>[_notification(actorName: null)]),
      );

      expect(find.text('Gaby a publié une sortie'), findsOneWidget);
      expect(find.textContaining('null'), findsNothing);
    },
  );

  testWidgets('la boîte vide le dit, et ne propose pas de sortie de filtre', (
    WidgetTester tester,
  ) async {
    await open(tester, _StubRepository(<NotificationDto>[]));

    expect(find.text('Aucune notification'), findsOneWidget);
    expect(find.text('Toutes'), findsOneWidget); // le segmenté, pas un bouton
  });

  testWidgets('« Tout marquer lu » n\'apparaît qu\'avec des non lues', (
    WidgetTester tester,
  ) async {
    final _StubRepository withUnread = _StubRepository(<NotificationDto>[
      _notification(),
    ], unread: 1);
    await open(tester, withUnread);
    expect(find.bySemanticsLabel('Tout marquer lu'), findsOneWidget);

    await open(
      tester,
      _StubRepository(<NotificationDto>[_notification(read: true)]),
    );
    expect(find.bySemanticsLabel('Tout marquer lu'), findsNothing);
  });

  testWidgets('un sujet inconnu reste lisible, mais n\'est pas tapable', (
    WidgetTester tester,
  ) async {
    await open(
      tester,
      _StubRepository(<NotificationDto>[
        _notification(subject: NotificationSubjectType.$unknown),
      ]),
    );

    expect(find.text('Gaby Landais a publié une sortie'), findsOneWidget);
  });

  test('la destination vient du sujet, jamais du type', () {
    expect(_notification().path(), '/equipes/gaby/sorties/sortie-du-dimanche');
    expect(
      _notification(subject: NotificationSubjectType.post).path(),
      '/equipes/gaby/articles/sortie-du-dimanche',
    );
    // Un sujet qu'une version plus ancienne ne connaît pas n'invente pas de
    // route : la ligne se rend, elle ne mène nulle part.
    expect(
      _notification(subject: NotificationSubjectType.$unknown).path(),
      isNull,
    );
  });
}
