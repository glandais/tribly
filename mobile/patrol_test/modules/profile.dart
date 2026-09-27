import 'package:patrol/patrol.dart';

import 'module.dart';

final class Profile extends Module {
  Profile(super.$);

  Future<void> logOut() async {
    await $(keys.profile.logoutButton).scrollTo().tap();
  }
}
