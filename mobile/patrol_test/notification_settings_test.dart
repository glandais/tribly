import 'api/peripheral_seed.dart';
import 'common.dart';

/// Web counterpart: the profile's notifications table (`/profile/notifications`), one column per
/// channel the server declares.
///
/// « Notifications » of the profile shows one row per type with a chip per channel — **only the
/// channels `GET /api/notifications/preferences` declares**: no E-mail chip and no « Résumé
/// quotidien par e-mail » on a server whose e-mail delivery is off, no Push chip without FCM. A chip
/// writes its own cell (`PUT`), and the server's answer is what the page shows. The inbox and the
/// settings lead to one another (« Ouvrir mes notifications », the inbox's settings button).
///
/// The e2e stack (`.env.e2e`) runs with e-mail notifications off and no FCM: it declares no channel,
/// so there the test pins the absence of every chip and of the digest, and the toggle step runs only
/// on a stack that declares one (`PEDALONS_NOTIFICATIONS_EMAIL_ENABLED=true`).
void main() {
  testApp(
    'The notification chips follow the channels the server declares, a chip writes its cell, and '
    'the inbox and the settings lead to one another',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final member = await backend.newUser('Notification chips');
      // A team of the member's: its switch shows once the settings have loaded, whatever channels
      // the server declares — what the absence checks wait for.
      final team = await backend.newTeam(member, 'Equipe puces');
      final teamSlug = team['slug'] as String;
      final channels = await backend.notificationChannels(member);
      const type = 'RIDE_PUBLISHED';

      await openAppSignedIn($, member);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.profile.openNotificationSettings();
      await modules.notifications.waitUntilTeamSwitchIsShown(teamSlug);
      final digestShown = modules.notifications.showsDigest;
      await modules.notifications.scrollSettingsToTop();

      final shownChips = {
        for (final channel in const ['EMAIL', 'PUSH'])
          channel: modules.notifications.showsChannelChip(type, channel),
      };

      bool? before;
      bool? after;
      if (channels.isNotEmpty) {
        final channel = channels.first;
        before = await backend.notificationCellEnabled(member, type, channel);
        await modules.notifications.toggleChannelChip(type, channel);
        after = await eventually(
          () => backend.notificationCellEnabled(member, type, channel),
          until: (bool? enabled) => enabled != before,
          description: 'the $type × $channel cell flipped on the server',
          timeout: const Duration(seconds: 10),
        );
      }

      await modules.notifications.openInboxFromSettings();
      await modules.notifications.openSettingsFromInbox();

      expect(shownChips, {
        'EMAIL': channels.contains('EMAIL'),
        'PUSH': channels.contains('PUSH'),
      });
      expect(digestShown, channels.contains('EMAIL'));
      if (channels.isNotEmpty) expect(after, isNot(before));
    },
  );
}
