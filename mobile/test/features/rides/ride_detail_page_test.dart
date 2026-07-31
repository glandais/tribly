import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/pdl/pdl.dart';
import 'package:pedalons/core/preferences/user_preferences_provider.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/features/rides/data/ride_repository.dart';
import 'package:pedalons/features/rides/presentation/pages/ride_detail_page.dart';
import 'package:pedalons/features/rides/presentation/widgets/ride_group_card.dart';
import 'package:pedalons/features/teams/data/team_repository.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../support/localization.dart';
import 'ride_fixtures.dart';

/// S12-8 — l'assemblage de l'écran 12 et ses états.
///
/// L'écran est monté en entier, carte comprise : `PdlMap` se réduit à un
/// squelette tant qu'aucun style n'est résolu, ce qui est le cas sans serveur.
/// Ce qui est vérifié est le reste — les bandeaux d'état et, surtout, **ce qui
/// n'est pas rendu** quand la sortie est annulée, passée, ou que l'utilisateur
/// n'est pas membre. C'est exactement là que la v1 se trompait : elle
/// proposait « Participer » sur une sortie déjà courue.
class _StubRideRepository implements RideRepository {
  _StubRideRepository(this.ride, {this.neverCompletes = false});

  final RideDto ride;

  /// Laisse le détail en vol indéfiniment, pour observer l'état de chargement.
  final bool neverCompletes;

  @override
  Future<RideDto> getRide(String teamSlug, String rideSlug) {
    if (neverCompletes) return Completer<RideDto>().future;
    return Future<RideDto>.value(ride);
  }

  @override
  Future<RideParticipationDto> joinGroup(String t, String r, String g) async =>
      const RideParticipationDto(id: 'p', userId: 'u');

  @override
  Future<void> leaveGroup(String t, String r, String g) async {}
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
            about: MediaDto(
              markdown: '',
              assets: AssetsDto(
                images: <AssetDto>[],
                attachments: <AssetDto>[],
              ),
            ),
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
    RideDto ride, {
    required bool member,
    Brightness? brightness,
    bool loading = false,
  }) {
    return ProviderScope(
      overrides: [
        sharedPreferencesProvider.overrideWithValue(prefs),
        rideRepositoryProvider.overrideWithValue(
          _StubRideRepository(ride, neverCompletes: loading),
        ),
        teamRepositoryProvider.overrideWithValue(
          _StubTeamRepository(member: member),
        ),
      ],
      child: MaterialApp(
        theme: PedalonsTheme.build(brightness ?? Brightness.light),
        home: RideDetailPage(teamSlug: ride.team.slug, rideSlug: ride.slug),
      ),
    );
  }

  Future<void> open(
    WidgetTester tester,
    RideDto ride, {
    bool member = true,
    Brightness brightness = Brightness.light,
  }) async {
    await tester.pumpWidget(app(ride, member: member, brightness: brightness));
    // Trois paliers asynchrones s'enchaînent : le chargement des traductions,
    // le détail, puis l'appartenance à l'équipe. Chacun demande un tour de
    // boucle d'événements ; `pumpAndSettle` est exclu, le scintillement des
    // squelettes ne se stabilise jamais.
    for (int i = 0; i < 4; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
  }

  /// La section Groupes vit sous la ligne de flottaison, et un
  /// `CustomScrollView` ne construit pas ce qu'il n'affiche pas.
  Future<void> scrollToGroups(WidgetTester tester) async {
    await tester.drag(find.byType(CustomScrollView), const Offset(0, -1200));
    await tester.pump();
    await tester.pump();
  }

  testWidgets('le squelette est structuré, pas un rond qui tourne', (
    WidgetTester tester,
  ) async {
    await tester.pumpWidget(app(fixtureRide(), member: true, loading: true));
    await tester.pump();

    expect(find.byType(PdlSkeleton), findsWidgets);
    expect(find.byType(CircularProgressIndicator), findsNothing);
  });

  testWidgets('une sortie annulée porte son bandeau et aucun bouton', (
    WidgetTester tester,
  ) async {
    await open(
      tester,
      fixtureRide(
        status: 'CANCELLED',
        registered: true,
        registeredGroupId: 'g1',
        groups: <RideGroupDto>[fixtureGroup(id: 'g1', registered: true)],
      ),
    );

    // `PdlBanner` compose titre et message dans un seul `RichText`.
    expect(
      find.textContaining('Sortie annulée.', findRichText: true),
      findsOneWidget,
    );

    await scrollToGroups(tester);
    // Pas même « Quitter » : la sortie n'a plus lieu, s'en désinscrire n'a
    // plus de sens.
    expect(find.byType(RideGroupCard), findsOneWidget);
    expect(find.text('Quitter'), findsNothing);
    expect(find.text('Rejoindre'), findsNothing);
  });

  testWidgets('une sortie passée porte « Terminée » et n\'inscrit plus', (
    WidgetTester tester,
  ) async {
    await open(tester, fixtureRide(dateTime: '2020-01-01T08:00:00Z'));

    expect(find.text('TERMINÉE'), findsOneWidget);

    await scrollToGroups(tester);
    expect(find.byType(RideGroupCard), findsOneWidget);
    expect(find.text('Rejoindre'), findsNothing);
  });

  testWidgets('un non-membre voit le bandeau et aucun bouton d\'inscription', (
    WidgetTester tester,
  ) async {
    await open(tester, fixtureRide(), member: false);

    expect(
      find.textContaining(
        'Rejoignez cette équipe pour participer aux sorties.',
        findRichText: true,
      ),
      findsOneWidget,
    );

    await scrollToGroups(tester);
    expect(find.text('Rejoindre'), findsNothing);
  });

  testWidgets('un membre se voit proposer « Rejoindre »', (
    WidgetTester tester,
  ) async {
    await open(tester, fixtureRide());
    await scrollToGroups(tester);

    expect(find.text('Rejoindre'), findsOneWidget);
  });

  testWidgets('une sortie sans groupe le dit, plutôt que de ne rien rendre', (
    WidgetTester tester,
  ) async {
    await open(tester, fixtureRide(groups: <RideGroupDto>[]));
    await scrollToGroups(tester);

    expect(find.text('Aucun groupe'), findsOneWidget);
    expect(find.byType(RideGroupCard), findsNothing);
  });

  for (final Brightness brightness in Brightness.values) {
    final String mode = brightness == Brightness.light ? 'clair' : 'sombre';
    testWidgets('l\'écran se rend sans débordement — $mode', (
      WidgetTester tester,
    ) async {
      await open(tester, fixtureRide(), brightness: brightness);
      expect(tester.takeException(), isNull);
      expect(find.text('N-Peloton #665'), findsWidgets);

      await scrollToGroups(tester);
      expect(tester.takeException(), isNull);
      expect(find.text('Groupes'), findsOneWidget);
    });
  }
}
