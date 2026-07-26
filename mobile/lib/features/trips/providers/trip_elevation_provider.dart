import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pdl/elevation/elevation_samples.dart';
import 'trip_detail_provider.dart';
import 'trip_tracks_provider.dart';

/// Budget total d'échantillons du profil global.
///
/// Il n'y a **pas d'endpoint de profil multi-parcours** : le profil d'un voyage
/// est la concaténation de N profils d'étape. Demander 300 points par étape en
/// ferait 2 100 sur sept étapes, pour un graphique de 350 px agrégé à
/// ~76 barres. Le budget est donc global et **réparti proportionnellement à la
/// distance** : une étape de 100 km reçoit plus de points qu'une de 40 km, ce
/// qui donne une résolution constante le long de l'axe plutôt que par étape.
const int kTripElevationBudget = 240;

/// Le profil global d'un voyage, et ce qui lui manque.
@immutable
class TripElevation {
  const TripElevation({required this.samples, required this.gaps});

  final ElevationSamples samples;

  /// Les étapes dont le profil n'a pas pu être chargé. Le graphique les
  /// enjambe ; le rendre sans le dire ferait passer une traversée trouée pour
  /// une traversée complète, et fausserait la lecture du dénivelé.
  final List<String> gaps;

  bool get hasGaps => gaps.isNotEmpty;
}

/// Le profil altimétrique global, concaténé.
///
/// Chaque segment est **décalé de la distance cumulée** des étapes qui le
/// précèdent, si bien que l'axe court de 0 à la distance totale du voyage.
///
/// `autoDispose` : lié à l'écran ouvert, comme tout profil (§1.3.1).
final tripElevationProvider = FutureProvider.autoDispose
    .family<TripElevation?, TripKey>((Ref ref, TripKey key) async {
      final TripDto trip = await ref.watch(tripDetailProvider(key).future);

      // Le même plafond que la carte : au-delà, le profil couvre ce que la
      // carte couvre, et pas davantage.
      final List<TripStageDto> stages = trip.orderedStages
          .where((TripStageDto s) => s.route != null)
          .take(kTripTrackStageCap)
          .toList();
      if (stages.isEmpty) return null;

      final double total = stages.fold<double>(
        0,
        (double sum, TripStageDto s) => sum + s.route!.distance,
      );
      if (total <= 0) return null;

      final RoutesClient client = ref.watch(routesClientProvider);

      Future<ElevationProfileDto?> fetch(TripStageDto stage) async {
        // Chaque étape reçoit sa part du budget, jamais moins de deux points —
        // le serveur borne de toute façon à `2..1000` et réduit au nombre de
        // points réellement stockés.
        final int samples =
            (kTripElevationBudget * stage.route!.distance / total)
                .round()
                .clamp(2, 300);
        try {
          return await client.getRouteElevationProfile(
            teamSlug: key.teamSlug,
            routeSlug: stage.route!.slug,
            samples: samples,
          );
        } catch (_) {
          return null;
        }
      }

      // Même pool que les géométries : quatre requêtes en vol au plus.
      final List<ElevationProfileDto?> profiles =
          List<ElevationProfileDto?>.filled(stages.length, null);
      int cursor = 0;
      Future<void> worker() async {
        while (true) {
          if (cursor >= stages.length) return;
          final int index = cursor++;
          profiles[index] = await fetch(stages[index]);
        }
      }

      await Future.wait(<Future<void>>[
        for (int i = 0; i < kTripTrackConcurrency && i < stages.length; i++)
          worker(),
      ]);

      final List<ElevationPoint> points = <ElevationPoint>[];
      final List<String> gaps = <String>[];
      double offset = 0;

      for (int i = 0; i < stages.length; i++) {
        final TripStageDto stage = stages[i];
        final ElevationProfileDto? profile = profiles[i];
        if (profile == null || profile.points.isEmpty) {
          // Le trou est **enjambé, pas masqué** : l'axe avance quand même de la
          // distance de l'étape, sans quoi les étapes suivantes se
          // retrouveraient au mauvais kilomètre.
          gaps.add(stage.name);
          offset += stage.route!.distance;
          continue;
        }
        for (int p = 0; p < profile.points.length; p++) {
          // Le premier point d'une étape coïncide avec le dernier de la
          // précédente : le garder créerait un segment de longueur nulle, que
          // l'agrégation en barres compte comme une pente indéfinie.
          if (p == 0 && points.isNotEmpty) continue;
          final ElevationPointDto point = profile.points[p];
          points.add(
            ElevationPoint(
              distance: offset + point.distance,
              elevation: point.elevation,
              grade: p == 0 ? null : point.grade,
            ),
          );
        }
        offset += profile.distance;
      }

      if (points.length < 2) return null;

      return TripElevation(
        samples: ElevationSamples.fromPoints(
          points,
          // `totalDistance` du voyage prime : c'est lui que porte l'axe, et il
          // reste juste même quand une étape manque.
          totalDistance: trip.totalDistance ?? offset,
        ),
        gaps: gaps,
      );
    });
