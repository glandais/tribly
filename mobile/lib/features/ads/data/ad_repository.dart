import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final adRepositoryProvider = Provider<AdRepository>((ref) {
  return AdRepository(ref.watch(adsClientProvider));
});

class AdRepository {
  final AdsClient _adsClient;

  AdRepository(this._adsClient);

  Future<AdDto> getAd(String teamSlug, String adSlug) {
    return _adsClient.getAd(teamSlug: teamSlug, slug: adSlug);
  }
}
