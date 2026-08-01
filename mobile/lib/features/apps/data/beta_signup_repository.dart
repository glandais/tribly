import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final betaSignupRepositoryProvider = Provider<BetaSignupRepository>((ref) {
  return BetaSignupRepository(ref.watch(betaSignupsClientProvider));
});

class BetaSignupRepository {
  BetaSignupRepository(this._client);

  final BetaSignupsClient _client;

  Future<void> signUp(String email) {
    return _client.signUpForBeta(body: BetaSignupRequest(email: email));
  }
}
