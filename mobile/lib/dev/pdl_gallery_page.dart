// Galerie de composants `core/pdl` — page de DEBUG.
//
// Elle n'est **pas** enregistrée dans le `GoRouter` : elle n'a rien à faire
// dans une app livrée, et une route de production coûterait un chemin public
// et une entrée de deeplink. Pour l'ouvrir, poussez-la depuis n'importe où :
//
// ```dart
// Navigator.of(context).push(
//   MaterialPageRoute<void>(builder: (_) => const PdlGalleryPage()),
// );
// ```
//
// Le plus court en développement est de remplacer temporairement le `home` de
// `PedalonsApp`, ou d'ajouter le push ci-dessus derrière un appui long sur le
// logotype. La page porte sa propre bascule clair / sombre : elle applique le
// thème avec `Theme(data: PedalonsTheme.build(brightness))`, ce qui permet de
// vérifier les deux modes sans toucher aux préférences de l'app.

import 'package:flutter/material.dart';

import '../core/pdl/pdl.dart';
import '../core/theme/enum_colors.dart' show PdlTone;
import '../core/theme/pdl_colors.dart';
import '../core/theme/pdl_icons.dart';
import '../core/theme/pdl_tokens.dart';
import '../core/theme/pdl_typography.dart';
import '../core/theme/pedalons_theme.dart';

/// Galerie des 20 primitives de la vague A, dans leurs variantes, en clair et
/// en sombre.
class PdlGalleryPage extends StatefulWidget {
  const PdlGalleryPage({super.key});

  @override
  State<PdlGalleryPage> createState() => _PdlGalleryPageState();
}

class _PdlGalleryPageState extends State<PdlGalleryPage> {
  Brightness _brightness = Brightness.light;

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = PedalonsTheme.build(_brightness);
    return Theme(
      data: theme,
      child: Builder(
        builder: (BuildContext context) => Scaffold(
          backgroundColor: context.pdl.bg,
          appBar: AppBar(
            title: const Text('Galerie core/pdl'),
            // La bascule vit dans le `bottom` et non dans les `actions` :
            // `PdlSegmented` répartit ses positions avec `Expanded`, il lui
            // faut donc une largeur bornée — ce que la rangée d'actions d'une
            // `AppBar`, de largeur libre, ne fournit pas.
            bottom: PreferredSize(
              preferredSize: const Size.fromHeight(62),
              child: Padding(
                padding: const EdgeInsets.fromLTRB(
                  PdlSpacing.section,
                  0,
                  PdlSpacing.section,
                  9,
                ),
                child: PdlSegmented<Brightness>(
                  segments: const <PdlSegment<Brightness>>[
                    PdlSegment<Brightness>(
                      value: Brightness.light,
                      label: 'Clair',
                    ),
                    PdlSegment<Brightness>(
                      value: Brightness.dark,
                      label: 'Sombre',
                    ),
                  ],
                  value: _brightness,
                  onChanged: (Brightness b) => setState(() => _brightness = b),
                ),
              ),
            ),
          ),
          body: const _GalleryBody(),
        ),
      ),
    );
  }
}

class _GalleryBody extends StatefulWidget {
  const _GalleryBody();

  @override
  State<_GalleryBody> createState() => _GalleryBodyState();
}

class _GalleryBodyState extends State<_GalleryBody> {
  bool _switchOn = true;
  bool _switchOff = false;
  String _segment = 'mois';
  String? _search;
  bool _chipSelected = true;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;

    PdlTone tone(Color fill, PdlSoftPair pair) =>
        PdlTone(fill: fill, soft: pair.background, onSoft: pair.foreground);

    return ListView(
      padding: const EdgeInsets.symmetric(vertical: PdlSpacing.section),
      children: <Widget>[
        // ── A1 ────────────────────────────────────────────────────────────
        _Block(
          title: 'A1 · PdlSkeleton',
          children: <Widget>[
            const PdlSkeleton(width: double.infinity, height: 120),
            const SizedBox(height: 8),
            Row(
              children: const <Widget>[
                PdlSkeleton.circle(size: 40),
                SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      PdlSkeleton.text(width: 160, height: 16),
                      SizedBox(height: 8),
                      PdlSkeleton.text(width: 100),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),

        // ── A2 ────────────────────────────────────────────────────────────
        _Block(
          title: 'A2 · PdlBadge',
          children: <Widget>[
            Wrap(
              spacing: 6,
              runSpacing: 6,
              children: <Widget>[
                PdlBadge(label: 'Sortie', tone: tone(c.accentBlue, c.softBlue)),
                PdlBadge(
                  label: 'Publication',
                  tone: tone(c.accentGrape, c.softGrape),
                ),
                PdlBadge(label: 'Voyage', tone: tone(c.accentTeal, c.softTeal)),
                PdlBadge(
                  label: 'Annonce',
                  tone: tone(c.accentOrange, c.softOrange),
                ),
                PdlBadge(label: 'Publié', tone: tone(c.success, c.softGreen)),
                PdlBadge(label: 'Brouillon', tone: tone(c.neutral, c.softGray)),
                PdlBadge(label: 'Annulé', tone: tone(c.danger, c.softRed)),
                PdlBadge(label: 'Terminée', tone: tone(c.neutral, c.softDone)),
                PdlBadge(
                  label: 'Inscrit',
                  icon: PdlIcons.check,
                  tone: tone(c.primary, c.softIndigo),
                ),
                PdlBadge(
                  label: 'Public',
                  icon: PdlIcons.visibilityPublic,
                  tone: tone(c.accentBlue, c.softBlue),
                ),
              ],
            ),
            const SizedBox(height: 10),
            const _Caption('taille lg · aplat (catégories de col)'),
            Wrap(
              spacing: 6,
              runSpacing: 6,
              children: <Widget>[
                PdlBadge(
                  label: 'Organisateur',
                  size: PdlBadgeSize.lg,
                  tone: tone(c.accentBlue, c.softBlue),
                ),
                PdlBadge(
                  label: 'HC',
                  filled: true,
                  tone: tone(c.accentGrape, c.softGrape),
                ),
                PdlBadge(
                  label: 'Cat. 1',
                  filled: true,
                  tone: tone(c.danger, c.softRed),
                ),
                PdlBadge(
                  label: 'Cat. 3',
                  filled: true,
                  tone: PdlTone(
                    fill: c.warning,
                    soft: c.warningSoft,
                    onSoft: c.warningOnSoft,
                    onFill: c.neutralOnSoft,
                    filledStyle: true,
                  ),
                ),
                PdlBadge(
                  label: 'Cat. 4',
                  filled: true,
                  tone: tone(c.success, c.softGreen),
                ),
              ],
            ),
          ],
        ),

        // ── A3 ────────────────────────────────────────────────────────────
        _Block(
          title: 'A3 · PdlButton',
          children: <Widget>[
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: <Widget>[
                PdlButton(label: 'Rejoindre', onPressed: () {}),
                PdlButton(
                  label: 'Quitter',
                  variant: PdlButtonVariant.outline,
                  onPressed: () {},
                ),
                PdlButton(
                  label: 'Supprimer le compte',
                  variant: PdlButtonVariant.danger,
                  onPressed: () {},
                ),
                PdlButton(
                  label: 'Voir tout',
                  variant: PdlButtonVariant.text,
                  onPressed: () {},
                ),
              ],
            ),
            const SizedBox(height: 10),
            const _Caption('size sm · icône · pilule · désactivé · en cours'),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: <Widget>[
                PdlButton(
                  label: 'Rejoindre',
                  size: PdlButtonSize.sm,
                  onPressed: () {},
                ),
                PdlButton(
                  label: 'GPX',
                  icon: PdlIcons.gpx,
                  size: PdlButtonSize.sm,
                  variant: PdlButtonVariant.outline,
                  onPressed: () {},
                ),
                PdlButton(
                  label: 'Filtrer',
                  icon: PdlIcons.filter,
                  pill: true,
                  variant: PdlButtonVariant.outline,
                  onPressed: () {},
                ),
                const PdlButton(label: 'Complet', enabled: false),
                const PdlButton(
                  label: "S'inscrire",
                  loading: true,
                  loadingLabel: 'Inscription...',
                ),
              ],
            ),
            const SizedBox(height: 10),
            const _Caption('fullWidth'),
            PdlButton(
              label: "S'inscrire à cette sortie",
              fullWidth: true,
              onPressed: () {},
            ),
          ],
        ),

        // ── A4 ────────────────────────────────────────────────────────────
        _Block(
          title: 'A4 · PdlAvatar',
          children: <Widget>[
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: const <Widget>[
                PdlAvatar(name: 'Nantes Peloton', size: 56),
                SizedBox(width: 12),
                PdlAvatar(name: 'Gabriel Landais', size: 40),
                SizedBox(width: 12),
                PdlAvatar(name: 'Roger Rabbit', size: 32),
                SizedBox(width: 12),
                PdlAvatar(name: 'Claire Vasseur', size: 26),
                SizedBox(width: 12),
                PdlAvatar(name: 'Marc', size: 20),
                SizedBox(width: 12),
                PdlAvatar(name: 'Ada', size: 16),
              ],
            ),
            const SizedBox(height: 10),
            const _Caption(
              "l'utilisateur courant est toujours primary · anneau de grappe",
            ),
            Row(
              children: const <Widget>[
                PdlAvatar(
                  name: 'Gabriel Landais',
                  size: 40,
                  isCurrentUser: true,
                ),
                SizedBox(width: 12),
                PdlAvatar(name: 'Hélène Ebrard', size: 26, ring: true),
                PdlAvatar(name: 'Ines Barbot', size: 26, ring: true),
                PdlAvatar(name: 'Yann Meunier', size: 26, ring: true),
              ],
            ),
          ],
        ),

        // ── A5 ────────────────────────────────────────────────────────────
        _Block(
          title: 'A5 · PdlCard',
          children: <Widget>[
            PdlCard(
              onTap: () {},
              child: Text(
                'Carte standard, appuyable (échelle 0.98)',
                style: t.body,
              ),
            ),
            const SizedBox(height: 8),
            PdlCard(
              selected: true,
              child: Text('selected — bordure 2 px primary', style: t.body),
            ),
            const SizedBox(height: 8),
            PdlCard(
              flat: true,
              padding: PdlCardPadding.tight,
              child: Text('flat + padding tight (12)', style: t.body),
            ),
          ],
        ),

        // ── A6 / A7 ───────────────────────────────────────────────────────
        _Block(
          title: 'A6 · PdlStat · A7 · PdlStatRow',
          children: <Widget>[
            PdlStatRow(
              stats: <PdlStat>[
                const PdlStat(icon: PdlIcons.distance, value: '46,3 km'),
                const PdlStat(
                  icon: PdlIcons.elevationUp,
                  value: '187 m',
                  trend: PdlStatTrend.up,
                ),
                const PdlStat(
                  icon: PdlIcons.elevationDown,
                  value: '-188 m',
                  trend: PdlStatTrend.down,
                ),
                const PdlStat(
                  icon: PdlIcons.people,
                  value: '40',
                  label: 'participants',
                ),
              ],
            ),
            const SizedBox(height: 10),
            const _Caption('nowrap · big'),
            PdlStatRow(
              nowrap: true,
              stats: const <PdlStat>[
                PdlStat(icon: PdlIcons.time, value: '19:30'),
                PdlStat(icon: PdlIcons.speed, value: '26 km/h'),
                PdlStat(icon: PdlIcons.distance, value: '66,5 km'),
              ],
            ),
            const SizedBox(height: 10),
            Row(
              children: const <Widget>[
                PdlStat(value: '46,3 km', label: 'Distance', big: true),
                SizedBox(width: 24),
                PdlStat(
                  value: '187 m',
                  label: 'Dénivelé',
                  big: true,
                  trend: PdlStatTrend.up,
                ),
              ],
            ),
          ],
        ),

        // ── A8 / A9 / A10 ─────────────────────────────────────────────────
        _Block(
          title:
              'A8 · PdlProgressBar · A9 · PdlColorTrack · A10 · PdlNumberPill',
          children: <Widget>[
            const PdlProgressBar(value: 0.4),
            const SizedBox(height: 8),
            const PdlProgressBar(value: 0.85),
            const SizedBox(height: 8),
            const PdlProgressBar(value: 1),
            const SizedBox(height: 12),
            Row(
              children: <Widget>[
                PdlColorTrack(color: multiTrackColor(0)),
                const SizedBox(width: 8),
                PdlColorTrack(color: multiTrackColor(1)),
                const SizedBox(width: 8),
                PdlColorTrack(color: multiTrackColor(2)),
                const SizedBox(width: 16),
                PdlColorTrack(
                  color: multiTrackColor(3),
                  shape: PdlColorTrackShape.legend,
                ),
                const SizedBox(width: 8),
                PdlColorTrack(
                  color: multiTrackColor(4),
                  shape: PdlColorTrackShape.legend,
                ),
                const SizedBox(width: 16),
                const PdlNumberPill(value: 1),
                const SizedBox(width: 8),
                const PdlNumberPill(value: 12),
              ],
            ),
          ],
        ),

        // ── A11 ───────────────────────────────────────────────────────────
        _Block(
          title: 'A11 · PdlChip',
          children: <Widget>[
            Wrap(
              spacing: PdlSpacing.chipGap,
              children: <Widget>[
                PdlChip(
                  label: 'Toutes les équipes',
                  icon: PdlIcons.filter,
                  sortStyle: true,
                  onTap: () {},
                ),
                PdlChip(
                  label: 'Tout',
                  selected: _chipSelected,
                  onTap: () => setState(() => _chipSelected = !_chipSelected),
                ),
                PdlChip(label: 'Sorties', onTap: () {}),
                PdlChip(label: 'Gravel', selected: true, onRemoved: () {}),
                PdlChip(label: '40 – 80 km', selected: true, onRemoved: () {}),
              ],
            ),
          ],
        ),

        // ── A12 / A20 ─────────────────────────────────────────────────────
        _Block(
          title: 'A12 · PdlSearchField · A20 · PdlFilterButton',
          children: <Widget>[
            Row(
              children: <Widget>[
                Expanded(
                  child: PdlSearchField(
                    value: _search,
                    hintText: 'Rechercher un parcours…',
                    clearTooltip: 'Effacer',
                    onChanged: (String? v) => setState(() => _search = v),
                  ),
                ),
                const SizedBox(width: 10),
                PdlFilterButton(
                  onPressed: () {},
                  semanticLabel: 'Filtres',
                  activeCount: 3,
                ),
              ],
            ),
            const SizedBox(height: 10),
            const _Caption('erreur · lecture seule'),
            PdlSearchField(
              value: 'abc',
              hintText: 'Rechercher',
              errorText: 'Au moins trois caractères',
              onChanged: (_) {},
            ),
            const SizedBox(height: 8),
            PdlSearchField(
              value: null,
              readOnly: true,
              hintText: 'Choisir une date…',
              onChanged: (_) {},
              onTap: () {},
            ),
          ],
        ),

        // ── A13 ───────────────────────────────────────────────────────────
        _Block(
          title: 'A13 · PdlSectionHeader',
          children: <Widget>[
            PdlSectionHeader(
              title: 'Participants',
              count: '40',
              action: PdlButton(
                label: 'Voir tout',
                variant: PdlButtonVariant.text,
                onPressed: () {},
              ),
            ),
            const PdlSectionHeader(title: 'Prochaines sorties'),
          ],
        ),

        // ── A14 / A15 ─────────────────────────────────────────────────────
        _Block(
          title: 'A14 · PdlSegmented · A15 · PdlSwitch',
          children: <Widget>[
            PdlSegmented<String>(
              segments: const <PdlSegment<String>>[
                PdlSegment<String>(value: 'mois', label: 'Mois'),
                PdlSegment<String>(value: 'agenda', label: 'Agenda'),
              ],
              value: _segment,
              onChanged: (String v) => setState(() => _segment = v),
            ),
            const SizedBox(height: 10),
            PdlSegmented<String>(
              segments: const <PdlSegment<String>>[
                PdlSegment<String>(
                  value: 'clair',
                  label: 'Clair',
                  icon: PdlIcons.theme,
                ),
                PdlSegment<String>(value: 'sombre', label: 'Sombre'),
                PdlSegment<String>(value: 'auto', label: 'Système'),
              ],
              value: 'auto',
              onChanged: (_) {},
            ),
            const SizedBox(height: 12),
            Row(
              children: <Widget>[
                PdlSwitch(
                  value: _switchOn,
                  semanticLabel: 'Actif',
                  onChanged: (bool v) => setState(() => _switchOn = v),
                ),
                const SizedBox(width: 16),
                PdlSwitch(
                  value: _switchOff,
                  semanticLabel: 'Inactif',
                  onChanged: (bool v) => setState(() => _switchOff = v),
                ),
                const SizedBox(width: 16),
                const PdlSwitch(
                  value: false,
                  onChanged: null,
                  semanticLabel: 'Désactivé',
                ),
              ],
            ),
          ],
        ),

        // ── A16 / A17 ─────────────────────────────────────────────────────
        _Block(
          title: 'A16 · PdlInfoLine · A17 · PdlSettingRow',
          padded: false,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: PdlSpacing.section,
              ),
              child: Column(
                children: <Widget>[
                  const PdlInfoLine(label: 'Distance', value: '66,5 km'),
                  const PdlInfoLine(label: 'Revêtement', value: 'Gravel'),
                  PdlInfoLine(
                    label: 'Visibilité',
                    showDivider: false,
                    valueWidget: PdlBadge(
                      label: 'Public',
                      icon: PdlIcons.visibilityPublic,
                      tone: tone(c.accentBlue, c.softBlue),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            PdlSettingRow(
              icon: PdlIcons.language,
              title: 'Langue',
              trailing: Text('Français', style: t.sub),
              showDivider: true,
              onTap: () {},
            ),
            PdlSettingRow(
              icon: PdlIcons.people,
              title: 'Contact par les membres',
              subtitle: 'Les membres de vos équipes peuvent vous écrire',
              showDivider: true,
              trailing: PdlSwitch(
                value: _switchOn,
                semanticLabel: 'Contact par les membres',
                onChanged: (bool v) => setState(() => _switchOn = v),
              ),
            ),
            PdlSettingRow(
              icon: PdlIcons.logout,
              title: 'Se déconnecter',
              destructive: true,
              onTap: () {},
            ),
          ],
        ),

        // ── A18 / A19 ─────────────────────────────────────────────────────
        _Block(
          title: 'A18 · PdlScrim · A19 · PdlBlurSurface',
          children: <Widget>[
            ClipRRect(
              borderRadius: PdlRadii.cardAll,
              child: SizedBox(
                height: 200,
                child: Stack(
                  fit: StackFit.expand,
                  children: <Widget>[
                    DecoratedBox(
                      decoration: BoxDecoration(gradient: PdlGradients.ride),
                    ),
                    const Positioned(
                      left: 0,
                      right: 0,
                      top: 0,
                      child: PdlScrim.top(),
                    ),
                    const Positioned(
                      left: 0,
                      right: 0,
                      bottom: 0,
                      child: PdlScrim.bottom(),
                    ),
                    Positioned(
                      left: 12,
                      top: 12,
                      child: PdlBlurSurface(
                        color: c.overlaySolid,
                        sigma: PdlMotion.blurOverlayButton,
                        borderRadius: PdlRadii.mdAll,
                        border: Border.all(color: c.borderSubtle),
                        child: SizedBox(
                          width: PdlMetrics.tapTarget,
                          height: PdlMetrics.tapTarget,
                          child: Icon(PdlIcons.layers, color: c.text),
                        ),
                      ),
                    ),
                    Positioned(
                      left: 12,
                      bottom: 12,
                      child: Text(
                        'Texte sur voile — jamais sans',
                        style: t.cardTitle.copyWith(color: c.onPrimary),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }
}

/// Appareillage de planche — n'appartient pas à la charte.
class _Block extends StatelessWidget {
  const _Block({
    required this.title,
    required this.children,
    this.padded = true,
  });

  final String title;
  final List<Widget> children;
  final bool padded;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;

    return Container(
      padding: const EdgeInsets.symmetric(vertical: 20),
      decoration: BoxDecoration(
        border: Border(bottom: BorderSide(color: c.borderSubtle)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.fromLTRB(
              PdlSpacing.section,
              0,
              PdlSpacing.section,
              12,
            ),
            child: Text(
              title.toUpperCase(),
              style: t.mono.copyWith(color: c.textPlaceholder),
            ),
          ),
          if (padded)
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: PdlSpacing.section,
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: children,
              ),
            )
          else
            Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: children,
            ),
        ],
      ),
    );
  }
}

class _Caption extends StatelessWidget {
  const _Caption(this.text);

  final String text;

  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.only(bottom: 6),
    child: Text(text, style: context.pdlText.mono),
  );
}
