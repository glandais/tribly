import 'package:flutter/widgets.dart';

class _AdDetailKey extends ValueKey<String> {
  const _AdDetailKey(String value) : super('adDetail_$value');
}

class AdDetailKeys {
  final title = const _AdDetailKey('title');
  final contactButton = const _AdDetailKey('contactButton');
  final moreButton = const _AdDetailKey('moreButton');
}
