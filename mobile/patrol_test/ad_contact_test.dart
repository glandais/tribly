import 'api/ads_seed.dart';
import 'common.dart';

/// Web counterpart: `ad-contact.e2e.ts` › « success » and « too short, blank or too long messages
/// are refused client-side ».
///
/// The relay never discloses an address: the API carries none, and the server mails the seller
/// with `Reply-To` set to the buyer — whose address the sheet says will be visible to them. The
/// sheet refuses a message outside 10..2000 characters (trimmed) without calling the server; a
/// sent message closes it and leaves a confirmation on the page, in place of the button.
void main() {
  testApp(
    'Contacting a seller: out-of-bounds messages are refused, a sent one closes the sheet, leaves '
    'a confirmation and reaches the seller with no address in the mail',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final seller = await backend.newUser('Ad contact seller');
      final buyer = await backend.newUser('Ad contact buyer');
      final team = await backend.newTeam(seller, 'Equipe contact annonces');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, buyer);
      final ad = await backend.newAd(seller, teamSlug, 'Velo de route');
      final adSlug = ad['slug'] as String;
      final adName = ad['name'] as String;
      const draft =
          'Bonjour, le velo est-il toujours disponible ? Je peux passer samedi.';

      // The API carries no address of the seller.
      final read = await backend.get(
        buyer,
        '/api/teams/$teamSlug/classifieds/$adSlug',
      );
      expect(read.toString().contains(seller.email), isFalse);

      await openAppSignedIn($, buyer);
      await openLink($, Paths.ad(teamSlug, adSlug));
      await modules.ad.waitUntilShown();
      await modules.adContact.openSheet();

      await modules.adContact.typeMessage('x' * 9);
      final tooShortEnabled = modules.adContact.sendIsEnabled;
      await modules.adContact.typeMessage('x' * 2001);
      final tooLongEnabled = modules.adContact.sendIsEnabled;
      // Ten spaces: blank once trimmed.
      await modules.adContact.typeMessage(' ' * 10);
      final blankEnabled = modules.adContact.sendIsEnabled;
      // The bounds themselves are accepted: the checks above are not a button that is always off.
      await modules.adContact.typeMessage('x' * 2000);
      final longestEnabled = modules.adContact.sendIsEnabled;
      await modules.adContact.typeMessage('x' * 10);
      final shortestEnabled = modules.adContact.sendIsEnabled;

      await modules.adContact.typeMessage(draft);
      await modules.adContact.send();
      await modules.adContact.waitUntilSheetIsClosed();
      await modules.adContact.waitUntilSentIsShown();

      expect(tooShortEnabled, isFalse);
      expect(tooLongEnabled, isFalse);
      expect(blankEnabled, isFalse);
      expect(longestEnabled, isTrue);
      expect(shortestEnabled, isTrue);
      expect(modules.adContact.offersContact, isFalse);

      // The relayed mail: one, to the seller, Reply-To the buyer, and neither address printed.
      final relayed = await eventually(
        () => apiClients.mailpit.relayedAbout(seller.email, adName),
        until: (mails) => mails.isNotEmpty,
        description: 'the relayed mail',
      );
      expect(relayed.length, 1);
      final mail = relayed.single;
      final body = '${mail['Text']}\n${mail['HTML']}';
      expect(body.contains(draft), isTrue);
      expect(addressesIn(mail['ReplyTo']), contains(buyer.email));
      expect(addressesIn(mail['From']).contains(buyer.email), isFalse);
      expect(body.contains(buyer.email), isFalse);
      expect(body.contains(seller.email), isFalse);
    },
  );
}
