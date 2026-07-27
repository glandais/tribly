import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../auth/data/auth_repository.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../data/profile_repository.dart';
import 'confirm_sheet.dart';

/// Le dernier export RGPD, ou `null` s'il n'y en a jamais eu.
final latestExportProvider = FutureProvider<UserExportDto?>((ref) async {
  return ref.watch(profileRepositoryProvider).latestExport();
});

/// Vos données : l'export RGPD.
///
/// Trois états, et ils ne se ressemblent pas : jamais demandé, en préparation,
/// prêt jusqu'à une date. Le troisième porte sa **date d'expiration** — un
/// lien de téléchargement qui a expiré sans prévenir est un ticket de support.
class DataExportCard extends ConsumerStatefulWidget {
  const DataExportCard({super.key});

  @override
  ConsumerState<DataExportCard> createState() => _DataExportCardState();
}

class _DataExportCardState extends ConsumerState<DataExportCard> {
  bool _busy = false;
  String? _error;

  Future<void> _request() async {
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      await ref.read(profileRepositoryProvider).requestExport();
      ref.invalidate(latestExportProvider);
      if (mounted) setState(() => _busy = false);
    } catch (error, stackTrace) {
      if (!mounted) return;
      setState(() {
        _busy = false;
        _error = getErrorMessage(error, stackTrace);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;
    final UserExportDto? export = ref.watch(latestExportProvider).value;
    final bool ready = export?.status.toUpperCase() == 'COMPLETED';
    final DateTime? expires = export?.expiresAt == null
        ? null
        : DateTime.tryParse(export!.expiresAt!)?.toLocal();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlCard(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              Row(
                children: <Widget>[
                  Icon(PdlIcons.gpx, size: 24, color: context.pdl.textDimmed),
                  const SizedBox(width: PdlSpacing.cardTight),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        Text('profile.data.export'.tr(), style: t.bodyStrong),
                        Text(switch (export?.status.toUpperCase()) {
                          null => 'profile.data.never'.tr(),
                          'COMPLETED' when expires != null =>
                            'profile.data.readyUntil'.tr(
                              namedArgs: <String, String>{
                                'date': AppFormatters.formatLongDate(expires),
                              },
                            ),
                          'COMPLETED' => 'profile.data.ready'.tr(),
                          'FAILED' => 'profile.data.failed'.tr(),
                          _ => 'profile.data.preparing'.tr(),
                        }, style: t.xs),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: PdlSpacing.chipGap),
              // **Pas de bouton de téléchargement.** `GET
              // /api/export/download/{token}` attend « le jeton reçu dans
              // l'e-mail de notification », et `UserExportDto` ne porte ni ce
              // jeton ni d'URL : l'archive se récupère depuis le message, et
              // dessiner un bouton ici mènerait à un 404.
              Text('profile.data.byEmail'.tr(), style: t.xs),
              const SizedBox(height: PdlSpacing.cardTight),
              PdlButton(
                label: ready
                    ? 'profile.data.newExport'.tr()
                    : 'profile.data.request'.tr(),
                variant: PdlButtonVariant.outline,
                fullWidth: true,
                loading: _busy,
                loadingLabel: 'profile.data.requesting'.tr(),
                onPressed: _request,
              ),
            ],
          ),
        ),
        if (_error != null) ...<Widget>[
          const SizedBox(height: PdlSpacing.chipGap),
          PdlBanner(tone: PdlBannerTone.danger, message: _error!),
        ],
      ],
    );
  }
}

/// Compte et zone de danger.
///
/// **`logout-all` était écrit et câblé nulle part** : le dépôt l'expose depuis
/// le début, aucun écran ne l'appelait. C'est la seule façon de reprendre la
/// main quand une session traîne sur un appareil qu'on n'a plus.
class AccountSection extends ConsumerStatefulWidget {
  const AccountSection({super.key});

  @override
  ConsumerState<AccountSection> createState() => _AccountSectionState();
}

class _AccountSectionState extends ConsumerState<AccountSection> {
  bool _busy = false;
  String? _error;

  Future<void> _run(Future<void> Function() action) async {
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      await action();
      if (mounted) setState(() => _busy = false);
    } catch (error, stackTrace) {
      if (!mounted) return;
      setState(() {
        _busy = false;
        _error = getErrorMessage(error, stackTrace);
      });
    }
  }

  Future<void> _logout() async {
    await ref.read(authProvider.notifier).logout();
    if (mounted) context.go(Paths.login());
  }

  Future<void> _logoutAll() async {
    final bool confirmed = await confirmDestructive(
      context,
      title: 'profile.account.logoutAllTitle'.tr(),
      message: 'profile.account.logoutAllMessage'.tr(),
      confirmLabel: 'profile.account.logoutAll'.tr(),
    );
    if (!confirmed || !mounted) return;
    await _run(() async {
      await ref.read(authRepositoryProvider).logoutAll();
      await ref.read(authProvider.notifier).logout();
      if (mounted) context.go(Paths.login());
    });
  }

  Future<void> _deleteAccount() async {
    final bool confirmed = await confirmDestructive(
      context,
      title: 'profile.account.deleteTitle'.tr(),
      message: 'profile.account.deleteMessage'.tr(),
      confirmLabel: 'profile.account.delete'.tr(),
    );
    if (!confirmed || !mounted) return;
    await _run(() async {
      await ref.read(profileRepositoryProvider).deleteAccount();
      await ref.read(authProvider.notifier).logout();
      if (mounted) context.go(Paths.login());
    });
  }

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;

    return Padding(
      padding: const EdgeInsets.fromLTRB(
        PdlSpacing.section,
        0,
        PdlSpacing.section,
        PdlSpacing.section,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          PdlSectionHeader(title: 'profile.account.title'.tr()),
          PdlButton(
            label: 'profile.account.logout'.tr(),
            variant: PdlButtonVariant.outline,
            fullWidth: true,
            enabled: !_busy,
            onPressed: () => _run(_logout),
          ),
          const SizedBox(height: PdlSpacing.chipGap),
          PdlButton(
            label: 'profile.account.logoutAll'.tr(),
            variant: PdlButtonVariant.outline,
            fullWidth: true,
            enabled: !_busy,
            onPressed: _logoutAll,
          ),
          if (_error != null) ...<Widget>[
            const SizedBox(height: PdlSpacing.chipGap),
            PdlBanner(tone: PdlBannerTone.danger, message: _error!),
          ],
          const SizedBox(height: 20),
          Divider(height: 1, color: c.borderSubtle),
          const SizedBox(height: 14),
          Text(
            'profile.account.dangerZone'.tr(),
            style: t.sectionTitle.copyWith(fontSize: 16, color: c.dangerOnSoft),
          ),
          const SizedBox(height: 6),
          Text('profile.account.dangerHint'.tr(), style: t.sub),
          const SizedBox(height: PdlSpacing.cardTight),
          // Contour rouge, **jamais un aplat** : un aplat rouge pleine largeur
          // se touche par réflexe, et celui-ci ne se défait pas.
          PdlButton(
            label: 'profile.account.delete'.tr(),
            variant: PdlButtonVariant.danger,
            fullWidth: true,
            enabled: !_busy,
            onPressed: _deleteAccount,
          ),
        ],
      ),
    );
  }
}
