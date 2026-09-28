import 'module.dart';

/// A post's page, and its comment thread.
final class Post extends Module {
  Post(super.$);

  Future<void> waitUntilShown() async {
    await $(keys.post.title).waitUntilVisible();
  }

  String? get title =>
      $(keys.post.title).exists ? $(keys.post.title).text : null;

  bool get isShownNow => isShown(keys.post.title);

  Future<void> waitUntilClosed() => waitUntilGone(keys.post.title);

  Future<void> openMoreMenu() async {
    await $(keys.post.moreButton).tap();
  }

  Future<void> waitUntilCommentIsShown(String commentId) async {
    await scrolledTo(keys.comments.comment(commentId));
  }

  bool showsComment(String commentId) =>
      isShown(keys.comments.comment(commentId));

  Future<void> waitUntilCommentIsGone(String commentId) =>
      waitUntilGone(keys.comments.comment(commentId));

  Future<void> openCommentMenu(String commentId) async {
    await (await scrolledTo(keys.comments.moreButton(commentId))).tap();
  }
}
