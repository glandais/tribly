import 'common.dart';

/// Web counterpart: `flow-notifications.e2e.ts` › « a published post reaches a member in the
/// team's name, and opening it reads it » (audit P0 #5). The dispatcher fans events out every
/// 15 s: the test waits for the notification through the API, then opens the app, whose bell reads
/// `unread-count` as it starts.
void main() {
  testApp(
    'A published post reaches a member in the team’s name, and opening it reads it',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final author = await backend.newUser('Notif author');
      final member = await backend.newUser('Notif member');
      final team = await backend.newTeam(author, 'Equipe annonces notif');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      final post = await backend.newPost(author, teamSlug, 'Article annonce');

      final notification = await eventually(
        () => backend.notificationAbout(
          member,
          'POST_PUBLISHED',
          post['slug'] as String,
        ),
        until: (Json? n) => n != null,
        description: 'the POST_PUBLISHED notification',
      );
      final id = notification!['id'] as String;
      // A publication speaks for the team: the API never names its author.
      expect(notification['actorName'], isNull);

      await openAppSignedIn($, member);
      expect(await modules.notifications.waitForBellBadge(), '1');

      await modules.notifications.openInboxFromBell();
      await modules.notifications.waitUntilEntryIsShown(id);
      expect(
        modules.notifications.entryShows(id, post['name'] as String),
        isTrue,
      );
      expect(
        modules.notifications.entryShows(id, team['name'] as String),
        isTrue,
      );
      expect(modules.notifications.entryShows(id, author.displayName), isFalse);

      await modules.notifications.openEntry(id);
      await modules.post.waitUntilShown();
      expect(modules.post.title, post['name']);
      await eventually(
        () => backend.unreadCount(member),
        until: (int count) => count == 0,
        description: 'the inbox read',
        timeout: const Duration(seconds: 10),
      );
    },
  );
}
