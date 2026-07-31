import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/pdl/pdl.dart';
import 'package:pedalons/core/preferences/user_preferences_provider.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/features/comments/presentation/widgets/comment_thread.dart';
import 'package:pedalons/features/teams/data/team_repository.dart';
import 'package:pedalons/features/trips/data/trip_repository.dart';
import 'package:pedalons/features/trips/presentation/pages/stage_detail_page.dart';
import 'package:pedalons/features/trips/presentation/pages/trip_detail_page.dart';
import 'package:pedalons/features/trips/presentation/widgets/stage_card.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../support/localization.dart';
import 'trip_fixtures.dart';

/// S24-1, S24-6, S25-2 et S25-4 — les deux écrans montés en entier.
///
/// La carte se réduit à un squelette sans style servi ; ce qui se vérifie ici
/// est le reste, et surtout **ce qui n'est pas rendu** : pas de bouton
/// « Participer » sur un voyage passé ou annulé, pas de fil de commentaires sur
/// une étape — le contrat n'en expose pas — et pas de ligne d'adresse vide
/// quand `PlaceDetailDto.address` manque.
class _StubTripRepository implements TripRepository {
  _StubTripRepository(this.trip, {this.neverCompletes = false});

  final TripDto trip;
  final bool neverCompletes;

  @override
  Future<TripDto> getTrip(String teamSlug, String tripSlug) {
    if (neverCompletes) return Completer<TripDto>().future;
    return Future<TripDto>.value(trip);
  }

  @override
  Future<TripParticipationDto> joinTrip(String t, String s) async =>
      const TripParticipationDto(id: 'p', userId: 'u');

  @override
  Future<void> leaveTrip(String t, String s) async {}
}

class _StubTeamRepository implements TeamRepository {
  _StubTeamRepository({required this.member});

  final bool member;

  @override
  Future<List<TeamDetailDto>> getMyTeams() async => member
      ? <TeamDetailDto>[
          const TeamDetailDto(
            id: 't1',
            slug: 'n-peloton',
            name: 'N-Peloton',
            about: kEmptyMedia,
            visibility: 'PUBLIC',
            enableTrips: true,
            enableAds: true,
            enablePosts: true,
            enableRides: true,
            enableRoutes: true,
            visibilityEditable: true,
            joinable: true,
            addMemberAllowed: true,
            enableRoutePlanner: false,
            memberCount: 40,
            upcomingRideCount: 3,
            routeCount: 12,
            createdAt: '2024-01-01T00:00:00Z',
          ),
        ]
      : <TeamDetailDto>[];

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  setUpAll(loadTestTranslations);

  late SharedPreferences prefs;

  setUp(() async {
    SharedPreferences.setMockInitialValues(<String, Object>{});
    prefs = await SharedPreferences.getInstance();
  });

  Widget app(
    TripDto trip, {
    required Widget home,
    bool member = true,
    bool loading = false,
    Brightness brightness = Brightness.light,
  }) {
    return ProviderScope(
      overrides: [
        sharedPreferencesProvider.overrideWithValue(prefs),
        tripRepositoryProvider.overrideWithValue(
          _StubTripRepository(trip, neverCompletes: loading),
        ),
        teamRepositoryProvider.overrideWithValue(
          _StubTeamRepository(member: member),
        ),
      ],
      child: MaterialApp(theme: PedalonsTheme.build(brightness), home: home),
    );
  }

  Future<void> openTrip(
    WidgetTester tester,
    TripDto trip, {
    bool member = true,
    Brightness brightness = Brightness.light,
  }) async {
    await tester.pumpWidget(
      app(
        trip,
        member: member,
        brightness: brightness,
        home: TripDetailPage(teamSlug: trip.team.slug, tripSlug: trip.slug),
      ),
    );
    for (int i = 0; i < 4; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
  }

  Future<void> openStage(
    WidgetTester tester,
    TripDto trip,
    String stageSlug,
  ) async {
    await tester.pumpWidget(
      app(
        trip,
        home: StageDetailPage(
          teamSlug: trip.team.slug,
          tripSlug: trip.slug,
          stageSlug: stageSlug,
        ),
      ),
    );
    for (int i = 0; i < 4; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
  }

  group('écran 24 — le voyage', () {
    testWidgets('le squelette est structuré, pas un rond qui tourne', (
      WidgetTester tester,
    ) async {
      await tester.pumpWidget(
        app(
          fixtureTrip(),
          loading: true,
          home: const TripDetailPage(
            teamSlug: 'n-peloton',
            tripSlug: 'gtmc-bromance',
          ),
        ),
      );
      await tester.pump();

      expect(find.byType(PdlSkeleton), findsWidgets);
      expect(find.byType(CircularProgressIndicator), findsNothing);
    });

    testWidgets('la synthèse rend « — » et jamais « 0 » sans distance', (
      WidgetTester tester,
    ) async {
      await openTrip(
        tester,
        fixtureTrip(totalDistance: null, totalElevationGain: null),
      );

      expect(find.text('—'), findsNWidgets(2));
      expect(find.text('0 km'), findsNothing);
    });

    testWidgets('un voyage annulé porte son bandeau et aucune action', (
      WidgetTester tester,
    ) async {
      await openTrip(tester, fixtureTrip(status: 'CANCELLED'));

      // `PdlBanner` compose titre et message dans un seul `RichText`.
      expect(
        find.textContaining('Voyage annulé.', findRichText: true),
        findsOneWidget,
      );
      expect(find.text('Rejoindre'), findsNothing);
      expect(find.byType(PdlActionBar), findsNothing);
    });

    testWidgets('un voyage passé n\'offre plus de participer', (
      WidgetTester tester,
    ) async {
      await openTrip(
        tester,
        fixtureTrip(
          dateTime: '2020-08-12T07:00:00Z',
          endDate: '2020-08-18T07:00:00Z',
        ),
      );

      expect(find.byType(PdlActionBar), findsNothing);
      expect(find.text('TERMINÉ'), findsOneWidget);
    });

    testWidgets('un non-membre est renvoyé vers l\'équipe, sans action', (
      WidgetTester tester,
    ) async {
      await openTrip(tester, fixtureTrip(), member: false);

      expect(
        find.textContaining('Rejoignez cette équipe', findRichText: true),
        findsOneWidget,
      );
      expect(find.byType(PdlActionBar), findsNothing);
    });

    testWidgets('les étapes se rendent en cartes, dans l\'ordre', (
      WidgetTester tester,
    ) async {
      await openTrip(tester, fixtureTrip());
      await tester.drag(find.byType(CustomScrollView), const Offset(0, -1400));
      await tester.pump();
      await tester.pump();

      expect(find.byType(StageCard), findsWidgets);
    });
  });

  group('écran 25 — l\'étape', () {
    TripDto tripWithPlaces({String? address = 'Place de Jaude, 63000'}) =>
        fixtureTrip(
          stages: <TripStageDto>[
            fixtureStage(
              index: 1,
              stageCount: 2,
              route: fixtureStageRoute(),
              startPlaceName: 'Clermont-Ferrand',
              startAddress: address,
              endPlaceName: 'Issoire',
            ),
            fixtureStage(index: 2, stageCount: 2),
          ],
        );

    testWidgets('le rail est épinglé et porte « Aperçu » puis les étapes', (
      WidgetTester tester,
    ) async {
      await openStage(tester, tripWithPlaces(), 'j1');

      expect(find.byType(PdlStageRail), findsOneWidget);
      expect(find.text('Aperçu'), findsOneWidget);
      // `PdlBadge` capitalise ses libellés.
      expect(find.text('ÉTAPE 1 SUR 2'), findsOneWidget);
    });

    testWidgets('l\'adresse s\'affiche sous le nom du lieu', (
      WidgetTester tester,
    ) async {
      await openStage(tester, tripWithPlaces(), 'j1');

      expect(find.text('Clermont-Ferrand'), findsWidgets);
      expect(find.text('Place de Jaude, 63000'), findsOneWidget);
    });

    testWidgets('sans adresse, la ligne se réduit sans espace vide', (
      WidgetTester tester,
    ) async {
      await openStage(tester, tripWithPlaces(address: null), 'j1');

      expect(find.text('Clermont-Ferrand'), findsWidgets);
      expect(find.text('Place de Jaude, 63000'), findsNothing);
    });

    testWidgets('aucun fil de commentaires : le contrat n\'en expose pas', (
      WidgetTester tester,
    ) async {
      await openStage(tester, tripWithPlaces(), 'j1');
      await tester.drag(find.byType(CustomScrollView), const Offset(0, -1400));
      await tester.pump();
      await tester.pump();

      expect(find.byType(CommentThread), findsNothing);
      expect(find.text('Commenter ce voyage'), findsOneWidget);
    });

    testWidgets('une étape inconnue rend un état introuvable, pas un vide', (
      WidgetTester tester,
    ) async {
      await openStage(tester, tripWithPlaces(), 'j9');

      expect(find.text('Étape introuvable'), findsOneWidget);
      expect(find.text('Retour au voyage'), findsWidgets);
    });
  });
}
