import 'dart:async';

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../config/paths.dart';
import '../../providers/auth_provider.dart';
import '../../services/passkey_service.dart';

enum AuthMethod { passkey, otp, register }

enum LoginStep { methodSelection, emailEntry, otpEntry }

class LoginPage extends ConsumerStatefulWidget {
  const LoginPage({super.key});

  @override
  ConsumerState<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends ConsumerState<LoginPage> {
  AuthMethod? _selectedMethod;
  LoginStep _currentStep = LoginStep.methodSelection;
  final _emailController = TextEditingController();
  final _displayNameController = TextEditingController();
  final _otpController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _isLoading = false;
  bool _passkeySupported = false;
  String? _successMessage;
  String? _errorMessage;
  String? _otpEmail;
  bool _canResendOtp = false;
  int _resendCountdown = 0;
  Timer? _countdownTimer;

  @override
  void initState() {
    super.initState();
    _checkPasskeySupport();
  }

  Future<void> _checkPasskeySupport() async {
    final passkeyService = ref.read(passkeyServiceProvider);
    final supported = await passkeyService.isSupported();
    if (mounted) {
      setState(() => _passkeySupported = supported);
    }
  }

  @override
  void dispose() {
    _countdownTimer?.cancel();
    _emailController.dispose();
    _displayNameController.dispose();
    _otpController.dispose();
    super.dispose();
  }

  void _startResendCountdown() {
    _countdownTimer?.cancel();
    setState(() {
      _resendCountdown = 30;
      _canResendOtp = false;
    });
    _countdownTimer = Timer.periodic(const Duration(seconds: 1), (_) {
      if (_resendCountdown > 0) {
        setState(() => _resendCountdown--);
      } else {
        _countdownTimer?.cancel();
        setState(() => _canResendOtp = true);
      }
    });
  }

  Future<void> _handlePasskeyLogin() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final passkeyService = ref.read(passkeyServiceProvider);
      await passkeyService.authenticate();
      if (mounted) {
        context.go(Paths.home());
      }
    } catch (e) {
      if (mounted) {
        setState(() => _errorMessage = 'auth.errors.authFailed'.tr());
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _handleRequestOtp() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final authNotifier = ref.read(authProvider.notifier);
      await authNotifier.requestOtp(_emailController.text.trim());
      if (mounted) {
        setState(() {
          _otpEmail = _emailController.text.trim();
          _currentStep = LoginStep.otpEntry;
          _otpController.clear();
        });
        _startResendCountdown();
      }
    } catch (e) {
      if (mounted) {
        setState(() => _errorMessage = 'auth.errors.generic'.tr(args: [e.toString()]));
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _handleVerifyOtp(String code) async {
    if (code.length != 6 || _otpEmail == null) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final authNotifier = ref.read(authProvider.notifier);
      await authNotifier.verifyOtp(_otpEmail!, code);
      if (mounted) {
        context.go(Paths.home());
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _errorMessage = 'auth.otp.invalid'.tr();
          _otpController.clear();
        });
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _handleResendOtp() async {
    if (!_canResendOtp || _otpEmail == null) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final authNotifier = ref.read(authProvider.notifier);
      await authNotifier.requestOtp(_otpEmail!);
      if (mounted) {
        _startResendCountdown();
      }
    } catch (e) {
      if (mounted) {
        setState(() => _errorMessage = 'auth.errors.generic'.tr(args: [e.toString()]));
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _handleRegister() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final authNotifier = ref.read(authProvider.notifier);
      await authNotifier.register(
        email: _emailController.text.trim(),
        displayName: _displayNameController.text.trim(),
      );
      if (mounted) {
        setState(() {
          _successMessage = 'auth.verifyEmail.verificationSent'
              .tr(args: [_emailController.text]);
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _errorMessage = 'auth.errors.generic'.tr(args: [e.toString()]));
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  void _goBack() {
    setState(() {
      if (_currentStep == LoginStep.otpEntry) {
        _currentStep = LoginStep.emailEntry;
        _otpEmail = null;
        _otpController.clear();
      } else if (_currentStep == LoginStep.emailEntry) {
        _currentStep = LoginStep.methodSelection;
        _selectedMethod = null;
      }
      _errorMessage = null;
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('auth.login'.tr()),
        centerTitle: true,
        leading: _currentStep != LoginStep.methodSelection
            ? IconButton(
                icon: const Icon(Icons.arrow_back),
                onPressed: _goBack,
              )
            : null,
      ),
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 400),
              child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Logo/Title
              const SizedBox(height: 32),
              Icon(
                Icons.directions_bike,
                size: 80,
                color: theme.colorScheme.primary,
              ),
              const SizedBox(height: 16),
              Text(
                'app.name'.tr(),
                style: theme.textTheme.headlineLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 8),
              Text(
                'app.tagline'.tr(),
                style: theme.textTheme.bodyLarge?.copyWith(
                  color: theme.colorScheme.onSurfaceVariant,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 48),

              // Success message (for registration)
              if (_successMessage != null) ...[
                Card(
                  color: theme.colorScheme.primaryContainer,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        Icon(Icons.check_circle,
                            color: theme.colorScheme.primary, size: 48),
                        const SizedBox(height: 16),
                        Text(
                          _successMessage!,
                          textAlign: TextAlign.center,
                          style: TextStyle(color: theme.colorScheme.onPrimaryContainer),
                        ),
                        const SizedBox(height: 16),
                        TextButton(
                          onPressed: () {
                            setState(() {
                              _successMessage = null;
                              _selectedMethod = null;
                              _currentStep = LoginStep.methodSelection;
                            });
                          },
                          child: Text('common.back'.tr()),
                        ),
                      ],
                    ),
                  ),
                ),
              ] else ...[
                // Error message
                if (_errorMessage != null) ...[
                  Card(
                    color: theme.colorScheme.errorContainer,
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          Icon(Icons.error, color: theme.colorScheme.error),
                          const SizedBox(width: 16),
                          Expanded(
                            child: Text(
                              _errorMessage!,
                              style: TextStyle(color: theme.colorScheme.onErrorContainer),
                            ),
                          ),
                          IconButton(
                            icon: const Icon(Icons.close),
                            onPressed: () =>
                                setState(() => _errorMessage = null),
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                ],

                // Content based on current step
                if (_currentStep == LoginStep.methodSelection)
                  _buildMethodSelection()
                else if (_currentStep == LoginStep.emailEntry)
                  _buildEmailForm()
                else if (_currentStep == LoginStep.otpEntry)
                  _buildOtpEntry(),
              ],
            ],
          ),
        ),
        ),
        ),
      ),
    );
  }

  Widget _buildMethodSelection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        // Passkey button
        if (_passkeySupported) ...[
          FilledButton.icon(
            onPressed: _isLoading ? null : _handlePasskeyLogin,
            icon: const Icon(Icons.fingerprint),
            label: Text('auth.passkey.login'.tr()),
            style: FilledButton.styleFrom(
              padding: const EdgeInsets.symmetric(vertical: 16),
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              const Expanded(child: Divider()),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Text('common.or'.tr()),
              ),
              const Expanded(child: Divider()),
            ],
          ),
          const SizedBox(height: 16),
        ],

        // Email OTP button
        OutlinedButton.icon(
          onPressed: _isLoading
              ? null
              : () => setState(() {
                    _selectedMethod = AuthMethod.otp;
                    _currentStep = LoginStep.emailEntry;
                  }),
          icon: const Icon(Icons.email),
          label: Text('auth.magicLink.title'.tr()),
          style: OutlinedButton.styleFrom(
            padding: const EdgeInsets.symmetric(vertical: 16),
          ),
        ),
        const SizedBox(height: 16),

        // Register button
        TextButton(
          onPressed: _isLoading
              ? null
              : () => setState(() {
                    _selectedMethod = AuthMethod.register;
                    _currentStep = LoginStep.emailEntry;
                  }),
          child: Text('auth.register'.tr()),
        ),
      ],
    );
  }

  Widget _buildEmailForm() {
    final theme = Theme.of(context);
    final isRegistration = _selectedMethod == AuthMethod.register;

    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(
            isRegistration ? 'auth.register'.tr() : 'auth.magicLink.title'.tr(),
            style: theme.textTheme.titleLarge,
          ),
          const SizedBox(height: 8),
          Text(
            isRegistration
                ? 'auth.magicLink.registerPrompt'.tr()
                : 'auth.magicLink.codePrompt'.tr(),
            style: theme.textTheme.bodyMedium?.copyWith(
              color: theme.colorScheme.onSurfaceVariant,
            ),
          ),
          const SizedBox(height: 24),

          // Email field
          TextFormField(
            controller: _emailController,
            keyboardType: TextInputType.emailAddress,
            autofillHints: const [AutofillHints.email],
            decoration: InputDecoration(
              labelText: 'auth.email'.tr(),
              prefixIcon: const Icon(Icons.email),
              border: const OutlineInputBorder(),
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'auth.errors.emailRequired'.tr();
              }
              if (!value.contains('@')) {
                return 'auth.errors.emailInvalid'.tr();
              }
              return null;
            },
          ),
          const SizedBox(height: 16),

          // Display name field (only for registration)
          if (isRegistration) ...[
            TextFormField(
              controller: _displayNameController,
              autofillHints: const [AutofillHints.name],
              decoration: InputDecoration(
                labelText: 'auth.displayName'.tr(),
                prefixIcon: const Icon(Icons.person),
                border: const OutlineInputBorder(),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'auth.errors.displayNameRequired'.tr();
                }
                if (value.length < 2) {
                  return 'auth.errors.displayNameTooShort'.tr();
                }
                if (value.length > 100) {
                  return 'auth.errors.displayNameTooLong'.tr();
                }
                return null;
              },
            ),
            const SizedBox(height: 16),
          ],

          // Submit button
          FilledButton(
            onPressed: _isLoading
                ? null
                : () {
                    if (isRegistration) {
                      _handleRegister();
                    } else {
                      _handleRequestOtp();
                    }
                  },
            style: FilledButton.styleFrom(
              padding: const EdgeInsets.symmetric(vertical: 16),
            ),
            child: _isLoading
                ? const SizedBox(
                    height: 20,
                    width: 20,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : Text(isRegistration
                    ? 'auth.otp.createAccount'.tr()
                    : 'auth.otp.sendCode'.tr()),
          ),
        ],
      ),
    );
  }

  Widget _buildOtpEntry() {
    final theme = Theme.of(context);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const Icon(
          Icons.email_outlined,
          size: 64,
          color: Colors.blue,
        ),
        const SizedBox(height: 16),
        Text(
          'auth.otp.title'.tr(),
          style: theme.textTheme.titleLarge,
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 8),
        Text(
          '${'auth.otp.sent'.tr()}\n$_otpEmail',
          style: theme.textTheme.bodyMedium?.copyWith(
            color: theme.colorScheme.onSurfaceVariant,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 24),

        // Simple 6-digit text field for OTP
        TextField(
          controller: _otpController,
          keyboardType: TextInputType.number,
          maxLength: 6,
          textAlign: TextAlign.center,
          style: theme.textTheme.headlineMedium?.copyWith(
            letterSpacing: 16,
            fontWeight: FontWeight.bold,
          ),
          decoration: InputDecoration(
            hintText: '000000',
            counterText: '',
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
            ),
          ),
          onChanged: (value) {
            if (value.length == 6) {
              _handleVerifyOtp(value);
            }
          },
          enabled: !_isLoading,
        ),

        const SizedBox(height: 8),
        Text(
          'auth.otp.expires'.tr(),
          style: theme.textTheme.bodySmall?.copyWith(
            color: theme.colorScheme.onSurfaceVariant,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 24),

        // Resend button
        TextButton.icon(
          onPressed: _canResendOtp && !_isLoading ? _handleResendOtp : null,
          icon: const Icon(Icons.refresh),
          label: Text(_canResendOtp
              ? 'auth.otp.resend'.tr()
              : 'auth.otp.resendIn'.tr(args: [_resendCountdown.toString()])),
        ),

        if (_isLoading) ...[
          const SizedBox(height: 16),
          const Center(child: CircularProgressIndicator()),
        ],
      ],
    );
  }
}
