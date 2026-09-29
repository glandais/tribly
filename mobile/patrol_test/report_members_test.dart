import 'api/rides_home_trips_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-moderation.e2e.ts` — a report reaches the team's queue. Here the two
/// lists of people that open a member's `⋯` menu: the team's « Membres » section and a ride's
/// participants sheet. The report names the member, and the moderator finds it in the queue.
void main() {
  testApp(
    'A member reports a teammate from the members list and a participant from a ride’s participants sheet; both reach the queue',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final moderator = await backend.newUser('Members moderator');
      final author = await backend.newUser('Members author');
      final participant = await backend.newUser('Members participant');
      final reporter = await backend.newUser('Members reporter');
      final team = await backend.newTeam(moderator, 'Equipe membres signales');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, author, role: 'ORGANIZER');
      await backend.addMember(teamSlug, participant);
      await backend.addMember(teamSlug, reporter);
      final ride = await backend.newRide(
        author,
        teamSlug,
        'Sortie des membres',
      );
      final rideSlug = ride['slug'] as String;
      await backend.joinGroup(
        participant,
        teamSlug,
        rideSlug,
        groupIdNamed(ride, 'Groupe A'),
      );

      await openAppSignedIn($, reporter);

      // The team's « Membres » section.
      await openLink($, Paths.teamMembers(teamSlug));
      await modules.detailMenus.openMemberMenu(author.id);
      await modules.moderation.waitUntilMenuIsShown();
      expect(modules.moderation.menuOffersBlock, isTrue);
      await modules.moderation.report();
      expect(
        await backend.queueItem(moderator, teamSlug, author.id),
        isNotNull,
      );

      // A ride's participants sheet.
      await openLink($, Paths.ride(teamSlug, rideSlug));
      await modules.ride.waitUntilShown();
      await modules.detailMenus.openParticipantMenu(participant.id);
      await modules.moderation.waitUntilMenuIsShown();
      await modules.moderation.report();
      expect(
        await backend.queueItem(moderator, teamSlug, participant.id),
        isNotNull,
      );
    },
  );
}
