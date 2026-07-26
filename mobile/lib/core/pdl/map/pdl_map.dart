import 'package:flutter/material.dart';
import 'package:maplibre/maplibre.dart';

import '../../theme/pdl_colors.dart';
import '../../theme/pdl_tokens.dart';
import '../../theme/pdl_typography.dart';
import 'pdl_map_controller.dart';

/// La carte Pédalons.
///
/// Remplace `features/routes/.../route_map.dart`, qui rendait un tracé, une
/// couleur, aucune interaction, avec **des URL de style codées en dur** et un
/// cadrage par `Future.delayed(100 ms)`. Ici :
///
/// * le fond vient de [styleUrl], que l'appelant lit sur `mapStyleProvider` —
///   `ConfigDto.mapStyles[]`, servi et non compilé. **Aucune URL de style n'est
///   écrite dans `lib/`** ;
/// * le cadrage part de `MapEventStyleLoaded`, jamais d'un délai ;
/// * la carte s'ouvre sur [initialCenter] (`ConfigDto.defaultCenter`) avant de
///   savoir ce qu'elle montre ;
/// * plusieurs tracés colorisés cohabitent, chacun sur **sa** couche, et le tap
///   rend l'identifiant du tracé touché.
///
/// Le widget ne connaît aucun DTO : voir [PdlMapTrack].
class PdlMap extends StatefulWidget {
  const PdlMap({
    super.key,
    required this.styleUrl,
    this.controller,
    this.tracks = const <PdlMapTrack>[],
    this.waypoints = const <PdlMapPoint>[],
    this.kmMarkers = const <PdlKmMarker>[],
    this.start,
    this.end,
    this.initialCenter,
    this.initialZoom = 5,
    this.fitBox,
    this.fitPadding = const EdgeInsets.all(50),
    this.selectedTrackId,
    this.onTrackSelected,
    this.onMapTapped,
    this.onCameraIdle,
    this.gestures = const MapGestures.all(),
    this.overlays = const <Widget>[],
  });

  /// L'URL du style de fond, **toujours** fournie par l'appelant.
  final String styleUrl;

  /// Contrôleur externe, quand l'écran a besoin de piloter la sélection ou de
  /// lire la région visible. À défaut, [PdlMap] en crée un et le possède.
  final PdlMapController? controller;

  final List<PdlMapTrack> tracks;
  final List<PdlMapPoint> waypoints;

  /// Bornes kilométriques déjà calculées — voir [computePdlKmMarkers].
  final List<PdlKmMarker> kmMarkers;

  final PdlMapPoint? start;
  final PdlMapPoint? end;

  /// Où la carte s'ouvre avant toute donnée (`ConfigDto.defaultCenter`).
  final PdlMapPoint? initialCenter;
  final double initialZoom;

  /// La boîte sur laquelle cadrer dès que le style est chargé. Pour une liste
  /// elle vient de `GET …/routes/bounds` ; pour un tracé unique,
  /// [PdlMapBox.ofTracks] la calcule localement.
  final PdlMapBox? fitBox;
  final EdgeInsets fitPadding;

  final String? selectedTrackId;
  final ValueChanged<String?>? onTrackSelected;

  /// Tap sur la carte, en coordonnées **géographiques** `(lon, lat)`.
  ///
  /// Distinct de [onTrackSelected], qui rend l'identité d'un tracé touché :
  /// ici c'est la position brute, dont l'appelant fait ce qu'il veut — la
  /// fiche parcours en dérive la distance cumulée du réticule.
  final void Function(double lon, double lat)? onMapTapped;

  /// Appelé quand la caméra se stabilise, avec la région visible — de quoi
  /// alimenter « Rechercher dans cette zone ».
  final ValueChanged<PdlMapBox>? onCameraIdle;

  final MapGestures gestures;

  /// Surcouches posées **dans** le contexte de la carte : boutons, pilules,
  /// carte flottante. Elles arrivent après la couche de marqueurs, donc
  /// au-dessus.
  final List<Widget> overlays;

  @override
  State<PdlMap> createState() => _PdlMapState();
}

class _PdlMapState extends State<PdlMap> {
  PdlMapController? _owned;
  PdlMapBox? _fittedBox;
  String? _appliedStyleUrl;

  PdlMapController get _controller =>
      widget.controller ?? (_owned ??= PdlMapController());

  @override
  void initState() {
    super.initState();
    _appliedStyleUrl = widget.styleUrl;
  }

  @override
  void dispose() {
    _owned?.dispose();
    super.dispose();
  }

  @override
  void didUpdateWidget(PdlMap oldWidget) {
    super.didUpdateWidget(oldWidget);

    // Changer de fond de carte **efface toutes les couches** : on ne fait que
    // demander le style, et `MapEventStyleLoaded` les repose.
    if (widget.styleUrl != _appliedStyleUrl) {
      _appliedStyleUrl = widget.styleUrl;
      _fittedBox = null;
      _controller.detachStyle();
      _controller.map?.setStyle(widget.styleUrl);
    }

    if (!identical(widget.tracks, oldWidget.tracks) ||
        !identical(widget.waypoints, oldWidget.waypoints) ||
        widget.start != oldWidget.start ||
        widget.end != oldWidget.end) {
      _pushContent();
    }
    if (widget.selectedTrackId != oldWidget.selectedTrackId) {
      _controller.select(widget.selectedTrackId);
    }
    if (widget.fitBox != null && widget.fitBox != _fittedBox) {
      _fit();
    }
  }

  void _pushContent() {
    _controller.setContent(
      tracks: widget.tracks,
      waypoints: widget.waypoints,
      start: widget.start,
      end: widget.end,
    );
  }

  Future<void> _fit() async {
    final PdlMapBox? box = widget.fitBox ?? PdlMapBox.ofTracks(widget.tracks);
    if (box == null) return;
    _fittedBox = box;
    await _controller.fitBox(box, padding: widget.fitPadding);
  }

  void _onEvent(MapEvent event) {
    switch (event) {
      case MapEventStyleLoaded():
        _onStyleLoaded(event.style);
      case MapEventClick():
        widget.onMapTapped?.call(event.point.lon, event.point.lat);
        final String? id = _controller.trackIdAt(event.screenPoint);
        if (id != null || _controller.selectedTrackId != null) {
          widget.onTrackSelected?.call(id);
          if (widget.selectedTrackId == null) _controller.select(id);
        }
      case MapEventCameraIdle():
        final PdlMapBox? box = _controller.visibleBox;
        if (box != null) widget.onCameraIdle?.call(box);
      default:
        break;
    }
  }

  Future<void> _onStyleLoaded(StyleController style) async {
    final PdlColors c = context.pdl;
    _controller
      ..setMarkerColors(
        start: c.mapStart,
        end: c.mapEnd,
        waypoint: c.mapWaypoint,
        stroke: c.surface,
        cursor: c.primary,
      )
      ..select(widget.selectedTrackId);
    await _controller.setContent(
      tracks: widget.tracks,
      waypoints: widget.waypoints,
      start: widget.start,
      end: widget.end,
    );
    await _controller.attachStyle(style);
    // **Le cadrage part d'ici**, et de nulle part ailleurs : c'est le seul
    // moment où la vue native a une taille et un style. Plus de
    // `Future.delayed`.
    await _fit();
  }

  @override
  Widget build(BuildContext context) {
    final PdlMapPoint? center = widget.initialCenter;

    return MapLibreMap(
      options: MapOptions(
        initCenter: center == null
            ? null
            : Geographic(lon: center.lon, lat: center.lat),
        initZoom: widget.initialZoom,
        initStyle: widget.styleUrl,
        gestures: widget.gestures,
      ),
      onMapCreated: _controller.attachMap,
      onEvent: _onEvent,
      children: <Widget>[
        if (widget.kmMarkers.isNotEmpty || widget.waypoints.isNotEmpty)
          WidgetLayer(
            markers: <Marker>[
              // Les bornes kilométriques d'abord, les points de passage
              // ensuite : c'est l'ordre de la liste qui arbitre le
              // recouvrement, donc **les bornes cèdent le pas aux points de
              // passage nommés**, comme le veut le §1.3.2.
              for (final PdlKmMarker m in widget.kmMarkers)
                Marker(
                  point: Geographic(lon: m.lon, lat: m.lat),
                  size: const Size(30, 30),
                  child: _KmBadge(label: m.label),
                ),
              for (final PdlMapPoint w in widget.waypoints)
                if (w.label != null && w.label!.isNotEmpty)
                  Marker(
                    point: Geographic(lon: w.lon, lat: w.lat),
                    size: const Size(160, 26),
                    child: _WaypointLabel(label: w.label!),
                  ),
            ],
          ),
        ...widget.overlays,
      ],
    );
  }
}

/// Borne kilométrique — pastille **opaque**, jamais du texte nu sur la tuile.
class _KmBadge extends StatelessWidget {
  const _KmBadge({required this.label});

  final String label;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    return DecoratedBox(
      decoration: BoxDecoration(
        color: c.overlaySolid,
        shape: BoxShape.circle,
        border: Border.all(color: c.border, width: 1.5),
      ),
      child: Center(
        child: Text(
          label,
          textAlign: TextAlign.center,
          style: context.pdlText.mono.copyWith(color: c.text),
        ),
      ),
    );
  }
}

/// Étiquette d'un point de passage : pastille ambre plus le nom, sur une
/// pilule opaque.
class _WaypointLabel extends StatelessWidget {
  const _WaypointLabel({required this.label});

  final String label;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    return Center(
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
        decoration: BoxDecoration(
          color: c.overlaySolid,
          borderRadius: PdlRadii.pillAll,
          border: Border.all(color: c.borderSubtle),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            Container(
              width: 8,
              height: 8,
              decoration: BoxDecoration(
                color: c.mapWaypoint,
                shape: BoxShape.circle,
              ),
            ),
            const SizedBox(width: 6),
            Flexible(
              child: Text(
                label,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                style: context.pdlText.xs.copyWith(color: c.text),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
