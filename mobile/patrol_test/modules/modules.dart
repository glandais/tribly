import 'package:patrol/patrol.dart';

import 'auth.dart';
import 'navigation.dart';
import 'profile.dart';

final class Modules {
  Modules(this._$);

  final PatrolIntegrationTester _$;

  late final auth = Auth(_$);
  late final navigation = Navigation(_$);
  late final profile = Profile(_$);
}
