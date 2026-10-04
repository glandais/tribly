import 'package:flutter/widgets.dart';
import 'package:go_router/go_router.dart';

import '../../config/router.dart';

/// Ouvre [location] par-dessus la page courante, comme `context.push` — sauf
/// dans le seul cas où `push` casse (docs/LEDGER_*.md MOB-44, MOB-46).
///
/// Une page de détail (sortie, parcours, publication, annonce, voyage…) vit sur
/// le navigateur **racine**, au-dessus du shell à onglets. Y pousser une page
/// **du shell** (l'équipe, une de ses sections, un onglet) recrée la branche
/// sous la page de détail : ses pages reprennent les clés de celles qui y sont
/// déjà, le `Navigator` refuse les doublons (`!keyReservation.contains(key)`
/// en debug) et l'écran reste vide, sans retour possible. Vrai pour toute page
/// du shell, pas seulement l'équipe du détail
/// (`test/core/utils/push_location_test.dart`).
///
/// Dans ce cas, la page est ouverte dans son onglet, sur la pile que lui
/// donnerait un lien profond ([openWithHierarchy]) : l'onglet Équipes, puis
/// l'équipe, puis la section. Le détail quitté ne reste pas dessous ; il reste
/// joignable depuis l'onglet d'où il avait été ouvert.
///
/// Partout ailleurs — depuis une page du shell, ou vers une page plein écran
/// — c'est un `push` ordinaire, qui garde le retour vers la page quittée.
void pushLocation(BuildContext context, String location, {Object? extra}) {
  final GoRouter router = GoRouter.of(context);
  if (_endsInShell(router.configuration.findMatch(Uri.parse(location))) &&
      !_endsInShell(_shownStack(router))) {
    openWithHierarchy(router, location);
    return;
  }
  router.push(location, extra: extra);
}

/// La pile réellement affichée. `currentConfiguration.uri` ne suffit pas : il
/// reste sur la base après un `push` impératif. Une page poussée par-dessus le
/// shell est une [ImperativeRouteMatch] en fin de liste, qui porte sa propre
/// liste ; une page poussée **dans** un onglet reste sous le [ShellRouteMatch].
RouteMatchList _shownStack(GoRouter router) {
  RouteMatchList stack = router.routerDelegate.currentConfiguration;
  while (stack.matches.isNotEmpty &&
      stack.matches.last is ImperativeRouteMatch) {
    stack = (stack.matches.last as ImperativeRouteMatch).matches;
  }
  return stack;
}

/// `true` si [stack] se termine sur une page d'un onglet du shell, `false` sur
/// une page plein écran (navigateur racine) ou hors shell.
bool _endsInShell(RouteMatchList stack) =>
    stack.matches.isNotEmpty && stack.matches.last is ShellRouteMatch;
