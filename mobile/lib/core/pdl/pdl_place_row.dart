import 'package:flutter/material.dart';

import '../theme/pdl_colors.dart';
import '../theme/pdl_typography.dart';

/// Les deux bouts d'un parcours. La couleur est celle des marqueurs
/// cartographiques : vert au départ, rouge à l'arrivée — la même que sur la
/// carte, sinon la lecture croisée ne se fait pas.
enum PdlPlaceKind { start, end }

/// B4 — Ligne de lieu : pastille de 10 px, nom, adresse.
///
/// Le nom est en `bodyStrong` et l'adresse en `xs` : c'est le nom qu'on
/// cherche (« Fonderies »), l'adresse ne sert qu'à lever un doute.
class PdlPlaceRow extends StatelessWidget {
  const PdlPlaceRow({
    super.key,
    required this.kind,
    required this.name,
    this.address,
    this.trailing,
    this.onTap,
  });

  final PdlPlaceKind kind;
  final String name;
  final String? address;

  /// Action de bout de ligne — « Itinéraire », « Copier l'adresse ».
  final Widget? trailing;

  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final Color dot = kind == PdlPlaceKind.start ? c.mapStart : c.mapEnd;

    final Widget content = Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        Padding(
          // 6 px de marge haute : la pastille s'aligne sur la première ligne
          // du nom, pas sur le haut de la boîte.
          padding: const EdgeInsets.only(top: 6),
          child: Container(
            width: 10,
            height: 10,
            decoration: BoxDecoration(color: dot, shape: BoxShape.circle),
          ),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              Text(name, style: t.bodyStrong),
              if (address != null) ...<Widget>[
                const SizedBox(height: 2),
                Text(address!, style: t.xs),
              ],
            ],
          ),
        ),
        if (trailing != null) ...<Widget>[const SizedBox(width: 8), trailing!],
      ],
    );

    if (onTap == null) return content;
    return Material(
      type: MaterialType.transparency,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 4),
          child: content,
        ),
      ),
    );
  }
}
