import '../../../api/generated/export.dart';
import '../../../core/pdl/elevation/elevation_samples.dart';

/// La frontière entre le contrat et la bibliothèque de composants.
///
/// `core/pdl` ne connaît aucun DTO (contrat du module, §3) : c'est ici que
/// `ElevationProfileDto` devient un [ElevationSamples], et nulle part ailleurs.
extension ElevationProfileDtoSamples on ElevationProfileDto {
  /// Agrège le profil en barres prêtes à peindre.
  ///
  /// `distance`, `minElevation` et `maxElevation` sont **repris du serveur**,
  /// pas recalculés depuis les points : l'échantillonnage peut avoir manqué le
  /// sommet, et une échelle qui bouge d'un rechargement à l'autre se voit.
  ///
  /// Le résultat est immuable et comparé par identité par le painter : gardez
  /// l'instance, ne la reconstruisez pas à chaque `build`.
  ElevationSamples toSamples({int targetBars = kElevationBarTarget}) =>
      ElevationSamples.fromPoints(
        <ElevationPoint>[
          for (final ElevationPointDto p in points)
            // `grade` est la pente du segment **se terminant** sur le point ;
            // celle du premier vaut 0 par contrat et n'est jamais lue par
            // l'agrégation, qui ne parcourt que les segments.
            ElevationPoint(
              distance: p.distance,
              elevation: p.elevation,
              grade: p.grade,
            ),
        ],
        targetBars: targetBars,
        totalDistance: distance,
        minElevation: minElevation,
        maxElevation: maxElevation,
      );
}
