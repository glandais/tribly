import 'api/lists_seed.dart';
import 'common.dart';

/// Web counterparts: `pagination.e2e.ts` (team posts and home feed, one page and one entry) and
/// `list-filters.e2e.ts` (the filtered empty state). The app has no page numbers: scrolling reaches
/// the entry the API puts on page 2. Which one that is is asked of the API. A team's posts and its
/// rides live in two sections since `MOB-60`: « Publications » and « Agenda ».
void main() {
  testApp(
    'A team\'s posts and the home feed scroll into their second page; the agenda holds the ride and no post; the search filters down to the filtered empty state',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Feed owner');
      final member = await backend.newUser('Feed member');
      final team = await backend.newTeam(owner, 'Equipe fil pagine');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      final ride = await backend.newRide(owner, teamSlug, 'Sortie du fil');
      final rideSlug = ride['slug'] as String;
      await backend.newPosts(owner, teamSlug, 'Article du fil', 21);

      final postsPage2 = await backend.teamFeedSlugs(
        member,
        teamSlug,
        page: 1,
        type: 'POST',
      );
      expect(postsPage2, isNot(isEmpty));
      final postsPage1 = await backend.teamFeedSlugs(
        member,
        teamSlug,
        type: 'POST',
      );
      final firstPost = postsPage1.first;
      final homePage2 = await backend.homeFeedSlugs(member, page: 1);
      expect(homePage2, isNot(isEmpty));

      await openAppSignedIn($, member);

      // « Publications »: the posts alone, page 2 reached by scrolling.
      await openLink($, Paths.teamPosts(teamSlug));
      await modules.lists.waitUntilFeedHas(firstPost);
      expect(modules.lists.feedHas(rideSlug), isFalse);
      await modules.lists.scrollFeedTo(postsPage2.first);
      // A search that matches nothing: the filtered empty state, not the empty list.
      await modules.lists.searchFeed(unique('introuvable'));
      await modules.lists.waitUntilFeedFilteredEmpty();
      expect(modules.lists.feedHas(firstPost), isFalse);

      // « Agenda »: the upcoming ride, and no post.
      await openLink($, Paths.teamAgenda(teamSlug));
      await modules.lists.waitUntilFeedHas(rideSlug);
      expect(modules.lists.feedHas(firstPost), isFalse);

      // The home feed: its page 2 too.
      await openLink($, Paths.home());
      await modules.home.waitUntilShown();
      await modules.lists.scrollFeedTo(homePage2.first);
    },
  );
}
