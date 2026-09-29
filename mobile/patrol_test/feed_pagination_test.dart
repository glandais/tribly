import 'package:pedalons/api/generated/export.dart';

import 'api/lists_seed.dart';
import 'common.dart';

/// Web counterparts: `pagination.e2e.ts` (team and home feeds, one page and one entry) and
/// `list-filters.e2e.ts` (type chips, the filtered empty state). The app has no page numbers:
/// scrolling reaches the entry the API puts on page 2. Which one that is is asked of the API.
void main() {
  testApp(
    'The team feed and the home feed scroll into their second page; the type chips and the search filter the feed, down to its filtered empty state',
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

      final teamPage2 = await backend.teamFeedSlugs(member, teamSlug, page: 1);
      expect(teamPage2, isNot(isEmpty));
      final teamPage1 = await backend.teamFeedSlugs(member, teamSlug);
      final firstPost = teamPage1.firstWhere((slug) => slug != rideSlug);
      final homePage2 = await backend.homeFeedSlugs(member, page: 1);
      expect(homePage2, isNot(isEmpty));

      await openAppSignedIn($, member);

      // The team feed: the entry of page 2 is reached by scrolling.
      await openLink($, Paths.team(teamSlug));
      await modules.lists.waitUntilFeedHas(firstPost);
      await modules.lists.scrollFeedTo(teamPage2.first);

      // « Sorties »: the ride, and no post.
      await modules.lists.selectFeedType(PublicationType.ride);
      await modules.lists.waitUntilFeedHas(rideSlug);
      await modules.lists.waitUntilFeedLacks(firstPost);
      // « Articles »: the posts, and not the ride.
      await modules.lists.selectFeedType(PublicationType.post);
      await modules.lists.waitUntilFeedHas(firstPost);
      expect(modules.lists.feedHas(rideSlug), isFalse);
      // A search that matches nothing: the filtered empty state, not the empty feed.
      await modules.lists.searchFeed(unique('introuvable'));
      await modules.lists.waitUntilFeedFilteredEmpty();
      expect(modules.lists.feedHas(firstPost), isFalse);

      // The home feed: its page 2 too.
      await openLink($, Paths.home());
      await modules.home.waitUntilShown();
      await modules.lists.scrollFeedTo(homePage2.first);
    },
  );
}
