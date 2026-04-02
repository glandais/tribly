import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../config/paths.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../auth/services/passkey_service.dart';

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
                  trailing: authState.hasPasskeys
                      ? Icon(Icons.check_circle, color: theme.colorScheme.primary)
                      : FilledButton.tonal(
                          onPressed: () => _registerPasskey(context, ref),
                          child: Text('profile.passkeys.add'.tr()),
                        ),
                ),
                const Divider(height: 1),
                ListTile(
                  leading: Icon(
                    Icons.email,
                    color: theme.colorScheme.outline,
                  ),
                  title: Text('profile.magicLink.title'.tr()),
                  subtitle: Text('profile.magicLink.description'.tr()),
                  trailing: Icon(Icons.check_circle, color: theme.colorScheme.primary),
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
                    Icons.notifications_outlined,
                    color: theme.colorScheme.outline,
                  ),
                  title: Text('profile.notifications'.tr()),
                  trailing: Switch(
                    value: true,
                    onChanged: (value) {
                      // TODO: Implement notification settings
                    },
                  ),
                ),
                const Divider(height: 1),
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
                    '1.0.0',
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
          const SizedBox(height: 32),

          // Danger zone
          OutlinedButton.icon(
            onPressed: () {
              // TODO: Delete account flow
            },
            style: OutlinedButton.styleFrom(
              foregroundColor: theme.colorScheme.error,
            ),
            icon: const Icon(Icons.delete_forever),
            label: Text('profile.deleteAccount'.tr()),
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
      final passkeyService = ref.read(passkeyServiceProvider);
      await passkeyService.register(deviceName: 'Mobile');
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('auth.passkey.success'.tr())),
        );
        // Refresh auth state to update passkey status
        ref.invalidate(authProvider);
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
