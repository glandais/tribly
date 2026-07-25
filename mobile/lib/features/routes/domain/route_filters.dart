import 'package:flutter/foundation.dart';

import '../../../api/generated/export.dart';

/// Upper bound of the distance slider, in meters. Beyond it the filter is
/// treated as "no maximum".
const double kRouteDistanceMaxMeters = 300000;

/// Step of the distance slider, in meters.
const double kRouteDistanceStepMeters = 5000;

/// Upper bound of the elevation-gain slider, in meters.
const double kRouteElevationMaxMeters = 5000;

/// Step of the elevation-gain slider, in meters.
const double kRouteElevationStepMeters = 50;

/// Sort defaults, mirroring `frontend/src/components/route/routeFilterDefaults.ts`.
const RouteSortBy kDefaultRouteSortBy = RouteSortBy.dateTime;
const SortDirection kDefaultRouteSortDir = SortDirection.desc;

/// A single filter dimension, used to label chips and to remove one filter at
/// a time.
enum RouteFilterField {
  search,
  distance,
  elevationGain,
  hilliness,
  surfaceType,
  windDirection,
}

/// Immutable identity of a route result set.
///
/// This doubles as the provider family key, which is what makes a filter
/// change produce a brand new list (reset + scroll to top) instead of merging
/// into the previous one.
@immutable
class RouteFilters {
  final String? search;

  /// Distances and elevations are in meters, like the API.
  final double? minDistance;
  final double? maxDistance;
  final double? minElevationGain;
  final double? maxElevationGain;

  final Hilliness? hilliness;
  final SurfaceType? surfaceType;
  final WindDirection? windDirection;
  final RouteSortBy sortBy;
  final SortDirection sortDir;

  const RouteFilters({
    this.search,
    this.minDistance,
    this.maxDistance,
    this.minElevationGain,
    this.maxElevationGain,
    this.hilliness,
    this.surfaceType,
    this.windDirection,
    this.sortBy = kDefaultRouteSortBy,
    this.sortDir = kDefaultRouteSortDir,
  });

  /// Fields the user has actually constrained, in a stable order.
  ///
  /// Sorting is not a filter and never appears here.
  List<RouteFilterField> get activeFields => [
    if (search != null && search!.isNotEmpty) RouteFilterField.search,
    if (minDistance != null || maxDistance != null) RouteFilterField.distance,
    if (minElevationGain != null || maxElevationGain != null)
      RouteFilterField.elevationGain,
    if (hilliness != null) RouteFilterField.hilliness,
    if (surfaceType != null) RouteFilterField.surfaceType,
    if (windDirection != null) RouteFilterField.windDirection,
  ];

  /// Count shown on the filter button badge. Search has its own visible input,
  /// so it is not counted here.
  int get activeCount =>
      activeFields.where((f) => f != RouteFilterField.search).length;

  bool get hasDefaultSort =>
      sortBy == kDefaultRouteSortBy && sortDir == kDefaultRouteSortDir;

  bool get isDefault => activeFields.isEmpty && hasDefaultSort;

  /// The filter most likely responsible for an empty result set.
  ///
  /// Ordered from narrowest to broadest so the dead-end empty state can offer
  /// to drop one filter rather than only "reset everything". Search comes last
  /// because dropping the user's own words is the least helpful suggestion.
  RouteFilterField? get narrowestField {
    const priority = [
      RouteFilterField.elevationGain,
      RouteFilterField.distance,
      RouteFilterField.windDirection,
      RouteFilterField.hilliness,
      RouteFilterField.surfaceType,
      RouteFilterField.search,
    ];
    final active = activeFields.toSet();
    for (final field in priority) {
      if (active.contains(field)) return field;
    }
    return null;
  }

  /// A copy without [field], keeping everything else — including the sort.
  RouteFilters without(RouteFilterField field) {
    return switch (field) {
      RouteFilterField.search => copyWith(search: null),
      RouteFilterField.distance => copyWith(
        minDistance: null,
        maxDistance: null,
      ),
      RouteFilterField.elevationGain => copyWith(
        minElevationGain: null,
        maxElevationGain: null,
      ),
      RouteFilterField.hilliness => copyWith(hilliness: null),
      RouteFilterField.surfaceType => copyWith(surfaceType: null),
      RouteFilterField.windDirection => copyWith(windDirection: null),
    };
  }

  /// Everything cleared, sort included.
  RouteFilters get cleared => const RouteFilters();

  /// All filters cleared but the search text and the sort kept, which is what
  /// "Réinitialiser" inside the sheet does.
  RouteFilters get filtersCleared =>
      RouteFilters(search: search, sortBy: sortBy, sortDir: sortDir);

  /// [copyWith] clears a field when its argument is explicitly null, which is
  /// what filter removal needs. Pass only the fields you mean to change.
  RouteFilters copyWith({
    Object? search = _unset,
    Object? minDistance = _unset,
    Object? maxDistance = _unset,
    Object? minElevationGain = _unset,
    Object? maxElevationGain = _unset,
    Object? hilliness = _unset,
    Object? surfaceType = _unset,
    Object? windDirection = _unset,
    RouteSortBy? sortBy,
    SortDirection? sortDir,
  }) {
    return RouteFilters(
      search: identical(search, _unset) ? this.search : search as String?,
      minDistance: identical(minDistance, _unset)
          ? this.minDistance
          : minDistance as double?,
      maxDistance: identical(maxDistance, _unset)
          ? this.maxDistance
          : maxDistance as double?,
      minElevationGain: identical(minElevationGain, _unset)
          ? this.minElevationGain
          : minElevationGain as double?,
      maxElevationGain: identical(maxElevationGain, _unset)
          ? this.maxElevationGain
          : maxElevationGain as double?,
      hilliness: identical(hilliness, _unset)
          ? this.hilliness
          : hilliness as Hilliness?,
      surfaceType: identical(surfaceType, _unset)
          ? this.surfaceType
          : surfaceType as SurfaceType?,
      windDirection: identical(windDirection, _unset)
          ? this.windDirection
          : windDirection as WindDirection?,
      sortBy: sortBy ?? this.sortBy,
      sortDir: sortDir ?? this.sortDir,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is RouteFilters &&
          other.search == search &&
          other.minDistance == minDistance &&
          other.maxDistance == maxDistance &&
          other.minElevationGain == minElevationGain &&
          other.maxElevationGain == maxElevationGain &&
          other.hilliness == hilliness &&
          other.surfaceType == surfaceType &&
          other.windDirection == windDirection &&
          other.sortBy == sortBy &&
          other.sortDir == sortDir;

  @override
  int get hashCode => Object.hash(
    search,
    minDistance,
    maxDistance,
    minElevationGain,
    maxElevationGain,
    hilliness,
    surfaceType,
    windDirection,
    sortBy,
    sortDir,
  );
}

const Object _unset = Object();
