import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../core/utils/link_launcher.dart';
import '../../../auth/domain/auth_state.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../auth/data/auth_repository.dart';
import '../../data/profile_repository.dart';
import 'confirm_sheet.dart';

/// Relit l'utilisateur pour que la carte reflète la connexion qui vient de
/// changer : `UserDto.connectedServices` et `socialIdentities` en sont la
/// seule source, et rien ne les pousse.
Future<void> _refreshUser(WidgetRef ref) async {
  final String? token = ref.read(accessTokenHolderProvider);
  if (token == null) return;
  final UserDto user = await ref.read(authRepositoryProvider).getMe(token);
  ref.read(authProvider.notifier).setUser(user);
}

/// Les services GPS que **le serveur** propose.
///
/// Jamais une liste codée en dur : `GET /api/gps/available` existe précisément
/// pour qu'un service ajouté au backend apparaisse sans nouvelle version de
/// l'app, et pour qu'un service retiré cesse d'être proposé.
final availableGpsProvider = FutureProvider<List<GpsServiceType>>((ref) async {
  return ref.watch(profileRepositoryProvider).availableGpsServices();
});

/// Appareils GPS : Garmin, Karoo, et ce que le serveur ajoutera.
///
/// **Pas de logo officiel** : aucun `logoUrl` n'est servi pour ces services, et
/// embarquer les marques dans les assets poserait une question de droits pour
/// un gain nul. Un avatar-lettre teinté, comme la maquette.
class GpsServicesCard extends ConsumerStatefulWidget {
  const GpsServicesCard({super.key});

  @override
  ConsumerState<GpsServicesCard> createState() => _GpsServicesCardState();
}

class _GpsServicesCardState extends ConsumerState<GpsServicesCard> {
  GpsServiceType? _busy;
  String? _error;

  Future<void> _connect(GpsServiceType service) async {
    setState(() {
      _busy = service;
      _error = null;
    });
    try {
      final String url = await ref
          .read(profileRepositoryProvider)
          .gpsConnectUrl(service);
      if (!mounted) return;
      // L'autorisation se donne chez le fournisseur, dans son navigateur : la
      // rejouer dans une webvue embarquée est ce que les fournisseurs OAuth
      // refusent, et ce que les magasins d'applications sanctionnent.
      await openLink(context, url);
      if (mounted) setState(() => _busy = null);
    } catch (error, stackTrace) {
      if (!mounted) return;
      setState(() {
        _busy = null;
        _error = getErrorMessage(error, stackTrace);
      });
    }
  }

  Future<void> _disconnect(GpsServiceType service, String label) async {
    final bool confirmed = await confirmDestructive(
      context,
      title: 'profile.gps.disconnectTitle'.tr(
        namedArgs: <String, String>{'service': label},
      ),
      message: 'profile.gps.disconnectMessage'.tr(),
      confirmLabel: 'profile.gps.disconnect'.tr(),
    );
    if (!confirmed || !mounted) return;

    setState(() {
      _busy = service;
      _error = null;
    });
    try {
      await ref.read(profileRepositoryProvider).disconnectGps(service);
      await _refreshUser(ref);
      if (mounted) setState(() => _busy = null);
    } catch (error, stackTrace) {
      if (!mounted) return;
      setState(() {
        _busy = null;
        _error = getErrorMessage(error, stackTrace);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;
    final List<GpsServiceType> available =
        ref.watch(availableGpsProvider).value ?? const <GpsServiceType>[];
    final List<GpsServiceConnectionDto> connected =
        ref.watch(
          authProvider.select((AuthState s) => s.user?.connectedServices),
        ) ??
        const <GpsServiceConnectionDto>[];

    if (available.isEmpty) {
      return PdlCard(child: Text('profile.gps.none'.tr(), style: t.sub));
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlCard(
          padding: PdlCardPadding.none,
          child: Column(
            children: <Widget>[
              for (int i = 0; i < available.length; i++)
                _serviceRow(
                  context,
                  service: available[i],
                  connection: connected
                      .where(
                        (GpsServiceConnectionDto s) =>
                            s.serviceType == available[i].json,
                      )
                      .firstOrNull,
                  showDivider: i < available.length - 1,
                ),
              Padding(
                padding: const EdgeInsets.fromLTRB(
                  PdlSpacing.section,
                  0,
                  PdlSpacing.section,
                  PdlSpacing.cardTight,
                ),
                child: Text('profile.gps.hint'.tr(), style: t.xs),
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

  Widget _serviceRow(
    BuildContext context, {
    required GpsServiceType service,
    required GpsServiceConnectionDto? connection,
    required bool showDivider,
  }) {
    final String label = connection?.displayName ?? 'gps.${service.json}'.tr();
    final DateTime? since = connection == null
        ? null
        : DateTime.tryParse(connection.connectedAt)?.toLocal();

    return PdlSettingRow(
      showDivider: showDivider,
      title: label,
      subtitle: since == null
          ? 'profile.gps.notConnected'.tr()
          : 'profile.gps.connectedOn'.tr(
              namedArgs: <String, String>{
                'date': AppFormatters.formatLongDate(since),
              },
            ),
      trailing: PdlButton(
        label: connection == null
            ? 'profile.gps.connect'.tr()
            : 'profile.gps.disconnect'.tr(),
        variant: PdlButtonVariant.outline,
        size: PdlButtonSize.sm,
        loading: _busy == service,
        loadingLabel: 'common.loading'.tr(),
        onPressed: connection == null
            ? () => _connect(service)
            : () => _disconnect(service, label),
      ),
    );
  }
}

/// Comptes liés — Strava.
///
/// **Aucun nom de compte distant** : `SocialIdentityDto` porte `provider`,
/// `displayName` et `linkedAt`, et rien qui identifie le compte chez le
/// fournisseur. La maquette écrit « gaby.landais · lié le 12 février » ; on ne
/// livre que la date, parce que l'autre moitié serait inventée.
class LinkedAccountsCard extends ConsumerStatefulWidget {
  const LinkedAccountsCard({super.key});

  @override
  ConsumerState<LinkedAccountsCard> createState() => _LinkedAccountsCardState();
}

class _LinkedAccountsCardState extends ConsumerState<LinkedAccountsCard> {
  bool _busy = false;
  String? _error;

  Future<void> _link() async {
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      final String url = await ref
          .read(profileRepositoryProvider)
          .stravaConnectUrl();
      if (!mounted) return;
      await openLink(context, url);
      if (mounted) setState(() => _busy = false);
    } catch (error, stackTrace) {
      if (!mounted) return;
      setState(() {
        _busy = false;
        _error = getErrorMessage(error, stackTrace);
      });
    }
  }

  Future<void> _unlink() async {
    final bool confirmed = await confirmDestructive(
      context,
      title: 'profile.linked.unlinkTitle'.tr(),
      message: 'profile.linked.unlinkMessage'.tr(),
      confirmLabel: 'profile.linked.unlink'.tr(),
    );
    if (!confirmed || !mounted) return;

    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      await ref.read(profileRepositoryProvider).unlinkStrava();
      await _refreshUser(ref);
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
    final List<SocialIdentityDto> identities =
        ref.watch(
          authProvider.select((AuthState s) => s.user?.socialIdentities),
        ) ??
        const <SocialIdentityDto>[];
    final SocialIdentityDto? strava = identities
        .where((SocialIdentityDto i) => i.provider.toUpperCase() == 'STRAVA')
        .firstOrNull;
    final DateTime? linkedAt = strava == null
        ? null
        : DateTime.tryParse(strava.linkedAt)?.toLocal();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlCard(
          padding: PdlCardPadding.none,
          child: PdlSettingRow(
            title: strava?.displayName ?? 'profile.linked.strava'.tr(),
            subtitle: linkedAt == null
                ? 'profile.linked.notLinked'.tr()
                : 'profile.linked.linkedOn'.tr(
                    namedArgs: <String, String>{
                      'date': AppFormatters.formatLongDate(linkedAt),
                    },
                  ),
            trailing: PdlButton(
              label: strava == null
                  ? 'profile.linked.link'.tr()
                  : 'profile.linked.unlink'.tr(),
              variant: PdlButtonVariant.outline,
              size: PdlButtonSize.sm,
              loading: _busy,
              loadingLabel: 'common.loading'.tr(),
              onPressed: strava == null ? _link : _unlink,
            ),
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
