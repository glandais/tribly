import '../config/app_config.dart';

/// `true` si une requête vers [uri] peut porter le jeton d'accès : seulement
/// vers l'**origine** de l'API — même schéma, même hôte, même port.
///
/// Tout le reste part sans lui. Une image de markdown pointe où son auteur le
/// veut ; la charger avec le `Bearer` remettait le jeton de chaque lecteur au
/// serveur de l'auteur (docs/LEDGER_*.md SEC-3, audit H4). L'hôte seul ne
/// suffit pas : un lien `http://` vers le même hôte ferait circuler le jeton
/// en clair.
bool carriesCredentials(Uri uri, {String apiBaseUrl = AppConfig.apiBaseUrl}) {
  final Uri api = Uri.parse(apiBaseUrl);
  return uri.scheme.toLowerCase() == api.scheme.toLowerCase() &&
      uri.host.toLowerCase() == api.host.toLowerCase() &&
      uri.port == api.port;
}

/// L'en-tête `Authorization` pour charger [url], ou `null` si [url] n'est pas
/// sur l'origine de l'API ou qu'il n'y a pas de jeton.
Map<String, String>? authHeadersFor(String url, String? token) {
  if (token == null) return null;
  final Uri? uri = Uri.tryParse(url);
  if (uri == null || !carriesCredentials(uri)) return null;
  return <String, String>{'Authorization': 'Bearer $token'};
}
