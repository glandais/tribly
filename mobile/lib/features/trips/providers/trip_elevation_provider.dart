import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../core/pdl/elevation/elevation_samples.dart';
import '../../routes/domain/route_elevation_builder.dart';
import 'trip_detail_provider.dart';
import 'trip_tracks_provider.dart';

/// Le profil global d'un voyage, et ce qui lui manque.
@immutable
class TripElevation {
  const TripElevation({required this.samples, required this.gaps});

  final ElevationSamples samples;

  /// Les étapes dont la géométrie n'a pas pu être chargée. Le graphique les
  /// enjambe ; le rendre sans le dire ferait passer une traversée trouée pour
  /// une traversée complète, et fausserait la lecture du dénivelé.
  final List<String> gaps;

  bool get hasGaps => gaps.isNotEmpty;
}

/// Le profil altimétrique global, concaténé.
///
/// **Aucune requête à lui.** Il lit les géométries que [tripTracksProvider] a
/// déjà chargées en un seul appel groupé pour la carte, et en tire le profil :
/// les coordonnées du contrat portent l'altitude et le cumul, donc tout est
/// déjà là. Avant, cet écran faisait un appel de profil **par étape** — jusqu'à
/// douze, avec un pool de quatre et un budget d'échantillons réparti au
/// prorata de la distance — pour reconstruire une donnée qu'il tenait en
/// mémoire à une résolution moindre.
///
/// Chaque étape est **décalée de la distance cumulée** de celles qui la
/// précèdent, si bien que l'axe court de 0 à la distance totale du voyage.
/// L'agrégation en barres n'a lieu **qu'une fois, sur l'ensemble** : agréger
/// étape par étape donnerait des barres de largeurs différentes selon la
/// longueur de l'étape.
final tripElevationProvider =
    Provider.family<AsyncValue<TripElevation?>, TripKey>((
      Ref ref,
      TripKey key,
    ) {
      final AsyncValue<TripDto> detail = ref.watch(tripDetailProvider(key));
      final TripDto? trip = detail.value;
      if (trip == null) {
        return detail.hasError
            ? AsyncValue<TripElevation?>.error(
                detail.error!,
                detail.stackTrace!,
              )
            : const AsyncValue<TripElevation?>.loading();
      }

      final TripTracksState tracks = ref.watch(tripTracksProvider(key));

      // Le même plafond que la carte : au-delà, le profil couvre ce que la
      // carte couvre, et pas davantage.
      final List<TripStageDto> stages = trip.orderedStages
          .where((TripStageDto s) => s.route != null)
          .take(kTripTrackStageCap)
          .toList();
      if (stages.isEmpty) return const AsyncValue<TripElevation?>.data(null);

      // Tant que le lot n'a pas fini, on ne montre pas un profil amputé qui se
      // compléterait ensuite : le graphique bougerait sous l'œil.
      if (tracks.isLoading) return const AsyncValue<TripElevation?>.loading();

      final List<ElevationPoint> points = <ElevationPoint>[];
      final List<String> gaps = <String>[];
      double offset = 0;

      for (final TripStageDto stage in stages) {
        final RouteDetailDto? geometry = tracks.geometries[stage.route!.slug];
        final List<ElevationPoint>? stagePoints = geometry?.elevationPoints;
        if (stagePoints == null) {
          // Le trou est **enjambé, pas masqué** : l'axe avance quand même de la
          // distance de l'étape, sans quoi les étapes suivantes se
          // retrouveraient au mauvais kilomètre.
          gaps.add(stage.name);
          offset += stage.route!.distance;
          continue;
        }
        for (int p = 0; p < stagePoints.length; p++) {
          // Le premier point d'une étape coïncide avec le dernier de la
          // précédente : le garder créerait un segment de longueur nulle, que
          // l'agrégation en barres compte comme une pente indéfinie.
          if (p == 0 && points.isNotEmpty) continue;
          final ElevationPoint point = stagePoints[p];
          points.add(
            ElevationPoint(
              distance: offset + point.distance,
              elevation: point.elevation,
              grade: p == 0 ? null : point.grade,
            ),
          );
        }
        offset += stagePoints.last.distance;
      }

      if (points.length < 2) {
        return const AsyncValue<TripElevation?>.data(null);
      }

      return AsyncValue<TripElevation?>.data(
        TripElevation(
          samples: ElevationSamples.fromPoints(
            points,
            // `totalDistance` du voyage prime : c'est lui que porte l'axe, et
            // il reste juste même quand une étape manque.
            totalDistance: trip.totalDistance ?? offset,
          ),
          gaps: gaps,
        ),
      );
    }, isAutoDispose: true);
