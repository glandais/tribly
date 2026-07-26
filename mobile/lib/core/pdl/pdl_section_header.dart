import 'package:flutter/material.dart';

import '../theme/pdl_typography.dart';

/// A13 — Titre de section, compteur, action.
///
/// Le compteur (13/500 `textDimmed`) est collé au titre et non aligné à droite :
/// « Participants 40 » se lit d'un bloc, « Participants … 40 » demande un
/// aller-retour du regard. L'action, elle, va à droite — c'est un
/// [PdlButton(variant: text)] dans presque tous les cas.
class PdlSectionHeader extends StatelessWidget {
  const PdlSectionHeader({
    super.key,
    required this.title,
    this.count,
    this.action,
    this.padding = const EdgeInsets.only(bottom: 8),
  });

  final String title;

  /// Déjà formaté par l'appelant : `core/pdl` ne connaît ni la locale ni le
  /// pluriel de ce qui est compté.
  final String? count;

  final Widget? action;
  final EdgeInsetsGeometry padding;

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;

    return Padding(
      padding: padding,
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: <Widget>[
          Flexible(
            child: Text(
              title,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: t.sectionTitle,
            ),
          ),
          if (count != null) ...<Widget>[
            const SizedBox(width: 8),
            Text(count!, style: t.count),
          ],
          const Spacer(),
          ?action,
        ],
      ),
    );
  }
}
