// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'asset_dimensions_dto.dart';

part 'asset_dto.freezed.dart';
part 'asset_dto.g.dart';

@Freezed()
abstract class AssetDto with _$AssetDto {
  const factory AssetDto({
    /// ID (TSID)
    required String id,

    /// Filename
    required String fileName,

    /// Content-Type
    required String contentType,

    /// url
    required String url,

    /// image template url
    String? imageUrl,

    /// image dimensions
    AssetDimensionsDto? imageDimensions,
  }) = _AssetDto;

  factory AssetDto.fromJson(Map<String, Object?> json) =>
      _$AssetDtoFromJson(json);
}
