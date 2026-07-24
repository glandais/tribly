import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:package_info_plus/package_info_plus.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../auth/data/auth_repository.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../auth/services/passkey_service.dart';

final _serverVersionProvider = FutureProvider<VersionDto>((ref) async {
  return ref.watch(serverVersionClientProvider).getVersion();
});

final _packageInfoProvider = FutureProvider<PackageInfo>(
  (ref) => PackageInfo.fromPlatform(),
);

class ProfilePage extends ConsumerWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    final theme = Theme.of(context);
    final user = authState.user;

    return Scaffold(
      appBar: AppBar(
        title: Text('profile.title'.tr()),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              final confirmed = await showDialog<bool>(
                context: context,
                builder: (context) => AlertDialog(
                  title: Text('dialog.logout.title'.tr()),
                  content: Text('dialog.logout.message'.tr()),
                  actions: [
                    TextButton(
                      onPressed: () => Navigator.of(context).pop(false),
                      child: Text('dialog.logout.cancel'.tr()),
                    ),
                    FilledButton(
                      onPressed: () => Navigator.of(context).pop(true),
                      child: Text('dialog.logout.confirm'.tr()),
                    ),
                  ],
                ),
              );
              if (confirmed == true && context.mounted) {
                await ref.read(authProvider.notifier).logout();
                if (context.mounted) {
                  context.go(Paths.login());
                }
              }
            },
          ),
        ],
      ),
      body: Center(
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 600),
          child: ListView(
            padding: const EdgeInsets.all(16),
            children: [
          // User avatar and info
          Center(
            child: Column(
              children: [
                AuthenticatedCircleAvatar(
                  imageUrl: user?.avatarUrl,
                  fallbackText: (user?.displayName).safeFirstUpper(),
                  radius: 50,
                  fontSize: 32,
                ),
                const SizedBox(height: 16),
                Text(
                  user?.displayName ?? 'common.user'.tr(),
                  style: theme.textTheme.headlineSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  user?.email ?? '',
                  style: theme.textTheme.bodyMedium?.copyWith(
                    color: theme.colorScheme.outline,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 32),

          // Security section
          Text(
            'profile.security'.tr(),
            style: theme.textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Card(
            child: Column(
              children: [
                ListTile(
                  leading: Icon(
                    Icons.fingerprint,
                    color: authState.hasPasskeys
                        ? theme.colorScheme.primary
                        : theme.colorScheme.outline,
                  ),
                  title: Text('profile.passkeys.title'.tr()),
                  subtitle: Text(
                    authState.hasPasskeys
                        ? 'profile.passkeys.enabled'.tr()
                        : 'profile.passkeys.notConfigured'.tr(),
                  ),
                  trailing: FilledButton.tonal(
                    onPressed: () => _registerPasskey(context, ref),
                    child: Text(
                      authState.hasPasskeys
                          ? 'profile.passkeys.replace'.tr()
                          : 'profile.passkeys.add'.tr(),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // Preferences section
          Text(
            'profile.preferences'.tr(),
            style: theme.textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Card(
            child: Column(
              children: [
                ListTile(
                  leading: Icon(
                    Icons.language,
                    color: theme.colorScheme.outline,
                  ),
                  title: Text('profile.language'.tr()),
                  subtitle: Text(
                    context.locale.languageCode == 'fr'
                        ? 'languages.fr'.tr()
                        : 'languages.en'.tr(),
                  ),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () => _showLanguagePicker(context),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // About section
          Text(
            'profile.about'.tr(),
            style: theme.textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Card(
            child: Column(
              children: [
                ListTile(
                  leading: Icon(
                    Icons.info_outline,
                    color: theme.colorScheme.outline,
                  ),
                  title: Text('profile.version'.tr()),
                  trailing: Text(
                    ref.watch(_packageInfoProvider).maybeWhen(
                          // The build number is the only part pubspec bumps on
                          // each release, so the version alone never changes.
                          data: (info) => info.buildNumber.isEmpty
                              ? info.version
                              : '${info.version} (${info.buildNumber})',
                          orElse: () => '',
                        ),
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: theme.colorScheme.outline,
                    ),
                  ),
                ),
                const Divider(height: 1),
                ListTile(
                  leading: Icon(
                    Icons.dns_outlined,
                    color: theme.colorScheme.outline,
                  ),
                  title: Text('profile.serverVersion'.tr()),
                  trailing: Text(
                    ref.watch(_serverVersionProvider).maybeWhen(
                          data: (version) => version.commit == null
                              ? version.apiVersion
                              : '${version.apiVersion} (${version.commit})',
                          // Nothing to show while loading, and a profile page is no
                          // place to surface a version-lookup failure.
                          orElse: () => '',
                        ),
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: theme.colorScheme.outline,
                    ),
                  ),
                ),
                const Divider(height: 1),
                ListTile(
                  leading: Icon(
                    Icons.policy_outlined,
                    color: theme.colorScheme.outline,
                  ),
                  title: Text('profile.privacy'.tr()),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () => context.push(Paths.privacy()),
                ),
                const Divider(height: 1),
                ListTile(
                  leading: Icon(
                    Icons.description_outlined,
                    color: theme.colorScheme.outline,
                  ),
                  title: Text('profile.terms'.tr()),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () => context.push(Paths.terms()),
                ),
              ],
            ),
          ),
        ],
      ),
        ),
      ),
    );
  }

  void _showLanguagePicker(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              title: Text('languages.fr'.tr()),
              trailing: context.locale.languageCode == 'fr'
                  ? const Icon(Icons.check)
                  : null,
              onTap: () {
                context.setLocale(const Locale('fr'));
                Navigator.pop(context);
              },
            ),
            ListTile(
              title: Text('languages.en'.tr()),
              trailing: context.locale.languageCode == 'en'
                  ? const Icon(Icons.check)
                  : null,
              onTap: () {
                context.setLocale(const Locale('en'));
                Navigator.pop(context);
              },
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _registerPasskey(BuildContext context, WidgetRef ref) async {
    try {
      final repository = ref.read(authRepositoryProvider);
      final accessToken = ref.read(accessTokenHolderProvider)!;

      // List existing passkeys before registering the new one
      final existingPasskeys = await repository.listPasskeys(accessToken);

      // Register new passkey
      final passkeyService = ref.read(passkeyServiceProvider);
      final newPasskey = await passkeyService.register(deviceName: 'Mobile');

      // Delete old passkeys (keep only the new one)
      for (final passkey in existingPasskeys) {
        final id = passkey.id;
        if (id != null && id != newPasskey.id) {
          await repository.deletePasskey(id, accessToken);
        }
      }

      ref.read(authProvider.notifier).setHasPasskeys(true);
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('auth.passkey.success'.tr())),
        );
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(getErrorMessage(e))),
        );
      }
    }
  }
}
