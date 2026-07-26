import 'package:flutter/material.dart';

import '../theme/pdl_colors.dart';
import '../theme/pdl_tokens.dart';
import 'pdl_sheet.dart';

/// C7 — La feuille à crans, posée sur une carte.
///
/// Trois crans, et chacun répond à une question : `.18` « qu'est-ce que je
/// regarde ? » (la poignée et une ligne de résumé), `.5` « et dans le
/// détail ? », `.92` « montre-moi la liste ». `snap: true` : elle ne s'arrête
/// pas entre deux.
///
/// Elle **remplace** le `DraggableScrollableSheet` bridé à `maxChildSize: 0.7`
/// de `route_detail_page.dart` — 0,7 laisse un tiers de carte qu'on ne regarde
/// plus et coupe la liste au milieu.
///
/// Deux points de construction, tous deux liés au fait qu'une feuille à crans
/// se reconstruit **à chaque pixel de glissement** :
///
/// * **Le [ValueListenableBuilder] n'englobe que l'en-tête.** L'en-tête change
///   de composition selon le cran ([headerBuilder] reçoit la fraction
///   courante) ; si le builder englobait la feuille entière, les 40 lignes de
///   la liste se reconstruiraient à 60 fois par seconde pendant le geste.
/// * **Le pied est un [Positioned] du [Stack] interne, pas le dernier enfant
///   d'une colonne** : il doit rester collé au bas de la feuille pendant que
///   le corps défile sous lui.
class PdlDetentSheet extends StatefulWidget {
  const PdlDetentSheet({
    super.key,
    required this.bodyBuilder,
    this.headerBuilder,
    this.footer,
    this.controller,
    this.detents = const <double>[0.18, 0.5, 0.92],
    this.initialDetentIndex = 1,
    this.onDetentChanged,
  }) : assert(detents.length >= 2, 'Une feuille à crans en a au moins deux.');

  /// Le corps reçoit le [ScrollController] de la feuille : sans lui, tirer sur
  /// la liste ne fait pas monter la feuille.
  final Widget Function(BuildContext context, ScrollController controller)
  bodyBuilder;

  /// En-tête, reconstruit à chaque changement de fraction — et lui seul.
  final Widget Function(BuildContext context, double extent)? headerBuilder;

  /// Pied fixe, collé au bas de la feuille.
  final Widget? footer;

  final DraggableScrollableController? controller;

  /// Les crans, en fraction de la hauteur disponible.
  final List<double> detents;

  final int initialDetentIndex;

  /// Notifié à chaque franchissement de cran, jamais en continu.
  final ValueChanged<double>? onDetentChanged;

  @override
  State<PdlDetentSheet> createState() => _PdlDetentSheetState();
}

class _PdlDetentSheetState extends State<PdlDetentSheet> {
  late final ValueNotifier<double> _extent = ValueNotifier<double>(
    widget.detents[widget.initialDetentIndex],
  );
  double _lastSnapped = -1;

  @override
  void dispose() {
    _extent.dispose();
    super.dispose();
  }

  bool _onNotification(DraggableScrollableNotification notification) {
    _extent.value = notification.extent;

    // Le rappel de cran ne suit pas le doigt : il ne parle qu'une fois posé.
    final double nearest = widget.detents.reduce(
      (double a, double b) =>
          (a - notification.extent).abs() < (b - notification.extent).abs()
          ? a
          : b,
    );
    if ((nearest - notification.extent).abs() < 0.01 &&
        nearest != _lastSnapped) {
      _lastSnapped = nearest;
      widget.onDetentChanged?.call(nearest);
    }
    return false;
  }

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final List<double> detents = widget.detents;

    return NotificationListener<DraggableScrollableNotification>(
      onNotification: _onNotification,
      child: DraggableScrollableSheet(
        controller: widget.controller,
        initialChildSize: detents[widget.initialDetentIndex],
        minChildSize: detents.first,
        maxChildSize: detents.last,
        snap: true,
        snapSizes: detents.sublist(1, detents.length - 1),
        builder: (BuildContext context, ScrollController scrollController) {
          return DecoratedBox(
            decoration: BoxDecoration(
              color: c.surfaceRaised,
              borderRadius: PdlRadii.sheetTop,
              border: Border(top: BorderSide(color: c.borderSubtle)),
              boxShadow: PdlShadows.sheet,
            ),
            child: ClipRRect(
              borderRadius: PdlRadii.sheetTop,
              child: Stack(
                children: <Widget>[
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: <Widget>[
                      const PdlSheetHandle(),
                      if (widget.headerBuilder != null)
                        ValueListenableBuilder<double>(
                          valueListenable: _extent,
                          builder: (BuildContext context, double extent, _) =>
                              widget.headerBuilder!(context, extent),
                        ),
                      Expanded(
                        child: widget.bodyBuilder(context, scrollController),
                      ),
                    ],
                  ),
                  if (widget.footer != null)
                    Positioned(
                      left: 0,
                      right: 0,
                      bottom: 0,
                      child: widget.footer!,
                    ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
