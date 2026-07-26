import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../../core/units/unit_system.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/formatters.dart';
import '../../../routes/data/elevation_profile_repository.dart';
import '../../../routes/providers/route_elevation_provider.dart';

/// Le profil altimétrique du groupe sélectionné (écran 12), en 110 px.
///
/// C'est un **enrichissement** : son chargement n'empêche rien, son échec ne
/// produit pas d'écran d'erreur mais une ligne discrète et un « Réessayer ».
/// Le reste de la sortie reste lisible et interactif dans les deux cas.
class RideElevationSection extends ConsumerWidget {
  const RideElevationSection({
    super.key,
    required this.teamSlug,
    required this.routeSlug,
    this.distance,
    this.elevationGain,
  });

  final String teamSlug;

  /// Le parcours du groupe sélectionné. Change avec la sélection ; le cache
  /// par `(routeSlug, samples)` fait que revenir sur un groupe déjà vu ne
  /// recharge rien.
  final String routeSlug;

  final double? distance;
  final double? elevationGain;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);

    final ElevationProfileKey key = ElevationProfileKey.forWidth(
      teamSlug: teamSlug,
      routeSlug: routeSlug,
      logicalWidth: MediaQuery.sizeOf(context).width,
    );
    final AsyncValue<ElevationSamples> samples = ref.watch(
      routeElevationSamplesProvider(key),
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        Row(
          children: <Widget>[
            Expanded(
              child: Text(
                'rides.selectedGroupProfile'.tr(),
                style: t.xs.copyWith(
                  color: c.text,
                  fontWeight: FontWeight.w600,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ),
            if (_summary(units) != null) Text(_summary(units)!, style: t.xs),
          ],
        ),
        const SizedBox(height: 6),
        samples.when(
          data: (ElevationSamples data) => PdlElevationProfile(
            samples: data,
            height: PdlMetrics.elevationMedium,
            // Seule la dernière graduation porte l'unité (§1.3.1).
            axisLabel: (double meters, {required bool isLast}) => isLast
                ? AppFormatters.formatDistance(meters, units)
                : AppFormatters.formatNumber(
                    units.longDistance(meters),
                    fractionDigits: 1,
                  ),
            tipLabel: (ElevationReading r) => <String>[
              AppFormatters.formatDistance(r.distance, units),
              AppFormatters.formatAltitude(r.elevation, units),
              if (r.grade != null) AppFormatters.formatGrade(r.grade!),
            ].join(' · '),
          ),
          loading: () => PdlElevationProfile.loading(
            label: 'routes.profileLoading'.tr(),
            height: PdlMetrics.elevationMedium,
          ),
          error: (Object error, StackTrace stack) => PdlElevationProfile.failed(
            label: 'routes.profileUnavailable'.tr(),
            actionLabel: 'common.retry'.tr(),
            height: PdlMetrics.elevationMedium,
            onRetry: () => ref.invalidate(elevationProfileProvider(key)),
          ),
        ),
      ],
    );
  }

  String? _summary(UnitSystem units) {
    final List<String> parts = <String>[
      if (distance != null) AppFormatters.formatDistance(distance!, units),
      if (elevationGain != null)
        AppFormatters.formatElevationGain(elevationGain!, units),
    ];
    return parts.isEmpty ? null : parts.join(' · ');
  }
}
