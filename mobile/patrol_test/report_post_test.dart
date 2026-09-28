import 'common.dart';

/// Web counterpart: `flow-moderation.e2e.ts` › « three reports hide a post from members… »
/// (audit P0 #6). One member reports from the app, two more through the API: the team's
/// moderator finds the three in the queue, and the post is hidden from the other members.
void main() {
  testApp(
    'A member reports a post from its page; three reports hide it from the team',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      // The moderator never sees reports about their own content: someone else writes the post.
      final moderator = await backend.newUser('Report moderator');
      final author = await backend.newUser('Report author');
      final reporter = await backend.newUser('Report reporter');
      final second = await backend.newUser('Report second');
      final third = await backend.newUser('Report third');
      final bystander = await backend.newUser('Report bystander');
      final team = await backend.newTeam(moderator, 'Equipe moderee');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, author, role: 'ORGANIZER');
      for (final member in [reporter, second, third, bystander]) {
        await backend.addMember(teamSlug, member);
      }
      final post = await backend.newPost(author, teamSlug, 'Article signale');
      final postSlug = post['slug'] as String;
      final postId = post['id'] as String;

      await openAppSignedIn($, reporter);
      await openLink($, Paths.post(teamSlug, postSlug));
      await modules.post.waitUntilShown();
      await modules.post.openMoreMenu();
      await modules.moderation.waitUntilMenuIsShown();
      // A post doesn't expose its author: the menu can report it, not block anyone.
      expect(modules.moderation.menuOffersBlock, isFalse);
      await modules.moderation.report();
      // A reported content leaves the reader's lists: its page closes behind the report.
      await modules.post.waitUntilClosed();

      final reported = await backend.queueItem(moderator, teamSlug, postId);
      expect(reported?['reportCount'], 1);
      expect(await backend.findPost(bystander, teamSlug, postSlug), isNotNull);

      await backend.report(second, teamSlug, 'POST', postId);
      await backend.report(third, teamSlug, 'POST', postId);
      final hidden = await backend.queueItem(moderator, teamSlug, postId);
      expect(hidden?['reportCount'], 3);
      expect(hidden?['hidden'], isTrue);
      expect(await backend.findPost(bystander, teamSlug, postSlug), isNull);
    },
  );
}
