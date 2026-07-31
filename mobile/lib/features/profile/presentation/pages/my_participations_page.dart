import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pagination/pagination.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../teams/presentation/widgets/publication_card.dart';
import '../../providers/participations_provider.dart';

/// Les sorties et voyages auxquels on est inscrit — à venir, ou passés.
///
/// Deux jeux et non deux écrans : la bascule est un segmenté, et chaque jeu a
/// sa liste paginée et son compteur, tous deux issus du **`total`** que
/// l'endpoint rend déjà.
class MyParticipationsPage extends ConsumerStatefulWidget {
  const MyParticipationsPage({super.key, this.initialUpcoming = true});

  /// Onglet d'ouverture. Il vient de la ligne qu'on a touchée sur le profil,
  /// dans l'`extra` de la route : ouvrir « Historique » et atterrir sur « à
  /// venir » serait ignorer le geste.
  final bool initialUpcoming;

  @override
  ConsumerState<MyParticipationsPage> createState() =>
      _MyParticipationsPageState();
}

class _MyParticipationsPageState extends ConsumerState<MyParticipationsPage> {
  late bool _upcoming = widget.initialUpcoming;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PagedListState<PublicationDto> state = ref.watch(
      participationsProvider(_upcoming),
    );
    final ParticipationsNotifier notifier = ref.read(
      participationsProvider(_upcoming).notifier,
    );

    return Scaffold(
      backgroundColor: c.bg,
      appBar: PdlAppBar(
        title: 'profile.participations.title'.tr(),
        onBack: () => context.pop(),
        backSemanticLabel: 'profile.title'.tr(),
      ),
      body: PdlRefresh(
        onRefresh: notifier.refresh,
        child: CustomScrollView(
          primary: true,
          slivers: <Widget>[
            PdlPinnedToolbar(
              child: Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: PdlSpacing.section,
                ),
                child: PdlSegmented<bool>(
                  value: _upcoming,
                  onChanged: (bool value) => setState(() => _upcoming = value),
                  segments: <PdlSegment<bool>>[
                    PdlSegment<bool>(
                      value: true,
                      label: 'profile.participations.upcoming'.tr(),
                    ),
                    PdlSegment<bool>(
                      value: false,
                      label: 'profile.participations.past'.tr(),
                    ),
                  ],
                ),
              ),
            ),
            ..._content(state, notifier),
            const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
          ],
        ),
      ),
    );
  }

  List<Widget> _content(
    PagedListState<PublicationDto> state,
    ParticipationsNotifier notifier,
  ) {
    const EdgeInsets padding = EdgeInsets.fromLTRB(
      PdlSpacing.section,
      PdlSpacing.chipGap,
      PdlSpacing.section,
      0,
    );

    if (state.showsSkeletons) {
      return <Widget>[
        const SliverPadding(
          padding: padding,
          sliver: SliverToBoxAdapter(
            child: PdlSkeletonCardList(
              variant: PdlSkeletonCardVariant.media,
              count: 5,
            ),
          ),
        ),
      ];
    }

    if (state.initialError != null) {
      return <Widget>[
        SliverFillRemaining(
          hasScrollBody: false,
          child: Center(
            child: PdlEmptyState(
              variant: PdlEmptyVariant.error,
              title: 'common.loadError'.tr(),
              message: getErrorMessage(state.initialError!),
              actions: <Widget>[
                PdlButton(
                  label: 'common.retry'.tr(),
                  variant: PdlButtonVariant.outline,
                  size: PdlButtonSize.sm,
                  onPressed: notifier.loadFirstPage,
                ),
              ],
            ),
          ),
        ),
      ];
    }

    if (state.isEmpty) {
      return <Widget>[
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 40),
            child: PdlEmptyState(
              variant: PdlEmptyVariant.empty,
              icon: Icons.directions_bike,
              title: _upcoming
                  ? 'profile.participations.emptyUpcoming'.tr()
                  : 'profile.participations.emptyPast'.tr(),
              message: _upcoming
                  ? 'profile.participations.emptyUpcomingHint'.tr()
                  : 'profile.participations.emptyPastHint'.tr(),
            ),
          ),
        ),
      ];
    }

    final int total = state.total ?? state.items.length;

    return <Widget>[
      SliverToBoxAdapter(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(
            PdlSpacing.section,
            PdlSpacing.chipGap,
            PdlSpacing.section,
            4,
          ),
          child: Text(
            'profile.participations.count'.plural(total),
            style: context.pdlText.count,
          ),
        ),
      ),
      SliverPadding(
        padding: padding,
        sliver: SliverList.separated(
          itemCount: state.items.length,
          separatorBuilder: (_, _) =>
              const SizedBox(height: PdlSpacing.feedGap),
          itemBuilder: (BuildContext context, int index) {
            notifier.onItemBuilt(index);
            return PublicationCard(publication: state.items[index]);
          },
        ),
      ),
      SliverPadding(
        padding: const EdgeInsets.symmetric(horizontal: PdlSpacing.section),
        sliver: SliverToBoxAdapter(
          child: PdlPagedListFooter(
            isLoadingNext: state.isLoadingNext,
            hasMore: state.hasMore,
            isEmpty: state.items.isEmpty,
            hasError: state.nextError != null,
            onRetry: notifier.retryNextPage,
            progressLabel: 'pagination.progress'.tr(
              namedArgs: <String, String>{
                'loaded': '${state.items.length}',
                'total': '$total',
              },
            ),
            loadingLabel: 'pagination.loadingMore'.tr(),
            endLabel: 'pagination.endOfList'.tr(),
            errorLabel: 'pagination.nextPageError'.tr(),
            retryLabel: 'common.retry'.tr(),
          ),
        ),
      ),
    ];
  }
}
