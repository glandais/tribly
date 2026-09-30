import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../keys.dart';

/// Device verification page for Karoo/Garmin device code flow.
/// The user enters or receives a 6-character code from their GPS device,
/// then explicitly authorizes or denies it: opening a link that carries a code
/// is never enough to pair a device (docs/LEDGER_*.md SEC-2, audit H3).
class DeviceVerifyPage extends ConsumerStatefulWidget {
  final String? code;

  const DeviceVerifyPage({super.key, this.code});

  @override
  ConsumerState<DeviceVerifyPage> createState() => _DeviceVerifyPageState();
}

class _DeviceVerifyPageState extends ConsumerState<DeviceVerifyPage> {
  bool _isVerifying = false;
  bool _isCompleting = false;
  bool _isDenying = false;
  bool _completed = false;
  bool _denied = false;

  /// The code the backend knows, awaiting the rider's answer.
  VerifyResponse? _pending;
  String? _errorMessage;
  final _codeController = TextEditingController();

  @override
  void initState() {
    super.initState();
    if (widget.code != null && widget.code!.isNotEmpty) {
      _verifyCode(widget.code!.toUpperCase());
    }
  }

  @override
  void dispose() {
    _codeController.dispose();
    super.dispose();
  }

  Future<void> _verifyCode(String code) async {
    setState(() {
      _isVerifying = true;
      _errorMessage = null;
    });

    try {
      final client = DeviceOAuthClient(
        ref.read(dioProvider),
        baseUrl: ref.read(dioProvider).options.baseUrl,
      );
      final response = await client.verify(code: code);
      if (mounted) {
        if (response.authorized == true) {
          setState(() {
            _isVerifying = false;
            _completed = true;
          });
        } else {
          setState(() {
            _isVerifying = false;
            _pending = response;
          });
        }
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isVerifying = false;
          _errorMessage = 'device.errors.invalidCodeMessage'.tr();
        });
      }
    }
  }

  Future<void> _completeAuthorization(String code) async {
    setState(() => _isCompleting = true);

    try {
      final client = DeviceOAuthClient(
        ref.read(dioProvider),
        baseUrl: ref.read(dioProvider).options.baseUrl,
      );
      await client.deviceComplete(
        body: CompleteRequest(userCode: code, confirmed: true),
      );
      if (mounted) {
        setState(() {
          _isCompleting = false;
          _completed = true;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isCompleting = false;
          _errorMessage = getErrorMessage(e);
        });
      }
    }
  }

  Future<void> _denyAuthorization(String code) async {
    setState(() => _isDenying = true);

    try {
      final client = DeviceOAuthClient(
        ref.read(dioProvider),
        baseUrl: ref.read(dioProvider).options.baseUrl,
      );
      await client.deviceDeny(body: DenyRequest(userCode: code));
      if (mounted) {
        setState(() {
          _isDenying = false;
          _denied = true;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isDenying = false;
          _errorMessage = getErrorMessage(e);
        });
      }
    }
  }

  void _submitCode() {
    final code = _codeController.text.trim().toUpperCase();
    if (code.length == 6) {
      _verifyCode(code);
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('device.title'.tr()),
        centerTitle: true,
        leading: const BackOrHomeButton(),
      ),
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: _buildContent(theme),
          ),
        ),
      ),
    );
  }

  Widget _buildContent(ThemeData theme) {
    if (_isVerifying || _isCompleting || _isDenying) {
      return Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircularProgressIndicator(),
          const SizedBox(height: 24),
          Text(
            _isCompleting ? 'device.completing'.tr() : 'device.verifying'.tr(),
          ),
        ],
      );
    }

    if (_completed) {
      return Column(
        key: keys.device.success,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.check_circle, size: 80, color: theme.colorScheme.primary),
          const SizedBox(height: 24),
          Text(
            'device.success.title'.tr(),
            style: theme.textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          Text('device.success.message'.tr(), textAlign: TextAlign.center),
          const SizedBox(height: 16),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Text(
                'device.success.returnToDevice'.tr(),
                textAlign: TextAlign.center,
                style: theme.textTheme.bodyMedium,
              ),
            ),
          ),
          const SizedBox(height: 24),
          FilledButton(
            onPressed: () => context.go(Paths.home()),
            child: Text('common.continue'.tr()),
          ),
        ],
      );
    }

    if (_denied) {
      return Column(
        key: keys.device.denied,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.block, size: 80, color: theme.colorScheme.outline),
          const SizedBox(height: 24),
          Text(
            'device.denied.title'.tr(),
            style: theme.textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          Text('device.denied.message'.tr(), textAlign: TextAlign.center),
          const SizedBox(height: 24),
          FilledButton(
            onPressed: () => context.go(Paths.home()),
            child: Text('common.continue'.tr()),
          ),
        ],
      );
    }

    if (_errorMessage != null) {
      return Column(
        key: keys.device.error,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.error_outline, size: 80, color: theme.colorScheme.error),
          const SizedBox(height: 24),
          Text(
            'device.errors.title'.tr(),
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
            key: keys.device.tryAgainButton,
            onPressed: () {
              setState(() {
                _codeController.clear();
                _errorMessage = null;
                _pending = null;
              });
            },
            child: Text('device.manualEntry.tryAgain'.tr()),
          ),
        ],
      );
    }

    final pending = _pending;
    if (pending != null) {
      return _buildConfirmation(theme, pending);
    }

    // Manual code entry
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(Icons.devices, size: 64, color: theme.colorScheme.primary),
        const SizedBox(height: 24),
        Text(
          'device.manualEntry.title'.tr(),
          style: theme.textTheme.headlineSmall,
        ),
        const SizedBox(height: 16),
        Text(
          'device.manualEntry.description'.tr(),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 32),
        SizedBox(
          width: 240,
          child: TextField(
            key: keys.device.codeField,
            controller: _codeController,
            textAlign: TextAlign.center,
            textCapitalization: TextCapitalization.characters,
            maxLength: 6,
            style: theme.textTheme.headlineMedium?.copyWith(
              letterSpacing: 8,
              fontWeight: FontWeight.bold,
            ),
            inputFormatters: [
              FilteringTextInputFormatter.allow(RegExp('[a-zA-Z0-9]')),
              UpperCaseTextFormatter(),
            ],
            decoration: InputDecoration(
              counterText: '',
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            onSubmitted: (_) => _submitCode(),
            autofocus: true,
          ),
        ),
        const SizedBox(height: 24),
        FilledButton(
          key: keys.device.submitButton,
          onPressed: _submitCode,
          child: Text('common.continue'.tr()),
        ),
      ],
    );
  }

  /// Explicit confirmation (RFC 8628 §5.4): which device, which account, which
  /// code, how long ago it was asked for — then the rider decides.
  Widget _buildConfirmation(ThemeData theme, VerifyResponse pending) {
    final deviceName = switch (pending.clientId) {
      'karoo' => 'device.client.karoo'.tr(),
      'garmin' => 'device.client.garmin'.tr(),
      _ => 'device.client.other'.tr(),
    };
    final accountName = ref.read(authProvider).user?.displayName ?? '';
    final requestedAt = DateTime.tryParse(pending.requestedAt);

    return Column(
      key: keys.device.confirm,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(Icons.devices, size: 64, color: theme.colorScheme.primary),
        const SizedBox(height: 24),
        Text(
          'device.confirm.title'.tr(),
          style: theme.textTheme.headlineSmall,
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 16),
        Text(
          'device.confirm.question'.tr(
            namedArgs: {'device': deviceName, 'name': accountName},
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 16),
        Text(
          pending.userCode,
          style: theme.textTheme.headlineMedium?.copyWith(
            letterSpacing: 8,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (requestedAt != null) ...[
          const SizedBox(height: 8),
          Text(
            'device.confirm.requestedAt'.tr(
              namedArgs: {'when': AppFormatters.formatRelative(requestedAt)},
            ),
            style: theme.textTheme.bodySmall,
            textAlign: TextAlign.center,
          ),
        ],
        const SizedBox(height: 16),
        Card(
          color: theme.colorScheme.errorContainer,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Text(
              'device.confirm.warning'.tr(),
              textAlign: TextAlign.center,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.onErrorContainer,
              ),
            ),
          ),
        ),
        const SizedBox(height: 24),
        Row(
          children: [
            Expanded(
              child: OutlinedButton(
                key: keys.device.denyButton,
                onPressed: () => _denyAuthorization(pending.userCode),
                child: Text('device.confirm.deny'.tr()),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: FilledButton(
                key: keys.device.authorizeButton,
                onPressed: () => _completeAuthorization(pending.userCode),
                child: Text('device.confirm.authorize'.tr()),
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class UpperCaseTextFormatter extends TextInputFormatter {
  @override
  TextEditingValue formatEditUpdate(
    TextEditingValue oldValue,
    TextEditingValue newValue,
  ) {
    return newValue.copyWith(text: newValue.text.toUpperCase());
  }
}
