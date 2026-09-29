import 'package:flutter/foundation.dart';

import '../../../api/generated/export.dart';

/// Les trois portées de la découverte d'équipes.
///
/// Elles sont alimentables depuis 1.5.0 : `joinable` (nouveau) porte la
/// deuxième, `minRole=MEMBER` la troisième. Avant, la chip aurait été un
/// réglage sans effet.
enum TeamDiscoveryScope {
  /// Toutes les équipes visibles sur ce domaine.
  all,

  /// Celles qui acceptent une demande d'adhésion.
  joinable,

  /// Celles dont on est déjà membre.
  mine,
}

/// L'ordre de l'annuaire : les équipes les plus peuplées d'abord
/// (`sortBy=MEMBER_COUNT`, `docs/LEDGER_*.md API-13`), le serveur départageant
/// les ex æquo par identifiant.
///
/// **Une seule constante** pour la requête (`TeamRepository.fetchTeams`) et
/// pour la mention « triées par nombre de membres » de l'écran : l'écran
/// n'annonce un tri que parce que c'est celui qu'il demande.
const TeamSortBy kTeamDiscoverySort = TeamSortBy.memberCount;

/// Ce qui identifie un jeu de résultats de la découverte.
@immutable
class TeamDiscoveryFilters {
  const TeamDiscoveryFilters({
    this.scope = TeamDiscoveryScope.all,
    this.search,
  });

  final TeamDiscoveryScope scope;
  final String? search;

  bool get isFiltered =>
      (search?.trim().isNotEmpty ?? false) || scope != TeamDiscoveryScope.all;

  TeamDiscoveryFilters get cleared => const TeamDiscoveryFilters();

  TeamDiscoveryFilters copyWith({
    TeamDiscoveryScope? scope,
    String? search,
    bool clearSearch = false,
  }) => TeamDiscoveryFilters(
    scope: scope ?? this.scope,
    search: clearSearch ? null : (search ?? this.search),
  );

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is TeamDiscoveryFilters &&
          other.scope == scope &&
          other.search == search;

  @override
  int get hashCode => Object.hash(scope, search);
}
