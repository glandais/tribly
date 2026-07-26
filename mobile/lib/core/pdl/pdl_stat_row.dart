import 'package:flutter/material.dart';

import '../theme/pdl_tokens.dart';
import 'pdl_stat.dart';

/// A7 — Rangée de [PdlStat].
///
/// Deux régimes : repliable (`.stats`, gouttières 4 × 16) et non repliable
/// (`.stats--nowrap`, gouttière 12) pour les barres denses d'une ligne.
///
/// Le régime non repliable **cède** dès que la typographie grossit : au-delà de
/// `textScaler.scale(14) > 17`, quatre statistiques sur une ligne ne tiennent
/// plus dans 402 pt et se feraient rogner. La rangée repasse alors en repli,
/// ce qui coûte une ligne de haut au lieu d'un texte tronqué.
class PdlStatRow extends StatelessWidget {
  const PdlStatRow({super.key, required this.stats, this.nowrap = false});

  final List<PdlStat> stats;
  final bool nowrap;

  @override
  Widget build(BuildContext context) {
    final bool scaled = MediaQuery.textScalerOf(context).scale(14) > 17;

    if (nowrap && !scaled) {
      return Row(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          for (int i = 0; i < stats.length; i++) ...<Widget>[
            if (i > 0) const SizedBox(width: PdlSpacing.statsNowrap),
            Flexible(child: stats[i]),
          ],
        ],
      );
    }

    return Wrap(
      spacing: PdlSpacing.statsWrapH,
      runSpacing: PdlSpacing.statsWrapV,
      crossAxisAlignment: WrapCrossAlignment.center,
      children: stats,
    );
  }
}
