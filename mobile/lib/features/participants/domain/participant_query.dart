import 'package:flutter/foundation.dart';

/// De qui on liste les participants : une sortie (tous groupes, ou l'un
/// d'eux) ou un voyage.
@immutable
sealed class ParticipantSource {
  const ParticipantSource(this.teamSlug);

  final String teamSlug;
}

@immutable
final class RideParticipantSource extends ParticipantSource {
  const RideParticipantSource({
    required String teamSlug,
    required this.rideSlug,
    this.groupId,
  }) : super(teamSlug);

  final String rideSlug;

  /// `null` pour la sortie entière, tous groupes confondus.
  final String? groupId;

  @override
  bool operator ==(Object other) =>
      other is RideParticipantSource &&
      other.teamSlug == teamSlug &&
      other.rideSlug == rideSlug &&
      other.groupId == groupId;

  @override
  int get hashCode => Object.hash('ride', teamSlug, rideSlug, groupId);
}

@immutable
final class TripParticipantSource extends ParticipantSource {
  const TripParticipantSource({
    required String teamSlug,
    required this.tripSlug,
  }) : super(teamSlug);

  final String tripSlug;

  @override
  bool operator ==(Object other) =>
      other is TripParticipantSource &&
      other.teamSlug == teamSlug &&
      other.tripSlug == tripSlug;

  @override
  int get hashCode => Object.hash('trip', teamSlug, tripSlug);
}

/// Ce qui identifie une liste de participants : sa source et la recherche.
///
/// Clé de famille de `participantListProvider` : changer la recherche
/// construit un notifier neuf plutôt que de mélanger deux jeux de résultats.
@immutable
class ParticipantQuery {
  const ParticipantQuery(this.source, {this.search = ''});

  final ParticipantSource source;

  /// Déjà débattue par la feuille ; vide pour tout lister.
  final String search;

  @override
  bool operator ==(Object other) =>
      other is ParticipantQuery &&
      other.source == source &&
      other.search == search;

  @override
  int get hashCode => Object.hash(source, search);
}
