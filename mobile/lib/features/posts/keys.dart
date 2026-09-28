import 'package:flutter/widgets.dart';

class _PostDetailKey extends ValueKey<String> {
  const _PostDetailKey(String value) : super('postDetail_$value');
}

class PostDetailKeys {
  final loadError = const _PostDetailKey('loadError');
  final title = const _PostDetailKey('title');
  final moreButton = const _PostDetailKey('moreButton');
}
