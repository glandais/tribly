import 'api/rides_home_trips_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-notifications.e2e.ts` › personal notifications › « a rider joining a
/// ride is announced to its creator and to the group's leader, with the group's name ».
///
/// The rider joins from the app. The entry is then read by the group's **leader**, who did not
/// create the ride: the leader is `RideGroupDto.leader`, never the ride's creator — and both
/// are told, the rider never is.
void main() {
  testApp(
    'A rider joining a ride from the app is announced to its creator and to the group’s leader, with the group’s name',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Joined owner');
      final leader = await backend.newUser('Joined leader');
      final rider = await backend.newUser('Joined rider');
      final team = await backend.newTeam(owner, 'Equipe inscriptions');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, leader);
      await backend.addMember(teamSlug, rider);
      final groupName = unique('Groupe rapide');
      final ride = await backend.newRide(
        owner,
        teamSlug,
        'Sortie rejointe',
        groups: [
          {'name': groupName, 'leaderId': leader.id},
        ],
      );
      final rideSlug = ride['slug'] as String;
      final groupId = groupIdNamed(ride, groupName);

      await openAppSignedIn($, rider);
      await openLink($, Paths.ride(teamSlug, rideSlug));
      await modules.ride.waitUntilShown();
      await modules.ride.join(groupId);

      final notifications = <Json>[];
      for (final organiser in [owner, leader]) {
        final found = await eventually(
          () => backend.notificationAbout(organiser, 'RIDE_JOINED', rideSlug),
          until: (Json? n) => n != null,
          description: 'the RIDE_JOINED notification',
        );
        // A personal notification names who did it; the group travels as the excerpt.
        expect(found!['actorName'], rider.displayName);
        expect(found['subjectType'], 'RIDE');
        expect(found['subjectName'], ride['name']);
        expect(found['excerpt'], groupName);
        notifications.add(found);
      }
      expect(
        await backend.notificationAbout(rider, 'RIDE_JOINED', rideSlug),
        isNull,
      );

      final id = notifications.last['id'] as String;
      await openAppSignedIn($, leader);
      await modules.notifications.openInboxFromBell();
      await modules.notifications.waitUntilEntryIsShown(id);
      expect(modules.notifications.entryShows(id, rider.displayName), isTrue);
      expect(modules.notifications.entryShows(id, groupName), isTrue);
      expect(
        modules.notifications.entryShows(id, ride['name'] as String),
        isTrue,
      );

      await modules.notifications.openEntry(id);
      await modules.ride.waitUntilShown();
      expect(modules.ride.title, ride['name']);
      expect(
        await modules.ride.showsLeader(groupId, leader.displayName),
        isTrue,
      );
    },
  );
}
