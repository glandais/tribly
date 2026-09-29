import 'api/ads_seed.dart';
import 'common.dart';

/// Web counterpart: `ad-contact.e2e.ts` › « AD_CONTACT_OPTED_OUT ».
///
/// A seller who turned off « contactable » in their profile can't be reached through the relay:
/// the server answers 400 `AD_CONTACT_OPTED_OUT`, the sheet closes, and a notice replaces the
/// button — trying again would change nothing. Nothing is mailed.
void main() {
  testApp(
    'A seller who opted out: the sheet closes, a notice replaces the button, nothing is mailed',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final seller = await backend.newUser('Ad optout seller');
      final buyer = await backend.newUser('Ad optout buyer');
      final team = await backend.newTeam(seller, 'Equipe optout annonces');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, buyer);
      final ad = await backend.newAd(seller, teamSlug, 'Velo injoignable');
      await backend.setContactable(seller, false);

      await openAppSignedIn($, buyer);
      await openLink($, Paths.ad(teamSlug, ad['slug'] as String));
      await modules.ad.waitUntilShown();
      await modules.adContact.openSheet();
      await modules.adContact.typeMessage(
        'Bonjour, le velo est-il toujours disponible ?',
      );
      await modules.adContact.send();
      await modules.adContact.waitUntilSheetIsClosed();
      await modules.adContact.waitUntilOptedOutIsShown();

      expect(modules.adContact.offersContact, isFalse);
      expect(modules.adContact.showsSent, isFalse);
      expect(
        await apiClients.mailpit.relayedAbout(
          seller.email,
          ad['name'] as String,
        ),
        isEmpty,
      );
    },
  );
}
