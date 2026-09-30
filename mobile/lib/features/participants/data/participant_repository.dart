import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';
import '../domain/participant_query.dart';

final participantRepositoryProvider = Provider<ParticipantRepository>((ref) {
  return ParticipantRepository(
    ref.watch(ridesClientProvider),
    ref.watch(tripsClientProvider),
  );
});

/// La liste complète des participants, lue page par page et cherchée **côté
/// serveur** : le détail d'une sortie ou d'un voyage n'en embarque que les
/// premiers (ledger `API-12`).
class ParticipantRepository {
  ParticipantRepository(this._ridesClient, this._tripsClient);

  final RidesClient _ridesClient;
  final TripsClient _tripsClient;

  Future<PageResult<PublicUserDto>> fetchPage(
    ParticipantQuery query, {
    required int page,
    required int size,
  }) async {
    final String search = query.search.trim();
    final ParticipantListResponse response = switch (query.source) {
      RideParticipantSource(:final teamSlug, :final rideSlug, :final groupId) =>
        await _ridesClient.getRideParticipants(
          teamSlug: teamSlug,
          rideSlug: rideSlug,
          groupId: groupId,
          search: search.isEmpty ? null : search,
          page: page,
          size: size,
        ),
      TripParticipantSource(:final teamSlug, :final tripSlug) =>
        await _tripsClient.getTripParticipants(
          teamSlug: teamSlug,
          tripSlug: tripSlug,
          search: search.isEmpty ? null : search,
          page: page,
          size: size,
        ),
    };
    return PageResult<PublicUserDto>(
      items: response.participants,
      total: response.total,
    );
  }
}
