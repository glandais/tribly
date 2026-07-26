import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/pagination/pagination.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../teams/presentation/widgets/publication_card.dart';
import '../../providers/publication_feed_provider.dart';

/// A publication feed: search, type chips, infinite scroll, pull-to-refresh
/// and the four list states.
///
/// Shared by the home feed and a team's feed so both behave identically —
/// only the slivers above the toolbar differ.
class PublicationFeedView extends ConsumerStatefulWidget {
  /// Team to show the feed of, or null for the cross-team home feed.
  final String? teamSlug;

  /// Slivers rendered above the pinned toolbar (app bar, prompts…).
  final List<Widget> leadingSlivers;

  /// Ce que le pull-to-refresh rafraîchit **en plus** du fil.
  ///
  /// L'accueil y met « Ma prochaine sortie » et « À venir » : tirer sur la
  /// liste doit rafraîchir tout l'écran, pas seulement sa moitié basse.
  final Future<void> Function()? onRefreshExtras;

  /// Message shown when the feed has nothing at all.
  final String emptyMessage;

  /// En-tête de section posé juste au-dessus des cartes — « Dernières
  /// publications · 1 248 publications » sur l'accueil.
  final bool showSectionHeader;

  const PublicationFeedView({
    super.key,
    required this.teamSlug,
    required this.emptyMessage,
    this.leadingSlivers = const [],
    this.showSectionHeader = false,
    this.onRefreshExtras,
  });

  @override
  ConsumerState<PublicationFeedView> createState() =>
      _PublicationFeedViewState();
}

class _PublicationFeedViewState extends ConsumerState<PublicationFeedView> {
  /// Scrolls back to the top through the route's primary controller.
  ///
  /// The feed deliberately declares no `controller:` of its own: that is what
  /// keeps it the primary scrollable of its route, which is what makes an iOS
  /// status-bar tap scroll it to the top.
  void _scrollToTop() {
    final controller = PrimaryScrollController.maybeOf(context);
    if (controller != null && controller.hasClients) controller.jumpTo(0);
  }

  void _setType(PublicationType? value) {
    ref.read(publicationFeedTypeProvider(widget.teamSlug).notifier).state =
        value;
  }

  void _setSearch(String? value) {
    ref.read(publicationFeedSearchProvider(widget.teamSlug).notifier).state =
        value;
  }

  void _setScope(MinRole? value) {
    ref.read(publicationFeedScopeProvider(widget.teamSlug).notifier).state =
        value;
  }

  @override
  Widget build(BuildContext context) {
    // Switching filter is a new result set: back to the top of the list.
    ref.listen(publicationFeedTypeProvider(widget.teamSlug), (previous, next) {
      if (previous != next) _scrollToTop();
    });
    ref.listen(publicationFeedSearchProvider(widget.teamSlug), (
      previous,
      next,
    ) {
      if (previous != next) _scrollToTop();
    });
    ref.listen(publicationFeedScopeProvider(widget.teamSlug), (previous, next) {
      if (previous != next) _scrollToTop();
    });

    final type = ref.watch(publicationFeedTypeProvider(widget.teamSlug));
    final search = ref.watch(publicationFeedSearchProvider(widget.teamSlug));
    // La portée n'existe que sur le fil d'accueil : un fil d'équipe *est* déjà
    // une portée.
    final minRole = widget.teamSlug == null
        ? ref.watch(publicationFeedScopeProvider(widget.teamSlug))
        : null;
    final key = (
      teamSlug: widget.teamSlug,
      type: type,
      search: search,
      minRole: minRole,
    );
    final state = ref.watch(publicationFeedProvider(key));
    final notifier = ref.read(publicationFeedProvider(key).notifier);

    return RefreshIndicator(
      onRefresh: () async {
        await Future.wait<void>(<Future<void>>[
          notifier.refresh(),
          if (widget.onRefreshExtras != null) widget.onRefreshExtras!(),
        ]);
      },
      child: CustomScrollView(
        // Explicitly the route's primary scrollable, with no controller of its
        // own: an iOS status-bar tap scrolls it back to the top, and pull-to-
        // refresh still works when the list is too short to scroll.
        primary: true,
        slivers: [
          ...widget.leadingSlivers,
          // F-DE-4 : la rangée de filtres vivait dans un `SliverToBoxAdapter`.
          // Elle sortait de l'écran au premier défilement et n'y revenait
          // qu'en remontant tout le fil : au 40ᵉ élément, plus moyen de savoir
          // ni de changer ce qu'on regardait. `PdlPinnedToolbar` l'épingle,
          // comme `routes_page` le fait déjà, et le champ de recherche — qui
          // manquait au fil — y monte avec elle.
          PdlPinnedToolbar(
            padding: EdgeInsets.zero,
            child: FeedToolbar(
              search: search,
              selectedType: type,
              selectedScope: minRole,
              showScope: widget.teamSlug == null,
              onSearchChanged: _setSearch,
              onTypeSelected: _setType,
              onScopeSelected: _setScope,
            ),
          ),
          if (widget.showSectionHeader)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
                child: PdlSectionHeader(
                  title: 'home.latestPublications'.tr(),
                  count: ref
                      .watch(publicationFeedCountProvider(key))
                      .maybeWhen(
                        data: (int total) =>
                            'home.publicationCount'.plural(total),
                        orElse: () => null,
                      ),
                  padding: EdgeInsets.zero,
                ),
              ),
            ),
          ..._buildContentSlivers(context, state, notifier, search),
          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );
  }

  List<Widget> _buildContentSlivers(
    BuildContext context,
    PagedListState<PublicationDto> state,
    PublicationFeedNotifier notifier,
    String? search,
  ) {
    if (state.showsSkeletons) {
      return [
        SliverPadding(
          padding: const EdgeInsets.all(16),
          sliver: SliverToBoxAdapter(
            child: ContentWidthConstraint(
              // Cinq squelettes, pas deux (§1.0.4) : deux ressemblent à une
              // fin de liste.
              child: const PdlSkeletonCardList(),
            ),
          ),
        ),
      ];
    }

    if (state.initialError != null) {
      return [
        SliverToBoxAdapter(
          child: ContentWidthConstraint(
            padding: const EdgeInsets.all(16),
            // §1.3.4 : un échec est un état, pas un événement. Il reste à
            // l'écran, dans le flux, et il nomme ce qui a échoué — là où un
            // `SnackBar` de 4 s passait derrière la barre d'onglets.
            child: PdlBanner(
              tone: PdlBannerTone.danger,
              title: 'home.feed.errorTitle'.tr(),
              message: getErrorMessage(state.initialError!),
              action: PdlButton(
                label: 'common.retry'.tr(),
                variant: PdlButtonVariant.text,
                size: PdlButtonSize.sm,
                onPressed: notifier.loadFirstPage,
              ),
            ),
          ),
        ),
      ];
    }

    if (state.isEmpty) {
      // F-DE-9 : « rien ici » et « rien qui corresponde » ne disent pas la
      // même chose et n'appellent pas la même action. Le vide filtré offre la
      // sortie ; le vide absolu n'a rien à proposer et ne fait pas semblant.
      final bool filtered = (search != null && search.isNotEmpty);
      return [
        SliverFillRemaining(
          hasScrollBody: false,
          child: Center(
            child: filtered
                ? PdlEmptyState(
                    variant: PdlEmptyVariant.filtered,
                    icon: Icons.dynamic_feed,
                    title: 'home.feed.emptyFiltered.title'.tr(),
                    message: 'home.feed.emptyFiltered.message'.tr(
                      namedArgs: {'search': search},
                    ),
                    actions: [
                      PdlButton(
                        label: 'common.clearSearch'.tr(),
                        variant: PdlButtonVariant.outline,
                        size: PdlButtonSize.sm,
                        onPressed: () => _setSearch(null),
                      ),
                    ],
                  )
                : PdlEmptyState(
                    variant: PdlEmptyVariant.empty,
                    icon: Icons.dynamic_feed,
                    title: widget.emptyMessage,
                    message: 'home.feed.emptyHint'.tr(),
                  ),
          ),
        ),
      ];
    }

    return [
      SliverPadding(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
        sliver: SliverList.separated(
          itemCount: state.items.length,
          separatorBuilder: (context, index) => const SizedBox(height: 8),
          itemBuilder: (context, index) {
            notifier.onItemBuilt(index);
            return ContentWidthConstraint(
              child: PublicationCard(publication: state.items[index]),
            );
          },
        ),
      ),
      SliverPadding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        sliver: SliverToBoxAdapter(
          child: ContentWidthConstraint(
            child: PagedListFooter(
              state: state,
              onRetry: notifier.retryNextPage,
              skeleton: const PdlSkeletonCard(),
            ),
          ),
        ),
      ),
    ];
  }
}

/// Le contenu de la barre épinglée : recherche puis chips de type.
///
/// Aucune hauteur n'y est écrite. C'est la contrepartie de F-DE-3 côté barre :
/// [PdlPinnedToolbar] mesure ce qu'on lui donne, à condition qu'on ne lui
/// donne rien de figé.
class FeedToolbar extends StatelessWidget {
  final String? search;
  final PublicationType? selectedType;

  /// Portée courante ; ignorée quand [showScope] est faux.
  final MinRole? selectedScope;
  final bool showScope;

  final ValueChanged<String?> onSearchChanged;
  final ValueChanged<PublicationType?> onTypeSelected;
  final ValueChanged<MinRole?> onScopeSelected;

  const FeedToolbar({
    super.key,
    required this.search,
    required this.selectedType,
    required this.onSearchChanged,
    required this.onTypeSelected,
    required this.onScopeSelected,
    this.selectedScope,
    this.showScope = false,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        ContentWidthConstraint(
          padding: const EdgeInsets.fromLTRB(16, 10, 16, 10),
          child: PdlSearchField(
            value: search,
            hintText: 'home.feed.searchPlaceholder'.tr(),
            clearTooltip: 'common.clearSearch'.tr(),
            onChanged: onSearchChanged,
          ),
        ),
        _FilterChips(
          selectedType: selectedType,
          selectedScope: selectedScope,
          showScope: showScope,
          onTypeSelected: onTypeSelected,
          onScopeSelected: onScopeSelected,
        ),
        const SizedBox(height: 10),
      ],
    );
  }
}

class _FilterChips extends StatelessWidget {
  final PublicationType? selectedType;
  final MinRole? selectedScope;
  final bool showScope;
  final ValueChanged<PublicationType?> onTypeSelected;
  final ValueChanged<MinRole?> onScopeSelected;

  const _FilterChips({
    required this.selectedType,
    required this.selectedScope,
    required this.showScope,
    required this.onTypeSelected,
    required this.onScopeSelected,
  });

  PublicationType? get selected => selectedType;
  ValueChanged<PublicationType?> get onSelected => onTypeSelected;

  @override
  Widget build(BuildContext context) {
    // F-DE-3 : un `SingleChildScrollView` nu coupait la 4ᵉ chip net sur le
    // bord droit, sans rien qui signale qu'il en restait — une chip tronquée
    // se lit comme une chip abîmée. `PdlChipRow` fond les 28 derniers pixels
    // et mesure sa hauteur au lieu de la figer.
    return PdlChipRow(
      children: [
        // La portée d'abord, en chip de tri : elle décide de *quoi* on parle
        // avant que le type ne décide *de quel type*.
        if (showScope)
          PdlChip(
            label: _scopeLabel(selectedScope),
            icon: Icons.tune,
            sortStyle: true,
            selected: selectedScope != null,
            onTap: () => _openScopeSheet(context),
          ),
        _chip(label: 'home.feed.all'.tr(), value: null),
        _chip(
          label: 'rides.title'.tr(),
          icon: Icons.directions_bike,
          value: PublicationType.ride,
        ),
        _chip(
          label: 'posts.title'.tr(),
          icon: Icons.article,
          value: PublicationType.post,
        ),
        _chip(
          label: 'trips.title'.tr(),
          icon: Icons.hiking,
          value: PublicationType.trip,
        ),
      ],
    );
  }

  /// Le libellé de la chip de portée : « Toutes les équipes » par défaut, le
  /// rôle minimum sinon.
  String _scopeLabel(MinRole? scope) => switch (scope) {
    MinRole.member => 'roles.member'.tr(),
    MinRole.organizer => 'roles.organizer'.tr(),
    MinRole.admin => 'roles.admin'.tr(),
    _ => 'home.scopeAllTeams'.tr(),
  };

  /// La portée s'ouvre en feuille plutôt qu'en quatre chips : elle est
  /// exclusive, et quatre chips de plus repousseraient les types hors écran.
  Future<void> _openScopeSheet(BuildContext context) async {
    const List<MinRole?> options = <MinRole?>[
      null,
      MinRole.member,
      MinRole.organizer,
      MinRole.admin,
    ];
    final int? picked = await PdlSheet.show<int>(
      context: context,
      builder: (BuildContext sheetContext) => PdlSheet(
        title: 'home.scope'.tr(),
        children: <Widget>[
          for (int i = 0; i < options.length; i++)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: PdlSettingRow(
                title: _scopeLabel(options[i]),
                trailing: options[i] == selectedScope
                    ? Icon(Icons.check, color: context.pdl.primary)
                    : null,
                onTap: () => Navigator.of(sheetContext).pop(i),
              ),
            ),
        ],
      ),
    );
    if (picked != null) onScopeSelected(options[picked]);
  }

  Widget _chip({
    required String label,
    required PublicationType? value,
    IconData? icon,
  }) {
    return PdlChip(
      label: label,
      icon: icon,
      selected: selected == value,
      onTap: () => onSelected(value),
    );
  }
}
