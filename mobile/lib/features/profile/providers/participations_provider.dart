import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../../core/pagination/pagination.dart';
import '../data/profile_repository.dart';

/// L'instant qui sépare « à venir » de l'historique, figé à la première
/// lecture.
///
/// Il est gelé volontairement : recalculer `DateTime.now()` à chaque page
/// ferait glisser la frontière pendant la pagination, et une sortie qui
/// commence entre deux pages apparaîtrait deux fois ou pas du tout. Il n'avance
/// que lorsqu'une participation change (`notifyParticipationChanged`), qui
/// recharge de toute façon la liste et le résumé du profil.
final participationsNowProvider = Provider<DateTime>((ref) => DateTime.now());

/// La liste paginée d'un des deux jeux.
final participationsProvider = StateNotifierProvider.autoDispose
    .family<ParticipationsNotifier, PagedListState<PublicationDto>, bool>((
      ref,
      upcoming,
    ) {
      return ParticipationsNotifier(
        ref.watch(profileRepositoryProvider),
        upcoming: upcoming,
        now: ref.watch(participationsNowProvider),
      );
    });

class ParticipationsNotifier extends PagedListNotifier<PublicationDto> {
  ParticipationsNotifier(
    this._repository, {
    required bool upcoming,
    required DateTime now,
  }) : _upcoming = upcoming,
       _now = now;

  final ProfileRepository _repository;
  final bool _upcoming;
  final DateTime _now;

  @override
  Future<PageResult<PublicationDto>> fetchPage(int page) {
    return _repository.fetchParticipations(
      upcoming: _upcoming,
      now: _now,
      page: page,
      size: pageSize,
    );
  }

  @override
  Object? itemKey(PublicationDto item) => switch (item) {
    PublicationDtoRide ride => ride.id,
    PublicationDtoPost post => post.id,
    PublicationDtoTrip trip => trip.id,
  };
}
