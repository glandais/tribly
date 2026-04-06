// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ad_type.dart';
import 'instant.dart';
import 'media_dto.dart';
import 'rental_period.dart';
import 'status.dart';
import 'team_publication_dto.dart';
import 'visibility.dart';

part 'ad_dto.freezed.dart';
part 'ad_dto.g.dart';

/// Ad data
@Freezed()
abstract class AdDto with _$AdDto {
  const factory AdDto({
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

    /// Whether the ad is soft-deleted
    required bool deleted,

    /// Price
    num? price,

    /// Rental period
    String? rentalPeriod,

    /// Location description
    String? locationDescription,
  }) = _AdDto;

  factory AdDto.fromJson(Map<String, Object?> json) => _$AdDtoFromJson(json);
}
