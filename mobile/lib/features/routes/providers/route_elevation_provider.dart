import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../core/pdl/elevation/elevation_samples.dart';
import '../data/elevation_profile_repository.dart';
import '../domain/elevation_samples_mapper.dart';

/// Le profil d'un parcours, **prêt à peindre**.
///
/// Un cran au-dessus d'[elevationProfileProvider], qui rend le DTO : ici
/// l'agrégation vers ~76 barres est faite une fois et **mémorisée avec la
/// réponse**. Sans ce palier, chaque `build` reconstruirait un
/// [ElevationSamples] neuf, que le painter comparerait par identité et jugerait
/// donc toujours différent — le profil se repeindrait à chaque frame de
/// glissement du réticule, ce que toute l'architecture à deux couches cherche
/// précisément à éviter.
///
/// La clé porte le nombre d'échantillons, quantifié par
/// [ElevationProfileKey.forWidth] : deux largeurs voisines partagent une
/// entrée, changer de groupe sur la même largeur en crée une par parcours.
final routeElevationSamplesProvider =
    Provider.family<AsyncValue<ElevationSamples>, ElevationProfileKey>((
      Ref ref,
      ElevationProfileKey key,
    ) {
      return ref
          .watch(elevationProfileProvider(key))
          .whenData((ElevationProfileDto dto) => dto.toSamples());
    }, isAutoDispose: true);
