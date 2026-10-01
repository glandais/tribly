import 'package:flutter/widgets.dart';

class _RoutesKey extends ValueKey<String> {
  const _RoutesKey(String value) : super('routes_$value');
}

/// La parcothèque : l'onglet Parcours et la section Parcours d'une équipe.
class RoutesKeys {
  /// La carte (ou la ligne compacte) d'un parcours de la liste.
  ValueKey<String> card(String routeSlug) => _RoutesKey('card_$routeSlug');

  /// Les tags d'une carte (ou d'une ligne compacte) de la liste.
  ValueKey<String> cardTags(String routeSlug) =>
      _RoutesKey('cardTags_$routeSlug');

  /// L'état vide filtré, « cul-de-sac ».
  final emptyState = const _RoutesKey('emptyState');

  final filterApplyButton = const _RoutesKey('filterApplyButton');
  final filterButton = const _RoutesKey('filterButton');

  /// Une valeur d'un choix de la feuille de filtres (revêtement, relief…).
  ValueKey<String> filterChoice(Object value) =>
      _RoutesKey('filterChoice_$value');

  final listViewSegment = const _RoutesKey('listViewSegment');
  final mapView = const _RoutesKey('mapView');
  final mapViewSegment = const _RoutesKey('mapViewSegment');
  final searchField = const _RoutesKey('searchField');
}

class _RouteDetailKey extends ValueKey<String> {
  const _RouteDetailKey(String value) : super('routeDetail_$value');
}

class RouteDetailKeys {
  /// Le profil altimétrique, tiré du tracé.
  final elevationProfile = const _RouteDetailKey('elevationProfile');

  /// Le `⋯` de l'overlay : signaler le parcours.
  final moreButton = const _RouteDetailKey('moreButton');

  final title = const _RouteDetailKey('title');

  /// Une carte de « Utilisée dans », par le slug de la sortie ou du voyage.
  ValueKey<String> usage(String slug) => _RouteDetailKey('usage_$slug');
}
