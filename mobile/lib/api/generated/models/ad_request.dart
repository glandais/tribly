// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ad_request_location_geometry.dart';
import 'ad_type.dart';
import 'media_dto.dart';
import 'rental_period.dart';
import 'status.dart';

part 'ad_request.freezed.dart';
part 'ad_request.g.dart';

/// Ad request
@Freezed()
abstract class AdRequest with _$AdRequest {
  const factory AdRequest({
    /// Ad name
    required String name,

    /// Ad description
    required MediaDto media,

    /// Ad status
    required String status,

    /// Ad type
    required String adType,

    /// Price (optional, null for 'contact for price')
    num? price,

    /// Rental period (required for RENTAL type)
    String? rentalPeriod,

    /// Location description
    String? locationDescription,

    /// Location coordinates [longitude, latitude]
    AdRequestLocationGeometry? locationGeometry,
  }) = _AdRequest;

  factory AdRequest.fromJson(Map<String, Object?> json) =>
      _$AdRequestFromJson(json);
}
