import 'common.dart';

/// Web counterpart: `flow-moderation.e2e.ts` › a moderator deletes someone else's comment. The
/// thread's `⋯` offers « Supprimer » on a teammate's comment to an organizer or an administrator of
/// the team (`viewer.moderates`, `comment_thread.dart`) — a plain member is refused it, which
/// `comment_reply_test` covers.
void main() {
  testApp('An organizer deletes a member’s comment on someone else’s post', (
    $,
    modules,
    apiClients,
  ) async {
    final backend = apiClients.backend;
    final owner = await backend.newUser('Moderating owner');
    final organizer = await backend.newUser('Moderating organizer');
    final commenter = await backend.newUser('Moderating commenter');
    final team = await backend.newTeam(owner, 'Equipe moderatrice');
    final teamSlug = team['slug'] as String;
    await backend.addMember(teamSlug, organizer, role: 'ORGANIZER');
    await backend.addMember(teamSlug, commenter);
    final post = await backend.newPost(owner, teamSlug, 'Article commente');
    final postSlug = post['slug'] as String;
    final comment = await backend.commentOnPost(
      commenter,
      teamSlug,
      postSlug,
      'Un commentaire a moderer',
    );
    final commentId = comment['id'] as String;

    await openAppSignedIn($, organizer);
    await openLink($, Paths.post(teamSlug, postSlug));
    await modules.post.waitUntilShown();
    await modules.post.openCommentMenu(commentId);
    await modules.moderation.waitUntilMenuIsShown();
    expect(modules.moderation.menuOffersDelete, isTrue);
    expect(modules.moderation.menuOffersReport, isTrue);
    await modules.post.deleteFromOpenMenu();
    await modules.post.waitUntilCommentIsGone(commentId);
    expect(
      await backend.postCommentIds(commenter, teamSlug, postSlug),
      isNot(contains(commentId)),
    );
  });
}
