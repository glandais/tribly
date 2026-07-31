import 'dart:math' as math;

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/config/config_provider.dart';
import '../../../../core/config/map_style_options.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../data/route_repository.dart';
import '../../data/routes_mass_repository.dart';
import '../../domain/route_filters.dart';
import 'route_card.dart';
import 'routes_near_me_controls.dart';

/// Le cadrage initial de la carte, **figé au montage**.
///
/// `GET …/routes/bounds` est lu une fois, avec les filtres tels qu'ils sont à
/// l'ouverture, et n'est **jamais rejoué** sur changement de filtre (§3.1) :
/// une carte qui se recadre toute seule pendant qu'on la déplace est une carte
/// qu'on ne peut pas lire.
final routeBoundsProvider = FutureProvider.autoDispose
    .family<PdlMapBox?, RouteFilters>(
      (ref, filters) => ref.watch(routeRepositoryProvider).fetchBounds(filters),
    );

/// La vue Carte de la parcothèque.
///
/// **Le plafond du repli GeoJSON est visible, jamais silencieux** (§3.1, risque
/// n° 2) : dès que la zone contient plus de tracés que
/// [PdlMassLayerController.limit], une pilule annonce « 412 ici · 120
/// affichés ». Une carte tronquée sans le dire se lit comme une carte
/// complète, et fait conclure qu'il n'y a rien de plus.
class RoutesMapView extends ConsumerStatefulWidget {
  const RoutesMapView({
    super.key,
    required this.filters,
    required this.onChanged,
  });

  final RouteFilters filters;
  final ValueChanged<RouteFilters> onChanged;

  @override
  ConsumerState<RoutesMapView> createState() => _RoutesMapViewState();
}

class _RoutesMapViewState extends ConsumerState<RoutesMapView> {
  final PdlMapController _controller = PdlMapController();
  PdlMassLayerController? _mass;
  String? _selectedSlug;

  /// Les filtres du premier cadre : ce sont eux, et eux seuls, qui cadrent la
  /// carte.
  late final RouteFilters _framingFilters = widget.filters;

  @override
  void didUpdateWidget(RoutesMapView oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.filters != oldWidget.filters) {
      // Un filtre a changé : la zone regardée ne bouge pas, son contenu si.
      _selectedSlug = null;
      _mass?.searchHere();
    }
  }

  @override
  void dispose() {
    _mass?.dispose();
    _controller.dispose();
    super.dispose();
  }

  PdlMassLayerController _massFor(Color color) {
    // Le fetcher capture les filtres courants ; il est reconstruit à chaque
    // build, le contrôleur — et donc les tracés déjà chargés — survit.
    final PdlMassFetcher fetcher = ref
        .read(routesMassRepositoryProvider)
        .fetcher(color: color, filters: widget.filters);
    final PdlMassLayerController existing = _mass ??= PdlMassLayerController(
      fetcher: (PdlMassRequest r) => fetcher(r),
    );
    return existing;
  }

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final Brightness brightness = Theme.of(context).brightness;
    final AsyncValue<ResolvedMapStyle?> style = ref.watch(
      mapStyleProvider(brightness),
    );
    final AsyncValue<PdlMapBox?> bounds = ref.watch(
      routeBoundsProvider(_framingFilters),
    );
    final AsyncValue<MapCenterDto> center = ref.watch(defaultCenterProvider);

    final ResolvedMapStyle? resolved = style.value;
    if (resolved == null) {
      return _MapPending(error: style.error, onRetry: _reloadConfig);
    }

    final PdlMassLayerController mass = _massFor(c.mapTrack);

    return AnimatedBuilder(
      animation: mass,
      builder: (BuildContext context, _) {
        final PdlMapBox? fitBox = _proximityBox() ?? bounds.value;

        return PdlMapHero(
          labels: PdlMapHeroLabels(
            enterFullscreen: 'map.fullscreen'.tr(),
            exitFullscreen: 'map.exitFullscreen'.tr(),
            chooseBackground: 'map.background'.tr(),
            backgroundSheetTitle: 'map.background'.tr(),
            hillshade: 'map.hillshade'.tr(),
          ),
          styles: servedMapStyleOptions(ref),
          hillshadeEnabled: servedHillshadeEnabled(ref),
          onHillshadeChanged: (bool on) =>
              ref.read(hillshadeEnabledProvider.notifier).set(on),
          selectedStyleId: resolved.style.id,
          onStyleSelected: (String id) =>
              ref.read(mapStyleIdProvider.notifier).select(id),
          pill: _pill(mass),
          floatingCard: _floatingCard(mass),
          foreground: Positioned(
            left: PdlSpacing.sectionTightV,
            top: PdlSpacing.sectionTightV + PdlMetrics.tapTarget + 8,
            right: PdlSpacing.sectionTightV + PdlMetrics.tapTarget + 8,
            child: RoutesNearMeControls(
              filters: widget.filters,
              onChanged: widget.onChanged,
            ),
          ),
          mapBuilder: (BuildContext context) => PdlMap(
            hillshade: servedHillshade(context, ref),
            styleUrl: resolved.url,
            controller: _controller,
            tracks: mass.tracks,
            initialCenter: center.value == null
                ? null
                : PdlMapPoint(lon: center.value!.lon, lat: center.value!.lat),
            initialZoom: center.value?.zoom.toDouble() ?? 5,
            fitBox: fitBox,
            selectedTrackId: _selectedSlug,
            onTrackSelected: (String? id) => setState(() => _selectedSlug = id),
            onCameraIdle: mass.onCameraIdle,
          ),
        );
      },
    );
  }

  void _reloadConfig() => ref.invalidate(appConfigProvider);

  /// La boîte à cadrer quand « Autour de moi » est armé : le carré de rayon
  /// choisi autour de la position. Sans elle, changer de rayon ne changerait
  /// que la liste.
  PdlMapBox? _proximityBox() {
    final RouteFilters f = widget.filters;
    if (!f.hasProximity) return null;
    final double lat = f.nearLat!;
    final double lon = f.nearLon!;
    // 1° de latitude ≈ 111 km ; en longitude, autant divisé par le cosinus de
    // la latitude. Le plancher évite une division par zéro aux pôles.
    final double dLat = f.effectiveNearRadius / 111000;
    final double dLon =
        dLat / math.max(0.05, math.cos(lat * math.pi / 180).abs());
    return PdlMapBox(
      minLon: lon - dLon,
      minLat: lat - dLat,
      maxLon: lon + dLon,
      maxLat: lat + dLat,
    );
  }

  /// « Rechercher dans cette zone » — n'apparaît **qu'après un déplacement**,
  /// jamais au premier cadre : proposer de chercher là où on vient d'arriver
  /// n'a pas de sens. Elle cède la place au plafond quand celui-ci est atteint,
  /// parce que le plafond est l'information la plus utile des deux.
  Widget? _pill(PdlMassLayerController mass) {
    if (mass.loading) {
      return PdlMapPill(icon: PdlIcons.refresh, label: 'common.loading'.tr());
    }
    if (mass.error != null) {
      return PdlMapPill(
        icon: PdlIcons.error,
        label: getErrorMessage(mass.error!),
        onPressed: mass.searchHere,
      );
    }
    if (mass.isStale) {
      return PdlMapPill(
        icon: PdlIcons.refresh,
        label: 'map.searchThisArea'.tr(),
        onPressed: mass.searchHere,
      );
    }
    if (mass.truncated) {
      final String label = 'map.truncated'.tr(
        namedArgs: <String, String>{
          'total': '${mass.total ?? mass.tracks.length}',
          'shown': '${mass.tracks.length}',
        },
      );
      return PdlMapPill(icon: PdlIcons.warning, label: label);
    }
    return null;
  }

  Widget? _floatingCard(PdlMassLayerController mass) {
    final String? slug = _selectedSlug;
    if (slug == null) return null;
    final PdlMapTrack? track = mass.tracks
        .where((PdlMapTrack t) => t.id == slug)
        .firstOrNull;
    if (track == null) return null;
    return _SelectedRouteCard(
      title: track.label ?? slug,
      route: ref.read(routesMassRepositoryProvider).row(slug),
    );
  }
}

/// La carte flottante d'un tracé sélectionné.
///
/// Elle relit la ligne de liste que le repli de masse a déjà chargée plutôt
/// que de rappeler l'API : c'est la même source, et un appel de plus sur un tap
/// de carte serait payé à chaque sélection.
class _SelectedRouteCard extends ConsumerWidget {
  const _SelectedRouteCard({required this.title, required this.route});

  final String title;
  final RouteDto? route;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);

    final RouteDto? row = route;
    return PdlMapFloatingCard(
      onTap: row == null
          ? null
          : () => context.push(Paths.route(row.team.slug, row.slug)),
      child: Row(
        children: <Widget>[
          PdlThumb(
            imageUrl:
                row?.media.assets.thumbnailLight?.url ?? row?.thumbnailUrl,
            darkImageUrl: row?.media.assets.thumbnailDark?.url,
            size: PdlMetrics.thumbSm,
            fallbackIcon: PdlIcons.route,
          ),
          const SizedBox(width: PdlSpacing.cardTight),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Text(
                  row?.name ?? title,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: t.cardTitle.copyWith(color: c.text),
                ),
                if (row != null) ...<Widget>[
                  const SizedBox(height: 4),
                  PdlStatRow(stats: routeStats(row, units)),
                ],
              ],
            ),
          ),
          if (row != null) ...<Widget>[
            const SizedBox(width: PdlSpacing.chipGap),
            PdlButton(
              size: PdlButtonSize.sm,
              label: 'common.open'.tr(),
              onPressed: () =>
                  context.push(Paths.route(row.team.slug, row.slug)),
            ),
          ],
        ],
      ),
    );
  }
}

/// Un écran de carte qui n'a pas encore son fond : `ConfigDto` est en vol, ou
/// n'est pas venu.
class _MapPending extends StatelessWidget {
  const _MapPending({required this.error, required this.onRetry});

  final Object? error;
  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    if (error == null) {
      return const Center(child: CircularProgressIndicator());
    }
    return Center(
      child: PdlEmptyState(
        variant: PdlEmptyVariant.error,
        title: 'common.loadError'.tr(),
        message: getErrorMessage(error!),
        actions: <Widget>[
          PdlButton(
            label: 'common.retry'.tr(),
            variant: PdlButtonVariant.outline,
            size: PdlButtonSize.sm,
            onPressed: onRetry,
          ),
        ],
      ),
    );
  }
}
