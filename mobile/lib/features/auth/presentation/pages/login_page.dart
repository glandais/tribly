import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../config/paths.dart';
import '../../providers/auth_provider.dart';
import '../../services/passkey_service.dart';

enum AuthMethod { passkey, magicLink, register }

class LoginPage extends ConsumerStatefulWidget {
  const LoginPage({super.key});

  @override
  ConsumerState<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends ConsumerState<LoginPage> {
  AuthMethod? _selectedMethod;
  final _emailController = TextEditingController();
  final _displayNameController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _isLoading = false;
  bool _passkeySupported = false;
  String? _successMessage;
  String? _errorMessage;
  bool _showPasteLink = false;

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
    _emailController.dispose();
    _displayNameController.dispose();
    super.dispose();
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
        setState(() => _errorMessage = 'Erreur lors de l\'authentification: $e');
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _handleMagicLink() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final authNotifier = ref.read(authProvider.notifier);
      await authNotifier.requestMagicLink(_emailController.text.trim());
      if (mounted) {
        setState(() {
          _successMessage =
              'Un lien de connexion a été envoyé à ${_emailController.text}';
          _showPasteLink = true;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _errorMessage = 'Erreur: $e');
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
          _successMessage =
              'Un email de vérification a été envoyé à ${_emailController.text}';
          _showPasteLink = true;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _errorMessage = 'Erreur: $e');
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _handlePasteLink() async {
    try {
      final clipboardData = await Clipboard.getData(Clipboard.kTextPlain);
      final link = clipboardData?.text;

      if (link == null || link.isEmpty) {
        setState(() => _errorMessage = 'Aucun lien dans le presse-papiers');
        return;
      }

      // Extract token from magic link or verify email link
      String? token;
      final uri = Uri.tryParse(link);
      if (uri != null) {
        token = uri.queryParameters['token'];
      }

      if (token == null || token.isEmpty) {
        setState(() => _errorMessage = 'Lien invalide: token non trouvé');
        return;
      }

      setState(() {
        _isLoading = true;
        _errorMessage = null;
      });

      final authNotifier = ref.read(authProvider.notifier);

      // Determine if it's a magic link or verify email based on the path
      if (link.contains('magic-link')) {
        await authNotifier.verifyMagicLink(token);
      } else if (link.contains('verify-email')) {
        await authNotifier.verifyEmail(token);
      } else {
        // Try magic link first, then verify email
        try {
          await authNotifier.verifyMagicLink(token);
        } catch (_) {
          await authNotifier.verifyEmail(token);
        }
      }

      if (mounted) {
        context.go(Paths.home());
      }
    } catch (e) {
      if (mounted) {
        setState(() => _errorMessage = 'Erreur de vérification: $e');
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
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
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
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
                'Tribly',
                style: theme.textTheme.headlineLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 8),
              Text(
                'Plateforme pour équipes cyclistes',
                style: theme.textTheme.bodyLarge?.copyWith(
                  color: theme.colorScheme.onSurfaceVariant,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 48),

              // Success message
              if (_successMessage != null) ...[
                Card(
                  color: Colors.green.shade50,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        const Icon(Icons.check_circle, color: Colors.green, size: 48),
                        const SizedBox(height: 16),
                        Text(
                          _successMessage!,
                          textAlign: TextAlign.center,
                          style: const TextStyle(color: Colors.green),
                        ),
                        if (_showPasteLink) ...[
                          const SizedBox(height: 16),
                          const Text(
                            'Consultez votre email, copiez le lien et collez-le ci-dessous:',
                            textAlign: TextAlign.center,
                            style: TextStyle(fontSize: 12),
                          ),
                          const SizedBox(height: 12),
                          FilledButton.icon(
                            onPressed: _isLoading ? null : _handlePasteLink,
                            icon: _isLoading
                                ? const SizedBox(
                                    height: 16,
                                    width: 16,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2,
                                      color: Colors.white,
                                    ),
                                  )
                                : const Icon(Icons.content_paste),
                            label: const Text('Coller le lien'),
                          ),
                        ],
                        const SizedBox(height: 16),
                        TextButton(
                          onPressed: () {
                            setState(() {
                              _successMessage = null;
                              _selectedMethod = null;
                              _showPasteLink = false;
                            });
                          },
                          child: const Text('Retour'),
                        ),
                      ],
                    ),
                  ),
                ),
              ] else ...[
                // Error message
                if (_errorMessage != null) ...[
                  Card(
                    color: Colors.red.shade50,
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          const Icon(Icons.error, color: Colors.red),
                          const SizedBox(width: 16),
                          Expanded(
                            child: Text(
                              _errorMessage!,
                              style: const TextStyle(color: Colors.red),
                            ),
                          ),
                          IconButton(
                            icon: const Icon(Icons.close),
                            onPressed: () => setState(() => _errorMessage = null),
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                ],

                // Method selection
                if (_selectedMethod == null) ...[
                  // Passkey button
                  if (_passkeySupported) ...[
                    FilledButton.icon(
                      onPressed: _isLoading ? null : _handlePasskeyLogin,
                      icon: const Icon(Icons.fingerprint),
                      label: const Text('Connexion avec Passkey'),
                      style: FilledButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 16),
                      ),
                    ),
                    const SizedBox(height: 16),
                    const Row(
                      children: [
                        Expanded(child: Divider()),
                        Padding(
                          padding: EdgeInsets.symmetric(horizontal: 16),
                          child: Text('ou'),
                        ),
                        Expanded(child: Divider()),
                      ],
                    ),
                    const SizedBox(height: 16),
                  ],

                  // Magic link button
                  OutlinedButton.icon(
                    onPressed: _isLoading
                        ? null
                        : () => setState(() => _selectedMethod = AuthMethod.magicLink),
                    icon: const Icon(Icons.email),
                    label: const Text('Connexion par email'),
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 16),
                    ),
                  ),
                  const SizedBox(height: 16),

                  // Register button
                  TextButton(
                    onPressed: _isLoading
                        ? null
                        : () => setState(() => _selectedMethod = AuthMethod.register),
                    child: const Text('Créer un compte'),
                  ),
                ] else ...[
                  // Form for magic link or registration
                  Form(
                    key: _formKey,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        // Back button
                        Align(
                          alignment: Alignment.centerLeft,
                          child: TextButton.icon(
                            onPressed: () => setState(() => _selectedMethod = null),
                            icon: const Icon(Icons.arrow_back),
                            label: const Text('Retour'),
                          ),
                        ),
                        const SizedBox(height: 16),

                        Text(
                          _selectedMethod == AuthMethod.register
                              ? 'Créer un compte'
                              : 'Connexion par email',
                          style: theme.textTheme.titleLarge,
                        ),
                        const SizedBox(height: 24),

                        // Email field
                        TextFormField(
                          controller: _emailController,
                          keyboardType: TextInputType.emailAddress,
                          autofillHints: const [AutofillHints.email],
                          decoration: const InputDecoration(
                            labelText: 'Email',
                            prefixIcon: Icon(Icons.email),
                            border: OutlineInputBorder(),
                          ),
                          validator: (value) {
                            if (value == null || value.isEmpty) {
                              return 'L\'email est requis';
                            }
                            if (!value.contains('@')) {
                              return 'Email invalide';
                            }
                            return null;
                          },
                        ),
                        const SizedBox(height: 16),

                        // Display name field (only for registration)
                        if (_selectedMethod == AuthMethod.register) ...[
                          TextFormField(
                            controller: _displayNameController,
                            autofillHints: const [AutofillHints.name],
                            decoration: const InputDecoration(
                              labelText: 'Nom d\'affichage',
                              prefixIcon: Icon(Icons.person),
                              border: OutlineInputBorder(),
                            ),
                            validator: (value) {
                              if (value == null || value.isEmpty) {
                                return 'Le nom d\'affichage est requis';
                              }
                              if (value.length < 2) {
                                return 'Au moins 2 caractères';
                              }
                              if (value.length > 100) {
                                return '100 caractères maximum';
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
                                  if (_selectedMethod == AuthMethod.register) {
                                    _handleRegister();
                                  } else {
                                    _handleMagicLink();
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
                              : Text(_selectedMethod == AuthMethod.register
                                  ? 'Créer mon compte'
                                  : 'Envoyer le lien'),
                        ),
                      ],
                    ),
                  ),
                ],
              ],
            ],
          ),
        ),
      ),
    );
  }
}
