import 'common.dart';

/// Web counterpart: `flow-ads.e2e.ts` › « ad rights » (audit P0 #4), a `test.fail` on the web —
/// `AdDetailPage.tsx` offers « Modifier » to any member there. The app never offers to edit or
/// delete someone else's ad: a member may contact its author, report it, or block its author.
void main() {
  testApp(
    'A member who is not the author may contact, report or block — never delete the ad',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final author = await backend.newUser('Ad author');
      final member = await backend.newUser('Ad reader');
      final team = await backend.newTeam(author, 'Equipe annonces');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      final ad = await backend.newAd(author, teamSlug, 'Velo a vendre');
      final adSlug = ad['slug'] as String;

      await openAppSignedIn($, member);
      await openLink($, Paths.ad(teamSlug, adSlug));
      await modules.ad.waitUntilShown();
      expect(await modules.ad.offersContact(), isTrue);

      await modules.ad.openMoreMenu();
      await modules.moderation.waitUntilMenuIsShown();
      expect(modules.moderation.menuOffersReport, isTrue);
      expect(modules.moderation.menuOffersBlock, isTrue);
      expect(modules.moderation.menuOffersDelete, isFalse);

      // What the app doesn't offer, the API refuses.
      expect(await backend.deleteAdStatus(member, teamSlug, adSlug), 403);
    },
  );
}
