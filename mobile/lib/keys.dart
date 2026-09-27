import 'core/adaptive/keys.dart';
import 'features/auth/keys.dart';
import 'features/profile/keys.dart';

/// Les clés des widgets que les tests de bout en bout (`patrol_test/`)
/// trouvent. Une clé n'existe ici que si un widget la porte.
final keys = Keys();

class Keys {
  final login = LoginPageKeys();
  final navigation = NavigationKeys();
  final profile = ProfilePageKeys();
}
