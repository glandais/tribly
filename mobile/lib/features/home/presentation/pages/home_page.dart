import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../auth/services/passkey_service.dart';
import '../../../feed/presentation/widgets/publication_feed_view.dart';

class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    final theme = Theme.of(context);

    final userName =
        authState.user?.displayName.split(' ').firstOrNull ??
        'home.defaultGreeting'.tr();

    return Scaffold(
      body: PublicationFeedView(
        teamSlug: null,
        emptyMessage: 'home.feed.empty'.tr(),
        leadingSlivers: [
          // App bar with greeting
          SliverAppBar(
            expandedHeight: 120,
            pinned: true,
            flexibleSpace: FlexibleSpaceBar(
              title: Text(
                'home.greeting'.tr(namedArgs: {'name': userName}),
                style: const TextStyle(fontSize: 18),
              ),
              background: Container(
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: [
                      theme.colorScheme.primary,
                      theme.colorScheme.primaryContainer,
                    ],
                  ),
                ),
              ),
            ),
          ),

          // Passkey prompt if not configured
          if (!authState.hasPasskeys)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Card(
                  color: theme.colorScheme.primaryContainer,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Row(
                      children: [
                        Icon(
                          Icons.fingerprint,
                          color: theme.colorScheme.onPrimaryContainer,
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'auth.passkey.simplifiedLogin'.tr(),
                                style: theme.textTheme.titleSmall?.copyWith(
                                  color: theme.colorScheme.onPrimaryContainer,
                                ),
                              ),
                              Text(
                                'auth.passkey.enablePrompt'.tr(),
                                style: theme.textTheme.bodySmall?.copyWith(
                                  color: theme.colorScheme.onPrimaryContainer,
                                ),
                              ),
                            ],
                          ),
                        ),
                        TextButton(
                          onPressed: () => _registerPasskey(context, ref),
                          child: Text('auth.passkey.enable'.tr()),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Future<void> _registerPasskey(BuildContext context, WidgetRef ref) async {
    try {
      final passkeyService = ref.read(passkeyServiceProvider);
      await passkeyService.register(deviceName: 'Mobile');
      ref.read(authProvider.notifier).setHasPasskeys(true);
      if (context.mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('auth.passkey.success'.tr())));
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text(getErrorMessage(e))));
      }
    }
  }
}
