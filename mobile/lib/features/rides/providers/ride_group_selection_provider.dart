import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../routes/data/route_repository.dart';
import 'ride_detail_provider.dart';

/// Le groupe pointé, **un seul pour tout l'écran**.
///
/// Il pilote simultanément la carte de groupe sélectionnée, la pilule posée sur
/// la carte, l'entrée active de la légende, l'épaisseur et l'opacité des
/// tracés, et la source du profil altimétrique. Un état unique, parce que cinq
/// états séparés se désynchronisent au premier tap.
///
/// `null` signifie « pas encore choisi » : l'écran initialise à
/// `registeredGroupId ?? groups.first.id` une fois le détail chargé, ce qui
/// place d'emblée le lecteur sur *son* groupe.
final selectedRideGroupProvider = StateProvider.autoDispose
    .family<String?, RideKey>((Ref ref, RideKey key) => null);

/// Les tracés d'une sortie, **dédoublonnés par `routeSlug`**.
///
/// Dix groupes partagent souvent trois parcours : sans déduplication, la carte
/// déclencherait dix `getRoute` pour trois géométries. La clé du cache est le
/// slug, pas le groupe.
///
/// `simplify: 15` / `points: 1500` : un aperçu de sortie n'a pas besoin de la
/// précision du mètre, et dix tracés pleins tiendraient plusieurs mégaoctets.
final rideRouteGeometryProvider =
    FutureProvider.family<RouteDetailDto, RouteRef>((
      Ref ref,
      RouteRef route,
    ) async {
      return ref
          .watch(routeRepositoryProvider)
          .getRouteDetail(
            route.teamSlug,
            route.routeSlug,
            simplify: 15,
            points: 1500,
          );
    }, isAutoDispose: true);

/// Un parcours dans son équipe — la clé de cache des géométries.
class RouteRef {
  const RouteRef({required this.teamSlug, required this.routeSlug});

  final String teamSlug;
  final String routeSlug;

  @override
  bool operator ==(Object other) =>
      other is RouteRef &&
      other.teamSlug == teamSlug &&
      other.routeSlug == routeSlug;

  @override
  int get hashCode => Object.hash(teamSlug, routeSlug);

  @override
  String toString() => 'RouteRef($teamSlug/$routeSlug)';
}

/// Les `routeSlug` distincts d'une sortie, dans l'ordre des groupes.
///
/// Le parcours de la sortie elle-même sert de repli aux groupes qui n'en
/// portent pas : c'est la même règle que le bouton « Voir le parcours ».
List<String> distinctRouteSlugs(RideDto ride) {
  final List<String> slugs = <String>[];
  for (final RideGroupDto group in ride.groups) {
    final String? slug = group.routeSlug ?? ride.routeSlug;
    if (slug != null && !slugs.contains(slug)) slugs.add(slug);
  }
  if (slugs.isEmpty && ride.routeSlug != null) slugs.add(ride.routeSlug!);
  return slugs;
}
