import 'common.dart';

/// Web counterpart: `flow-notifications.e2e.ts` › « muting a team in the profile silences its
/// announcements » (audit P0 #5). The switch lives in the profile, even with no configurable
/// channel: muting a team also keeps its announcements out of the inbox.
void main() {
  testApp(
    'Muting a team in the profile keeps its posts out of the inbox, not another team’s',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      // One team per owner: the backend refuses a second.
      final author = await backend.newUser('Mute author');
      final otherAuthor = await backend.newUser('Mute other author');
      final member = await backend.newUser('Mute member');
      final muted = await backend.newTeam(author, 'Equipe coupee');
      final heard = await backend.newTeam(otherAuthor, 'Equipe ecoutee');
      final mutedSlug = muted['slug'] as String;
      final heardSlug = heard['slug'] as String;
      await backend.addMember(mutedSlug, member);
      await backend.addMember(heardSlug, member);

      await openAppSignedIn($, member);
      await modules.navigation.goToProfile();
      await modules.notifications.toggleTeamInProfile(mutedSlug);
      await eventually(
        () => backend.isMuted(member, mutedSlug),
        until: (bool? isMuted) => isMuted == true,
        description: 'the team muted',
        timeout: const Duration(seconds: 10),
      );
      expect(await backend.isMuted(member, heardSlug), isFalse);

      final silenced = await backend.newPost(author, mutedSlug, 'Tu');
      final announced = await backend.newPost(otherAuthor, heardSlug, 'Dit');
      await eventually(
        () => backend.notificationAbout(
          member,
          'POST_PUBLISHED',
          announced['slug'] as String,
        ),
        until: (Json? n) => n != null,
        description: 'the unmuted team’s announcement',
      );
      // Both posts were queued together: once one is out, the other went through the same tick.
      expect(
        await backend.notificationAbout(
          member,
          'POST_PUBLISHED',
          silenced['slug'] as String,
        ),
        isNull,
      );
    },
  );
}
