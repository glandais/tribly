import 'package:dio/dio.dart';

import 'backend_client.dart';

/// Reads of the profile's settings, as the server keeps them.
extension ProfileSeed on BackendClient {
  /// `GET /api/users/me` as [who]: the display name and the preferences (`unitSystem`, `theme`,
  /// `language`, `contactableByMembers`).
  Future<Json> profile(TestUser who) => get(who, '/api/users/me');

  /// [who]'s latest data export, or null when none was ever requested (204).
  Future<Json?> latestExport(TestUser who) async {
    final response = await http.get<Json>(
      '/api/users/me/export',
      options: Options(headers: {'Authorization': 'Bearer ${who.accessToken}'}),
    );
    return response.statusCode == 204 ? null : response.data;
  }
}
