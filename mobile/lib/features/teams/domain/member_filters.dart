import 'package:flutter/foundation.dart';

import '../../../api/generated/export.dart';

/// Ce qui identifie un jeu de résultats du trombinoscope.
///
/// Même contrat que les autres listes du lot 3 et 4 : les champs entrent dans
/// la clé de famille du notifier, donc changer un filtre construit une liste
/// neuve au lieu d'entrelacer deux jeux de résultats.
@immutable
class MemberFilters {
  const MemberFilters({required this.teamSlug, this.search, this.role});

  final String teamSlug;
  final String? search;

  /// `null` ⇒ tous les rôles. L'API n'accepte qu'un rôle à la fois, les chips
  /// sont donc **exclusives** — offrir une combinaison qui ne partirait pas au
  /// serveur serait un réglage sans effet.
  final TeamRole? role;

  bool get isFiltered => (search?.trim().isNotEmpty ?? false) || role != null;

  MemberFilters get cleared => MemberFilters(teamSlug: teamSlug);

  MemberFilters copyWith({
    String? search,
    bool clearSearch = false,
    TeamRole? role,
    bool clearRole = false,
  }) => MemberFilters(
    teamSlug: teamSlug,
    search: clearSearch ? null : (search ?? this.search),
    role: clearRole ? null : (role ?? this.role),
  );

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is MemberFilters &&
          other.teamSlug == teamSlug &&
          other.search == search &&
          other.role == role;

  @override
  int get hashCode => Object.hash(teamSlug, search, role);
}
