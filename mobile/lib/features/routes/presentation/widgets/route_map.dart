import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/config/config_provider.dart';
import '../../../../core/pdl/map/pdl_map.dart';
import '../../../../core/pdl/map/pdl_map_controller.dart';
import '../../../../core/pdl/map/pdl_map_hero.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../domain/track_slope_colors.dart';
import 'route_cursor_controller.dart';

/// L'adaptateur entre `RouteDetailDto` et [PdlMap].
///
/// C'est tout ce qu'il reste des 276 lignes d'origine : la conversion du DTO
/// vers les types simples de `core/pdl/map`, et le branchement des fonds servis
/// par `ConfigDto.mapStyles[]`. **Aucune URL de style, aucun
/// `Future.delayed`, aucune couleur en dur.**
class RouteMap extends ConsumerStatefulWidget {
  const RouteMap({
    super.key,
    required this.route,
    this.cursor,
    this.onMapTapped,
  });

  final RouteDetailDto route;

  /// Le réticule partagé avec le profil altimétrique. `null` sur les écrans qui
  /// n'en ont pas (l'aperçu d'une sortie, par exemple).
  final RouteCursorController? cursor;

  /// Tap sur la carte, converti en distance cumulée par [cursor]. `null`
  /// signifie « hors du tracé », ce qui efface le réticule.
  final ValueChanged<double?>? onMapTapped;

  @override
  ConsumerState<RouteMap> createState() => _RouteMapState();
}

class _RouteMapState extends ConsumerState<RouteMap> {
  final PdlMapController _controller = PdlMapController();

  RouteDetailDto get route => widget.route;

  @override
  void initState() {
    super.initState();
    widget.cursor?.distance.addListener(_pushCursor);
  }

  @override
  void didUpdateWidget(RouteMap oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.cursor != widget.cursor) {
      oldWidget.cursor?.distance.removeListener(_pushCursor);
      widget.cursor?.distance.addListener(_pushCursor);
    }
  }

  @override
  void dispose() {
    widget.cursor?.distance.removeListener(_pushCursor);
    _controller.dispose();
    super.dispose();
  }

  /// Profil → carte. **Aucun `setState`** : on pousse la source GeoJSON du
  /// marqueur, l'arbre Flutter ne bouge pas d'un widget.
  void _pushCursor() {
    final RouteCursorController? cursor = widget.cursor;
    if (cursor == null) return;
    _controller.setCursor(cursor.positionFor(cursor.distance.value));
  }

  /// Les LineStrings du parcours, telles quelles.
  List<List<List<double>>> get _lines => <List<List<double>>>[
    for (final TrackDto t in route.tracks) t.line.coordinates,
  ];

  @override
  Widget build(BuildContext context) {
    final Brightness brightness = Theme.of(context).brightness;
    final AsyncValue<ResolvedMapStyle?> style = ref.watch(
      mapStyleProvider(brightness),
    );
    final AsyncValue<MapCenterDto> center = ref.watch(defaultCenterProvider);
    final ResolvedMapStyle? resolved = style.value;

    if (resolved == null) {
      // Pas de style servi, pas de carte : on n'invente pas d'URL de repli.
      return ColoredBox(
        color: context.pdl.surfaceAlt,
        child: Center(
          child: style.hasError
              ? Text('map.unavailable'.tr())
              : const CircularProgressIndicator(),
        ),
      );
    }

    final List<PdlMapTrack> tracks = <PdlMapTrack>[
      PdlMapTrack(
        id: route.id,
        lines: _lines,
        color: context.pdl.mapTrack,
        label: route.name,
        // Colorisation **par segment** : chaque tronçon porte la teinte de sa
        // propre pente (§1.1.5). Le repli `mapTrack` reste la couleur d'un
        // segment sans altitude exploitable.
        segmentColors: slopeColorsForLines(_lines),
      ),
    ];

    final MapCenterDto? defaultCenter = center.value;

    return PdlMapHero(
      labels: PdlMapHeroLabels(
        enterFullscreen: 'map.fullscreen'.tr(),
        exitFullscreen: 'map.exitFullscreen'.tr(),
        chooseBackground: 'map.chooseBackground'.tr(),
        backgroundSheetTitle: 'map.background'.tr(),
      ),
      // La fiche parcours **est** déjà la carte plein écran : un bouton de
      // plus n'y mènerait nulle part.
      showFullscreenButton: false,
      topScrim: true,
      styles: <PdlMapStyleOption>[
        for (final MapStyleDto s in _servedStyles(ref))
          PdlMapStyleOption(id: s.id, label: s.label),
      ],
      selectedStyleId: resolved.style.id,
      onStyleSelected: (String id) =>
          ref.read(mapStyleIdProvider.notifier).select(id),
      mapBuilder: (BuildContext context) => PdlMap(
        controller: _controller,
        styleUrl: resolved.url,
        onMapTapped: widget.onMapTapped == null
            ? null
            : (double lon, double lat) =>
                  widget.onMapTapped!(widget.cursor?.distanceNear(lon, lat)),
        tracks: tracks,
        start: _point(route.start) ?? route.firstPoint,
        end: _point(route.end) ?? route.lastPoint,
        waypoints: <PdlMapPoint>[
          for (final WaypointDto w in route.waypoints)
            if (w.geometry.coordinates.length >= 2)
              PdlMapPoint(
                lon: w.geometry.coordinates[0],
                lat: w.geometry.coordinates[1],
                label: w.name,
              ),
        ],
        kmMarkers: computePdlKmMarkers(_lines, totalMeters: route.distance),
        initialCenter: defaultCenter == null
            ? null
            : PdlMapPoint(lon: defaultCenter.lon, lat: defaultCenter.lat),
        initialZoom: defaultCenter?.zoom ?? 5,
        fitBox: PdlMapBox.ofTracks(tracks),
      ),
    );
  }

  List<MapStyleDto> _servedStyles(WidgetRef ref) =>
      ref.watch(appConfigProvider).value?.mapStyles ?? const <MapStyleDto>[];

  /// Le départ et l'arrivée servis par le contrat ; à défaut, les extrémités du
  /// tracé — c'est ce que faisait l'implémentation d'origine.
  PdlMapPoint? _point(GeoJsonPoint? point) {
    if (point != null && point.coordinates.length >= 2) {
      return PdlMapPoint(lon: point.coordinates[0], lat: point.coordinates[1]);
    }
    return null;
  }
}

/// Les extrémités du tracé, quand `start` / `end` sont absents du DTO.
extension RouteMapEndpoints on RouteDetailDto {
  PdlMapPoint? get firstPoint => _endpoint(first: true);
  PdlMapPoint? get lastPoint => _endpoint(first: false);

  PdlMapPoint? _endpoint({required bool first}) {
    for (final TrackDto t in first ? tracks : tracks.reversed) {
      final List<List<double>> line = t.line.coordinates;
      if (line.isEmpty) continue;
      final List<double> c = first ? line.first : line.last;
      if (c.length < 2) continue;
      return PdlMapPoint(lon: c[0], lat: c[1]);
    }
    return null;
  }
}
