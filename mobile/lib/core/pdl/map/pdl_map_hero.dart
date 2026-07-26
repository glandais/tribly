import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../theme/pdl_colors.dart';
import '../../theme/pdl_icons.dart';
import '../../theme/pdl_tokens.dart';
import '../pdl_scrim.dart';
import '../pdl_setting_row.dart';
import '../pdl_sheet.dart';
import 'pdl_map_buttons.dart';

/// Un fond de carte proposé au sélecteur.
///
/// Le pendant DTO-libre de `MapStyleDto` : l'écran lit `ConfigDto.mapStyles[]`
/// via `mapStyleProvider` et convertit. **Aucune URL de style n'est écrite
/// ici** — changer `mapStyles` côté serveur change le sélecteur sans livrer une
/// version du client.
@immutable
class PdlMapStyleOption {
  const PdlMapStyleOption({required this.id, required this.label});

  final String id;

  /// Libellé déjà localisé par l'appelant.
  final String label;
}

/// Les libellés du hero. `core/pdl` ne traduit rien : l'écran les fournit.
@immutable
class PdlMapHeroLabels {
  const PdlMapHeroLabels({
    required this.enterFullscreen,
    required this.exitFullscreen,
    required this.chooseBackground,
    required this.backgroundSheetTitle,
  });

  final String enterFullscreen;
  final String exitFullscreen;
  final String chooseBackground;
  final String backgroundSheetTitle;
}

/// La coquille d'une carte occupant une surface, avec ses voiles et ses
/// commandes.
///
/// Elle tient trois promesses du brief §5 :
///
/// 1. `SystemUiOverlayStyle.light` **dès qu'une carte est en fond** — l'heure
///    et la batterie en noir sur une tuile sombre était le défaut de la
///    maquette 13 ;
/// 2. un `PdlScrim` haut, et un bas si demandé, pour que rien ne soit jamais
///    écrit à même la tuile ;
/// 3. plein écran et sélecteur de fond, tous deux à 44 px.
///
/// Le plein écran repasse par [mapBuilder] : la carte n'est pas déplacée dans
/// l'arbre — MapLibre n'y survivrait pas — elle est **reconstruite** dans une
/// route opaque.
class PdlMapHero extends StatelessWidget {
  const PdlMapHero({
    super.key,
    required this.mapBuilder,
    required this.labels,
    this.height,
    this.topScrim = true,
    this.bottomScrim = false,
    this.styles = const <PdlMapStyleOption>[],
    this.selectedStyleId,
    this.onStyleSelected,
    this.showFullscreenButton = true,
    this.extraButtons = const <Widget>[],
    this.pill,
    this.floatingCard,
    this.foreground,
    this.isFullscreen = false,
  });

  /// Construit la carte elle-même — typiquement un `PdlMap`. Appelé une fois
  /// dans le hero, une fois dans la page plein écran.
  final WidgetBuilder mapBuilder;

  final PdlMapHeroLabels labels;

  /// Hauteur imposée. `null` = la carte prend la place disponible.
  final double? height;

  final bool topScrim;
  final bool bottomScrim;

  /// Les fonds servis, **dans l'ordre servi**. Vide = pas de sélecteur.
  final List<PdlMapStyleOption> styles;
  final String? selectedStyleId;
  final ValueChanged<String>? onStyleSelected;

  final bool showFullscreenButton;

  /// Boutons ajoutés sous les deux boutons standards — « autour de moi »,
  /// recentrage.
  final List<Widget> extraButtons;

  /// Pilule posée en haut au centre — « Rechercher dans cette zone ».
  final Widget? pill;

  /// Carte flottante posée en bas, à la sélection.
  final Widget? floatingCard;

  /// Contenu libre posé au-dessus de tout (barre superposée, titre).
  final Widget? foreground;

  /// Vrai dans la page plein écran : le bouton bascule alors en sortie.
  final bool isFullscreen;

  @override
  Widget build(BuildContext context) {
    final Widget stack = _buildStack(context);
    final Widget sized = height == null
        ? stack
        : SizedBox(height: height, child: stack);

    // Une carte est un fond sombre par nature (et un fond clair l'est aussi
    // sous le voile) : la barre système passe en clair, sans exception.
    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: SystemUiOverlayStyle.light,
      child: sized,
    );
  }

  Widget _buildStack(BuildContext context) {
    final List<Widget> buttons = <Widget>[
      if (showFullscreenButton)
        PdlMapButton(
          icon: isFullscreen ? PdlIcons.fullscreenExit : PdlIcons.fullscreen,
          semanticLabel: isFullscreen
              ? labels.exitFullscreen
              : labels.enterFullscreen,
          onPressed: () => _toggleFullscreen(context),
        ),
      if (styles.isNotEmpty && onStyleSelected != null)
        PdlMapButton(
          icon: PdlIcons.layers,
          semanticLabel: labels.chooseBackground,
          onPressed: () => _openStyleSheet(context),
        ),
      ...extraButtons,
    ];

    return Stack(
      fit: StackFit.expand,
      children: <Widget>[
        Builder(builder: mapBuilder),
        if (topScrim)
          const Positioned(left: 0, right: 0, top: 0, child: PdlScrim.top()),
        if (bottomScrim)
          const Positioned(
            left: 0,
            right: 0,
            bottom: 0,
            child: PdlScrim.bottom(),
          ),
        if (pill != null)
          Positioned(
            left: PdlSpacing.sectionTightH,
            right: PdlSpacing.sectionTightH,
            top: PdlSpacing.sectionTightV,
            child: Align(child: pill),
          ),
        if (buttons.isNotEmpty)
          Positioned(
            right: PdlSpacing.sectionTightV,
            top: 0,
            bottom: 0,
            child: Center(child: PdlMapButtonColumn(children: buttons)),
          ),
        if (floatingCard != null)
          Positioned(
            left: PdlSpacing.sectionTightV,
            right: PdlSpacing.sectionTightV,
            bottom: PdlSpacing.sectionTightV,
            child: floatingCard!,
          ),
        ?foreground,
      ],
    );
  }

  void _toggleFullscreen(BuildContext context) {
    if (isFullscreen) {
      Navigator.of(context).maybePop();
      return;
    }
    Navigator.of(context).push<void>(
      MaterialPageRoute<void>(
        fullscreenDialog: true,
        builder: (BuildContext context) => Scaffold(
          body: PdlMapHero(
            mapBuilder: mapBuilder,
            labels: labels,
            topScrim: topScrim,
            bottomScrim: bottomScrim,
            styles: styles,
            selectedStyleId: selectedStyleId,
            onStyleSelected: onStyleSelected,
            extraButtons: extraButtons,
            isFullscreen: true,
          ),
        ),
      ),
    );
  }

  Future<void> _openStyleSheet(BuildContext context) async {
    final PdlColors c = context.pdl;
    // Par `PdlSheet` et non par `showModalBottomSheet` : c'est la règle du
    // C5, et elle vaut aussi pour la bibliothèque elle-même.
    final String? picked = await PdlSheet.show<String>(
      context: context,
      builder: (BuildContext sheetContext) => PdlSheet(
        title: labels.backgroundSheetTitle,
        children: <Widget>[
          // **Dans l'ordre servi** : le serveur classe ses fonds, le client
          // ne re-trie pas.
          for (final PdlMapStyleOption style in styles)
            PdlSettingRow(
              title: style.label,
              onTap: () => Navigator.of(sheetContext).pop(style.id),
              trailing: style.id == selectedStyleId
                  ? Icon(PdlIcons.check, size: 20, color: c.primary)
                  : const SizedBox.shrink(),
            ),
        ],
      ),
    );
    if (picked != null) onStyleSelected?.call(picked);
  }
}
