import 'package:flutter/widgets.dart';

class _CommentKey extends ValueKey<String> {
  const _CommentKey(String value) : super('comment_$value');
}

class CommentKeys {
  ValueKey<String> comment(String commentId) => _CommentKey(commentId);

  ValueKey<String> moreButton(String commentId) =>
      _CommentKey('more_$commentId');
}
