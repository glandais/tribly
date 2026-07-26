import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:geolocator/geolocator.dart';

import 'polyline_index.dart';

/// Pourquoi une position n'a pas pu être obtenue.
///
/// La distinction n'est pas cosmétique : un service coupé se règle dans les
/// réglages du **système**, un refus définitif dans ceux de l'**application**,
/// et un refus ponctuel se redemande. Les confondre produit soit un dialogue
/// système qui se rouvre en boucle, soit un message qui envoie l'utilisateur
/// au mauvais endroit.
enum LocationFailure {
  /// La localisation est désactivée sur l'appareil.
  serviceDisabled,

  /// L'utilisateur a refusé cette fois-ci. On peut redemander.
  denied,

  /// Refus définitif (ou restriction parentale) : seul un passage par les
  /// réglages de l'app le lèvera. **Ne jamais redemander** — sur iOS le
  /// dialogue ne s'affiche même plus, l'appel échoue silencieusement.
  deniedForever,

  /// Position indisponible malgré la permission (aucun signal, délai dépassé).
  unavailable,
}

/// Le résultat d'une demande de position : une position, ou une raison.
@immutable
class LocationResult {
  const LocationResult.success(LngLat this.position) : failure = null;
  const LocationResult.failed(LocationFailure this.failure) : position = null;

  final LngLat? position;
  final LocationFailure? failure;

  bool get isSuccess => position != null;
}

/// L'accès à la position de l'appareil, réduit à ce dont l'app a besoin.
///
/// L'app ne suit personne : elle demande **une** position **approximative**,
/// et seulement pour trier des parcours par proximité. D'où
/// [LocationAccuracy.low] — `ACCESS_COARSE_LOCATION` seule est déclarée au
/// manifeste — et l'absence de tout flux continu.
class LocationService {
  const LocationService();

  /// Rend une position approximative, ou `null`.
  ///
  /// **Ne lève jamais.** Un filtre de proximité est un confort : l'écran doit
  /// rester utilisable quand il échoue, pas s'arrêter sur une exception.
  Future<LngLat?> coarsePosition({
    Duration timeLimit = const Duration(seconds: 10),
  }) async {
    final LocationResult result = await requestCoarsePosition(
      timeLimit: timeLimit,
    );
    return result.position;
  }

  /// Comme [coarsePosition], mais dit **pourquoi** en cas d'échec — de quoi
  /// composer un message exact plutôt qu'un « position indisponible » générique.
  Future<LocationResult> requestCoarsePosition({
    Duration timeLimit = const Duration(seconds: 10),
  }) async {
    try {
      if (!await Geolocator.isLocationServiceEnabled()) {
        return const LocationResult.failed(LocationFailure.serviceDisabled);
      }

      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
      }
      switch (permission) {
        case LocationPermission.denied:
          return const LocationResult.failed(LocationFailure.denied);
        case LocationPermission.deniedForever:
          return const LocationResult.failed(LocationFailure.deniedForever);
        case LocationPermission.always:
        case LocationPermission.whileInUse:
        case LocationPermission.unableToDetermine:
          break;
      }

      final Position position = await Geolocator.getCurrentPosition(
        locationSettings: LocationSettings(
          accuracy: LocationAccuracy.low,
          timeLimit: timeLimit,
        ),
      );
      return LocationResult.success(
        LngLat(position.longitude, position.latitude),
      );
    } catch (error, stackTrace) {
      log(
        'Position indisponible',
        name: 'LocationService',
        error: error,
        stackTrace: stackTrace,
      );
      return const LocationResult.failed(LocationFailure.unavailable);
    }
  }

  /// Ouvre les réglages de l'application. C'est la **seule** issue d'un
  /// [LocationFailure.deniedForever].
  Future<bool> openAppSettings() => Geolocator.openAppSettings();

  /// Ouvre les réglages système de localisation, pour un
  /// [LocationFailure.serviceDisabled].
  Future<bool> openLocationSettings() => Geolocator.openLocationSettings();

  /// La clé de traduction du message à afficher pour un échec donné.
  static String messageKey(LocationFailure failure) => switch (failure) {
    LocationFailure.serviceDisabled => 'location.serviceDisabled',
    LocationFailure.denied => 'location.denied',
    LocationFailure.deniedForever => 'location.deniedForever',
    LocationFailure.unavailable => 'location.unavailable',
  };

  /// Vrai quand la seule issue passe par les réglages : l'écran propose alors
  /// « Ouvrir les réglages » plutôt que « Réessayer ».
  static bool needsSettings(LocationFailure failure) =>
      failure == LocationFailure.deniedForever ||
      failure == LocationFailure.serviceDisabled;
}

final locationServiceProvider = Provider<LocationService>(
  (Ref ref) => const LocationService(),
);
