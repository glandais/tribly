import 'package:flutter/widgets.dart';

class _ProfilePageKey extends ValueKey<String> {
  const _ProfilePageKey(String value) : super('profilePage_$value');
}

class ProfilePageKeys {
  final logoutButton = const _ProfilePageKey('logoutButton');
}
