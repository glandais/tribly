import 'package:flutter_riverpod/flutter_riverpod.dart';

/// Le lien profond ouvert hors session, qui attend la connexion.
///
/// Un lien (QR code d'un appareil, lien partagé, notification) ouvert sans
/// session passe par la page de connexion ; sans cette mémoire, la connexion
/// ramène à l'accueil et le lien est perdu — l'appareil n'est jamais associé.
/// Le gestionnaire de liens de `main.dart` le dépose ici, la connexion le
/// reprend ([take]) et la déconnexion l'efface : il ne survit jamais à la
/// session qui l'a consommé.
///
/// Un simple conteneur, sans notification : personne n'a à se reconstruire
/// quand il change, et il reste inscriptible depuis n'importe quel rappel.
class PendingSignInLink {
  /// L'adresse en attente, query string comprise (`/garmin?code=…`).
  String? location;

  /// Rend l'adresse en attente et l'oublie : elle ne sert qu'une fois.
  String? take() {
    final taken = location;
    location = null;
    return taken;
  }

  void clear() => location = null;
}

final pendingSignInLinkProvider = Provider<PendingSignInLink>(
  (ref) => PendingSignInLink(),
);
