import 'package:flutter/widgets.dart';

class _CommentKey extends ValueKey<String> {
  const _CommentKey(String value) : super('comment_$value');
}

class CommentKeys {
  ValueKey<String> comment(String commentId) => _CommentKey(commentId);

  ValueKey<String> moreButton(String commentId) =>
      _CommentKey('more_$commentId');

  /// Un fil : le commentaire racine et les réponses rangées sous lui.
  ValueKey<String> thread(String commentId) => _CommentKey('thread_$commentId');

  /// « Répondre », sous un commentaire racine.
  ValueKey<String> replyButton(String commentId) =>
      _CommentKey('reply_$commentId');

  /// Le composeur, unique pour tout le fil.
  final composerField = const _CommentKey('composerField');
  final sendButton = const _CommentKey('sendButton');

  /// « En réponse à {nom} », au-dessus du composeur, et son « Annuler ».
  final replyingToBanner = const _CommentKey('replyingToBanner');
  final cancelReplyButton = const _CommentKey('cancelReplyButton');
}
