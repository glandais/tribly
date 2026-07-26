import 'package:flutter/material.dart';

import 'elevation_samples.dart';

/// Le painter du **bas** de la pile : l'histogramme, et rien d'autre.
///
/// C'est tout l'intérêt de la superposition (§1.3.1 du plan) : ce painter ne
/// dépend que des données, son [shouldRepaint] est faux tant que le profil ne
/// change pas, et le réticule qui se repeint à chaque frame de glissement ne
/// l'entraîne pas avec lui.
///
/// La classe est **publique** — le plan la nomme `_ProfileBarsPainter` — parce
/// que la garantie « le glissement ne repeint pas les barres » se vérifie par
/// un compteur d'appels à `paint`, et qu'un compteur sur une classe privée
/// d'un autre fichier ne se lit pas depuis un test.
class ElevationBarsPainter extends CustomPainter {
  ElevationBarsPainter({
    required this.samples,
    required this.neutralColor,
    required this.slopeColorOf,
  });

  final ElevationSamples samples;

  /// Couleur d'une barre dont la pente est inconnue — `slopeNeutral`.
  final Color neutralColor;

  /// Échelle de pente. Injectée plutôt qu'appelée en dur pour que le test de
  /// painter puisse observer la couleur exacte demandée à chaque barre.
  final Color Function(double gradePercent) slopeColorOf;

  /// Nombre d'appels à [paint] depuis le début du processus.
  ///
  /// Instrumentation de test uniquement : c'est la preuve que le glissement
  /// n'entraîne pas les barres.
  @visibleForTesting
  static int debugPaintCount = 0;

  @override
  void paint(Canvas canvas, Size size) {
    debugPaintCount++;

    final int n = samples.bars.length;
    if (n == 0 || size.width <= 0 || size.height <= 0) return;

    // `.elev { gap: 1px }` : n barres, n − 1 gouttières.
    const double gap = 1;
    final double barWidth = (size.width - (n - 1) * gap) / n;
    if (barWidth <= 0) return;

    const Radius top = Radius.circular(1);
    final Paint paint = Paint()..isAntiAlias = true;

    for (int i = 0; i < n; i++) {
      final ElevationBar bar = samples.bars[i];
      final double height = samples.normalizedHeight(bar) * size.height;
      final double left = i * (barWidth + gap);
      final double? grade = bar.grade;

      paint.color = grade == null ? neutralColor : slopeColorOf(grade);
      canvas.drawRRect(
        RRect.fromRectAndCorners(
          Rect.fromLTWH(left, size.height - height, barWidth, height),
          topLeft: top,
          topRight: top,
        ),
        paint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant ElevationBarsPainter old) =>
      !identical(old.samples, samples) || old.neutralColor != neutralColor;
}
