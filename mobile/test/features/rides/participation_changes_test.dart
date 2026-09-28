import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/api/pedalons_api_client.dart';
import 'package:pedalons/core/pagination/pagination.dart';
import 'package:pedalons/features/calendar/providers/calendar_month_provider.dart';
import 'package:pedalons/features/home/providers/next_ride_provider.dart';
import 'package:pedalons/features/home/providers/upcoming_provider.dart';
import 'package:pedalons/features/profile/data/profile_repository.dart';
import 'package:pedalons/features/profile/providers/participations_provider.dart';
import 'package:pedalons/features/rides/data/ride_repository.dart';
import 'package:pedalons/features/rides/providers/participation_changes.dart';
import 'package:pedalons/features/rides/providers/ride_detail_provider.dart';
import 'package:pedalons/features/rides/providers/ride_registration_controller.dart';
import 'package:pedalons/features/trips/data/trip_repository.dart';
import 'package:pedalons/features/trips/providers/trip_detail_provider.dart';
import 'package:pedalons/features/trips/providers/trip_participation_controller.dart';

import '../trips/trip_fixtures.dart';
import 'ride_fixtures.dart';

/// Une inscription faite dans l'app doit atteindre **toutes** les vues qui
/// dérivent de mes participations — pas seulement le détail de la sortie.
///
/// C'est la régression que traquent `next_ride_card_test` et
/// `profile_participations_count_test` côté Patrol : la carte « Ma prochaine
/// sortie » et le compteur du profil gardaient leur première valeur.

/// Le « serveur » : l'état d'inscription, partagé par tous les faux clients.
class _Server {
  RideDto ride = fixtureRide();
  TripDto trip = fixtureTrip();

  bool get rideRegistered => ride.registeredGroupId != null;

  int participationCount({required bool upcoming}) =>
      upcoming ? (rideRegistered ? 1 : 0) + (trip.registered ? 1 : 0) : 0;
}

class _FakeRideRepository implements RideRepository {
  _FakeRideRepository(this._server);
  final _Server _server;

  /// Retient la réponse de `joinGroup` jusqu'à ce que le test la libère.
  Completer<void>? gate;

  /// L'erreur que `joinGroup` rend au lieu d'inscrire.
  Object? joinError;

  @override
  Future<RideDto> getRide(String teamSlug, String rideSlug) async =>
      _server.ride;

  @override
  Future<RideParticipationDto> joinGroup(
    String teamSlug,
    String rideSlug,
    String groupId,
  ) async {
    if (gate != null) await gate!.future;
    if (joinError != null) throw joinError!;
    _server.ride = _twoGroups(registeredIn: groupId);
    return const RideParticipationDto(id: 'p1', userId: 'u0');
  }

  @override
  Future<void> leaveGroup(
    String teamSlug,
    String rideSlug,
    String groupId,
  ) async {
    _server.ride = _twoGroups();
  }

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

class _FakeTripRepository implements TripRepository {
  _FakeTripRepository(this._server);
  final _Server _server;

  @override
  Future<TripDto> getTrip(String teamSlug, String tripSlug) async =>
      _server.trip;

  @override
  Future<TripParticipationDto> joinTrip(
    String teamSlug,
    String tripSlug,
  ) async {
    _server.trip = fixtureTrip(registered: true, participantCount: 4);
    return const TripParticipationDto(id: 'p1', userId: 'u0');
  }

  @override
  Future<void> leaveTrip(String teamSlug, String tripSlug) async {
    _server.trip = fixtureTrip();
  }

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

/// Le seul appel dont `nextRideProvider` a besoin : la prochaine
/// participation.
class _FakeUsersClient implements UsersClient {
  _FakeUsersClient(this._server);
  final _Server _server;

  int participationCalls = 0;

  @override
  Future<PublicationListResponse> listMyParticipations({
    int? page = 0,
    int? size = 20,
    String? from,
    Status? status,
    String? to,
    ListViewMode? view,
  }) async {
    participationCalls++;
    return PublicationListResponse(
      publications: <PublicationDto>[
        if (_server.rideRegistered) _asListRow(_server.ride),
      ],
      total: _server.rideRegistered ? 1 : 0,
      page: 0,
      size: size ?? 20,
    );
  }

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

class _FakeProfileRepository implements ProfileRepository {
  _FakeProfileRepository(this._server);
  final _Server _server;

  int listCalls = 0;

  @override
  Future<PageResult<PublicationDto>> fetchParticipations({
    required bool upcoming,
    required DateTime now,
    int page = 0,
    int size = 20,
  }) async {
    if (size > 1) listCalls++;
    return PageResult<PublicationDto>(
      items: const <PublicationDto>[],
      total: _server.participationCount(upcoming: upcoming),
    );
  }

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

RideDto _twoGroups({String? registeredIn}) => fixtureRide(
  registered: registeredIn != null,
  registeredGroupId: registeredIn,
  groups: <RideGroupDto>[
    fixtureGroup(id: 'g1', name: 'Groupe A', registered: registeredIn == 'g1'),
    fixtureGroup(
      id: 'g2',
      name: 'Groupe B',
      sortOrder: 1,
      registered: registeredIn == 'g2',
    ),
  ],
);

/// Une ligne de `listMyParticipations` : pas de `groups[]`, comme côté
/// serveur.
PublicationDtoRide _asListRow(RideDto r) => PublicationDtoRide(
  team: r.team,
  id: r.id,
  slug: r.slug,
  name: r.name,
  media: r.media,
  dateTime: r.dateTime,
  status: r.status,
  visibility: r.visibility,
  participantCount: r.participantCount,
  groupCount: r.groupCount,
  groups: const <RideGroupDto>[],
  topParticipants: const <PublicUserDto>[],
  deleted: false,
  registered: r.registered,
  registeredGroupId: r.registeredGroupId,
  full: r.full,
);

DioException _apiError(String code, {int status = 409}) => DioException(
  requestOptions: RequestOptions(path: '/'),
  type: DioExceptionType.badResponse,
  response: Response<Map<String, dynamic>>(
    requestOptions: RequestOptions(path: '/'),
    statusCode: status,
    data: <String, dynamic>{'code': code},
  ),
);

void main() {
  const RideKey rideKey = RideKey(teamSlug: 'n-peloton', rideSlug: 'np-665');
  const TripKey tripKey = TripKey(
    teamSlug: 'n-peloton',
    tripSlug: 'gtmc-bromance',
  );
  final CalendarMonthKey monthKey = const CalendarMonthKey(
    year: 2099,
    month: 7,
  );

  late _Server server;
  late _FakeUsersClient users;
  late _FakeProfileRepository profile;
  late _FakeRideRepository rides;
  late ProviderContainer container;
  late int upcomingBuilds;
  late int calendarBuilds;

  setUp(() {
    server = _Server()..ride = _twoGroups();
    users = _FakeUsersClient(server);
    profile = _FakeProfileRepository(server);
    rides = _FakeRideRepository(server);
    upcomingBuilds = 0;
    calendarBuilds = 0;
    container = ProviderContainer(
      overrides: [
        rideRepositoryProvider.overrideWithValue(rides),
        tripRepositoryProvider.overrideWithValue(_FakeTripRepository(server)),
        usersClientProvider.overrideWithValue(users),
        profileRepositoryProvider.overrideWithValue(profile),
        upcomingProvider.overrideWith((Ref ref) async {
          upcomingBuilds++;
          return const <PublicationDto>[];
        }),
        calendarMonthProvider.overrideWith((
          Ref ref,
          CalendarMonthKey key,
        ) async {
          calendarBuilds++;
          return CalendarMonth.empty;
        }),
      ],
    );
    addTearDown(container.dispose);
  });

  /// Monte les vues dérivées comme le feraient l'accueil, le profil et le
  /// calendrier, restés ouverts sous l'écran de la sortie.
  Future<void> mountDerivedViews() async {
    container.listen(nextRideProvider, (_, _) {}, fireImmediately: true);
    container.listen(upcomingProvider, (_, _) {}, fireImmediately: true);
    container.listen(
      participationCountProvider(true),
      (_, _) {},
      fireImmediately: true,
    );
    container.listen(
      participationsProvider(true),
      (_, _) {},
      fireImmediately: true,
    );
    container.listen(
      calendarMonthProvider(monthKey),
      (_, _) {},
      fireImmediately: true,
    );
    await container.read(nextRideProvider.future);
    await container.read(upcomingProvider.future);
    await container.read(participationCountProvider(true).future);
    await container.read(calendarMonthProvider(monthKey).future);
  }

  Future<RideRegistrationController> openRide() async {
    container.listen(
      rideRegistrationProvider(rideKey),
      (_, _) {},
      fireImmediately: true,
    );
    await container.read(rideDetailProvider(rideKey).future);
    return container.read(rideRegistrationProvider(rideKey).notifier);
  }

  Future<TripParticipationController> openTrip() async {
    container.listen(
      tripParticipationProvider(tripKey),
      (_, _) {},
      fireImmediately: true,
    );
    await container.read(tripDetailProvider(tripKey).future);
    return container.read(tripParticipationProvider(tripKey).notifier);
  }

  test('rejoindre depuis la sortie fait apparaître « Ma prochaine sortie » '
      'et incrémente le compteur du profil', () async {
    await mountDerivedViews();
    expect(container.read(nextRideProvider).value, isNull);
    expect(container.read(participationCountProvider(true)).value, 0);

    await (await openRide()).join('g2');

    final NextRide? next = await container.read(nextRideProvider.future);
    expect(next, isNotNull);
    expect(next!.ride.slug, 'np-665');
    expect(next.group?.name, 'Groupe B');
    expect(await container.read(participationCountProvider(true).future), 1);
  });

  test(
    'quitter depuis la carte fait disparaître « Ma prochaine sortie »',
    () async {
      server.ride = _twoGroups(registeredIn: 'g1');
      await mountDerivedViews();
      expect(container.read(nextRideProvider).value?.group?.id, 'g1');
      expect(container.read(participationCountProvider(true)).value, 1);

      // La carte de l'accueil passe par le même contrôleur que la page.
      await (await openRide()).leave('g1');

      expect(await container.read(nextRideProvider.future), isNull);
      expect(await container.read(participationCountProvider(true).future), 0);
    },
  );

  test('une inscription recharge le carrousel, la liste du profil et le '
      'calendrier', () async {
    await mountDerivedViews();
    final int upcomingBefore = upcomingBuilds;
    final int calendarBefore = calendarBuilds;
    final ParticipationsNotifier listBefore = container.read(
      participationsProvider(true).notifier,
    );

    await (await openRide()).join('g1');
    await container.read(upcomingProvider.future);
    await container.read(calendarMonthProvider(monthKey).future);

    expect(upcomingBuilds, upcomingBefore + 1);
    expect(calendarBuilds, calendarBefore + 1);
    expect(
      container.read(participationsProvider(true).notifier),
      isNot(same(listBefore)),
      reason: 'la liste « Mes sorties à venir » repart de sa première page',
    );
  });

  test('un échec ne recharge pas les vues dérivées', () async {
    server.ride = _twoGroups(registeredIn: 'g1');
    await mountDerivedViews();
    final int calls = users.participationCalls;
    final int upcomingBefore = upcomingBuilds;

    // Exclusivité : refusée sans appel, rien n'a changé côté serveur.
    await (await openRide()).join('g2');
    await container.read(nextRideProvider.future);

    expect(upcomingBuilds, upcomingBefore);
    expect(users.participationCalls, calls);
  });

  test(
    'rejoindre un voyage met aussi à jour le profil et l\'accueil',
    () async {
      await mountDerivedViews();
      final int upcomingBefore = upcomingBuilds;
      final int calendarBefore = calendarBuilds;

      await (await openTrip()).join();

      expect(await container.read(participationCountProvider(true).future), 1);
      await container.read(upcomingProvider.future);
      await container.read(calendarMonthProvider(monthKey).future);
      expect(upcomingBuilds, upcomingBefore + 1);
      expect(calendarBuilds, calendarBefore + 1);
    },
  );

  test('invalider « Ma prochaine sortie » relit la participation — le '
      'pull-to-refresh de l\'accueil n\'en fait pas plus', () async {
    await mountDerivedViews();
    expect(container.read(nextRideProvider).value, isNull);

    // Inscription faite ailleurs (le web), sans passer par l'app.
    server.ride = _twoGroups(registeredIn: 'g1');
    container.invalidate(nextRideProvider);

    expect((await container.read(nextRideProvider.future))?.group?.id, 'g1');
  });

  Map<String, bool> overrides() =>
      container.read(registrationOverridesProvider);

  test('une inscription faite dans l\'app corrige le badge des fils, sans '
      'recharger leur page', () async {
    final RideRegistrationController controller = await openRide();

    await controller.join('g1');
    expect(overrides(), <String, bool>{server.ride.id: true});

    await controller.leave('g1');
    expect(overrides(), <String, bool>{server.ride.id: false});
  });

  test('« déjà inscrit » : le serveur dément l\'app, les vues dérivées sont '
      'relues et ce qu\'on croyait savoir de la sortie est oublié', () async {
    await mountDerivedViews();
    final RideRegistrationController controller = await openRide();
    await controller.join('g1');
    await controller.leave('g1');
    expect(overrides(), isNotEmpty);
    final int upcomingBefore = upcomingBuilds;

    // Inscrit entre-temps ailleurs (le web) : l'app ne le sait pas.
    rides.joinError = _apiError('ALREADY_REGISTERED');
    await controller.join('g2');
    await container.read(upcomingProvider.future);

    expect(upcomingBuilds, upcomingBefore + 1);
    expect(overrides(), isEmpty);
  });

  test('fermer la sortie pendant l\'inscription ne perd pas la mise à jour '
      'des autres écrans', () async {
    await mountDerivedViews();
    expect(container.read(nextRideProvider).value, isNull);

    // L'écran de la sortie : le seul à tenir le contrôleur.
    final ProviderSubscription<RideRegistrationState> screen = container.listen(
      rideRegistrationProvider(rideKey),
      (_, _) {},
      fireImmediately: true,
    );
    await container.read(rideDetailProvider(rideKey).future);
    rides.gate = Completer<void>();
    final Future<void> joining = container
        .read(rideRegistrationProvider(rideKey).notifier)
        .join('g2');

    // L'écran se ferme avant la réponse du serveur.
    screen.close();
    await Future<void>.delayed(Duration.zero);
    rides.gate!.complete();
    await joining;

    expect((await container.read(nextRideProvider.future))?.group?.id, 'g2');
    expect(overrides()[server.ride.id], isTrue);
  });
}
