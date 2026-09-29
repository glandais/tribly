import 'package:dio/dio.dart' show Dio;
import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:path_provider/path_provider.dart';
import 'package:share_plus/share_plus.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pdl/pdl.dart';
import '../../../core/theme/pdl_icons.dart';

/// Les exports d'un parcours, partagés par la fiche parcours
/// (`RouteDownloadActions`) et par les cartes de groupe d'une sortie : les
/// mêmes gestes doivent se comporter de la même façon partout.
///
/// Les fonctions lèvent en cas d'échec ; c'est à l'appelant d'afficher son
/// bandeau.

/// Télécharge [asset] dans le répertoire temporaire, puis ouvre la feuille de
/// partage du système sur le fichier.
Future<void> downloadAndShareRouteAsset(
  WidgetRef ref,
  AssetDto asset, {
  required Rect origin,
}) async {
  final Dio dio = ref.read(dioProvider);
  final String path =
      '${(await getTemporaryDirectory()).path}/'
      '${asset.fileName}';
  await dio.download(asset.url, path);
  await SharePlus.instance.share(
    ShareParams(files: <XFile>[XFile(path)], sharePositionOrigin: origin),
  );
}

/// Le choix de l'appareil passe par une `PdlSheet` : `PdlSheet.show` force
/// `useRootNavigator`, donc la feuille passe au-dessus de la barre d'onglets
/// (F-DE-8). `null` si le membre referme la feuille sans choisir.
Future<GpsServiceConnectionDto?> pickGpsService(
  BuildContext context,
  List<GpsServiceConnectionDto> services,
) {
  return PdlSheet.show<GpsServiceConnectionDto>(
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
}

/// Envoie le parcours [routeSlug] de [teamSlug] vers [service].
Future<void> uploadRouteToService(
  WidgetRef ref,
  GpsServiceConnectionDto service, {
  required String teamSlug,
  required String routeSlug,
}) {
  return ref
      .read(gpsServicesClientProvider)
      .uploadRoute(
        serviceType: GpsServiceType.fromJson(service.serviceType),
        teamSlug: teamSlug,
        routeSlug: routeSlug,
      );
}

/// Le rectangle d'où part la feuille de partage (iPad) : celui de [context].
Rect shareOriginOf(BuildContext context) {
  final RenderBox? box = context.findRenderObject() as RenderBox?;
  return box == null ? Rect.zero : box.localToGlobal(Offset.zero) & box.size;
}
