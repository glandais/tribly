import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../config/paths.dart';
import '../../providers/auth_provider.dart';

class MagicLinkVerifyPage extends ConsumerStatefulWidget {
  final String? token;

  const MagicLinkVerifyPage({super.key, this.token});

  @override
  ConsumerState<MagicLinkVerifyPage> createState() =>
      _MagicLinkVerifyPageState();
}

class _MagicLinkVerifyPageState extends ConsumerState<MagicLinkVerifyPage> {
  bool _isLoading = true;
  bool _isSuccess = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _verifyMagicLink();
  }

  Future<void> _verifyMagicLink() async {
    if (widget.token == null || widget.token!.isEmpty) {
      setState(() {
        _isLoading = false;
        _errorMessage = 'Token manquant';
      });
      return;
    }

    try {
      final authNotifier = ref.read(authProvider.notifier);
      await authNotifier.verifyMagicLink(widget.token!);

      if (mounted) {
        setState(() {
          _isLoading = false;
          _isSuccess = true;
        });

        // Redirect to home after a short delay
        await Future.delayed(const Duration(seconds: 2));
        if (mounted) {
          context.go(Paths.home());
        }
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isLoading = false;
          _errorMessage = 'Erreur lors de la vérification: $e';
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Connexion'),
        centerTitle: true,
      ),
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                if (_isLoading) ...[
                  const CircularProgressIndicator(),
                  const SizedBox(height: 24),
                  const Text('Vérification en cours...'),
                ] else if (_errorMessage != null) ...[
                  Icon(
                    Icons.error_outline,
                    size: 80,
                    color: theme.colorScheme.error,
                  ),
                  const SizedBox(height: 24),
                  Text(
                    'Erreur',
                    style: theme.textTheme.headlineSmall,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    _errorMessage!,
                    textAlign: TextAlign.center,
                    style: TextStyle(color: theme.colorScheme.error),
                  ),
                  const SizedBox(height: 24),
                  FilledButton(
                    onPressed: () => context.go(Paths.login()),
                    child: const Text('Retour à la connexion'),
                  ),
                ] else if (_isSuccess) ...[
                  const Icon(
                    Icons.check_circle,
                    size: 80,
                    color: Colors.green,
                  ),
                  const SizedBox(height: 24),
                  Text(
                    'Connexion réussie!',
                    style: theme.textTheme.headlineSmall,
                  ),
                  const SizedBox(height: 16),
                  const Text(
                    'Redirection en cours...',
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 24),
                  const CircularProgressIndicator(),
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }
}
