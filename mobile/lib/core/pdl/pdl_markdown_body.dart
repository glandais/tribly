import 'package:flutter/material.dart';
import 'package:markdown_widget/markdown_widget.dart';

import '../theme/pdl_colors.dart';
import '../theme/pdl_tokens.dart';
import '../theme/pdl_typography.dart';
import '../widgets/authenticated_image.dart';
import 'pdl_skeleton.dart';

/// B24 — Corps markdown, feuille de style unique du plan §1.3.5.
///
/// Racine 15 px / interligne 1,6 · `p` marge basse 14 · `h1/h2/h3` 20/700,
/// 18/700, 16/700 · listes retrait 20 · citation filet gauche 3 px ·
/// `pre` sur `surfaceAlt` bordé rayon 8 **à défilement horizontal** · code
/// inline mono 12 · image de 160 px rayon 8 · **table dans un conteneur bordé
/// dont le défilement horizontal est indépendant du défilement vertical de la
/// page**, largeur minimale 430 px, en-têtes 600 sur `surfaceAlt`.
///
/// Le widget ne défile pas : il se dimensionne à son contenu et laisse le
/// défilement au parent. C'est ce qui garde le `ScrollController` de la page
/// attaché au `PrimaryScrollController`, dont iOS a besoin pour le tap sur la
/// barre d'état.
///
/// **Les liens sont stylés, pas encore actifs.** `LinkConfig` reçoit ici sa
/// seule feuille de style ; l'attachement de `onTap`, la résolution interne
/// contre `paths.generated.dart` et `url_launcher` sont la tâche F-TE-10, qui
/// livrera aussi `core/utils/link_launcher.dart`. Poser un `onTap` à moitié
/// ici — sans repli ni bandeau d'échec — rendrait le défaut plus difficile à
/// voir, pas moins réel.
class PdlMarkdownBody extends StatelessWidget {
  const PdlMarkdownBody({
    super.key,
    required this.data,
    this.selectable = true,
    this.imageBuilder,
    this.onLinkTap,
  });

  /// Markdown déjà résolu — les directives `::asset{…}` sont dépliées par
  /// l'appelant, qui seul connaît les DTO d'images.
  final String data;

  final bool selectable;

  /// Rendu d'image imposé. À défaut, une image authentifiée de 160 px de haut,
  /// rayon 8.
  final Widget Function(String url, Map<String, String> attributes)?
  imageBuilder;

  /// Prévu pour F-TE-10 ; laissé nul, les liens restent inertes mais stylés.
  final ValueChanged<String>? onLinkTap;

  /// Largeur minimale d'une table avant défilement horizontal.
  static const double tableMinWidth = 430;

  /// Hauteur d'une image de contenu.
  static const double imageHeight = 160;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final bool dark = Theme.of(context).brightness == Brightness.dark;

    final TextStyle body = t.body.copyWith(height: 1.6, color: c.text);
    final TextStyle heading = TextStyle(color: c.textBright, height: 1.35);

    final MarkdownConfig config =
        (dark ? MarkdownConfig.darkConfig : MarkdownConfig.defaultConfig).copy(
          configs: <WidgetConfig>[
            PConfig(textStyle: body),
            H1Config(
              style: heading.copyWith(
                fontSize: 20,
                fontWeight: FontWeight.w700,
              ),
            ),
            H2Config(
              style: heading.copyWith(
                fontSize: 18,
                fontWeight: FontWeight.w700,
              ),
            ),
            H3Config(
              style: heading.copyWith(
                fontSize: 16,
                fontWeight: FontWeight.w700,
              ),
            ),
            const ListConfig(marginLeft: 20, marginBottom: 6),
            LinkConfig(
              style: body.copyWith(
                color: c.link,
                decoration: TextDecoration.underline,
                decorationColor: c.link,
              ),
              onTap: onLinkTap,
            ),
            CodeConfig(
              style: t.mono.copyWith(
                fontSize: 12,
                color: c.neutralOnSoft,
                backgroundColor: c.surfaceAlt,
              ),
            ),
            PreConfig(
              textStyle: t.mono.copyWith(fontSize: 12, color: c.text),
              padding: const EdgeInsets.all(12),
              margin: const EdgeInsets.symmetric(vertical: 8),
              decoration: BoxDecoration(
                color: c.surfaceAlt,
                borderRadius: PdlRadii.mdAll,
                border: Border.all(color: c.borderSubtle),
              ),
            ),
            BlockquoteConfig(
              sideColor: c.border,
              textColor: c.textDimmed,
              sideWith: 3,
            ),
            HrConfig(height: 1, color: c.borderSubtle),
            TableConfig(
              headerStyle: t.sub.copyWith(
                fontWeight: FontWeight.w600,
                color: c.text,
              ),
              bodyStyle: t.sub.copyWith(color: c.text),
              headerRowDecoration: BoxDecoration(color: c.surfaceAlt),
              border: TableBorder.all(color: c.borderSubtle),
              // Le défilement de la table lui appartient : imbriqué dans la
              // page, il ne doit pas emporter le défilement vertical.
              wrapper: (Widget table) => Container(
                margin: const EdgeInsets.symmetric(vertical: 8),
                decoration: BoxDecoration(
                  borderRadius: PdlRadii.mdAll,
                  border: Border.all(color: c.borderSubtle),
                ),
                clipBehavior: Clip.antiAlias,
                child: SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: ConstrainedBox(
                    constraints: const BoxConstraints(minWidth: tableMinWidth),
                    child: table,
                  ),
                ),
              ),
            ),
            ImgConfig(
              builder:
                  imageBuilder ??
                  (String url, Map<String, String> attributes) =>
                      _defaultImage(context, url),
            ),
          ],
        );

    return MarkdownBlock(
      data: data,
      selectable: selectable,
      config: config,
      // `linesMargin` porte la marge basse de 14 px des paragraphes : elle
      // s'applique entre blocs, ce que `PConfig` ne sait pas faire.
      generator: MarkdownGenerator(
        linesMargin: const EdgeInsets.symmetric(vertical: 7),
      ),
    );
  }

  Widget _defaultImage(BuildContext context, String url) {
    final PdlColors c = context.pdl;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: ClipRRect(
        borderRadius: PdlRadii.mdAll,
        child: SizedBox(
          height: imageHeight,
          child: AuthenticatedImage(
            imageUrl: url,
            fit: BoxFit.cover,
            width: double.infinity,
            height: imageHeight,
            placeholder: const PdlSkeleton(
              width: double.infinity,
              height: imageHeight,
            ),
            errorWidget: ColoredBox(color: c.surfaceAlt),
          ),
        ),
      ),
    );
  }
}
