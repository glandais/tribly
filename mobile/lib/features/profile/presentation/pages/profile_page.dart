import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../auth/providers/auth_provider.dart';
import '../../../auth/services/passkey_service.dart';
import '../../../../config/paths.dart';

class ProfilePage extends ConsumerWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    final theme = Theme.of(context);
    final user = authState.user;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Profil'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              final confirmed = await showDialog<bool>(
                context: context,
                builder: (context) => AlertDialog(
                  title: const Text('Déconnexion'),
                  content: const Text('Voulez-vous vraiment vous déconnecter?'),
                  actions: [
                    TextButton(
                      onPressed: () => Navigator.of(context).pop(false),
                      child: const Text('Annuler'),
                    ),
                    FilledButton(
                      onPressed: () => Navigator.of(context).pop(true),
                      child: const Text('Déconnexion'),
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
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // User avatar and info
          Center(
            child: Column(
              children: [
                CircleAvatar(
                  radius: 50,
                  backgroundImage: user?.avatarUrl != null
                      ? NetworkImage(user!.avatarUrl!)
                      : null,
                  child: user?.avatarUrl == null
                      ? Text(
                          user?.displayName.substring(0, 1).toUpperCase() ?? '?',
                          style: const TextStyle(fontSize: 32),
                        )
                      : null,
                ),
                const SizedBox(height: 16),
                Text(
                  user?.displayName ?? 'Utilisateur',
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
            'Sécurité',
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
                        ? Colors.green
                        : theme.colorScheme.outline,
                  ),
                  title: const Text('Passkeys'),
                  subtitle: Text(
                    authState.hasPasskeys
                        ? 'Connexion biométrique activée'
                        : 'Non configuré',
                  ),
                  trailing: authState.hasPasskeys
                      ? const Icon(Icons.check_circle, color: Colors.green)
                      : FilledButton.tonal(
                          onPressed: () => _registerPasskey(context, ref),
                          child: const Text('Ajouter'),
                        ),
                ),
                const Divider(height: 1),
                ListTile(
                  leading: Icon(
                    Icons.email,
                    color: theme.colorScheme.outline,
                  ),
                  title: const Text('Magic Link'),
                  subtitle: const Text('Connexion par email'),
                  trailing: const Icon(Icons.check_circle, color: Colors.green),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // Preferences section
          Text(
            'Préférences',
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
                  title: const Text('Notifications'),
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
                  title: const Text('Langue'),
                  subtitle: const Text('Français'),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () {
                    // TODO: Language picker
                  },
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // About section
          Text(
            'À propos',
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
                  title: const Text('Version'),
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
                  title: const Text('Politique de confidentialité'),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () {
                    // TODO: Open privacy policy
                  },
                ),
                const Divider(height: 1),
                ListTile(
                  leading: Icon(
                    Icons.description_outlined,
                    color: theme.colorScheme.outline,
                  ),
                  title: const Text('Conditions d\'utilisation'),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () {
                    // TODO: Open terms
                  },
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
            label: const Text('Supprimer mon compte'),
          ),
        ],
      ),
    );
  }

  Future<void> _registerPasskey(BuildContext context, WidgetRef ref) async {
    try {
      final passkeyService = ref.read(passkeyServiceProvider);
      await passkeyService.register(deviceName: 'Mobile');
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Passkey ajouté avec succès!')),
        );
        // Refresh auth state to update passkey status
        ref.invalidate(authProvider);
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur: $e')),
        );
      }
    }
  }
}
