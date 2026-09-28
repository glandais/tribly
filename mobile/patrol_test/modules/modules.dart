import 'package:patrol/patrol.dart';

import 'ad.dart';
import 'auth.dart';
import 'device.dart';
import 'moderation.dart';
import 'navigation.dart';
import 'notifications.dart';
import 'post.dart';
import 'profile.dart';
import 'ride.dart';
import 'teams.dart';

final class Modules {
  Modules(this._$);

  final PatrolIntegrationTester _$;

  late final ad = Ad(_$);
  late final auth = Auth(_$);
  late final device = Device(_$);
  late final moderation = Moderation(_$);
  late final navigation = Navigation(_$);
  late final notifications = Notifications(_$);
  late final post = Post(_$);
  late final profile = Profile(_$);
  late final ride = Ride(_$);
  late final teams = Teams(_$);
}
