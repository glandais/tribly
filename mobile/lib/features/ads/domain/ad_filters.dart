import 'package:flutter/foundation.dart';

import '../../../api/generated/export.dart';

/// Ce qui identifie un jeu de résultats d'annonces.
///
/// Recherche, type, colonne et sens de tri : les quatre participent à la clé
/// de famille de `adListProvider`, donc **changer l'un d'eux construit un
/// notifier neuf** plutôt que de muter celui en place. C'est le contrat que le
/// lot 3 a posé pour les parcours et qu'un test protège — deux jeux de
/// résultats ne peuvent pas se mélanger.
///
/// Le tri en fait partie bien qu'il ne filtre rien : il change l'ordre, donc
/// la page 0 n'est plus la même, donc reprendre la pagination en cours
/// entrelacerait deux ordres.
@immutable
class AdFilters {
  const AdFilters({
    required this.teamSlug,
    this.search,
    this.adType,
    this.sortBy = AdSortBy.dateTime,
    this.sortDir = SortDirection.desc,
  });

  /// Portée **imposée** par l'écran : la rubrique Annonces appartient à une
  /// équipe, et il n'existe pas de liste d'annonces inter-équipes au contrat.
  final String teamSlug;

  final String? search;
  final AdType? adType;

  /// Défauts du serveur, répétés ici pour que la chip de tri sache quoi
  /// afficher avant le premier appel.
  final AdSortBy sortBy;
  final SortDirection sortDir;

  /// Vrai dès qu'un critère **restreint** le jeu de résultats. Le tri n'en est
  /// pas : il ne peut pas rendre une liste vide, donc il n'a rien à faire dans
  /// le diagnostic d'un cul-de-sac.
  bool get isFiltered => (search?.trim().isNotEmpty ?? false) || adType != null;

  /// Les filtres levés, le tri conservé — « Tout réinitialiser » remet la
  /// liste en vue, il ne réordonne pas ce que l'utilisateur a choisi de lire.
  AdFilters get cleared =>
      AdFilters(teamSlug: teamSlug, sortBy: sortBy, sortDir: sortDir);

  AdFilters copyWith({
    String? search,
    bool clearSearch = false,
    AdType? adType,
    bool clearAdType = false,
    AdSortBy? sortBy,
    SortDirection? sortDir,
  }) {
    return AdFilters(
      teamSlug: teamSlug,
      search: clearSearch ? null : (search ?? this.search),
      adType: clearAdType ? null : (adType ?? this.adType),
      sortBy: sortBy ?? this.sortBy,
      sortDir: sortDir ?? this.sortDir,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is AdFilters &&
          other.teamSlug == teamSlug &&
          other.search == search &&
          other.adType == adType &&
          other.sortBy == sortBy &&
          other.sortDir == sortDir;

  @override
  int get hashCode => Object.hash(teamSlug, search, adType, sortBy, sortDir);

  @override
  String toString() =>
      'AdFilters($teamSlug, search: $search, type: $adType, '
      'sort: ${sortBy.json} ${sortDir.json})';
}
