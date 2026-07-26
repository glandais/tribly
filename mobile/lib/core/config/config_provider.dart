import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../api/generated/export.dart';
import '../../api/pedalons_api_client.dart';
import '../preferences/user_preferences_provider.dart';

/// `GET /api/config`, la configuration servie plutôt que compilée.
///
/// Elle est **servie** précisément pour qu'un changement de fournisseur de
/// tuiles, de centre par défaut ou de plancher de version n'impose pas de
/// livrer une version du client. Rien de ce qu'elle porte ne doit donc être
/// recopié en dur ailleurs.
///
/// `keepAlive` : c'est une donnée de démarrage, pas une donnée d'écran.
final appConfigProvider = FutureProvider<ConfigDto>(
  (Ref ref) => ref.watch(configurationClientProvider).getConfig(),
  isAutoDispose: false,
);

const String _kMapStyleKey = 'map.styleId';

/// Le fond de carte choisi, mémorisé localement.
///
/// Ce choix ne remonte pas au serveur : il n'existe pas au contrat, et il est
/// légitimement propre à l'appareil — on ne veut pas du fond satellite choisi
/// sur mobile en ouvrant le site sur un grand écran.
final mapStyleIdProvider = NotifierProvider<MapStyleIdNotifier, String?>(
  MapStyleIdNotifier.new,
);

class MapStyleIdNotifier extends Notifier<String?> {
  @override
  String? build() =>
      ref.watch(sharedPreferencesProvider).getString(_kMapStyleKey);

  Future<void> select(String? styleId) async {
    state = styleId;
    final SharedPreferences prefs = ref.read(sharedPreferencesProvider);
    if (styleId == null) {
      await prefs.remove(_kMapStyleKey);
    } else {
      await prefs.setString(_kMapStyleKey, styleId);
    }
  }
}

/// Le style de fond de carte effectif, résolu pour une luminosité donnée.
///
/// Le sélecteur liste `ConfigDto.mapStyles` **dans l'ordre servi** ; en sombre
/// on charge `darkVariant` **s'il est non nul**, sinon le style clair — un
/// fond sans variante sombre reste utilisable, il n'est pas escamoté.
@immutable
class ResolvedMapStyle {
  const ResolvedMapStyle({required this.style, required this.url});

  final MapStyleDto style;
  final String url;
}

ResolvedMapStyle? resolveMapStyle(
  List<MapStyleDto> styles,
  String? selectedId,
  Brightness brightness,
) {
  if (styles.isEmpty) return null;
  MapStyleDto? chosen;
  for (final MapStyleDto s in styles) {
    if (s.id == selectedId) {
      chosen = s;
      break;
    }
  }
  // Un identifiant mémorisé qui a disparu du contrat retombe sur le premier
  // style servi : mieux vaut une carte que rien.
  chosen ??= styles.first;
  final String? dark = chosen.darkVariant;
  return ResolvedMapStyle(
    style: chosen,
    url: brightness == Brightness.dark && dark != null && dark.isNotEmpty
        ? dark
        : chosen.url,
  );
}

/// L'URL de style à passer à MapLibre, pour la luminosité demandée.
///
/// **Aucune URL de style n'est codée en dur nulle part** : changer
/// `mapStyles` côté serveur change le sélecteur sans livrer une version.
final mapStyleProvider =
    Provider.family<AsyncValue<ResolvedMapStyle?>, Brightness>(
      (Ref ref, Brightness brightness) => ref
          .watch(appConfigProvider)
          .whenData(
            (ConfigDto config) => resolveMapStyle(
              config.mapStyles,
              ref.watch(mapStyleIdProvider),
              brightness,
            ),
          ),
    );

/// Où une carte s'ouvre avant de savoir ce qu'elle montre.
final defaultCenterProvider = Provider<AsyncValue<MapCenterDto>>(
  (Ref ref) =>
      ref.watch(appConfigProvider).whenData((ConfigDto c) => c.defaultCenter),
);

/// L'équipe à laquelle le domaine est épinglé, quand il l'est.
final pinnedTeamSlugProvider = Provider<String?>(
  (Ref ref) => ref.watch(appConfigProvider).value?.pinnedTeamSlug,
);

/// Vrai quand cette version du client est plus ancienne que le plancher servi.
///
/// Alimente un bandeau d'avertissement persistant. `null` tant que la
/// configuration ou la version locale n'est pas connue : on ne prévient pas
/// d'une obsolescence qu'on n'a pas encore constatée.
final appOutdatedProvider = FutureProvider<bool>((Ref ref) async {
  final ConfigDto config = await ref.watch(appConfigProvider.future);
  final String? floor = config.minSupportedAppVersion;
  if (floor == null || floor.isEmpty) return false;
  final PackageInfo info = await PackageInfo.fromPlatform();
  return compareSemver(info.version, floor) < 0;
});

/// Compare deux versions semver. Les qualificatifs de pré-version sont
/// ignorés : le plancher porte sur la version livrée, pas sur une RC.
int compareSemver(String a, String b) {
  List<int> parts(String v) {
    final String core = v.split('-').first.split('+').first;
    final List<int> out = <int>[
      for (final String p in core.split('.')) int.tryParse(p) ?? 0,
    ];
    while (out.length < 3) {
      out.add(0);
    }
    return out;
  }

  final List<int> pa = parts(a);
  final List<int> pb = parts(b);
  for (int i = 0; i < 3; i++) {
    final int c = pa[i].compareTo(pb[i]);
    if (c != 0) return c;
  }
  return 0;
}
