import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/rides/presentation/widgets/ride_groups_map.dart';
import 'package:pedalons/features/rides/providers/ride_group_selection_provider.dart';

import 'ride_fixtures.dart';

/// S12-4 — ce que la carte de sortie doit demander, et ce qu'elle doit mesurer.
void main() {
  test('dix groupes sur trois parcours ne font que trois appels', () {
    final RideDto ride = fixtureRide(
      groups: <RideGroupDto>[
        for (int i = 0; i < 10; i++)
          fixtureGroup(
            id: 'g$i',
            sortOrder: i,
            // Trois parcours partagés, comme une sortie réelle : les groupes
            // se répartissent sur un court, un moyen et un long.
            routeSlug: 'parcours-${i % 3}',
          ),
      ],
    );

    expect(distinctRouteSlugs(ride), <String>[
      'parcours-0',
      'parcours-1',
      'parcours-2',
    ]);
  });

  test('un groupe sans parcours reprend celui de la sortie', () {
    final RideDto ride = fixtureRide(
      groups: <RideGroupDto>[fixtureGroup(id: 'g1')],
    ).copyWith(routeSlug: 'parcours-de-la-sortie');

    expect(distinctRouteSlugs(ride), <String>['parcours-de-la-sortie']);
  });

  test('une sortie sans aucun parcours ne demande rien', () {
    expect(distinctRouteSlugs(fixtureRide()), isEmpty);
  });

  test('la hauteur de carte est bornée, jamais figée à 300', () {
    // iPhone SE — 300 px fixes mangeraient 40 % de l'écran.
    expect(rideMapHeight(667), 293.48);
    // Petit écran : le plancher tient.
    expect(rideMapHeight(500), 260);
    // Grande tablette : le plafond tient.
    expect(rideMapHeight(1366), 460);
  });
}
