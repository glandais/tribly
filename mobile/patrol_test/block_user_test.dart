import 'common.dart';

/// Web counterpart: `flow-moderation.e2e.ts` › « blocking a comment author hides their comments;
/// unblocking… » (audit P0 #6). On mobile, unblocking lives in the profile's « Utilisateurs
/// bloqués », under « Confidentialité ».
void main() {
  testApp(
    'Blocking a comment’s author hides their comments; unblocking from the profile brings them back',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Block owner');
      final commenter = await backend.newUser('Block commenter');
      final reader = await backend.newUser('Block reader');
      final team = await backend.newTeam(owner, 'Equipe blocage');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, commenter);
      await backend.addMember(teamSlug, reader);
      final post = await backend.newPost(owner, teamSlug, 'Article commente');
      final postSlug = post['slug'] as String;
      final comment = await backend.commentOnPost(
        commenter,
        teamSlug,
        postSlug,
        'Un commentaire a masquer',
      );
      final commentId = comment['id'] as String;

      await openAppSignedIn($, reader);
      await openLink($, Paths.post(teamSlug, postSlug));
      await modules.post.waitUntilShown();
      await modules.post.waitUntilCommentIsShown(commentId);

      await modules.post.openCommentMenu(commentId);
      await modules.moderation.waitUntilMenuIsShown();
      expect(modules.moderation.menuOffersDelete, isFalse);
      await modules.moderation.blockAuthor();
      await modules.post.waitUntilCommentIsGone(commentId);
      expect(await backend.blockedIds(reader), contains(commenter.id));
      expect(
        await backend.postCommentIds(reader, teamSlug, postSlug),
        isNot(contains(commentId)),
      );

      // A post is a full-screen page, over the tab bar. A cold link to the
      // blocked users finds « Confidentialité » underneath: Profil ›
      // Confidentialité › Bloqués, as the site's breadcrumb.
      await openLink($, Paths.blockedUsers());
      await modules.moderation.waitUntilUnblockIsShown(commenter.id);
      await modules.moderation.backToPrivacy();

      await modules.moderation.unblockFromProfile(commenter.id);
      expect(await backend.blockedIds(reader), isNot(contains(commenter.id)));

      await openLink($, Paths.post(teamSlug, postSlug));
      await modules.post.waitUntilShown();
      await modules.post.waitUntilCommentIsShown(commentId);
    },
  );
}
