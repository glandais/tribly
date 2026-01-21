// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_json_point.dart';

part 'place_detail_dto.freezed.dart';
part 'place_detail_dto.g.dart';

@Freezed()
abstract class PlaceDetailDto with _$PlaceDetailDto {
  const factory PlaceDetailDto({
    /// Place ID (TSID)
    required String id,
    required String name,
    required bool startPlace,
    required bool endPlace,
    String? address,
    String? link,
    GeoJsonPoint? geometry,
  }) = _PlaceDetailDto;

  factory PlaceDetailDto.fromJson(Map<String, Object?> json) =>
      _$PlaceDetailDtoFromJson(json);
}
