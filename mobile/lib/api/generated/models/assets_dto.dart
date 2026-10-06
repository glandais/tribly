// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'asset_dto.dart';

part 'assets_dto.freezed.dart';
part 'assets_dto.g.dart';

/// Assets of a content. On a write, the logo, images and attachments must each be an asset uploaded to this team and attached to no other content, else 400 ASSET_NOT_AVAILABLE naming the asset id — the same answer whether the id is unknown, of another team or held by another content.
@Freezed()
abstract class AssetsDto with _$AssetsDto {
  const factory AssetsDto({
    /// Images
    required List<AssetDto> images,

    /// Attachments
    required List<AssetDto> attachments,

    /// Logo
    AssetDto? logo,

    /// Original GPX
    AssetDto? originalGpx,

    /// GPX
    AssetDto? gpx,

    /// FIT
    AssetDto? fit,

    /// Light thumbnail
    AssetDto? thumbnailLight,

    /// Dark thumbnail
    AssetDto? thumbnailDark,
  }) = _AssetsDto;

  factory AssetsDto.fromJson(Map<String, Object?> json) =>
      _$AssetsDtoFromJson(json);
}
