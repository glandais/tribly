import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';

/// Quitte une sous-page du profil : on dépile quand une pile existe (ouverte
/// depuis la vue d'ensemble), sinon on retombe sur [fallback] — la vue
/// d'ensemble par défaut. Sans pile, c'est un lien froid ou un changement de
/// branche (la boîte de réception ouvre ses réglages par `go`) : une flèche
/// qui ne mènerait nulle part serait pire que pas de flèche.
void leaveProfileSubpage(BuildContext context, {String? fallback}) {
  if (context.canPop()) {
    context.pop();
  } else {
    context.go(fallback ?? Paths.profile());
  }
}

/// L'écran d'une sous-page du profil : la barre avec son retour vers
/// « Profil », et une colonne de sections bornée à la largeur de contenu.
///
/// Toutes les sous-pages passent par ici pour que le retour dise la même
/// chose partout (« Retour au profil ») et que la vue d'ensemble, l'onglet
/// racine, reste la seule page du profil sans flèche.
class ProfileSubpage extends StatelessWidget {
  const ProfileSubpage({
    super.key,
    required this.title,
    required this.slivers,
    this.intro,
    this.onRefresh,
    this.actions = const <Widget>[],
    this.backFallback,
  });

  final String title;

  /// Une phrase sous la barre, avant la première section : ce que la page
  /// règle, quand le titre ne suffit pas à le dire.
  final String? intro;

  final List<Widget> slivers;
  final Future<void> Function()? onRefresh;
  final List<Widget> actions;

  /// Où mène le retour quand aucune pile n'existe — le profil par défaut.
  final String? backFallback;

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;
    return PdlScreenScaffold(
      appBar: PdlAppBar(
        title: title,
        onBack: () => leaveProfileSubpage(context, fallback: backFallback),
        backSemanticLabel: 'profile.backToProfile'.tr(),
        actions: actions,
      ),
      onRefresh: onRefresh,
      slivers: <Widget>[
        const SliverToBoxAdapter(child: SizedBox(height: PdlSpacing.section)),
        if (intro != null)
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(
                PdlSpacing.section,
                0,
                PdlSpacing.section,
                PdlSpacing.section,
              ),
              child: Text(intro!, style: t.sub),
            ),
          ),
        ...slivers,
        const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
      ],
    );
  }
}

/// Une section d'une page du profil : un en-tête facultatif, son contenu
/// (une carte, le plus souvent) et une note facultative dessous.
class ProfileSection extends StatelessWidget {
  const ProfileSection({
    super.key,
    this.title,
    this.count,
    required this.child,
    this.footnote,
  });

  final String? title;
  final String? count;
  final Widget child;
  final String? footnote;

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;
    return Padding(
      padding: const EdgeInsets.fromLTRB(
        PdlSpacing.section,
        0,
        PdlSpacing.section,
        PdlSpacing.section,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          if (title != null) PdlSectionHeader(title: title!, count: count),
          child,
          if (footnote != null) ...<Widget>[
            const SizedBox(height: PdlSpacing.chipGap),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 4),
              child: Text(footnote!, style: t.xs),
            ),
          ],
        ],
      ),
    );
  }
}
