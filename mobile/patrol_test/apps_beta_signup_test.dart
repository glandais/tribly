import 'api/peripheral_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-platform-admin.e2e.ts` › « beta sign-ups ».
///
/// The Apps page (`apps_page.dart`), reached from the profile, lists the companion apps and takes an
/// address for the next beta. A malformed address stays on the form with its message; a good one is
/// recorded — the platform admin finds it in the list. The sign-up is idempotent on the address,
/// whatever its case, and answers the same either way.
void main() {
  testApp(
    'The Apps page refuses a malformed address, then records a beta sign-up once, whatever its case',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final user = await backend.newUser('Beta tester');
      final email =
          'mobile-beta-${DateTime.now().microsecondsSinceEpoch}@e2e.test';

      await openAppSignedIn($, user);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.peripheral.openAppsFromProfile();
      await modules.peripheral.signUpForBeta('pas-une-adresse');
      await modules.peripheral.waitUntilBetaEmailIsRefused();
      final signedUpWithBadAddress = modules.peripheral.showsBetaSignedUp;
      await modules.peripheral.signUpForBeta(email);
      await modules.peripheral.waitUntilBetaSignUpIsRecorded();

      // Again, in capitals, from a fresh page: the same answer, and no second row.
      await openAppSignedIn($, user);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.peripheral.openAppsFromProfile();
      await modules.peripheral.signUpForBeta(email.toUpperCase());
      await modules.peripheral.waitUntilBetaSignUpIsRecorded();

      expect(signedUpWithBadAddress, isFalse);
      final listed = await backend.latestBetaSignups();
      expect(
        listed.where((e) => e.toLowerCase() == email).toList(),
        equals([email]),
      );
    },
  );
}
