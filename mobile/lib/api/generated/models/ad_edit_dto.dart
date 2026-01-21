// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ad_edit_dto_location_geometry.dart';
import 'ad_type.dart';
import 'instant.dart';
import 'media_dto.dart';
import 'rental_period.dart';
import 'status.dart';
import 'team_publication_dto.dart';
import 'visibility.dart';

part 'ad_edit_dto.freezed.dart';
part 'ad_edit_dto.g.dart';

/// Ad data
@Freezed()
abstract class AdEditDto with _$AdEditDto {
  const factory AdEditDto({
    /// Team
    required TeamPublicationDto team,

    /// Ad ID (TSID)
    required String id,

    /// Ad URL slug
    required String slug,

    /// Ad name
    required String name,

    /// Ad media
    required MediaDto media,

    /// Ad status
    required String status,

    /// Visibility level
    required String visibility,

    /// Ad type
    required String adType,

    /// Creation timestamp
    required String createdAt,

    /// Creation timestamp
    required String updatedAt,

    /// Creator ID (TSID)
    required String createdById,

    /// Price
    num? price,

    /// Rental period
    String? rentalPeriod,

    /// Location coordinates [longitude, latitude]
    AdEditDtoLocationGeometry? locationGeometry,

    /// Location description
    String? locationDescription,
  }) = _AdEditDto;

  factory AdEditDto.fromJson(Map<String, Object?> json) =>
      _$AdEditDtoFromJson(json);
}
