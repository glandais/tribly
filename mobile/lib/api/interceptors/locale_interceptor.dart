import 'package:dio/dio.dart';

import '../../config/locale_context.dart';

/// Pose `Accept-Language` sur **chaque** requête, d'après la locale courante.
///
/// F-DE-11 : les deux `BaseOptions` figeaient l'en-tête à `fr`, une valeur
/// écrite une fois pour toutes à la construction du `Dio`. Un utilisateur
/// anglophone recevait donc les libellés serveur — messages d'erreur,
/// notifications, gabarits d'e-mail — en français, sans recours.
///
/// L'en-tête est un état, pas une constante de construction : il se lit au
/// moment de la requête. C'est aussi ce qui le rend correct après un
/// changement de langue en cours de session, là où un `BaseOptions` aurait
/// exigé de reconstruire le client.
///
/// Il s'applique aux **deux** `Dio` : l'authentifié comme le nu. Le nu porte
/// justement les endpoints dont la réponse est la plus visible — inscription,
/// mot de passe oublié, code à usage unique.
class LocaleInterceptor extends Interceptor {
  const LocaleInterceptor();

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    options.headers['Accept-Language'] = getCurrentLocale();
    handler.next(options);
  }
}
