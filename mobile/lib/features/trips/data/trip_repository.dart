import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final tripRepositoryProvider = Provider<TripRepository>((ref) {
  return TripRepository(ref.watch(tripsClientProvider));
});

class TripRepository {
  final TripsClient _tripsClient;

  TripRepository(this._tripsClient);

  Future<TripDto> getTrip(String teamSlug, String tripSlug) {
    return _tripsClient.getTrip(teamSlug: teamSlug, tripSlug: tripSlug);
  }

  Future<TripParticipationDto> joinTrip(String teamSlug, String tripSlug) {
    return _tripsClient.joinTrip(teamSlug: teamSlug, tripSlug: tripSlug);
  }

  Future<void> leaveTrip(String teamSlug, String tripSlug) {
    return _tripsClient.leaveTrip(teamSlug: teamSlug, tripSlug: tripSlug);
  }
}
