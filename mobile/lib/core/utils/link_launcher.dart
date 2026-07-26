import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../config/app_config.dart';
import '../../config/paths.dart';
import '../pdl/pdl_banner.dart';
import '../pdl/pdl_button.dart';
import '../theme/pdl_icons.dart';

/// F-TE-10 — Le routeur de liens de l'application (plan §1.3.5).
///
/// Il vit dans `core/utils` et non dans `core/pdl` : ouvrir une application
/// tierce, pousser une route et traduire un libellé sont trois choses que la
/// bibliothèque de composants s'interdit. `PdlMarkdownBody` reçoit donc un
/// simple `ValueChanged<String>`, et c'est ici qu'il pointe.
///
/// La résolution se fait en trois temps, **dans cet ordre** :
///
/// 1. une URL **du domaine courant** dont le chemin est reconnu par un motif de
///    `paths.generated.dart` → `context.push` : rester dans l'app, ne pas
///    partir dans le navigateur pour revenir sur la même page en moins bien ;
/// 2. sinon `launchUrl(mode: externalApplication)` — `http`, `https`, mais
///    aussi `mailto:`, `tel:` et `webcal:`, les schémas déclarés dans
///    `Info.plist` et dans le bloc `<queries>` Android par F-TE-1 ;
/// 3. schéma inconnu, URL illisible ou lancement refusé → un [PdlBanner] `warn`
///    qui **montre l'URL en clair** et propose de la copier.
///
/// **Jamais d'échec silencieux.** Un lien qui ne mène nulle part le dit ; le
/// défaut d'origine (`LinkConfig` sans `onTap`) était précisément un lien
/// stylé qui ne faisait rien, et cassait toutes les inscriptions annoncées
/// dans les publications.

/// Ce qu'un tap sur un lien a produit. Retourné pour que l'appelant — un test,
/// une trace — puisse le constater ; aucun écran n'a besoin de le lire.
enum LinkOutcome {
  /// Poussé sur le `GoRouter` sans quitter l'application.
  internal,

  /// Confié au système (navigateur, client mail, téléphone, calendrier).
  external,

  /// Ni l'un ni l'autre : le bandeau a été affiché.
  refused,
}

/// Marque de position d'un segment variable dans un motif de route.
///
/// Un caractère de contrôle, donc absent de tout slug réel, et qui survit à
/// `RegExp.escape` puisqu'on découpe *avant* d'échapper.
const String _slot = '\u0001';

/// Tous les motifs de route de l'app, un identifiant par entrée de
/// `PathVariants`, toutes locales confondues.
///
/// Dérivé de `paths.generated.dart` plutôt que recopié : un chemin ajouté au
/// contrat `contracts/routes.yaml` n'a qu'une ligne à ajouter ici, et
/// `link_launcher_test.dart` échoue tant qu'elle manque.
@visibleForTesting
final Map<String, List<String>> internalRouteTemplates = <String, List<String>>{
  'home': PathVariants.home().values.toList(),
  'login': PathVariants.login().values.toList(),
  'register': PathVariants.register().values.toList(),
  'verifyEmail': PathVariants.verifyEmail().values.toList(),
  'forgotPassword': PathVariants.forgotPassword().values.toList(),
  'resetPassword': PathVariants.resetPassword().values.toList(),
  'deviceVerifyGarmin': PathVariants.deviceVerifyGarmin().values.toList(),
  'deviceVerifyKaroo': PathVariants.deviceVerifyKaroo().values.toList(),
  'privacy': PathVariants.privacy().values.toList(),
  'terms': PathVariants.terms().values.toList(),
  'profile': PathVariants.profile().values.toList(),
  'calendar': PathVariants.calendar().values.toList(),
  'allRoutes': PathVariants.allRoutes().values.toList(),
  'allRoutesMap': PathVariants.allRoutesMap().values.toList(),
  'teams': PathVariants.teams().values.toList(),
  'teamsDiscover': PathVariants.teamsDiscover().values.toList(),
  'team': PathVariants.team(_slot).values.toList(),
  'teamAbout': PathVariants.teamAbout(_slot).values.toList(),
  'teamCalendar': PathVariants.teamCalendar(_slot).values.toList(),
  'teamMembersPublic': PathVariants.teamMembersPublic(_slot).values.toList(),
  'teamPage': PathVariants.teamPage(_slot, _slot).values.toList(),
  'ride': PathVariants.ride(_slot, _slot).values.toList(),
  'trip': PathVariants.trip(_slot, _slot).values.toList(),
  'stage': PathVariants.stage(_slot, _slot, _slot).values.toList(),
  'post': PathVariants.post(_slot, _slot).values.toList(),
  'routes': PathVariants.routes(_slot).values.toList(),
  'route': PathVariants.route(_slot, _slot).values.toList(),
  'teamAds': PathVariants.teamAds(_slot).values.toList(),
  'ad': PathVariants.ad(_slot, _slot).values.toList(),
};

/// Les motifs compilés. `final` de haut niveau : construits à la première
/// résolution de lien, jamais à chaque tap.
final List<RegExp> _routePatterns = <RegExp>[
  for (final List<String> templates in internalRouteTemplates.values)
    for (final String template in templates) _templateToRegExp(template),
];

/// `/teams/<slot>/rides/<slot>` → `^/teams/[^/]+/rides/[^/]+/?$`.
///
/// On découpe sur la marque **puis** on échappe chaque littéral : la marque ne
/// traverse jamais `RegExp.escape`, dont le résultat n'est pas spécifié.
RegExp _templateToRegExp(String template) =>
    RegExp('^${template.split(_slot).map(RegExp.escape).join('[^/]+')}/?\$');

/// Le nom d'hôte, débarrassé de son `www.`, pour comparer deux domaines qui ne
/// diffèrent que par lui.
String _bareHost(String host) {
  final String h = host.toLowerCase();
  return h.startsWith('www.') ? h.substring(4) : h;
}

/// `true` si [host] est le domaine servi par cette installation.
///
/// Un hôte vide est un lien **relatif** (`/equipes/x/sorties/y`) : par
/// définition le domaine courant.
bool _isCurrentHost(String host) {
  if (host.isEmpty) return true;
  final String h = _bareHost(host);
  return h == _bareHost(AppConfig.deepLinkHost) ||
      h == _bareHost(Uri.parse(AppConfig.apiBaseUrl).host);
}

/// L'emplacement `GoRouter` correspondant à [href], ou `null` si ce lien n'est
/// pas une route interne de cette application.
///
/// Pur, sans contexte : c'est lui que les tests interrogent en masse.
String? internalLocationFor(String href) {
  final String trimmed = href.trim();
  // Une ancre seule (`#section`) n'est pas une route : sans ce garde-fou elle
  // se résoudrait en `/` et renverrait l'utilisateur à l'accueil.
  if (trimmed.isEmpty || trimmed.startsWith('#')) return null;

  final Uri? uri = Uri.tryParse(trimmed);
  if (uri == null) return null;
  if (uri.hasScheme && uri.scheme != 'http' && uri.scheme != 'https') {
    return null;
  }
  if (!_isCurrentHost(uri.host)) return null;

  final String path = uri.path.isEmpty ? '/' : uri.path;
  if (!_routePatterns.any((RegExp pattern) => pattern.hasMatch(path))) {
    return null;
  }

  final StringBuffer location = StringBuffer(path);
  if (uri.hasQuery && uri.query.isNotEmpty) location.write('?${uri.query}');
  if (uri.fragment.isNotEmpty) location.write('#${uri.fragment}');
  return location.toString();
}

/// Le lanceur système, substituable en test.
///
/// Un test ne peut pas ouvrir Safari ; il peut en revanche vérifier **quelle**
/// URL a été confiée au système, et simuler un refus.
typedef LinkOpener = Future<bool> Function(Uri url);

@visibleForTesting
LinkOpener? debugLinkOpener;

Future<bool> _launchExternally(Uri url) =>
    launchUrl(url, mode: LaunchMode.externalApplication);

/// Ouvre [href] selon les trois temps décrits en tête de fichier.
Future<LinkOutcome> openLink(BuildContext context, String href) async {
  final String trimmed = href.trim();

  final String? location = internalLocationFor(trimmed);
  if (location != null) {
    context.push(location);
    return LinkOutcome.internal;
  }

  final Uri? uri = trimmed.isEmpty ? null : Uri.tryParse(trimmed);
  if (uri == null) {
    showUnopenableLinkBanner(context, href);
    return LinkOutcome.refused;
  }

  bool opened = false;
  try {
    opened = await (debugLinkOpener ?? _launchExternally)(uri);
  } on Object catch (error, stack) {
    // Un schéma non déclaré lève plutôt qu'il ne renvoie `false` : les deux
    // aboutissent au même bandeau, mais la trace nomme la cause.
    debugPrint('openLink: $uri refusé par le système (${error.runtimeType})');
    debugPrintStack(stackTrace: stack, maxFrames: 6);
  }

  if (!opened) {
    if (context.mounted) showUnopenableLinkBanner(context, href);
    return LinkOutcome.refused;
  }
  return LinkOutcome.external;
}

/// Le bandeau du troisième temps : `warn`, **l'URL en clair**, et de quoi la
/// copier pour l'ouvrir ailleurs.
///
/// Un `MaterialBanner` et non un `SnackBar` : c'est un état, pas un événement
/// de quatre secondes — et sous la barre d'onglets un snackbar est masqué
/// (règle transverse du plan §1.3.4).
void showUnopenableLinkBanner(BuildContext context, String href) {
  final ScaffoldMessengerState messenger = ScaffoldMessenger.of(context);
  messenger.clearMaterialBanners();
  messenger.showMaterialBanner(
    MaterialBanner(
      padding: EdgeInsets.zero,
      backgroundColor: Colors.transparent,
      surfaceTintColor: Colors.transparent,
      dividerColor: Colors.transparent,
      content: PdlBanner(
        tone: PdlBannerTone.warn,
        title: 'links.unopenable.title'.tr(),
        message: href,
        onDismiss: messenger.hideCurrentMaterialBanner,
        dismissSemanticLabel: 'common.close'.tr(),
        action: PdlButton(
          label: 'links.copy'.tr(),
          variant: PdlButtonVariant.text,
          size: PdlButtonSize.sm,
          icon: PdlIcons.copy,
          onPressed: () async {
            await Clipboard.setData(ClipboardData(text: href));
            messenger.hideCurrentMaterialBanner();
          },
        ),
      ),
      // `MaterialBanner` exige une action ; les nôtres sont dans le
      // `PdlBanner`, qui porte la charte. Celle-ci ne rend rien.
      actions: const <Widget>[SizedBox.shrink()],
    ),
  );
}
