import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/config/config_provider.dart';
import '../../../../core/config/map_style_options.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../providers/trip_detail_provider.dart';
import '../../providers/trip_tracks_provider.dart';

/// Hauteur de la carte interactive du voyage.
const double _kTripMapHeight = 280;

/// La carte multi-étapes, et la moitié cartographique de la sélection croisée.
///
/// **Une couche par étape**, toutes adossées à la même source : `queryLayers`
/// rend le `layerId` sans les propriétés de l'entité touchée, si bien que seul
/// le nom de la couche porte l'identité d'un tracé (§1.0.3-7). Sélectionner ne
/// reconstruit donc pas la carte — `PdlMap` repeint deux couches.
///
/// La carte se **remplit progressivement** : le hero porte la vignette statique
/// pendant ce temps, et les étapes apparaissent dans l'ordre du voyage.
class TripMap extends ConsumerWidget {
  const TripMap({
    super.key,
    required this.tripKey,
    required this.trip,
    required this.selectedStageId,
    required this.onSelect,
  });

  final TripKey tripKey;
  final TripDto trip;
  final String? selectedStageId;
  final ValueChanged<String> onSelect;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final Brightness brightness = Theme.of(context).brightness;
    final AsyncValue<ResolvedMapStyle?> style = ref.watch(
      mapStyleProvider(brightness),
    );
    final TripTracksState tracks = ref.watch(tripTracksProvider(tripKey));
    final List<TripStageDto> stages = trip.orderedStages;

    // Un voyage sans tracé ne rend pas une carte vide : il ne rend pas de
    // carte. Le bloc entier disparaît (§4.1).
    if (stages.every((TripStageDto s) => s.route == null)) {
      return const SizedBox.shrink();
    }

    final ResolvedMapStyle? resolved = style.value;
    final List<PdlMapTrack> mapTracks = _tracks(stages, tracks);
    final TripStageDto? selected = _selectedStage(stages);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 0, 16, 8),
          child: Text('trips.tripTrack'.tr(), style: t.sectionTitle),
        ),
        SizedBox(
          height: _kTripMapHeight,
          child: resolved == null
              ? PdlSkeleton(
                  height: _kTripMapHeight,
                  borderRadius: PdlRadii.mdAll,
                )
              : PdlMapHero(
                  height: _kTripMapHeight,
                  labels: PdlMapHeroLabels(
                    enterFullscreen: 'map.fullscreen'.tr(),
                    exitFullscreen: 'map.exitFullscreen'.tr(),
                    chooseBackground: 'map.chooseBackground'.tr(),
                    backgroundSheetTitle: 'map.background'.tr(),
                  ),
                  topScrim: false,
                  styles: servedMapStyleOptions(ref),
                  selectedStyleId: resolved.style.id,
                  onStyleSelected: (String id) =>
                      ref.read(mapStyleIdProvider.notifier).select(id),
                  pill: selected == null
                      ? null
                      : PdlMapPill(
                          label: _pillLabel(selected),
                          leading: PdlColorTrack(
                            color: multiTrackColor(selected.paletteIndex),
                            shape: PdlColorTrackShape.legend,
                          ),
                        ),
                  mapBuilder: (BuildContext context) => PdlMap(
                    styleUrl: resolved.url,
                    tracks: mapTracks,
                    start: _point(stages.first.startPlace),
                    end: _point(stages.last.endPlace),
                    initialCenter: _defaultCenter(ref),
                    fitBox: PdlMapBox.ofTracks(mapTracks),
                    selectedTrackId: selectedStageId,
                    onTrackSelected: (String? id) {
                      if (id != null) onSelect(id);
                    },
                  ),
                ),
        ),
        if (stages.length > 1)
          Padding(
            padding: const EdgeInsets.only(top: 8),
            child: PdlLegendRow(
              entries: <PdlLegendEntry>[
                for (final TripStageDto s in stages)
                  PdlLegendEntry(
                    color: multiTrackColor(s.paletteIndex),
                    label: s.name,
                    onTap: () => onSelect(s.id),
                  ),
              ],
              activeIndex: stages.indexWhere(
                (TripStageDto s) => s.id == selectedStageId,
              ),
            ),
          ),
        if (tracks.truncated || tracks.failed.isNotEmpty)
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
            child: Row(
              children: <Widget>[
                Icon(PdlIcons.info, size: 15, color: c.textDimmed),
                const SizedBox(width: 6),
                Expanded(
                  child: Text(
                    tracks.truncated
                        ? 'trips.partialTrack'.tr(
                            namedArgs: <String, String>{
                              'shown': '${tracks.requested}',
                              'total': '${tracks.total}',
                            },
                          )
                        : 'trips.missingTracks'.tr(
                            namedArgs: <String, String>{
                              'count': '${tracks.failed.length}',
                            },
                          ),
                    style: t.xs,
                  ),
                ),
                if (tracks.failed.isNotEmpty)
                  PdlButton(
                    label: 'common.retry'.tr(),
                    variant: PdlButtonVariant.text,
                    size: PdlButtonSize.sm,
                    onPressed: ref
                        .read(tripTracksProvider(tripKey).notifier)
                        .retryFailed,
                  ),
              ],
            ),
          ),
      ],
    );
  }

  /// Un tracé par **étape**, pas par parcours : deux étapes qui partagent un
  /// parcours doivent chacune être sélectionnables, et chacune porte la couleur
  /// de son rang.
  List<PdlMapTrack> _tracks(List<TripStageDto> stages, TripTracksState state) {
    final List<PdlMapTrack> tracks = <PdlMapTrack>[];
    for (final TripStageDto stage in stages) {
      final String? slug = stage.route?.slug;
      final RouteDetailDto? route = slug == null
          ? null
          : state.geometries[slug];
      if (route == null) continue;
      tracks.add(
        PdlMapTrack(
          id: stage.id,
          lines: <List<List<double>>>[
            for (final TrackDto t in route.tracks) t.line.coordinates,
          ],
          color: multiTrackColor(stage.paletteIndex),
          label: stage.name,
        ),
      );
    }
    // L'étape sélectionnée est dessinée **en dernier** : MapLibre n'a pas de
    // `zIndex` sur les lignes, c'est l'ordre d'ajout qui décide.
    final int index = tracks.indexWhere(
      (PdlMapTrack t) => t.id == selectedStageId,
    );
    if (index >= 0) tracks.add(tracks.removeAt(index));
    return tracks;
  }

  /// « J1 · Clermont-Ferrand → Issoire ».
  String _pillLabel(TripStageDto stage) {
    final String? from = stage.startPlace?.name;
    final String? to = stage.endPlace?.name;
    if (from == null || to == null) return stage.name;
    return '${stage.name} · $from → $to';
  }

  TripStageDto? _selectedStage(List<TripStageDto> stages) {
    for (final TripStageDto s in stages) {
      if (s.id == selectedStageId) return s;
    }
    return null;
  }

  PdlMapPoint? _point(PlaceDetailDto? place) {
    final List<double>? c = place?.geometry?.coordinates;
    if (c == null || c.length < 2) return null;
    return PdlMapPoint(lon: c[0], lat: c[1], label: place!.name);
  }

  PdlMapPoint? _defaultCenter(WidgetRef ref) {
    final MapCenterDto? center = ref.watch(defaultCenterProvider).value;
    if (center == null) return null;
    return PdlMapPoint(lon: center.lon, lat: center.lat);
  }
}
