import 'core/adaptive/keys.dart';
import 'features/ads/keys.dart';
import 'features/auth/keys.dart';
import 'features/comments/keys.dart';
import 'features/device/keys.dart';
import 'features/home/keys.dart';
import 'features/moderation/keys.dart';
import 'features/notifications/keys.dart';
import 'features/posts/keys.dart';
import 'features/profile/keys.dart';
import 'features/rides/keys.dart';
import 'features/teams/keys.dart';
import 'features/trips/keys.dart';

/// Les clés des widgets que les tests de bout en bout (`patrol_test/`)
/// trouvent. Une clé n'existe ici que si un widget la porte.
final keys = Keys();

class Keys {
  final login = LoginPageKeys();
  final navigation = NavigationKeys();
  final profile = ProfilePageKeys();
  final teams = TeamsPageKeys();
  final teamsDiscover = TeamsDiscoverKeys();
  final team = TeamPageKeys();
  final ride = RideDetailKeys();
  final ad = AdDetailKeys();
  final post = PostDetailKeys();
  final comments = CommentKeys();
  final moderation = ModerationKeys();
  final notifications = NotificationsKeys();
  final device = DeviceVerifyKeys();
  final home = HomeKeys();
  final trip = TripDetailKeys();
}
