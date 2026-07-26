// `Visibility` est à la fois un widget Flutter et un enum du contrat : ici
// c'est l'enum qui compte.
import 'package:flutter/widgets.dart' hide Visibility;

import '../../api/generated/models/ad_type.dart';
import '../../api/generated/models/climb_category.dart';
import '../../api/generated/models/publication_type.dart';
import '../../api/generated/models/status.dart';
import '../../api/generated/models/surface_type.dart';
import '../../api/generated/models/team_role.dart';
import '../../api/generated/models/visibility.dart';
import 'pdl_colors.dart';

/// La teinte d'un badge : un aplat, une paire douce, et la façon de la rendre.
///
/// Les badges de la charte sont **doux** par défaut — fond pâle, texte foncé
/// de la même famille. Une seule série fait exception, les catégories de col,
/// qui se rendent en **aplat** : d'où [filledStyle] et [onFill].
///
/// [onFill] n'est pas décoratif : `CAT3` est jaune et porte du `#212529`, là
/// où les quatre autres portent du blanc. Sans ce champ, une catégorie sur
/// cinq serait illisible.
@immutable
class PdlTone {
  const PdlTone({
    required this.fill,
    required this.soft,
    required this.onSoft,
    this.onFill = const Color(0xFFFFFFFF),
    this.filledStyle = false,
  });

  /// L'aplat de la famille — trait de rappel, point, icône pleine.
  final Color fill;

  /// Le fond doux du badge.
  final Color soft;

  /// Le texte posé sur [soft].
  final Color onSoft;

  /// Le texte posé sur [fill], quand le badge est rendu en aplat.
  final Color onFill;

  /// Vrai quand le badge de cette famille se rend en aplat et non en doux.
  final bool filledStyle;

  /// Raccourci pour une famille rendue en doux : la paire vient du jeton, et
  /// [fill] est l'aplat correspondant.
  ///
  /// Publique parce que tous les badges ne sortent pas d'une énumération du
  /// contrat : « ✓ Inscrit » (famille indigo) et « Terminée » (famille grise
  /// foncée) sont des états dérivés côté client, sans champ d'API qui les
  /// porte.
  PdlTone.pair(PdlSoftPair pair, this.fill)
    : soft = pair.background,
      onSoft = pair.foreground,
      onFill = const Color(0xFFFFFFFF),
      filledStyle = false;
}

// ── Statut de publication ───────────────────────────────────────────────────

extension StatusTone on Status {
  PdlTone tone(PdlColors c) => switch (this) {
    Status.draft => PdlTone.pair(c.softGray, c.neutral),
    Status.published => PdlTone.pair(c.softGreen, c.success),
    Status.cancelled => PdlTone.pair(c.softRed, c.danger),
    Status.$unknown => PdlTone.pair(c.softGray, c.neutral),
  };
}

// ── Rôle dans l'équipe ──────────────────────────────────────────────────────

extension TeamRoleTone on TeamRole {
  PdlTone tone(PdlColors c) => switch (this) {
    TeamRole.admin => PdlTone.pair(c.softGrape, c.accentGrape),
    TeamRole.organizer => PdlTone.pair(c.softBlue, c.accentBlue),
    TeamRole.member => PdlTone.pair(c.softGray, c.neutral),
    TeamRole.$unknown => PdlTone.pair(c.softGray, c.neutral),
  };
}

// ── Revêtement ──────────────────────────────────────────────────────────────

extension SurfaceTypeTone on SurfaceType {
  /// `road` est le seul cas où l'aplat n'appartient pas à la famille du fond
  /// doux : la charte lui donne le near-black `#2e2e2e` en trait de rappel de
  /// tracé, tout en le badgeant en gris.
  PdlTone tone(PdlColors c) => switch (this) {
    SurfaceType.road => PdlTone.pair(c.softGray, c.accentDark),
    SurfaceType.gravel => PdlTone.pair(c.softOrange, c.accentOrange),
    SurfaceType.mtb => PdlTone.pair(c.softGreen, c.success),
    SurfaceType.mixed => PdlTone.pair(c.softTeal, c.accentTeal),
    SurfaceType.$unknown => PdlTone.pair(c.softGray, c.neutral),
  };
}

// ── Catégorie de col ────────────────────────────────────────────────────────

extension ClimbCategoryTone on ClimbCategory {
  /// La **seule** famille rendue en aplat (`filledStyle`). `cat3` porte du
  /// texte foncé, les autres du blanc.
  PdlTone tone(PdlColors c) => switch (this) {
    ClimbCategory.hc => PdlTone(
      fill: c.accentGrape,
      soft: c.softGrape.background,
      onSoft: c.softGrape.foreground,
      filledStyle: true,
    ),
    ClimbCategory.cat1 => PdlTone(
      fill: c.danger,
      soft: c.softRed.background,
      onSoft: c.softRed.foreground,
      filledStyle: true,
    ),
    ClimbCategory.cat2 => PdlTone(
      fill: c.accentOrange,
      soft: c.softOrange.background,
      onSoft: c.softOrange.foreground,
      filledStyle: true,
    ),
    ClimbCategory.cat3 => PdlTone(
      fill: c.warning,
      soft: c.warningSoft,
      onSoft: c.warningOnSoft,
      onFill: c.neutralOnSoft,
      filledStyle: true,
    ),
    ClimbCategory.cat4 => PdlTone(
      fill: c.success,
      soft: c.softGreen.background,
      onSoft: c.softGreen.foreground,
      filledStyle: true,
    ),
    ClimbCategory.$unknown => PdlTone(
      fill: c.neutral,
      soft: c.softGray.background,
      onSoft: c.softGray.foreground,
      filledStyle: true,
    ),
  };
}

// ── Type d'annonce ──────────────────────────────────────────────────────────

extension AdTypeTone on AdType {
  PdlTone tone(PdlColors c) => switch (this) {
    AdType.sale => PdlTone.pair(c.softGreen, c.success),
    AdType.rental => PdlTone.pair(c.softIndigo, c.primary),
    AdType.wanted => PdlTone.pair(c.softOrange, c.accentOrange),
    AdType.$unknown => PdlTone.pair(c.softGray, c.neutral),
  };
}

// ── Visibilité ──────────────────────────────────────────────────────────────

extension VisibilityTone on Visibility {
  PdlTone tone(PdlColors c) => switch (this) {
    Visibility.public => PdlTone.pair(c.softBlue, c.accentBlue),
    Visibility.publicUnlisted => PdlTone.pair(c.softOrange, c.accentOrange),
    Visibility.team => PdlTone.pair(c.softGray, c.neutral),
    Visibility.$unknown => PdlTone.pair(c.softGray, c.neutral),
  };
}

// ── Type de publication ─────────────────────────────────────────────────────

extension PublicationTypeTone on PublicationType {
  PdlTone tone(PdlColors c) => switch (this) {
    PublicationType.ride => PdlTone.pair(c.softBlue, c.accentBlue),
    PublicationType.post => PdlTone.pair(c.softGrape, c.accentGrape),
    PublicationType.trip => PdlTone.pair(c.softTeal, c.accentTeal),
    PublicationType.$unknown => PdlTone.pair(c.softGray, c.neutral),
  };
}

// ── États dérivés côté client ───────────────────────────────────────────────

/// Les deux familles de badge qu'aucune énumération du contrat ne porte.
///
/// « Inscrit » se déduit de `registered`, « Terminée » de `dateTime < now`
/// (§5.2-18 : le statut `TERMINÉE` n'existe pas dans l'enum `Status`). Les
/// nommer ici évite qu'un écran ne recompose la paire à la main — et donc
/// qu'un écran se retrouve avec une autre teinte que son voisin.
abstract final class PdlDerivedTones {
  /// `.b-ins` — indigo. Inscription à une sortie ou à un groupe.
  static PdlTone registered(PdlColors c) =>
      PdlTone.pair(c.softIndigo, c.primary);

  /// `.b-done` — gris foncé. Sortie ou voyage terminé.
  static PdlTone done(PdlColors c) => PdlTone.pair(c.softDone, c.neutral);
}
