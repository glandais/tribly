import 'package:easy_localization/easy_localization.dart';

import '../../../../api/generated/export.dart';
import '../../domain/route_filters.dart';

/// Localized wording for every route filter value.
///
/// Kept out of [RouteFilters] so the domain object stays free of translations.
class RouteFilterLabels {
  RouteFilterLabels._();

  static final NumberFormat _integer = NumberFormat.decimalPattern();
  static final NumberFormat _oneDecimal = NumberFormat.decimalPattern()
    ..minimumFractionDigits = 1
    ..maximumFractionDigits = 1;

  /// Rounded distance, for filter bounds and chips: "30 km".
  static String distance(double meters) => 'routes.filters.kmValue'.tr(
    namedArgs: {'value': _integer.format((meters / 1000).round())},
  );

  /// A route's own distance, where the tenth of a kilometre matters:
  /// "33,9 km".
  static String preciseDistance(double meters) => 'routes.filters.kmValue'.tr(
    namedArgs: {'value': _oneDecimal.format(meters / 1000)},
  );

  static String elevation(double meters) => 'routes.filters.mValue'.tr(
    namedArgs: {'value': _integer.format(meters.round())},
  );

  static String hilliness(Hilliness value) =>
      _enum('routes.hilliness', value.json);

  static String surfaceType(SurfaceType value) =>
      _enum('routes.surfaceType', value.json);

  static String windDirection(WindDirection value) =>
      _enum('routes.windDirection', value.json);

  static String sortBy(RouteSortBy value) => _enum('routes.sort', value.json);

  /// Name of a filter dimension, used as a section title in the sheet and
  /// inside the "remove this filter" wording.
  static String field(RouteFilterField field) => switch (field) {
    RouteFilterField.search => 'routes.filters.search'.tr(),
    RouteFilterField.distance => 'routes.filters.distance'.tr(),
    RouteFilterField.elevationGain => 'routes.filters.elevationGain'.tr(),
    RouteFilterField.hilliness => 'routes.filters.hilliness'.tr(),
    RouteFilterField.surfaceType => 'routes.filters.surfaceType'.tr(),
    RouteFilterField.windDirection => 'routes.filters.windDirection'.tr(),
  };

  /// Value of a filter, as shown on its chip. Null when the field is unset.
  static String? chip(RouteFilters filters, RouteFilterField field) {
    return switch (field) {
      RouteFilterField.search =>
        filters.search?.trim().isEmpty ?? true
            ? null
            : '"${filters.search!.trim()}"',
      RouteFilterField.distance => _range(
        filters.minDistance,
        filters.maxDistance,
        distance,
      ),
      RouteFilterField.elevationGain => _range(
        filters.minElevationGain,
        filters.maxElevationGain,
        elevation,
        suffix: 'routes.filters.elevationSuffix'.tr(),
      ),
      RouteFilterField.hilliness =>
        filters.hilliness == null ? null : hilliness(filters.hilliness!),
      RouteFilterField.surfaceType =>
        filters.surfaceType == null ? null : surfaceType(filters.surfaceType!),
      RouteFilterField.windDirection =>
        filters.windDirection == null
            ? null
            : windDirection(filters.windDirection!),
    };
  }

  /// Summary of a range shown next to a slider: "Tous", "30 – 60 km", "> 30 km".
  static String range(
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
        namedArgs: {'min': format(min), 'max': format(max)},
      );
    } else if (min != null) {
      text = 'routes.filters.rangeFrom'.tr(namedArgs: {'value': format(min)});
    } else {
      text = 'routes.filters.rangeTo'.tr(namedArgs: {'value': format(max!)});
    }
    return suffix == null ? text : '$text $suffix';
  }

  static String _enum(String prefix, String? jsonValue) {
    if (jsonValue == null) return 'routes.filters.any'.tr();
    return '$prefix.${jsonValue.toLowerCase()}'.tr();
  }
}
