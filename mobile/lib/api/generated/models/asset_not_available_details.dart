// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'error_code.dart';
import 'error_details.dart';

part 'asset_not_available_details.freezed.dart';
part 'asset_not_available_details.g.dart';

@Freezed()
abstract class AssetNotAvailableDetails with _$AssetNotAvailableDetails {
  const factory AssetNotAvailableDetails({
    /// Type
    required String type,

    /// Id of the asset, as the request cited it
    required String assetId,
  }) = _AssetNotAvailableDetails;

  factory AssetNotAvailableDetails.fromJson(Map<String, Object?> json) =>
      _$AssetNotAvailableDetailsFromJson(json);
}
