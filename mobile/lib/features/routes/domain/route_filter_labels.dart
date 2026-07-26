import 'package:easy_localization/easy_localization.dart';

import '../../../api/generated/export.dart';
import '../../../core/utils/formatters.dart';
import 'route_filters.dart';

/// Les libellés des filtres de parcours.
///
/// Ils vivent ici et non dans `AppFormatters` pour une raison de couches :
/// `core` ne connaît pas `features`. Tout le formatage **numérique** est en
/// revanche délégué à [AppFormatters] — une borne de filtre est une distance
/// ou un dénivelé comme les autres, et doit suivre le système d'unités de
/// l'utilisateur. Il n'y a donc toujours qu'une seule implémentation du
/// formatage, et ce fichier ne fait que la nommer.
abstract final class RouteFilterLabels {
  /// Nom d'une dimension de filtre : titre de section, et sujet du « retirer
  /// ce filtre ».
  static String filterFieldName(RouteFilterField field) => switch (field) {
    RouteFilterField.search => 'routes.filters.search'.tr(),
    RouteFilterField.distance => 'routes.filters.distance'.tr(),
    RouteFilterField.elevationGain => 'routes.filters.elevationGain'.tr(),
    RouteFilterField.hilliness => 'routes.filters.hilliness'.tr(),
    RouteFilterField.surfaceType => 'routes.filters.surfaceType'.tr(),
    RouteFilterField.windDirection => 'routes.filters.windDirection'.tr(),
    RouteFilterField.proximity => 'routes.filters.proximity'.tr(),
  };

  static String hillinessName(Hilliness value) =>
      _enumName('routes.hilliness', value.json);

  static String surfaceTypeName(SurfaceType value) =>
      _enumName('routes.surfaceType', value.json);

  static String windDirectionName(WindDirection value) =>
      _enumName('routes.windDirection', value.json);

  static String routeSortByName(RouteSortBy value) =>
      _enumName('routes.sort', value.json);

  /// Valeur d'un filtre telle qu'elle s'affiche sur sa puce. `null` quand le
  /// champ n'est pas contraint.
  static String? filterChip(
    RouteFilters filters,
    RouteFilterField field, [
    UnitSystem units = UnitSystem.metric,
  ]) {
    return switch (field) {
      RouteFilterField.search =>
        (filters.search?.trim().isEmpty ?? true)
            ? null
            : '"${filters.search!.trim()}"',
      RouteFilterField.distance => _range(
        filters.minDistance,
        filters.maxDistance,
        (double m) => AppFormatters.formatDistanceRounded(m, units),
      ),
      RouteFilterField.elevationGain => _range(
        filters.minElevationGain,
        filters.maxElevationGain,
        (double m) => AppFormatters.formatElevation(m, units),
        suffix: 'units.elevationGain'.tr(),
      ),
      RouteFilterField.hilliness =>
        filters.hilliness == null ? null : hillinessName(filters.hilliness!),
      RouteFilterField.surfaceType =>
        filters.surfaceType == null
            ? null
            : surfaceTypeName(filters.surfaceType!),
      RouteFilterField.windDirection =>
        filters.windDirection == null
            ? null
            : windDirectionName(filters.windDirection!),
      // « Autour de moi · 25 km » : le rayon fait partie du filtre, et le
      // taire rendrait la chip indistincte d'un rayon à 50 km.
      RouteFilterField.proximity =>
        !filters.hasProximity
            ? null
            : 'routes.filters.proximityValue'.tr(
                namedArgs: <String, String>{
                  'radius': AppFormatters.formatDistanceRounded(
                    filters.effectiveNearRadius,
                    units,
                  ),
                },
              ),
    };
  }

  /// Résumé d'une plage, à côté d'un curseur : « Tous », « 30 – 60 km »,
  /// « > 30 km ».
  static String filterRange(
    double? min,
    double? max,
    String Function(double) format,
  ) => _range(min, max, format) ?? 'routes.filters.any'.tr();

  static String? _range(
    double? min,
    double? max,
    String Function(double) format, {
    String? suffix,
  }) {
    if (min == null && max == null) return null;
    final String text;
    if (min != null && max != null) {
      text = 'routes.filters.rangeBetween'.tr(
        namedArgs: <String, String>{'min': format(min), 'max': format(max)},
      );
    } else if (min != null) {
      text = 'routes.filters.rangeFrom'.tr(
        namedArgs: <String, String>{'value': format(min)},
      );
    } else {
      text = 'routes.filters.rangeTo'.tr(
        namedArgs: <String, String>{'value': format(max!)},
      );
    }
    return suffix == null ? text : '$text $suffix';
  }

  static String _enumName(String prefix, String? jsonValue) {
    if (jsonValue == null) return 'routes.filters.any'.tr();
    return '$prefix.${jsonValue.toLowerCase()}'.tr();
  }
}
