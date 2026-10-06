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

  /// La météo du voyage, étape par étape. Toujours 200 pour qui peut lire
  /// le voyage : l'état est dans `TripWeatherDto.status` et, pour chaque
  /// étape, dans `leg.status`. Le serveur ne lit que son cache : l'appel est
  /// bon marché, et le recharger ne « force » aucune prévision.
  Future<TripWeatherDto> getTripWeather(String teamSlug, String tripSlug) {
    return _tripsClient.getTripWeather(teamSlug: teamSlug, tripSlug: tripSlug);
  }

  Future<TripParticipationDto> joinTrip(String teamSlug, String tripSlug) {
    return _tripsClient.joinTrip(teamSlug: teamSlug, tripSlug: tripSlug);
  }

  Future<void> leaveTrip(String teamSlug, String tripSlug) {
    return _tripsClient.leaveTrip(teamSlug: teamSlug, tripSlug: tripSlug);
  }
}
