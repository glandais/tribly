import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/config/config_provider.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../providers/ride_detail_provider.dart';
import '../../providers/ride_group_selection_provider.dart';

/// La hauteur de la carte de sortie.
///
/// La maquette la fige à 300 px ; le brief la veut proportionnelle, et c'est
/// lui qui l'emporte (§1.0.4) : 300 px fixes mangent 40 % d'un iPhone SE, et
/// laissent une bande vide sur un grand écran.
double rideMapHeight(double screenHeight) =>
    (screenHeight * 0.44).clamp(260.0, 460.0);

/// La carte multi-tracés de l'écran 12, et la moitié cartographique de la
/// sélection croisée.
///
/// **Une couche par groupe**, toutes adossées à une même source : le SDK
/// MapLibre rend `queryLayers` sans les propriétés de l'entité touchée, donc
/// seul le `layerId` porte l'identité d'un tracé (§1.0.3-7). `PdlMap` s'en
/// charge ; ce widget lui donne les tracés, la couleur de chacun, et reçoit le
/// tap.
///
/// Les géométries sont **dédoublonnées par `routeSlug`** en amont : dix groupes
/// sur trois parcours coûtent trois appels.
class RideGroupsMap extends ConsumerWidget {
  const RideGroupsMap({
    super.key,
    required this.rideKey,
    required this.ride,
    required this.selectedGroupId,
    required this.onSelect,
  });

  final RideKey rideKey;
  final RideDto ride;
  final String? selectedGroupId;
  final ValueChanged<String> onSelect;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final double height = rideMapHeight(MediaQuery.sizeOf(context).height);
    final Brightness brightness = Theme.of(context).brightness;
    final AsyncValue<ResolvedMapStyle?> style = ref.watch(
      mapStyleProvider(brightness),
    );

    final List<String> slugs = distinctRouteSlugs(ride);
    if (slugs.isEmpty) {
      return PdlCard(
        child: PdlEmptyState(
          variant: PdlEmptyVariant.empty,
          icon: PdlIcons.route,
          title: 'rides.noRouteTitle'.tr(),
          message: 'rides.noRouteMessage'.tr(),
        ),
      );
    }

    // Une entrée de cache par parcours distinct, et une seule.
    final Map<String, RouteDetailDto> geometries = <String, RouteDetailDto>{};
    for (final String slug in slugs) {
      final RouteDetailDto? route = ref
          .watch(
            rideRouteGeometryProvider(
              RouteRef(teamSlug: ride.team.slug, routeSlug: slug),
            ),
          )
          .value;
      if (route != null) geometries[slug] = route;
    }

    final String? styleUrl = style.value?.url;
    if (styleUrl == null) {
      return PdlSkeleton(height: height, borderRadius: PdlRadii.mdAll);
    }

    final List<PdlMapTrack> tracks = _tracks(geometries);
    final RideGroupDto? selected = _selectedGroup();

    return SizedBox(
      height: height,
      child: PdlMap(
        styleUrl: styleUrl,
        tracks: tracks,
        start: _point(ride.startPlace),
        end: _point(ride.endPlace),
        initialCenter: _defaultCenter(ref),
        selectedTrackId: selectedGroupId,
        onTrackSelected: (String? id) {
          if (id != null) onSelect(id);
        },
        overlays: <Widget>[
          if (selected != null)
            Positioned(
              left: 12,
              top: 12,
              child: PdlMapPill(
                label: selected.name,
                leading: PdlColorTrack(
                  color: multiTrackColor(selected.sortOrder),
                  shape: PdlColorTrackShape.legend,
                ),
              ),
            ),
        ],
      ),
    );
  }

  /// Un tracé par groupe — **pas** un par parcours : deux groupes qui partagent
  /// un parcours doivent chacun être sélectionnables, et la carte de groupe
  /// sélectionnée doit s'allumer quel que soit le tracé touché.
  List<PdlMapTrack> _tracks(Map<String, RouteDetailDto> geometries) {
    final List<PdlMapTrack> tracks = <PdlMapTrack>[];
    for (final RideGroupDto group in ride.groups) {
      final String? slug = group.routeSlug ?? ride.routeSlug;
      final RouteDetailDto? route = slug == null ? null : geometries[slug];
      if (route == null) continue;
      tracks.add(
        PdlMapTrack(
          id: group.id,
          lines: <List<List<double>>>[
            for (final TrackDto t in route.tracks) t.line.coordinates,
          ],
          color: multiTrackColor(group.sortOrder),
          label: group.name,
        ),
      );
    }
    // Le tracé sélectionné est dessiné **en dernier** : MapLibre n'a pas de
    // `zIndex` sur les lignes, c'est l'ordre d'ajout qui décide.
    final int index = tracks.indexWhere(
      (PdlMapTrack t) => t.id == selectedGroupId,
    );
    if (index >= 0) tracks.add(tracks.removeAt(index));
    return tracks;
  }

  RideGroupDto? _selectedGroup() {
    for (final RideGroupDto g in ride.groups) {
      if (g.id == selectedGroupId) return g;
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
