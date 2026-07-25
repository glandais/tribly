// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'user_export_status.dart';

part 'user_export_dto.freezed.dart';
part 'user_export_dto.g.dart';

/// Status of a personal data export request
@Freezed()
abstract class UserExportDto with _$UserExportDto {
  const factory UserExportDto({
    /// Export job identifier
    required String id,

    /// Current status
    required String status,

    /// When the export was requested
    required String requestedAt,

    /// When the export finished building
    String? completedAt,

    /// When the download link stops working
    String? expiresAt,

    /// Size of the archive in bytes
    int? fileSize,
  }) = _UserExportDto;

  factory UserExportDto.fromJson(Map<String, Object?> json) =>
      _$UserExportDtoFromJson(json);
}
