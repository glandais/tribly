import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/tribly_api_client.dart';

final rideRepositoryProvider = Provider<RideRepository>((ref) {
  return RideRepository(
    ref.watch(ridesClientProvider),
    ref.watch(publicationsClientProvider),
  );
});

class RideRepository {
  final RidesClient _ridesClient;
  final PublicationsClient _publicationsClient;

  RideRepository(this._ridesClient, this._publicationsClient);

  /// Get upcoming rides for a team via the publications endpoint
  Future<List<RideDto>> getTeamRides(
    String teamSlug, {
    int page = 0,
    int size = 20,
    bool upcoming = true,
  }) async {
    final response = await _publicationsClient.listPublications(
      teamSlug: teamSlug,
      type: PublicationType.ride,
      from: upcoming ? DateTime.now().toUtc().toIso8601String() : null,
      page: page,
      size: size,
    );
    return response.publications
        .whereType<PublicationDtoRide>()
        .map((p) => RideDto(
              type: 'RIDE',
              team: p.team,
              id: p.id,
              slug: p.slug,
              name: p.name,
              media: p.media,
              dateTime: p.dateTime,
              status: p.status,
              visibility: p.visibility,
              participantCount: p.participantCount,
              groupCount: p.groupCount,
              groups: p.groups,
              topParticipants: p.topParticipants,
              publishAt: p.publishAt,
              createdAt: p.createdAt,
              routeSlug: p.routeSlug,
              startPlace: p.startPlace,
              endPlace: p.endPlace,
              routeThumbnailLightUrl: p.routeThumbnailLightUrl,
              routeThumbnailDarkUrl: p.routeThumbnailDarkUrl,
            ))
        .toList();
  }

  /// Get ride details
  Future<RideDto> getRide(String teamSlug, String rideSlug) {
    return _ridesClient.getRide(teamSlug: teamSlug, rideSlug: rideSlug);
  }

  /// Join a ride group
  Future<RideParticipationDto> joinRideGroup(
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

  /// Leave a ride group
  Future<void> leaveRideGroup(
    String teamSlug,
    String rideSlug,
    String groupId,
  ) {
    return _ridesClient.leaveGroup(
      teamSlug: teamSlug,
      rideSlug: rideSlug,
      groupId: groupId,
    );
  }
}
