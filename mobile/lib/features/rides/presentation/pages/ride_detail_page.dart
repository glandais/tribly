import 'package:easy_localization/easy_localization.dart';
// `Visibility` est à la fois un widget Flutter et un enum du contrat.
import 'package:flutter/material.dart' hide Visibility;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/utils/share_link.dart';
import '../../../../core/widgets/markdown_content.dart';
import '../../../../core/widgets/media_attachments.dart';
import '../../../comments/data/comment_repository.dart';
import '../../../comments/presentation/widgets/comment_thread.dart';
import '../../../participants/presentation/widgets/participants_sheet.dart';
import '../../../teams/providers/team_providers.dart';
import '../../providers/ride_detail_provider.dart';
import '../../providers/ride_group_selection_provider.dart';
import '../../providers/ride_registration_controller.dart';
import '../widgets/ride_elevation_section.dart';
import '../widgets/ride_groups_map.dart';
import '../widgets/ride_groups_section.dart';

/// Hauteur du hero cartographique : l'aperçu statique multi-tracés.
const double _kHeroHeight = 210;

/// L'écran 12 — détail d'une sortie et inscription à un groupe.
///
/// Réécrit intégralement. De la v1 il ne reste rien : ni l'`AlertDialog` de
/// choix de groupe, ni le bouton d'action global en bas d'écran qui ne disait
/// pas dans quel groupe il inscrivait, ni la détection d'inscription par
/// parcours de `participants[]`, ni le `SnackBar` que la barre d'onglets
/// masquait.
///
/// Huit sections, dans l'ordre : hero, identité, bandeaux, méta, carte et
/// profil, groupes, description, commentaires.
class RideDetailPage extends ConsumerStatefulWidget {
  const RideDetailPage({
    super.key,
    required this.teamSlug,
    required this.rideSlug,
    this.autoJoin = false,
  });

  final String teamSlug;
  final String rideSlug;

  /// Arrivée depuis le carrousel de l'accueil (S11-4) : l'écran tente
  /// l'inscription dès le détail chargé, **si et seulement si** il y a bien un
  /// seul groupe non plein. C'est un aller simple, pas une boîte de dialogue —
  /// l'échec retombe dans le bandeau de la section Groupes.
  final bool autoJoin;

  @override
  ConsumerState<RideDetailPage> createState() => _RideDetailPageState();
}

class _RideDetailPageState extends ConsumerState<RideDetailPage> {
  bool _autoJoinAttempted = false;

  RideKey get _key =>
      RideKey(teamSlug: widget.teamSlug, rideSlug: widget.rideSlug);

  @override
  Widget build(BuildContext context) {
    final RideRegistrationState registration = ref.watch(
      rideRegistrationProvider(_key),
    );

    return registration.ride.when(
      data: (RideDto ride) {
        _maybeAutoJoin(ride);
        return _RideDetailContent(rideKey: _key, ride: ride);
      },
      loading: () => const _RideDetailSkeleton(),
      error: (Object error, StackTrace stack) =>
          _RideDetailError(rideKey: _key, error: error),
    );
  }

  /// L'inscription automatique n'est tentée qu'une fois, et seulement quand la
  /// condition est **vérifiable** : un groupe, non plein, sortie ouverte. Le
  /// carrousel de l'accueil ne connaît pas l'identifiant du groupe (la ligne de
  /// liste ne porte pas `groups[]`), c'est donc ici que la décision se prend.
  void _maybeAutoJoin(RideDto ride) {
    if (!widget.autoJoin || _autoJoinAttempted) return;
    _autoJoinAttempted = true;
    if (ride.registered || ride.isPast || ride.isCancelled) return;
    if (ride.groups.length != 1) return;
    final RideGroupDto only = ride.groups.first;
    if (only.full) return;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        ref.read(rideRegistrationProvider(_key).notifier).join(only.id);
      }
    });
  }
}

// ─────────────────────────────────────────────────────────────── contenu

class _RideDetailContent extends ConsumerWidget {
  const _RideDetailContent({required this.rideKey, required this.ride});

  final RideKey rideKey;
  final RideDto ride;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final bool? isMember = ref.watch(teamMembershipProvider(ride.team.slug));

    // La sélection est initialisée sur *mon* groupe quand j'en ai un : c'est
    // celui qu'on veut voir en premier sur la carte comme dans le profil.
    final String? selected =
        ref.watch(selectedRideGroupProvider(rideKey)) ??
        ride.registeredGroupId ??
        (ride.groups.isEmpty ? null : ride.groups.first.id);

    void select(String id) =>
        ref.read(selectedRideGroupProvider(rideKey).notifier).state = id;

    return PdlScreenScaffold(
      slivers: <Widget>[
        SliverToBoxAdapter(child: _hero(context, ref)),
        SliverToBoxAdapter(child: _identity(context)),
        if (ride.isCancelled)
          SliverToBoxAdapter(
            child: PdlBanner(
              tone: PdlBannerTone.danger,
              icon: PdlIcons.cancelled,
              title: 'rides.cancelledTitle'.tr(),
              message: 'rides.cancelledMessage'.tr(),
              fullBleed: true,
            ),
          ),
        if (isMember == false)
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
              child: PdlBanner(
                tone: PdlBannerTone.warn,
                message: 'rides.notMemberBanner'.tr(),
                action: PdlButton(
                  label: 'teams.viewTeam'.tr(),
                  variant: PdlButtonVariant.outline,
                  size: PdlButtonSize.sm,
                  onPressed: () => context.push(Paths.team(ride.team.slug)),
                ),
              ),
            ),
          ),
        SliverToBoxAdapter(child: _meta(context)),
        SliverToBoxAdapter(
          child: _mapAndProfile(context, ref, selected, select),
        ),
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(PdlSpacing.section),
            child: RideGroupsSection(
              rideKey: rideKey,
              ride: ride,
              isMember: isMember,
              selectedGroupId: selected,
              onSelect: select,
              onShowParticipants: (RideGroupDto g) =>
                  ParticipantsSheet.open(context, g),
              onViewRoute: (RideGroupDto g) {
                final String? slug = g.routeSlug ?? ride.routeSlug;
                if (slug != null) {
                  context.push(Paths.route(ride.team.slug, slug));
                }
              },
              onViewTeam: () => context.push(Paths.team(ride.team.slug)),
            ),
          ),
        ),
        if (ride.media.markdown.trim().isNotEmpty ||
            ride.media.assets.attachments.isNotEmpty)
          SliverToBoxAdapter(child: _description(context)),
        // `commentCount` absent signifie « vous n'avez pas le droit de lire les
        // commentaires » — un non-membre n'est même pas informé qu'il y en a
        // zéro. On ne monte donc pas le fil.
        if (ride.commentCount != null)
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(PdlSpacing.section),
              child: CommentThread(
                target: CommentTarget(
                  entity: CommentEntity.ride,
                  teamSlug: ride.team.slug,
                  slug: ride.slug,
                ),
                canComment: isMember ?? false,
              ),
            ),
          ),
        const SliverToBoxAdapter(child: SizedBox(height: PdlSpacing.section)),
      ],
    );
  }

  // ── 1 · Hero ────────────────────────────────────────────────────────────
  /// L'aperçu statique multi-tracés est la **vignette servie par le serveur** :
  /// une seconde instance MapLibre au-dessus de la carte interactive coûterait
  /// une vue native de plus pour une image fixe.
  Widget _hero(BuildContext context, WidgetRef ref) {
    final String? url = Theme.of(context).brightness == Brightness.dark
        ? (ride.thumbnailDarkUrl ?? ride.thumbnailLightUrl)
        : (ride.thumbnailLightUrl ?? ride.thumbnailDarkUrl);

    return SizedBox(
      height: _kHeroHeight,
      child: Stack(
        fit: StackFit.expand,
        children: <Widget>[
          PdlCardMedia(
            tone: PdlMediaTone.ride,
            height: _kHeroHeight,
            imageUrl: url,
            icon: PdlIcons.ride,
            borderRadius: BorderRadius.zero,
          ),
          const PdlScrim(edge: PdlScrimEdge.top),
          const PdlScrim(edge: PdlScrimEdge.bottom),
          Positioned(
            top: 0,
            left: 0,
            right: 0,
            child: SafeArea(
              bottom: false,
              child: PdlAppBar(
                variant: PdlAppBarVariant.overlay,
                title: ride.name,
                onBack: () => context.pop(),
                backSemanticLabel: 'common.back'.tr(),
                actions: <Widget>[
                  PdlAppBarAction(
                    icon: PdlIcons.share,
                    semanticLabel: 'routes.share'.tr(),
                    onPressed: () => _share(context),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _share(BuildContext context) => shareAppLink(
    context,
    title: ride.name,
    path: Paths.ride(ride.team.slug, ride.slug),
  );

  // ── 2 · Identité ────────────────────────────────────────────────────────
  Widget _identity(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final RideGroupDto? mine = ride.registeredGroup;

    return Padding(
      padding: const EdgeInsets.all(PdlSpacing.section),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          // Le logo d'équipe est absent : la sortie porte un
          // `TeamPublicationDto`, qui n'a pas de `logoUrl` (§5.2-2). Repli sur
          // les initiales teintées, pas sur une image inventée.
          PdlTeamLine(
            label: ride.team.name,
            onTap: () => context.push(Paths.team(ride.team.slug)),
          ),
          const SizedBox(height: 2),
          Text(ride.name, style: t.screenTitle),
          const SizedBox(height: PdlSpacing.chipGap),
          Wrap(
            spacing: PdlSpacing.badgeGap,
            runSpacing: PdlSpacing.badgeGap,
            children: <Widget>[
              if (ride.isPast && !ride.isCancelled)
                PdlBadge(
                  label: 'rides.finished'.tr(),
                  tone: PdlDerivedTones.done(c),
                )
              else
                PdlBadge(
                  label: 'status.${ride.status.toLowerCase()}'.tr(),
                  tone: Status.fromJson(ride.status).tone(c),
                ),
              PdlBadge(
                label: 'visibility.${ride.visibility.toLowerCase()}'.tr(),
                tone: Visibility.fromJson(ride.visibility).tone(c),
                icon: _visibilityIcon(ride.visibility),
              ),
              if (mine != null)
                PdlBadge(
                  label: 'rides.registeredIn'.tr(
                    namedArgs: <String, String>{'group': mine.name},
                  ),
                  tone: PdlDerivedTones.registered(c),
                  icon: PdlIcons.check,
                ),
            ],
          ),
        ],
      ),
    );
  }

  IconData _visibilityIcon(String visibility) => switch (visibility) {
    'PUBLIC' => PdlIcons.visibilityPublic,
    'PUBLIC_UNLISTED' => PdlIcons.visibilityUnlisted,
    _ => PdlIcons.visibilityTeam,
  };

  // ── 3 · Bloc méta ───────────────────────────────────────────────────────
  Widget _meta(BuildContext context) {
    final PdlTypography t = context.pdlText;
    final DateTime? start = ride.startsAt;

    Widget cell(String label, String value, {Widget? extra}) => Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        Text(label, style: t.xs),
        const SizedBox(height: 2),
        Text(value, style: t.statBigValue),
        ?extra,
      ],
    );

    final List<Widget> cells = <Widget>[
      cell(
        'rides.dateAndTime'.tr(),
        start == null ? '—' : AppFormatters.formatLongDate(start),
        extra: start == null
            ? null
            : Text(AppFormatters.formatTime(start), style: t.xs),
      ),
      cell(
        'participants.title'.tr(),
        'rides.participants'.tr(
          namedArgs: <String, String>{'count': '${ride.participantCount}'},
        ),
        // Un `PdlButton(text)` et non un `Text` tappable : il porte la cible
        // de 44 px et la sémantique de bouton, que la maquette (un simple
        // lien bleu) ne donne pas.
        extra: ride.groups.isEmpty
            ? null
            : Align(
                alignment: Alignment.centerLeft,
                child: PdlButton(
                  label: 'rides.viewParticipants'.tr(),
                  variant: PdlButtonVariant.text,
                  size: PdlButtonSize.sm,
                  onPressed: () => ParticipantsSheet.open(
                    context,
                    ride.registeredGroup ?? ride.groups.first,
                  ),
                ),
              ),
      ),
      if (ride.startPlace != null)
        PdlPlaceRow(
          kind: PdlPlaceKind.start,
          name: ride.startPlace!.name,
          address: ride.startPlace!.address,
        ),
      if (ride.endPlace != null)
        PdlPlaceRow(
          kind: PdlPlaceKind.end,
          name: ride.endPlace!.name,
          address: ride.endPlace!.address,
        ),
    ];

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
      child: PdlCard(
        flat: true,
        child: LayoutBuilder(
          builder: (BuildContext context, BoxConstraints constraints) {
            // Grille 2 × 2 tant qu'une colonne reste lisible ; une colonne
            // au-delà, plutôt que quatre cellules écrasées.
            final bool twoColumns =
                constraints.maxWidth >= 320 &&
                MediaQuery.textScalerOf(context).scale(14) <= 18;
            if (!twoColumns) {
              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  for (int i = 0; i < cells.length; i++) ...<Widget>[
                    if (i > 0) const SizedBox(height: PdlSpacing.meta2V),
                    cells[i],
                  ],
                ],
              );
            }
            return Wrap(
              spacing: PdlSpacing.meta2H,
              runSpacing: PdlSpacing.meta2V,
              children: <Widget>[
                for (final Widget cell in cells)
                  SizedBox(
                    width:
                        (constraints.maxWidth - PdlSpacing.meta2H) / 2 -
                        PdlSpacing.card,
                    child: cell,
                  ),
              ],
            );
          },
        ),
      ),
    );
  }

  // ── 4 · Carte, légende, profil ──────────────────────────────────────────
  Widget _mapAndProfile(
    BuildContext context,
    WidgetRef ref,
    String? selected,
    ValueChanged<String> select,
  ) {
    if (ride.groups.isEmpty && ride.routeSlug == null) {
      return const SizedBox.shrink();
    }

    final PdlTypography t = context.pdlText;
    final RideGroupDto? selectedGroup = _groupById(selected);
    final String? routeSlug = selectedGroup?.routeSlug ?? ride.routeSlug;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 0, 16, 8),
          child: Text('rides.groupsRoute'.tr(), style: t.sectionTitle),
        ),
        RideGroupsMap(
          rideKey: rideKey,
          ride: ride,
          selectedGroupId: selected,
          onSelect: select,
        ),
        if (ride.groups.length > 1)
          Padding(
            padding: const EdgeInsets.only(top: 8),
            child: PdlLegendRow(
              entries: <PdlLegendEntry>[
                for (final RideGroupDto g in ride.groups)
                  PdlLegendEntry(
                    color: multiTrackColor(g.sortOrder),
                    label: g.name,
                    onTap: () => select(g.id),
                  ),
              ],
              activeIndex: ride.groups.indexWhere(
                (RideGroupDto g) => g.id == selected,
              ),
            ),
          ),
        if (routeSlug != null)
          Padding(
            padding: const EdgeInsets.all(PdlSpacing.section),
            child: RideElevationSection(
              teamSlug: ride.team.slug,
              rideSlug: ride.slug,
              routeSlug: routeSlug,
              distance: selectedGroup?.distance,
              elevationGain: selectedGroup?.elevationGain,
            ),
          ),
      ],
    );
  }

  RideGroupDto? _groupById(String? id) {
    if (id == null) return null;
    for (final RideGroupDto g in ride.groups) {
      if (g.id == id) return g;
    }
    return null;
  }

  // ── 5 · Description et pièces jointes ───────────────────────────────────
  Widget _description(BuildContext context) {
    final List<AssetDto> attachments = ride.media.assets.attachments;
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          PdlSectionHeader(title: 'rides.description'.tr()),
          if (ride.media.markdown.trim().isNotEmpty)
            MarkdownContent(
              data: ride.media.markdown,
              images: ride.media.assets.images,
            ),
          // Les lignes portaient le nom du fichier sans bouton : elles
          // annonçaient une pièce jointe sans donner de quoi l'ouvrir.
          MediaAttachments(attachments: attachments),
        ],
      ),
    );
  }
}

// ────────────────────────────────────────────────────── chargement, erreur

/// Squelette **structuré** : le hero puis quatre blocs, pas un rond qui tourne.
///
/// Le brief §5 en demande cinq sur une liste ; sur un détail, ce sont les
/// blocs réels qui se dessinent en gris, ce qui annonce la forme de la page.
class _RideDetailSkeleton extends StatelessWidget {
  const _RideDetailSkeleton();

  @override
  Widget build(BuildContext context) {
    return PdlScreenScaffold(
      appBar: PdlAppBar(
        onBack: () => context.pop(),
        backSemanticLabel: 'common.back'.tr(),
      ),
      body: ListView(
        children: <Widget>[
          const PdlSkeleton(
            height: _kHeroHeight,
            borderRadius: BorderRadius.zero,
          ),
          Padding(
            padding: const EdgeInsets.all(PdlSpacing.section),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                const PdlSkeleton(height: 12, width: 120),
                const SizedBox(height: PdlSpacing.chipGap),
                const PdlSkeleton(height: 20, width: 220),
                const SizedBox(height: 14),
                for (int i = 0; i < 3; i++) ...<Widget>[
                  if (i > 0) const SizedBox(height: PdlSpacing.feedGap),
                  const PdlSkeleton(height: 60),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _RideDetailError extends ConsumerWidget {
  const _RideDetailError({required this.rideKey, required this.error});

  final RideKey rideKey;
  final Object error;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final ApiError resolved = resolveApiError(error);
    final bool notFound = resolved.code == 'NOT_FOUND';

    return PdlScreenScaffold(
      appBar: PdlAppBar(
        onBack: () => context.pop(),
        backSemanticLabel: 'common.back'.tr(),
      ),
      body: Center(
        child: PdlEmptyState(
          variant: notFound
              ? PdlEmptyVariant.notFound
              : (resolved.isOffline
                    ? PdlEmptyVariant.error
                    : PdlEmptyVariant.error),
          icon: resolved.isOffline ? PdlIcons.offline : null,
          title: resolved.title ?? 'common.loadError'.tr(),
          message: resolved.message,
          actions: <Widget>[
            PdlButton(
              label: 'common.retry'.tr(),
              variant: PdlButtonVariant.outline,
              onPressed: () => ref.invalidate(rideDetailProvider(rideKey)),
            ),
          ],
        ),
      ),
    );
  }
}

/// Ce qu'un appelant interne passe à l'écran 12 hors de l'URL.
///
/// `autoJoin` ne doit **pas** être un paramètre de requête : un lien partagé
/// inscrirait alors son destinataire à son insu. Il voyage dans `extra`, que
/// seul un `context.push` du code peut renseigner.
class RideDetailExtra {
  const RideDetailExtra({this.autoJoin = false});

  final bool autoJoin;
}
