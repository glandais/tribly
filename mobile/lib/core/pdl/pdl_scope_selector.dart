import 'package:flutter/material.dart';

import '../theme/pdl_colors.dart';
import '../theme/pdl_icons.dart';
import '../theme/pdl_tokens.dart';
import '../theme/pdl_typography.dart';
import 'pdl_chip.dart';

/// Les deux rendus du sélecteur de portée.
enum PdlScopeStyle {
  /// Chip de tri (contour `text`), dans une rangée de chips.
  chip,

  /// Ligne pleine largeur de 44 px, dans une barre d'outils — le rendu de
  /// l'écran 22.
  row,
}

/// B23 — Sélecteur de portée : toutes mes équipes, une équipe, un rôle
/// minimum.
///
/// Il **ne choisit rien lui-même** : il affiche la portée courante et déclenche
/// [onTap], à charge de l'écran d'ouvrir la feuille de sélection. Deux raisons :
/// la liste des portées est faite de DTO, et la feuille (`PdlSheet`) appartient
/// à la vague C.
///
/// Le contour `text` de la variante chip ([PdlChip.sortStyle]) le distingue des
/// filtres qui l'entourent : une portée n'est pas un filtre de plus, elle
/// **change la source des données**.
class PdlScopeSelector extends StatelessWidget {
  const PdlScopeSelector({
    super.key,
    required this.label,
    required this.onTap,
    this.style = PdlScopeStyle.chip,
    this.icon = PdlIcons.teams,
    this.semanticLabel,
  });

  /// Portée courante, déjà traduite — « Toutes mes équipes », « N-Peloton ».
  final String label;

  final VoidCallback? onTap;
  final PdlScopeStyle style;
  final IconData icon;
  final String? semanticLabel;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;

    if (style == PdlScopeStyle.chip) {
      return Semantics(
        label: semanticLabel,
        child: PdlChip(label: label, icon: icon, sortStyle: true, onTap: onTap),
      );
    }

    return Semantics(
      button: true,
      label: semanticLabel,
      child: Material(
        color: c.surface,
        shape: RoundedRectangleBorder(
          borderRadius: PdlRadii.mdAll,
          side: BorderSide(color: c.border),
        ),
        clipBehavior: Clip.antiAlias,
        child: InkWell(
          onTap: onTap,
          child: ConstrainedBox(
            constraints: const BoxConstraints(minHeight: PdlMetrics.tapTarget),
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 12),
              child: Row(
                children: <Widget>[
                  Icon(icon, size: 20, color: c.textDimmed),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      label,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: t.bodyStrong,
                    ),
                  ),
                  Icon(PdlIcons.chevronRight, size: 16, color: c.textDimmed),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
