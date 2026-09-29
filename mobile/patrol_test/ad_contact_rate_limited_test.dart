import 'api/ads_seed.dart';
import 'common.dart';

/// Web counterpart: `ad-contact.e2e.ts` › « AD_CONTACT_RATE_LIMITED (429) ».
///
/// The relay takes 10 messages per sender per hour (`pedalons.ads.contact.*`). The eleventh is
/// refused with a 429 whose `Retry-After` (3600 s) the sheet turns into words; the sheet stays
/// open with the draft, its button becomes « Réessayer », and the page keeps « Contacter le
/// vendeur » since a later attempt may go through.
void main() {
  testApp(
    'A spent quota: the sheet says when to retry, keeps the draft, and the page keeps its button',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final seller = await backend.newUser('Ad quota seller');
      final buyer = await backend.newUser('Ad quota buyer');
      final team = await backend.newTeam(seller, 'Equipe quota annonces');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, buyer);
      final ad = await backend.newAd(seller, teamSlug, 'Velo convoite');
      final adSlug = ad['slug'] as String;
      for (var i = 1; i <= 10; i++) {
        await backend.contactSeller(
          buyer,
          teamSlug,
          adSlug,
          'Message de quota numero $i.',
        );
      }
      const draft = 'Bonjour, puis-je passer voir le velo samedi matin ?';

      await openAppSignedIn($, buyer);
      await openLink($, Paths.ad(teamSlug, adSlug));
      await modules.ad.waitUntilShown();
      await modules.adContact.openSheet();
      await modules.adContact.typeMessage(draft);
      await modules.adContact.send();
      await modules.adContact.waitUntilErrorIsShown();

      // Retry-After: 3600 s, rounded to the hour.
      expect(
        modules.adContact.errorSays(
          'Vous avez envoyé trop de messages. Réessayez dans 1 heure.',
        ),
        isTrue,
      );
      expect(modules.adContact.draft, draft);
      expect(modules.adContact.sendButtonSays('Réessayer'), isTrue);
      expect(modules.adContact.sendIsEnabled, isTrue);

      // Under the sheet, the page still offers the button: a later attempt may go through.
      expect(modules.adContact.offersContact, isTrue);
      expect(modules.adContact.showsSent, isFalse);
      // Only the ten messages of the quota reached the seller.
      final relayed = await eventually(
        () =>
            apiClients.mailpit.relayedAbout(seller.email, ad['name'] as String),
        until: (mails) => mails.length >= 10,
        description: 'the ten relayed mails of the quota',
      );
      expect(relayed.length, 10);
    },
  );
}
