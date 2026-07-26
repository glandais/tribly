import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
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
import '../../../../core/utils/formatters.dart';
import '../../../routes/presentation/widgets/embedded_route_sheet.dart';
import '../../../routes/providers/route_detail_provider.dart';
import '../../providers/trip_detail_provider.dart';

/// L'écran 25 — une étape de voyage.
///
/// **Il n'y a pas d'endpoint d'étape dédié, et ce n'est pas un problème** :
/// l'écran charge le voyage et y sélectionne l'étape par son slug. Le rail
/// d'étapes a de toute façon besoin de la liste complète, et le détail est
/// partagé avec l'écran 24 — passer d'une étape à l'autre ne recharge rien.
class StageDetailPage extends ConsumerWidget {
  const StageDetailPage({
    super.key,
    required this.teamSlug,
    required this.tripSlug,
    required this.stageSlug,
  });

  final String teamSlug;
  final String tripSlug;
  final String stageSlug;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final TripKey key = TripKey(teamSlug: teamSlug, tripSlug: tripSlug);

    return ref
        .watch(tripDetailProvider(key))
        .when(
          data: (TripDto trip) {
            final List<TripStageDto> stages = trip.orderedStages;
            final int index = stages.indexWhere(
              (TripStageDto s) => s.slug == stageSlug,
            );
            if (index < 0) return _StageNotFound(trip: trip);
            return _StageDetailContent(
              trip: trip,
              stages: stages,
              stage: stages[index],
            );
          },
          loading: () => const _StageSkeleton(),
          error: (Object error, StackTrace stack) =>
              _StageError(tripKey: key, error: error),
        );
  }
}

// ─────────────────────────────────────────────────────────────── contenu

class _StageDetailContent extends ConsumerWidget {
  const _StageDetailContent({
    required this.trip,
    required this.stages,
    required this.stage,
  });

  final TripDto trip;
  final List<TripStageDto> stages;
  final TripStageDto stage;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final String? routeSlug = stage.route?.slug;

    return PdlScreenScaffold(
      appBar: PdlAppBar(
        title: stage.name,
        onBack: () => _openTrip(context),
        backSemanticLabel: 'trips.stage.backToTrip'.tr(),
        actions: <Widget>[
          PdlAppBarAction(
            icon: PdlIcons.share,
            semanticLabel: 'routes.share'.tr(),
            onPressed: () => _share(context),
          ),
        ],
      ),
      // `PdlScreenScaffold` enveloppe lui-même la barre dans un
      // `PdlPinnedToolbar` : l'envelopper ici en imbriquerait deux slivers.
      toolbar: _rail(context),
      slivers: <Widget>[
        SliverToBoxAdapter(child: _identity(context)),
        SliverToBoxAdapter(child: _facts(context)),
        if (routeSlug != null)
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
              // **Le bloc de l'écran 13, tel quel.** Toute divergence de rendu
              // entre les deux serait un défaut, pas une adaptation : c'est le
              // même widget, avec ses exports, son réticule et ses cols.
              child: EmbeddedRouteSheet(
                routeKey: RouteKey(
                  teamSlug: trip.team.slug,
                  routeSlug: routeSlug,
                ),
              ),
            ),
          )
        else
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
              child: PdlEmptyState(
                variant: PdlEmptyVariant.empty,
                icon: PdlIcons.route,
                title: 'trips.stage.noRouteTitle'.tr(),
                message: 'trips.stage.noRouteMessage'.tr(),
              ),
            ),
          ),
        if (stage.media.markdown.trim().isNotEmpty)
          SliverToBoxAdapter(child: _description(context)),
        // **Pas de commentaires d'étape** : le contrat n'en expose que sur les
        // publications, sorties, parcours et voyages (§4.2). Plutôt qu'un fil
        // vide ou un composeur qui échouerait, un renvoi vers celui du voyage.
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
            child: PdlButton(
              label: 'trips.stage.commentOnTrip'.tr(),
              variant: PdlButtonVariant.text,
              icon: PdlIcons.comment,
              onPressed: () => _openTrip(context),
            ),
          ),
        ),
        const SliverToBoxAdapter(child: SizedBox(height: PdlSpacing.section)),
      ],
    );
  }

  // ── Le rail ─────────────────────────────────────────────────────────────
  /// Épinglé sous la barre, pastille active recentrée : arriver sur la J6 sans
  /// recentrage donnerait un rail qui commence à J1, c'est-à-dire un rail qui
  /// ne montre pas où l'on est.
  ///
  /// Un tap **remplace** l'écran, il ne l'empile pas : sans cela, parcourir
  /// sept étapes laisserait sept pages dans la pile et sept retours à faire.
  Widget _rail(BuildContext context) {
    final int current = stages.indexOf(stage);

    return PdlStageRail(
      selectedIndex: current + 1,
      items: <PdlStageRailItem>[
        PdlStageRailItem(label: 'trips.stage.overview'.tr()),
        for (final TripStageDto s in stages)
          PdlStageRailItem(
            label: s.name,
            sublabel: s.startsAt == null
                ? null
                : AppFormatters.formatDayMonth(s.startsAt!),
          ),
      ],
      onSelected: (int index) {
        if (index == 0) {
          _openTrip(context);
          return;
        }
        final TripStageDto target = stages[index - 1];
        if (target.slug == stage.slug) return;
        context.replace(Paths.stage(trip.team.slug, trip.slug, target.slug));
      },
    );
  }

  void _openTrip(BuildContext context) =>
      context.go(Paths.trip(trip.team.slug, trip.slug));

  Future<void> _share(BuildContext context) async {
    final RenderBox? box = context.findRenderObject() as RenderBox?;
    await SharePlus.instance.share(
      ShareParams(
        text: '${trip.name} — ${stage.name}',
        sharePositionOrigin: box == null
            ? Rect.zero
            : box.localToGlobal(Offset.zero) & box.size,
      ),
    );
  }

  // ── Identité ────────────────────────────────────────────────────────────
  Widget _identity(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;

    return Padding(
      padding: const EdgeInsets.all(PdlSpacing.section),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          // La ligne de contexte remonte au **voyage**, pas à l'équipe : c'est
          // le parent immédiat de l'étape, et celui vers lequel tout le reste
          // de l'écran renvoie.
          PdlTeamLine(
            label: trip.name,
            icon: PdlIcons.trip,
            onTap: () => _openTrip(context),
          ),
          const SizedBox(height: 2),
          Text(stage.name, style: t.screenTitle),
          const SizedBox(height: PdlSpacing.chipGap),
          Wrap(
            spacing: PdlSpacing.badgeGap,
            runSpacing: PdlSpacing.badgeGap,
            children: <Widget>[
              PdlBadge(
                label: 'trips.stage.numberOf'.tr(
                  namedArgs: <String, String>{
                    'index': '${stage.stageIndex}',
                    'total': '${stage.stageCount}',
                  },
                ),
                tone: PdlDerivedTones.registered(c),
              ),
              if (trip.isCancelled)
                PdlBadge(
                  label: 'status.cancelled'.tr(),
                  tone: Status.fromJson('CANCELLED').tone(c),
                )
              else if (trip.isPast)
                PdlBadge(
                  label: 'trips.finished'.tr(),
                  tone: PdlDerivedTones.done(c),
                )
              else
                PdlBadge(
                  label: 'status.${trip.status.toLowerCase()}'.tr(),
                  tone: Status.fromJson(trip.status).tone(c),
                ),
            ],
          ),
        ],
      ),
    );
  }

  // ── Date et lieux ───────────────────────────────────────────────────────
  Widget _facts(BuildContext context) {
    final PdlTypography t = context.pdlText;
    final DateTime? start = stage.startsAt;
    final PlaceDetailDto? from = stage.startPlace;
    final PlaceDetailDto? to = stage.endPlace;

    if (start == null && from == null && to == null) {
      return const SizedBox.shrink();
    }

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
      child: PdlCard(
        flat: true,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            if (start != null)
              Row(
                children: <Widget>[
                  PdlNumberPill(value: stage.stageIndex),
                  const SizedBox(width: PdlSpacing.chipGap),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Text(
                          AppFormatters.formatLongDate(start),
                          style: t.bodyStrong,
                        ),
                        Text(
                          'trips.stage.groupStart'.tr(
                            namedArgs: <String, String>{
                              'time': AppFormatters.formatTime(start),
                            },
                          ),
                          style: t.xs,
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            // `PlaceDetailDto.address` existe au contrat et n'était **jamais
            // lue** : « Place de Jaude » devient « Place de Jaude, 63000
            // Clermont-Ferrand ». Absente, la ligne se réduit d'elle-même.
            if (from != null) ...<Widget>[
              const SizedBox(height: PdlSpacing.cardTight),
              PdlPlaceRow(
                kind: PdlPlaceKind.start,
                name: from.name,
                address: from.address,
              ),
            ],
            if (to != null) ...<Widget>[
              const SizedBox(height: PdlSpacing.chipGap),
              PdlPlaceRow(
                kind: PdlPlaceKind.end,
                name: to.name,
                address: to.address,
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _description(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          PdlSectionHeader(title: 'trips.description'.tr()),
          PdlMarkdownBody(data: stage.media.markdown),
        ],
      ),
    );
  }
}

// ────────────────────────────────────────── introuvable, chargement, erreur

class _StageNotFound extends StatelessWidget {
  const _StageNotFound({required this.trip});

  final TripDto trip;

  @override
  Widget build(BuildContext context) {
    void openTrip() => context.go(Paths.trip(trip.team.slug, trip.slug));

    return PdlScreenScaffold(
      appBar: PdlAppBar(
        onBack: openTrip,
        backSemanticLabel: 'trips.stage.backToTrip'.tr(),
      ),
      body: Center(
        child: PdlEmptyState(
          variant: PdlEmptyVariant.notFound,
          title: 'trips.stage.notFound'.tr(),
          message: 'trips.stage.notFoundMessage'.tr(),
          actions: <Widget>[
            PdlButton(
              label: 'trips.stage.backToTrip'.tr(),
              variant: PdlButtonVariant.outline,
              onPressed: openTrip,
            ),
          ],
        ),
      ),
    );
  }
}

/// Le squelette annonce la forme de l'écran : quatre pastilles de rail, la
/// carte, deux blocs. Pas un rond qui tourne.
class _StageSkeleton extends StatelessWidget {
  const _StageSkeleton();

  @override
  Widget build(BuildContext context) {
    return PdlScreenScaffold(
      appBar: PdlAppBar(
        onBack: () => context.pop(),
        backSemanticLabel: 'common.back'.tr(),
      ),
      body: ListView(
        padding: const EdgeInsets.all(PdlSpacing.section),
        children: <Widget>[
          Row(
            children: <Widget>[
              for (int i = 0; i < 4; i++) ...<Widget>[
                if (i > 0) const SizedBox(width: PdlSpacing.chipGap),
                const PdlSkeleton(
                  height: PdlMetrics.tapTarget,
                  width: 74,
                  borderRadius: PdlRadii.pillAll,
                ),
              ],
            ],
          ),
          const SizedBox(height: PdlSpacing.section),
          const PdlSkeleton(height: 20, width: 180),
          const SizedBox(height: PdlSpacing.chipGap),
          const PdlSkeleton(height: 96),
          const SizedBox(height: PdlSpacing.section),
          const PdlSkeleton(height: 240),
          const SizedBox(height: PdlSpacing.section),
          const PdlSkeleton(height: 120),
        ],
      ),
    );
  }
}

class _StageError extends ConsumerWidget {
  const _StageError({required this.tripKey, required this.error});

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
