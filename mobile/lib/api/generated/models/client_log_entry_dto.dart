// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'client_log_level.dart';
import 'instant.dart';

part 'client_log_entry_dto.freezed.dart';
part 'client_log_entry_dto.g.dart';

/// One entry of the client's recent log
@Freezed()
abstract class ClientLogEntryDto with _$ClientLogEntryDto {
  const factory ClientLogEntryDto({
    /// When it was logged
    required String ts,

    /// Severity
    required String level,

    /// What logged it: console, http, navigation, error…
    required String source,

    /// The entry, truncated by the client
    required String message,
  }) = _ClientLogEntryDto;

  factory ClientLogEntryDto.fromJson(Map<String, Object?> json) =>
      _$ClientLogEntryDtoFromJson(json);
}
