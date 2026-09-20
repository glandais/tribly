/// Ce qu'un message push transporte, une fois débarrassé de FCM.
///
/// Le serveur envoie un `notification` (titre et corps, déjà rendus dans la
/// langue du destinataire — c'est le seul endroit où il rend du texte, parce
/// que le système affiche la bannière sans l'app) **et** un `data` structuré,
/// qui est ce que l'app utilise pour agir :
/// `type`, `notificationId`, `teamSlug`, `subjectType`, `subjectSlug`, `path`.
///
/// Le `path` vient de `NotificationLinks` côté serveur, donc d'une route
/// renommée dans `contracts/routes.yaml` — l'app n'a pas à le recalculer à
/// partir du sujet, et ne le fait pas.
class PushMessage {
  const PushMessage({this.title, this.body, this.data = const {}});

  final String? title;
  final String? body;
  final Map<String, String> data;

  /// L'identifiant de la notification à marquer lue quand on ouvre celle-ci.
  String? get notificationId => _nonEmpty('notificationId');

  /// La route à ouvrir au tap, telle que le serveur l'a calculée.
  String? get path => _nonEmpty('path');

  String? _nonEmpty(String key) {
    final String? value = data[key];
    return (value == null || value.isEmpty) ? null : value;
  }
}

/// L'autorisation d'afficher des notifications sur *cet* appareil.
enum PushAuthorization {
  /// Jamais demandée : c'est le seul état où proposer le bouton a un sens.
  notDetermined,

  /// Accordée — un jeton peut être enregistré et une bannière s'affichera.
  granted,

  /// Refusée. iOS ne redemandera pas, Android non plus après deux refus : la
  /// seule issue passe par les réglages du système, et l'app le dit plutôt que
  /// de rejouer un bouton sans effet.
  denied,

  /// Plateforme sans push (le bureau, le web, les tests) — rien à proposer.
  unsupported,
}
