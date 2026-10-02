import 'api/rides_home_trips_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-trips.e2e.ts` › « a member comments on a stage: its own thread, the trip
/// author is told and sent to the trip » (docs/LEDGER_*.md API-11).
///
/// A stage carries its own thread on screen 25, apart from the trip's; a stage is no publication,
/// so the trip's author is the one told. The composer and the comments are the shared
/// `keys.comments` ones — the post module's helpers drive them on any screen.
void main() {
  testApp(
    'A member comments on a stage: the stage’s own thread, and the trip’s author hears of it',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Stage thread owner');
      final member = await backend.newUser('Stage thread member');
      final team = await backend.newTeam(owner, 'Equipe fil etape');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);

      final today = DateTime.now();
      DateTime at(int days) =>
          DateTime(today.year, today.month, today.day + days, 8);
      final stageName = unique('Etape commentee');
      final trip = await backend.newTrip(
        owner,
        teamSlug,
        'Voyage commente',
        dateTime: at(3),
        stages: [
          (name: stageName, at: at(3)),
          (name: unique('Etape muette'), at: at(4)),
        ],
      );
      final tripSlug = trip['slug'] as String;
      final stages = (trip['stages'] as List).cast<Json>();
      final stageSlug =
          stages.firstWhere((stage) => stage['name'] == stageName)['slug']
              as String;
      final quietSlug =
          stages.firstWhere((stage) => stage['name'] != stageName)['slug']
              as String;

      await openAppSignedIn($, member);
      await openLink($, Paths.stage(teamSlug, tripSlug, stageSlug));
      await modules.trip.waitUntilStageIs(stageName);

      final text = unique('On dort au gite');
      await modules.post.sendComment(text);
      final sent = (await backend.stageComments(
        member,
        teamSlug,
        stageSlug,
      )).where((Json c) => c['content'] == text).firstOrNull;
      expect(sent, isNotNull);
      await modules.post.waitUntilCommentIsShown(sent!['id'] as String);

      // The stage's thread only: neither the trip's nor the other stage's.
      expect(await backend.tripComments(member, teamSlug, tripSlug), isEmpty);
      expect(await backend.stageComments(member, teamSlug, quietSlug), isEmpty);

      // The trip's author is told, and the notification opens the trip.
      final notification = await eventually(
        () => backend.notificationAbout(
          owner,
          'COMMENT_ON_MY_PUBLICATION',
          tripSlug,
        ),
        until: (Json? n) => n != null,
        description:
            'the COMMENT_ON_MY_PUBLICATION notification of the trip author',
      );
      expect(notification!['subjectType'], 'TRIP');
    },
  );
}
