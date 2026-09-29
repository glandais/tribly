import 'api/ride_detail_seed.dart';
import 'api/rides_home_trips_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-notifications.e2e.ts` › personal notifications › « a cancelled ride
/// reaches its registered riders only, and leads to the ride marked « Annulé » ».
///
/// The ride is cancelled the way the web editor does it (a PUT of the ride, status
/// `CANCELLED`); the dispatcher fans the event out every 15 s, so the test waits for it through
/// the API before opening the app.
void main() {
  testApp(
    'A cancelled ride reaches its registered riders only, and its entry opens the ride marked cancelled',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Cancel owner');
      final registered = await backend.newUser('Cancel registered');
      final bystander = await backend.newUser('Cancel bystander');
      final team = await backend.newTeam(owner, 'Equipe sorties annulees');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, registered);
      await backend.addMember(teamSlug, bystander);
      final ride = await backend.newRide(owner, teamSlug, 'Sortie annulee');
      final rideSlug = ride['slug'] as String;
      await backend.joinGroup(
        registered,
        teamSlug,
        rideSlug,
        groupIdNamed(ride, 'Groupe A'),
      );
      await backend.cancelRide(owner, teamSlug, rideSlug);

      final notification = await eventually(
        () => backend.notificationAbout(registered, 'RIDE_CANCELLED', rideSlug),
        until: (Json? n) => n != null,
        description: 'the RIDE_CANCELLED notification',
      );
      final id = notification!['id'] as String;
      expect(notification['read'], isFalse);
      expect(notification['subjectType'], 'RIDE');
      expect(notification['subjectName'], ride['name']);
      // Same event, same tick: the bystander's copy would be there by now.
      expect(
        await backend.notificationAbout(bystander, 'RIDE_CANCELLED', rideSlug),
        isNull,
      );

      await openAppSignedIn($, registered);
      await modules.notifications.openInboxFromBell();
      await modules.notifications.waitUntilEntryIsShown(id);
      expect(
        modules.notifications.entryShows(id, 'Une sortie est annulée'),
        isTrue,
      );
      expect(
        modules.notifications.entryShows(id, ride['name'] as String),
        isTrue,
      );

      await modules.notifications.openEntry(id);
      await modules.ride.waitUntilCancelled();
      expect(modules.ride.title, ride['name']);
    },
  );
}
