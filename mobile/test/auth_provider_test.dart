import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/pedalons_api_client.dart';
import 'package:pedalons/features/auth/providers/auth_provider.dart';

void main() {
  test('invalidating apiClientProvider keeps the auth notifier alive', () {
    final container = ProviderContainer();
    addTearDown(container.dispose);

    // Keep the provider alive like the app does (PedalonsApp watches it).
    final sub = container.listen(authProvider, (_, _) {});
    addTearDown(sub.close);

    final notifier = container.read(authProvider.notifier);

    // Resetting user-scoped caches must not tear down auth state, otherwise
    // isInitialized flips back to false and the app is stuck on the spinner.
    container.invalidate(apiClientProvider);

    expect(container.read(authProvider.notifier), same(notifier));
  });
}
