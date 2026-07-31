import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

/// Le jeton qui autorise les requêtes de tuiles vectorielles.
///
/// **Pourquoi il existe.** MapLibre va chercher ses tuiles par sa propre pile
/// HTTP native : ni Dio ni ses intercepteurs ne la voient, et le paquet Dart
/// n'expose ni `transformRequest` ni en-tête personnalisable. Le navigateur s'en
/// tire parce que son cookie de session part tout seul en same-origin ; le
/// mobile n'a pas de cookie. Reste la chaîne de requête.
///
/// **Pourquoi il est ici et pas dans `core/pdl`** : le contrat du module
/// (`core/pdl/README.md`) interdit à `core/pdl` de connaître le moindre DTO
/// généré — `grep -rn "api/generated" lib/core/pdl` doit rester vide. La carte
/// reçoit donc une `String` d'URL, jamais ce dépôt.
///
/// **Renouvellement paresseux, jamais sur minuterie.** Sur un téléphone en
/// veille une minuterie ne tire pas, et sur un écran qu'on a quitté elle ne
/// rachète rien : elle brûle une requête pour rien. Le jeton se renouvelle
/// quand on le demande, et le cycle de vie de l'application le redemande au
/// réveil.
class TileTokenRepository {
  TileTokenRepository(this._client);

  final TilesClient _client;

  /// 80 % de la durée de vie.
  ///
  /// Un déplacement de carte ne doit jamais tomber sur un jeton mort, et une
  /// session de carte normale ne doit jamais payer plus d'un renouvellement :
  /// le jeton fait partie de l'URL de tuile, donc de la clé de cache de
  /// MapLibre, et chaque renouvellement fait retélécharger la vue entière.
  static const double _renewRatio = 0.8;

  String? _token;
  DateTime? _renewAt;
  Future<String>? _inFlight;

  /// Le jeton courant, frais.
  ///
  /// Les appels concurrents partagent **un seul** appel réseau : une carte qui
  /// monte demande son URL depuis plusieurs endroits à la fois.
  Future<String> current() {
    final String? token = _token;
    final DateTime? renewAt = _renewAt;
    if (token != null && renewAt != null && DateTime.now().isBefore(renewAt)) {
      return Future<String>.value(token);
    }
    return _inFlight ??= _fetch().whenComplete(() => _inFlight = null);
  }

  Future<String> _fetch() async {
    final TileTokenDto dto = await _client.mint();
    _token = dto.token;
    // `expiresIn`, jamais `expiresAt` : l'échéance se calcule sur l'horloge de
    // l'appareil, qui peut être fausse de plusieurs minutes sans que personne
    // ne s'en aperçoive. Un instant absolu comparé à une horloge fausse
    // renouvelle soit bien trop tôt, soit jamais.
    _renewAt = DateTime.now().add(
      Duration(seconds: (dto.expiresIn * _renewRatio).round()),
    );
    return dto.token;
  }

  /// Vrai quand le jeton en mémoire est périmé — ce que l'écran regarde au
  /// retour d'arrière-plan.
  bool get needsRenewal {
    final DateTime? renewAt = _renewAt;
    return _token == null ||
        renewAt == null ||
        !DateTime.now().isBefore(renewAt);
  }

  /// À appeler à la déconnexion.
  ///
  /// Un jeton apatride ne se révoque pas côté serveur : la seule chose qu'on
  /// maîtrise est de ne plus s'en servir.
  void clear() {
    _token = null;
    _renewAt = null;
  }
}

final tileTokenRepositoryProvider = Provider<TileTokenRepository>(
  (Ref ref) => TileTokenRepository(ref.watch(tilesClientProvider)),
);
