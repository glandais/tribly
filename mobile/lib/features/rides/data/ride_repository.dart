import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/tribly_api_client.dart';

final rideRepositoryProvider = Provider<RideRepository>((ref) {
  return RideRepository(
    ref.watch(dioProvider),
    ref.watch(ridesClientProvider),
  );
});

class RideRepository {
  final Dio _dio;
  final RidesClient _ridesClient;

  RideRepository(this._dio, this._ridesClient);

  /// Get upcoming rides for a team
  /// Note: Uses Dio directly because the listing endpoint isn't in the generated client
  Future<List<RideDto>> getTeamRides(
    String teamSlug, {
    int page = 0,
    int size = 20,
    bool upcoming = true,
  }) async {
    final response = await _dio.get(
      '/api/teams/$teamSlug/rides',
      queryParameters: {
        'page': page,
        'size': size,
        if (upcoming) 'upcoming': true,
      },
    );
    final data = response.data;
    if (data is Map && data['rides'] != null) {
      return (data['rides'] as List)
          .map((e) => RideDto.fromJson(e as Map<String, Object?>))
          .toList();
    }
    if (data is Map && data['content'] != null) {
      return (data['content'] as List)
          .map((e) => RideDto.fromJson(e as Map<String, Object?>))
          .toList();
    }
    return (data as List)
        .map((e) => RideDto.fromJson(e as Map<String, Object?>))
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
