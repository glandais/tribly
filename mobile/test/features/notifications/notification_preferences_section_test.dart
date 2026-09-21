import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/pagination/pagination.dart';
import 'package:pedalons/core/pdl/pdl.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/features/notifications/data/notifications_repository.dart';
import 'package:pedalons/features/notifications/presentation/widgets/notification_preferences_section.dart';

import '../../support/localization.dart';

/// La section du profil : ce qu'elle montre selon ce que le serveur déclare,
/// et l'appel unique que chaque interrupteur envoie.
class _StubRepository implements NotificationsRepository {
  _StubRepository({
    this.channels = const <NotificationChannel>[],
    List<NotificationTeamPreferenceDto> teams =
        const <NotificationTeamPreferenceDto>[],
  }) : _teams = teams;

  final List<NotificationChannel> channels;
  List<NotificationTeamPreferenceDto> _teams;
  bool _digest = false;
  final List<String> calls = <String>[];

  @override
  Future<NotificationPreferencesDto> preferences() async =>
      NotificationPreferencesDto(
        channels: channels,
        preferences: <NotificationPreferenceDto>[
          for (final NotificationChannel channel in channels)
            NotificationPreferenceDto(
              type: NotificationType.ridePublished.toJson(),
              channel: channel.toJson(),
              enabled: true,
              enabledByDefault: true,
            ),
        ],
        teams: _teams,
        emailDigest: _digest,
      );

  @override
  Future<NotificationPreferencesDto> setTeamMuted({
    required String teamSlug,
    required bool muted,
  }) async {
    calls.add('team:$teamSlug:$muted');
    _teams = <NotificationTeamPreferenceDto>[
      for (final NotificationTeamPreferenceDto t in _teams)
        t.teamSlug == teamSlug ? t.copyWith(muted: muted) : t,
    ];
    return preferences();
  }

  @override
  Future<NotificationPreferencesDto> setEmailDigest(bool enabled) async {
    calls.add('digest:$enabled');
    _digest = enabled;
    return preferences();
  }

  @override
  Future<NotificationPreferencesDto> setPreference({
    required NotificationType type,
    required NotificationChannel channel,
    required bool enabled,
  }) async {
    calls.add('cell:${type.toJson()}:${channel.toJson()}:$enabled');
    return preferences();
  }

  @override
  Future<PageResult<NotificationDto>> fetchPage({
    int page = 0,
    int size = 20,
    bool unreadOnly = false,
  }) async =>
      const PageResult<NotificationDto>(items: <NotificationDto>[], total: 0);

  @override
  Future<int> unreadCount() async => 0;

  @override
  Future<void> markRead(String id) async {}

  @override
  Future<void> markAllRead() async {}
}

const NotificationTeamPreferenceDto _gaby = NotificationTeamPreferenceDto(
  teamSlug: 'gaby',
  teamName: 'Gaby Cyclo',
  muted: false,
);

void main() {
  setUpAll(loadTestTranslations);

  Future<void> pump(WidgetTester tester, _StubRepository repository) async {
    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          notificationsRepositoryProvider.overrideWithValue(repository),
        ],
        child: MaterialApp(
          theme: PedalonsTheme.build(Brightness.light),
          home: const Scaffold(
            body: SingleChildScrollView(
              child: NotificationPreferencesSection(),
            ),
          ),
        ),
      ),
    );
    for (int i = 0; i < 4; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
  }

  testWidgets('ni canal ni équipe : rien du tout, en-tête compris', (
    WidgetTester tester,
  ) async {
    await pump(tester, _StubRepository());

    expect(find.text('Notifications'), findsNothing);
    expect(find.byType(PdlSwitch), findsNothing);
  });

  testWidgets(
    'sans canal, les équipes se règlent quand même — et la matrice reste cachée',
    (WidgetTester tester) async {
      final _StubRepository repository = _StubRepository(
        teams: const <NotificationTeamPreferenceDto>[_gaby],
      );
      await pump(tester, repository);

      expect(find.text('Annonces des équipes'), findsOneWidget);
      expect(find.text('Gaby Cyclo'), findsOneWidget);
      expect(find.text('Sortie publiée'), findsNothing);
      expect(find.text('Résumé quotidien par e-mail'), findsNothing);
      expect(find.byType(PdlSwitch), findsOneWidget);
      // Allumé = je reçois ses annonces.
      expect(tester.widget<PdlSwitch>(find.byType(PdlSwitch)).value, isTrue);

      await tester.tap(find.byType(PdlSwitch));
      for (int i = 0; i < 4; i++) {
        await tester.pump(const Duration(milliseconds: 10));
      }

      expect(repository.calls, <String>['team:gaby:true']);
      expect(tester.widget<PdlSwitch>(find.byType(PdlSwitch)).value, isFalse);
    },
  );

  testWidgets('le résumé quotidien n\'apparaît qu\'avec l\'e-mail', (
    WidgetTester tester,
  ) async {
    await pump(
      tester,
      _StubRepository(
        channels: const <NotificationChannel>[NotificationChannel.push],
      ),
    );
    expect(find.text('Résumé quotidien par e-mail'), findsNothing);

    final _StubRepository withEmail = _StubRepository(
      channels: const <NotificationChannel>[NotificationChannel.email],
    );
    await pump(tester, withEmail);
    expect(find.text('Résumé quotidien par e-mail'), findsOneWidget);

    // Sous la matrice : le dernier interrupteur de la carte.
    await tester.tap(find.byType(PdlSwitch).last);
    for (int i = 0; i < 4; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
    expect(withEmail.calls, <String>['digest:true']);
  });
}
