import 'api/lists_seed.dart';
import 'common.dart';

/// Web counterpart: `rides.e2e.ts` › comments — a thread longer than a page. The app asks for the
/// comments by 20 (`commentThreadProvider`): the 21st, which the API puts on page 2, must be
/// reachable from the post's page.
void main() {
  testApp(
    'A post with 21 comments: scrolling the thread reaches the one on its second page',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Thread owner');
      final member = await backend.newUser('Thread member');
      final team = await backend.newTeam(owner, 'Equipe fil long');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      final post = await backend.newPost(
        owner,
        teamSlug,
        'Article tres commente',
      );
      final postSlug = post['slug'] as String;
      for (var i = 0; i < 21; i++) {
        await backend.commentOnPost(
          owner,
          teamSlug,
          postSlug,
          'Commentaire $i',
        );
      }
      final page1 = await backend.postCommentIdsPage(
        member,
        teamSlug,
        postSlug,
      );
      final page2 = await backend.postCommentIdsPage(
        member,
        teamSlug,
        postSlug,
        page: 1,
      );
      expect(page1.length, 20);
      expect(page2.length, 1);

      await openAppSignedIn($, member);
      await openLink($, Paths.post(teamSlug, postSlug));
      await modules.post.waitUntilShown();
      await modules.lists.waitUntilCommentIsLoaded(page1.first);
      await modules.lists.scrollCommentsTo(page2.single);
    },
  );
}
