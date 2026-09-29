import 'package:flutter/widgets.dart';

import '../../api/generated/export.dart';

class _FeedbackKey extends ValueKey<String> {
  const _FeedbackKey(String value) : super('feedback_$value');
}

/// La feuille « Signaler un problème ».
class FeedbackKeys {
  /// Un segment « Bug | Suggestion », par sa valeur.
  Key kind(FeedbackKind kind) => _FeedbackKey('kind_${kind.name}');
  final messageField = const _FeedbackKey('messageField');
  final sendButton = const _FeedbackKey('sendButton');

  /// Le message de remerciement, une fois le signalement parti.
  final sentMessage = const _FeedbackKey('sentMessage');
}
