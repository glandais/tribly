import 'package:easy_localization/easy_localization.dart';
// `Visibility` est à la fois un widget Flutter et un enum du contrat.
import 'package:flutter/material.dart' hide Visibility;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:share_plus/share_plus.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../calendar/presentation/widgets/calendar_subscription_card.dart';
import '../../../teams/providers/team_providers.dart';
import '../../providers/trip_detail_provider.dart';
import '../../providers/trip_stage_selection_provider.dart';
import '../widgets/stage_card.dart';
import '../widgets/trip_summary_card.dart';

/// Hauteur du hero : la vignette statique du tracé global.
const double _kTripHeroHeight = 210;

/// L'écran 24 — le voyage.
///
/// De la v1 il reste la liste d'étapes et la description ; tout le reste est
/// neuf. La v1 n'avait ni carte, ni distance totale, ni dénivelé cumulé, ni
/// date de fin, ni profil, ni commentaires — un voyage de sept jours y était
/// une date de départ suivie de sept lignes.
class TripDetailPage extends ConsumerWidget {
  const TripDetailPage({
    super.key,
    required this.teamSlug,
    required this.tripSlug,
  });

  final String teamSlug;
  final String tripSlug;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final TripKey key = TripKey(teamSlug: teamSlug, tripSlug: tripSlug);

    return ref
        .watch(tripDetailProvider(key))
        .when(
          data: (TripDto trip) => _TripDetailContent(tripKey: key, trip: trip),
          loading: () => const _TripDetailSkeleton(),
          error: (Object error, StackTrace stack) =>
              _TripDetailError(tripKey: key, error: error),
        );
  }
}

// ─────────────────────────────────────────────────────────────── contenu

class _TripDetailContent extends ConsumerWidget {
  const _TripDetailContent({required this.tripKey, required this.trip});

  final TripKey tripKey;
  final TripDto trip;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final bool? isMember = ref.watch(teamMembershipProvider(trip.team.slug));
    final List<TripStageDto> stages = trip.orderedStages;
    final String? selected = ref.watch(selectedStageProvider(tripKey));

    void select(String id) =>
        ref.read(selectedStageProvider(tripKey).notifier).state = id;

    return PdlScreenScaffold(
      slivers: <Widget>[
        SliverToBoxAdapter(child: _hero(context, isMember)),
        SliverToBoxAdapter(child: _identity(context)),
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
            child: TripSummaryCard(
              trip: trip,
              onShowParticipants: () => _showParticipants(context),
            ),
          ),
        ),
        SliverToBoxAdapter(child: _stages(context, stages, selected, select)),
        if (trip.media.markdown.trim().isNotEmpty)
          SliverToBoxAdapter(child: _description(context)),
        const SliverToBoxAdapter(child: SizedBox(height: PdlSpacing.section)),
      ],
    );
  }

  // ── 1 · Hero ────────────────────────────────────────────────────────────
  /// La vignette servie par le serveur, sous deux voiles. Aucun texte n'est
  /// posé sur la tuile sans voile (F-DE-1) : le titre de la barre est blanc sur
  /// le voile haut, pas sur l'image.
  Widget _hero(BuildContext context, bool? isMember) {
    final String? url = Theme.of(context).brightness == Brightness.dark
        ? (trip.thumbnailDarkUrl ?? trip.thumbnailLightUrl)
        : (trip.thumbnailLightUrl ?? trip.thumbnailDarkUrl);

    return SizedBox(
      height: _kTripHeroHeight,
      child: Stack(
        fit: StackFit.expand,
        children: <Widget>[
          PdlCardMedia(
            tone: PdlMediaTone.trip,
            height: _kTripHeroHeight,
            imageUrl: url,
            icon: PdlIcons.trip,
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
                title: trip.name,
                onBack: () => context.pop(),
                backSemanticLabel: 'common.back'.tr(),
                actions: <Widget>[
                  // Aucun endpoint ICS par publication (§5.2) : « Ajouter à mon
                  // calendrier » ouvre l'abonnement d'équipe de l'écran 22, qui
                  // porte le voyage parmi ses événements. Un non-membre n'a pas
                  // ce calendrier — le bouton disparaît plutôt que d'échouer.
                  if (isMember == true)
                    PdlAppBarAction(
                      icon: PdlIcons.date,
                      variant: PdlAppBarVariant.overlay,
                      semanticLabel: 'trips.addToCalendar'.tr(),
                      onPressed: () => _openCalendarSubscription(context),
                    ),
                  PdlAppBarAction(
                    icon: PdlIcons.share,
                    variant: PdlAppBarVariant.overlay,
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

  Future<void> _share(BuildContext context) async {
    final RenderBox? box = context.findRenderObject() as RenderBox?;
    await SharePlus.instance.share(
      ShareParams(
        text: trip.name,
        sharePositionOrigin: box == null
            ? Rect.zero
            : box.localToGlobal(Offset.zero) & box.size,
      ),
    );
  }

  Future<void> _openCalendarSubscription(BuildContext context) {
    return PdlSheet.show<void>(
      context: context,
      builder: (BuildContext _) => PdlSheet(
        title: 'calendar.subscription.title'.tr(),
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: PdlSpacing.section),
            child: CalendarSubscriptionCard(teamSlug: trip.team.slug),
          ),
        ],
      ),
    );
  }

  void _showParticipants(BuildContext context) {
    // À venir avec la barre d'action (S24-6) : la feuille Participants du
    // lot 2 est généralisée aux voyages, dont les participants sont eux aussi
    // embarqués et non paginés.
  }

  // ── 2 · Identité ────────────────────────────────────────────────────────
  Widget _identity(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;

    return Padding(
      padding: const EdgeInsets.all(PdlSpacing.section),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          PdlTeamLine(
            label: trip.team.name,
            onTap: () => context.push(Paths.team(trip.team.slug)),
          ),
          const SizedBox(height: 2),
          Text(trip.name, style: t.screenTitle),
          const SizedBox(height: PdlSpacing.chipGap),
          Wrap(
            spacing: PdlSpacing.badgeGap,
            runSpacing: PdlSpacing.badgeGap,
            children: <Widget>[
              if (trip.isPast && !trip.isCancelled)
                PdlBadge(
                  label: 'trips.finished'.tr(),
                  tone: PdlDerivedTones.done(c),
                )
              else
                PdlBadge(
                  label: 'status.${trip.status.toLowerCase()}'.tr(),
                  tone: Status.fromJson(trip.status).tone(c),
                ),
              PdlBadge(
                label: 'visibility.${trip.visibility.toLowerCase()}'.tr(),
                tone: Visibility.fromJson(trip.visibility).tone(c),
                icon: _visibilityIcon(trip.visibility),
              ),
              if (trip.registered)
                PdlBadge(
                  label: 'trips.registered'.tr(),
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

  // ── 3 · Étapes ──────────────────────────────────────────────────────────
  Widget _stages(
    BuildContext context,
    List<TripStageDto> stages,
    String? selected,
    ValueChanged<String> select,
  ) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          PdlSectionHeader(
            title: 'trips.stages'.tr(),
            count: stages.isEmpty ? null : '${stages.length}',
          ),
          if (stages.isEmpty)
            PdlEmptyState(
              variant: PdlEmptyVariant.empty,
              icon: PdlIcons.stage,
              title: 'trips.noStageTitle'.tr(),
              message: 'trips.noStageMessage'.tr(),
            )
          else
            for (final TripStageDto stage in stages)
              Padding(
                padding: const EdgeInsets.only(bottom: PdlSpacing.feedGap),
                child: StageCard(
                  stage: stage,
                  selected: stage.id == selected,
                  onSelect: () => select(stage.id),
                  onTap: () => context.push(
                    Paths.stage(trip.team.slug, trip.slug, stage.slug),
                  ),
                ),
              ),
        ],
      ),
    );
  }

  // ── 4 · Description ─────────────────────────────────────────────────────
  Widget _description(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          PdlSectionHeader(title: 'trips.description'.tr()),
          PdlMarkdownBody(data: trip.media.markdown),
        ],
      ),
    );
  }
}

// ────────────────────────────────────────────────────── chargement, erreur

class _TripDetailSkeleton extends StatelessWidget {
  const _TripDetailSkeleton();

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
            height: _kTripHeroHeight,
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
                for (int i = 0; i < 4; i++) ...<Widget>[
                  if (i > 0) const SizedBox(height: PdlSpacing.feedGap),
                  const PdlSkeleton(height: 64),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _TripDetailError extends ConsumerWidget {
  const _TripDetailError({required this.tripKey, required this.error});

  final TripKey tripKey;
  final Object error;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final ApiError resolved = resolveApiError(error);

    return PdlScreenScaffold(
      appBar: PdlAppBar(
        onBack: () => context.pop(),
        backSemanticLabel: 'common.back'.tr(),
      ),
      body: Center(
        child: PdlEmptyState(
          variant: resolved.code == 'NOT_FOUND'
              ? PdlEmptyVariant.notFound
              : PdlEmptyVariant.error,
          icon: resolved.isOffline ? PdlIcons.offline : null,
          title: resolved.title ?? 'common.loadError'.tr(),
          message: resolved.message,
          actions: <Widget>[
            PdlButton(
              label: 'common.retry'.tr(),
              variant: PdlButtonVariant.outline,
              onPressed: () => ref.invalidate(tripDetailProvider(tripKey)),
            ),
          ],
        ),
      ),
    );
  }
}
