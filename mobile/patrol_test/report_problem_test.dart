import 'package:pedalons/api/generated/export.dart';

import 'common.dart';

/// No web counterpart: « Signaler un problème » is the app's (`feedback_sheet.dart`).
///
/// From the profile, the sheet takes a bug or a suggestion of 10 to 5000 characters (trimmed) and
/// sends it with the technical context (`POST /api/feedback`). It closes only on the server's 204,
/// and the thanks are said once, on the screen it was opened from.
void main() {
  testApp(
    '« Signaler un problème » refuses a message too short, then sends a suggestion and says thanks',
    ($, modules, apiClients) async {
      final user = await apiClients.backend.newUser('Feedback sender');

      await openAppSignedIn($, user);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.peripheral.openReportProblemFromProfile();
      await modules.peripheral.chooseFeedbackKind(FeedbackKind.suggestion);
      await modules.peripheral.typeFeedback('   court   ');
      final blankishEnabled = modules.peripheral.feedbackSendIsEnabled;
      await modules.peripheral.typeFeedback(
        'Suggestion e2e : pouvoir filtrer les sorties par distance.',
      );
      final validEnabled = modules.peripheral.feedbackSendIsEnabled;
      await modules.peripheral.sendFeedback();
      await modules.peripheral.waitUntilFeedbackIsSent();

      expect(blankishEnabled, isFalse);
      expect(validEnabled, isTrue);
    },
  );
}
