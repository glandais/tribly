import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../auth/domain/auth_state.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../feed/presentation/widgets/publication_feed_view.dart';
import '../../providers/next_ride_provider.dart';
import '../../providers/upcoming_provider.dart';
import '../widgets/next_ride_card.dart';
import '../widgets/upcoming_carousel.dart';

/// L'accueil — « qu'est-ce que je fais à vélo cette semaine ? ».
///
/// Six slivers : barre supérieure rétractable, « Ma prochaine sortie », rangée
/// « À venir », barre d'outils épinglée, en-tête de fil et cartes, pied de
/// pagination. Les trois derniers viennent de [PublicationFeedView], que la
/// page d'équipe partage — le fil se comporte pareil des deux côtés.
///
/// Ce qui disparaît de la v1 : le dégradé de 182 pt (« Bonjour Gaby »), qui
/// coûtait la première carte du fil sans rien apprendre, la carte
/// d'incitation passkey conditionnelle, qui poussait le contenu encore plus
/// bas, et la cloche de notifications — aucun endpoint ne la sert, et le brief
/// §5 interdit une icône-action sans effet.
class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      body: PublicationFeedView(
        teamSlug: null,
        emptyMessage: 'home.feed.empty'.tr(),
        showSectionHeader: true,
        // Tirer sur la liste rafraîchit l'écran entier, pas sa seule moitié
        // basse.
        onRefreshExtras: () async {
          ref.invalidate(nextRideProvider);
          ref.invalidate(upcomingProvider);
          await Future.wait<void>(<Future<void>>[
            ref.read(nextRideProvider.future),
            ref.read(upcomingProvider.future),
          ]);
        },
        leadingSlivers: <Widget>[
          const _HomeAppBar(),
          const SliverToBoxAdapter(child: _NextRideSliver()),
          const SliverToBoxAdapter(child: _UpcomingSliver()),
        ],
      ),
    );
  }
}

/// La barre supérieure : 56 px, wordmark et avatar.
///
/// `pinned, floating, snap` — arbitrage du §1.0.4 en faveur du brief contre la
/// maquette : rendre 56 px au défilement ramène la cinquième carte à l'écran,
/// et c'est la **barre d'outils** qui doit rester, pas celle-ci.
class _HomeAppBar extends ConsumerWidget {
  const _HomeAppBar();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final UserDto? user = ref.watch(
      authProvider.select((AuthState s) => s.user),
    );

    return SliverAppBar(
      pinned: true,
      floating: true,
      snap: true,
      toolbarHeight: PdlMetrics.appBar,
      backgroundColor: c.surface,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      scrolledUnderElevation: 0,
      titleSpacing: PdlSpacing.section,
      title: Text('app.name'.tr(), style: t.wordmark),
      actions: <Widget>[
        if (user != null)
          Padding(
            padding: const EdgeInsets.only(right: PdlSpacing.section),
            child: Semantics(
              button: true,
              label: 'nav.profile'.tr(),
              child: GestureDetector(
                onTap: () => context.go(Paths.profile()),
                child: PdlAvatar(
                  name: user.displayName,
                  imageUrl: user.avatarUrl,
                  size: 32,
                  isCurrentUser: true,
                ),
              ),
            ),
          ),
      ],
      bottom: PreferredSize(
        preferredSize: const Size.fromHeight(1),
        child: Container(height: 1, color: c.borderSubtle),
      ),
    );
  }
}

/// « Ma prochaine sortie », ou la carte compacte qui la remplace.
///
/// **Un échec masque le bloc sans rien afficher** : c'est un enrichissement,
/// il n'a pas le droit de casser l'accueil.
class _NextRideSliver extends ConsumerWidget {
  const _NextRideSliver();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AsyncValue<NextRide?> next = ref.watch(nextRideProvider);

    return next.when(
      loading: () => const Padding(
        padding: EdgeInsets.all(PdlSpacing.section),
        child: PdlSkeletonCard(),
      ),
      error: (Object error, StackTrace stack) => const SizedBox.shrink(),
      data: (NextRide? value) => Padding(
        padding: const EdgeInsets.all(PdlSpacing.section),
        child: value == null
            ? NoNextRideCard(onExplore: () => context.go(Paths.allRoutes()))
            : NextRideCard(next: value),
      ),
    );
  }
}

class _UpcomingSliver extends ConsumerWidget {
  const _UpcomingSliver();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return ref
        .watch(upcomingProvider)
        .maybeWhen(
          data: (List<PublicationDto> items) => UpcomingCarousel(items: items),
          orElse: () => const SizedBox.shrink(),
        );
  }
}
