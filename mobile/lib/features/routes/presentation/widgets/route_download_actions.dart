import 'package:dio/dio.dart' show Dio;
import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:path_provider/path_provider.dart';
import 'package:share_plus/share_plus.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../auth/domain/auth_state.dart';
import '../../../auth/providers/auth_provider.dart';

/// La rangée d'exports d'un parcours : GPX plein, FIT contour, appareil,
/// partage.
///
/// Partagée par la barre d'actions collante de l'écran 13 et par
/// [EmbeddedRouteSheet] : les mêmes actions doivent se comporter de la même
/// façon des deux côtés.
///
/// Les échecs sont des **bandeaux**, pas des snackbars — un `SnackBar` posé en
/// bas d'écran passait sous la barre d'onglets et sous la barre d'actions
/// elle-même.
class RouteDownloadActions extends ConsumerStatefulWidget {
  const RouteDownloadActions({super.key, required this.route});

  final RouteDetailDto route;

  @override
  ConsumerState<RouteDownloadActions> createState() =>
      _RouteDownloadActionsState();
}

class _RouteDownloadActionsState extends ConsumerState<RouteDownloadActions> {
  String? _busyLabel;
  Object? _error;
  String? _success;

  RouteDetailDto get route => widget.route;

  @override
  Widget build(BuildContext context) {
    final AssetDto? gpx = route.media.assets.gpx;
    final AssetDto? fit = route.media.assets.fit;
    final List<GpsServiceConnectionDto> services = ref.watch(
      authProvider.select(
        (AuthState s) => s.user?.connectedServices ?? const [],
      ),
    );

    if (gpx == null && fit == null && services.isEmpty) {
      return const SizedBox.shrink();
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        if (_error != null) ...<Widget>[
          PdlBanner(
            tone: PdlBannerTone.danger,
            message: getErrorMessage(_error!),
            onDismiss: () => setState(() => _error = null),
            dismissSemanticLabel: 'common.close'.tr(),
          ),
          const SizedBox(height: PdlSpacing.chipGap),
        ],
        if (_success != null) ...<Widget>[
          PdlBanner(
            tone: PdlBannerTone.info,
            message: _success!,
            onDismiss: () => setState(() => _success = null),
            dismissSemanticLabel: 'common.close'.tr(),
          ),
          const SizedBox(height: PdlSpacing.chipGap),
        ],
        Row(
          children: <Widget>[
            if (gpx != null)
              Expanded(
                child: PdlButton(
                  label: 'routes.downloadGpx'.tr(),
                  loadingLabel: 'routes.downloading'.tr(),
                  icon: PdlIcons.gpx,
                  loading: _busyLabel == 'gpx',
                  onPressed: () => _download(gpx, 'gpx'),
                ),
              ),
            if (fit != null) ...<Widget>[
              const SizedBox(width: PdlSpacing.chipGap),
              PdlButton(
                label: 'routes.exportFit'.tr(),
                variant: PdlButtonVariant.outline,
                loading: _busyLabel == 'fit',
                onPressed: () => _download(fit, 'fit'),
              ),
            ],
            if (services.isNotEmpty) ...<Widget>[
              const SizedBox(width: PdlSpacing.chipGap),
              PdlButton(
                label: '',
                icon: PdlIcons.device,
                variant: PdlButtonVariant.outline,
                onPressed: () => _openDeviceSheet(services),
              ),
            ],
            const SizedBox(width: PdlSpacing.chipGap),
            PdlButton(
              label: '',
              icon: PdlIcons.share,
              variant: PdlButtonVariant.outline,
              onPressed: _share,
            ),
          ],
        ),
      ],
    );
  }

  Future<void> _download(AssetDto asset, String tag) async {
    setState(() {
      _busyLabel = tag;
      _error = null;
    });
    try {
      final Dio dio = ref.read(dioProvider);
      final String path =
          '${(await getTemporaryDirectory()).path}/'
          '${asset.fileName}';
      await dio.download(asset.url, path);
      if (!mounted) return;
      await SharePlus.instance.share(
        ShareParams(
          files: <XFile>[XFile(path)],
          sharePositionOrigin: _origin(),
        ),
      );
    } catch (error) {
      if (mounted) setState(() => _error = error);
    } finally {
      if (mounted) setState(() => _busyLabel = null);
    }
  }

  Future<void> _share() async {
    await SharePlus.instance.share(
      ShareParams(text: route.name, sharePositionOrigin: _origin()),
    );
  }

  Rect _origin() {
    final RenderBox? box = context.findRenderObject() as RenderBox?;
    return box == null ? Rect.zero : box.localToGlobal(Offset.zero) & box.size;
  }

  /// Le choix de l'appareil passe par une `PdlSheet` : `PdlSheet.show` force
  /// `useRootNavigator`, donc la feuille passe au-dessus de la barre d'onglets
  /// (F-DE-8).
  Future<void> _openDeviceSheet(List<GpsServiceConnectionDto> services) async {
    final GpsServiceConnectionDto? picked =
        await PdlSheet.show<GpsServiceConnectionDto>(
          context: context,
          builder: (BuildContext sheetContext) => PdlSheet(
            title: 'routes.sendToDevice'.tr(),
            children: <Widget>[
              for (final GpsServiceConnectionDto s in services)
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  child: PdlSettingRow(
                    title: s.displayName,
                    icon: PdlIcons.device,
                    onTap: () => Navigator.of(sheetContext).pop(s),
                  ),
                ),
            ],
          ),
        );
    if (picked != null) await _upload(picked);
  }

  Future<void> _upload(GpsServiceConnectionDto service) async {
    setState(() {
      _busyLabel = 'device';
      _error = null;
    });
    try {
      await ref
          .read(gpsServicesClientProvider)
          .uploadRoute(
            serviceType: GpsServiceType.fromJson(service.serviceType),
            teamSlug: route.team.slug,
            routeSlug: route.slug,
          );
      if (mounted) setState(() => _success = 'routes.uploadSuccess'.tr());
    } catch (error) {
      if (mounted) setState(() => _error = error);
    } finally {
      if (mounted) setState(() => _busyLabel = null);
    }
  }
}
