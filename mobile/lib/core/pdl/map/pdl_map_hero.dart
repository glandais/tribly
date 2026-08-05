import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../theme/pdl_colors.dart';
import '../../theme/pdl_icons.dart';
import '../../theme/pdl_tokens.dart';
import '../pdl_scrim.dart';
import '../pdl_section_header.dart';
import '../pdl_setting_row.dart';
import '../pdl_switch.dart';
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
  const PdlMapStyleOption({
    required this.id,
    required this.label,
    this.groupLabel,
  });

  final String id;

  /// Libellé déjà localisé par l'appelant.
  final String label;

  /// L'intitulé de la section, **déjà localisé**. Le sélecteur pose un titre
  /// chaque fois qu'il change d'une option à la suivante — il ne regroupe pas
  /// lui-même et ne re-trie pas : la liste arrive dans l'ordre servi, et deux
  /// tronçons portant le même intitulé donneraient deux sections, ce qui est la
  /// bonne lecture d'un serveur qui les a séparés. `null` partout = pas de
  /// titres, la liste plate d'avant.
  final String? groupLabel;
}

/// Les libellés du hero. `core/pdl` ne traduit rien : l'écran les fournit.
@immutable
class PdlMapHeroLabels {
  const PdlMapHeroLabels({
    required this.enterFullscreen,
    required this.exitFullscreen,
    required this.chooseBackground,
    required this.backgroundSheetTitle,
    this.hillshade,
  });

  final String enterFullscreen;
  final String exitFullscreen;
  final String chooseBackground;
  final String backgroundSheetTitle;

  /// L'intitulé de l'interrupteur d'ombrage. `null` = l'app de cet écran n'en
  /// propose pas, la ligne n'est pas rendue.
  final String? hillshade;
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
///
/// Cette route suit la **configuration vivante** du hero, et non la copie prise
/// à la poussée : le constructeur d'une `MaterialPageRoute` n'est appelé
/// qu'une fois, si bien qu'un `mapBuilder` et un `selectedStyleId` recopiés à
/// cet instant y resteraient figés. L'écran sous la route, lui, se reconstruit
/// bien quand son fond change — c'est cette reconstruction que le hero
/// republie, faute de quoi choisir un fond en plein écran ne change rien.
class PdlMapHero extends StatefulWidget {
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
    this.hillshadeEnabled,
    this.onHillshadeChanged,
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

  /// L'état de l'ombrage du relief. `null` = le déploiement ne sert aucune
  /// source d'élévation (`ConfigDto.terrain`), et l'interrupteur disparaît —
  /// plutôt qu'un réglage sans effet.
  final bool? hillshadeEnabled;
  final ValueChanged<bool>? onHillshadeChanged;

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

  /// Vrai si le contexte est celui de la carte **plein écran**.
  ///
  /// [mapBuilder] est appelé dans les deux, et une carte encastrée dans une
  /// liste ne se pilote pas comme une carte plein écran : la première doit
  /// laisser le doigt au défilement de la page, la seconde est là pour être
  /// manipulée. Sans ce témoin, l'appelant ne peut pas faire la différence.
  static bool isFullscreenOf(BuildContext context) =>
      context
          .dependOnInheritedWidgetOfExactType<_PdlMapHeroScope>()
          ?.isFullscreen ??
      false;

  @override
  State<PdlMapHero> createState() => _PdlMapHeroState();
}

class _PdlMapHeroState extends State<PdlMapHero> {
  /// La configuration courante du hero, republiée à chaque reconstruction.
  ///
  /// C'est le seul lien entre l'écran, qui reste monté sous la route, et la
  /// page plein écran, qui n'est construite qu'une fois.
  late final ValueNotifier<PdlMapHero> _config = ValueNotifier<PdlMapHero>(
    widget,
  );

  /// Vrai tant que la page plein écran poussée d'ici est à l'écran : elle
  /// écoute [_config], qu'on ne peut donc pas libérer sous ses pieds.
  bool _fullscreenOpen = false;

  /// Vrai quand une republication est déjà en attente de fin d'image.
  bool _publishScheduled = false;

  @override
  void didUpdateWidget(PdlMapHero oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (!_fullscreenOpen) {
      // Personne n'écoute : la page plein écran lira [_config] à sa poussée.
      _config.value = widget;
      return;
    }
    // Publier ici notifierait un auditeur qui vit dans **un autre sous-arbre**
    // — la route poussée — au beau milieu de la phase de construction :
    // `markNeedsBuild` y est interdit, et l'auditeur reste alors sur sa
    // dernière valeur, page plein écran figée et inerte. La republication
    // attend donc la fin de l'image.
    if (_publishScheduled) return;
    _publishScheduled = true;
    WidgetsBinding.instance.addPostFrameCallback((Duration _) {
      _publishScheduled = false;
      if (mounted) _config.value = widget;
    });
  }

  @override
  void dispose() {
    if (!_fullscreenOpen) _config.dispose();
    super.dispose();
  }

  /// Vrai quand l'ombrage du relief est proposable : une source servie, un
  /// intitulé et de quoi prévenir l'écran.
  static bool _hasHillshade(PdlMapHero c) =>
      c.hillshadeEnabled != null &&
      c.onHillshadeChanged != null &&
      c.labels.hillshade != null;

  @override
  Widget build(BuildContext context) {
    final Widget stack = _buildStack(context, widget);
    final Widget sized = widget.height == null
        ? stack
        : SizedBox(height: widget.height, child: stack);

    // Une carte est un fond sombre par nature (et un fond clair l'est aussi
    // sous le voile) : la barre système passe en clair, sans exception.
    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: SystemUiOverlayStyle.light,
      child: sized,
    );
  }

  /// Construit la pile à partir de [c], la configuration à honorer — celle du
  /// widget en place, ou celle republiée par l'écran quand on est en plein
  /// écran.
  Widget _buildStack(BuildContext context, PdlMapHero c) {
    final bool isFullscreen = widget.isFullscreen;
    final List<Widget> buttons = <Widget>[
      if (c.showFullscreenButton)
        PdlMapButton(
          icon: isFullscreen ? PdlIcons.fullscreenExit : PdlIcons.fullscreen,
          semanticLabel: isFullscreen
              ? c.labels.exitFullscreen
              : c.labels.enterFullscreen,
          onPressed: () => _toggleFullscreen(context),
        ),
      if ((c.styles.isNotEmpty && c.onStyleSelected != null) ||
          _hasHillshade(c))
        PdlMapButton(
          icon: PdlIcons.layers,
          semanticLabel: c.labels.chooseBackground,
          onPressed: () => _openStyleSheet(context, c),
        ),
      ...c.extraButtons,
    ];

    return Stack(
      fit: StackFit.expand,
      children: <Widget>[
        _PdlMapHeroScope(
          isFullscreen: isFullscreen,
          child: Builder(builder: c.mapBuilder),
        ),
        if (c.topScrim)
          const Positioned(left: 0, right: 0, top: 0, child: PdlScrim.top()),
        if (c.bottomScrim)
          const Positioned(
            left: 0,
            right: 0,
            bottom: 0,
            child: PdlScrim.bottom(),
          ),
        if (c.pill != null)
          Positioned(
            left: PdlSpacing.sectionTightH,
            right: PdlSpacing.sectionTightH,
            top: PdlSpacing.sectionTightV,
            child: Align(child: c.pill),
          ),
        if (buttons.isNotEmpty)
          Positioned(
            right: PdlSpacing.sectionTightV,
            top: 0,
            bottom: 0,
            child: Center(child: PdlMapButtonColumn(children: buttons)),
          ),
        if (c.floatingCard != null)
          Positioned(
            left: PdlSpacing.sectionTightV,
            right: PdlSpacing.sectionTightV,
            bottom: PdlSpacing.sectionTightV,
            child: c.floatingCard!,
          ),
        ?c.foreground,
      ],
    );
  }

  Future<void> _toggleFullscreen(BuildContext context) async {
    if (widget.isFullscreen) {
      await Navigator.of(context).maybePop();
      return;
    }
    _fullscreenOpen = true;
    await Navigator.of(context).push<void>(
      MaterialPageRoute<void>(
        fullscreenDialog: true,
        // La page se réabonne à la configuration vivante du hero : un fond
        // choisi ici passe par l'écran, qui se reconstruit et republie — c'est
        // ce qui fait arriver la nouvelle URL de style jusqu'à `mapBuilder`.
        builder: (BuildContext context) => Scaffold(
          body: ValueListenableBuilder<PdlMapHero>(
            valueListenable: _config,
            builder: (BuildContext context, PdlMapHero c, Widget? _) =>
                PdlMapHero(
                  mapBuilder: c.mapBuilder,
                  labels: c.labels,
                  topScrim: c.topScrim,
                  bottomScrim: c.bottomScrim,
                  styles: c.styles,
                  selectedStyleId: c.selectedStyleId,
                  onStyleSelected: c.onStyleSelected,
                  hillshadeEnabled: c.hillshadeEnabled,
                  onHillshadeChanged: c.onHillshadeChanged,
                  extraButtons: c.extraButtons,
                  isFullscreen: true,
                ),
          ),
        ),
      ),
    );
    _fullscreenOpen = false;
    // L'écran a pu être démonté pendant que sa page plein écran vivait : la
    // libération de [_config] lui avait alors été refusée, elle a lieu ici.
    if (!mounted) _config.dispose();
  }

  Future<void> _openStyleSheet(BuildContext context, PdlMapHero config) async {
    final PdlColors c = context.pdl;
    // Par `PdlSheet` et non par `showModalBottomSheet` : c'est la règle du
    // C5, et elle vaut aussi pour la bibliothèque elle-même.
    final String? picked = await PdlSheet.show<String>(
      context: context,
      builder: (BuildContext sheetContext) => PdlSheet(
        title: config.labels.backgroundSheetTitle,
        children: <Widget>[
          // **Dans l'ordre servi** : le serveur classe ses fonds, le client
          // ne re-trie pas.
          for (final (int i, PdlMapStyleOption style)
              in config.styles.indexed) ...<Widget>[
            if (style.groupLabel != null &&
                (i == 0 || style.groupLabel != config.styles[i - 1].groupLabel))
              Padding(
                padding: const EdgeInsets.only(top: PdlSpacing.sectionTightV),
                child: PdlSectionHeader(title: style.groupLabel!),
              ),
            PdlSettingRow(
              title: style.label,
              onTap: () => Navigator.of(sheetContext).pop(style.id),
              trailing: style.id == config.selectedStyleId
                  ? Icon(PdlIcons.check, size: 20, color: c.primary)
                  : const SizedBox.shrink(),
            ),
          ],
          // Le relief est un réglage **du** fond, pas un choix **de** fond : il
          // vient après la liste et, contrairement à elle, ne referme pas la
          // feuille — on veut pouvoir juger de l'ombrage puis changer de fond.
          if (_hasHillshade(config))
            _HillshadeRow(
              label: config.labels.hillshade!,
              initialValue: config.hillshadeEnabled!,
              // Le hero peut avoir été republié pendant que la feuille est
              // ouverte : on prévient l'écran par le rappel **courant**, pas
              // par celui capturé à l'ouverture.
              onChanged: (bool on) =>
                  _config.value.onHillshadeChanged?.call(on),
            ),
        ],
      ),
    );
    if (picked != null) _config.value.onStyleSelected?.call(picked);
  }
}

/// La ligne « Ombrage du relief » de la feuille des fonds.
///
/// Elle tient son propre état parce que la feuille est construite une fois :
/// `PdlSheet` n'est pas reconstruit quand l'écran l'est, si bien qu'un
/// interrupteur piloté seulement par la propriété resterait figé dans sa
/// position d'origine jusqu'à la fermeture. L'écran reste la source de vérité —
/// il reçoit le changement et le persiste ; ce témoin local n'est que l'écho
/// immédiat que la feuille doit à celui qui vient d'appuyer.
class _HillshadeRow extends StatefulWidget {
  const _HillshadeRow({
    required this.label,
    required this.initialValue,
    required this.onChanged,
  });

  final String label;
  final bool initialValue;
  final ValueChanged<bool> onChanged;

  @override
  State<_HillshadeRow> createState() => _HillshadeRowState();
}

class _HillshadeRowState extends State<_HillshadeRow> {
  late bool _value = widget.initialValue;

  void _toggle() {
    setState(() => _value = !_value);
    widget.onChanged(_value);
  }

  @override
  Widget build(BuildContext context) => PdlSettingRow(
    title: widget.label,
    onTap: _toggle,
    trailing: PdlSwitch(
      value: _value,
      // La ligne entière est déjà une cible tactile de 44 px ; l'interrupteur
      // n'en ouvre pas une seconde qui ferait diverger les deux états.
      onChanged: (bool _) => _toggle(),
      semanticLabel: widget.label,
    ),
  );
}

/// Porte [PdlMapHero.isFullscreenOf] jusqu'au `mapBuilder`.
class _PdlMapHeroScope extends InheritedWidget {
  const _PdlMapHeroScope({required this.isFullscreen, required super.child});

  final bool isFullscreen;

  @override
  bool updateShouldNotify(_PdlMapHeroScope oldWidget) =>
      oldWidget.isFullscreen != isFullscreen;
}
