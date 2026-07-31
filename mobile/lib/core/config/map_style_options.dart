import 'package:easy_localization/easy_localization.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../api/generated/models/map_style_dto.dart';
import '../pdl/pdl.dart';
import 'config_provider.dart';

/// Les fonds servis, prêts pour le sélecteur de `PdlMapHero`.
///
/// Un seul endroit pour les cinq cartes de l'app : la conversion était recopiée
/// à l'identique dans chacune, et l'ajout des sections l'aurait été aussi.
/// `core/pdl` ne traduit rien — l'intitulé de section est localisé ici.
List<PdlMapStyleOption> servedMapStyleOptions(
  WidgetRef ref,
) => <PdlMapStyleOption>[
  for (final MapStyleDto style
      in ref.watch(appConfigProvider).value?.mapStyles ?? const <MapStyleDto>[])
    PdlMapStyleOption(
      id: style.id,
      label: style.label,
      groupLabel: mapStyleGroupLabel(style.group),
    ),
];

/// L'intitulé d'une section du sélecteur.
///
/// Le contrat laisse `group` libre : un groupe que cette version ne connaît pas
/// est rendu **tel quel**, en section à lui. Le traduire par sa clé afficherait
/// `map.styleGroup.xxx` à l'écran, et l'ignorer rangerait silencieusement le
/// fond sous la section précédente — les deux mentent sur ce que le serveur a
/// classé.
String mapStyleGroupLabel(String group) => switch (group) {
  'vector' => 'map.styleGroup.vector'.tr(),
  'satellite' => 'map.styleGroup.satellite'.tr(),
  'raster' => 'map.styleGroup.raster'.tr(),
  _ => group,
};
