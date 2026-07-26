// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'map_style_dto.freezed.dart';
part 'map_style_dto.g.dart';

/// A basemap style the clients may offer
@Freezed()
abstract class MapStyleDto with _$MapStyleDto {
  const factory MapStyleDto({
    /// Stable style identifier, e.g. 'colorful'
    required String id,

    /// Human-readable label for the style switcher
    required String label,

    /// URL of the MapLibre style document to load in light mode (or at all times when darkVariant is null)
    required String url,

    /// URL of the style document to load instead of 'url' when the client renders in dark mode. Null when the style has no dark counterpart — the client then keeps using 'url'.
    String? darkVariant,
  }) = _MapStyleDto;

  factory MapStyleDto.fromJson(Map<String, Object?> json) =>
      _$MapStyleDtoFromJson(json);
}
