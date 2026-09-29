import 'api/lists_seed.dart';
import 'common.dart';

/// Web counterparts: `pagination.e2e.ts` › ads, `list-filters.e2e.ts` › ads (search and type) and
/// `member-directory.e2e.ts` (the search). A team of its own with 21 sale ads and one wanted ad,
/// and 22 members: each list has a second page, reached by scrolling. Which entry lands there is
/// asked of the API.
void main() {
  testApp(
    'A team’s ads and members scroll into their second page; the ad type chips and the searches filter them',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Lists owner');
      final member = await backend.newUser('Lists member');
      final team = await backend.newTeam(owner, 'Equipe listes paginees');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      final others = [
        for (var i = 0; i < 20; i++) await backend.newUser('Membre liste $i'),
      ];
      for (final other in others) {
        await backend.addMember(teamSlug, other);
      }
      await backend.newAds(owner, teamSlug, 'Velo a vendre', 21);
      final wanted = (await backend.newAds(
        owner,
        teamSlug,
        'Cherche roue',
        1,
        adType: 'WANTED',
      )).single;
      final wantedSlug = wanted['slug'] as String;

      final adsPage1 = await backend.adSlugs(member, teamSlug);
      final adsPage2 = await backend.adSlugs(member, teamSlug, page: 1);
      expect(adsPage2, isNot(isEmpty));
      final aSale = adsPage1.firstWhere((slug) => slug != wantedSlug);
      final membersPage2 = await backend.memberUserIds(
        member,
        teamSlug,
        page: 1,
      );
      expect(membersPage2, isNot(isEmpty));

      await openAppSignedIn($, member);

      // Ads: page 2 by scrolling.
      await openLink($, Paths.teamAds(teamSlug));
      await modules.lists.waitUntilAdsHave(aSale);
      await modules.lists.scrollAdsTo(adsPage2.first);
      // « Recherche »: the wanted ad alone.
      await modules.lists.selectAdType('WANTED');
      await modules.lists.waitUntilAdsHave(wantedSlug);
      await modules.lists.waitUntilAdsLack(aSale);
      // Back to every type, then a search that matches nothing: the filtered dead end.
      await modules.lists.selectAdType(null);
      await modules.lists.waitUntilAdsHave(aSale);
      await modules.lists.searchAds(unique('introuvable'));
      await modules.lists.waitUntilAdsFilteredEmpty();
      expect(modules.lists.adsHave(aSale), isFalse);

      // Members: page 2 by scrolling, then the search narrows the list to one name.
      await openLink($, Paths.teamMembers(teamSlug));
      await modules.lists.waitUntilMembersHave(owner.id);
      await modules.lists.scrollMembersTo(membersPage2.first);
      await modules.lists.searchMembers(others[7].displayName);
      // « Membre liste 7 » alone — not « Membre liste 17 », which a substring search leaves out.
      await modules.lists.waitUntilMembersAre([others[7].id]);
      expect(modules.lists.membersHave(owner.id), isFalse);
      expect(modules.lists.membersHave(others[17].id), isFalse);
    },
  );
}
