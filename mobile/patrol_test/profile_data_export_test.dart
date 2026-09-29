import 'api/profile_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-account.e2e.ts` › « personal data export ».
///
/// « Demander un export » queues a GDPR export (`POST /api/users/me/export`, 202); the backend's
/// scheduler builds it — one pending export per 30 s tick — and mails a download link. The card
/// says where the export stands: « En préparation… » once asked, then « Prêt · valable jusqu'au
/// {date} » once the server calls it `READY`, which a member sees on coming back to the profile.
void main() {
  testApp(
    'A data export requested from the profile is mailed, and the profile then says it is ready',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final user = await backend.newUser('Export donnees');
      final seen = await apiClients.mailpit.mailbox(user.email);

      await openAppSignedIn($, user);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.profileSettings.waitUntilExportStatusSays('Jamais demandé');
      await modules.profileSettings.requestExport();
      await modules.profileSettings.waitUntilExportStatusSays('En préparation');

      final mail = await apiClients.mailpit.waitForNewMail(
        user.email,
        seen,
        timeout: const Duration(seconds: 200),
      );
      final export = await backend.latestExport(user);

      // Back on the profile after the mail: a fresh launch reads the export again.
      await openAppSignedIn($, user);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.profileSettings.waitUntilExportStatusSays('Prêt');

      expect(mail.contains('/api/export/download/'), isTrue);
      expect(export?['status'], 'READY');
    },
  );
}
