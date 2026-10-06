import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/config/paths.dart';
import 'package:pedalons/config/router.dart';
import 'package:pedalons/core/pagination/pagination.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/core/utils/link_launcher.dart';
import 'package:pedalons/features/feed/presentation/widgets/publication_feed_view.dart';
import 'package:pedalons/features/feed/providers/publication_feed_provider.dart';
import 'package:pedalons/features/tags/providers/team_tags_provider.dart';
import 'package:pedalons/features/teams/presentation/pages/team_home_page.dart';
import 'package:pedalons/features/teams/presentation/widgets/team_sections.dart';
import 'package:pedalons/keys.dart';

import '../../support/localization.dart';

/// Ledger `WEB-64` — les onglets « Sorties » et « Voyages » du site
/// (`/equipes/{slug}/sorties`, `/equipes/{slug}/voyages`) tombent sous les
/// motifs de lien profond de l'équipe (`/equipes/*`, `/equipes/.*`). L'app
/// doit donc les ouvrir — sur le fil de l'équipe filtré par type — et non sur
/// sa page d'erreur.
void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  setUpAll(loadTestTranslations);

  late GoRouter router;

  setUpAll(() {
    final ProviderContainer container = ProviderContainer();
    addTearDown(container.dispose);
    router = container.read(routerProvider);
  });

  final Map<String, (Map<String, String>, PublicationType)>
  tabs = <String, (Map<String, String>, PublicationType)>{
    'teamRides': (PathVariants.teamRides('velo-club'), PublicationType.ride),
    'teamTrips': (PathVariants.teamTrips('velo-club'), PublicationType.trip),
  };

  tabs.forEach((String id, (Map<String, String>, PublicationType) tab) {
    final (Map<String, String> variants, PublicationType type) = tab;
    for (final MapEntry<String, String> entry in variants.entries) {
      final String path = entry.value;

      test('$id [${entry.key}] $path reste dans l\'app', () {
        expect(internalLocationFor('https://www.pedalons.fr$path'), path);
        expect(ancestorsForDeepLink(path), <String>[
          PathVariants.teams()[entry.key]!,
          PathVariants.team('velo-club')[entry.key]!,
        ]);
      });

      testWidgets('$id [${entry.key}] $path ouvre le fil filtré', (
        WidgetTester tester,
      ) async {
        final RouteMatchList matches = router.configuration.findMatch(
          Uri.parse(path),
        );
        expect(matches.isError, isFalse, reason: 'aucune route pour $path');

        late BuildContext context;
        await tester.pumpWidget(
          Builder(
            builder: (BuildContext c) {
              context = c;
              return const SizedBox();
            },
          ),
        );

        final RouteMatch leaf = matches.last;
        final GoRoute route = leaf.route;
        final Page<dynamic> page = route.pageBuilder!(
          context,
          leaf.buildState(
            router.configuration,
            matches,
            metadata: const <String, dynamic>{},
          ),
        );
        final TeamHomePage home =
            (page as NoTransitionPage<dynamic>).child as TeamHomePage;
        expect(home.teamSlug, 'velo-club');
        expect(home.section, TeamSectionKind.feed);
        expect(home.feedType, type);
      });
    }
  });

  testWidgets('initialType : la première page demandée est déjà filtrée, '
      'puis les chips restent libres', (WidgetTester tester) async {
    final List<PublicationFeedKey> requested = <PublicationFeedKey>[];
    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          // Aucun tag : pas d'appel réseau pour le vocabulaire du type.
          teamTagsProvider.overrideWith(
            (ref, key) async => const <TagWithUsageDto>[],
          ),
          publicationFeedProvider.overrideWith((ref, key) {
            requested.add(key);
            return _StuckFeedNotifier(key);
          }),
        ],
        child: MaterialApp(
          theme: PedalonsTheme.build(Brightness.light),
          home: const Scaffold(
            body: PublicationFeedView(
              teamSlug: 'velo-club',
              emptyMessage: 'empty',
              initialType: PublicationType.trip,
            ),
          ),
        ),
      ),
    );
    await tester.pump();
    await tester.pump();

    expect(requested, isNotEmpty);
    expect(
      requested.map((PublicationFeedKey k) => k.type).toSet(),
      <PublicationType?>{PublicationType.trip},
      reason: 'aucune page « Tout » demandée puis jetée',
    );

    // Point de départ, pas filtre imposé : les chips restent libres.
    await tester.ensureVisible(
      find.byKey(keys.feed.typeChip(PublicationType.ride)),
    );
    await tester.tap(find.byKey(keys.feed.typeChip(PublicationType.ride)));
    await tester.pump();
    await tester.pump();
    expect(requested.last.type, PublicationType.ride);

    // Laisse s'éteindre les minuteries du fil (squelettes, autoDispose).
    await tester.pumpWidget(const SizedBox());
    await tester.pump(const Duration(seconds: 1));
  });

  testWidgets(
    'initialType ne fuit pas vers le fil de l\'équipe empilé dessous',
    (WidgetTester tester) async {
      // Lien profond froid vers /equipes/x/sorties : `ancestorsForDeepLink`
      // empile le fil de l'équipe (même `teamSlug`, donc mêmes providers de
      // filtre) sous le fil filtré.
      final GlobalKey<NavigatorState> navigator = GlobalKey<NavigatorState>();
      await tester.pumpWidget(
        ProviderScope(
          overrides: [
            teamTagsProvider.overrideWith(
              (ref, key) async => const <TagWithUsageDto>[],
            ),
            publicationFeedProvider.overrideWith(
              (ref, key) => _StuckFeedNotifier(key),
            ),
          ],
          child: MaterialApp(
            navigatorKey: navigator,
            theme: PedalonsTheme.build(Brightness.light),
            home: const Scaffold(
              body: PublicationFeedView(
                teamSlug: 'velo-club',
                emptyMessage: 'empty',
              ),
            ),
          ),
        ),
      );
      unawaited(
        navigator.currentState!.push(
          MaterialPageRoute<void>(
            builder: (BuildContext context) => const Scaffold(
              body: PublicationFeedView(
                teamSlug: 'velo-club',
                emptyMessage: 'empty',
                initialType: PublicationType.ride,
              ),
            ),
          ),
        ),
      );
      await tester.pump();
      await tester.pump(const Duration(seconds: 1));
      expect(
        tester.widget<FeedToolbar>(find.byType(FeedToolbar)).selectedType,
        PublicationType.ride,
      );

      navigator.currentState!.pop();
      await tester.pump();
      await tester.pump(const Duration(seconds: 1));

      expect(
        tester.widget<FeedToolbar>(find.byType(FeedToolbar)).selectedType,
        isNull,
        reason: 'le fil de l\'équipe dessous est resté sur « Tout »',
      );

      await tester.pumpWidget(const SizedBox());
      await tester.pump(const Duration(seconds: 1));
    },
  );

  testWidgets(
    'choisir un tag sur le fil filtré rejoint l\'état partagé, type compris',
    (WidgetTester tester) async {
      // Les tags sont partagés par équipe : écrits seuls, des tags de sortie
      // se colleraient au type « Voyages » du fil empilé dessous.
      final ProviderContainer container = ProviderContainer(
        overrides: [
          teamTagsProvider.overrideWith(
            (ref, key) async => const <TagWithUsageDto>[],
          ),
          publicationFeedProvider.overrideWith(
            (ref, key) => _StuckFeedNotifier(key),
          ),
        ],
      );
      addTearDown(container.dispose);
      container.read(publicationFeedTypeProvider('velo-club').notifier).state =
          PublicationType.trip;
      await tester.pumpWidget(
        UncontrolledProviderScope(
          container: container,
          child: MaterialApp(
            theme: PedalonsTheme.build(Brightness.light),
            home: const Scaffold(
              body: PublicationFeedView(
                teamSlug: 'velo-club',
                emptyMessage: 'empty',
                initialType: PublicationType.ride,
              ),
            ),
          ),
        ),
      );
      await tester.pump();

      tester.widget<FeedToolbar>(find.byType(FeedToolbar)).onTagsChanged!(
        <String>['tag-sortie'],
      );
      await tester.pump();

      expect(
        container.read(publicationFeedTypeProvider('velo-club')),
        PublicationType.ride,
      );
      expect(container.read(publicationFeedTagsProvider('velo-club')), <String>[
        'tag-sortie',
      ]);
      expect(
        tester.widget<FeedToolbar>(find.byType(FeedToolbar)).selectedType,
        PublicationType.ride,
      );

      await tester.pumpWidget(const SizedBox());
      await tester.pump(const Duration(seconds: 1));
    },
  );
}

/// Ne répond jamais : aucun appel HTTP, le fil reste sur ses squelettes.
class _StuckFeedNotifier extends PublicationFeedNotifier {
  _StuckFeedNotifier(PublicationFeedKey key)
    : super(PublicationsClient(Dio()), key);

  @override
  Future<PageResult<PublicationDto>> fetchPage(int page) =>
      Completer<PageResult<PublicationDto>>().future;
}
