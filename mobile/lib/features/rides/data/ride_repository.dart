import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final rideRepositoryProvider = Provider<RideRepository>((ref) {
  return RideRepository(ref.watch(ridesClientProvider));
});

class RideRepository {
  final RidesClient _ridesClient;

  RideRepository(this._ridesClient);

  /// Le détail d'une sortie, groupes peuplés.
  ///
  /// C'est la **seule** source de `groups[]` : une ligne de liste renvoie
  /// `List.of()` côté serveur (`RideDto.fromListItem`), donc rien ne se dérive
  /// d'un item de fil.
  Future<RideDto> getRide(String teamSlug, String rideSlug) {
    return _ridesClient.getRide(teamSlug: teamSlug, rideSlug: rideSlug);
  }

  /// Rejoindre un groupe.
  Future<RideParticipationDto> joinGroup(
    String teamSlug,
    String rideSlug,
    String groupId,
  ) {
    return _ridesClient.joinGroup(
      teamSlug: teamSlug,
      rideSlug: rideSlug,
      groupId: groupId,
    );
  }

  /// Quitter un groupe. Un **seul** appel : `registeredGroupId` désigne le
  /// groupe à quitter, il n'y a jamais à boucler sur `groups[]`.
  Future<void> leaveGroup(String teamSlug, String rideSlug, String groupId) {
    return _ridesClient.leaveGroup(
      teamSlug: teamSlug,
      rideSlug: rideSlug,
      groupId: groupId,
    );
  }
}
