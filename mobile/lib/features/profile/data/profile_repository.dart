import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';

final profileRepositoryProvider = Provider<ProfileRepository>((ref) {
  return ProfileRepository(
    ref.watch(usersClientProvider),
    ref.watch(gpsServicesClientProvider),
    ref.watch(stravaAuthenticationClientProvider),
  );
});

/// Tout ce que l'écran de profil demande au serveur.
///
/// Un seul dépôt plutôt qu'un par section : ce sont dix appels sur le même
/// utilisateur, et les éclater en dix fichiers rendrait plus difficile de voir
/// ce qui est réellement disponible — c'est précisément ce qui a laissé
/// `logout-all` câblé nulle part pendant un an.
class ProfileRepository {
  ProfileRepository(this._users, this._gps, this._strava);

  final UsersClient _users;
  final GpsServicesClient _gps;
  final StravaAuthenticationClient _strava;

  Future<UserDto> updateDisplayName(String displayName) {
    return _users.updateMe(body: UpdateUserRequest(displayName: displayName));
  }

  Future<UserDto> uploadAvatar(String filePath) {
    return _users.uploadAvatar(file: MultipartFile.fromFileSync(filePath));
  }

  Future<UserDto> deleteAvatar() => _users.deleteAvatar();

  Future<void> deleteAccount() => _users.deleteCurrentUser();

  /// L'export RGPD le plus récent, ou `null` quand il n'y en a jamais eu.
  ///
  /// L'absence d'export est un **404**, pas une liste vide : sans ce filet,
  /// l'écran afficherait « Chargement impossible » à tout utilisateur qui n'a
  /// jamais rien demandé, c'est-à-dire à presque tous.
  Future<UserExportDto?> latestExport() async {
    try {
      return await _users.getLatestExport();
    } on DioException catch (error) {
      if (error.response?.statusCode == 404) return null;
      rethrow;
    }
  }

  Future<UserExportDto> requestExport() => _users.requestExport();

  /// Une page de participations, bornée dans le temps.
  ///
  /// `from = maintenant` donne les sorties à venir, `to = maintenant`
  /// l'historique — et c'est le `total` de la réponse qui alimente les deux
  /// compteurs, sans endpoint de comptage.
  Future<PageResult<PublicationDto>> fetchParticipations({
    required bool upcoming,
    required DateTime now,
    int page = 0,
    int size = kDefaultPageSize,
  }) async {
    final String boundary = now.toUtc().toIso8601String();
    final PublicationListResponse response = await _users.listMyParticipations(
      page: page,
      size: size,
      from: upcoming ? boundary : null,
      to: upcoming ? null : boundary,
      view: ListViewMode.compact,
    );
    return PageResult<PublicationDto>(
      items: response.publications,
      total: response.total,
    );
  }

  /// Les services GPS que **le serveur** propose. Jamais une liste codée en
  /// dur : un service ajouté au backend apparaît sans livrer de mobile.
  Future<List<GpsServiceType>> availableGpsServices() =>
      _gps.getAvailableServices();

  Future<String> gpsConnectUrl(GpsServiceType service) async {
    final GpsOAuthUrlResponse response = await _gps.getConnectUrl(
      serviceType: service,
    );
    return response.authorizationUrl;
  }

  Future<void> disconnectGps(GpsServiceType service) =>
      _gps.disconnect(serviceType: service);

  Future<String> stravaConnectUrl() async {
    final StravaAuthUrlResponse response = await _strava.getStravaConnectUrl();
    return response.authorizationUrl;
  }

  Future<void> unlinkStrava() => _strava.unlinkStrava();
}
