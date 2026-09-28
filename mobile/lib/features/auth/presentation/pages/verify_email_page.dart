import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../config/paths.dart';
import '../../../../config/router.dart';
import '../../../../keys.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../providers/auth_provider.dart';
import '../../services/passkey_service.dart';

class VerifyEmailPage extends ConsumerStatefulWidget {
  final String? token;

  const VerifyEmailPage({super.key, this.token});

  @override
  ConsumerState<VerifyEmailPage> createState() => _VerifyEmailPageState();
}

class _VerifyEmailPageState extends ConsumerState<VerifyEmailPage> {
  bool _isLoading = true;
  bool _isSuccess = false;
  String? _errorMessage;
  bool _showPasskeyPrompt = false;

  @override
  void initState() {
    super.initState();
    // Après la première image, jamais pendant `initState` : la vérification
    // passe par `AuthNotifier.verifyEmail`, qui pose `isLoading` avant son
    // premier `await` — modifier un provider pendant que l'arbre se construit
    // est refusé par Riverpod (test/features/auth/verify_email_page_test.dart).
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) _verifyEmail();
    });
  }

  Future<void> _verifyEmail() async {
    if (widget.token == null || widget.token!.isEmpty) {
      setState(() {
        _isLoading = false;
        _errorMessage = 'auth.verifyEmail.tokenMissing'.tr();
      });
      return;
    }

    try {
      final authNotifier = ref.read(authProvider.notifier);
      await authNotifier.verifyEmail(widget.token!);

      // Check if passkeys are supported
      final passkeyService = ref.read(passkeyServiceProvider);
      final supported = await passkeyService.isSupported();

      if (mounted) {
        setState(() {
          _isLoading = false;
          _isSuccess = true;
          _showPasskeyPrompt = supported;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isLoading = false;
          _errorMessage = getErrorMessage(e);
        });
      }
    }
  }

  Future<void> _registerPasskey() async {
    setState(() => _isLoading = true);

    try {
      final passkeyService = ref.read(passkeyServiceProvider);
      await passkeyService.register(deviceName: 'Mobile');
      if (mounted) {
        goAfterSignIn(ref);
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isLoading = false;
          _errorMessage = getErrorMessage(e);
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('auth.verifyEmail.title'.tr()),
        centerTitle: true,
      ),
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                if (_isLoading) ...[
                  const CircularProgressIndicator(),
                  const SizedBox(height: 24),
                  Text('auth.verifyEmail.verifying'.tr()),
                ] else if (_errorMessage != null) ...[
                  Icon(
                    Icons.error_outline,
                    size: 80,
                    color: theme.colorScheme.error,
                  ),
                  const SizedBox(height: 24),
                  Text(
                    'common.error'.tr(),
                    style: theme.textTheme.headlineSmall,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    _errorMessage!,
                    key: keys.login.verifyError,
                    textAlign: TextAlign.center,
                    style: TextStyle(color: theme.colorScheme.error),
                  ),
                  const SizedBox(height: 24),
                  FilledButton(
                    key: keys.login.verifyBackToLoginButton,
                    onPressed: () => context.go(Paths.login()),
                    child: Text('auth.verifyEmail.backToLogin'.tr()),
                  ),
                ] else if (_isSuccess) ...[
                  if (_showPasskeyPrompt) ...[
                    Icon(
                      Icons.check_circle,
                      size: 80,
                      color: theme.colorScheme.primary,
                    ),
                    const SizedBox(height: 24),
                    Text(
                      'auth.verifyEmail.success'.tr(),
                      key: keys.login.verifySuccess,
                      style: theme.textTheme.headlineSmall,
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'auth.verifyEmail.accountCreated'.tr(),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 32),
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          children: [
                            Icon(
                              Icons.fingerprint,
                              size: 48,
                              color: theme.colorScheme.primary,
                            ),
                            const SizedBox(height: 16),
                            Text(
                              'auth.passkey.prompt'.tr(),
                              style: theme.textTheme.titleMedium,
                            ),
                            const SizedBox(height: 8),
                            Text(
                              'auth.passkey.description'.tr(),
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 16),
                            FilledButton.icon(
                              onPressed: _registerPasskey,
                              icon: const Icon(Icons.fingerprint),
                              label: Text('auth.passkey.register'.tr()),
                            ),
                            const SizedBox(height: 8),
                            TextButton(
                              key: keys.login.verifyLaterButton,
                              onPressed: () => goAfterSignIn(ref),
                              child: Text('common.later'.tr()),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ] else ...[
                    Icon(
                      Icons.check_circle,
                      size: 80,
                      color: theme.colorScheme.primary,
                    ),
                    const SizedBox(height: 24),
                    Text(
                      'auth.verifyEmail.success'.tr(),
                      key: keys.login.verifySuccess,
                      style: theme.textTheme.headlineSmall,
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'auth.verifyEmail.accountCreated'.tr(),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 24),
                    FilledButton(
                      key: keys.login.verifyContinueButton,
                      onPressed: () => goAfterSignIn(ref),
                      child: Text('common.continue'.tr()),
                    ),
                  ],
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }
}
