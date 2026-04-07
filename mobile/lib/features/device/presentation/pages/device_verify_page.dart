import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/utils/api_error_handler.dart';

/// Device verification page for Karoo/Garmin device code flow.
/// The user enters or receives a 6-character code from their GPS device
/// and this page authorizes the device.
class DeviceVerifyPage extends ConsumerStatefulWidget {
  final String? code;

  const DeviceVerifyPage({super.key, this.code});

  @override
  ConsumerState<DeviceVerifyPage> createState() => _DeviceVerifyPageState();
}

class _DeviceVerifyPageState extends ConsumerState<DeviceVerifyPage> {
  bool _isVerifying = false;
  bool _isCompleting = false;
  bool _completed = false;
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
          setState(() => _isVerifying = false);
          _completeAuthorization(code);
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
        body: CompleteRequest(userCode: code),
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
    if (_isVerifying || _isCompleting) {
      return Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircularProgressIndicator(),
          const SizedBox(height: 24),
          Text(
            _isCompleting
                ? 'device.completing'.tr()
                : 'device.verifying'.tr(),
          ),
        ],
      );
    }

    if (_completed) {
      return Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.check_circle, size: 80, color: theme.colorScheme.primary),
          const SizedBox(height: 24),
          Text(
            'device.success.title'.tr(),
            style: theme.textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          Text(
            'device.success.message'.tr(),
            textAlign: TextAlign.center,
          ),
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

    if (_errorMessage != null) {
      return Column(
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
            onPressed: () {
              setState(() {
                _codeController.clear();
                _errorMessage = null;
              });
            },
            child: Text('device.manualEntry.tryAgain'.tr()),
          ),
        ],
      );
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
          onPressed: _submitCode,
          child: Text('common.continue'.tr()),
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
